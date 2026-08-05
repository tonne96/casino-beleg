package beleg.rouletteservice.handler.info;

import beleg.rouletteservice.handler.game.strategies.BetStrategyResolver;
import beleg.rouletteservice.handler.game.strategies.IBetStrategy;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class RouletteInfoHandlerImpl implements IRouletteInfoHandler {

    private final BetStrategyResolver betStrategyResolver;
    private final MessageSource messages;

    public RouletteInfoHandlerImpl(BetStrategyResolver betStrategyResolver, MessageSource messages) {
        this.betStrategyResolver = betStrategyResolver;
        this.messages = messages;
    }

    @Override
    public String getRules() {
        Locale locale = LocaleContextHolder.getLocale();
        StringBuilder text = new StringBuilder();

        text.append(msg("roulette.rules.header", locale)).append("\n\n");
        for (IBetStrategy strategy : betStrategyResolver.all()) {
            text.append("- ").append(describe(strategy, locale)).append("\n");
        }
        text.append("\n").append(msg("roulette.rules.footer", locale));

        return text.toString();
    }

    @Override
    public String getChances() {
        Locale locale = LocaleContextHolder.getLocale();
        StringBuilder text = new StringBuilder();

        text.append(msg("roulette.chances.header", locale)).append("\n\n");
        text.append(msg("roulette.chances.formula", locale)).append("\n\n");
        for (IBetStrategy strategy : betStrategyResolver.all()) {
            text.append(String.format("%-13s %d:1%n",
                    strategy.getBetType().name(), strategy.getPayoutMultiplier()));
        }
        return text.toString();
    }

    private String describe(IBetStrategy strategy, Locale locale) {
        String key = "bet." + strategy.getBetType().name() + ".description";
        return messages.getMessage(key, null, key, locale);
    }

    private String msg(String key, Locale locale) {
        return messages.getMessage(key, null, key, locale);
    }
}