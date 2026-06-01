package beleg.slotsservice.controller;

import beleg.slotsservice.handler.info.SlotInfoHandler;
import beleg.slotsservice.view.SlotsChancesView;
import beleg.slotsservice.view.SlotsRulesView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller fuer reine Informations-Endpunkte.
 *
 * Diese Endpunkte sind unabhaengig von Banking und Datenbank.
 */
@RestController
@RequestMapping("/casino/slots/api/info")
public class SlotsInfoController {

    private final SlotInfoHandler slotInfoHandler;

    public SlotsInfoController(SlotInfoHandler slotInfoHandler) {
        this.slotInfoHandler = slotInfoHandler;
    }

    /**
     * GET /casino/slots/api/info/rules
     *
     * Liefert die Spielregeln und Auszahlungsregeln.
     */
    @GetMapping("/rules")
    public ResponseEntity<SlotsRulesView> getRules() {
        return ResponseEntity.ok(slotInfoHandler.getRules());
    }

    /**
     * GET /casino/slots/api/info/chances
     *
     * Liefert die Wahrscheinlichkeiten fuer die aktuellen Slot-Regeln.
     */
    @GetMapping("/chances")
    public ResponseEntity<SlotsChancesView> getChances() {
        return ResponseEntity.ok(slotInfoHandler.getChances());
    }
}
