package beleg.slotsservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-Test für einen im Banking unbekannten User.
 */
class BankingUserNotFoundExceptionTest {

    @Test
    void constructorIncludesUserIdInMessage() {
        BankingUserNotFoundException exception = new BankingUserNotFoundException(Long.MAX_VALUE);

        assertTrue(exception.getMessage().contains(String.valueOf(Long.MAX_VALUE)));
    }
}
