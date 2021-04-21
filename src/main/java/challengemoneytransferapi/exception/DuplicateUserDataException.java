package challengemoneytransferapi.exception;

public class DuplicateUserDataException extends RuntimeException {

	
	private static final long serialVersionUID = 8858666559749664613L;

	public DuplicateUserDataException(String message) {
		super(message);
	}
}
