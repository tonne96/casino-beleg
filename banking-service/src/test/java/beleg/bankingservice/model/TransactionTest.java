package beleg.bankingservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private Transaction transaction;
    private Long userId;
    private BigDecimal amount;

    @BeforeEach
    void setup() {
        userId = 1L;
        amount = BigDecimal.valueOf(10);
        transaction = new Transaction(Transaction.InvoicingParty.ROULETTE, userId, amount);
    }

    @Test
    void getInvoicingParty() {
        assertEquals(Transaction.InvoicingParty.ROULETTE, transaction.getInvoicingParty());
    }

    @Test
    void getUserId() {
        assertEquals(userId, transaction.getUserId());
    }

    @Test
    void getAmount() {
        assertEquals(amount, transaction.getAmount());
    }

}