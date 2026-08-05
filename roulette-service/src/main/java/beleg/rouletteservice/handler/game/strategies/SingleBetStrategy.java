package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SingleBetStrategy implements IBetStrategy {

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.SINGLE;
    }

    @Override
    public int getPayoutMultiplier() {
        return 35;
    }

    @Override
    public IResult<Void, Failures> validateNumbers(List<Integer> betNumbers) {
        if (betNumbers.size() != 1) {
            return BetNumberChecks.OUT_OF_RANGE;
        }
        int n = betNumbers.get(0);
        return (n >= 0 && n <= 36) ? BetNumberChecks.OK : BetNumberChecks.OUT_OF_RANGE;
    }

    @Override
    public boolean isWinning(List<Integer> betNumbers, int winningNumber) {
        return betNumbers.get(0) == winningNumber;
    }
}