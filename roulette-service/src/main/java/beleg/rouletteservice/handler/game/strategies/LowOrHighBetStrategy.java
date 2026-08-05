package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Component;

import java.util.List;

// bei low setzt man auf 1-18 , bei high auf 19-36
// bei 0 gewinnt immer das Casino
@Component
public class LowOrHighBetStrategy implements IBetStrategy {

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.LOW_OR_HIGH;
    }

    @Override
    public boolean isWinning(List<Integer> betNumbers, int winningNumber) {
        if (winningNumber == 0) {
            return false;
        }
        boolean isHigh = winningNumber >= 19;
        boolean choseHigh = betNumbers.get(0) == 1;
        return choseHigh == isHigh;
    }
}