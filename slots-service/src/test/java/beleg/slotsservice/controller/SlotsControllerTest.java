package beleg.slotsservice.controller;

import beleg.slotsservice.exception.BankingCommunicationException;
import beleg.slotsservice.exception.BankingUserNotFoundException;
import beleg.slotsservice.exception.InsufficientBalanceException;
import beleg.slotsservice.handler.play.IPlaySlotsHandler;
import beleg.slotsservice.model.SlotSymbol;
import beleg.slotsservice.view.PlaySlotsRequest;
import beleg.slotsservice.view.SlotsPlayView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests fuer den Play-Controller.
 *
 * Der PlaySlotsHandler wird gemockt. Dadurch testen wir hier nur,
 * ob der Controller die richtigen HTTP-Statuscodes zurueckgibt.
 */
class SlotsControllerTest {

    private IPlaySlotsHandler playSlotsHandler;
    private SlotsController slotsController;

    @BeforeEach
    void setUp() {
        playSlotsHandler = mock(IPlaySlotsHandler.class, withSettings().mockMaker(MockMakers.PROXY));
        slotsController = new SlotsController(playSlotsHandler);
    }

    @Test
    void playReturnsOkForSuccessfulRequest() {
        PlaySlotsRequest request = new PlaySlotsRequest(1L, BigDecimal.TEN);
        SlotsPlayView view = createSlotsPlayView();

        when(playSlotsHandler.play(request)).thenReturn(view);

        ResponseEntity<SlotsPlayView> response = slotsController.play(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(view, response.getBody());
        verify(playSlotsHandler).play(request);
    }

    @Test
    void playReturnsNotFoundWhenUserDoesNotExist() {
        PlaySlotsRequest request = new PlaySlotsRequest(99L, BigDecimal.TEN);

        when(playSlotsHandler.play(request)).thenThrow(new BankingUserNotFoundException(99L));

        ResponseEntity<SlotsPlayView> response = slotsController.play(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(playSlotsHandler).play(request);
    }

    @Test
    void playReturnsServiceUnavailableWhenBankingIsDown() {
        PlaySlotsRequest request = new PlaySlotsRequest(1L, BigDecimal.TEN);

        when(playSlotsHandler.play(request))
                .thenThrow(new BankingCommunicationException("Banking-Service nicht erreichbar.", null));

        ResponseEntity<SlotsPlayView> response = slotsController.play(request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNull(response.getBody());
        verify(playSlotsHandler).play(request);
    }

    @Test
    void playReturnsBadRequestForInvalidRequest() {
        PlaySlotsRequest request = new PlaySlotsRequest(null, BigDecimal.TEN);

        when(playSlotsHandler.play(request)).thenThrow(new IllegalArgumentException("User darf nicht null sein."));

        ResponseEntity<SlotsPlayView> response = slotsController.play(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(playSlotsHandler).play(request);
    }

    @Test
    void playReturnsBadRequestWhenBalanceIsTooLow() {
        PlaySlotsRequest request = new PlaySlotsRequest(1L, BigDecimal.TEN);

        when(playSlotsHandler.play(request))
                .thenThrow(new InsufficientBalanceException(1L, BigDecimal.valueOf(5), BigDecimal.TEN));

        ResponseEntity<SlotsPlayView> response = slotsController.play(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(playSlotsHandler).play(request);
    }

    private SlotsPlayView createSlotsPlayView() {
        return new SlotsPlayView(
                1L,
                1L,
                BigDecimal.TEN,
                true,
                BigDecimal.ZERO,
                List.of(SlotSymbol.CHERRY, SlotSymbol.CHERRY, SlotSymbol.BAR),
                1,
                LocalDateTime.now()
        );
    }
}
