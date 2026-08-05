package beleg.rouletteservice.model;

import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.result.Results;
import beleg.rouletteservice.result.Success;
import beleg.rouletteservice.rules.RouletteBetType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "roulette_game")
public class RouletteGameImpl implements IRouletteGame{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "bet_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal betAmount;

    @Column(nullable = false)
    private boolean winning;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "bet_type", nullable = false)
    private RouletteBetType betType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "roulette_game_bet_numbers",
            joinColumns = @JoinColumn(name = "roulette_game_id")
    )
    @Column(name = "bet_number", nullable = false)
    @OrderColumn(name = "position")
    private List<Integer> betNumbers;

    @Column(name = "winning_number", nullable = false)
    private int winningNumber;

    @Column(name = "payout_multiplier", nullable = false)
    private int payoutMultiplier;

    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;

    // JPA benoetigt einen parameterlosen Konstruktor.
    protected RouletteGameImpl() {}

    public static IResult<RouletteGameImpl, Failures> create(
            Long userId,
            BigDecimal betAmount,
            boolean winning,
            BigDecimal amount,
            RouletteBetType betType,
            List<Integer> betNumbers,
            int winningNumber,
            int payoutMultiplier,
            LocalDateTime playedAt) {

        List<IResult<Void, Failures>> validations = List.of(
                validateUserId(userId),
                validateBetAmount(betAmount),
                validateAmount(amount),
                validateRouletteBetType(betType),
                validateBetNumbersNotNull(betNumbers),
                validateWinningNumber(winningNumber),
                validatePayoutMultiplier(payoutMultiplier),
                validatePlayedAt(playedAt)
        );

        IResult<Void, Failures> validationResult = Results.firstFailure(validations);
        if (!validationResult.isSuccess()) {
            return new Failure<>(validationResult.getMessage());
        }

        RouletteGameImpl rouletteGame = new RouletteGameImpl(
                userId,
                betAmount,
                winning,
                amount,
                betType,
                betNumbers,
                winningNumber,
                payoutMultiplier,
                playedAt
        );

        return new Success<>(rouletteGame);
    }

    private RouletteGameImpl(
            Long userId,
            BigDecimal betAmount,
            boolean winning,
            BigDecimal amount,
            RouletteBetType betType,
            List<Integer> betNumbers,
            int winningNumber,
            int payoutMultiplier,
            LocalDateTime playedAt) {
        this.userId = userId;
        this.betAmount = betAmount;
        this.winning = winning;
        this.amount = amount;
        this.betType = betType;
        this.betNumbers = List.copyOf(betNumbers);
        this.winningNumber = winningNumber;
        this.payoutMultiplier = payoutMultiplier;
        this.playedAt = playedAt;
    }

    public Long getId() {return id;}

    public Long getUserId() {return userId;}

    public BigDecimal getBetAmount() {return betAmount;}

    public boolean isWinning() {return winning;}

    public BigDecimal getAmount() {return amount;}

    public RouletteBetType getBetType() {return betType;}

    public List<Integer> getBetNumbers() {return betNumbers;}

    public int getWinningNumber() {return winningNumber;}

    public int getPayoutMultiplier() {return payoutMultiplier;}

    public LocalDateTime getPlayedAt() {return playedAt;}


    private static IResult<Void, Failures> validateUserId(Long userId) {
        if (userId == null) {
            return new Failure<>(Failures.NOT_NULL);
        }
        if (userId <= 0) {
            return new Failure<>(Failures.BIGGER_ZERO);
        }
        return new Success<>(null);
    }

    private static IResult<Void, Failures> validateBetAmount(BigDecimal betAmount) {
        if (betAmount == null) {
            return new Failure<>(Failures.NOT_NULL);
        }
        if (betAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return new Failure<>(Failures.BIGGER_ZERO);
        }
        return new Success<>(null);
    }

    private static IResult<Void, Failures> validateAmount(BigDecimal amount) {
        if (amount == null) {
            return new Failure<>(Failures.NOT_NULL);
        }
        return new Success<>(null);
    }

    private static IResult<Void, Failures> validateRouletteBetType(RouletteBetType rouletteBetType) {
        if (rouletteBetType == null) {
            return new Failure<>(Failures.NOT_NULL);
        }
        return new Success<>(null);
    }

    private static IResult<Void, Failures> validateBetNumbersNotNull(List<Integer> betNumbers) {
        if (betNumbers == null) {
            return new Failure<>(Failures.NOT_NULL);
        }
        for (Integer n : betNumbers) {
            if (n == null) {
                return new Failure<>(Failures.NOT_NULL);
            }
        }
        return new Success<>(null);
    }

    private static IResult<Void, Failures> validateWinningNumber(int winningNumber) {
        if (winningNumber < 0 || winningNumber > 36) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        return new Success<>(null);
    }

    private static IResult<Void, Failures> validatePayoutMultiplier(int payoutMultiplier) {
        if (payoutMultiplier < 0) {
            return new Failure<>(Failures.NOT_NEGATIVE);
        }
        return new Success<>(null);
    }

    private static IResult<Void, Failures> validatePlayedAt(LocalDateTime playedAt) {
        if (playedAt == null) {
            return new Failure<>(Failures.NOT_NULL);
        }
        return new Success<>(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouletteGameImpl rouletteGame)) return false;
        return Objects.equals(id, rouletteGame.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "RouletteGame{id=" + id + ", userId=" + userId + ", betAmount=" + betAmount
                + ", winning=" + winning + ", amount=" + amount + ", betType=" + betType
                + ", betNumbers=" + betNumbers + ", winningNumber=" + winningNumber
                + ", payoutMultiplier=" + payoutMultiplier + ", playedAt=" + playedAt + '}';
    }
}