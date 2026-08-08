package beleg.rouletteservice.controller;

import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.Success;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class FailureResponseMapperTest {

	private FailureResponseMapper mapper;

	@BeforeEach
	void setup() {
		mapper = new FailureResponseMapper();
	}

	@Test
	void toResponseReturnsOkWithValueWhenResultIsSuccessful() {
		String value = "test";
		Success<String, Failures> result = new Success<>(value);

		ResponseEntity<Object> response = mapper.toResponse(result);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(value, response.getBody());
	}

	@Test
	void toResponseReturnsNotFoundWithValueWhenResultIsNotSuccessful() {
		Failure<String, Failures> result = new Failure<>(Failures.USER_NOT_FOUND);

		ResponseEntity<Object> response = mapper.toResponse(result);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(Failures.USER_NOT_FOUND, response.getBody());
	}

	@Test
	void toResponseReturnsNotFoundWhenUserDoesNotExist() {
		Failures failure = Failures.USER_NOT_FOUND;

		ResponseEntity<Object> response = mapper.toResponse(failure);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(failure, response.getBody());
	}

	@Test
	void toResponseReturnsNotFoundWhenGameDoesNotExist() {
		Failures failure = Failures.GAME_NOT_FOUND;

		ResponseEntity<Object> response = mapper.toResponse(failure);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(failure, response.getBody());
	}

	@Test
	void toResponseReturnsBadRequestWhenFailureIsNotNull() {
		Failures failure = Failures.NOT_NULL;

		ResponseEntity<Object> response = mapper.toResponse(failure);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(failure, response.getBody());
	}

	@Test
	void toResponseReturnsBadRequestWhenFailureIsBiggerZero() {
		Failures failure = Failures.BIGGER_ZERO;

		ResponseEntity<Object> response = mapper.toResponse(failure);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(failure, response.getBody());
	}

	@Test
	void toResponseReturnsBadRequestWhenFailureIsNotNegative() {
		Failures failure = Failures.NOT_NEGATIVE;

		ResponseEntity<Object> response = mapper.toResponse(failure);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(failure, response.getBody());
	}

	@Test
	void toResponseReturnsBadRequestWhenFailureIsOutOfRange() {
		Failures failure = Failures.OUT_OF_RANGE;

		ResponseEntity<Object> response = mapper.toResponse(failure);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(failure, response.getBody());
	}

	@Test
	void toResponseReturnsBadRequestWhenFailureIsInvalidBetType() {
		Failures failure = Failures.INVALID_BET_TYPE;

		ResponseEntity<Object> response = mapper.toResponse(failure);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(failure, response.getBody());
	}

	@Test
	void toResponseReturnsBadRequestWhenBalanceIsInsufficient() {
		Failures failure = Failures.INSUFFICIENT_BALANCE;

		ResponseEntity<Object> response = mapper.toResponse(failure);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(failure, response.getBody());
	}

	@Test
	void toResponseReturnsBadRequestWhenRequestIsMalformed() {
		Failures failure = Failures.MALFORMED_REQUEST;

		ResponseEntity<Object> response = mapper.toResponse(failure);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(failure, response.getBody());
	}

	@Test
	void toResponseReturnsServiceUnavailableWhenBankingServiceIsUnavailable() {
		Failures failure = Failures.BANKING_SERVICE_UNAVAILABLE;

		ResponseEntity<Object> response = mapper.toResponse(failure);

		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
		assertEquals(failure, response.getBody());
	}

	@Test
	void toResponseReturnsInternalServerErrorWhenRoundResultIsInconsistent() {
		Failures failure = Failures.INCONSISTENT_ROUND_RESULT;

		ResponseEntity<Object> response = mapper.toResponse(failure);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals(failure, response.getBody());
	}

	@Test
	void toResponseReturnsInternalServerErrorWhenInternalErrorOccurs() {
		Failures failure = Failures.INTERNAL_ERROR;

		ResponseEntity<Object> response = mapper.toResponse(failure);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals(failure, response.getBody());
	}
}