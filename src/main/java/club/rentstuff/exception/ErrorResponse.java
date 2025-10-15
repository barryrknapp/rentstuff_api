package club.rentstuff.exception;

public class ErrorResponse {
	private int status;
	private String message;
	private Response response;

	public ErrorResponse(int status, String message) {
		this.status = status;
		this.message = message;
		response = new Response("ERROR");
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getResponse() {
		return message;
	}
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	class Response {
		String data;
		public Response(String v) {
			data = v;
		}
		public String getData(){
			return data;
		}
	}
}