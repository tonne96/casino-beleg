package beleg.slotsservice.handler.game;

import beleg.slotsservice.factory.ISlotGameFactory;
import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.repository.SlotGameRepository;
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

    private final SlotGameRepository slotGameRepository;
    private final ISlotGameFactory slotGameFactory;

    public SlotGameHistoryHandler(SlotGameRepository slotGameRepository, ISlotGameFactory slotGameFactory) {
        this.slotGameRepository = slotGameRepository;
        this.slotGameFactory = slotGameFactory;
    }

    /**
     * Speichert eine gespielte Slot-Runde in der Slots-Datenbank.
     */
    @Override
    public SlotGame saveGame(Long userId, BigDecimal betAmount, SlotGameResult result) {
        SlotGame slotGame = slotGameFactory.create(userId, betAmount, result);
        return slotGameRepository.save(slotGame);
    }

    /**
     * Liefert alle gespeicherten Slot-Runden.
     */
    @Override
    public List<SlotGame> getAllGames() {
        return slotGameRepository.findAll();
    }

    /**
     * Liefert alle gespeicherten Slot-Runden eines Users.
     */
    @Override
    public List<SlotGame> getGamesByUser(Long userId) {
        return slotGameRepository.findByUserId(userId);
    }

    /**
     * Liefert eine einzelne gespeicherte Slot-Runde.
     */
    @Override
    public Optional<SlotGame> getGame(Long gameId) {
        return slotGameRepository.findById(gameId);
    }

    /**
     * Loescht eine gespeicherte Slot-Runde.
     */
    @Override
    public Optional<SlotGame> deleteGame(Long gameId) {
        return slotGameRepository.findById(gameId).map(slotGame -> {
            slotGameRepository.delete(slotGame);
            return slotGame;
        });
    }
}
