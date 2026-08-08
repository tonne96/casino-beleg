package beleg.slotsservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit-Test für Kommunikationsfehler zum Banking-Service.
 */
class BankingCommunicationExceptionTest {

    @Test
    void constructorStoresMessageAndCause() {
        RuntimeException cause = new RuntimeException("HTTP-Verbindung fehlgeschlagen");

        BankingCommunicationException exception = new BankingCommunicationException(
                "Banking nicht erreichbar",
                cause
        );

        assertEquals("Banking nicht erreichbar", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
