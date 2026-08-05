package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


// es werden alle BetStrategys gesammelt und die passende 'BetStrategy' ausgelöst
// mit dem optional kann der Handler eine unbekannte BetStrategy als FehlerTyp INVALID_BET_TYPE
// behandeln ohne eine exception werfen zu müssen
@Component
public class BetStrategyResolver {

    private final Map<RouletteBetType, IBetStrategy> strategiesByType;

    public BetStrategyResolver(List<IBetStrategy> betStrategies) {
  
        this.strategiesByType = betStrategies.stream()
                .collect(Collectors.toMap(IBetStrategy::getBetType, Function.identity()));

        List<RouletteBetType> missing = Arrays.stream(RouletteBetType.values())
                .filter(type -> !strategiesByType.containsKey(type))
                .toList();
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Keine IBetStrategy vorhanden fuer: " + missing);
        }
    }

    public Optional<IBetStrategy> resolve(RouletteBetType betType) {
        return Optional.ofNullable(strategiesByType.get(betType));
    }

    public List<IBetStrategy> all() {
        return strategiesByType.values().stream()
                .sorted(Comparator.comparing(IBetStrategy::getBetType))
                .toList();
    }
}