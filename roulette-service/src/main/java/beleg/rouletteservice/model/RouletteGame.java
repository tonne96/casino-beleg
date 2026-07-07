package beleg.rouletteservice.model;

import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.Result;
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
public class RouletteGame {

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
    protected RouletteGame() {}

    public static Result<RouletteGame, Failures> create(
            Long userId,
            BigDecimal betAmount,
            boolean winning,
            BigDecimal amount,
            RouletteBetType betType,
            List<Integer> betNumbers,
            int winningNumber,
            int payoutMultiplier,
            LocalDateTime playedAt) {

        List<Result<Void, Failures>> validations = List.of(
                validateUserId(userId),
                validateBetAmount(betAmount),
                validateAmount(amount),
                validateRouletteBetType(betType),
                validateBetSetup(betType, betNumbers),
                validateWinningNumber(winningNumber),
                validatePayoutMultiplier(payoutMultiplier),
                validatePlayedAt(playedAt)
        );

        Result<Void, Failures> validationResult = Results.firstFailure(validations);
        if (!validationResult.isSuccess()) {
            return new Failure<>(validationResult.getMessage());
        }

        RouletteGame rouletteGame = new RouletteGame(
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

    private RouletteGame(
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


    private static Result<Void, Failures> validateUserId(Long userId) {
        if (userId == null) {
            return new Failure<>(Failures.NOT_NULL);
        }
        if (userId <= 0) {
            return new Failure<>(Failures.BIGGER_ZERO);
        }
        return new Success<>(null);
    }

    private static Result<Void, Failures> validateBetAmount(BigDecimal betAmount) {
        if (betAmount == null) {
            return new Failure<>(Failures.NOT_NULL);
        }
        if (betAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return new Failure<>(Failures.BIGGER_ZERO);
        }
        return new Success<>(null);
    }

    private static Result<Void, Failures> validateAmount(BigDecimal amount) {
        if (amount == null) {
            return new Failure<>(Failures.NOT_NULL);
        }
        return new Success<>(null);
    }

    private static Result<Void, Failures> validateRouletteBetType(RouletteBetType rouletteBetType) {
        if (rouletteBetType == null) {
            return new Failure<>(Failures.NOT_NULL);
        }
        return new Success<>(null);
    }

    private static Result<Void, Failures> validateBetSetup(
            RouletteBetType betType, List<Integer> betNumbers) {

        if (betNumbers == null) {
            return new Failure<>(Failures.NOT_NULL);
        }
        for (Integer n : betNumbers) {
            if (n == null) {
                return new Failure<>(Failures.NOT_NULL);
            }
        }
        return switch (betType) {
            case RED_OR_BLACK, ODD_OR_EVEN, LOW_OR_HIGH -> validateBinaryChoice(betNumbers);
            case DOZEN, COLUMN -> validateOneToThree(betNumbers);
            case SINGLE -> validateSingle(betNumbers);
            case STREET -> validateStreet(betNumbers);
            case SIX_LINE -> validateSixLine(betNumbers);
            case CORNER -> validateCorner(betNumbers);
            case SPLIT -> validateSplit(betNumbers);
        };
    }

    //SINGLE: genau eine Zahl zwischen 0 und 36
    private static Result<Void, Failures> validateSingle(List<Integer> betNumbers) {
        if (betNumbers.size() != 1) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        int n = betNumbers.get(0);
        if (n < 0 || n > 36) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        return new Success<>(null);
    }

    //RED_OR_BLACK / ODD_OR_EVEN / LOW_OR_HIGH: genau eine Zahl, nur 0 oder 1
    private static Result<Void, Failures> validateBinaryChoice(List<Integer> betNumbers) {
        if (betNumbers.size() != 1) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        int choice = betNumbers.get(0);
        if (choice != 0 && choice != 1) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        return new Success<>(null);
    }

    // DOZEN / COLUMN: genau eine Zahl, nur 1, 2 oder 3
    private static Result<Void, Failures> validateOneToThree(List<Integer> betNumbers) {
        if (betNumbers.size() != 1) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        int choice = betNumbers.get(0);
        if (choice < 1 || choice > 3) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        return new Success<>(null);
    }


    // nur eine zahl die sich in der linken spalte befinden muss %3 ergibt immer 1 !
    private static Result<Void, Failures> validateStreet(List<Integer> betNumbers) {
        if (betNumbers.size() != 1) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        int a = betNumbers.get(0);
        if (a < 1 || a > 34 || (a - 1) % 3 != 0) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        return new Success<>(null);
    }

    // nur eine zahl die sich in der linken spalte befinden muss %3 ergibt immer 1 !
    private static Result<Void, Failures> validateSixLine(List<Integer> betNumbers) {
        if (betNumbers.size() != 1) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        int a = betNumbers.get(0);
        if (a < 1 || a > 31 || (a - 1) % 3 != 0) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        return new Success<>(null);
    }

    // nur eine zahl die sich nur in der linken und mittleren spalte befinden muss %3 !=0
    private static Result<Void, Failures> validateCorner(List<Integer> betNumbers) {
        if (betNumbers.size() != 1) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        int a = betNumbers.get(0);
        if (a < 1 || a > 32 || a % 3 == 0) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        return new Success<>(null);
    }

    // zwei zahlen die benachtbart sein müssen(also horizontal oder vertikal auf dem Tableau), 0 ist speziall Fall
    private static Result<Void, Failures> validateSplit(List<Integer> betNumbers) {
        if (betNumbers.size() != 2) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        int a = betNumbers.get(0);
        int b = betNumbers.get(1);
        if (a < 0 || a > 36 || b < 0 || b > 36 || a == b) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        if (!areAdjacentOnTable(a, b)) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        return new Success<>(null);
    }

    // prüft horizontale nachbarn zahlen diffferenz = 1
    // und vertikale zahlen differenz = 3
    // ausnahme bildet 0 die nur mit 1,2,3 benachtbart ist
    private static boolean areAdjacentOnTable(int a, int b) {
        // Spezialfall: 0 ist mit 1, 2, 3 benachbart
        if ((a == 0 && (b == 1 || b == 2 || b == 3)) || (b == 0 && (a == 1 || a == 2 || a == 3))) {
            return true;
        }
        int rowA = (a - 1) / 3;
        int rowB = (b - 1) / 3;
        boolean horizontallyAdjacent = rowA == rowB && Math.abs(a - b) == 1;
        boolean verticallyAdjacent = Math.abs(a - b) == 3;
        return horizontallyAdjacent || verticallyAdjacent;
    }

    private static Result<Void, Failures> validateWinningNumber(int winningNumber) {
        if (winningNumber < 0 || winningNumber > 36) {
            return new Failure<>(Failures.OUT_OF_RANGE);
        }
        return new Success<>(null);
    }

    private static Result<Void, Failures> validatePayoutMultiplier(int payoutMultiplier) {
        if (payoutMultiplier < 0) {
            return new Failure<>(Failures.NOT_NEGATIVE);
        }
        return new Success<>(null);
    }

    private static Result<Void, Failures> validatePlayedAt(LocalDateTime playedAt) {
        if (playedAt == null) {
            return new Failure<>(Failures.NOT_NULL);
        }
        return new Success<>(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouletteGame rouletteGame)) return false;
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