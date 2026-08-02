package beleg.slotsservice.view;

import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotSymbol;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Test fuer die JSON-Antwort einer gespielten Slot-Runde.
 */
class SlotsPlayViewTest {

    @Test
    void fromCopiesAllEntityValues() {
        LocalDateTime playedAt = LocalDateTime.of(2026, 8, 2, 13, 0);
        SlotGame game = SlotGame.create(
                7L,
                new BigDecimal("99999999999999999.99"),
                true,
                new BigDecimal("899999999999999999.91"),
                SlotSymbol.SEVEN,
                SlotSymbol.SEVEN,
                SlotSymbol.SEVEN,
                10,
                playedAt
        );

        SlotsPlayView view = SlotsPlayView.from(game);

        assertNull(view.gameId());
        assertEquals(7L, view.user());
        assertEquals(new BigDecimal("99999999999999999.99"), view.betAmount());
        assertTrue(view.winning());
        assertEquals(new BigDecimal("899999999999999999.91"), view.amount());
        assertEquals(List.of(SlotSymbol.SEVEN, SlotSymbol.SEVEN, SlotSymbol.SEVEN), view.slotStates());
        assertEquals(10, view.payoutMultiplier());
        assertEquals(playedAt, view.playedAt());
    }
}
