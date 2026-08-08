package beleg.slotsservice.exception;

public class BankingCommunicationException extends RuntimeException {

    public BankingCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
