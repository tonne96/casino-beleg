package beleg.slotsservice.controller;

import beleg.slotsservice.handler.game.ISlotGameHistoryHandler;
import beleg.slotsservice.handler.stats.ISlotStatsHandler;
import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotSymbol;
import beleg.slotsservice.view.SlotGameView;
import beleg.slotsservice.view.SlotsStatsView;
import beleg.slotsservice.view.SlotsUserStatsView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests fuer den Statistik-Controller.
 *
 * Die Handler werden gemockt. Der Test prueft nur, ob der Controller die
 * Handler-Ergebnisse korrekt in HTTP-Responses uebersetzt.
 */
class SlotsStatsControllerTest {

    private ISlotGameHistoryHandler slotGameHistoryHandler;
    private ISlotStatsHandler slotStatsHandler;
    private SlotsStatsController slotsStatsController;

    @BeforeEach
    void setUp() {
        slotGameHistoryHandler = mock(ISlotGameHistoryHandler.class, withSettings().mockMaker(MockMakers.PROXY));
        slotStatsHandler = mock(ISlotStatsHandler.class, withSettings().mockMaker(MockMakers.PROXY));
        slotsStatsController = new SlotsStatsController(slotGameHistoryHandler, slotStatsHandler);
    }

    @Test
    void getStatsReturnsOk() {
        SlotsStatsView stats = new SlotsStatsView(
                2,
                5,
                BigDecimal.valueOf(30),
                BigDecimal.valueOf(70),
                BigDecimal.valueOf(100)
        );

        when(slotStatsHandler.getStats()).thenReturn(stats);

        ResponseEntity<SlotsStatsView> response = slotsStatsController.getStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(stats, response.getBody());
        verify(slotStatsHandler).getStats();
        verifyNoInteractions(slotGameHistoryHandler);
    }

    @Test
    void getUserStatsReturnsOkWhenStatsExist() {
        Long userId = 1L;
        SlotsUserStatsView userStats = new SlotsUserStatsView(
                userId,
                3,
                BigDecimal.valueOf(40),
                BigDecimal.TEN,
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(30),
                BigDecimal.TEN.negate()
        );

        when(slotStatsHandler.getUserStats(userId)).thenReturn(Optional.of(userStats));

        ResponseEntity<SlotsUserStatsView> response = slotsStatsController.getUserStats(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(userStats, response.getBody());
        verify(slotStatsHandler).getUserStats(userId);
        verifyNoInteractions(slotGameHistoryHandler);
    }

    @Test
    void getUserStatsReturnsNotFoundWhenNoStatsExist() {
        Long userId = 99L;

        when(slotStatsHandler.getUserStats(userId)).thenReturn(Optional.empty());

        ResponseEntity<SlotsUserStatsView> response = slotsStatsController.getUserStats(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(slotStatsHandler).getUserStats(userId);
        verifyNoInteractions(slotGameHistoryHandler);
    }

    @Test
    void getUserStatsReturnsBadRequestForInvalidUserId() {
        ResponseEntity<SlotsUserStatsView> response = slotsStatsController.getUserStats(0L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verifyNoInteractions(slotStatsHandler, slotGameHistoryHandler);
    }

    @Test
    void getAllGamesReturnsOkWithGameList() {
        SlotGame firstGame = createSlotGame(1L, BigDecimal.TEN, false, BigDecimal.TEN.negate(), 0);
        SlotGame secondGame = createSlotGame(2L, BigDecimal.valueOf(20), true, BigDecimal.valueOf(40), 3);
        List<SlotGame> games = List.of(firstGame, secondGame);

        when(slotGameHistoryHandler.getAllGames()).thenReturn(games);

        ResponseEntity<List<SlotGameView>> response = slotsStatsController.getAllGames();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).user());
        assertEquals(2L, response.getBody().get(1).user());
        verify(slotGameHistoryHandler).getAllGames();
        verifyNoInteractions(slotStatsHandler);
    }

    @Test
    void getGameReturnsOkWhenGameExists() {
        Long gameId = 5L;
        SlotGame game = createSlotGame(1L, BigDecimal.TEN, true, BigDecimal.ZERO, 1);

        when(slotGameHistoryHandler.getGame(gameId)).thenReturn(Optional.of(game));

        ResponseEntity<SlotGameView> response = slotsStatsController.getGame(gameId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().user());
        assertEquals(BigDecimal.TEN, response.getBody().betAmount());
        verify(slotGameHistoryHandler).getGame(gameId);
        verifyNoInteractions(slotStatsHandler);
    }

    @Test
    void getGameReturnsNotFoundWhenGameDoesNotExist() {
        Long gameId = 99L;

        when(slotGameHistoryHandler.getGame(gameId)).thenReturn(Optional.empty());

        ResponseEntity<SlotGameView> response = slotsStatsController.getGame(gameId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(slotGameHistoryHandler).getGame(gameId);
        verifyNoInteractions(slotStatsHandler);
    }

    @Test
    void getGameReturnsBadRequestForInvalidGameId() {
        ResponseEntity<SlotGameView> response = slotsStatsController.getGame(0L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verifyNoInteractions(slotStatsHandler, slotGameHistoryHandler);
    }

    @Test
    void deleteGameReturnsOkWhenGameExists() {
        Long gameId = 5L;
        SlotGame game = createSlotGame(1L, BigDecimal.TEN, false, BigDecimal.TEN.negate(), 0);

        when(slotGameHistoryHandler.deleteGame(gameId)).thenReturn(Optional.of(game));

        ResponseEntity<SlotGameView> response = slotsStatsController.deleteGame(gameId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().user());
        assertEquals(BigDecimal.TEN.negate(), response.getBody().amount());
        verify(slotGameHistoryHandler).deleteGame(gameId);
        verifyNoInteractions(slotStatsHandler);
    }

    @Test
    void deleteGameReturnsNotFoundWhenGameDoesNotExist() {
        Long gameId = 99L;

        when(slotGameHistoryHandler.deleteGame(gameId)).thenReturn(Optional.empty());

        ResponseEntity<SlotGameView> response = slotsStatsController.deleteGame(gameId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(slotGameHistoryHandler).deleteGame(gameId);
        verifyNoInteractions(slotStatsHandler);
    }

    @Test
    void deleteGameReturnsBadRequestForInvalidGameId() {
        ResponseEntity<SlotGameView> response = slotsStatsController.deleteGame(0L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verifyNoInteractions(slotStatsHandler, slotGameHistoryHandler);
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
