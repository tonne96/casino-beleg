package beleg.slotsservice.factory;

import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.model.SlotSymbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer die SlotGameFactory.
 *
 * Die Factory uebersetzt ein berechnetes SlotGameResult in eine speicherbare
 * SlotGame-Entity.
 */
class SlotGameFactoryTest {

    private SlotGameFactory slotGameFactory;

    @BeforeEach
    void setUp() {
        slotGameFactory = new SlotGameFactory();
    }

    @Test
    void createCopiesResultValuesIntoSlotGame() {
        Long userId = 7L;
        BigDecimal betAmount = BigDecimal.TEN;
        BigDecimal amount = BigDecimal.valueOf(20);
        List<SlotSymbol> slotStates = List.of(SlotSymbol.BAR, SlotSymbol.BAR, SlotSymbol.BAR);
        SlotGameResult result = new SlotGameResult(true, amount, slotStates, 3);

        SlotGame slotGame = slotGameFactory.create(userId, betAmount, result);

        assertEquals(userId, slotGame.getUserId());
        assertEquals(betAmount, slotGame.getBetAmount());
        assertTrue(slotGame.isWinning());
        assertEquals(amount, slotGame.getAmount());
        assertEquals(slotStates, slotGame.getSlotStates());
        assertEquals(3, slotGame.getPayoutMultiplier());
        assertNotNull(slotGame.getPlayedAt());
    }

    @Test
    void createRejectsNullResult() {
        try {
            slotGameFactory.create(1L, BigDecimal.TEN, null);
            fail("Null als SlotGameResult muss abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }
}
