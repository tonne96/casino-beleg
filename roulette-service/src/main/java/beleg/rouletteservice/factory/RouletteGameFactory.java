package beleg.rouletteservice.factory;

import beleg.rouletteservice.model.RouletteGame;
import beleg.rouletteservice.model.RouletteGameResult;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.Result;
import beleg.rouletteservice.rules.RouletteBetType;

import java.math.BigDecimal;
import java.util.List;

public interface RouletteGameFactory {

    Result<RouletteGame, Failures> create(
            Long userId,
            BigDecimal betAmount,
            RouletteBetType betType,
            List<Integer> betNumbers,
            RouletteGameResult result);
}


