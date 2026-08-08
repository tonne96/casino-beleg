package beleg.slotsservice.view;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Test für die zusammengefassten Gewinnchancen.
 */
class SlotsChancesViewTest {

    @Test
    void recordStoresChanceOverview() {
        SlotChanceView jackpot = new SlotChanceView(
                "JACKPOT_THREE_SEVENS",
                1,
                125,
                new BigDecimal("0.80"),
                10
        );
        SlotsChancesView view = new SlotsChancesView(
                3,
                5,
                125,
                List.of(jackpot),
                "Alle Symbole sind gleich wahrscheinlich."
        );

        assertEquals(3, view.reelCount());
        assertEquals(5, view.symbolCount());
        assertEquals(125, view.totalOutcomes());
        assertEquals(List.of(jackpot), view.chances());
        assertEquals("Alle Symbole sind gleich wahrscheinlich.", view.note());
    }
}
