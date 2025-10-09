package club.rentstuff.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import club.rentstuff.entity.ConfigEnt;
import club.rentstuff.repo.ConfigRepo;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private ConfigRepo configRepository;

    @PutMapping("/configs/{key}")
    public ConfigEnt updateConfig(@PathVariable String key, @RequestBody String value) {
        ConfigEnt config = configRepository.findByKey(key).orElse(new ConfigEnt());
        config.setKey(key);
        config.setValue(value);
        return configRepository.save(config);
    }
}
