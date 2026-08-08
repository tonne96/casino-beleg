package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BetStrategyResolverTest {

	// Hilfsmethode
	private static List<IBetStrategy> allStrategies() {
		return List.of(
				strategy(RouletteBetType.SINGLE),
				strategy(RouletteBetType.RED_OR_BLACK),
				strategy(RouletteBetType.ODD_OR_EVEN),
				strategy(RouletteBetType.LOW_OR_HIGH),
				strategy(RouletteBetType.DOZEN),
				strategy(RouletteBetType.COLUMN),
				strategy(RouletteBetType.SIX_LINE),
				strategy(RouletteBetType.CORNER),
				strategy(RouletteBetType.STREET),
				strategy(RouletteBetType.SPLIT)
		);
	}

	@Test
	void constructorCreatesResolverWhenAllStrategiesExist() {
		List<IBetStrategy> strategies = allStrategies();

		assertDoesNotThrow(() -> new BetStrategyResolver(strategies));
	}

	@Test
	void constructorThrowsExceptionWhenStrategyIsMissing() {
		List<IBetStrategy> strategies = List.of(strategy(RouletteBetType.SINGLE));

		assertThrows(IllegalStateException.class, () -> new BetStrategyResolver(strategies));
	}

	@Test
	void resolveReturnsStrategyForKnownBetType() {
		List<IBetStrategy> strategies = allStrategies();
		IBetStrategy singleStrategy = strategies.stream()
				.filter(strategy -> strategy.getBetType() == RouletteBetType.SINGLE)
				.findFirst()
				.orElseThrow();
		BetStrategyResolver resolver = new BetStrategyResolver(strategies);

		Optional<IBetStrategy> result = resolver.resolve(RouletteBetType.SINGLE);

		assertTrue(result.isPresent());
		assertSame(singleStrategy, result.get());
	}

	@Test
	void resolveReturnsEmptyForUnknownBetType() {
		BetStrategyResolver resolver = new BetStrategyResolver(allStrategies());

		Optional<IBetStrategy> result = resolver.resolve(null);

		assertTrue(result.isEmpty());
	}

	@Test
	void allReturnsStrategiesSortedByBetType() {
		BetStrategyResolver resolver = new BetStrategyResolver(allStrategies());

		List<IBetStrategy> result = resolver.all();

		assertEquals(RouletteBetType.values().length, result.size());
		assertEquals(
				result.stream()
						.map(IBetStrategy::getBetType)
						.sorted()
						.toList(),
				result.stream()
						.map(IBetStrategy::getBetType)
						.toList()
		);
	}

	private static IBetStrategy strategy(RouletteBetType betType) {
		IBetStrategy strategy = mock(IBetStrategy.class);
		when(strategy.getBetType()).thenReturn(betType);
		return strategy;
	}
}