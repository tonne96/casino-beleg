package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
public class BetDescriptionCheck {

    public BetDescriptionCheck(MessageSource messages) {
        List<String> missing = Arrays.stream(RouletteBetType.values())
                .map(type -> "bet." + type.name() + ".description")
                .filter(key -> key.equals(messages.getMessage(key, null, key, Locale.GERMAN)))
                .toList();
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Fehlende Beschreibungstexte: " + missing);
        }
    }
}
