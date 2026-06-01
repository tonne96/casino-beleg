package beleg.slotsservice.view;

/**
 * View-DTO fuer eine einzelne Auszahlungsregel.
 */
public record SlotPayoutRuleView(
        String combination,
        int payoutMultiplier,
        String description
) {}
