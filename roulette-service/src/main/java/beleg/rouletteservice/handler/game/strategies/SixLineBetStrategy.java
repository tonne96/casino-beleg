package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Component;

import java.util.List;

// man setzt auf 6 aufeinander folgende zahlen, dabei muss sich die erste zahl
// in der linken spalte befinden -> (a - 1) % 3 == 0, max. 31 damit a+5 <= 36
@Component
public class SixLineBetStrategy implements IBetStrategy {

    private static final int MAX_START = 31;

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.SIX_LINE;
    }

    @Override
    public int getPayoutMultiplier() {
        return 5; 
    }

    @Override
    public IResult<Void, Failures> validateNumbers(List<Integer> betNumbers) {
        return BetNumberChecks.rowStart(betNumbers, MAX_START);
    }

    @Override
    public boolean isWinning(List<Integer> betNumbers, int winningNumber) {
        int a = betNumbers.get(0);
        return winningNumber >= a && winningNumber <= a + 5;
    }
}