package beleg.slotsservice.view;

public record SlotPayoutRuleView(
        String combination,
        int payoutMultiplier,
        String description
) {}
