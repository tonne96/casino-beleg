package beleg.slotsservice.handler.game;

import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.model.SlotSymbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer die reine Slot-Spiellogik.
 *
 * Hier wird bewusst evaluate(...) getestet, weil diese Methode keinen Zufall,
 * kein HTTP und keine Datenbank braucht.
 */
class SlotGameHandlerTest {

    private SlotGameHandler slotGameHandler;

    @BeforeEach
    void setUp() {
        slotGameHandler = new SlotGameHandler();
    }

    @Test
    void evaluateReturnsLossForThreeDifferentSymbols() {
        BigDecimal betAmount = BigDecimal.TEN;
        List<SlotSymbol> slotStates = List.of(SlotSymbol.CHERRY, SlotSymbol.LEMON, SlotSymbol.BELL);

        SlotGameResult result = slotGameHandler.evaluate(betAmount, slotStates);

        assertFalse(result.winning());
        assertEquals(BigDecimal.TEN.negate(), result.amount());
        assertEquals(0, result.payoutMultiplier());
        assertEquals(slotStates, result.slotStates());
    }

    @Test
    void evaluateReturnsStakeBackForTwoEqualSymbols() {
        BigDecimal betAmount = BigDecimal.TEN;
        List<SlotSymbol> slotStates = List.of(SlotSymbol.CHERRY, SlotSymbol.CHERRY, SlotSymbol.LEMON);

        SlotGameResult result = slotGameHandler.evaluate(betAmount, slotStates);

        assertTrue(result.winning());
        assertEquals(BigDecimal.ZERO, result.amount());
        assertEquals(1, result.payoutMultiplier());
        assertEquals(slotStates, result.slotStates());
    }

    @Test
    void evaluateReturnsNetProfitForThreeEqualNonSevenSymbols() {
        BigDecimal betAmount = BigDecimal.TEN;
        List<SlotSymbol> slotStates = List.of(SlotSymbol.BAR, SlotSymbol.BAR, SlotSymbol.BAR);

        SlotGameResult result = slotGameHandler.evaluate(betAmount, slotStates);

        assertTrue(result.winning());
        assertEquals(BigDecimal.valueOf(20), result.amount());
        assertEquals(3, result.payoutMultiplier());
        assertEquals(slotStates, result.slotStates());
    }

    @Test
    void evaluateReturnsNetProfitForJackpot() {
        BigDecimal betAmount = BigDecimal.TEN;
        List<SlotSymbol> slotStates = List.of(SlotSymbol.SEVEN, SlotSymbol.SEVEN, SlotSymbol.SEVEN);

        SlotGameResult result = slotGameHandler.evaluate(betAmount, slotStates);

        assertTrue(result.winning());
        assertEquals(BigDecimal.valueOf(90), result.amount());
        assertEquals(10, result.payoutMultiplier());
        assertEquals(slotStates, result.slotStates());
    }

    @Test
    void evaluateRejectsNullBetAmount() {
        List<SlotSymbol> slotStates = List.of(SlotSymbol.CHERRY, SlotSymbol.LEMON, SlotSymbol.BELL);

        try {
            slotGameHandler.evaluate(null, slotStates);
            fail("Null als BetAmount muss abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-10.50"})
    void evaluateRejectsZeroAndNegativeBetAmounts(String invalidBetAmount) {
        List<SlotSymbol> slotStates = List.of(SlotSymbol.CHERRY, SlotSymbol.LEMON, SlotSymbol.BELL);

        try {
            slotGameHandler.evaluate(new BigDecimal(invalidBetAmount), slotStates);
            fail("Ungueltige BetAmounts muessen abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void evaluateRejectsNullSlotStates() {
        try {
            slotGameHandler.evaluate(BigDecimal.TEN, null);
            fail("Null als SlotStates muss abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void evaluateRejectsSlotStatesWithTooFewSymbols() {
        List<SlotSymbol> slotStates = List.of(SlotSymbol.CHERRY, SlotSymbol.LEMON);

        try {
            slotGameHandler.evaluate(BigDecimal.TEN, slotStates);
            fail("Zu wenige Slot-Symbole muessen abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void evaluateRejectsSlotStatesWithTooManySymbols() {
        List<SlotSymbol> slotStates = List.of(
                SlotSymbol.CHERRY,
                SlotSymbol.LEMON,
                SlotSymbol.BELL,
                SlotSymbol.BAR
        );

        try {
            slotGameHandler.evaluate(BigDecimal.TEN, slotStates);
            fail("Zu viele Slot-Symbole muessen abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void evaluateRejectsSlotStatesWithNullSymbol() {
        List<SlotSymbol> slotStates = Arrays.asList(SlotSymbol.CHERRY, null, SlotSymbol.LEMON);

        try {
            slotGameHandler.evaluate(BigDecimal.TEN, slotStates);
            fail("Null in SlotStates muss abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }
}
