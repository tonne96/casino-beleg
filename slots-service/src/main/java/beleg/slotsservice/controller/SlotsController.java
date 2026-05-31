package beleg.slotsservice.controller;

import beleg.slotsservice.handler.game.SlotGameHandler;
import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.view.PlaySlotsRequest;
import beleg.slotsservice.view.SlotsPlayView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller nimmt HTTP-Requests von aussen entgegen und gibt HTTP-Responses zurueck.
 *
 * Aktuell spielt dieser Controller nur die Slot-Runde.
 * Banking-Service und Datenbank werden spaeter angebunden.
 */
@RestController
@RequestMapping("/casino/slots/api")
public class SlotsController {

    private final SlotGameHandler slotGameHandler;

    public SlotsController(SlotGameHandler slotGameHandler) {
        this.slotGameHandler = slotGameHandler;
    }

    /**
     * POST /casino/slots/api/play
     *
     * Spielt eine einzelne zustandslose Slot-Runde:
     * Request rein, Runde auswerten, Ergebnis raus.
     */
    @PostMapping("/play")
    public ResponseEntity<SlotsPlayView> play(@RequestBody PlaySlotsRequest request) {
        try {
            if (request == null) {
                throw new IllegalArgumentException("Request darf nicht null sein.");
            }
            request.validate();

            SlotGameResult result = slotGameHandler.play(request.betAmount());
            SlotsPlayView response = SlotsPlayView.from(request.user(), request.betAmount(), result);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
