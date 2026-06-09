package beleg.slotsservice.handler.stats;

import beleg.slotsservice.view.SlotsStatsView;

/**
 * Interface fuer zusammengefasste Slot-Statistiken.
 *
 * Der Controller kennt nur diesen Vertrag; die konkrete Berechnung liegt in SlotStatsHandler.
 */
public interface ISlotStatsHandler {

    /**
     * Berechnet die Gesamtstatistik ueber alle gespeicherten Slot-Runden.
     */
    SlotsStatsView getStats();
}
