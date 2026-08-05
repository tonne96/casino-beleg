package beleg.rouletteservice.controller;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class FailureResponseMapper {


    public <T> ResponseEntity<Object> toResponse(IResult<T, Failures> result) {
        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(result.getValue());
        }
        return toResponse(result.getMessage());
    }

    public ResponseEntity<Object> toResponse(Failures failure) {
        HttpStatus status = statusFor(failure);
        return ResponseEntity.status(status).body(failure);
    }
    
    private HttpStatus statusFor(Failures failure) {
        HttpStatus status = switch (failure) {
            case USER_NOT_FOUND, GAME_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case NOT_NULL, BIGGER_ZERO, NOT_NEGATIVE, OUT_OF_RANGE,
                INVALID_BET_TYPE, INSUFFICIENT_BALANCE, MALFORMED_REQUEST -> HttpStatus.BAD_REQUEST;
            case BANKING_SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case INCONSISTENT_ROUND_RESULT, INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return status;
    }
}
