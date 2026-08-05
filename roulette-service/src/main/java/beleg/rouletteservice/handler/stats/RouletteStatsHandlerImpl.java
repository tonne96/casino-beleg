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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


 // - total_turnover / total_house_turnover_from_client: Summe aller Einsätze (betAmount), unabhaengig vom Ausgang der jeweiligen Runde
// total_cash_out: Summe aller an Spieler ausgezahlten Gewinne (nur gewonnene Runden)
// total_profit / total_house_profit_from_client: Hausgewinn = negativer Netto-Gewinn der Spieler (unser Modell verbucht pro Runde nur den Netto-Betrag, siehe RouletteGameHandlerImpl - daher ist Hausgewinn exakt der negierte Spielergewinn.
// getUserStats Nutzer wird bei banking verifiziert, da Roulette Service keine User Verwaltung hat und eine leere Spielhistorie sonst nicht von einem unbekannten Nutzer unterschieden werden kann
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

        long totalGamesCount = allGames.size();
        long totalClientCount = allGames.stream()
                .map(RouletteGameImpl::getUserId)
                .distinct()
                .count();

        BigDecimal totalTurnover = sum(allGames, RouletteGameImpl::getBetAmount);
        BigDecimal totalCashOut = allGames.stream()
                .filter(RouletteGameImpl::isWinning)
                .map(RouletteGameImpl::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalProfit = sum(allGames, RouletteGameImpl::getAmount).negate();

        return new GlobalStatsResponseDto(
                totalClientCount, totalGamesCount, totalProfit, totalCashOut, totalTurnover
        );
    }

    @Override
    public IResult<UserStatsResponseDto, Failures> getUserStats(Long userId) {
        IResult<BankingUserDto, Failures> userResult = bankingClient.getUser(userId);
        if (!userResult.isSuccess()) {
            return new Failure<>(userResult.getMessage());
        }

        List<RouletteGameImpl> userGames = repository.findByUserId(userId);

        long totalGamesCount = userGames.size();
        BigDecimal totalWinnings = userGames.stream()
                .filter(RouletteGameImpl::isWinning)
                .map(RouletteGameImpl::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalLosses = userGames.stream()
                .filter(game -> !game.isWinning())
                .map(RouletteGameImpl::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .negate();
        BigDecimal totalClientProfit = totalWinnings.subtract(totalLosses);
        BigDecimal totalHouseTurnoverFromClient = sum(userGames, RouletteGameImpl::getBetAmount);
        BigDecimal totalHouseProfitFromClient = totalClientProfit.negate();

        UserStatsResponseDto dto = new UserStatsResponseDto(
                userId,
                totalGamesCount,
                totalWinnings,
                totalLosses,
                totalClientProfit,
                totalHouseTurnoverFromClient,
                totalHouseProfitFromClient
        );
        return new Success<>(dto);
    }

    @Override
    public List<GameStatDto> getAllGameStats() {
        return repository.findAll().stream()
                .map(this::toGameStatDto)
                .collect(Collectors.toList());
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

    private BigDecimal sum(List<RouletteGameImpl> games, Function<RouletteGameImpl, BigDecimal> extractor) {
        return games.stream()
                .map(extractor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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