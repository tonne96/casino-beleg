package beleg.rouletteservice.view.response;

import beleg.rouletteservice.rules.RouletteBetType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record PlayResponseDto(

        Long user,
        boolean winning,
        BigDecimal amount,
        @JsonProperty("ball_position")
        int ballPosition,
        @JsonProperty("bet_type")
        RouletteBetType betType,
        @JsonProperty("bet_numbers")
        List<Integer> betNumbers,
        @JsonProperty("bet_amount")
        BigDecimal betAmount,
        @JsonProperty("payout_multiplier")
        int payoutMultiplier
) {
}
