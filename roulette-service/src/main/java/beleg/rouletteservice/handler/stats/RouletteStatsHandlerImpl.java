package beleg.rouletteservice.handler.stats;

import beleg.rouletteservice.client.banking.IBankingClient;
import beleg.rouletteservice.client.banking.BankingUserDto;
import beleg.rouletteservice.model.RouletteGameImpl;
import beleg.rouletteservice.repository.IRouletteGameRepository;
import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.result.Success;
import beleg.rouletteservice.view.response.GameStatDto;
import beleg.rouletteservice.view.response.GlobalStatsResponseDto;
import beleg.rouletteservice.view.response.UserStatsResponseDto;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class RouletteStatsHandlerImpl implements IRouletteStatsHandler {

    private final IRouletteGameRepository repository;
    private final IBankingClient bankingClient;

    public RouletteStatsHandlerImpl(IRouletteGameRepository repository, IBankingClient bankingClient) {
        this.repository = repository;
        this.bankingClient = bankingClient;
    }

    @Override
    public GlobalStatsResponseDto getGlobalStats() {
        List<RouletteGameImpl> allGames = repository.findAll();

        Set<Long> clients = new HashSet<>();
        BigDecimal totalTurnover = BigDecimal.ZERO;
        BigDecimal totalCashOut = BigDecimal.ZERO;

        for (RouletteGameImpl game : allGames) {
            clients.add(game.getUserId());
            totalTurnover = totalTurnover.add(game.getBetAmount());
            totalCashOut = totalCashOut.add(calculateCashOut(game));
        }

        BigDecimal totalProfit = totalTurnover.subtract(totalCashOut);

        return new GlobalStatsResponseDto(
                clients.size(),
                allGames.size(),
                totalProfit,
                totalCashOut,
                totalTurnover
        );
    }

    @Override
    public IResult<UserStatsResponseDto, Failures> getUserStats(Long userId) {
        IResult<BankingUserDto, Failures> userResult = bankingClient.getUser(userId);
        if (!userResult.isSuccess()) {
            return new Failure<>(userResult.getMessage());
        }

        List<RouletteGameImpl> userGames = repository.findByUserId(userId);

        BigDecimal totalWinnings = BigDecimal.ZERO;
        BigDecimal totalLosses = BigDecimal.ZERO;
        BigDecimal totalTurnover = BigDecimal.ZERO;

        for (RouletteGameImpl game : userGames) {
            totalWinnings = totalWinnings.add(calculateCashOut(game));
            totalTurnover = totalTurnover.add(game.getBetAmount());

            if (!game.isWinning()) {
                totalLosses = totalLosses.add(game.getBetAmount());
            }
        }

        BigDecimal totalClientProfit = totalWinnings.subtract(totalTurnover);
        BigDecimal totalHouseProfitFromClient = totalTurnover.subtract(totalWinnings);

        UserStatsResponseDto dto = new UserStatsResponseDto(
                userId,
                userGames.size(),
                totalWinnings,
                totalLosses,
                totalClientProfit,
                totalTurnover,
                totalHouseProfitFromClient
        );
        return new Success<>(dto);
    }

    @Override
    public List<GameStatDto> getAllGameStats() {
        List<RouletteGameImpl> allGames = repository.findAll();

        List<GameStatDto> dtos = new ArrayList<>();
        for (RouletteGameImpl game : allGames) {
            dtos.add(toGameStatDto(game));
        }
        return dtos;
    }

    @Override
    public IResult<GameStatDto, Failures> getGameStat(Long gameId) {
        Optional<RouletteGameImpl> maybeGame = repository.findById(gameId);
        if (maybeGame.isEmpty()) {
            return new Failure<>(Failures.GAME_NOT_FOUND);
        }
        return new Success<>(toGameStatDto(maybeGame.get()));
    }

    @Override
    public IResult<GameStatDto, Failures> deleteGameStat(Long gameId) {
        Optional<RouletteGameImpl> maybeGame = repository.findById(gameId);
        if (maybeGame.isEmpty()) {
            return new Failure<>(Failures.GAME_NOT_FOUND);
        }
        GameStatDto dto = toGameStatDto(maybeGame.get());
        repository.deleteById(gameId);
        return new Success<>(dto);
    }

    private BigDecimal calculateCashOut(RouletteGameImpl game) {
        if (!game.isWinning()) {
            return BigDecimal.ZERO;
        }
        long bruttoMultiplier = game.getPayoutMultiplier() + 1L;
        return game.getBetAmount().multiply(BigDecimal.valueOf(bruttoMultiplier));
    }

    private GameStatDto toGameStatDto(RouletteGameImpl game) {
        return new GameStatDto(
                game.getId(),
                game.getUserId(),
                game.isWinning(),
                game.getAmount(),
                game.getWinningNumber(),
                game.getBetType(),
                game.getBetNumbers(),
                game.getBetAmount(),
                game.getPayoutMultiplier(),
                game.getPlayedAt()
        );
    }
}