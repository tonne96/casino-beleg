package beleg.bankingservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private Random random;
    private Transaction transaction;
    private Long userId;
    private BigDecimal amount;

    @BeforeEach
    void setup() {
        random = new Random();
        userId = random.nextLong(1, 10000);
        amount = BigDecimal.valueOf(random.nextDouble(1, 500)).setScale(2, RoundingMode.HALF_UP);
        transaction = new Transaction(Transaction.InvoicingParty.ROULETTE, userId, amount);
    }

    // ── Konstruktor Normalfall ────────────────────────────

    @Test
    void constructor_setsInvoicingPartyRoulette() {
        Transaction t = new Transaction(Transaction.InvoicingParty.ROULETTE, userId, amount);
        assertEquals(Transaction.InvoicingParty.ROULETTE, t.getInvoicingParty());
    }

    @Test
    void constructor_setsInvoicingPartySlots() {
        Transaction t = new Transaction(Transaction.InvoicingParty.SLOTS, userId, amount);
        assertEquals(Transaction.InvoicingParty.SLOTS, t.getInvoicingParty());
    }

    @Test
    void constructor_setsUserId() {
        assertEquals(userId, transaction.getUserId());
    }

    @Test
    void constructor_setsAmount() {
        assertEquals(amount, transaction.getAmount());
    }

    @Test
    void constructor_withNegativeAmount() {
        BigDecimal negativeAmount = BigDecimal.valueOf(random.nextDouble(1, 500)).negate().setScale(2, RoundingMode.HALF_UP);
        Transaction t = new Transaction(Transaction.InvoicingParty.SLOTS, userId, negativeAmount);
        assertEquals(negativeAmount, t.getAmount());
    }

    // ── Konstruktor Validierung ───────────────────────────

    @Test
    void constructor_nullInvoicingPartyThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Transaction(null, userId, amount));
    }

    @Test
    void constructor_nullUserIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Transaction(Transaction.InvoicingParty.ROULETTE, null, amount));
    }

    @Test
    void constructor_nullAmountThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Transaction(Transaction.InvoicingParty.ROULETTE, userId, null));
    }

    // ── update() Normalfall ───────────────────────────────

    @Test
    void update_changesInvoicingParty() {
        transaction.update(Transaction.InvoicingParty.SLOTS, userId, amount);
        assertEquals(Transaction.InvoicingParty.SLOTS, transaction.getInvoicingParty());
    }

    @Test
    void update_changesUserId() {
        Long newUserId = random.nextLong(1, 10000);
        transaction.update(Transaction.InvoicingParty.ROULETTE, newUserId, amount);
        assertEquals(newUserId, transaction.getUserId());
    }

    @Test
    void update_changesAmount() {
        BigDecimal newAmount = BigDecimal.valueOf(random.nextDouble(1, 500)).setScale(2, RoundingMode.HALF_UP);
        transaction.update(Transaction.InvoicingParty.ROULETTE, userId, newAmount);
        assertEquals(newAmount, transaction.getAmount());
    }

    @Test
    void update_changesAllFields() {
        Long newUserId = random.nextLong(1, 10000);
        BigDecimal newAmount = BigDecimal.valueOf(random.nextDouble(1, 500)).setScale(2, RoundingMode.HALF_UP);

        transaction.update(Transaction.InvoicingParty.SLOTS, newUserId, newAmount);

        assertEquals(Transaction.InvoicingParty.SLOTS, transaction.getInvoicingParty());
        assertEquals(newUserId, transaction.getUserId());
        assertEquals(newAmount, transaction.getAmount());
    }

    // ── update() Validierung ──────────────────────────────

    @Test
    void update_nullInvoicingPartyThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                transaction.update(null, userId, amount));
    }

    @Test
    void update_nullUserIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                transaction.update(Transaction.InvoicingParty.ROULETTE, null, amount));
    }

    @Test
    void update_nullAmountThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                transaction.update(Transaction.InvoicingParty.ROULETTE, userId, null));
    }
}
