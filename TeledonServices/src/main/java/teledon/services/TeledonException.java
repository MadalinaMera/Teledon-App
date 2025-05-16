package teledon.services;

public class TeledonException extends Exception {
    public TeledonException() {
        super();
    }
    public TeledonException(String message) {
        super(message);
    }
    public TeledonException(String message, Throwable cause) {
        super(message, cause);
    }
}
