package club.rentstuff.exception;

public class ServiceException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8342579528624047402L;

	public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(String message) {
        super(message);
    }
}