package beleg.rouletteservice.factory;

import beleg.rouletteservice.model.IRouletteGame;
import beleg.rouletteservice.model.RouletteGameResult;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.rules.RouletteBetType;

import java.math.BigDecimal;
import java.util.List;

public interface IRouletteGameFactory {

    IResult<IRouletteGame, Failures> create(
            Long userId,
            BigDecimal betAmount,
            RouletteBetType betType,
            List<Integer> betNumbers,
            RouletteGameResult result);
}


