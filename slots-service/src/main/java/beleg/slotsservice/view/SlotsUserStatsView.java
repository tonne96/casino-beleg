package beleg.slotsservice.view;

import java.math.BigDecimal;

/**
 * View-DTO fuer die zusammengefassten Slot-Statistiken eines einzelnen Users.
 *
 * Die Feldnamen orientieren sich direkt an der Belegvorgabe.
 */
public record SlotsUserStatsView(
        Long client,
        long total_games_count,
        BigDecimal total_winnings,
        BigDecimal total_losses,
        BigDecimal total_client_profit,
        BigDecimal total_house_turnover_from_client,
        BigDecimal total_house_profit_from_client
) {}
