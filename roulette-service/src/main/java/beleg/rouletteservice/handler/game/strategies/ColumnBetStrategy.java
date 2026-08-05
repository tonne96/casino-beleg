package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Component;

import java.util.List;

// Die drei Spalten 1-34 / 2-35 / 3-36
// Bei 0 gewinnt immer das Casino
@Component
public class ColumnBetStrategy implements IBetStrategy {

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.COLUMN;
    }

    @Override
    public int getPayoutMultiplier() {
        return 2; 
    }

    @Override
    public IResult<Void, Failures> validateNumbers(List<Integer> betNumbers) {
        return BetNumberChecks.oneToThree(betNumbers);
    }

    @Override
    public boolean isWinning(List<Integer> betNumbers, int winningNumber) {
        if (winningNumber == 0) {
            return false;
        }
        int column = ((winningNumber - 1) % 3) + 1;
        return betNumbers.get(0) == column;
    }
}