package club.rentstuff.exception;

import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Aspect
@Component
public class ServiceExceptionAspect {

	@AfterThrowing(pointcut = "execution(* club.rentstuff.service.impl.*.*(..))", throwing = "ex")
	public void handleServiceException(Exception ex) {

		if (ex instanceof SizeLimitExceededException) {
			throw new ServiceException("Upload size exceeds the maximum limit of 200MB", ex);
		}

		if (ex instanceof IllegalArgumentException) {
			throw new ServiceException(ex.getLocalizedMessage(), ex);
		}

		if (ex instanceof ResponseStatusException) {
			throw new ServiceException(ex.getLocalizedMessage(), ex);
		}

		// Log the exception with stack trace
		log.error("Service exception occurred: {}", ex.getMessage(), ex);

		// Throw a custom exception with a friendly message
		throw new ServiceException("An error occurred while processing your request. Please try again later.", ex);
	}
}