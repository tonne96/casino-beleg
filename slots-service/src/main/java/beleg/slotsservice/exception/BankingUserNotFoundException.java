package beleg.slotsservice.exception;

/**
 * Wird geworfen, wenn der Banking-Service den angefragten User nicht kennt.
 */
public class BankingUserNotFoundException extends RuntimeException {

    public BankingUserNotFoundException(Long userId) {
        super("User existiert im Banking-Service nicht: " + userId);
    }
}
