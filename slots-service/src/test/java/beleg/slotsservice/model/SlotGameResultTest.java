package beleg.slotsservice.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für das Ergebnis einer simulierten Slot-Runde.
 */
class SlotGameResultTest {

    @Test
    void constructorStoresValidResultValues() {
        List<SlotSymbol> slotStates = List.of(SlotSymbol.BAR, SlotSymbol.BAR, SlotSymbol.BAR);

        SlotGameResult result = new SlotGameResult(true, BigDecimal.valueOf(20), slotStates, 3);

        assertTrue(result.winning());
        assertEquals(BigDecimal.valueOf(20), result.amount());
        assertEquals(slotStates, result.slotStates());
        assertEquals(3, result.payoutMultiplier());
    }

    @Test
    void constructorCopiesSlotStatesDefensively() {
        List<SlotSymbol> mutableSlotStates = new ArrayList<>();
        mutableSlotStates.add(SlotSymbol.CHERRY);
        mutableSlotStates.add(SlotSymbol.CHERRY);
        mutableSlotStates.add(SlotSymbol.LEMON);

        SlotGameResult result = new SlotGameResult(true, BigDecimal.ZERO, mutableSlotStates, 1);
        mutableSlotStates.set(0, SlotSymbol.SEVEN);

        assertEquals(SlotSymbol.CHERRY, result.slotStates().get(0));

        try {
            result.slotStates().set(0, SlotSymbol.SEVEN);
            fail("Die Ergebnisliste muss unveraenderbar sein.");
        } catch (UnsupportedOperationException e) {
            assertNotNull(e);
        }
    }

    @Test
    void constructorAcceptsRandomizedValidResults() {
        Random testRandom = new Random(20260802L);
        SlotSymbol[] symbols = SlotSymbol.values();

        for (int testRun = 0; testRun < 250; testRun++) {
            List<SlotSymbol> slotStates = List.of(
                    symbols[testRandom.nextInt(symbols.length)],
                    symbols[testRandom.nextInt(symbols.length)],
                    symbols[testRandom.nextInt(symbols.length)]
            );
            BigDecimal amount = BigDecimal.valueOf(testRandom.nextLong(-1_000_000, 1_000_001), 2);

            SlotGameResult result = new SlotGameResult(false, amount, slotStates, 0);

            assertEquals(amount, result.amount());
            assertEquals(slotStates, result.slotStates());
        }
    }

    @Test
    void constructorRejectsNullAmount() {
        expectInvalidResult(null, List.of(SlotSymbol.CHERRY, SlotSymbol.LEMON, SlotSymbol.BELL));
    }

    @Test
    void constructorRejectsNullSlotStates() {
        expectInvalidResult(BigDecimal.ZERO, null);
    }

    @Test
    void constructorRejectsWrongSlotStateCount() {
        expectInvalidResult(BigDecimal.ZERO, List.of(SlotSymbol.CHERRY, SlotSymbol.LEMON));
        expectInvalidResult(
                BigDecimal.ZERO,
                List.of(SlotSymbol.CHERRY, SlotSymbol.LEMON, SlotSymbol.BELL, SlotSymbol.BAR)
        );
    }

    @Test
    void constructorRejectsNullSymbol() {
        List<SlotSymbol> slotStates = Arrays.asList(SlotSymbol.CHERRY, null, SlotSymbol.LEMON);
        expectInvalidResult(BigDecimal.ZERO, slotStates);
    }

    private void expectInvalidResult(BigDecimal amount, List<SlotSymbol> slotStates) {
        try {
            new SlotGameResult(false, amount, slotStates, 0);
            fail("Ungueltige Ergebniswerte muessen abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }
}
