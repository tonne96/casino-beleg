package beleg.rouletteservice.view.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record GlobalStatsResponseDto(

        @JsonProperty("total_client_count")
        long totalClientCount,
        @JsonProperty("total_games_count")
        long totalGamesCount,
        @JsonProperty("total_profit")
        BigDecimal totalProfit,
        @JsonProperty("total_cash_out")
        BigDecimal totalCashOut,
        @JsonProperty("total_turnover")
        BigDecimal totalTurnover
) {
}