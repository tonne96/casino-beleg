package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DozenBetStrategyTest {

	private DozenBetStrategy strategy;

	@BeforeEach
	void setUp() {
		strategy = new DozenBetStrategy();
	}

	@Test
	void getBetTypeReturnsDozen() {
		assertEquals(RouletteBetType.DOZEN, strategy.getBetType());
	}

	@Test
	void getPayoutMultiplierReturnsTwo() {
		assertEquals(2, strategy.getPayoutMultiplier());
	}

	@Test
	void validateNumbersReturnsSuccessForValidDozen() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(2));

		assertTrue(result.isSuccess());
	}

	@Test
	void validateNumbersReturnsFailureForInvalidDozen() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(4));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void isWinningReturnsFalseForZero() {
		assertFalse(strategy.isWinning(List.of(1), 0));
	}

	@Test
	void isWinningReturnsTrueForFirstDozen() {
		assertTrue(strategy.isWinning(List.of(1), 1));
		assertTrue(strategy.isWinning(List.of(1), 12));
	}

	@Test
	void isWinningReturnsTrueForSecondDozen() {
		assertTrue(strategy.isWinning(List.of(2), 13));
		assertTrue(strategy.isWinning(List.of(2), 24));
	}

	@Test
	void isWinningReturnsTrueForThirdDozen() {
		assertTrue(strategy.isWinning(List.of(3), 25));
		assertTrue(strategy.isWinning(List.of(3), 36));
	}

	@Test
	void isWinningReturnsFalseForWrongDozen() {
		assertFalse(strategy.isWinning(List.of(1), 13));
	}
}