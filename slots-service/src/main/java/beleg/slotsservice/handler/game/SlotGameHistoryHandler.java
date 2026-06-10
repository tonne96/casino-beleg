package beleg.slotsservice.handler.game;

import beleg.slotsservice.factory.ISlotGameFactory;
import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.repository.IGameResultRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * SlotGameHistoryHandler = Implementierung fuer gespeicherte Slot-Runden.
 *
 * Diese Klasse trennt Datenbankoperationen von Controller und reiner Spiellogik.
 */
@Service
public class SlotGameHistoryHandler implements ISlotGameHistoryHandler {

    private final IGameResultRepository gameResultRepository;
    private final ISlotGameFactory slotGameFactory;

    public SlotGameHistoryHandler(IGameResultRepository gameResultRepository, ISlotGameFactory slotGameFactory) {
        this.gameResultRepository = gameResultRepository;
        this.slotGameFactory = slotGameFactory;
    }

    /**
     * Speichert eine gespielte Slot-Runde in der Slots-Datenbank.
     */
    @Override
    public SlotGame saveGame(Long userId, BigDecimal betAmount, SlotGameResult result) {
        SlotGame slotGame = slotGameFactory.create(userId, betAmount, result);
        return gameResultRepository.save(slotGame);
    }

    /**
     * Liefert alle gespeicherten Slot-Runden.
     */
    @Override
    public List<SlotGame> getAllGames() {
        return gameResultRepository.findAll();
    }

    /**
     * Liefert alle gespeicherten Slot-Runden eines Users.
     */
    @Override
    public List<SlotGame> getGamesByUser(Long userId) {
        return gameResultRepository.findByUserId(userId);
    }

    /**
     * Liefert eine einzelne gespeicherte Slot-Runde.
     */
    @Override
    public Optional<SlotGame> getGame(Long gameId) {
        return gameResultRepository.findById(gameId);
    }

    /**
     * Loescht eine gespeicherte Slot-Runde.
     */
    @Override
    public Optional<SlotGame> deleteGame(Long gameId) {
        return gameResultRepository.findById(gameId).map(slotGame -> {
            gameResultRepository.delete(slotGame);
            return slotGame;
        });
    }
}
