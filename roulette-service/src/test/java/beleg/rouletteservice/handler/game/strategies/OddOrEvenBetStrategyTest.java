package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OddOrEvenBetStrategyTest {

	private OddOrEvenBetStrategy strategy;

	@BeforeEach
	void setUp() {
		strategy = new OddOrEvenBetStrategy();
	}

	@Test
	void getBetTypeReturnsOddOrEven() {
		assertEquals(RouletteBetType.ODD_OR_EVEN, strategy.getBetType());
	}

	@Test
	void getPayoutMultiplierReturnsOne() {
		assertEquals(1, strategy.getPayoutMultiplier());
	}

	@Test
	void validateNumbersReturnsSuccessForValidChoices() {
		assertTrue(strategy.validateNumbers(List.of(0)).isSuccess());
		assertTrue(strategy.validateNumbers(List.of(1)).isSuccess());
	}

	@Test
	void validateNumbersReturnsFailureForInvalidChoice() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(2));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void isWinningReturnsFalseForZero() {
		assertFalse(strategy.isWinning(List.of(0), 0));
		assertFalse(strategy.isWinning(List.of(1), 0));
	}

	@Test
	void isWinningReturnsTrueWhenEvenWasChosenAndNumberIsEven() {
		assertTrue(strategy.isWinning(List.of(0), 2));
		assertTrue(strategy.isWinning(List.of(0), 36));
	}

	@Test
	void isWinningReturnsFalseWhenEvenWasChosenAndNumberIsOdd() {
		assertFalse(strategy.isWinning(List.of(0), 1));
		assertFalse(strategy.isWinning(List.of(0), 35));
	}

	@Test
	void isWinningReturnsTrueWhenOddWasChosenAndNumberIsOdd() {
		assertTrue(strategy.isWinning(List.of(1), 1));
		assertTrue(strategy.isWinning(List.of(1), 35));
	}

	@Test
	void isWinningReturnsFalseWhenOddWasChosenAndNumberIsEven() {
		assertFalse(strategy.isWinning(List.of(1), 2));
		assertFalse(strategy.isWinning(List.of(1), 36));
	}
}