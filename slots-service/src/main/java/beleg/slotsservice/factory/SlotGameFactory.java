package beleg.slotsservice.factory;

import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotGameResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * SlotGameFactory = Implementierung fuer die Erzeugung von SlotGame.
 *
 * Die eigentliche Domain-Validierung bleibt in SlotGame.create(...).
 * Diese Factory kapselt aber, wo und wie SlotGame-Objekte erzeugt werden.
 */
@Service
public class SlotGameFactory implements ISlotGameFactory {

    /**
     * Erstellt eine neue SlotGame-Entity fuer die Speicherung in der Slots-DB.
     */
    @Override
    public SlotGame create(Long userId, BigDecimal betAmount, SlotGameResult result) {
        return SlotGame.create(userId, betAmount, result);
    }
}
