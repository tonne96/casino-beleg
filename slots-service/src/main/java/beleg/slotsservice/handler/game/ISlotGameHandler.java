package beleg.slotsservice.handler.game;

import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.model.SlotSymbol;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface fuer die reine Slot-Spiellogik.
 *
 * Controller und andere Klassen haengen nur von diesem Vertrag ab.
 * Die konkrete Ausfuehrung liegt in SlotGameHandler.
 */
public interface ISlotGameHandler {

    /**
     * Spielt eine echte zufaellige Slot-Runde.
     */
    SlotGameResult play(BigDecimal betAmount);

    /**
     * Bewertet eine Runde mit vorgegebenen Symbolen.
     * Diese Methode ist besonders gut fuer spaetere Unit-Tests.
     */
    SlotGameResult evaluate(BigDecimal betAmount, List<SlotSymbol> slotStates);
}
