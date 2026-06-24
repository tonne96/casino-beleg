package beleg.slotsservice.exception;

import java.math.BigDecimal;

/**
 * Wird geworfen, wenn ein User nicht genug Guthaben fuer den Einsatz hat.
 */
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(Long userId, BigDecimal balance, BigDecimal betAmount) {
        super("User " + userId + " hat nicht genug Guthaben. Balance: " + balance + ", Einsatz: " + betAmount);
    }
}
