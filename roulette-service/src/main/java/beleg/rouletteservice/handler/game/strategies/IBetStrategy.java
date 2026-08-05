package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;

import java.util.List;

public interface IBetStrategy {

    RouletteBetType getBetType();

    int getPayoutMultiplier();

    default IResult<Void, Failures> validate(List<Integer> betNumbers) {
        if (betNumbers == null) {
            return new Failure<>(Failures.NOT_NULL);
        }
        for (Integer n : betNumbers) {
            if (n == null) {
                return new Failure<>(Failures.NOT_NULL);
            }
        }
        return validateNumbers(betNumbers);
    }

    IResult<Void, Failures> validateNumbers(List<Integer> betNumbers);

    boolean isWinning(List<Integer> betNumbers, int winningNumber);
}