package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Component;

import java.util.List;


// bei split werden auf zwei benachbarte zahlen gesetzt
// hier kann man auch auf 0,1 / 0,2 und 0,3 setzen
@Component
public class SplitBetStrategy implements BetStrategy {

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.SPLIT;
    }

    @Override
    public boolean isWinning(List<Integer> betNumbers, int winningNumber) {
        return betNumbers.contains(winningNumber);
    }
}