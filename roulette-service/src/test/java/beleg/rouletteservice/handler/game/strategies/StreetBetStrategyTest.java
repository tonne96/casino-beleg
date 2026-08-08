package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StreetBetStrategyTest {

	private StreetBetStrategy strategy;

	@BeforeEach
	void setUp() {
		strategy = new StreetBetStrategy();
	}

	@Test
	void getBetTypeReturnsStreet() {
		assertEquals(RouletteBetType.STREET, strategy.getBetType());
	}

	@Test
	void getPayoutMultiplierReturnsEleven() {
		assertEquals(11, strategy.getPayoutMultiplier());
	}

	@Test
	void validateNumbersReturnsSuccessForValidStart() {
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(34));

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
		IResult<Void, Failures> result = strategy.validateNumbers(List.of(35));

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
	void isWinningReturnsTrueForSecondNumber() {
		assertTrue(strategy.isWinning(List.of(1), 2));
	}

	@Test
	void isWinningReturnsTrueForThirdNumber() {
		assertTrue(strategy.isWinning(List.of(1), 3));
	}

	@Test
	void isWinningReturnsFalseForNumberBelowRange() {
		assertFalse(strategy.isWinning(List.of(4), 2));
	}

	@Test
	void isWinningReturnsFalseForNumberAboveRange() {
		assertFalse(strategy.isWinning(List.of(1), 4));
	}
}