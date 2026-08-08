package beleg.rouletteservice.handler.game;

import beleg.rouletteservice.client.banking.BankingUserDto;
import beleg.rouletteservice.client.banking.IBankingClient;
import beleg.rouletteservice.factory.IRouletteGameFactory;
import beleg.rouletteservice.handler.game.strategies.BetStrategyResolver;
import beleg.rouletteservice.handler.game.strategies.IBetStrategy;
import beleg.rouletteservice.handler.game.wheel.IRouletteWheel;
import beleg.rouletteservice.model.RouletteGameImpl;
import beleg.rouletteservice.model.RouletteGameResult;
import beleg.rouletteservice.repository.IRouletteGameRepository;
import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.result.Success;
import beleg.rouletteservice.rules.RouletteBetType;
import beleg.rouletteservice.view.request.PlayRequestDto;
import beleg.rouletteservice.view.response.PlayResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouletteGameHandlerImplTest {

	private IBankingClient bankingClient;
	private BetStrategyResolver betStrategyResolver;
	private IRouletteWheel rouletteWheel;
	private IRouletteGameFactory rouletteGameFactory;
	private IRouletteGameRepository repository;
	private RouletteGameHandlerImpl handler;

	@BeforeEach
	void setUp() {
		bankingClient = mock(IBankingClient.class);
		betStrategyResolver = mock(BetStrategyResolver.class);
		rouletteWheel = mock(IRouletteWheel.class);
		rouletteGameFactory = mock(IRouletteGameFactory.class);
		repository = mock(IRouletteGameRepository.class);

		handler = new RouletteGameHandlerImpl(bankingClient, betStrategyResolver, rouletteWheel, rouletteGameFactory, repository);
	}

	@Test
	void playReturnsNotNullFailureIfRequestIsNull() {
		IResult<PlayResponseDto, Failures> result = handler.play(null);

		assertFalse(result.isSuccess());
		assertSame(Failures.NOT_NULL, result.getMessage());
		verifyNoInteractions(bankingClient, betStrategyResolver, rouletteWheel, rouletteGameFactory, repository);
	}

	@Test
	void playReturnsNotNullFailureIfUserIsNull() {
		PlayRequestDto request = new PlayRequestDto(null, BigDecimal.TEN, RouletteBetType.SINGLE, List.of(17));

		IResult<PlayResponseDto, Failures> result = handler.play(request);

		assertFalse(result.isSuccess());
		assertSame(Failures.NOT_NULL, result.getMessage());
		verifyNoInteractions(bankingClient, betStrategyResolver, rouletteWheel, rouletteGameFactory, repository);
	}

	@Test
	void playReturnsNotNullFailureIfBetAmountIsNull() {
		PlayRequestDto request = new PlayRequestDto(1L, null, RouletteBetType.SINGLE, List.of(17));

		IResult<PlayResponseDto, Failures> result = handler.play(request);

		assertFalse(result.isSuccess());
		assertSame(Failures.NOT_NULL, result.getMessage());
		verifyNoInteractions(bankingClient, betStrategyResolver, rouletteWheel, rouletteGameFactory, repository);
	}

	@Test
	void playReturnsNotNullFailureIfBetTypeIsNull() {
		PlayRequestDto request = new PlayRequestDto(1L, BigDecimal.TEN, null, List.of(17));

		IResult<PlayResponseDto, Failures> result = handler.play(request);

		assertFalse(result.isSuccess());
		assertSame(Failures.NOT_NULL, result.getMessage());
		verifyNoInteractions(bankingClient, betStrategyResolver, rouletteWheel, rouletteGameFactory, repository);
	}

	@Test
	void playReturnsNotNullFailureIfBetNumbersAreNull() {
		PlayRequestDto request = new PlayRequestDto(1L, BigDecimal.TEN, RouletteBetType.SINGLE, null);

		IResult<PlayResponseDto, Failures> result = handler.play(request);

		assertFalse(result.isSuccess());
		assertSame(Failures.NOT_NULL, result.getMessage());
		verifyNoInteractions(bankingClient, betStrategyResolver, rouletteWheel, rouletteGameFactory, repository);
	}

	@Test
	void playReturnsInvalidBetTypeIfNoStrategyExists() {
		PlayRequestDto request = new PlayRequestDto(1L, BigDecimal.TEN, RouletteBetType.SINGLE, List.of(17));
		when(betStrategyResolver.resolve(RouletteBetType.SINGLE)).thenReturn(Optional.empty());

		IResult<PlayResponseDto, Failures> result = handler.play(request);

		assertFalse(result.isSuccess());
		assertSame(Failures.INVALID_BET_TYPE, result.getMessage());
		verify(betStrategyResolver).resolve(RouletteBetType.SINGLE);
		verifyNoInteractions(bankingClient, rouletteWheel, rouletteGameFactory, repository);
	}

	@Test
	void playReturnsBiggerZeroFailureIfBetAmountIsZero() {
		IBetStrategy strategy = mock(IBetStrategy.class);
		PlayRequestDto request = new PlayRequestDto(1L, BigDecimal.ZERO, RouletteBetType.SINGLE, List.of(17));
		when(betStrategyResolver.resolve(RouletteBetType.SINGLE)).thenReturn(Optional.of(strategy));

		IResult<PlayResponseDto, Failures> result = handler.play(request);

		assertFalse(result.isSuccess());
		assertSame(Failures.BIGGER_ZERO, result.getMessage());
		verify(strategy, never()).validate(any());
		verifyNoInteractions(bankingClient, rouletteWheel, rouletteGameFactory, repository);
	}

	@Test
	void playReturnsBiggerZeroFailureIfBetAmountIsNegative() {
		IBetStrategy strategy = mock(IBetStrategy.class);
		PlayRequestDto request = new PlayRequestDto(1L, BigDecimal.valueOf(-10), RouletteBetType.SINGLE, List.of(17));
		when(betStrategyResolver.resolve(RouletteBetType.SINGLE)).thenReturn(Optional.of(strategy));

		IResult<PlayResponseDto, Failures> result = handler.play(request);

		assertFalse(result.isSuccess());
		assertSame(Failures.BIGGER_ZERO, result.getMessage());
		verify(strategy, never()).validate(any());
		verifyNoInteractions(bankingClient, rouletteWheel, rouletteGameFactory, repository);
	}

	@Test
	void playReturnsValidationFailureIfStrategyRejectsBetNumbers() {
		IBetStrategy strategy = mock(IBetStrategy.class);
		PlayRequestDto request = new PlayRequestDto(1L, BigDecimal.TEN, RouletteBetType.SINGLE, List.of(99));
		when(betStrategyResolver.resolve(RouletteBetType.SINGLE)).thenReturn(Optional.of(strategy));
		when(strategy.validate(request.betNumbers())).thenReturn(new Failure<>(Failures.OUT_OF_RANGE));

		IResult<PlayResponseDto, Failures> result = handler.play(request);

		assertFalse(result.isSuccess());
		assertSame(Failures.OUT_OF_RANGE, result.getMessage());
		verify(strategy).validate(request.betNumbers());
		verifyNoInteractions(bankingClient, rouletteWheel, rouletteGameFactory, repository);
	}

	@Test
	void playReturnsBankingFailureIfUserCannotBeLoaded() {
		IBetStrategy strategy = mock(IBetStrategy.class);
		PlayRequestDto request = new PlayRequestDto(1L, BigDecimal.TEN, RouletteBetType.SINGLE, List.of(17));
		when(betStrategyResolver.resolve(RouletteBetType.SINGLE)).thenReturn(Optional.of(strategy));
		when(strategy.validate(request.betNumbers())).thenReturn(new Success<>(null));
		when(bankingClient.getUser(1L)).thenReturn(new Failure<>(Failures.USER_NOT_FOUND));

		IResult<PlayResponseDto, Failures> result = handler.play(request);

		assertFalse(result.isSuccess());
		assertSame(Failures.USER_NOT_FOUND, result.getMessage());
		verify(bankingClient).getUser(1L);
		verifyNoInteractions(rouletteWheel, rouletteGameFactory, repository);
	}

	@Test
	void playReturnsInsufficientBalanceIfUserBalanceIsTooLow() {
		IBetStrategy strategy = mock(IBetStrategy.class);
		PlayRequestDto request = new PlayRequestDto(1L, BigDecimal.TEN, RouletteBetType.SINGLE, List.of(17));
		BankingUserDto user = mock(BankingUserDto.class);
		when(betStrategyResolver.resolve(RouletteBetType.SINGLE)).thenReturn(Optional.of(strategy));
		when(strategy.validate(request.betNumbers())).thenReturn(new Success<>(null));
		when(bankingClient.getUser(1L)).thenReturn(new Success<>(user));
		when(user.balance()).thenReturn(BigDecimal.valueOf(5));

		IResult<PlayResponseDto, Failures> result = handler.play(request);

		assertFalse(result.isSuccess());
		assertSame(Failures.INSUFFICIENT_BALANCE, result.getMessage());
		verify(bankingClient).getUser(1L);
		verifyNoInteractions(rouletteWheel, rouletteGameFactory, repository);
	}

	@Test
	void playReturnsInconsistentRoundResultIfGameResultCreationFails() {
		IBetStrategy strategy = mock(IBetStrategy.class);
		PlayRequestDto request = new PlayRequestDto(1L, BigDecimal.TEN, RouletteBetType.SINGLE, List.of(17));
		BankingUserDto user = mock(BankingUserDto.class);
		when(betStrategyResolver.resolve(RouletteBetType.SINGLE)).thenReturn(Optional.of(strategy));
		when(strategy.validate(request.betNumbers())).thenReturn(new Success<>(null));
		when(bankingClient.getUser(1L)).thenReturn(new Success<>(user));
		when(user.balance()).thenReturn(BigDecimal.valueOf(100));
		when(rouletteWheel.spin()).thenReturn(17);
		when(strategy.isWinning(request.betNumbers(), 17)).thenReturn(true);
		when(strategy.getPayoutMultiplier()).thenReturn(0);

		IResult<PlayResponseDto, Failures> result = handler.play(request);

		assertFalse(result.isSuccess());
		assertSame(Failures.INCONSISTENT_ROUND_RESULT, result.getMessage());
		verify(rouletteWheel).spin();
		verify(strategy).isWinning(request.betNumbers(), 17);
		verify(strategy).getPayoutMultiplier();
		verifyNoInteractions(rouletteGameFactory, repository);
		verify(bankingClient, never()).bookTransaction(anyLong(), any());
	}

	@Test
	void playReturnsFactoryFailureIfGameCreationFails() {
		IBetStrategy strategy = mock(IBetStrategy.class);
		RouletteGameResult gameResult = new RouletteGameResult(true, BigDecimal.valueOf(350), 17, 35);
		PlayRequestDto request = new PlayRequestDto(1L, BigDecimal.TEN, RouletteBetType.SINGLE, List.of(17));
		BankingUserDto user = mock(BankingUserDto.class);
		when(betStrategyResolver.resolve(RouletteBetType.SINGLE)).thenReturn(Optional.of(strategy));
		when(strategy.validate(request.betNumbers())).thenReturn(new Success<>(null));
		when(bankingClient.getUser(1L)).thenReturn(new Success<>(user));
		when(user.balance()).thenReturn(BigDecimal.valueOf(100));
		when(rouletteWheel.spin()).thenReturn(17);
		when(strategy.isWinning(request.betNumbers(), 17)).thenReturn(true);
		when(strategy.getPayoutMultiplier()).thenReturn(35);
		when(rouletteGameFactory.create(1L, BigDecimal.TEN, RouletteBetType.SINGLE, List.of(17), gameResult)).thenReturn(new Failure<>(Failures.INTERNAL_ERROR));

		IResult<PlayResponseDto, Failures> result = handler.play(request);

		assertFalse(result.isSuccess());
		assertSame(Failures.INTERNAL_ERROR, result.getMessage());
		verify(rouletteGameFactory).create(1L, BigDecimal.TEN, RouletteBetType.SINGLE, List.of(17), gameResult);
		verify(bankingClient, never()).bookTransaction(anyLong(), any());
		verifyNoInteractions(repository);
	}

	@Test
	void playReturnsBankingFailureIfTransactionCannotBeBooked() {
		IBetStrategy strategy = mock(IBetStrategy.class);
		RouletteGameResult gameResult = new RouletteGameResult(true, BigDecimal.valueOf(350), 17, 35);
		PlayRequestDto request = new PlayRequestDto(1L, BigDecimal.TEN, RouletteBetType.SINGLE, List.of(17));
		RouletteGameImpl game = mock(RouletteGameImpl.class);
		BankingUserDto user = mock(BankingUserDto.class);
		when(betStrategyResolver.resolve(RouletteBetType.SINGLE)).thenReturn(Optional.of(strategy));
		when(strategy.validate(request.betNumbers())).thenReturn(new Success<>(null));
		when(bankingClient.getUser(1L)).thenReturn(new Success<>(user));
		when(user.balance()).thenReturn(BigDecimal.valueOf(100));
		when(rouletteWheel.spin()).thenReturn(17);
		when(strategy.isWinning(request.betNumbers(), 17)).thenReturn(true);
		when(strategy.getPayoutMultiplier()).thenReturn(35);
		when(rouletteGameFactory.create(1L, BigDecimal.TEN, RouletteBetType.SINGLE, List.of(17), gameResult)).thenReturn(new Success<>(game));
		when(bankingClient.bookTransaction(1L, BigDecimal.valueOf(350))).thenReturn(new Failure<>(Failures.BANKING_SERVICE_UNAVAILABLE));

		IResult<PlayResponseDto, Failures> result = handler.play(request);

		assertFalse(result.isSuccess());
		assertSame(Failures.BANKING_SERVICE_UNAVAILABLE, result.getMessage());
		verify(bankingClient).bookTransaction(1L, BigDecimal.valueOf(350));
		verifyNoInteractions(repository);
	}

	@Test
	void playReturnsWinningResponseWhenBetWins() {
		IBetStrategy strategy = mock(IBetStrategy.class);
		RouletteGameResult gameResult = new RouletteGameResult(true, BigDecimal.valueOf(350), 17, 35);
		RouletteGameImpl game = mock(RouletteGameImpl.class);
		BankingUserDto user = mock(BankingUserDto.class);
		PlayRequestDto request = new PlayRequestDto(1L, BigDecimal.TEN, RouletteBetType.SINGLE, List.of(17));
		when(betStrategyResolver.resolve(RouletteBetType.SINGLE)).thenReturn(Optional.of(strategy));
		when(strategy.validate(request.betNumbers())).thenReturn(new Success<>(null));
		when(bankingClient.getUser(1L)).thenReturn(new Success<>(user));
		when(user.balance()).thenReturn(BigDecimal.valueOf(100));
		when(rouletteWheel.spin()).thenReturn(17);
		when(strategy.isWinning(request.betNumbers(), 17)).thenReturn(true);
		when(strategy.getPayoutMultiplier()).thenReturn(35);
		when(rouletteGameFactory.create(1L, BigDecimal.TEN, RouletteBetType.SINGLE, List.of(17), gameResult)).thenReturn(new Success<>(game));
		when(bankingClient.bookTransaction(1L, BigDecimal.valueOf(350))).thenReturn(new Success<>(null));
		when(repository.save(game)).thenReturn(game);
		when(game.getUserId()).thenReturn(1L);
		when(game.isWinning()).thenReturn(true);
		when(game.getAmount()).thenReturn(BigDecimal.valueOf(350));
		when(game.getWinningNumber()).thenReturn(17);
		when(game.getBetType()).thenReturn(RouletteBetType.SINGLE);
		when(game.getBetNumbers()).thenReturn(List.of(17));
		when(game.getBetAmount()).thenReturn(BigDecimal.TEN);
		when(game.getPayoutMultiplier()).thenReturn(35);

		IResult<PlayResponseDto, Failures> result = handler.play(request);

		assertTrue(result.isSuccess());
		assertNotNull(result.getValue());
		assertEquals(1L, result.getValue().user());
		assertTrue(result.getValue().winning());
		assertEquals(BigDecimal.valueOf(350), result.getValue().amount());
		assertEquals(17, result.getValue().ballPosition());
		assertEquals(RouletteBetType.SINGLE, result.getValue().betType());
		assertEquals(List.of(17), result.getValue().betNumbers());
		assertEquals(BigDecimal.TEN, result.getValue().betAmount());
		assertEquals(35, result.getValue().payoutMultiplier());
		verify(bankingClient).bookTransaction(1L, BigDecimal.valueOf(350));
		verify(repository).save(game);
	}

	@Test
	void playReturnsLosingResponseWhenBetLoses() {
		IBetStrategy strategy = mock(IBetStrategy.class);
		RouletteGameResult gameResult = new RouletteGameResult(false, BigDecimal.TEN.negate(), 18, 0);
		RouletteGameImpl game = mock(RouletteGameImpl.class);
		BankingUserDto user = mock(BankingUserDto.class);
		PlayRequestDto request = new PlayRequestDto(1L, BigDecimal.TEN, RouletteBetType.SINGLE, List.of(17));
		when(betStrategyResolver.resolve(RouletteBetType.SINGLE)).thenReturn(Optional.of(strategy));
		when(strategy.validate(request.betNumbers())).thenReturn(new Success<>(null));
		when(bankingClient.getUser(1L)).thenReturn(new Success<>(user));
		when(user.balance()).thenReturn(BigDecimal.valueOf(100));
		when(rouletteWheel.spin()).thenReturn(18);
		when(strategy.isWinning(request.betNumbers(), 18)).thenReturn(false);
		when(strategy.getPayoutMultiplier()).thenReturn(35);
		when(rouletteGameFactory.create(1L, BigDecimal.TEN, RouletteBetType.SINGLE, List.of(17), gameResult)).thenReturn(new Success<>(game));
		when(bankingClient.bookTransaction(1L, BigDecimal.TEN.negate())).thenReturn(new Success<>(null));
		when(repository.save(game)).thenReturn(game);
		when(game.getUserId()).thenReturn(1L);
		when(game.isWinning()).thenReturn(false);
		when(game.getAmount()).thenReturn(BigDecimal.TEN.negate());
		when(game.getWinningNumber()).thenReturn(18);
		when(game.getBetType()).thenReturn(RouletteBetType.SINGLE);
		when(game.getBetNumbers()).thenReturn(List.of(17));
		when(game.getBetAmount()).thenReturn(BigDecimal.TEN);
		when(game.getPayoutMultiplier()).thenReturn(0);

		IResult<PlayResponseDto, Failures> result = handler.play(request);

		assertTrue(result.isSuccess());
		assertNotNull(result.getValue());
		assertEquals(1L, result.getValue().user());
		assertFalse(result.getValue().winning());
		assertEquals(BigDecimal.TEN.negate(), result.getValue().amount());
		assertEquals(18, result.getValue().ballPosition());
		assertEquals(RouletteBetType.SINGLE, result.getValue().betType());
		assertEquals(List.of(17), result.getValue().betNumbers());
		assertEquals(BigDecimal.TEN, result.getValue().betAmount());
		assertEquals(0, result.getValue().payoutMultiplier());
		verify(bankingClient).bookTransaction(1L, BigDecimal.TEN.negate());
		verify(repository).save(game);
	}
}