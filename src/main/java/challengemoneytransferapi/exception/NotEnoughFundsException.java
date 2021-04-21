package challengemoneytransferapi.exception;

public class NotEnoughFundsException extends RuntimeException {

	private static final long serialVersionUID = 8028508236362572720L;

	public NotEnoughFundsException(String message) {
		super(message);
	}

}
