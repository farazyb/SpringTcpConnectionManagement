package ir.co.ocs.connection.envoriments.exceptions;

public class NetworkBindingException extends RuntimeException {
    public NetworkBindingException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkBindingException(String message) {
        super(message);
    }
}
