package beleg.slotsservice.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Domain-Ergebnis einer einzelnen Slot-Runde.
 *
 * amount ist der Betrag, der später beim Banking-Service gebucht werden kann:
 * positiver Betrag = Gewinn, negativer Betrag = Verlust.
 */
public record SlotGameResult(
        boolean winning,
        BigDecimal amount,
        List<SlotSymbol> slotStates,
        int payoutMultiplier
) {
    public SlotGameResult {
        if (amount == null) {
            throw new IllegalArgumentException("Amount darf nicht null sein.");
        }
        if (slotStates == null || slotStates.size() != 3) {
            throw new IllegalArgumentException("Eine Slot-Runde braucht genau drei Symbole.");
        }
        for (SlotSymbol symbol : slotStates) {
            if (symbol == null) {
                throw new IllegalArgumentException("Slot-Symbole dürfen nicht null sein.");
            }
        }

        // Defensive Kopie: Von außen kann die Ergebnisliste danach nicht verändert werden.
        slotStates = List.copyOf(slotStates);
    }
}
