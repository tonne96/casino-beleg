package beleg.slotsservice.factory;

import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotGameResult;

import java.math.BigDecimal;

/**
 * Interface fuer die Erzeugung von SlotGame-Entities.
 *
 * Handler haengen dadurch nicht direkt an der konkreten Erzeugungslogik.
 */
public interface ISlotGameFactory {

    /**
     * Erstellt eine neue gespeicherte Slot-Runde aus dem berechneten Spielergebnis.
     */
    SlotGame create(Long userId, BigDecimal betAmount, SlotGameResult result);
}
