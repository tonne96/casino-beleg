package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ColumnBetStrategyTest {

	private ColumnBetStrategy strategy;

	@BeforeEach
	void setUp() {
		strategy = new ColumnBetStrategy();
	}

	@Test
	void getBetTypeReturnsColumn() {
		assertEquals(RouletteBetType.COLUMN, strategy.getBetType());
	}

	@Test
	void getPayoutMultiplierReturnsTwo() {
		assertEquals(2, strategy.getPayoutMultiplier());
	}

	@Test
	void validateNumbersReturnsSuccessForValidColumn() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(1));

		assertTrue(result.isSuccess());
	}

	@Test
	void validateNumbersReturnsFailureForInvalidColumn() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(4));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void isWinningReturnsFalseForZero() {
		assertFalse(strategy.isWinning(List.of(1), 0));
	}

	@Test
	void isWinningReturnsTrueWhenWinningNumberIsInSelectedColumn() {
		assertTrue(strategy.isWinning(List.of(1), 4));
	}

	@Test
	void isWinningReturnsFalseWhenWinningNumberIsInDifferentColumn() {
		assertFalse(strategy.isWinning(List.of(1), 5));
	}
}