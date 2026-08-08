package beleg.slotsservice.view;

import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotSymbol;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Test für die Außendarstellung einer gespeicherten Slot-Runde.
 */
class SlotGameViewTest {

    @Test
    void fromCopiesAllEntityValues() {
        LocalDateTime playedAt = LocalDateTime.of(2026, 8, 2, 12, 30);
        SlotGame game = SlotGame.create(
                Long.MAX_VALUE,
                new BigDecimal("0.01"),
                false,
                new BigDecimal("-0.01"),
                SlotSymbol.CHERRY,
                SlotSymbol.LEMON,
                SlotSymbol.BELL,
                0,
                playedAt
        );

        SlotGameView view = SlotGameView.from(game);

        assertNull(view.gameId());
        assertEquals(Long.MAX_VALUE, view.user());
        assertEquals(new BigDecimal("0.01"), view.betAmount());
        assertFalse(view.winning());
        assertEquals(new BigDecimal("-0.01"), view.amount());
        assertEquals(List.of(SlotSymbol.CHERRY, SlotSymbol.LEMON, SlotSymbol.BELL), view.slotStates());
        assertEquals(0, view.payoutMultiplier());
        assertEquals(playedAt, view.playedAt());
    }
}
