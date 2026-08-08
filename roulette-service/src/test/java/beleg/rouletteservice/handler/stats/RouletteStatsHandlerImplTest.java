package beleg.rouletteservice.handler.stats;

import beleg.rouletteservice.client.banking.IBankingClient;
import beleg.rouletteservice.client.banking.BankingUserDto;
import beleg.rouletteservice.model.RouletteGameImpl;
import beleg.rouletteservice.repository.IRouletteGameRepository;
import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.result.Success;
import beleg.rouletteservice.rules.RouletteBetType;
import beleg.rouletteservice.view.response.GameStatDto;
import beleg.rouletteservice.view.response.GlobalStatsResponseDto;
import beleg.rouletteservice.view.response.UserStatsResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouletteStatsHandlerImplTest {

	private IRouletteGameRepository repository;
	private IBankingClient bankingClient;
	private RouletteStatsHandlerImpl handler;

	// Hilfsmethode
	private RouletteGameImpl game(Long id, Long userId, boolean winning, int amount, int betAmount, int payoutMultiplier) {
		RouletteGameImpl game = mock(RouletteGameImpl.class);
		when(game.getId()).thenReturn(id);
		when(game.getUserId()).thenReturn(userId);
		when(game.isWinning()).thenReturn(winning);
		when(game.getAmount()).thenReturn(BigDecimal.valueOf(amount));
		when(game.getBetAmount()).thenReturn(BigDecimal.valueOf(betAmount));
		when(game.getWinningNumber()).thenReturn(17);
		when(game.getBetType()).thenReturn(RouletteBetType.SINGLE);
		when(game.getBetNumbers()).thenReturn(List.of(17));
		when(game.getPayoutMultiplier()).thenReturn(payoutMultiplier);
		when(game.getPlayedAt()).thenReturn(LocalDateTime.of(2026, 1, 1, 12, 0));
		return game;
	}

	@BeforeEach
	void setUp() {
		repository = mock(IRouletteGameRepository.class);
		bankingClient = mock(IBankingClient.class);
		handler = new RouletteStatsHandlerImpl(repository, bankingClient);
	}

	@Test
	void getGlobalStatsCalculatesStatisticsCorrectly() {
		RouletteGameImpl winningGame = game(1L, 10L, true, 20, 10, 1);
		RouletteGameImpl losingGame = game(2L, 20L, false, -10, 10, 0);
		when(repository.findAll()).thenReturn(List.of(winningGame, losingGame));

		GlobalStatsResponseDto result = handler.getGlobalStats();

		assertEquals(2, result.totalClientCount());
		assertEquals(2, result.totalGamesCount());
		assertEquals(BigDecimal.ZERO, result.totalProfit());
		assertEquals(new BigDecimal("20"), result.totalCashOut());
		assertEquals(new BigDecimal("20"), result.totalTurnover());
	}

	@Test
	void getGlobalStatsReturnsZeroValuesWhenNoGamesExist() {
		when(repository.findAll()).thenReturn(List.of());

		GlobalStatsResponseDto result = handler.getGlobalStats();

		assertEquals(0, result.totalClientCount());
		assertEquals(0, result.totalGamesCount());
		assertEquals(BigDecimal.ZERO, result.totalProfit());
		assertEquals(BigDecimal.ZERO, result.totalCashOut());
		assertEquals(BigDecimal.ZERO, result.totalTurnover());
	}

	@Test
	void getUserStatsReturnsFailureWhenBankingUserIsUnavailable() {
		when(bankingClient.getUser(1L)).thenReturn(new Failure<>(Failures.USER_NOT_FOUND));

		IResult<UserStatsResponseDto, Failures> result = handler.getUserStats(1L);

		assertFalse(result.isSuccess());
		assertSame(Failures.USER_NOT_FOUND, result.getMessage());
		verifyNoInteractions(repository);
	}

	@Test
	void getUserStatsCalculatesStatisticsCorrectly() {
		RouletteGameImpl winningGame = game(1L, 1L, true, 20, 10, 1);
		RouletteGameImpl losingGame = game(2L, 1L, false, -10, 10, 0);
		when(bankingClient.getUser(1L)).thenReturn(new Success<>(mock(BankingUserDto.class)));
		when(repository.findByUserId(1L)).thenReturn(List.of(winningGame, losingGame));

		IResult<UserStatsResponseDto, Failures> result = handler.getUserStats(1L);

		assertTrue(result.isSuccess());
		UserStatsResponseDto dto = result.getValue();

		assertEquals(1L, dto.client());
		assertEquals(2, dto.totalGamesCount());
		assertEquals(new BigDecimal("20"), dto.totalWinnings());
		assertEquals(new BigDecimal("10"), dto.totalLosses());
		assertEquals(new BigDecimal("0"), dto.totalClientProfit());
		assertEquals(new BigDecimal("20"), dto.totalHouseTurnoverFromClient());
		assertEquals(new BigDecimal("0"), dto.totalHouseProfitFromClient());
	}

	@Test
	void getUserStatsReturnsZeroValuesWhenUserHasNoGames() {
		when(bankingClient.getUser(1L)).thenReturn(new Success<>(mock(BankingUserDto.class)));
		when(repository.findByUserId(1L)).thenReturn(List.of());

		IResult<UserStatsResponseDto, Failures> result = handler.getUserStats(1L);

		assertTrue(result.isSuccess());
		UserStatsResponseDto dto = result.getValue();

		assertEquals(1L, dto.client());
		assertEquals(0, dto.totalGamesCount());
		assertEquals(BigDecimal.ZERO, dto.totalWinnings());
		assertEquals(BigDecimal.ZERO, dto.totalLosses());
		assertEquals(BigDecimal.ZERO, dto.totalClientProfit());
		assertEquals(BigDecimal.ZERO, dto.totalHouseTurnoverFromClient());
		assertEquals(BigDecimal.ZERO, dto.totalHouseProfitFromClient());
	}

	@Test
	void getAllGameStatsConvertsAllGamesToDtos() {
		RouletteGameImpl first = game(1L, 1L, true, 20, 10, 1);
		RouletteGameImpl second = game(2L, 2L, false, -10, 10, 0);
		when(repository.findAll()).thenReturn(List.of(first, second));

		List<GameStatDto> result = handler.getAllGameStats();

		assertEquals(2, result.size());
		assertEquals(1L, result.get(0).id());
		assertEquals(2L, result.get(1).id());
		assertEquals(1L, result.get(0).user());
		assertEquals(2L, result.get(1).user());
	}

	@Test
	void getAllGameStatsReturnsEmptyListWhenNoGamesExist() {
		when(repository.findAll()).thenReturn(List.of());

		List<GameStatDto> result = handler.getAllGameStats();

		assertTrue(result.isEmpty());
	}

	@Test
	void getGameStatReturnsGameNotFoundWhenGameDoesNotExist() {
		when(repository.findById(1L)).thenReturn(Optional.empty());

		IResult<GameStatDto, Failures> result = handler.getGameStat(1L);

		assertFalse(result.isSuccess());
		assertSame(Failures.GAME_NOT_FOUND, result.getMessage());
	}

	@Test
	void getGameStatReturnsDtoWhenGameExists() {
		RouletteGameImpl game = game(1L, 1L, true, 20, 10, 1);
		when(repository.findById(1L)).thenReturn(Optional.of(game));

		IResult<GameStatDto, Failures> result = handler.getGameStat(1L);

		assertTrue(result.isSuccess());
		assertEquals(1L, result.getValue().id());
		assertEquals(1L, result.getValue().user());
		assertTrue(result.getValue().winning());
		assertEquals(new BigDecimal("20"), result.getValue().amount());
		assertEquals(new BigDecimal("10"), result.getValue().betAmount());
		assertEquals(1, result.getValue().payoutMultiplier());
	}

	@Test
	void deleteGameStatReturnsGameNotFoundWhenGameDoesNotExist() {
		when(repository.findById(1L)).thenReturn(Optional.empty());

		IResult<GameStatDto, Failures> result = handler.deleteGameStat(1L);

		assertFalse(result.isSuccess());
		assertSame(Failures.GAME_NOT_FOUND, result.getMessage());
		verify(repository, never()).deleteById(anyLong());
	}

	@Test
	void deleteGameStatReturnsDtoAndDeletesGameWhenGameExists() {
		RouletteGameImpl game = game(1L, 1L, true, 20, 10, 1);
		when(repository.findById(1L)).thenReturn(Optional.of(game));

		IResult<GameStatDto, Failures> result = handler.deleteGameStat(1L);

		assertTrue(result.isSuccess());
		assertEquals(1L, result.getValue().id());
		verify(repository).deleteById(1L);
	}
}