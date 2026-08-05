package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

// man setzt entweder auf die roten oder schwarzen zahlen
// die 0 ist grün, es gewinnt somit das casino immer bei 0
@Component
public class RedOrBlackBetStrategy implements IBetStrategy {

    private static final Set<Integer> RED_NUMBERS = Set.of(
            1, 3, 5, 7, 9, 12, 14, 16, 18,
            19, 21, 23, 25, 27, 30, 32, 34, 36
    );

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.RED_OR_BLACK;
    }

    @Override
    public int getPayoutMultiplier() {
        return 1; 
    }

    @Override
    public IResult<Void, Failures> validateNumbers(List<Integer> betNumbers) {
        return BetNumberChecks.binaryChoice(betNumbers);
    }

    @Override
    public boolean isWinning(List<Integer> betNumbers, int winningNumber) {
        if (winningNumber == 0) {
            return false;
        }
        boolean isRed = RED_NUMBERS.contains(winningNumber);
        boolean choseRed = betNumbers.get(0) == 1;
        return choseRed == isRed;
    }
}