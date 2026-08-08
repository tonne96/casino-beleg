package beleg.slotsservice.handler.game;

import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotGameResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Vertrag für das Speichern und Lesen von Slot-Runden.
 */
public interface ISlotGameHistoryHandler {

    SlotGame saveGame(Long userId, BigDecimal betAmount, SlotGameResult result);

    List<SlotGame> getAllGames();

    List<SlotGame> getGamesByUser(Long userId);

    Optional<SlotGame> getGame(Long gameId);

    Optional<SlotGame> deleteGame(Long gameId);
}
