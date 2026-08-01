package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;

import java.util.List;
// interface für die einzelnen Wettarten, damit kann man die einzelnen Wettarten
// wiederverwenden und zusätzlich neue einführen, wenn man auch andere Roulette Spiele anbieten möchte
// z.B. amerikanisches Roulettte
public interface BetStrategy {

    RouletteBetType getBetType();

    boolean isWinning(List<Integer> betNumbers, int winningNumber);
}