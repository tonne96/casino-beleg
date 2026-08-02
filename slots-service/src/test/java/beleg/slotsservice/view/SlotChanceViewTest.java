package beleg.slotsservice.view;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Test fuer die Darstellung einer einzelnen Gewinnchance.
 */
class SlotChanceViewTest {

    @Test
    void recordStoresChanceValues() {
        SlotChanceView view = new SlotChanceView(
                "JACKPOT_THREE_SEVENS",
                1,
                125,
                new BigDecimal("0.80"),
                10
        );

        assertEquals("JACKPOT_THREE_SEVENS", view.result());
        assertEquals(1, view.matchingOutcomes());
        assertEquals(125, view.totalOutcomes());
        assertEquals(new BigDecimal("0.80"), view.probabilityPercent());
        assertEquals(10, view.payoutMultiplier());
    }
}
