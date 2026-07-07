package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Component;

import java.util.List;


// es wird auf die geraden oder ungeraden zahlen gesetz
// 0 zählt zu keinen der beiden kategorien, das Casino gewinnt immer bei 0
@Component
public class OddOrEvenBetStrategy implements BetStrategy {

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.ODD_OR_EVEN;
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