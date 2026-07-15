package beleg.slotsservice.handler.play;

import beleg.slotsservice.client.BankingUserView;
import beleg.slotsservice.client.IBankingClient;
import beleg.slotsservice.exception.BankingCommunicationException;
import beleg.slotsservice.exception.InsufficientBalanceException;
import beleg.slotsservice.handler.game.ISlotGameHandler;
import beleg.slotsservice.handler.game.ISlotGameHistoryHandler;
import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.view.PlaySlotsRequest;
import beleg.slotsservice.view.SlotsPlayView;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Handler fuer den kompletten Ablauf einer Slot-Runde.
 *
 * Diese Klasse entlastet den Controller:
 * User pruefen, Guthaben pruefen, Spiel ausfuehren, Banking buchen
 * und Ergebnis speichern passieren hier.
 */
@Service
public class PlaySlotsHandler implements IPlaySlotsHandler {

    private final ISlotGameHandler slotGameHandler;
    private final ISlotGameHistoryHandler slotGameHistoryHandler;
    private final IBankingClient bankingClient;

    public PlaySlotsHandler(
            ISlotGameHandler slotGameHandler,
            ISlotGameHistoryHandler slotGameHistoryHandler,
            IBankingClient bankingClient) {
        this.slotGameHandler = slotGameHandler;
        this.slotGameHistoryHandler = slotGameHistoryHandler;
        this.bankingClient = bankingClient;
    }

    /**
     * Fuehrt eine zustandslose Slot-Runde aus.
     *
     * Wichtig: Erst wird beim Banking-Service gebucht. Wenn Banking ablehnt,
     * wird keine Slot-Runde in der Slots-Datenbank gespeichert.
     */
    @Override
    public SlotsPlayView play(PlaySlotsRequest request) {
        validateRequest(request);

        BankingUserView user = bankingClient.getUser(request.user());
        ensureEnoughBalance(user, request.betAmount());

        SlotGameResult result = slotGameHandler.play(request.betAmount());

        bankingClient.createSlotsTransaction(request.user(), result.amount());

        SlotGame savedGame = saveGameOrCompensateBanking(request.user(), request.betAmount(), result);
        return SlotsPlayView.from(savedGame);
    }

    /**
     * Speichert die Runde nach der Banking-Buchung.
     *
     * Da Banking- und Slots-Datenbank keine gemeinsame Transaktion besitzen,
     * wird eine erfolgreiche Banking-Buchung bei einem Speicherfehler durch
     * den exakten Gegenbetrag kompensiert (Saga/Compensating Transaction).
     */
    private SlotGame saveGameOrCompensateBanking(
            Long userId,
            BigDecimal betAmount,
            SlotGameResult result) {
        try {
            return slotGameHistoryHandler.saveGame(userId, betAmount, result);
        } catch (RuntimeException saveException) {
            compensateBanking(userId, result.amount(), saveException);
            throw saveException;
        }
    }

    private void compensateBanking(Long userId, BigDecimal bookedAmount, RuntimeException saveException) {
        try {
            bankingClient.createSlotsTransaction(userId, bookedAmount.negate());
        } catch (RuntimeException compensationException) {
            // Der Speicherfehler bleibt die Hauptursache; der fehlgeschlagene
            // Kompensationsversuch geht trotzdem nicht in der Fehlerkette verloren.
            saveException.addSuppressed(compensationException);
        }
    }

    private void validateRequest(PlaySlotsRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request darf nicht null sein.");
        }
        request.validate();
    }

    /**
     * Der Slots-Service verhindert selbst, dass ein User mehr setzt,
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
