package beleg.slotsservice.handler.info;

import beleg.slotsservice.model.SlotSymbol;
import beleg.slotsservice.view.SlotChanceView;
import beleg.slotsservice.view.SlotPayoutRuleView;
import beleg.slotsservice.view.SlotsChancesView;
import beleg.slotsservice.view.SlotsRulesView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer die Info-Logik.
 *
 * Diese Tests gleichen Regeln und Chancen mit den Anforderungen aus dem Beleg ab:
 * Regeln und Gewinnchancen muessen ueber die Info-Endpunkte abrufbar sein.
 */
class SlotInfoHandlerTest {

    private SlotInfoHandler slotInfoHandler;

    @BeforeEach
    void setUp() {
        slotInfoHandler = new SlotInfoHandler();
    }

    @Test
    void getRulesReturnsSlotRulesAndPayoutRules() {
        SlotsRulesView rules = slotInfoHandler.getRules();

        assertEquals(3, rules.reelCount());
        assertEquals(5, rules.symbols().size());
        assertEquals(SlotSymbol.CHERRY, rules.symbols().get(0));
        assertEquals(SlotSymbol.LEMON, rules.symbols().get(1));
        assertEquals(SlotSymbol.BELL, rules.symbols().get(2));
        assertEquals(SlotSymbol.BAR, rules.symbols().get(3));
        assertEquals(SlotSymbol.SEVEN, rules.symbols().get(4));

        assertFalse(rules.rules().isEmpty());
        assertEquals(4, rules.payoutRules().size());

        SlotPayoutRuleView jackpotRule = rules.payoutRules().get(0);
        assertEquals("SEVEN + SEVEN + SEVEN", jackpotRule.combination());
        assertEquals(10, jackpotRule.payoutMultiplier());

        SlotPayoutRuleView twoEqualRule = rules.payoutRules().get(2);
        assertEquals("Zwei gleiche Symbole", twoEqualRule.combination());
        assertEquals(1, twoEqualRule.payoutMultiplier());
    }

    @Test
    void getChancesReturnsCalculatedProbabilities() {
        SlotsChancesView chances = slotInfoHandler.getChances();

        assertEquals(3, chances.reelCount());
        assertEquals(5, chances.symbolCount());
        assertEquals(125, chances.totalOutcomes());
        assertEquals(4, chances.chances().size());
        assertNotNull(chances.note());

        List<SlotChanceView> chanceEntries = chances.chances();

        SlotChanceView jackpot = chanceEntries.get(0);
        assertEquals("JACKPOT_THREE_SEVENS", jackpot.result());
        assertEquals(1, jackpot.matchingOutcomes());
        assertEquals(125, jackpot.totalOutcomes());
        assertEquals(new BigDecimal("0.80"), jackpot.probabilityPercent());
        assertEquals(10, jackpot.payoutMultiplier());

        SlotChanceView threeEqual = chanceEntries.get(1);
        assertEquals("THREE_EQUAL_NON_SEVEN", threeEqual.result());
        assertEquals(4, threeEqual.matchingOutcomes());
        assertEquals(125, threeEqual.totalOutcomes());
        assertEquals(new BigDecimal("3.20"), threeEqual.probabilityPercent());
        assertEquals(3, threeEqual.payoutMultiplier());

        SlotChanceView twoEqual = chanceEntries.get(2);
        assertEquals("TWO_EQUAL_SYMBOLS", twoEqual.result());
        assertEquals(60, twoEqual.matchingOutcomes());
        assertEquals(125, twoEqual.totalOutcomes());
        assertEquals(new BigDecimal("48.00"), twoEqual.probabilityPercent());
        assertEquals(1, twoEqual.payoutMultiplier());

        SlotChanceView loss = chanceEntries.get(3);
        assertEquals("THREE_DIFFERENT_SYMBOLS", loss.result());
        assertEquals(60, loss.matchingOutcomes());
        assertEquals(125, loss.totalOutcomes());
        assertEquals(new BigDecimal("48.00"), loss.probabilityPercent());
        assertEquals(0, loss.payoutMultiplier());
    }
}
