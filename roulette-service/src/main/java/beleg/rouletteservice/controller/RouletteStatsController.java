package beleg.rouletteservice.controller;

import beleg.rouletteservice.handler.stats.IRouletteStatsHandler;
import beleg.rouletteservice.view.response.GameStatDto;
import beleg.rouletteservice.view.response.GlobalStatsResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/casino/roulette/api")
public class RouletteStatsController {

    private final IRouletteStatsHandler statsService;
    private final FailureResponseMapper responseMapper;

    public RouletteStatsController(IRouletteStatsHandler statsService,
                                   FailureResponseMapper responseMapper) {
        this.statsService = statsService;
        this.responseMapper = responseMapper;
    }

    @GetMapping("/stats")
    public ResponseEntity<GlobalStatsResponseDto> getGlobalStats() {
        return ResponseEntity.status(HttpStatus.OK).body(statsService.getGlobalStats());
    }

    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<Object> getUserStats(@PathVariable("userId") Long userId) {
        return responseMapper.toResponse(statsService.getUserStats(userId));
    }

    @GetMapping("/stats/games")
    public ResponseEntity<List<GameStatDto>> getAllGameStats() {
        return ResponseEntity.status(HttpStatus.OK).body(statsService.getAllGameStats());
    }

    @GetMapping("/stat/{gameId}")
    public ResponseEntity<Object> getGameStat(@PathVariable("gameId") Long gameId) {
        return responseMapper.toResponse(statsService.getGameStat(gameId));
    }

    @DeleteMapping("/stat/{gameId}")
    public ResponseEntity<Object> deleteGameStat(@PathVariable("gameId") Long gameId) {
        return responseMapper.toResponse(statsService.deleteGameStat(gameId));
    }
}