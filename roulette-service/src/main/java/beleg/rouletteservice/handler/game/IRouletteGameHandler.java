package beleg.rouletteservice.handler.game;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.view.request.PlayRequestDto;
import beleg.rouletteservice.view.response.PlayResponseDto;
// interface für die ausführung einer runde roulette

public interface IRouletteGameHandler {
    IResult<PlayResponseDto, Failures> play(PlayRequestDto request);
}
