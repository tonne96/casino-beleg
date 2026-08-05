package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import org.springframework.stereotype.Component;

import java.util.List;


// bei split werden auf zwei benachbarte zahlen gesetzt
// hier kann man auch auf 0,1 / 0,2 und 0,3 setzen
@Component
public class SplitBetStrategy implements IBetStrategy {

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.SPLIT;
    }

    @Override
    public int getPayoutMultiplier() {
        return 17; // 17:1 (2 Zahlen)
    }

    @Override
    public IResult<Void, Failures> validateNumbers(List<Integer> betNumbers) {
        if (betNumbers.size() != 2) {
            return BetNumberChecks.OUT_OF_RANGE;
        }
        int a = betNumbers.get(0);
        int b = betNumbers.get(1);
        if (a < 0 || a > 36 || b < 0 || b > 36 || a == b) {
            return BetNumberChecks.OUT_OF_RANGE;
        }
        return areAdjacentOnTable(a, b) ? BetNumberChecks.OK : BetNumberChecks.OUT_OF_RANGE;
    }

    @Override
    public boolean isWinning(List<Integer> betNumbers, int winningNumber) {
        return betNumbers.contains(winningNumber);
    }

    // horizontale Nachbarn: Differenz 1 in derselben Reihe
    // vertikale Nachbarn: Differenz 3
    // Ausnahme: 0 ist mit 1, 2, 3 benachbart
    private static boolean areAdjacentOnTable(int a, int b) {
        if ((a == 0 && (b == 1 || b == 2 || b == 3)) || (b == 0 && (a == 1 || a == 2 || a == 3))) {
            return true;
        }
        int rowA = (a - 1) / 3;
        int rowB = (b - 1) / 3;
        boolean horizontallyAdjacent = rowA == rowB && Math.abs(a - b) == 1;
        boolean verticallyAdjacent = Math.abs(a - b) == 3;
        return horizontallyAdjacent || verticallyAdjacent;
    }
}