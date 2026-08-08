package beleg.slotsservice.view;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Test für die globale Statistikantwort aus der Belegvorgabe.
 */
class SlotsStatsViewTest {

    @Test
    void recordStoresGlobalStatsValues() {
        SlotsStatsView view = new SlotsStatsView(
                Long.MAX_VALUE,
                Long.MAX_VALUE,
                new BigDecimal("99999999999999999.99"),
                new BigDecimal("0.01"),
                new BigDecimal("100000000000000000.00")
        );

        assertEquals(Long.MAX_VALUE, view.total_client_count());
        assertEquals(Long.MAX_VALUE, view.total_games_count());
        assertEquals(new BigDecimal("99999999999999999.99"), view.total_profit());
        assertEquals(new BigDecimal("0.01"), view.total_cash_out());
        assertEquals(new BigDecimal("100000000000000000.00"), view.total_turnover());
    }
}
