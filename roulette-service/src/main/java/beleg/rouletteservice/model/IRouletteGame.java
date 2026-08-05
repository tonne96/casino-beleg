package beleg.rouletteservice.model;

import beleg.rouletteservice.rules.RouletteBetType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface IRouletteGame {

    Long getId();

    Long getUserId();

    BigDecimal getBetAmount();

    boolean isWinning();

    BigDecimal getAmount();

    RouletteBetType getBetType();

    List<Integer> getBetNumbers();

    int getWinningNumber();

    int getPayoutMultiplier();

    LocalDateTime getPlayedAt();
}