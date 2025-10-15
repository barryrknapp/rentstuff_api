package club.rentstuff.exception;

public class ServiceException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8342579528624047402L;

	private Response response;

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
		response = new Response(message);
	}

	public ServiceException(String message) {
		super(message);
	}

	public Response getResponse() {
		return response;
	}

	class Response {
		String data;

		public Response(String v) {
			data = v;
		}

		public String getData() {
			return data;
		}
	}
}