package beleg.slotsservice.exception;

/**
 * Wird geworfen, wenn der Banking-Service nicht erreichbar ist oder unerwartet antwortet.
 */
public class BankingCommunicationException extends RuntimeException {

    public BankingCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
