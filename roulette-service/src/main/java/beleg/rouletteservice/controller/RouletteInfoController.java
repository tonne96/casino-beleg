package beleg.rouletteservice.controller;

import beleg.rouletteservice.handler.info.IRouletteInfoHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/casino/roulette/api/info")
public class RouletteInfoController {

    private final IRouletteInfoHandler infoService;

    public RouletteInfoController(IRouletteInfoHandler infoService) {
        this.infoService = infoService;
    }

    @GetMapping("/rules")
    public ResponseEntity<String> getRules() {
        String rulesText = infoService.getRules();
        return ResponseEntity.status(HttpStatus.OK).body(rulesText);
    }

    @GetMapping("/chances")
    public ResponseEntity<String> getChances() {
        String chancesText = infoService.getChances();
        return ResponseEntity.status(HttpStatus.OK).body(chancesText);
    }
}