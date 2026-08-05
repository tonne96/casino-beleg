package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Component;

import java.util.List;

// man setzt auf ein dutzend der zahlen 1-12, 13-24, 25-36
// bei 0 gewinnt immer das Casino
@Component
public class DozenBetStrategy implements IBetStrategy {

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.DOZEN;
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
        int dozen = ((winningNumber - 1) / 12) + 1;
        return betNumbers.get(0) == dozen;
    }
}