package beleg.rouletteservice.factory;

import beleg.rouletteservice.model.IRouletteGame;
import beleg.rouletteservice.model.RouletteGameImpl;
import beleg.rouletteservice.model.RouletteGameResult;
import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.result.Success;
import beleg.rouletteservice.rules.RouletteBetType;
import beleg.rouletteservice.handler.game.strategies.BetStrategyResolver;
import beleg.rouletteservice.handler.game.strategies.IBetStrategy;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class RouletteGameFactoryImpl implements IRouletteGameFactory {

    private final BetStrategyResolver betStrategyResolver;

    public RouletteGameFactoryImpl(BetStrategyResolver betStrategyResolver) {
        this.betStrategyResolver = betStrategyResolver;
    }

    @Override
    public IResult<IRouletteGame, Failures> create(
            Long userId,
            BigDecimal betAmount,
            RouletteBetType betType,
            List<Integer> betNumbers,
            RouletteGameResult result) {

        if (result == null || betType == null) {
            return new Failure<>(Failures.NOT_NULL);
        }

        Optional<IBetStrategy> maybeStrategy = betStrategyResolver.resolve(betType);
        if (maybeStrategy.isEmpty()) {
            return new Failure<>(Failures.INVALID_BET_TYPE);
        }

        IResult<Void, Failures> betValidation = maybeStrategy.get().validate(betNumbers);
        if (!betValidation.isSuccess()) {
            return new Failure<>(betValidation.getMessage());
        }

        IResult<RouletteGameImpl, Failures> creationResult = RouletteGameImpl.create(
                userId,
                betAmount,
                result.winning(),
                result.amount(),
                betType,
                betNumbers,
                result.winningNumber(),
                result.payoutMultiplier(),
                LocalDateTime.now()
        );
        if (!creationResult.isSuccess()) {
            return new Failure<>(creationResult.getMessage());
        }
        return new Success<>(creationResult.getValue());
    }
}