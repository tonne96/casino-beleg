package beleg.slotsservice.controller;

import beleg.slotsservice.handler.game.SlotGameHistoryHandler;
import beleg.slotsservice.handler.game.SlotGameHandler;
import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.view.PlaySlotsRequest;
import beleg.slotsservice.view.SlotsPlayView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller nimmt HTTP-Requests von aussen entgegen und gibt HTTP-Responses zurueck.
 *
 * Aktuell spielt dieser Controller die Slot-Runde und speichert sie in der Slots-DB.
 * Der Banking-Service wird spaeter angebunden.
 */
@RestController
@RequestMapping("/casino/slots/api")
public class SlotsController {

    private final SlotGameHandler slotGameHandler;
    private final SlotGameHistoryHandler slotGameHistoryHandler;

    public SlotsController(SlotGameHandler slotGameHandler, SlotGameHistoryHandler slotGameHistoryHandler) {
        this.slotGameHandler = slotGameHandler;
        this.slotGameHistoryHandler = slotGameHistoryHandler;
    }

    /**
     * POST /casino/slots/api/play
     *
     * Spielt eine einzelne zustandslose Slot-Runde:
     * Request rein, Runde auswerten, Ergebnis speichern, Ergebnis raus.
     */
    @PostMapping("/play")
    public ResponseEntity<SlotsPlayView> play(@RequestBody PlaySlotsRequest request) {
        try {
            if (request == null) {
                throw new IllegalArgumentException("Request darf nicht null sein.");
            }
            request.validate();

            SlotGameResult result = slotGameHandler.play(request.betAmount());
            SlotGame savedGame = slotGameHistoryHandler.saveGame(request.user(), request.betAmount(), result);
            SlotsPlayView response = SlotsPlayView.from(savedGame);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
