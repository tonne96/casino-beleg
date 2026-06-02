package beleg.slotsservice.handler.game;

import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.repository.SlotGameRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Handler = Historien-Logik fuer gespeicherte Slot-Runden.
 *
 * Diese Klasse trennt Datenbankoperationen von Controller und reiner Spiellogik.
 */
@Service
public class SlotGameHistoryHandler {

    private final SlotGameRepository slotGameRepository;

    public SlotGameHistoryHandler(SlotGameRepository slotGameRepository) {
        this.slotGameRepository = slotGameRepository;
    }

    /**
     * Speichert eine gespielte Slot-Runde in der Slots-Datenbank.
     */
    public SlotGame saveGame(Long userId, BigDecimal betAmount, SlotGameResult result) {
        SlotGame slotGame = new SlotGame(userId, betAmount, result);
        return slotGameRepository.save(slotGame);
    }

    /**
     * Liefert alle gespeicherten Slot-Runden.
     */
    public List<SlotGame> getAllGames() {
        return slotGameRepository.findAll();
    }

    /**
     * Liefert alle gespeicherten Slot-Runden eines Users.
     */
    public List<SlotGame> getGamesByUser(Long userId) {
        return slotGameRepository.findByUserId(userId);
    }

    /**
     * Liefert eine einzelne gespeicherte Slot-Runde.
     */
    public Optional<SlotGame> getGame(Long gameId) {
        return slotGameRepository.findById(gameId);
    }

    /**
     * Loescht eine gespeicherte Slot-Runde.
     */
    public Optional<SlotGame> deleteGame(Long gameId) {
        return slotGameRepository.findById(gameId).map(slotGame -> {
            slotGameRepository.delete(slotGame);
            return slotGame;
        });
    }
}
