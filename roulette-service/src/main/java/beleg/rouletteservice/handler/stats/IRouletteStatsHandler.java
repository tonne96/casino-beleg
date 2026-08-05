package beleg.rouletteservice.handler.stats;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.view.response.GameStatDto;
import beleg.rouletteservice.view.response.GlobalStatsResponseDto;
import beleg.rouletteservice.view.response.UserStatsResponseDto;

import java.util.List;

public interface IRouletteStatsHandler {

    GlobalStatsResponseDto getGlobalStats();
    IResult<UserStatsResponseDto, Failures> getUserStats(Long userId);
    List<GameStatDto> getAllGameStats();
    IResult<GameStatDto, Failures> getGameStat(Long gameId);
    IResult<GameStatDto, Failures> deleteGameStat(Long gameId);
}