package beleg.rouletteservice.controller;

import beleg.rouletteservice.result.Failures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouletteExceptionHandlerTest {

	private FailureResponseMapper responseMapper;
	private RouletteExceptionHandler exceptionHandler;

	@BeforeEach
	void setUp() {
		responseMapper = mock(FailureResponseMapper.class);
		exceptionHandler = new RouletteExceptionHandler(responseMapper);
	}

	@Test
	void handleUnreadableBodyReturnsMappedMalformedRequest() {
		HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
		ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Failures.MALFORMED_REQUEST);
		when(responseMapper.toResponse(Failures.MALFORMED_REQUEST)).thenReturn(expectedResponse);

		ResponseEntity<Object> response = exceptionHandler.handleUnreadableBody(exception);

		assertEquals(expectedResponse, response);
		verify(responseMapper).toResponse(Failures.MALFORMED_REQUEST);
	}

	@Test
	void handleUnexpectedReturnsMappedInternalError() {
		Exception exception = new RuntimeException("unexpected error");
		ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Failures.INTERNAL_ERROR);
		when(responseMapper.toResponse(Failures.INTERNAL_ERROR)).thenReturn(expectedResponse);

		ResponseEntity<Object> response = exceptionHandler.handleUnexpected(exception);

		assertEquals(expectedResponse, response);
		verify(responseMapper).toResponse(Failures.INTERNAL_ERROR);
	}
}