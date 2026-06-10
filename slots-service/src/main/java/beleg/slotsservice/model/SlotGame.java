package beleg.slotsservice.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Domain-Entity: repraesentiert eine gespeicherte Slot-Runde.
 *
 * Diese Entity ist die Grundlage fuer Spielhistorie und Statistiken.
 * @GeneratedValue: die ID wird automatisch von der Datenbank generiert.
 */
@Entity
@Table(name = "slot_game")
public class SlotGame {

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
    @Column(name = "first_slot", nullable = false)
    private SlotSymbol firstSlot;

    @Enumerated(EnumType.STRING)
    @Column(name = "second_slot", nullable = false)
    private SlotSymbol secondSlot;

    @Enumerated(EnumType.STRING)
    @Column(name = "third_slot", nullable = false)
    private SlotSymbol thirdSlot;

    @Column(name = "payout_multiplier", nullable = false)
    private int payoutMultiplier;

    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;

    // JPA benoetigt einen parameterlosen Konstruktor.
    protected SlotGame() {}

    /**
     * Factory-Methode fuer eine neue gespeicherte Slot-Runde.
     *
     * Von aussen soll eine SlotGame-Entity bewusst ueber create(...) erzeugt werden.
     * Dadurch bleibt die Erzeugung an einer Stelle gekapselt.
     */
    public static SlotGame create(
            Long userId,
            BigDecimal betAmount,
            boolean winning,
            BigDecimal amount,
            SlotSymbol firstSlot,
            SlotSymbol secondSlot,
            SlotSymbol thirdSlot,
            int payoutMultiplier,
            LocalDateTime playedAt) {
        validateUserId(userId);
        validateBetAmount(betAmount);
        validateAmount(amount);
        validateSlotSymbol(firstSlot, "FirstSlot");
        validateSlotSymbol(secondSlot, "SecondSlot");
        validateSlotSymbol(thirdSlot, "ThirdSlot");
        validatePayoutMultiplier(payoutMultiplier);
        validatePlayedAt(playedAt);

        return new SlotGame(
                userId,
                betAmount,
                winning,
                amount,
                firstSlot,
                secondSlot,
                thirdSlot,
                payoutMultiplier,
                playedAt
        );
    }

    /**
     * Fachlicher Konstruktor bleibt privat.
     *
     * Die SlotGame-Entity bekommt nur fertige Werte. Sie kennt kein
     * SlotGameResult, weil das Ergebnis vorher vom Simulator berechnet und
     * in der Factory in speicherbare Felder uebersetzt wird.
     */
    private SlotGame(
            Long userId,
            BigDecimal betAmount,
            boolean winning,
            BigDecimal amount,
            SlotSymbol firstSlot,
            SlotSymbol secondSlot,
            SlotSymbol thirdSlot,
            int payoutMultiplier,
            LocalDateTime playedAt) {
        this.userId = userId;
        this.betAmount = betAmount;
        this.winning = winning;
        this.amount = amount;
        this.firstSlot = firstSlot;
        this.secondSlot = secondSlot;
        this.thirdSlot = thirdSlot;
        this.payoutMultiplier = payoutMultiplier;
        this.playedAt = playedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public boolean isWinning() {
        return winning;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public List<SlotSymbol> getSlotStates() {
        return List.of(firstSlot, secondSlot, thirdSlot);
    }

    public int getPayoutMultiplier() {
        return payoutMultiplier;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    private static void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId darf nicht null sein.");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("UserId muss groesser als 0 sein.");
        }
    }

    private static void validateBetAmount(BigDecimal betAmount) {
        if (betAmount == null) {
            throw new IllegalArgumentException("BetAmount darf nicht null sein.");
        }
        if (betAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("BetAmount muss groesser als 0 sein.");
        }
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount darf nicht null sein.");
        }
    }

    private static void validateSlotSymbol(SlotSymbol slotSymbol, String fieldName) {
        if (slotSymbol == null) {
            throw new IllegalArgumentException(fieldName + " darf nicht null sein.");
        }
    }

    private static void validatePayoutMultiplier(int payoutMultiplier) {
        if (payoutMultiplier < 0) {
            throw new IllegalArgumentException("PayoutMultiplier darf nicht negativ sein.");
        }
    }

    private static void validatePlayedAt(LocalDateTime playedAt) {
        if (playedAt == null) {
            throw new IllegalArgumentException("PlayedAt darf nicht null sein.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SlotGame slotGame)) return false;
        return Objects.equals(id, slotGame.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SlotGame{id=" + id + ", userId=" + userId + ", betAmount=" + betAmount
                + ", winning=" + winning + ", amount=" + amount + ", slotStates=" + getSlotStates()
                + ", payoutMultiplier=" + payoutMultiplier + ", playedAt=" + playedAt + '}';
    }
}
