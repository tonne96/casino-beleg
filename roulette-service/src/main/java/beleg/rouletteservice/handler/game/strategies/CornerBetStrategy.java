package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Component;

import java.util.List;


// Corner beschriebt ein viereck an zahlen auf das man setzt,
// Bsp.: 1,2,4,5  bei unseren Wetten gibt man einfach die kleinste Zahl des vierecks an

@Component
public class CornerBetStrategy implements IBetStrategy {

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.CORNER;
    }

    @Override
    public boolean isWinning(List<Integer> betNumbers, int winningNumber) {
        int a = betNumbers.get(0);
        return winningNumber == a || winningNumber == a + 1
                || winningNumber == a + 3 || winningNumber == a + 4;
    }
}