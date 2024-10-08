package ir.co.ocs.managers;

public class ManagersException extends RuntimeException {
    public ManagersException(String message) {
        super(message);
    }

    public ManagersException(String message, Throwable cause) {
        super(message, cause);
    }
}
