package beleg.slotsservice.handler.stats;

import beleg.slotsservice.view.SlotsStatsView;
import beleg.slotsservice.view.SlotsUserStatsView;

import java.util.Optional;

/**
 * Vertrag für zusammengefasste Slot-Statistiken.
 */
public interface ISlotStatsHandler {

    SlotsStatsView getStats();

    /**
     * {@code Optional.empty()} bedeutet, dass für den User keine Spiele vorliegen.
     */
    Optional<SlotsUserStatsView> getUserStats(Long userId);
}
