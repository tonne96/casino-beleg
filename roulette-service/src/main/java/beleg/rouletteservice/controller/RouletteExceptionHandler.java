package beleg.rouletteservice.controller;

import beleg.rouletteservice.result.Failures;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RouletteExceptionHandler {

    private final FailureResponseMapper responseMapper;

    public RouletteExceptionHandler(FailureResponseMapper responseMapper) {
        this.responseMapper = responseMapper;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleUnreadableBody(HttpMessageNotReadableException e) {
        return responseMapper.toResponse(Failures.MALFORMED_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception e) {
        return responseMapper.toResponse(Failures.INTERNAL_ERROR);
    }
}