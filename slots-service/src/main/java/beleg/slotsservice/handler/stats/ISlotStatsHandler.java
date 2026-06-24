package beleg.slotsservice.handler.stats;

import beleg.slotsservice.view.SlotsStatsView;
import beleg.slotsservice.view.SlotsUserStatsView;

import java.util.Optional;

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

    /**
     * Berechnet die Statistik fuer einen bestimmten User.
     *
     * Optional.empty bedeutet: Fuer diesen User gibt es keine gespeicherten
     * Slot-Runden und damit auch keine Statistik.
     */
    Optional<SlotsUserStatsView> getUserStats(Long userId);
}
