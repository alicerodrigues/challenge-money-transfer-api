package challengemoneytransferapi.exception;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = -6571367505890712290L;

	public NotFoundException(String message) {
		super(message);
	}

}
