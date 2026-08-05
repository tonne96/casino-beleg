package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Component;

import java.util.List;


// man kann auf eine Zahl setzen 0-36
@Component
public class SingleBetStrategy implements IBetStrategy {

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.SINGLE;
    }

    @Override
    public boolean isWinning(List<Integer> betNumbers, int winningNumber) {
        return betNumbers.get(0) == winningNumber;
    }
}