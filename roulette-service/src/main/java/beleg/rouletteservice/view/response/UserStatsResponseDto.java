package beleg.rouletteservice.view.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record UserStatsResponseDto(

        Long client,
        @JsonProperty("total_games_count")
        long totalGamesCount,
        @JsonProperty("total_winnings")
        BigDecimal totalWinnings,
        @JsonProperty("total_losses")
        BigDecimal totalLosses,
        @JsonProperty("total_client_profit")
        BigDecimal totalClientProfit,
        @JsonProperty("total_house_turnover_from_client")
        BigDecimal totalHouseTurnoverFromClient,
        @JsonProperty("total_house_profit_from_client")
        BigDecimal totalHouseProfitFromClient
) {
}