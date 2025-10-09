package club.rentstuff.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import club.rentstuff.entity.ConfigEnt;
import club.rentstuff.service.ConfigService;

@RestController
@RequestMapping("/secureadmin/configs")
public class ConfigController {
	@Autowired
	private ConfigService configService;

	@PutMapping("/{key}")
	public ResponseEntity<ConfigEnt> updateConfig(@PathVariable String key, @RequestBody ConfigEnt config) {
		// Update logic
		return ResponseEntity.ok(configService.updateConfig(key, config.getValue()));
	}
}