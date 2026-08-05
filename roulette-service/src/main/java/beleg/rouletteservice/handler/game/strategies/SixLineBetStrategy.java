package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Component;

import java.util.List;


// man setzt auf 6 aufeinander folgende zahlen, dabei muss sich die ersten zahl in der linken spalte befinden
@Component
public class SixLineBetStrategy implements IBetStrategy {

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.SIX_LINE;
    }

    @Override
    public boolean isWinning(List<Integer> betNumbers, int winningNumber) {
        int a = betNumbers.get(0);
        return winningNumber >= a && winningNumber <= a + 5;
    }
}