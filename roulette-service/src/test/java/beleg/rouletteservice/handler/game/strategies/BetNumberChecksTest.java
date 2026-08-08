package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BetNumberChecksTest {

	@Test
	void binaryChoiceAcceptsZero() {
		IResult<Void, Failures> result = BetNumberChecks.binaryChoice(List.of(0));

		assertTrue(result.isSuccess());
	}

	@Test
	void binaryChoiceAcceptsOne() {
		IResult<Void, Failures> result = BetNumberChecks.binaryChoice(List.of(1));

		assertTrue(result.isSuccess());
	}

	@Test
	void binaryChoiceRejectsInvalidValue() {
		IResult<Void, Failures> result = BetNumberChecks.binaryChoice(List.of(2));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void binaryChoiceRejectsWrongNumberOfValues() {
		IResult<Void, Failures> result = BetNumberChecks.binaryChoice(List.of(0, 1));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void oneToThreeAcceptsOne() {
		IResult<Void, Failures> result = BetNumberChecks.oneToThree(List.of(1));

		assertTrue(result.isSuccess());
	}

	@Test
	void oneToThreeAcceptsThree() {
		IResult<Void, Failures> result = BetNumberChecks.oneToThree(List.of(3));

		assertTrue(result.isSuccess());
	}

	@Test
	void oneToThreeRejectsInvalidValue() {
		IResult<Void, Failures> result = BetNumberChecks.oneToThree(List.of(4));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void oneToThreeRejectsWrongNumberOfValues() {
		IResult<Void, Failures> result = BetNumberChecks.oneToThree(List.of(1, 2));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void rowStartAcceptsValidValue() {
		IResult<Void, Failures> result = BetNumberChecks.rowStart(List.of(4), 34);

		assertTrue(result.isSuccess());
	}

	@Test
	void rowStartAcceptsMaximumValidValue() {
		IResult<Void, Failures> result = BetNumberChecks.rowStart(List.of(34), 34);

		assertTrue(result.isSuccess());
	}

	@Test
	void rowStartRejectsInvalidStart() {
		IResult<Void, Failures> result = BetNumberChecks.rowStart(List.of(5), 34);

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void rowStartRejectsValueAboveMaximum() {
		IResult<Void, Failures> result = BetNumberChecks.rowStart(List.of(37), 34);

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void rowStartRejectsWrongNumberOfValues() {
		IResult<Void, Failures> result = BetNumberChecks.rowStart(List.of(1, 4), 34);

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void oneToThreeReturnsOkForBoundaryChoice() {
		IResult<Void, Failures> result = BetNumberChecks.oneToThree(List.of(1));

		assertTrue(result.isSuccess());
	}

	@Test
	void oneToThreeReturnsOutOfRangeForZero() {
		IResult<Void, Failures> result = BetNumberChecks.oneToThree(List.of(0));

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void rowStartReturnsOutOfRangeWhenBelowMinimum() {
		IResult<Void, Failures> result = BetNumberChecks.rowStart(List.of(0), 34);

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}

	@Test
	void rowStartReturnsOutOfRangeWhenAboveMaximum() {
		IResult<Void, Failures> result = BetNumberChecks.rowStart(List.of(34 + 1), 34);

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
	}
}