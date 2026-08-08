package beleg.slotsservice.view;

import java.math.BigDecimal;

public record SlotChanceView(
        String result,
        int matchingOutcomes,
        int totalOutcomes,
        BigDecimal probabilityPercent,
        int payoutMultiplier
) {}
