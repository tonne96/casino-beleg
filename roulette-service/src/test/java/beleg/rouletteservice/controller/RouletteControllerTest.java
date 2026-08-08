package beleg.rouletteservice.controller;

import beleg.rouletteservice.handler.game.IRouletteGameHandler;
import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.Success;
import beleg.rouletteservice.view.request.PlayRequestDto;
import beleg.rouletteservice.view.response.PlayResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouletteControllerTest {

	private IRouletteGameHandler rouletteGameHandler;
	private FailureResponseMapper responseMapper;
	private RouletteController rouletteController;

	@BeforeEach
	void setUp() {
		rouletteGameHandler = mock(IRouletteGameHandler.class);
		responseMapper = mock(FailureResponseMapper.class);
		rouletteController = new RouletteController(rouletteGameHandler, responseMapper);
	}

	@Test
	void playReturnsMappedResponseWhenGameSucceeds() {
		PlayRequestDto request = new PlayRequestDto(1L, BigDecimal.TEN, null, List.of(17));
		PlayResponseDto resultValue = new PlayResponseDto(1L, true, BigDecimal.TEN, 17, null, List.of(17), BigDecimal.TEN, 2);
		Success<PlayResponseDto, Failures> result = new Success<>(resultValue);
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok(resultValue);
		when(rouletteGameHandler.play(request)).thenReturn(result);
		when(responseMapper.toResponse(result)).thenReturn(expectedResponse);

		ResponseEntity<Object> response = rouletteController.play(request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(resultValue, response.getBody());
		verify(rouletteGameHandler).play(request);
		verify(responseMapper).toResponse(result);
	}

	@Test
	void playReturnsMappedErrorResponseWhenGameFails() {
		PlayRequestDto request = new PlayRequestDto(1L, BigDecimal.TEN, null, List.of(17));
		Failure<PlayResponseDto, Failures> result = new Failure<>(Failures.USER_NOT_FOUND);
		ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).body(Failures.USER_NOT_FOUND);
		when(rouletteGameHandler.play(request)).thenReturn(result);
		when(responseMapper.toResponse(result)).thenReturn(expectedResponse);

		ResponseEntity<Object> response = rouletteController.play(request);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(Failures.USER_NOT_FOUND, response.getBody());
		verify(rouletteGameHandler).play(request);
		verify(responseMapper).toResponse(result);
	}
}