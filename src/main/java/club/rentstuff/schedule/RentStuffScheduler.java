package club.rentstuff.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class RentStuffScheduler {


	
	
	// once per day at 1am
	@Scheduled(cron = "0 1 1 ? * *")
	public void distributeRewards() {

		
	}


}
