package beleg.rouletteservice.handler.stats;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.Result;
import beleg.rouletteservice.view.response.GameStatDto;
import beleg.rouletteservice.view.response.GlobalStatsResponseDto;
import beleg.rouletteservice.view.response.UserStatsResponseDto;

import java.util.List;

public interface RouletteStatsHandler {

    GlobalStatsResponseDto getGlobalStats();
    Result<UserStatsResponseDto, Failures> getUserStats(Long userId);
    List<GameStatDto> getAllGameStats();
    Result<GameStatDto, Failures> getGameStat(Long gameId);
    Result<GameStatDto, Failures> deleteGameStat(Long gameId);
}