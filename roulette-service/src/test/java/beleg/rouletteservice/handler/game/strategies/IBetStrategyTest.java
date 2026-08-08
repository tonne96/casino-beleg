package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.result.Success;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IBetStrategyTest {

	private IBetStrategy strategy;

	@BeforeEach
	void setUp() {
		strategy = mock(IBetStrategy.class, CALLS_REAL_METHODS);
	}

	@Test
	void validateReturnsNotNullFailureWhenBetNumbersAreNull() {
		IResult<Void, Failures> result = strategy.validate(null);
		Failures expectedFailure = Failures.NOT_NULL;

		assertFalse(result.isSuccess());
		assertSame(expectedFailure, result.getMessage());
		verify(strategy, never()).validateNumbers(any());
	}

	@Test
	void validateReturnsNotNullFailureWhenBetNumbersContainNull() {
		List<Integer> betNumbers = Arrays.asList(1, null);
		Failures expectedFailure = Failures.NOT_NULL;

		IResult<Void, Failures> result = strategy.validate(betNumbers);

		assertFalse(result.isSuccess());
		assertSame(expectedFailure, result.getMessage());
		verify(strategy, never()).validateNumbers(any());
	}

	@Test
	void validateDelegatesToValidateNumbersWhenBetNumbersAreValid() {
		List<Integer> betNumbers = List.of(1, 2);
		IResult<Void, Failures> expected = new Success<>(null);
		when(strategy.validateNumbers(betNumbers)).thenReturn(expected);

		IResult<Void, Failures> result = strategy.validate(betNumbers);

		assertSame(expected, result);
		verify(strategy).validateNumbers(betNumbers);
	}
}