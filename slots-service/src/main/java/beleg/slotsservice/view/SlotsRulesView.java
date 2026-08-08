package beleg.slotsservice.view;

import beleg.slotsservice.model.SlotSymbol;

import java.util.List;

public record SlotsRulesView(
        int reelCount,
        List<SlotSymbol> symbols,
        List<String> rules,
        List<SlotPayoutRuleView> payoutRules
) {}
