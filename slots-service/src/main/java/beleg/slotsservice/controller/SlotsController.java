package beleg.slotsservice.controller;

import beleg.slotsservice.exception.BankingCommunicationException;
import beleg.slotsservice.exception.BankingUserNotFoundException;
import beleg.slotsservice.exception.InsufficientBalanceException;
import beleg.slotsservice.handler.play.IPlaySlotsHandler;
import beleg.slotsservice.view.PlaySlotsRequest;
import beleg.slotsservice.view.SlotsPlayView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * HTTP-Einstiegspunkt für Slot-Runden.
 * Die fachliche Ablaufsteuerung liegt im PlaySlotsHandler.
 */
@RestController
@RequestMapping("/casino/slots/api")
public class SlotsController {

    private final IPlaySlotsHandler playSlotsHandler;

    public SlotsController(IPlaySlotsHandler playSlotsHandler) {
        this.playSlotsHandler = playSlotsHandler;
    }

    @PostMapping("/play")
    public ResponseEntity<SlotsPlayView> play(@RequestBody PlaySlotsRequest request) {
        try {
            return ResponseEntity.ok(playSlotsHandler.play(request));
        } catch (BankingUserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BankingCommunicationException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
