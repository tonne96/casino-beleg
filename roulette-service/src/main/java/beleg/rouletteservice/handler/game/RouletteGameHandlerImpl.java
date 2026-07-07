package beleg.rouletteservice.handler.game;

import beleg.rouletteservice.client.banking.BankingClient;
import beleg.rouletteservice.client.banking.BankingUserDto;
import beleg.rouletteservice.factory.RouletteGameFactory;
import beleg.rouletteservice.handler.game.strategies.BetStrategy;
import beleg.rouletteservice.handler.game.strategies.BetStrategyResolver;
import beleg.rouletteservice.handler.game.wheel.RouletteWheel;
import beleg.rouletteservice.model.RouletteGame;
import beleg.rouletteservice.model.RouletteGameResult;
import beleg.rouletteservice.repository.RouletteGameRepository;
import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.Result;
import beleg.rouletteservice.result.Success;
import beleg.rouletteservice.rules.RoulettePayoutRules;
import beleg.rouletteservice.view.request.PlayRequestDto;
import beleg.rouletteservice.view.response.PlayResponseDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;


//Falls das lokale Speichern nach erfolgreicher Banking-Buchung nicht klappt, entsteht eine inkonsistenz
//  Geld wurde bereits gebucht, aber kein lokaler Datensatz existiert...
@Service
public class RouletteGameHandlerImpl implements RouletteGameHandler {

    private final BankingClient bankingClient;
    private final BetStrategyResolver betStrategyResolver;
    private final RouletteWheel rouletteWheel;
    private final RoulettePayoutRules payoutRules;
    private final RouletteGameFactory rouletteGameFactory;
    private final RouletteGameRepository repository;

    public RouletteGameHandlerImpl(
            BankingClient bankingClient,
            BetStrategyResolver betStrategyResolver,
            RouletteWheel rouletteWheel,
            RoulettePayoutRules payoutRules,
            RouletteGameFactory rouletteGameFactory,
            RouletteGameRepository repository) {
        this.bankingClient = bankingClient;
        this.betStrategyResolver = betStrategyResolver;
        this.rouletteWheel = rouletteWheel;
        this.payoutRules = payoutRules;
        this.rouletteGameFactory = rouletteGameFactory;
        this.repository = repository;
    }

    @Override
    public Result<PlayResponseDto, Failures> play(PlayRequestDto request) {

        if (request == null || request.user() == null || request.betAmount() == null
                || request.betType() == null || request.betNumbers() == null) {
            return new Failure<>(Failures.NOT_NULL);
        }

        Optional<BetStrategy> maybeStrategy = betStrategyResolver.resolve(request.betType());
        if (maybeStrategy.isEmpty()) {
            return new Failure<>(Failures.INVALID_BET_TYPE);
        }
        BetStrategy betStrategy = maybeStrategy.get();

        Result<BankingUserDto, Failures> userResult = bankingClient.getUser(request.user());
        if (!userResult.isSuccess()) {
            return new Failure<>(userResult.getMessage());
        }

        BankingUserDto user = userResult.getValue();
        if (user.balance().compareTo(request.betAmount()) < 0) {
            return new Failure<>(Failures.INSUFFICIENT_BALANCE);
        }

        int winningNumber = rouletteWheel.spin();
        boolean won = betStrategy.isWinning(request.betNumbers(), winningNumber);
        int payoutMultiplier = won ? payoutRules.getPayoutMultiplier(request.betType()) : 0;

        BigDecimal netAmount = won
                ? request.betAmount().multiply(BigDecimal.valueOf(payoutMultiplier))
                : request.betAmount().negate();

        Result<RouletteGameResult, Failures> gameResultResult =
                RouletteGameResult.create(won, netAmount, winningNumber, payoutMultiplier);
        if (!gameResultResult.isSuccess()) {
            return new Failure<>(gameResultResult.getMessage());
        }

        Result<RouletteGame, Failures> gameCreationResult = rouletteGameFactory.create(
                request.user(),
                request.betAmount(),
                request.betType(),
                request.betNumbers(),
                gameResultResult.getValue()
        );
        if (!gameCreationResult.isSuccess()) {
            return new Failure<>(gameCreationResult.getMessage());
        }

        Result<Void, Failures> bookingResult = bankingClient.bookTransaction(request.user(), netAmount);
        if (!bookingResult.isSuccess()) {
            return new Failure<>(bookingResult.getMessage());
        }

        RouletteGame savedGame = repository.save(gameCreationResult.getValue());

        PlayResponseDto response = new PlayResponseDto(
                savedGame.getUserId(),
                savedGame.isWinning(),
                savedGame.getAmount(),
                savedGame.getWinningNumber(),
                savedGame.getBetType(),
                savedGame.getBetNumbers(),
                savedGame.getBetAmount(),
                savedGame.getPayoutMultiplier()
        );

        return new Success<>(response);
    }
}