package beleg.slotsservice.handler.game;

import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotGameResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interface fuer gespeicherte Slot-Runden.
 *
 * Die Controller kennen nur diese Methoden, nicht die konkrete Datenbank-Implementierung.
 */
public interface ISlotGameHistoryHandler {

    /**
     * Speichert eine gespielte Slot-Runde.
     */
    SlotGame saveGame(Long userId, BigDecimal betAmount, SlotGameResult result);

    /**
     * Liefert alle gespeicherten Slot-Runden.
     */
    List<SlotGame> getAllGames();

    /**
     * Liefert alle gespeicherten Slot-Runden eines Users.
     */
    List<SlotGame> getGamesByUser(Long userId);

    /**
     * Liefert eine einzelne gespeicherte Slot-Runde.
     */
    Optional<SlotGame> getGame(Long gameId);

    /**
     * Loescht eine gespeicherte Slot-Runde.
     */
    Optional<SlotGame> deleteGame(Long gameId);
}
