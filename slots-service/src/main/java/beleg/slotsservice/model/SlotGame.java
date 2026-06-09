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
    public static SlotGame create(Long userId, BigDecimal betAmount, SlotGameResult result) {
        validateUserId(userId);
        validateBetAmount(betAmount);
        validateResult(result);

        return new SlotGame(userId, betAmount, result);
    }

    /**
     * Fachlicher Konstruktor bleibt privat.
     *
     * Validierung passiert in create(...), damit andere Klassen nicht direkt
     * ueber new SlotGame(...) an der Factory-Methode vorbei erzeugen.
     */
    private SlotGame(Long userId, BigDecimal betAmount, SlotGameResult result) {
        List<SlotSymbol> slotStates = result.slotStates();

        this.userId = userId;
        this.betAmount = betAmount;
        this.winning = result.winning();
        this.amount = result.amount();
        this.firstSlot = slotStates.get(0);
        this.secondSlot = slotStates.get(1);
        this.thirdSlot = slotStates.get(2);
        this.payoutMultiplier = result.payoutMultiplier();
        this.playedAt = LocalDateTime.now();
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

    private static void validateResult(SlotGameResult result) {
        if (result == null) {
            throw new IllegalArgumentException("SlotGameResult darf nicht null sein.");
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
