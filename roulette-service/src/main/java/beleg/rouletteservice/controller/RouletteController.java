package beleg.rouletteservice.controller;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.handler.game.IRouletteGameHandler;
import beleg.rouletteservice.view.request.PlayRequestDto;
import beleg.rouletteservice.view.response.PlayResponseDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/casino/roulette/api")
public class RouletteController {

    private static final Logger log = LoggerFactory.getLogger(RouletteController.class);

    private final IRouletteGameHandler rouletteGameHandler;

    public RouletteController(IRouletteGameHandler rouletteGameHandler) {
        this.rouletteGameHandler = rouletteGameHandler;
    }
    // Mehrere Beispiele für die Wettarten und wie man den Request macht
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {
                            @ExampleObject(
                                    name = "SINGLE - Wette auf eine Zahl",
                                    value = """
                                            {
                                              "user": 1,
                                              "bet_amount": 10.00,
                                              "bet_type": "SINGLE",
                                              "bet_numbers": [17]
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "RED_OR_BLACK - Wette auf Rot oder Schwarz",
                                    value = """
                                            {
                                              "user": 1,
                                              "bet_amount": 10.00,
                                              "bet_type": "RED_OR_BLACK",
                                              "bet_numbers": [1]
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "SPLIT - Wette auf zwei benachbarte Zahlen",
                                    value = """
                                            {
                                              "user": 1,
                                              "bet_amount": 10.00,
                                              "bet_type": "SPLIT",
                                              "bet_numbers": [17, 20]
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "DOZEN - Wette auf ein Dutzend (1-12 / 13-24 / 25-36)",
                                    value = """
                                            {
                                              "user": 1,
                                              "bet_amount": 10.00,
                                              "bet_type": "DOZEN",
                                              "bet_numbers": [2]
                                            }
                                            """
                            )
                    }
            )
    )
    @PostMapping("/play")
    public ResponseEntity<Object> play(@RequestBody PlayRequestDto request) {
        IResult<PlayResponseDto, Failures> result = rouletteGameHandler.play(request);
        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(result.getValue());
        }
        return buildErrorResponse(result.getMessage());
    }

    private ResponseEntity<Object> buildErrorResponse(Failures failure) {
        HttpStatus status = switch (failure) {
            case USER_NOT_FOUND, GAME_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case NOT_NULL, BIGGER_ZERO, NOT_NEGATIVE, OUT_OF_RANGE, INVALID_BET_TYPE, INSUFFICIENT_BALANCE -> HttpStatus.BAD_REQUEST;
            case BANKING_SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case INCONSISTENT_ROUND_RESULT -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        log.warn("Error: {}", failure);
        return ResponseEntity.status(status).body(failure);
    }
}