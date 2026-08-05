package beleg.rouletteservice.view.request;

import beleg.rouletteservice.rules.RouletteBetType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
// Request Body für POST
// Validierung passiert erst in Domain (RouletteGame.create / RouletteGameResult.create)

public record PlayRequestDto(

        @Schema(description = "ID des wettenden Nutzers (beim Banking-Service verwaltet)", example = "1")
        Long user,

        @Schema(description = "Eingesetzter Betrag, muss groesser als 0 sein", example = "10.00")
        @JsonProperty("bet_amount")
        BigDecimal betAmount,

        @Schema(description = "Gewaehlte Wettart. Bestimmt die Bedeutung von bet_numbers - siehe dessen Beschreibung.",
                example = "SINGLE")
        @JsonProperty("bet_type")
        RouletteBetType betType,

        @Schema(description = """
                Zahl(en), deren Bedeutung vom gewaehlten bet_type abhaengt:
                - SINGLE: [a] - genau eine Zahl, a aus 0 bis 36
                - RED_OR_BLACK: [0|1] - 0 = Schwarz, 1 = Rot
                - ODD_OR_EVEN: [0|1] - 0 = Gerade, 1 = Ungerade
                - LOW_OR_HIGH: [0|1] - 0 = Niedrig (1-18), 1 = Hoch (19-36)
                - DOZEN: [1|2|3] - 1., 2. oder 3. Dutzend
                - COLUMN: [1|2|3] - 1., 2. oder 3. Spalte
                - STREET: [a] - Start einer 3er-Reihe, a aus {1,4,7,...,34} 1. Spalte
                - SIX_LINE: [a] - Start einer 6er-Reihe, a aus {1,4,7,...,31} 1. Spalte
                - CORNER: [a] - linke obere Ecke eines Vierecks, a modulo 3 ungleich 0, a aus 1. oder 2. Spalte
                - SPLIT: [a, b] - zwei auf dem Tableau echt benachbarte Zahlen, je 0-36
                Ausfuehrliche Erklaerung: GET /casino/roulette/api/info/rules
                """,
                example = "[17]")
        @JsonProperty("bet_numbers")
        List<Integer> betNumbers
) {
}
