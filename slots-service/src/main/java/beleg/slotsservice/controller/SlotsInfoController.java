package beleg.slotsservice.controller;

import beleg.slotsservice.handler.info.ISlotInfoHandler;
import beleg.slotsservice.view.SlotsChancesView;
import beleg.slotsservice.view.SlotsRulesView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Liefert Spielregeln und Gewinnchancen unabhängig von Banking und Datenbank.
 */
@RestController
@RequestMapping("/casino/slots/api/info")
public class SlotsInfoController {

    private final ISlotInfoHandler slotInfoHandler;

    public SlotsInfoController(ISlotInfoHandler slotInfoHandler) {
        this.slotInfoHandler = slotInfoHandler;
    }

    @GetMapping("/rules")
    public ResponseEntity<SlotsRulesView> getRules() {
        return ResponseEntity.ok(slotInfoHandler.getRules());
    }

    @GetMapping("/chances")
    public ResponseEntity<SlotsChancesView> getChances() {
        return ResponseEntity.ok(slotInfoHandler.getChances());
    }
}
