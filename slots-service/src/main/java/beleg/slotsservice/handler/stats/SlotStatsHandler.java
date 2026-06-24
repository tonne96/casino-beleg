package beleg.slotsservice.handler.stats;

import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.repository.IGameResultRepository;
import beleg.slotsservice.view.SlotsStatsView;
import beleg.slotsservice.view.SlotsUserStatsView;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * SlotStatsHandler = Standard-Implementierung fuer Slot-Statistiken.
 *
 * Diese Klasse berechnet zusammengefasste Werte aus der Slots-Datenbank.
 * Sie spielt keine neue Runde und loescht keine Daten.
 */
@Service
public class SlotStatsHandler implements ISlotStatsHandler {

    private final IGameResultRepository gameResultRepository;

    public SlotStatsHandler(IGameResultRepository gameResultRepository) {
        this.gameResultRepository = gameResultRepository;
    }

    /**
     * Berechnet die Gesamtstatistik ueber alle gespeicherten Slot-Runden.
     */
    @Override
    public SlotsStatsView getStats() {
        List<SlotGame> games = gameResultRepository.findAll();

        Set<Long> clients = new HashSet<>();
        BigDecimal totalTurnover = BigDecimal.ZERO;
        BigDecimal totalCashOut = BigDecimal.ZERO;

        for (SlotGame game : games) {
            clients.add(game.getUserId());
            totalTurnover = totalTurnover.add(game.getBetAmount());
            totalCashOut = totalCashOut.add(calculateCashOut(game));
        }

        BigDecimal totalProfit = totalTurnover.subtract(totalCashOut);

        return new SlotsStatsView(
                clients.size(),
                games.size(),
                totalProfit,
                totalCashOut,
                totalTurnover
        );
    }

    /**
     * Berechnet die zusammengefasste Statistik fuer einen einzelnen User.
     */
    @Override
    public Optional<SlotsUserStatsView> getUserStats(Long userId) {
        List<SlotGame> games = gameResultRepository.findByUserId(userId);

        if (games.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal totalWinnings = BigDecimal.ZERO;
        BigDecimal totalLosses = BigDecimal.ZERO;
        BigDecimal totalTurnover = BigDecimal.ZERO;

        for (SlotGame game : games) {
            BigDecimal cashOut = calculateCashOut(game);

            totalWinnings = totalWinnings.add(cashOut);
            totalTurnover = totalTurnover.add(game.getBetAmount());

            if (!game.isWinning()) {
                totalLosses = totalLosses.add(game.getBetAmount());
            }
        }

        BigDecimal totalClientProfit = totalWinnings.subtract(totalTurnover);
        BigDecimal totalHouseProfit = totalTurnover.subtract(totalWinnings);

        return Optional.of(new SlotsUserStatsView(
                userId,
                games.size(),
                totalWinnings,
                totalLosses,
                totalClientProfit,
                totalTurnover,
                totalHouseProfit
        ));
    }

    /**
     * Cash-Out ist die Brutto-Auszahlung des Automaten.
     *
     * Beispiel bei Einsatz 10:
     *  - Verlust: payoutMultiplier 0  -> Cash-Out 0
     *  - Einsatz zurueck: 1           -> Cash-Out 10
     *  - Drei gleiche: 3              -> Cash-Out 30
     *  - Jackpot: 10                  -> Cash-Out 100
     */
    private BigDecimal calculateCashOut(SlotGame game) {
        return game.getBetAmount().multiply(BigDecimal.valueOf(game.getPayoutMultiplier()));
    }
}
