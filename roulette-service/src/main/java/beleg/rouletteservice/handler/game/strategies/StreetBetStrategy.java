package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Component;

import java.util.List;


// Street sind drei zahlen in einer reihe, kleinste zahl muss in der linken spalte sein
// Bsp.: 1-3
//   0 kann man momentan nicht setzen, obwohl es auch die wettmöglichkeit first three gäbe,
// die 0-2 enthält - überlegen noch zu implementieren
@Component
public class StreetBetStrategy implements BetStrategy {

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.STREET;
    }

    @Override
    public boolean isWinning(List<Integer> betNumbers, int winningNumber) {
        int a = betNumbers.get(0);
        return winningNumber == a || winningNumber == a + 1 || winningNumber == a + 2;
    }
}