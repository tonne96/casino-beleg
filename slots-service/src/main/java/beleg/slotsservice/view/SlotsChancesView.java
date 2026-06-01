package beleg.slotsservice.view;

import java.util.List;

/**
 * View-DTO fuer die Wahrscheinlichkeiten des Slots-Service.
 */
public record SlotsChancesView(
        int reelCount,
        int symbolCount,
        int totalOutcomes,
        List<SlotChanceView> chances,
        String note
) {}
