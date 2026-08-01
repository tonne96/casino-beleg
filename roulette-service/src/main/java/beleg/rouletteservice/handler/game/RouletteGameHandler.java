package beleg.rouletteservice.handler.game;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.Result;
import beleg.rouletteservice.view.request.PlayRequestDto;
import beleg.rouletteservice.view.response.PlayResponseDto;
// interface für die ausführung einer runde roulette

public interface RouletteGameHandler {
    Result<PlayResponseDto, Failures> play(PlayRequestDto request);
}
