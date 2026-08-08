package beleg.slotsservice.controller;

import beleg.slotsservice.handler.game.ISlotGameHistoryHandler;
import beleg.slotsservice.handler.stats.ISlotStatsHandler;
import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.view.SlotGameView;
import beleg.slotsservice.view.SlotsStatsView;
import beleg.slotsservice.view.SlotsUserStatsView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * HTTP-Zugriff auf Statistiken und gespeicherte Slot-Runden.
 */
@RestController
@RequestMapping("/casino/slots/api")
public class SlotsStatsController {

    private final ISlotGameHistoryHandler slotGameHistoryHandler;
    private final ISlotStatsHandler slotStatsHandler;

    public SlotsStatsController(ISlotGameHistoryHandler slotGameHistoryHandler, ISlotStatsHandler slotStatsHandler) {
        this.slotGameHistoryHandler = slotGameHistoryHandler;
        this.slotStatsHandler = slotStatsHandler;
    }

    @GetMapping("/stats")
    public ResponseEntity<SlotsStatsView> getStats() {
        return ResponseEntity.ok(slotStatsHandler.getStats());
    }

    @GetMapping("/stats/games")
    public ResponseEntity<List<SlotGameView>> getAllGames() {
        List<SlotGame> allGames = slotGameHistoryHandler.getAllGames();

        List<SlotGameView> games = new ArrayList<>();
        for (SlotGame game : allGames) {
            games.add(SlotGameView.from(game));
        }

        return ResponseEntity.ok(games);
    }

    @GetMapping("/stats/user/{user_id}")
    public ResponseEntity<SlotsUserStatsView> getUserStats(@PathVariable Long user_id) {
        if (user_id == null || user_id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<SlotsUserStatsView> userStats = slotStatsHandler.getUserStats(user_id);

        if (userStats.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userStats.get());
    }

    @GetMapping("/stat/{game_id}")
    public ResponseEntity<SlotGameView> getGame(@PathVariable Long game_id) {
        if (game_id == null || game_id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<SlotGame> found = slotGameHistoryHandler.getGame(game_id);

        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SlotGame game = found.get();
        SlotGameView view = SlotGameView.from(game);
        return ResponseEntity.ok(view);
    }

    @DeleteMapping("/stat/{game_id}")
    public ResponseEntity<SlotGameView> deleteGame(@PathVariable Long game_id) {
        if (game_id == null || game_id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<SlotGame> deleted = slotGameHistoryHandler.deleteGame(game_id);

        if (deleted.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SlotGame game = deleted.get();
        SlotGameView view = SlotGameView.from(game);
        return ResponseEntity.ok(view);
    }
}
