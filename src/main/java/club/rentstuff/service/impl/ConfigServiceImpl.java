package club.rentstuff.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import club.rentstuff.entity.ConfigEnt;
import club.rentstuff.repo.ConfigRepo;
import club.rentstuff.service.ConfigService;

@Service
public class ConfigServiceImpl implements ConfigService {

	@Autowired
	protected ConfigRepo configRepo;

	private String getByKey(String key) {
		Optional<ConfigEnt> config = configRepo.findOne(Example.of(ConfigEnt.builder().key(key).build()));
		if (config.isEmpty()) {
			return null;
		}
		return config.get().getValue();

	}

	@Override
	public String getConfig(String string) {
		return getByKey(string);
	}

	@Override
	public ConfigEnt updateConfig(String key, String value) {

		Optional<ConfigEnt> config = configRepo.findOne(Example.of(ConfigEnt.builder().key(key).build()));
		if (config.isPresent() && !config.get().getValue().equals(value)) {
			return configRepo
					.save(ConfigEnt.builder().id(config.get().getId()).value(value).key(config.get().getKey()).build());
		}

		return null;
	}

}
