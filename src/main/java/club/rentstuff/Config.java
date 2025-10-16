package club.rentstuff;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@EnableScheduling
public class Config {

	@Value("${rentstuff.version}")
	private String version;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
