package beleg.slotsservice.view;

import java.math.BigDecimal;

/**
 * View-DTO fuer eine einzelne Gewinnchance.
 */
public record SlotChanceView(
        String result,
        int matchingOutcomes,
        int totalOutcomes,
        BigDecimal probabilityPercent,
        int payoutMultiplier
) {}
