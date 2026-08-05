package beleg.rouletteservice.controller;

import beleg.rouletteservice.handler.game.IRouletteGameHandler;
import beleg.rouletteservice.view.request.PlayRequestDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/casino/roulette/api")
public class RouletteController {

    private final IRouletteGameHandler rouletteGameHandler;
    private final FailureResponseMapper responseMapper;

    public RouletteController(IRouletteGameHandler rouletteGameHandler,
                              FailureResponseMapper responseMapper) {
        this.rouletteGameHandler = rouletteGameHandler;
        this.responseMapper = responseMapper;
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
                                    name = "RED_OR_BLACK - Wette auf Schwarz [0] oder Rot [1]",
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
                                    name = "DOZEN - Wette auf ein Dutzend (1-12 [1] / 13-24 [2] / 25-36 [3])",
                                    value = """
                                            {
                                              "user": 1,
                                              "bet_amount": 10.00,
                                              "bet_type": "DOZEN",
                                              "bet_numbers": [2]
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "CORNER - Wette auf ein Viereck aus vier Zahlen [kleinste Zahl]",
                                    value = """
                                            {
                                            "user": 1,
                                            "bet_amount": 10.00,
                                            "bet_type": "CORNER",
                                            "bet_numbers": [26]
                                            }
                                            """
                            )
                    }
            )
    )
        @PostMapping("/play")
        public ResponseEntity<Object> play(@RequestBody PlayRequestDto request) {
                return responseMapper.toResponse(rouletteGameHandler.play(request));
    }
}