package beleg.slotsservice.view;

import java.math.BigDecimal;

/**
 * View-DTO fuer die zusammengefassten Slot-Statistiken.
 *
 * Diese Antwort beschreibt nicht eine einzelne Runde, sondern den Gesamtzustand
 * aller gespeicherten Slot-Runden in der Slots-Datenbank.
 */
public record SlotsStatsView(
        long totalGames,
        long totalWins,
        long totalLosses,
        long jackpotCount,
        BigDecimal totalBetAmount,
        BigDecimal totalResultAmount,
        BigDecimal averageBetAmount,
        BigDecimal winRatePercent
) {}
