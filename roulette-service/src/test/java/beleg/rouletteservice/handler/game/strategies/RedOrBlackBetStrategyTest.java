package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RedOrBlackBetStrategyTest {

	private RedOrBlackBetStrategy strategy;

	@BeforeEach
	void setUp() {
		strategy = new RedOrBlackBetStrategy();
	}

	@Test
	void getBetTypeReturnsRedOrBlack() {
		assertEquals(RouletteBetType.RED_OR_BLACK, strategy.getBetType());
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
	void isWinningReturnsTrueWhenRedWasChosenAndNumberIsRed() {
		assertTrue(strategy.isWinning(List.of(1), 1));
		assertTrue(strategy.isWinning(List.of(1), 36));
	}

	@Test
	void isWinningReturnsFalseWhenRedWasChosenAndNumberIsBlack() {
		assertFalse(strategy.isWinning(List.of(1), 2));
		assertFalse(strategy.isWinning(List.of(1), 35));
	}

	@Test
	void isWinningReturnsTrueWhenBlackWasChosenAndNumberIsBlack() {
		assertTrue(strategy.isWinning(List.of(0), 2));
		assertTrue(strategy.isWinning(List.of(0), 35));
	}

	@Test
	void isWinningReturnsFalseWhenBlackWasChosenAndNumberIsRed() {
		assertFalse(strategy.isWinning(List.of(0), 1));
		assertFalse(strategy.isWinning(List.of(0), 36));
	}
}