package beleg.slotsservice.handler.stats;

import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.repository.SlotGameRepository;
import beleg.slotsservice.view.SlotsStatsView;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * SlotStatsHandler = Standard-Implementierung fuer Slot-Statistiken.
 *
 * Diese Klasse berechnet zusammengefasste Werte aus der Slots-Datenbank.
 * Sie spielt keine neue Runde und loescht keine Daten.
 */
@Service
public class SlotStatsHandler implements ISlotStatsHandler {

    private static final int JACKPOT_MULTIPLIER = 10;

    private final SlotGameRepository slotGameRepository;

    public SlotStatsHandler(SlotGameRepository slotGameRepository) {
        this.slotGameRepository = slotGameRepository;
    }

    /**
     * Berechnet die Gesamtstatistik ueber alle gespeicherten Slot-Runden.
     */
    @Override
    public SlotsStatsView getStats() {
        List<SlotGame> games = slotGameRepository.findAll();

        long totalGames = games.size();
        long totalWins = 0;
        long jackpotCount = 0;
        BigDecimal totalBetAmount = BigDecimal.ZERO;
        BigDecimal totalResultAmount = BigDecimal.ZERO;

        for (SlotGame game : games) {
            totalBetAmount = totalBetAmount.add(game.getBetAmount());
            totalResultAmount = totalResultAmount.add(game.getAmount());

            if (game.isWinning()) {
                totalWins++;
            }
            if (game.getPayoutMultiplier() == JACKPOT_MULTIPLIER) {
                jackpotCount++;
            }
        }

        long totalLosses = totalGames - totalWins;

        return new SlotsStatsView(
                totalGames,
                totalWins,
                totalLosses,
                jackpotCount,
                totalBetAmount,
                totalResultAmount,
                calculateAverageBetAmount(totalBetAmount, totalGames),
                calculateWinRatePercent(totalWins, totalGames)
        );
    }

    /**
     * Durchschnittlicher Einsatz pro Runde.
     * Bei 0 Spielen wird 0 zurueckgegeben, damit keine Division durch 0 passiert.
     */
    private BigDecimal calculateAverageBetAmount(BigDecimal totalBetAmount, long totalGames) {
        if (totalGames == 0) {
            return BigDecimal.ZERO;
        }
        return totalBetAmount.divide(BigDecimal.valueOf(totalGames), 2, RoundingMode.HALF_UP);
    }

    /**
     * Gewinnquote in Prozent.
     * Beispiel: 3 Gewinne bei 10 Spielen ergeben 30.00 Prozent.
     */
    private BigDecimal calculateWinRatePercent(long totalWins, long totalGames) {
        if (totalGames == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(totalWins)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalGames), 2, RoundingMode.HALF_UP);
    }
}
