package beleg.rouletteservice.model;

import beleg.rouletteservice.result.*;

import java.math.BigDecimal;
import java.util.List;

public record RouletteGameResult(
        boolean winning,
        BigDecimal amount,
        int winningNumber,
        int payoutMultiplier
) {

    public static IResult<RouletteGameResult, Failures> create(
            boolean winning, BigDecimal amount, int winningNumber, int payoutMultiplier) {

        List<IResult<Void, Failures>> validations = List.of(
                validateAmount(amount, winning),
                validateWinningNumber(winningNumber),
                validatePayoutMultiplier(payoutMultiplier, winning)
        );

        IResult<Void, Failures> validationResult = Results.firstFailure(validations);
        if (!validationResult.isSuccess()) {
            return new Failure<>(validationResult.getMessage());
        }

        return new Success<>(new RouletteGameResult(winning, amount, winningNumber, payoutMultiplier));
    }

    private static IResult<Void, Failures> validateAmount(BigDecimal amount, boolean winning) {
        if (amount == null) {
            return new Failure<>(Failures.NOT_NULL);
        }
        if (winning && amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new Failure<>(Failures.INCONSISTENT_ROUND_RESULT);
        }
        if (!winning && amount.compareTo(BigDecimal.ZERO) >= 0) {
            return new Failure<>(Failures.INCONSISTENT_ROUND_RESULT);
        }
        return new Success<>(null);
    }

    private static IResult<Void, Failures> validateWinningNumber(int winningNumber) {
        if (winningNumber < 0 || winningNumber > 36) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        return new Success<>(null);
    }

    private static IResult<Void, Failures> validatePayoutMultiplier(int payoutMultiplier, boolean winning) {
        if (payoutMultiplier < 0) {
            return new Failure<>(Failures.NOT_NEGATIVE);
        }
        if (winning && payoutMultiplier == 0) {
            return new Failure<>(Failures.INCONSISTENT_ROUND_RESULT);
        }
        return new Success<>(null);
    }

}
