package beleg.slotsservice.factory;

import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotGameResult;

import java.math.BigDecimal;

/**
 * Vertrag für die Erzeugung von SlotGame-Entities.
 * Handler hängen dadurch nicht von der konkreten Erzeugungslogik ab.
 */
public interface ISlotGameFactory {

    SlotGame create(Long userId, BigDecimal betAmount, SlotGameResult result);
}
