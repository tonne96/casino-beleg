package beleg.slotsservice.handler.stats;

import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotSymbol;
import beleg.slotsservice.repository.IGameResultRepository;
import beleg.slotsservice.view.SlotsStatsView;
import beleg.slotsservice.view.SlotsUserStatsView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests fuer die Statistik-Logik.
 *
 * Das Repository wird gemockt. Dadurch pruefen wir nur die Berechnung im
 * SlotStatsHandler.
 */
class SlotStatsHandlerTest {

    private IGameResultRepository gameResultRepository;
    private SlotStatsHandler slotStatsHandler;

    @BeforeEach
    void setUp() {
        gameResultRepository = mock(IGameResultRepository.class, withSettings().mockMaker(MockMakers.PROXY));
        slotStatsHandler = new SlotStatsHandler(gameResultRepository);
    }

    @Test
    void getStatsReturnsZeroValuesForEmptyDatabase() {
        when(gameResultRepository.findAll()).thenReturn(List.of());

        SlotsStatsView result = slotStatsHandler.getStats();

        assertEquals(0, result.total_client_count());
        assertEquals(0, result.total_games_count());
        assertEquals(BigDecimal.ZERO, result.total_profit());
        assertEquals(BigDecimal.ZERO, result.total_cash_out());
        assertEquals(BigDecimal.ZERO, result.total_turnover());
        verify(gameResultRepository).findAll();
    }

    @Test
    void getStatsCalculatesGlobalStatsForMultipleUsers() {
        List<SlotGame> games = List.of(
                createSlotGame(1L, BigDecimal.TEN, false, BigDecimal.TEN.negate(), 0),
                createSlotGame(1L, BigDecimal.TEN, true, BigDecimal.ZERO, 1),
                createSlotGame(2L, BigDecimal.valueOf(20), true, BigDecimal.valueOf(40), 3)
        );

        when(gameResultRepository.findAll()).thenReturn(games);

        SlotsStatsView result = slotStatsHandler.getStats();

        assertEquals(2, result.total_client_count());
        assertEquals(3, result.total_games_count());
        assertEquals(BigDecimal.valueOf(40), result.total_turnover());
        assertEquals(BigDecimal.valueOf(70), result.total_cash_out());
        assertEquals(BigDecimal.valueOf(-30), result.total_profit());
        verify(gameResultRepository).findAll();
    }

    @Test
    void getUserStatsCalculatesStatsForUserWithGames() {
        Long userId = 1L;
        List<SlotGame> games = List.of(
                createSlotGame(userId, BigDecimal.TEN, false, BigDecimal.TEN.negate(), 0),
                createSlotGame(userId, BigDecimal.TEN, true, BigDecimal.ZERO, 1),
                createSlotGame(userId, BigDecimal.valueOf(20), true, BigDecimal.valueOf(40), 3)
        );

        when(gameResultRepository.findByUserId(userId)).thenReturn(games);

        Optional<SlotsUserStatsView> result = slotStatsHandler.getUserStats(userId);

        assertTrue(result.isPresent());

        SlotsUserStatsView stats = result.get();
        assertEquals(userId, stats.client());
        assertEquals(3, stats.total_games_count());
        assertEquals(BigDecimal.valueOf(70), stats.total_winnings());
        assertEquals(BigDecimal.TEN, stats.total_losses());
        assertEquals(BigDecimal.valueOf(30), stats.total_client_profit());
        assertEquals(BigDecimal.valueOf(40), stats.total_house_turnover_from_client());
        assertEquals(BigDecimal.valueOf(-30), stats.total_house_profit_from_client());
        verify(gameResultRepository).findByUserId(userId);
    }

    @Test
    void getUserStatsReturnsEmptyWhenUserHasNoGames() {
        Long userId = 99L;

        when(gameResultRepository.findByUserId(userId)).thenReturn(List.of());

        Optional<SlotsUserStatsView> result = slotStatsHandler.getUserStats(userId);

        assertTrue(result.isEmpty());
        verify(gameResultRepository).findByUserId(userId);
    }

    private SlotGame createSlotGame(
            Long userId,
            BigDecimal betAmount,
            boolean winning,
            BigDecimal amount,
            int payoutMultiplier) {
        return SlotGame.create(
                userId,
                betAmount,
                winning,
                amount,
                SlotSymbol.BAR,
                SlotSymbol.BAR,
                SlotSymbol.BAR,
                payoutMultiplier,
                LocalDateTime.of(2026, 6, 24, 12, 0)
        );
    }
}
