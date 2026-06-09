package beleg.slotsservice.handler.play;

import beleg.slotsservice.view.PlaySlotsRequest;
import beleg.slotsservice.view.SlotsPlayView;

/**
 * Interface fuer den kompletten Play-Use-Case des Slots-Service.
 *
 * Der Controller kennt nur diesen Vertrag. Der konkrete Ablauf liegt in der
 * Implementierung PlaySlotsHandler.
 */
public interface IPlaySlotsHandler {

    /**
     * Spielt eine vollstaendige Slot-Runde und liefert die Antwort fuer den Client.
     */
    SlotsPlayView play(PlaySlotsRequest request);
}
