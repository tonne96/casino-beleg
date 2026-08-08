package beleg.slotsservice.handler.game;

import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.model.SlotSymbol;

import java.math.BigDecimal;
import java.util.List;

/**
 * Vertrag für die reine, von HTTP und Datenbank unabhängige Spiellogik.
 */
public interface ISlotGameHandler {

    SlotGameResult play(BigDecimal betAmount);

    /**
     * Bewertet eine Runde mit vorgegebenen Symbolen.
     * Die vorgegebenen Symbole ermöglichen deterministische Unit-Tests.
     */
    SlotGameResult evaluate(BigDecimal betAmount, List<SlotSymbol> slotStates);
}
