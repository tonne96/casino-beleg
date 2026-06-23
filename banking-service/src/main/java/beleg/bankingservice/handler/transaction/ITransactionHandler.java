package beleg.bankingservice.handler.transaction;

import beleg.bankingservice.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ITransactionHandler {

    List<Transaction> getAllTransactions();

    // Empty Optional = User existiert nicht; leere Liste = User hat keine Transaktionen
    Optional<List<Transaction>> getTransactionsByUser(Long userId);

    Optional<Transaction> getTransaction(Long id);

    // Empty Optional = User existiert nicht; IllegalArgumentException = ungültige Party
    Optional<Transaction> createTransaction(String invoicingParty, Long userId, BigDecimal amount);

    Optional<Transaction> updateTransaction(Long id, String invoicingParty, Long userId, BigDecimal amount);

    Optional<Transaction> deleteTransaction(Long id);
}
