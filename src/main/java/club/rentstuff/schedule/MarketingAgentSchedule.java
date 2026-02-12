package club.rentstuff.schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import club.rentstuff.entity.MarketingEntity;
import club.rentstuff.model.MarketingRequestDto;
import club.rentstuff.model.MarketingRequestDto.FacebookCredentials;
import club.rentstuff.model.MarketingRequestDto.TwitterCredentials;
import club.rentstuff.repo.MarketingRepo;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class MarketingAgentSchedule {

    private final ObjectMapper objectMapper = new ObjectMapper();
//    private final ExecutorService executor = Executors.newFixedThreadPool(4); // Limit concurrent jobs
    private final ExecutorService executor = Executors.newSingleThreadExecutor();  
	
	@Value("${rentstuff.autogen.dir}")
	private String autogenDir;
    

    @Autowired
    private MarketingRepo marketingRepo;

    // Fire-and-forget with full logging + timeout
    public void triggerMarketingAsync(MarketingRequestDto dto) {
        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize marketing request: {}", dto.getCompanyName(), e);
            return;
        }

        CompletableFuture.runAsync(() -> runPythonScript(dto.getCompanyName(), jsonPayload), executor)
            .exceptionally(ex -> {
                log.error("Async job failed for {}", dto.getCompanyName(), ex);
                return null;
            });
    }

    private void runPythonScript(String companyName, String jsonPayload) {

        String quotedJson =  "\"" + jsonPayload.replace("\"", "\\\"") + "\"";

        log.info("Starting marketing job for: {} with json: {}", companyName, quotedJson);
        
        ProcessBuilder pb = new ProcessBuilder(
            "python", "marketingagent.py", autogenDir.contains("opt") ? jsonPayload : quotedJson
        );
        
        pb.directory(new File(autogenDir));

        try {
            Process process = pb.start();

            // CAPTURE PYTHON OUTPUT AND LOG IT
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), log::info);
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), log::error);
            CompletableFuture.runAsync(streamGobbler);
            CompletableFuture.runAsync(errorGobbler);

            // OPTIONAL: Timeout after 30 minutes
            boolean finished = process.waitFor(60, TimeUnit.MINUTES);
            if (finished) {
                int exitCode = process.exitValue();
                log.info("Marketing job completed for {} with exit code: {}", companyName, exitCode);
            } else {
                process.destroyForcibly();
                log.warn("Marketing job TIMED OUT for {} after 30 minutes", companyName);
            }

        } catch (IOException e) {
            log.error("Failed to start Python process for {}", companyName, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Marketing job interrupted for {}", companyName, e);
        }
    }

    // Helper to read process output
    private static class StreamGobbler implements Runnable {
        private final InputStream inputStream;
        private final Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                .forEach(consumer);
        }
    }

    // Overload for entity ID
    public void triggerMarketingAsync(Long id) {
        MarketingEntity ent = marketingRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Marketing entity not found: " + id));

        MarketingRequestDto dto = MarketingRequestDto.builder()
            .companyName(ent.getCompanyName())
            .domain(ent.getDomain())
            .website(ent.getWebsite())
            .category(ent.getCategory())
            .focusKeyword(ent.getFocusKeyword())
            .facebook(FacebookCredentials.builder().accessToken(ent.getFacebookAccessToken()).build())
            .twitter(TwitterCredentials.builder()
                .apiKey(ent.getXApiKey())
                .apiSecret(ent.getXApiSecret())
                .accessToken(ent.getXAccessToken())
                .accessTokenSecret(ent.getXAccessTokenSecret())
                .bearerToken(ent.getXBearerToken())
                .build())
            .build();

        triggerMarketingAsync(dto);
    }

    // Optional: Schedule all
//    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // Daily
    @Scheduled(cron = "0 0 12 * * ?", zone = "America/New_York")
    public void runAllDaily() {
        marketingRepo.findAll().forEach(ent -> triggerMarketingAsync(ent.getId()));
    }
}