package beleg.slotsservice.view;

import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotSymbol;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * View-DTO fuer die JSON-Aussendarstellung einer gespeicherten Slot-Runde.
 */
public record SlotGameView(
        Long gameId,
        Long user,
        BigDecimal betAmount,
        boolean winning,
        BigDecimal amount,
        List<SlotSymbol> slotStates,
        int payoutMultiplier,
        LocalDateTime playedAt
) {
    public static SlotGameView from(SlotGame slotGame) {
        return new SlotGameView(
                slotGame.getId(),
                slotGame.getUserId(),
                slotGame.getBetAmount(),
                slotGame.isWinning(),
                slotGame.getAmount(),
                slotGame.getSlotStates(),
                slotGame.getPayoutMultiplier(),
                slotGame.getPlayedAt()
        );
    }
}
