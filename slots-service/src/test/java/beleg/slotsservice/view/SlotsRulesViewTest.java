package beleg.slotsservice.view;

import beleg.slotsservice.model.SlotSymbol;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Test für die Regelbeschreibung des Slots-Service.
 */
class SlotsRulesViewTest {

    @Test
    void recordStoresRulesAndPayoutRules() {
        List<SlotSymbol> symbols = List.of(SlotSymbol.values());
        List<String> rules = List.of("Eine Runde hat drei Walzen.");
        SlotPayoutRuleView payoutRule = new SlotPayoutRuleView("Drei unterschiedliche", 0, "Verlust");

        SlotsRulesView view = new SlotsRulesView(3, symbols, rules, List.of(payoutRule));

        assertEquals(3, view.reelCount());
        assertEquals(symbols, view.symbols());
        assertEquals(rules, view.rules());
        assertEquals(List.of(payoutRule), view.payoutRules());
    }
}
