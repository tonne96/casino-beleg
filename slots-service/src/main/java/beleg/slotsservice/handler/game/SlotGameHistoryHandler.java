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
 * Kapselt Factory und Repository für gespeicherte Slot-Runden.
 */
@Service
public class SlotGameHistoryHandler implements ISlotGameHistoryHandler {

    private final IGameResultRepository gameResultRepository;
    private final ISlotGameFactory slotGameFactory;

    public SlotGameHistoryHandler(IGameResultRepository gameResultRepository, ISlotGameFactory slotGameFactory) {
        this.gameResultRepository = gameResultRepository;
        this.slotGameFactory = slotGameFactory;
    }

    @Override
    public SlotGame saveGame(Long userId, BigDecimal betAmount, SlotGameResult result) {
        SlotGame slotGame = slotGameFactory.create(userId, betAmount, result);
        return gameResultRepository.save(slotGame);
    }

    @Override
    public List<SlotGame> getAllGames() {
        return gameResultRepository.findAll();
    }

    @Override
    public List<SlotGame> getGamesByUser(Long userId) {
        return gameResultRepository.findByUserId(userId);
    }

    @Override
    public Optional<SlotGame> getGame(Long gameId) {
        return gameResultRepository.findById(gameId);
    }

    @Override
    public Optional<SlotGame> deleteGame(Long gameId) {
        Optional<SlotGame> found = gameResultRepository.findById(gameId);

        if (found.isEmpty()) {
            return Optional.empty();
        }

        SlotGame slotGame = found.get();
        gameResultRepository.delete(slotGame);
        return Optional.of(slotGame);
    }
}
