package beleg.slotsservice.exception;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-Test für unzureichendes Spielguthaben.
 */
class InsufficientBalanceExceptionTest {

    @Test
    void constructorIncludesUserBalanceAndBetInMessage() {
        InsufficientBalanceException exception = new InsufficientBalanceException(
                7L,
                new BigDecimal("0.01"),
                new BigDecimal("99999999999999999.99")
        );

        assertTrue(exception.getMessage().contains("7"));
        assertTrue(exception.getMessage().contains("0.01"));
        assertTrue(exception.getMessage().contains("99999999999999999.99"));
    }
}
