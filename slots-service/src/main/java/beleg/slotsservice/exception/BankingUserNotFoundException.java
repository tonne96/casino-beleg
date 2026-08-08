package beleg.slotsservice.exception;

public class BankingUserNotFoundException extends RuntimeException {

    public BankingUserNotFoundException(Long userId) {
        super("User existiert im Banking-Service nicht: " + userId);
    }
}
