package beleg.slotsservice.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Test fuer eine einzelne Auszahlungsregel.
 */
class SlotPayoutRuleViewTest {

    @Test
    void recordStoresPayoutRuleValues() {
        SlotPayoutRuleView view = new SlotPayoutRuleView(
                "SEVEN + SEVEN + SEVEN",
                10,
                "Jackpot: Einsatz mal 10"
        );

        assertEquals("SEVEN + SEVEN + SEVEN", view.combination());
        assertEquals(10, view.payoutMultiplier());
        assertEquals("Jackpot: Einsatz mal 10", view.description());
    }
}
