package beleg.rouletteservice.handler.game;

import beleg.rouletteservice.client.banking.IBankingClient;
import beleg.rouletteservice.client.banking.BankingUserDto;
import beleg.rouletteservice.factory.IRouletteGameFactory;
import beleg.rouletteservice.handler.game.strategies.IBetStrategy;
import beleg.rouletteservice.handler.game.strategies.BetStrategyResolver;
import beleg.rouletteservice.handler.game.wheel.IRouletteWheel;
import beleg.rouletteservice.model.IRouletteGame;
import beleg.rouletteservice.model.RouletteGameImpl;
import beleg.rouletteservice.model.RouletteGameResult;
import beleg.rouletteservice.repository.IRouletteGameRepository;
import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.result.Success;
import beleg.rouletteservice.view.request.PlayRequestDto;
import beleg.rouletteservice.view.response.PlayResponseDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;


//Falls das lokale Speichern nach erfolgreicher Banking-Buchung nicht klappt, entsteht eine inkonsistenz
//  Geld wurde bereits gebucht, aber kein lokaler Datensatz existiert...
@Service
public class RouletteGameHandlerImpl implements IRouletteGameHandler {

    private final IBankingClient bankingClient;
    private final BetStrategyResolver betStrategyResolver;
    private final IRouletteWheel rouletteWheel;
    private final IRouletteGameFactory rouletteGameFactory;
    private final IRouletteGameRepository repository;

    public RouletteGameHandlerImpl(
            IBankingClient bankingClient,
            BetStrategyResolver betStrategyResolver,
            IRouletteWheel rouletteWheel,
            IRouletteGameFactory rouletteGameFactory,
            IRouletteGameRepository repository) {
        this.bankingClient = bankingClient;
        this.betStrategyResolver = betStrategyResolver;
        this.rouletteWheel = rouletteWheel;
        this.rouletteGameFactory = rouletteGameFactory;
        this.repository = repository;
    }

    @Override
    public IResult<PlayResponseDto, Failures> play(PlayRequestDto request) {

        if (request == null || request.user() == null || request.betAmount() == null
                || request.betType() == null || request.betNumbers() == null) {
            return new Failure<>(Failures.NOT_NULL);
        }

        Optional<IBetStrategy> maybeStrategy = betStrategyResolver.resolve(request.betType());
        if (maybeStrategy.isEmpty()) {
            return new Failure<>(Failures.INVALID_BET_TYPE);
        }
        IBetStrategy betStrategy = maybeStrategy.get();

        IResult<Void, Failures> inputValidation = validateInput(request, betStrategy);
        if (!inputValidation.isSuccess()) {
            return new Failure<>(inputValidation.getMessage());
        }

        IResult<BankingUserDto, Failures> userResult = bankingClient.getUser(request.user());
        if (!userResult.isSuccess()) {
            return new Failure<>(userResult.getMessage());
        }

        BankingUserDto user = userResult.getValue();
        if (user.balance().compareTo(request.betAmount()) < 0) {
            return new Failure<>(Failures.INSUFFICIENT_BALANCE);
        }

        int winningNumber = rouletteWheel.spin();
        boolean won = betStrategy.isWinning(request.betNumbers(), winningNumber);
        int payoutMultiplier = won ? betStrategy.getPayoutMultiplier() : 0;
        BigDecimal netAmount = won
                ? request.betAmount().multiply(BigDecimal.valueOf(payoutMultiplier))
                : request.betAmount().negate();

        IResult<RouletteGameResult, Failures> gameResultResult =
                RouletteGameResult.create(won, netAmount, winningNumber, payoutMultiplier);
        if (!gameResultResult.isSuccess()) {
            return new Failure<>(gameResultResult.getMessage());
        }

        IResult<IRouletteGame, Failures> gameCreationResult = rouletteGameFactory.create(
                request.user(),
                request.betAmount(),
                request.betType(),
                request.betNumbers(),
                gameResultResult.getValue()
        );
        if (!gameCreationResult.isSuccess()) {
            return new Failure<>(gameCreationResult.getMessage());
        }

        IResult<Void, Failures> bookingResult = bankingClient.bookTransaction(request.user(), netAmount);
        if (!bookingResult.isSuccess()) {
            return new Failure<>(bookingResult.getMessage());
        }

        IRouletteGame savedGame = repository.save((RouletteGameImpl) gameCreationResult.getValue());

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
    private IResult<Void, Failures> validateInput(PlayRequestDto request, IBetStrategy strategy) {
        if (request.betAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return new Failure<>(Failures.BIGGER_ZERO);
        }
        return strategy.validate(request.betNumbers());
    }
}