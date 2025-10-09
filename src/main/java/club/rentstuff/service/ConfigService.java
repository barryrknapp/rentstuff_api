package club.rentstuff.service;

import club.rentstuff.entity.ConfigEnt;

public interface ConfigService {

	String getConfig(String string);

	ConfigEnt updateConfig(String key, String value);
}
