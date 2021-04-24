package challengemoneytransferapi.exception;

public class UnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = -7350630445332505769L;

	public UnauthorizedException(String message) {
		super(message);
	}

}
