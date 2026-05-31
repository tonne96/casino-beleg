package beleg.slotsservice.view;

import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.model.SlotSymbol;

import java.math.BigDecimal;
import java.util.List;

/**
 * View-DTO fuer die JSON-Antwort einer Slot-Runde.
 *
 * Views trennen die interne Domain-Logik von der Darstellung nach aussen.
 */
public record SlotsPlayView(
        Long user,
        BigDecimal betAmount,
        boolean winning,
        BigDecimal amount,
        List<SlotSymbol> slotStates,
        int payoutMultiplier
) {
    public static SlotsPlayView from(Long user, BigDecimal betAmount, SlotGameResult result) {
        return new SlotsPlayView(
                user,
                betAmount,
                result.winning(),
                result.amount(),
                result.slotStates(),
                result.payoutMultiplier()
        );
    }
}
