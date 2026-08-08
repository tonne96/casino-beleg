package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SingleBetStrategyTest {

	private SingleBetStrategy strategy;

	@BeforeEach
	void setUp() {
		strategy = new SingleBetStrategy();
	}

	@Test
	void getBetTypeReturnsSingle() {
		assertEquals(RouletteBetType.SINGLE, strategy.getBetType());
	}

	@Test
	void getPayoutMultiplierReturnsThirtyFive() {
		assertEquals(35, strategy.getPayoutMultiplier());
	}

	@Test
	void validateNumbersReturnsFailureWhenNumberCountIsNotOne() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(1, 2));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsSuccessForZero() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(0));

		assertTrue(result.isSuccess());
	}

	@Test
	void validateNumbersReturnsSuccessForThirtySix() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(36));

		assertTrue(result.isSuccess());
	}

	@Test
	void validateNumbersReturnsFailureForNegativeNumber() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(-1));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void validateNumbersReturnsFailureForNumberAboveThirtySix() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(37));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void isWinningReturnsTrueWhenNumbersMatch() {
		assertTrue(strategy.isWinning(List.of(17), 17));
	}

	@Test
	void isWinningReturnsFalseWhenNumbersDoNotMatch() {
		assertFalse(strategy.isWinning(List.of(17), 18));
	}
}