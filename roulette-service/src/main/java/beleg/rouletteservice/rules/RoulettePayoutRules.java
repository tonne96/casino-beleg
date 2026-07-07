package beleg.rouletteservice.rules;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RoulettePayoutRules {
    // === QUOTEN PRO WETTTYP ===
    private static final Map<RouletteBetType, Integer> PAYOUT_MULTIPLIERS = Map.ofEntries(
            Map.entry(RouletteBetType.RED_OR_BLACK, 1),      // 1:1
            Map.entry(RouletteBetType.ODD_OR_EVEN, 1),       // 1:1
            Map.entry(RouletteBetType.LOW_OR_HIGH, 1),       // 1:1 (1-18 oder 19-36)
            Map.entry(RouletteBetType.DOZEN, 2),             // 2:1 (12 Zahlen)
            Map.entry(RouletteBetType.COLUMN, 2),            // 2:1 (12 Zahlen)
            Map.entry(RouletteBetType.SIX_LINE, 5),          // 5:1 (6 Zahlen)
            Map.entry(RouletteBetType.CORNER, 8),            // 8:1 (4 Zahlen)
            Map.entry(RouletteBetType.STREET, 11),           // 11:1 (3 Zahlen)
            Map.entry(RouletteBetType.SPLIT, 17),            // 17:1 (2 Zahlen)
            Map.entry(RouletteBetType.SINGLE, 35)            // 35:1 (1 Zahl)
    );

    public Integer getPayoutMultiplier(RouletteBetType betType) {
        return PAYOUT_MULTIPLIERS.getOrDefault(betType, 0);
    }
}