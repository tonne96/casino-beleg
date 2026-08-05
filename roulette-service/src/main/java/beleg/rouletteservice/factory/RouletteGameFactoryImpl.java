package beleg.rouletteservice.factory;

import beleg.rouletteservice.model.RouletteGameImpl;
import beleg.rouletteservice.model.RouletteGameResult;
import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RouletteGameFactoryImpl implements IRouletteGameFactory {

    @Override
    public IResult<RouletteGameImpl, Failures> create(
            Long userId,
            BigDecimal betAmount,
            RouletteBetType betType,
            List<Integer> betNumbers,
            RouletteGameResult result) {

        if (result == null) {
            return new Failure<>(Failures.NOT_NULL);
        }

        return RouletteGameImpl.create(
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
