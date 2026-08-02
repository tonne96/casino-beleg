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
import java.util.Random;

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

    @Test
    void evaluateHandlesRandomizedBetsAndSymbolCombinations() {
        Random testRandom = new Random(20260802L);
        SlotSymbol[] symbols = SlotSymbol.values();

        // Fester Seed: breite Zufallsabdeckung, aber bei Fehlern reproduzierbar.
        for (int testRun = 0; testRun < 500; testRun++) {
            BigDecimal betAmount = BigDecimal.valueOf(testRandom.nextLong(1, 1_000_000), 2);
            List<SlotSymbol> slotStates = List.of(
                    symbols[testRandom.nextInt(symbols.length)],
                    symbols[testRandom.nextInt(symbols.length)],
                    symbols[testRandom.nextInt(symbols.length)]
            );

            SlotGameResult result = slotGameHandler.evaluate(betAmount, slotStates);

            assertEquals(slotStates, result.slotStates());
            assertEquals(result.payoutMultiplier() > 0, result.winning());
            assertEquals(expectedNetAmount(betAmount, result.payoutMultiplier()), result.amount());
        }
    }

    @Test
    void evaluateAcceptsPositiveExtremeBetAmounts() {
        List<SlotSymbol> loss = List.of(SlotSymbol.CHERRY, SlotSymbol.LEMON, SlotSymbol.BELL);
        BigDecimal smallestCent = new BigDecimal("0.01");
        BigDecimal veryLargeBet = new BigDecimal("99999999999999999.99");

        assertEquals(smallestCent.negate(), slotGameHandler.evaluate(smallestCent, loss).amount());
        assertEquals(veryLargeBet.negate(), slotGameHandler.evaluate(veryLargeBet, loss).amount());
    }

    @Test
    void playCreatesValidRandomizedRounds() {
        Random testRandom = new Random(20260803L);

        for (int testRun = 0; testRun < 250; testRun++) {
            BigDecimal betAmount = BigDecimal.valueOf(testRandom.nextLong(1, 1_000_000), 2);

            SlotGameResult result = slotGameHandler.play(betAmount);

            assertEquals(3, result.slotStates().size());
            assertEquals(result.payoutMultiplier() > 0, result.winning());
            assertEquals(expectedNetAmount(betAmount, result.payoutMultiplier()), result.amount());
        }
    }

    private BigDecimal expectedNetAmount(BigDecimal betAmount, int payoutMultiplier) {
        BigDecimal payout = betAmount.multiply(BigDecimal.valueOf(payoutMultiplier));
        return payout.subtract(betAmount);
    }
}
