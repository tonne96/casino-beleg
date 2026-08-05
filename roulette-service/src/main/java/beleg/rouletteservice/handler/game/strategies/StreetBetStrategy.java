package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Component;

import java.util.List;

// Street sind drei zahlen in einer reihe, kleinste zahl muss in der linken spalte sein
// Bsp.: 1-3 -> (a - 1) % 3 == 0, max. 34 damit a+2 <= 36
// 0 kann man momentan nicht setzen, obwohl es auch die wettmoeglichkeit first three gaebe,
// die 0-2 enthaelt - ueberlegen noch zu implementieren
@Component
public class StreetBetStrategy implements IBetStrategy {

    private static final int MAX_START = 34;

    @Override
    public RouletteBetType getBetType() {
        return RouletteBetType.STREET;
    }

    @Override
    public int getPayoutMultiplier() {
        return 11; 
    }

    @Override
    public IResult<Void, Failures> validateNumbers(List<Integer> betNumbers) {
        return BetNumberChecks.rowStart(betNumbers, MAX_START);
    }

    @Override
    public boolean isWinning(List<Integer> betNumbers, int winningNumber) {
        int a = betNumbers.get(0);
        return winningNumber == a || winningNumber == a + 1 || winningNumber == a + 2;
    }
}