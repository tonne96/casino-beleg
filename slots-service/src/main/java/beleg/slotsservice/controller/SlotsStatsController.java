package beleg.slotsservice.controller;

import beleg.slotsservice.handler.game.SlotGameHistoryHandler;
import beleg.slotsservice.handler.stats.SlotStatsHandler;
import beleg.slotsservice.view.SlotGameView;
import beleg.slotsservice.view.SlotsStatsView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller fuer gespeicherte Slot-Runden.
 *
 * Diese Endpunkte machen die Spielhistorie ueber Swagger/Clients abrufbar.
 */
@RestController
@RequestMapping("/casino/slots/api")
public class SlotsStatsController {

    private final SlotGameHistoryHandler slotGameHistoryHandler;
    private final SlotStatsHandler slotStatsHandler;

    public SlotsStatsController(SlotGameHistoryHandler slotGameHistoryHandler, SlotStatsHandler slotStatsHandler) {
        this.slotGameHistoryHandler = slotGameHistoryHandler;
        this.slotStatsHandler = slotStatsHandler;
    }

    /**
     * GET /casino/slots/api/stats
     *
     * Liefert eine zusammengefasste Statistik ueber alle gespeicherten Slot-Runden.
     */
    @GetMapping("/stats")
    public ResponseEntity<SlotsStatsView> getStats() {
        return ResponseEntity.ok(slotStatsHandler.getStats());
    }

    /**
     * GET /casino/slots/api/stats/games
     *
     * Liefert alle gespeicherten Slot-Runden.
     */
    @GetMapping("/stats/games")
    public ResponseEntity<List<SlotGameView>> getAllGames() {
        List<SlotGameView> games = slotGameHistoryHandler.getAllGames()
                .stream()
                .map(SlotGameView::from)
                .toList();

        return ResponseEntity.ok(games);
    }

    /**
     * GET /casino/slots/api/stats/user/{user_id}
     *
     * Liefert alle gespeicherten Slot-Runden eines Users.
     */
    @GetMapping("/stats/user/{user_id}")
    public ResponseEntity<List<SlotGameView>> getGamesByUser(@PathVariable Long user_id) {
        if (user_id == null || user_id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        List<SlotGameView> games = slotGameHistoryHandler.getGamesByUser(user_id)
                .stream()
                .map(SlotGameView::from)
                .toList();

        return ResponseEntity.ok(games);
    }

    /**
     * GET /casino/slots/api/stat/{game_id}
     *
     * Liefert eine einzelne gespeicherte Slot-Runde.
     */
    @GetMapping("/stat/{game_id}")
    public ResponseEntity<SlotGameView> getGame(@PathVariable Long game_id) {
        if (game_id == null || game_id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        return slotGameHistoryHandler.getGame(game_id)
                .map(game -> ResponseEntity.ok(SlotGameView.from(game)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /casino/slots/api/stat/{game_id}
     *
     * Loescht eine gespeicherte Slot-Runde und gibt sie zur Kontrolle zurueck.
     */
    @DeleteMapping("/stat/{game_id}")
    public ResponseEntity<SlotGameView> deleteGame(@PathVariable Long game_id) {
        if (game_id == null || game_id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        return slotGameHistoryHandler.deleteGame(game_id)
                .map(game -> ResponseEntity.ok(SlotGameView.from(game)))
                .orElse(ResponseEntity.notFound().build());
    }
}
