package beleg.rouletteservice.handler.info;

import beleg.rouletteservice.handler.game.strategies.BetStrategyResolver;
import beleg.rouletteservice.handler.game.strategies.IBetStrategy;
import beleg.rouletteservice.rules.RouletteBetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouletteInfoHandlerImplTest {

	private BetStrategyResolver betStrategyResolver;
	private MessageSource messages;
	private RouletteInfoHandlerImpl handler;

	// Hilfsmethode
	private IBetStrategy strategy(RouletteBetType betType, int payoutMultiplier) {
		IBetStrategy strategy = mock(IBetStrategy.class);
		when(strategy.getBetType()).thenReturn(betType);
		when(strategy.getPayoutMultiplier()).thenReturn(payoutMultiplier);
		return strategy;
	}

	@BeforeEach
	void setUp() {
		betStrategyResolver = mock(BetStrategyResolver.class);
		messages = mock(MessageSource.class);
		handler = new RouletteInfoHandlerImpl(betStrategyResolver, messages);
		LocaleContextHolder.setLocale(Locale.GERMAN);
	}

	@Test
	void getRulesReturnsHeaderDescriptionsAndFooter() {
		IBetStrategy redOrBlack = strategy(RouletteBetType.RED_OR_BLACK, 1);
		IBetStrategy single = strategy(RouletteBetType.SINGLE, 35);
		when(betStrategyResolver.all()).thenReturn(List.of(redOrBlack, single));
		when(messages.getMessage("roulette.rules.header", null, "roulette.rules.header", Locale.GERMAN)).thenReturn("Roulette Regeln");
		when(messages.getMessage("roulette.rules.footer", null, "roulette.rules.footer", Locale.GERMAN)).thenReturn("Viel Erfolg!");
		when(messages.getMessage("bet.RED_OR_BLACK.description", null, "bet.RED_OR_BLACK.description", Locale.GERMAN)).thenReturn("Rot oder Schwarz");
		when(messages.getMessage("bet.SINGLE.description", null, "bet.SINGLE.description", Locale.GERMAN)).thenReturn("Einzelne Zahl");

		String result = handler.getRules();

		assertEquals("Roulette Regeln\n\n- Rot oder Schwarz\n- Einzelne Zahl\n\nViel Erfolg!", result);
	}

	@Test
	void getRulesReturnsFallbackKeysWhenMessagesAreMissing() {
		IBetStrategy single = strategy(RouletteBetType.SINGLE, 35);
		when(betStrategyResolver.all()).thenReturn(List.of(single));
		when(messages.getMessage("roulette.rules.header", null, "roulette.rules.header", Locale.GERMAN)).thenReturn("roulette.rules.header");
		when(messages.getMessage("bet.SINGLE.description", null, "bet.SINGLE.description", Locale.GERMAN)).thenReturn("bet.SINGLE.description");
		when(messages.getMessage("roulette.rules.footer", null, "roulette.rules.footer", Locale.GERMAN)).thenReturn("roulette.rules.footer");

		String result = handler.getRules();

		assertEquals("roulette.rules.header\n\n- bet.SINGLE.description\n\nroulette.rules.footer", result);
	}

	@Test
	void getChancesReturnsHeaderFormulaAndMultipliers() {
		IBetStrategy redOrBlack = strategy(RouletteBetType.RED_OR_BLACK, 1);
		IBetStrategy single = strategy(RouletteBetType.SINGLE, 35);
		when(betStrategyResolver.all()).thenReturn(List.of(redOrBlack, single));
		when(messages.getMessage("roulette.chances.header", null, "roulette.chances.header", Locale.GERMAN)).thenReturn("Gewinnchancen");
		when(messages.getMessage("roulette.chances.formula", null, "roulette.chances.formula", Locale.GERMAN)).thenReturn("Auszahlung");

		String result = handler.getChances();

		assertEquals("Gewinnchancen\n\nAuszahlung\n\nRED_OR_BLACK  1:1\nSINGLE        35:1\n", result);
	}

	@Test
	void getChancesReturnsFallbackKeysWhenMessagesAreMissing() {
		IBetStrategy single = strategy(RouletteBetType.SINGLE, 35);
		when(betStrategyResolver.all()).thenReturn(List.of(single));
		when(messages.getMessage("roulette.chances.header", null, "roulette.chances.header", Locale.GERMAN)).thenReturn("roulette.chances.header");
		when(messages.getMessage("roulette.chances.formula", null, "roulette.chances.formula", Locale.GERMAN)).thenReturn("roulette.chances.formula");

		String result = handler.getChances();

		assertEquals("roulette.chances.header\n\nroulette.chances.formula\n\nSINGLE        35:1\n", result);
	}
}