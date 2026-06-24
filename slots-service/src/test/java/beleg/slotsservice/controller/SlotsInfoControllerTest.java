package beleg.slotsservice.controller;

import beleg.slotsservice.handler.info.ISlotInfoHandler;
import beleg.slotsservice.model.SlotSymbol;
import beleg.slotsservice.view.SlotsChancesView;
import beleg.slotsservice.view.SlotsRulesView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests fuer den Info-Controller.
 *
 * Der InfoHandler wird gemockt. Dadurch pruefen wir nur, ob der Controller
 * die Info-Ergebnisse korrekt als HTTP 200 zurueckgibt.
 */
class SlotsInfoControllerTest {

    private ISlotInfoHandler slotInfoHandler;
    private SlotsInfoController slotsInfoController;

    @BeforeEach
    void setUp() {
        slotInfoHandler = mock(ISlotInfoHandler.class, withSettings().mockMaker(MockMakers.PROXY));
        slotsInfoController = new SlotsInfoController(slotInfoHandler);
    }

    @Test
    void getRulesReturnsOk() {
        SlotsRulesView rules = new SlotsRulesView(
                3,
                List.of(SlotSymbol.CHERRY, SlotSymbol.LEMON, SlotSymbol.BELL, SlotSymbol.BAR, SlotSymbol.SEVEN),
                List.of("Eine Slot-Runde besteht aus genau drei Walzen."),
                List.of()
        );

        when(slotInfoHandler.getRules()).thenReturn(rules);

        ResponseEntity<SlotsRulesView> response = slotsInfoController.getRules();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(rules, response.getBody());
        verify(slotInfoHandler).getRules();
    }

    @Test
    void getChancesReturnsOk() {
        SlotsChancesView chances = new SlotsChancesView(
                3,
                5,
                125,
                List.of(),
                "Test-Notiz"
        );

        when(slotInfoHandler.getChances()).thenReturn(chances);

        ResponseEntity<SlotsChancesView> response = slotsInfoController.getChances();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(chances, response.getBody());
        verify(slotInfoHandler).getChances();
    }
}
