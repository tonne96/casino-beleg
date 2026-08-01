package beleg.rouletteservice.controller;


import beleg.rouletteservice.handler.stats.RouletteStatsHandler;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.Result;
import beleg.rouletteservice.view.response.GameStatDto;
import beleg.rouletteservice.view.response.GlobalStatsResponseDto;
import beleg.rouletteservice.view.response.UserStatsResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(RouletteStatsController.class);

    private final RouletteStatsHandler statsService;

    public RouletteStatsController(RouletteStatsHandler statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/stats")
    public ResponseEntity<GlobalStatsResponseDto> getGlobalStats() {
        GlobalStatsResponseDto globalStats = statsService.getGlobalStats();
        return ResponseEntity.status(HttpStatus.OK).body(globalStats);
    }

    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<Object> getUserStats(@PathVariable("userId") Long userId) {
        Result<UserStatsResponseDto, Failures> result = statsService.getUserStats(userId);

        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(result.getValue());
        }

        return buildErrorResponse(result.getMessage());
    }

    @GetMapping("/stats/games")
    public ResponseEntity<List<GameStatDto>> getAllGameStats() {
        List<GameStatDto> gameStats = statsService.getAllGameStats();
        return ResponseEntity.status(HttpStatus.OK).body(gameStats);
    }

    @GetMapping("/stat/{gameId}")
    public ResponseEntity<Object> getGameStat(@PathVariable("gameId") Long gameId) {
        Result<GameStatDto, Failures> result = statsService.getGameStat(gameId);

        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(result.getValue());
        }

        return buildErrorResponse(result.getMessage());
    }

    @DeleteMapping("/stat/{gameId}")
    public ResponseEntity<Object> deleteGameStat(@PathVariable("gameId") Long gameId) {
        Result<GameStatDto, Failures> result = statsService.deleteGameStat(gameId);

        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(result.getValue());
        }

        return buildErrorResponse(result.getMessage());
    }

    private ResponseEntity<Object> buildErrorResponse(Failures failure) {
        HttpStatus status = switch (failure) {
            case USER_NOT_FOUND, GAME_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case NOT_NULL, OUT_OF_RANGE -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        log.warn("Fehlerhafter Statistik-Aufruf: {}", failure);
        return ResponseEntity.status(status).body(failure);
    }
}