package beleg.rouletteservice.controller;

import beleg.rouletteservice.handler.stats.IRouletteStatsHandler;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.view.response.GameStatDto;
import beleg.rouletteservice.view.response.GlobalStatsResponseDto;
import beleg.rouletteservice.view.response.UserStatsResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouletteStatsControllerTest {

	private IRouletteStatsHandler statsService;
	private FailureResponseMapper responseMapper;
	private RouletteStatsController controller;

	@BeforeEach
	void setUp() {
		statsService = mock(IRouletteStatsHandler.class);
		responseMapper = mock(FailureResponseMapper.class);
		controller = new RouletteStatsController(statsService, responseMapper);
	}

	@Test
	void getGlobalStatsReturnsStatsFromService() {
		GlobalStatsResponseDto stats = mock(GlobalStatsResponseDto.class);
		when(statsService.getGlobalStats()).thenReturn(stats);

		ResponseEntity<GlobalStatsResponseDto> response = controller.getGlobalStats();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(stats, response.getBody());
		verify(statsService).getGlobalStats();
	}

	@Test
	void getUserStatsReturnsMappedResponse() {
		Long userId = 1L;
		IResult<UserStatsResponseDto, Failures> result = mock(IResult.class);
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
		when(statsService.getUserStats(userId)).thenReturn(result);
		when(responseMapper.toResponse(result)).thenReturn(expectedResponse);

		ResponseEntity<Object> response = controller.getUserStats(userId);

		assertEquals(expectedResponse, response);
		verify(statsService).getUserStats(userId);
		verify(responseMapper).toResponse(result);
	}

	@Test
	void getAllGameStatsReturnsStatsFromService() {
		List<GameStatDto> stats = List.of(mock(GameStatDto.class), mock(GameStatDto.class));
		when(statsService.getAllGameStats()).thenReturn(stats);

		ResponseEntity<List<GameStatDto>> response = controller.getAllGameStats();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(stats, response.getBody());
		verify(statsService).getAllGameStats();
	}

	@Test
	void getGameStatReturnsMappedResponse() {
		Long gameId = 1L;
		IResult<GameStatDto, Failures> result = mock(IResult.class);
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
		when(statsService.getGameStat(gameId)).thenReturn(result);
		when(responseMapper.toResponse(result)).thenReturn(expectedResponse);

		ResponseEntity<Object> response = controller.getGameStat(gameId);

		assertEquals(expectedResponse, response);
		verify(statsService).getGameStat(gameId);
		verify(responseMapper).toResponse(result);
	}

	@Test
	void deleteGameStatReturnsMappedResponse() {
		Long gameId = 1L;
		IResult<GameStatDto, Failures> result = mock(IResult.class);
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
		when(statsService.deleteGameStat(gameId)).thenReturn(result);
		when(responseMapper.toResponse(result)).thenReturn(expectedResponse);

		ResponseEntity<Object> response = controller.deleteGameStat(gameId);

		assertEquals(expectedResponse, response);
		verify(statsService).deleteGameStat(gameId);
		verify(responseMapper).toResponse(result);
	}
}