package beleg.slotsservice.view;

import beleg.slotsservice.model.SlotSymbol;

import java.util.List;

/**
 * View-DTO fuer die Regelbeschreibung des Slots-Service.
 */
public record SlotsRulesView(
        int reelCount,
        List<SlotSymbol> symbols,
        List<String> rules,
        List<SlotPayoutRuleView> payoutRules
) {}
