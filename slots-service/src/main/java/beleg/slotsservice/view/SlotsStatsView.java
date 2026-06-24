package beleg.slotsservice.view;

import java.math.BigDecimal;

/**
 * View-DTO fuer die zusammengefassten Slot-Statistiken.
 *
 * Die Feldnamen orientieren sich direkt an der Belegvorgabe.
 */
public record SlotsStatsView(
        long total_client_count,
        long total_games_count,
        BigDecimal total_profit,
        BigDecimal total_cash_out,
        BigDecimal total_turnover
) {}
