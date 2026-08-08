package beleg.slotsservice.handler.play;

import beleg.slotsservice.view.PlaySlotsRequest;
import beleg.slotsservice.view.SlotsPlayView;

/**
 * Vertrag für den vollständigen Ablauf einer Slot-Runde.
 */
public interface IPlaySlotsHandler {

    SlotsPlayView play(PlaySlotsRequest request);
}
