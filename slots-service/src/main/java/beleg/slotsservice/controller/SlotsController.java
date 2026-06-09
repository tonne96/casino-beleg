package beleg.slotsservice.controller;

import beleg.slotsservice.client.IBankingClient;
import beleg.slotsservice.client.BankingUserView;
import beleg.slotsservice.exception.BankingCommunicationException;
import beleg.slotsservice.exception.BankingUserNotFoundException;
import beleg.slotsservice.exception.InsufficientBalanceException;
import beleg.slotsservice.handler.game.ISlotGameHandler;
import beleg.slotsservice.handler.game.ISlotGameHistoryHandler;
import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.view.PlaySlotsRequest;
import beleg.slotsservice.view.SlotsPlayView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controller nimmt HTTP-Requests von aussen entgegen und gibt HTTP-Responses zurueck.
 *
 * Dieser Controller koordiniert die komplette Slot-Runde:
 * Banking pruefen, Runde spielen, Banking buchen, Ergebnis speichern.
 */
@RestController
@RequestMapping("/casino/slots/api")
public class SlotsController {

    private final ISlotGameHandler slotGameHandler;
    private final ISlotGameHistoryHandler slotGameHistoryHandler;
    private final IBankingClient bankingClient;

    public SlotsController(
            ISlotGameHandler slotGameHandler,
            ISlotGameHistoryHandler slotGameHistoryHandler,
            IBankingClient bankingClient) {
        this.slotGameHandler = slotGameHandler;
        this.slotGameHistoryHandler = slotGameHistoryHandler;
        this.bankingClient = bankingClient;
    }

    /**
     * POST /casino/slots/api/play
     *
     * Spielt eine einzelne zustandslose Slot-Runde:
     * Request rein, User/Guthaben pruefen, Runde auswerten, Banking buchen,
     * Ergebnis speichern, Ergebnis raus.
     */
    @PostMapping("/play")
    public ResponseEntity<SlotsPlayView> play(@RequestBody PlaySlotsRequest request) {
        try {
            if (request == null) {
                throw new IllegalArgumentException("Request darf nicht null sein.");
            }
            request.validate();

            BankingUserView user = bankingClient.getUser(request.user());
            ensureEnoughBalance(user, request.betAmount());

            SlotGameResult result = slotGameHandler.play(request.betAmount());

            // Erst Banking buchen: Wenn Banking ablehnt, wird keine Slot-Runde gespeichert.
            bankingClient.createSlotsTransaction(request.user(), result.amount());

            SlotGame savedGame = slotGameHistoryHandler.saveGame(request.user(), request.betAmount(), result);
            SlotsPlayView response = SlotsPlayView.from(savedGame);

            return ResponseEntity.ok(response);
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

    /**
     * Der Slots-Service verhindert aktuell selbst, dass ein User mehr setzt,
     * als er laut Banking-Service auf dem Konto hat.
     */
    private void ensureEnoughBalance(BankingUserView user, BigDecimal betAmount) {
        if (user == null || user.balance() == null) {
            throw new BankingCommunicationException("Banking-Service hat keinen gueltigen User geliefert.", null);
        }
        if (user.balance().compareTo(betAmount) < 0) {
            throw new InsufficientBalanceException(user.id(), user.balance(), betAmount);
        }
    }
}
