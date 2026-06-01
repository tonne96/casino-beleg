package beleg.slotsservice.handler.info;

import beleg.slotsservice.model.SlotSymbol;
import beleg.slotsservice.view.SlotChanceView;
import beleg.slotsservice.view.SlotPayoutRuleView;
import beleg.slotsservice.view.SlotsChancesView;
import beleg.slotsservice.view.SlotsRulesView;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * Handler = Info-Logik.
 *
 * Diese Klasse liefert Regeln und Wahrscheinlichkeiten fuer Swagger/Clients.
 * Sie spielt keine Runde und veraendert keine Daten.
 */
@Service
public class SlotInfoHandler {

    private static final int REEL_COUNT = 3;
    private static final int JACKPOT_MULTIPLIER = 10;
    private static final int THREE_EQUAL_MULTIPLIER = 3;
    private static final int TWO_EQUAL_MULTIPLIER = 1;
    private static final int LOSS_MULTIPLIER = 0;

    /**
     * Liefert die menschenlesbaren Spielregeln.
     */
    public SlotsRulesView getRules() {
        return new SlotsRulesView(
                REEL_COUNT,
                getSymbols(),
                List.of(
                        "Eine Slot-Runde besteht aus genau drei Walzen.",
                        "Jede Walze zeigt eines der erlaubten Slot-Symbole.",
                        "Drei SEVEN ergeben den Jackpot.",
                        "Drei gleiche Symbole ergeben einen normalen Gewinn.",
                        "Zwei gleiche Symbole ergeben einen kleinen Gewinn.",
                        "Drei unterschiedliche Symbole ergeben einen Verlust."
                ),
                getPayoutRules()
        );
    }

    /**
     * Liefert die Chancen auf Basis gleichverteilter Symbole.
     */
    public SlotsChancesView getChances() {
        int symbolCount = SlotSymbol.values().length;
        int totalOutcomes = calculateTotalOutcomes(symbolCount);

        int jackpotOutcomes = 1;
        int threeEqualOutcomes = symbolCount - 1;
        int twoEqualOutcomes = symbolCount * REEL_COUNT * (symbolCount - 1);
        int lossOutcomes = symbolCount * (symbolCount - 1) * (symbolCount - 2);

        return new SlotsChancesView(
                REEL_COUNT,
                symbolCount,
                totalOutcomes,
                List.of(
                        new SlotChanceView(
                                "JACKPOT_THREE_SEVENS",
                                jackpotOutcomes,
                                totalOutcomes,
                                calculateProbabilityPercent(jackpotOutcomes, totalOutcomes),
                                JACKPOT_MULTIPLIER
                        ),
                        new SlotChanceView(
                                "THREE_EQUAL_NON_SEVEN",
                                threeEqualOutcomes,
                                totalOutcomes,
                                calculateProbabilityPercent(threeEqualOutcomes, totalOutcomes),
                                THREE_EQUAL_MULTIPLIER
                        ),
                        new SlotChanceView(
                                "TWO_EQUAL_SYMBOLS",
                                twoEqualOutcomes,
                                totalOutcomes,
                                calculateProbabilityPercent(twoEqualOutcomes, totalOutcomes),
                                TWO_EQUAL_MULTIPLIER
                        ),
                        new SlotChanceView(
                                "THREE_DIFFERENT_SYMBOLS",
                                lossOutcomes,
                                totalOutcomes,
                                calculateProbabilityPercent(lossOutcomes, totalOutcomes),
                                LOSS_MULTIPLIER
                        )
                ),
                "Die Wahrscheinlichkeiten gelten, wenn jedes Symbol auf jeder Walze gleich wahrscheinlich ist."
        );
    }

    private List<SlotSymbol> getSymbols() {
        return List.copyOf(Arrays.asList(SlotSymbol.values()));
    }

    private List<SlotPayoutRuleView> getPayoutRules() {
        return List.of(
                new SlotPayoutRuleView("SEVEN + SEVEN + SEVEN", JACKPOT_MULTIPLIER, "Jackpot: Einsatz mal 10"),
                new SlotPayoutRuleView("Drei gleiche Symbole", THREE_EQUAL_MULTIPLIER, "Normaler Gewinn: Einsatz mal 3"),
                new SlotPayoutRuleView("Zwei gleiche Symbole", TWO_EQUAL_MULTIPLIER, "Kleiner Gewinn: Einsatz mal 1"),
                new SlotPayoutRuleView("Drei unterschiedliche Symbole", LOSS_MULTIPLIER, "Verlust: Einsatz wird negativ gebucht")
        );
    }

    private int calculateTotalOutcomes(int symbolCount) {
        int totalOutcomes = 1;
        for (int reel = 0; reel < REEL_COUNT; reel++) {
            totalOutcomes *= symbolCount;
        }
        return totalOutcomes;
    }

    private BigDecimal calculateProbabilityPercent(int matchingOutcomes, int totalOutcomes) {
        return BigDecimal.valueOf(matchingOutcomes)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalOutcomes), 2, RoundingMode.HALF_UP);
    }
}
