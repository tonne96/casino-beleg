package beleg.bankingservice.handler.transaction;

import beleg.bankingservice.model.Transaction;
import beleg.bankingservice.model.User;
import beleg.bankingservice.repository.TransactionRepository;
import beleg.bankingservice.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Handler = Geschäftslogik
 * Controller fragt Handler, Handler kennt Repository aber nicht HTTP und JSON
 */
@Service
public class TransactionHandler {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionHandler(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Liefert alle Transaktionen.
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Liefert alle Transaktionen eines Users.
     *
     * @param userId User-ID
     * @return Liste der Transaktionen (leer wenn keine vorhanden)
     */
    public List<Transaction> getTransactionsByUser(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    /**
     * Liefert eine einzelne Transaktion anhand ihrer ID.
     */
    public Optional<Transaction> getTransaction(Long id) {
        return transactionRepository.findById(id);
    }

    /**
     * Legt eine neue Transaktion an.
     *
     * @param invoicingParty Name des aufrufenden Game-Services
     * @param userId         betroffener User
     * @param amount         Betrag (positiv oder negativ)
     * @return gespeicherte Transaktion mit generierter ID
     * @throws IllegalArgumentException wenn invoicingParty unbekannt ist
     */
    @Transactional
    public Transaction createTransaction(String invoicingParty, Long userId, BigDecimal amount) {
        Transaction.InvoicingParty party = parseInvoicingParty(invoicingParty);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User existiert nicht: " + userId));

        user.adjustBalance(amount);

        Transaction transaction = new Transaction(party, userId, amount);
        return transactionRepository.save(transaction);
    }

    /**
     * Aktualisiert eine bestehende Transaktion.
     *
     * @return Optional mit aktualisierter Transaktion oder leer wenn nicht gefunden
     */
    public Optional<Transaction> updateTransaction(Long id, String invoicingParty, Long userId, BigDecimal amount) {
        Transaction.InvoicingParty party = parseInvoicingParty(invoicingParty);
        return transactionRepository.findById(id).map(transaction -> {
            transaction.update(party, userId, amount);
            return transactionRepository.save(transaction);
        });
    }

    /**
     * Löscht eine Transaktion anhand ihrer ID.
     *
     * @return Optional mit der gelöschten Transaktion oder leer wenn nicht gefunden
     */
    public Optional<Transaction> deleteTransaction(Long id) {
        return transactionRepository.findById(id).map(transaction -> {
            transactionRepository.delete(transaction);
            return transaction;
        });
    }

    // ── Private Hilfsmethoden ────────────────────────────

    /**
     * Parst den String-Namen zu einem InvoicingParty-Enum.
     * Wirft IllegalArgumentException wenn der Name unbekannt ist —
     * der Controller gibt dann 400 zurück.
     */
    private Transaction.InvoicingParty parseInvoicingParty(String name) {
        try {
            return Transaction.InvoicingParty.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException(
                    "Unbekannter Rechnungssteller: '" + name + "'. " +
                            "Erlaubt: ROULETTE, SLOTS"
            );
        }
    }
}
