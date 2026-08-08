package beleg.slotsservice.view;

import java.util.List;

public record SlotsChancesView(
        int reelCount,
        int symbolCount,
        int totalOutcomes,
        List<SlotChanceView> chances,
        String note
) {}
