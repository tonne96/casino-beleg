package beleg.slotsservice.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer die SlotGame-Entity.
 *
 * Diese Tests pruefen, dass SlotGame nur in einem gueltigen Zustand erzeugt
 * werden kann.
 */
class SlotGameTest {

    @Test
    void createReturnsValidSlotGame() {
        LocalDateTime playedAt = LocalDateTime.of(2026, 6, 24, 12, 0);

        SlotGame slotGame = createValidSlotGame(playedAt);

        assertNull(slotGame.getId());
        assertEquals(1L, slotGame.getUserId());
        assertEquals(BigDecimal.TEN, slotGame.getBetAmount());
        assertTrue(slotGame.isWinning());
        assertEquals(BigDecimal.valueOf(20), slotGame.getAmount());
        assertEquals(List.of(SlotSymbol.BAR, SlotSymbol.BAR, SlotSymbol.BAR), slotGame.getSlotStates());
        assertEquals(3, slotGame.getPayoutMultiplier());
        assertEquals(playedAt, slotGame.getPlayedAt());
    }

    @Test
    void createRejectsNullUserId() {
        try {
            SlotGame.create(
                    null,
                    BigDecimal.TEN,
                    true,
                    BigDecimal.valueOf(20),
                    SlotSymbol.BAR,
                    SlotSymbol.BAR,
                    SlotSymbol.BAR,
                    3,
                    LocalDateTime.now()
            );
            fail("Null als UserId muss abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void createRejectsZeroAndNegativeUserIds(Long invalidUserId) {
        try {
            SlotGame.create(
                    invalidUserId,
                    BigDecimal.TEN,
                    true,
                    BigDecimal.valueOf(20),
                    SlotSymbol.BAR,
                    SlotSymbol.BAR,
                    SlotSymbol.BAR,
                    3,
                    LocalDateTime.now()
            );
            fail("Ungueltige UserIds muessen abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void createRejectsNullBetAmount() {
        try {
            SlotGame.create(
                    1L,
                    null,
                    true,
                    BigDecimal.valueOf(20),
                    SlotSymbol.BAR,
                    SlotSymbol.BAR,
                    SlotSymbol.BAR,
                    3,
                    LocalDateTime.now()
            );
            fail("Null als BetAmount muss abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-10.50"})
    void createRejectsZeroAndNegativeBetAmounts(String invalidBetAmount) {
        try {
            SlotGame.create(
                    1L,
                    new BigDecimal(invalidBetAmount),
                    true,
                    BigDecimal.valueOf(20),
                    SlotSymbol.BAR,
                    SlotSymbol.BAR,
                    SlotSymbol.BAR,
                    3,
                    LocalDateTime.now()
            );
            fail("Ungueltige BetAmounts muessen abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void createRejectsNullAmount() {
        try {
            SlotGame.create(
                    1L,
                    BigDecimal.TEN,
                    true,
                    null,
                    SlotSymbol.BAR,
                    SlotSymbol.BAR,
                    SlotSymbol.BAR,
                    3,
                    LocalDateTime.now()
            );
            fail("Null als Amount muss abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void createRejectsNullFirstSlot() {
        try {
            SlotGame.create(
                    1L,
                    BigDecimal.TEN,
                    true,
                    BigDecimal.valueOf(20),
                    null,
                    SlotSymbol.BAR,
                    SlotSymbol.BAR,
                    3,
                    LocalDateTime.now()
            );
            fail("Null als erstes Slot-Symbol muss abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void createRejectsNullSecondSlot() {
        try {
            SlotGame.create(
                    1L,
                    BigDecimal.TEN,
                    true,
                    BigDecimal.valueOf(20),
                    SlotSymbol.BAR,
                    null,
                    SlotSymbol.BAR,
                    3,
                    LocalDateTime.now()
            );
            fail("Null als zweites Slot-Symbol muss abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void createRejectsNullThirdSlot() {
        try {
            SlotGame.create(
                    1L,
                    BigDecimal.TEN,
                    true,
                    BigDecimal.valueOf(20),
                    SlotSymbol.BAR,
                    SlotSymbol.BAR,
                    null,
                    3,
                    LocalDateTime.now()
            );
            fail("Null als drittes Slot-Symbol muss abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void createRejectsNullPlayedAt() {
        try {
            SlotGame.create(
                    1L,
                    BigDecimal.TEN,
                    true,
                    BigDecimal.valueOf(20),
                    SlotSymbol.BAR,
                    SlotSymbol.BAR,
                    SlotSymbol.BAR,
                    3,
                    null
            );
            fail("Null als PlayedAt muss abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void createAcceptsPositiveExtremeValues() {
        BigDecimal veryLargeAmount = new BigDecimal("99999999999999999.99");
        LocalDateTime playedAt = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

        SlotGame slotGame = SlotGame.create(
                Long.MAX_VALUE,
                new BigDecimal("0.01"),
                true,
                veryLargeAmount,
                SlotSymbol.SEVEN,
                SlotSymbol.SEVEN,
                SlotSymbol.SEVEN,
                10,
                playedAt
        );

        assertEquals(Long.MAX_VALUE, slotGame.getUserId());
        assertEquals(new BigDecimal("0.01"), slotGame.getBetAmount());
        assertEquals(veryLargeAmount, slotGame.getAmount());
        assertEquals(playedAt, slotGame.getPlayedAt());
    }

    @Test
    void createRejectsNegativePayoutMultiplier() {
        try {
            SlotGame.create(
                    1L,
                    BigDecimal.TEN,
                    false,
                    BigDecimal.TEN.negate(),
                    SlotSymbol.CHERRY,
                    SlotSymbol.LEMON,
                    SlotSymbol.BELL,
                    -1,
                    LocalDateTime.now()
            );
            fail("Ein negativer PayoutMultiplier muss abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    private SlotGame createValidSlotGame(LocalDateTime playedAt) {
        return SlotGame.create(
                1L,
                BigDecimal.TEN,
                true,
                BigDecimal.valueOf(20),
                SlotSymbol.BAR,
                SlotSymbol.BAR,
                SlotSymbol.BAR,
                3,
                playedAt
        );
    }
}
