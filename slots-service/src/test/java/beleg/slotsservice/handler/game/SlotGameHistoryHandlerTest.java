package beleg.slotsservice.handler.game;

import beleg.slotsservice.factory.ISlotGameFactory;
import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.model.SlotSymbol;
import beleg.slotsservice.repository.IGameResultRepository;
import org.mockito.MockMakers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests fuer die Historien-Logik.
 *
 * Repository und Factory werden gemockt, damit nur der Ablauf im Handler
 * getestet wird.
 */
class SlotGameHistoryHandlerTest {

    private IGameResultRepository gameResultRepository;
    private ISlotGameFactory slotGameFactory;
    private SlotGameHistoryHandler slotGameHistoryHandler;

    @BeforeEach
    void setUp() {
        gameResultRepository = mock(IGameResultRepository.class, withSettings().mockMaker(MockMakers.PROXY));
        slotGameFactory = mock(ISlotGameFactory.class, withSettings().mockMaker(MockMakers.PROXY));
        slotGameHistoryHandler = new SlotGameHistoryHandler(gameResultRepository, slotGameFactory);
    }

    @Test
    void saveGameCreatesSlotGameWithFactoryAndSavesIt() {
        Long userId = 1L;
        BigDecimal betAmount = BigDecimal.TEN;
        SlotGameResult result = new SlotGameResult(
                true,
                BigDecimal.valueOf(20),
                List.of(SlotSymbol.BAR, SlotSymbol.BAR, SlotSymbol.BAR),
                3
        );
        SlotGame createdGame = createSlotGame(userId, betAmount, true, BigDecimal.valueOf(20), 3);
        SlotGame savedGame = createSlotGame(userId, betAmount, true, BigDecimal.valueOf(20), 3);

        when(slotGameFactory.create(userId, betAmount, result)).thenReturn(createdGame);
        when(gameResultRepository.save(createdGame)).thenReturn(savedGame);

        SlotGame returnedGame = slotGameHistoryHandler.saveGame(userId, betAmount, result);

        assertSame(savedGame, returnedGame);
        verify(slotGameFactory).create(userId, betAmount, result);
        verify(gameResultRepository).save(createdGame);
    }

    @Test
    void getAllGamesUsesRepositoryFindAll() {
        List<SlotGame> games = List.of(
                createSlotGame(1L, BigDecimal.TEN, false, BigDecimal.TEN.negate(), 0)
        );

        when(gameResultRepository.findAll()).thenReturn(games);

        List<SlotGame> result = slotGameHistoryHandler.getAllGames();

        assertSame(games, result);
        verify(gameResultRepository).findAll();
    }

    @Test
    void getGamesByUserUsesRepositoryFindByUserId() {
        Long userId = 1L;
        List<SlotGame> games = List.of(
                createSlotGame(userId, BigDecimal.TEN, true, BigDecimal.ZERO, 1)
        );

        when(gameResultRepository.findByUserId(userId)).thenReturn(games);

        List<SlotGame> result = slotGameHistoryHandler.getGamesByUser(userId);

        assertSame(games, result);
        verify(gameResultRepository).findByUserId(userId);
    }

    @Test
    void getGameUsesRepositoryFindById() {
        Long gameId = 5L;
        SlotGame slotGame = createSlotGame(1L, BigDecimal.TEN, true, BigDecimal.ZERO, 1);
        Optional<SlotGame> foundGame = Optional.of(slotGame);

        when(gameResultRepository.findById(gameId)).thenReturn(foundGame);

        Optional<SlotGame> result = slotGameHistoryHandler.getGame(gameId);

        assertSame(foundGame, result);
        verify(gameResultRepository).findById(gameId);
    }

    @Test
    void deleteGameDeletesAndReturnsGameWhenFound() {
        Long gameId = 5L;
        SlotGame slotGame = createSlotGame(1L, BigDecimal.TEN, true, BigDecimal.ZERO, 1);

        when(gameResultRepository.findById(gameId)).thenReturn(Optional.of(slotGame));

        Optional<SlotGame> result = slotGameHistoryHandler.deleteGame(gameId);

        assertTrue(result.isPresent());
        assertSame(slotGame, result.get());
        verify(gameResultRepository).findById(gameId);
        verify(gameResultRepository).delete(slotGame);
    }

    @Test
    void deleteGameReturnsEmptyWhenGameDoesNotExist() {
        Long gameId = 99L;

        when(gameResultRepository.findById(gameId)).thenReturn(Optional.empty());

        Optional<SlotGame> result = slotGameHistoryHandler.deleteGame(gameId);

        assertTrue(result.isEmpty());
        verify(gameResultRepository).findById(gameId);
        verify(gameResultRepository, never()).delete(any());
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
