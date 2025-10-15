package club.rentstuff;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@EnableScheduling
public class Config {

	@Value("${rentstuff.version}")
	private String version;
	

}
