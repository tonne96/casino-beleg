package beleg.bankingservice.handler.transaction;

import beleg.bankingservice.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ITransactionHandler {

    List<Transaction> getAllTransactions();

    List<Transaction> getTransactionsByUser(Long userID);

    Optional<Transaction> getTransaction(Long id);

    Transaction createTransaction(String invoicingParty, Long userId, BigDecimal amount);

    Optional<Transaction> updateTransaction(Long id, String invoicingParty, Long userId, BigDecimal amount);

    Optional<Transaction> deleteTransaction(Long id);

}
