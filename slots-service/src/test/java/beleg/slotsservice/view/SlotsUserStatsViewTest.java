package beleg.slotsservice.view;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Test für die Statistikantwort eines einzelnen Users.
 */
class SlotsUserStatsViewTest {

    @Test
    void recordStoresUserStatsValues() {
        SlotsUserStatsView view = new SlotsUserStatsView(
                Long.MAX_VALUE,
                Long.MAX_VALUE,
                new BigDecimal("100.00"),
                new BigDecimal("-25.00"),
                new BigDecimal("75.00"),
                new BigDecimal("125.00"),
                new BigDecimal("-75.00")
        );

        assertEquals(Long.MAX_VALUE, view.client());
        assertEquals(Long.MAX_VALUE, view.total_games_count());
        assertEquals(new BigDecimal("100.00"), view.total_winnings());
        assertEquals(new BigDecimal("-25.00"), view.total_losses());
        assertEquals(new BigDecimal("75.00"), view.total_client_profit());
        assertEquals(new BigDecimal("125.00"), view.total_house_turnover_from_client());
        assertEquals(new BigDecimal("-75.00"), view.total_house_profit_from_client());
    }
}
