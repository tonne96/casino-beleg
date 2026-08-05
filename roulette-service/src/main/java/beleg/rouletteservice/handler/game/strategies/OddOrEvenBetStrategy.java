package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Component;

import java.util.List;

// es wird auf die geraden oder ungeraden zahlen gesetzt
// 0 zaehlt zu keiner der beiden kategorien, das Casino gewinnt immer bei 0
@Component
public class OddOrEvenBetStrategy implements IBetStrategy {

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.ODD_OR_EVEN;
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
        boolean isOdd = winningNumber % 2 == 1;
        boolean choseOdd = betNumbers.get(0) == 1;
        return choseOdd == isOdd;
    }
}