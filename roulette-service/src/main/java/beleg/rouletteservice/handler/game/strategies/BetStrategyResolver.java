package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


// es werden alle BetStrategys gesammelt und die passende 'BetStrategy' ausgelöst
// mit dem optional kann der Handler eine unbekannte BetStrategy als FehlerTyp INVALID_BET_TYPE
// behandeln ohne eine exception werfen zu müssen
@Component
public class BetStrategyResolver {

    private final Map<RouletteBetType, BetStrategy> strategiesByType;

    public BetStrategyResolver(List<BetStrategy> betStrategies) {
        this.strategiesByType = betStrategies.stream()
                .collect(Collectors.toMap(BetStrategy::getBetType, Function.identity()));
    }

    public Optional<BetStrategy> resolve(RouletteBetType betType) {
        return Optional.ofNullable(strategiesByType.get(betType));
    }
}