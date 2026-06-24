package beleg.slotsservice.handler.play;

import beleg.slotsservice.client.BankingUserView;
import beleg.slotsservice.client.IBankingClient;
import beleg.slotsservice.exception.BankingCommunicationException;
import beleg.slotsservice.exception.BankingUserNotFoundException;
import beleg.slotsservice.exception.InsufficientBalanceException;
import beleg.slotsservice.handler.game.ISlotGameHandler;
import beleg.slotsservice.handler.game.ISlotGameHistoryHandler;
import beleg.slotsservice.model.SlotGame;
import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.model.SlotSymbol;
import beleg.slotsservice.view.PlaySlotsRequest;
import beleg.slotsservice.view.SlotsPlayView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.MockMakers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests fuer den kompletten Play-Ablauf.
 *
 * Banking, Spiellogik und Historie werden gemockt. Dadurch pruefen wir nur,
 * ob der PlaySlotsHandler die Schritte in der richtigen Reihenfolge ausfuehrt.
 */
class PlaySlotsHandlerTest {

    private ISlotGameHandler slotGameHandler;
    private ISlotGameHistoryHandler slotGameHistoryHandler;
    private IBankingClient bankingClient;
    private PlaySlotsHandler playSlotsHandler;

    @BeforeEach
    void setUp() {
        slotGameHandler = mock(ISlotGameHandler.class, withSettings().mockMaker(MockMakers.PROXY));
        slotGameHistoryHandler = mock(ISlotGameHistoryHandler.class, withSettings().mockMaker(MockMakers.PROXY));
        bankingClient = mock(IBankingClient.class, withSettings().mockMaker(MockMakers.PROXY));
        playSlotsHandler = new PlaySlotsHandler(slotGameHandler, slotGameHistoryHandler, bankingClient);
    }

    @Test
    void playWithValidUserAndEnoughBalanceBooksBankingAndSavesGame() {
        PlaySlotsRequest request = new PlaySlotsRequest(1L, BigDecimal.TEN);
        BankingUserView user = new BankingUserView(1L, "Dat", "Tran", BigDecimal.valueOf(100));
        SlotGameResult gameResult = new SlotGameResult(
                true,
                BigDecimal.ZERO,
                List.of(SlotSymbol.CHERRY, SlotSymbol.CHERRY, SlotSymbol.BAR),
                1
        );
        SlotGame savedGame = createSlotGame(1L, BigDecimal.TEN, true, BigDecimal.ZERO, 1);

        when(bankingClient.getUser(1L)).thenReturn(user);
        when(slotGameHandler.play(BigDecimal.TEN)).thenReturn(gameResult);
        when(slotGameHistoryHandler.saveGame(1L, BigDecimal.TEN, gameResult)).thenReturn(savedGame);

        SlotsPlayView response = playSlotsHandler.play(request);

        assertEquals(1L, response.user());
        assertEquals(BigDecimal.TEN, response.betAmount());
        assertTrue(response.winning());
        assertEquals(BigDecimal.ZERO, response.amount());
        assertEquals(1, response.payoutMultiplier());

        InOrder inOrder = inOrder(bankingClient, slotGameHandler, slotGameHistoryHandler);
        inOrder.verify(bankingClient).getUser(1L);
        inOrder.verify(slotGameHandler).play(BigDecimal.TEN);
        inOrder.verify(bankingClient).createSlotsTransaction(1L, BigDecimal.ZERO);
        inOrder.verify(slotGameHistoryHandler).saveGame(1L, BigDecimal.TEN, gameResult);
    }

    @Test
    void playThrowsUserNotFoundWhenBankingDoesNotKnowUser() {
        PlaySlotsRequest request = new PlaySlotsRequest(99L, BigDecimal.TEN);
        BankingUserNotFoundException exception = new BankingUserNotFoundException(99L);

        when(bankingClient.getUser(99L)).thenThrow(exception);

        try {
            playSlotsHandler.play(request);
            fail("Unbekannte Banking-User muessen abgelehnt werden.");
        } catch (BankingUserNotFoundException result) {
            assertSame(exception, result);
        }

        verify(bankingClient).getUser(99L);
        verifyNoInteractions(slotGameHandler, slotGameHistoryHandler);
        verify(bankingClient, never()).createSlotsTransaction(anyLong(), any());
    }

    @Test
    void playThrowsCommunicationExceptionWhenBankingIsNotReachable() {
        PlaySlotsRequest request = new PlaySlotsRequest(1L, BigDecimal.TEN);
        BankingCommunicationException exception =
                new BankingCommunicationException("Banking-Service nicht erreichbar.", null);

        when(bankingClient.getUser(1L)).thenThrow(exception);

        try {
            playSlotsHandler.play(request);
            fail("Nicht erreichbares Banking muss als Fehler weitergegeben werden.");
        } catch (BankingCommunicationException result) {
            assertSame(exception, result);
        }

        verify(bankingClient).getUser(1L);
        verifyNoInteractions(slotGameHandler, slotGameHistoryHandler);
        verify(bankingClient, never()).createSlotsTransaction(anyLong(), any());
    }

    @Test
    void playWithInsufficientBalanceDoesNotPlayBookOrSaveGame() {
        PlaySlotsRequest request = new PlaySlotsRequest(1L, BigDecimal.TEN);
        BankingUserView user = new BankingUserView(1L, "Dat", "Tran", BigDecimal.valueOf(5));

        when(bankingClient.getUser(1L)).thenReturn(user);

        try {
            playSlotsHandler.play(request);
            fail("Zu wenig Guthaben muss abgelehnt werden.");
        } catch (InsufficientBalanceException e) {
            assertNotNull(e.getMessage());
        }

        verify(bankingClient).getUser(1L);
        verifyNoInteractions(slotGameHandler, slotGameHistoryHandler);
        verify(bankingClient, never()).createSlotsTransaction(anyLong(), any());
    }

    @Test
    void playDoesNotSaveGameWhenBankingRejectsTransaction() {
        PlaySlotsRequest request = new PlaySlotsRequest(1L, BigDecimal.TEN);
        BankingUserView user = new BankingUserView(1L, "Dat", "Tran", BigDecimal.valueOf(100));
        SlotGameResult gameResult = new SlotGameResult(
                false,
                BigDecimal.TEN.negate(),
                List.of(SlotSymbol.CHERRY, SlotSymbol.LEMON, SlotSymbol.BAR),
                0
        );

        when(bankingClient.getUser(1L)).thenReturn(user);
        when(slotGameHandler.play(BigDecimal.TEN)).thenReturn(gameResult);
        doThrow(new IllegalArgumentException("Banking-Service hat die Slots-Transaktion abgelehnt."))
                .when(bankingClient)
                .createSlotsTransaction(1L, BigDecimal.TEN.negate());

        try {
            playSlotsHandler.play(request);
            fail("Abgelehnte Banking-Transaktion muss als Fehler weitergegeben werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        verify(bankingClient).getUser(1L);
        verify(slotGameHandler).play(BigDecimal.TEN);
        verify(bankingClient).createSlotsTransaction(1L, BigDecimal.TEN.negate());
        verifyNoInteractions(slotGameHistoryHandler);
    }

    @Test
    void playWithInvalidRequestFailsBeforeAnyDependencyIsCalled() {
        try {
            playSlotsHandler.play(null);
            fail("Null als Request muss abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        verifyNoInteractions(bankingClient, slotGameHandler, slotGameHistoryHandler);
    }

    private SlotGame createSlotGame(
            Long userId,
            BigDecimal betAmount,
            boolean winning,
            BigDecimal amount,
            int payoutMultiplier) {
        return SlotGame.create(
                userId,
                betAmount,
                winning,
                amount,
                SlotSymbol.CHERRY,
                SlotSymbol.CHERRY,
                SlotSymbol.BAR,
                payoutMultiplier,
                LocalDateTime.now()
        );
    }
}
