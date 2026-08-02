package beleg.slotsservice.client;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Test fuer den Request an den Banking-Service.
 */
class BankingTransactionRequestTest {

    @Test
    void recordStoresTransactionRequestValues() {
        BankingTransactionRequest request = new BankingTransactionRequest(
                "SLOTS",
                Long.MAX_VALUE,
                new BigDecimal("-99999999999999999.99")
        );

        assertEquals("SLOTS", request.invoicing_party());
        assertEquals(Long.MAX_VALUE, request.user());
        assertEquals(new BigDecimal("-99999999999999999.99"), request.amount());
    }
}
