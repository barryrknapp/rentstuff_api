package club.rentstuff.exception;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Aspect
@Component
public class ServiceExceptionAspect {

	@AfterThrowing(pointcut = "execution(* club.rentstuff.service.impl.*.*(..))", throwing = "ex")
	public void handleServiceException(Exception ex) {
		// Log the exception with stack trace
		log.error("Service exception occurred: {}", ex.getMessage(), ex);

		// Throw a custom exception with a friendly message
		throw new ServiceException("An error occurred while processing your request. Please try again later.", ex);
	}
}