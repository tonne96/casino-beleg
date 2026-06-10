package beleg.slotsservice.factory;

import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.model.SlotSymbol;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * SlotGameFactory = Implementierung fuer die Erzeugung von SlotGame.
 *
 * Diese Factory uebersetzt das Ergebnis des Game-Simulators in eine
 * speicherbare SlotGame-Entity.
 */
@Service
public class SlotGameFactory implements ISlotGameFactory {

    /**
     * Erstellt eine neue SlotGame-Entity fuer die Speicherung in der Slots-DB.
     */
    @Override
    public SlotGame create(Long userId, BigDecimal betAmount, SlotGameResult result) {
        if (result == null) {
            throw new IllegalArgumentException("SlotGameResult darf nicht null sein.");
        }

        List<SlotSymbol> slotStates = result.slotStates();

        // Hier wird das Simulator-Ergebnis in einzelne Entity-Felder uebersetzt.
        return SlotGame.create(
                userId,
                betAmount,
                result.winning(),
                result.amount(),
                slotStates.get(0),
                slotStates.get(1),
                slotStates.get(2),
                result.payoutMultiplier(),
                LocalDateTime.now()
        );
    }
}
