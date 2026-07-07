package beleg.rouletteservice.factory;

import beleg.rouletteservice.model.RouletteGame;
import beleg.rouletteservice.model.RouletteGameResult;
import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.Result;
import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RouletteGameFactoryImpl implements RouletteGameFactory {

    @Override
    public Result<RouletteGame, Failures> create(
            Long userId,
            BigDecimal betAmount,
            RouletteBetType betType,
            List<Integer> betNumbers,
            RouletteGameResult result) {

        if (result == null) {
            return new Failure<>(Failures.NOT_NULL);
        }

        return RouletteGame.create(
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
    }
}
