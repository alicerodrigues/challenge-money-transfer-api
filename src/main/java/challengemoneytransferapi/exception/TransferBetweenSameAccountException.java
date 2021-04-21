package challengemoneytransferapi.exception;

public class TransferBetweenSameAccountException extends RuntimeException {

	private static final long serialVersionUID = 7284152409318212592L;

	public TransferBetweenSameAccountException(String message) {
		super(message);
	}

}
