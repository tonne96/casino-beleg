package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SplitBetStrategyTest {

	private SplitBetStrategy strategy;

	@BeforeEach
	void setUp() {
		strategy = new SplitBetStrategy();
	}

	@Test
	void getBetTypeReturnsSplit() {
		assertEquals(RouletteBetType.SPLIT, strategy.getBetType());
	}

	@Test
	void getPayoutMultiplierReturnsSeventeen() {
		assertEquals(17, strategy.getPayoutMultiplier());
	}

	@Test
	void validateNumbersReturnsSuccessForHorizontalNeighbours() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(1, 2));

		assertTrue(result.isSuccess());
	}

	@Test
	void validateNumbersReturnsSuccessForVerticalNeighbours() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(1, 4));

		assertTrue(result.isSuccess());
	}

	@Test
	void validateNumbersReturnsSuccessForZeroAndOne() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(0, 1));

		assertTrue(result.isSuccess());
	}

	@Test
	void validateNumbersReturnsSuccessForZeroAndTwo() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(0, 2));

		assertTrue(result.isSuccess());
	}

	@Test
	void validateNumbersReturnsSuccessForZeroAndThree() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(0, 3));

		assertTrue(result.isSuccess());
	}

	@Test
	void validateNumbersReturnsFailureWhenNumberCountIsBelowTwo() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(1));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsFailureWhenNumberCountIsAboveTwo() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(1, 2, 3));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsFailureWhenFirstNumberIsBelowZero() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(-1, 1));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsFailureWhenFirstNumberIsAboveThirtySix() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(37, 1));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsFailureWhenSecondNumberIsBelowZero() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(1, -1));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsFailureWhenSecondNumberIsAboveThirtySix() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(1, 37));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsFailureWhenNumbersAreEqual() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(5, 5));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsFailureWhenNumbersAreNotAdjacent() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(1, 5));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsFailureWhenZeroIsNotAdjacent() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(0, 4));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsSuccessForHorizontalNeighboursInReverseOrder() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(2, 1));

		assertTrue(result.isSuccess());
	}

	@Test
	void validateNumbersReturnsSuccessForVerticalNeighboursInReverseOrder() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(4, 1));

		assertTrue(result.isSuccess());
	}

	@Test
	void isWinningReturnsTrueWhenWinningNumberIsFirstBetNumber() {
		assertTrue(strategy.isWinning(List.of(17, 18), 17));
	}

	@Test
	void isWinningReturnsTrueWhenWinningNumberIsSecondBetNumber() {
		assertTrue(strategy.isWinning(List.of(17, 18), 18));
	}

	@Test
	void isWinningReturnsFalseWhenWinningNumberIsNotBetNumber() {
		assertFalse(strategy.isWinning(List.of(17, 18), 19));
	}

	@Test
	void validateNumbersReturnsSuccessForOneAndZero() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(1, 0));

		assertTrue(result.isSuccess());
	}

	@Test
	void validateNumbersReturnsSuccessForTwoAndZero() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(2, 0));

		assertTrue(result.isSuccess());
	}

	@Test
	void validateNumbersReturnsSuccessForThreeAndZero() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(3, 0));

		assertTrue(result.isSuccess());
	}

	@Test
	void validateNumbersReturnsFailureForZeroAndNonAdjacentNumber() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(4, 0));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsFailureForNumbersInDifferentRows() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(2, 4));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsFailureForNumbersInSameRowButMoreThanOneApart() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(1, 3));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}
}