package beleg.rouletteservice.factory;

import beleg.rouletteservice.handler.game.strategies.BetStrategyResolver;
import beleg.rouletteservice.handler.game.strategies.IBetStrategy;
import beleg.rouletteservice.model.IRouletteGame;
import beleg.rouletteservice.model.RouletteGameResult;
import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.result.Success;
import beleg.rouletteservice.rules.RouletteBetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouletteGameFactoryImplTest {

	private BetStrategyResolver betStrategyResolver;
	private RouletteGameFactoryImpl rouletteGameFactory;

	@BeforeEach
	void setUp() {
		betStrategyResolver = mock(BetStrategyResolver.class);
		rouletteGameFactory = new RouletteGameFactoryImpl(betStrategyResolver);
	}

	@Test
	void createReturnsNotNullFailureIfResultIsNull() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		RouletteBetType betType = RouletteBetType.RED_OR_BLACK;
		List<Integer> betNumbers = List.of(1);
		RouletteGameResult result = null;
		Failures expectedFailure = Failures.NOT_NULL;

		IResult<IRouletteGame, Failures> actualFailure = rouletteGameFactory.create(userId, amount, betType, betNumbers, result);

		assertSame(expectedFailure, actualFailure.getMessage());
		assertFalse(actualFailure.isSuccess());
		verifyNoInteractions(betStrategyResolver);
	}

	@Test
	void createReturnsNotNullFailureIfBetTypeIsNull() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		RouletteBetType betType = null;
		List<Integer> betNumbers = List.of(1);
		RouletteGameResult result = mock(RouletteGameResult.class);
		Failures expectedFailure = Failures.NOT_NULL;

		IResult<IRouletteGame, Failures> actualFailure = rouletteGameFactory.create(userId, amount, betType, betNumbers, result);

		assertSame(expectedFailure, actualFailure.getMessage());
		assertFalse(actualFailure.isSuccess());
		verifyNoInteractions(betStrategyResolver);
	}

	@Test
	void createReturnsInvalidBetTypeIfStrategyIsEmpty() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		RouletteBetType betType = RouletteBetType.RED_OR_BLACK;
		List<Integer> betNumbers = List.of(1);
		RouletteGameResult result = mock(RouletteGameResult.class);
		when(betStrategyResolver.resolve(betType)).thenReturn(Optional.empty());
		Failures expectedFailure = Failures.INVALID_BET_TYPE;

		IResult<IRouletteGame, Failures> actualResult = rouletteGameFactory.create(userId, amount, betType, betNumbers, result);

		assertFalse(actualResult.isSuccess());
		assertSame(expectedFailure, actualResult.getMessage());
		verify(betStrategyResolver).resolve(betType);
	}

	@Test
	void createReturnsValidationFailureIfBetIsInvalid() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		RouletteBetType betType = RouletteBetType.RED_OR_BLACK;
		List<Integer> betNumbers = List.of(1);
		RouletteGameResult result = mock(RouletteGameResult.class);
		IBetStrategy strategy = mock(IBetStrategy.class);
		Failures expectedFailure = Failures.OUT_OF_RANGE;
		when(betStrategyResolver.resolve(betType)).thenReturn(Optional.of(strategy));
		when(strategy.validate(betNumbers)).thenReturn(new Failure<>(Failures.OUT_OF_RANGE));

		IResult<IRouletteGame, Failures> actualResult = rouletteGameFactory.create(userId, amount, betType, betNumbers, result);

		assertFalse(actualResult.isSuccess());
		assertSame(expectedFailure, actualResult.getMessage());
		verify(betStrategyResolver).resolve(betType);
		verify(strategy).validate(betNumbers);
	}

	@Test
	void createReturnsValidationFailureFromStrategy() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		RouletteBetType betType = RouletteBetType.RED_OR_BLACK;
		List<Integer> betNumbers = List.of(1);
		RouletteGameResult result = mock(RouletteGameResult.class);
		IBetStrategy strategy = mock(IBetStrategy.class);
		Failures expectedFailure = Failures.NOT_NULL;
		when(betStrategyResolver.resolve(betType)).thenReturn(Optional.of(strategy));
		when(strategy.validate(betNumbers)).thenReturn(new Failure<>(Failures.NOT_NULL));

		IResult<IRouletteGame, Failures> actualResult = rouletteGameFactory.create(userId, amount, betType, betNumbers, result);

		assertFalse(actualResult.isSuccess());
		assertSame(expectedFailure, actualResult.getMessage());
		verify(strategy).validate(betNumbers);
	}

	@Test
	void createReturnsSuccessIfValidationSucceeds() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		RouletteBetType betType = RouletteBetType.RED_OR_BLACK;
		List<Integer> betNumbers = List.of(1);
		RouletteGameResult result = mock(RouletteGameResult.class);
		IBetStrategy strategy = mock(IBetStrategy.class);
		when(betStrategyResolver.resolve(betType)).thenReturn(Optional.of(strategy));
		when(strategy.validate(betNumbers)).thenReturn(new Success<>(null));
		when(result.winning()).thenReturn(true);
		when(result.amount()).thenReturn(BigDecimal.valueOf(20));
		when(result.winningNumber()).thenReturn(1);
		when(result.payoutMultiplier()).thenReturn(2);

		IResult<IRouletteGame, Failures> actualResult = rouletteGameFactory.create(userId, amount, betType, betNumbers, result);

		assertTrue(actualResult.isSuccess());
		assertNotNull(actualResult.getValue());
		verify(betStrategyResolver).resolve(betType);
		verify(strategy).validate(betNumbers);
	}

	@Test
	void createReturnsCreationFailureIfGameCreationFails() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		RouletteBetType betType = RouletteBetType.RED_OR_BLACK;
		List<Integer> betNumbers = List.of(1);
		RouletteGameResult result = mock(RouletteGameResult.class);
		IBetStrategy strategy = mock(IBetStrategy.class);
		Failures expectedFailure = Failures.OUT_OF_RANGE;
		when(betStrategyResolver.resolve(betType)).thenReturn(Optional.of(strategy));
		when(strategy.validate(betNumbers)).thenReturn(new Success<>(null));
		when(result.winning()).thenReturn(true);
		when(result.amount()).thenReturn(BigDecimal.valueOf(20));
		when(result.winningNumber()).thenReturn(37);
		when(result.payoutMultiplier()).thenReturn(2);

		IResult<IRouletteGame, Failures> actualResult = rouletteGameFactory.create(userId, amount, betType, betNumbers, result);

		assertFalse(actualResult.isSuccess());
		assertSame(expectedFailure, actualResult.getMessage());
		assertNull(actualResult.getValue());
		verify(strategy).validate(betNumbers);
	}
}