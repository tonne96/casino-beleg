package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CornerBetStrategyTest {

	private CornerBetStrategy strategy;

	@BeforeEach
	void setUp() {
		strategy = new CornerBetStrategy();
	}

	@Test
	void getBetTypeReturnsCorner() {
		assertEquals(RouletteBetType.CORNER, strategy.getBetType());
	}

	@Test
	void getPayoutMultiplierReturnsEight() {
		assertEquals(8, strategy.getPayoutMultiplier());
	}

	@Test
	void validateNumbersReturnsSuccessForValidCorner() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(1));

		assertTrue(result.isSuccess());
	}

	@Test
	void validateNumbersReturnsFailureWhenNumberCountIsNotOne() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(1, 2));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsFailureWhenStartIsBelowOne() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(0));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsFailureWhenStartIsAboveMaximum() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(33));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsFailureWhenStartIsInRightColumn() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(3));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void isWinningReturnsTrueForAllFourCornerNumbers() {
		assertTrue(strategy.isWinning(List.of(1), 1));
		assertTrue(strategy.isWinning(List.of(1), 2));
		assertTrue(strategy.isWinning(List.of(1), 4));
		assertTrue(strategy.isWinning(List.of(1), 5));
	}

	@Test
	void isWinningReturnsFalseForNumberOutsideCorner() {
		assertFalse(strategy.isWinning(List.of(1), 3));
	}
}