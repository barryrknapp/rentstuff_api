package club.rentstuff.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import club.rentstuff.schedule.MarketingAgentSchedule;

@RestController
@RequestMapping("/marketing")
public class MarketingController {

	@Autowired
    private MarketingAgentSchedule agentService;

    @PostMapping("/trigger/all")
    public ResponseEntity<Map<String, String>> runDaily() {
        agentService.runAllDaily();
        return ResponseEntity.accepted()
            .body(Map.of(
                "status", "queued",
                "message", "Marketing generation started"
            ));
    }
	
    @PostMapping("/trigger/{id}")
    public ResponseEntity<Map<String, String>> triggerMarketing(@PathVariable Long id) {
        agentService.triggerMarketingAsync(id);
        return ResponseEntity.accepted()
            .body(Map.of(
                "status", "queued",
                "message", "Marketing generation started for " + id
            ));
    }
}
