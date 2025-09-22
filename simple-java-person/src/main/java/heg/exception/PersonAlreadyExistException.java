package heg.exception;

public class PersonAlreadyExistException extends Exception {
    public PersonAlreadyExistException(String message) {
        super(message);
    }

    public PersonAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
