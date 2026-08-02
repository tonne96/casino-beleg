package beleg.slotsservice.client;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Test fuer vom Banking-Service gelieferte Userdaten.
 */
class BankingUserViewTest {

    @Test
    void recordStoresBankingUserValues() {
        BankingUserView user = new BankingUserView(
                Long.MAX_VALUE,
                "Ada",
                "Lovelace",
                new BigDecimal("99999999999999999.99")
        );

        assertEquals(Long.MAX_VALUE, user.id());
        assertEquals("Ada", user.first_name());
        assertEquals("Lovelace", user.last_name());
        assertEquals(new BigDecimal("99999999999999999.99"), user.balance());
    }
}
