package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Component;

import java.util.List;

// Corner beschreibt ein viereck an zahlen auf das man setzt,
// Bsp.: 1,2,4,5 - bei unseren Wetten gibt man die kleinste Zahl des vierecks an
// a muss in der linken oder mittleren spalte liegen (a % 3 != 0), max. 32 damit a+4 <= 36
@Component
public class CornerBetStrategy implements IBetStrategy {

    private static final int MAX_START = 32;

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.CORNER;
    }

    @Override
    public int getPayoutMultiplier() {
        return 8; 
    }

    @Override
    public IResult<Void, Failures> validateNumbers(List<Integer> betNumbers) {
        if (betNumbers.size() != 1) {
            return BetNumberChecks.OUT_OF_RANGE;
        }
        int a = betNumbers.get(0);
        if (a < 1 || a > MAX_START || a % 3 == 0) {
            return BetNumberChecks.OUT_OF_RANGE;
        }
        return BetNumberChecks.OK;
    }

    @Override
    public boolean isWinning(List<Integer> betNumbers, int winningNumber) {
        int a = betNumbers.get(0);
        return winningNumber == a || winningNumber == a + 1 || winningNumber == a + 3 || winningNumber == a + 4;
    }
}