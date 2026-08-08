package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SixLineBetStrategyTest {

	private SixLineBetStrategy strategy;

	@BeforeEach
	void setUp() {
		strategy = new SixLineBetStrategy();
	}

	@Test
	void getBetTypeReturnsSixLine() {
		assertEquals(RouletteBetType.SIX_LINE, strategy.getBetType());
	}

	@Test
	void getPayoutMultiplierReturnsFive() {
		assertEquals(5, strategy.getPayoutMultiplier());
	}

	@Test
	void validateNumbersReturnsSuccessForValidStart() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(31));

		assertTrue(result.isSuccess());
	}

	@Test
	void validateNumbersReturnsFailureWhenNumberCountIsNotOne() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(1, 4));

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
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(32));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsFailureWhenStartIsNotInLeftColumn() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(2));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void isWinningReturnsTrueForFirstNumber() {
		assertTrue(strategy.isWinning(List.of(1), 1));
	}

	@Test
	void isWinningReturnsTrueForLastNumber() {
		assertTrue(strategy.isWinning(List.of(1), 6));
	}

	@Test
	void isWinningReturnsFalseForNumberBelowRange() {
		assertFalse(strategy.isWinning(List.of(1), 0));
	}

	@Test
	void isWinningReturnsFalseForNumberAboveRange() {
		assertFalse(strategy.isWinning(List.of(1), 7));
	}
}