package beleg.bankingservice.handler.transaction;

import beleg.bankingservice.model.Transaction;
import beleg.bankingservice.repository.TransactionRepository;
import beleg.bankingservice.view.BalanceAdjustRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionHandler implements ITransactionHandler {

    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    @Value("${banking.self.url:http://localhost:8080}")
    private String selfUrl;

    public TransactionHandler(TransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Prüft zuerst via HTTP-Self-Call ob der User existiert.
     * Leeres Optional = User nicht gefunden.
     * Leere Liste im Optional = User existiert, hat aber keine Transaktionen.
     */
    @Override
    public Optional<List<Transaction>> getTransactionsByUser(Long userId) {
        boolean exists = userExists(userId);

        if (!exists) {
            return Optional.empty();
        }

        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return Optional.of(transactions);
    }

    @Override
    public Optional<Transaction> getTransaction(Long id) {
        return transactionRepository.findById(id);
    }

    /**
     * Legt eine neue Transaktion an und passt den Kontostand des Users an.
     *
     * User-Zugriff erfolgt via HTTP-Self-Call (Vertikal-Slice-Architektur:
     * Transaction-Subdomain darf User-Subdomain nicht direkt importieren).
     *
     * Leeres Optional = User nicht gefunden.
     * IllegalArgumentException = ungültige InvoicingParty.
     */
    @Override
    @Transactional
    public Optional<Transaction> createTransaction(String invoicingParty, Long userId, BigDecimal amount) {
        Transaction.InvoicingParty party = parseInvoicingParty(invoicingParty);

        boolean exists = userExists(userId);

        if (!exists) {
            return Optional.empty();
        }

        Transaction transaction = new Transaction(party, userId, amount);
        Transaction saved = transactionRepository.save(transaction);

        // Kontostand via HTTP-Self-Call anpassen (User-Subdomain bleibt isoliert)
        adjustUserBalance(userId, amount);

        return Optional.of(saved);
    }

    @Override
    public Optional<Transaction> updateTransaction(Long id, String invoicingParty, Long userId, BigDecimal amount) {
        Transaction.InvoicingParty party = parseInvoicingParty(invoicingParty);

        Optional<Transaction> found = transactionRepository.findById(id);

        if (found.isEmpty()) {
            return Optional.empty();
        }

        Transaction transaction = found.get();
        transaction.update(party, userId, amount);
        Transaction saved = transactionRepository.save(transaction);
        return Optional.of(saved);
    }

    @Override
    public Optional<Transaction> deleteTransaction(Long id) {
        Optional<Transaction> found = transactionRepository.findById(id);

        if (found.isEmpty()) {
            return Optional.empty();
        }

        Transaction transaction = found.get();
        transactionRepository.delete(transaction);
        return Optional.of(transaction);
    }

    // ── Private Hilfsmethoden ────────────────────────────

    private Transaction.InvoicingParty parseInvoicingParty(String name) {
        try {
            return Transaction.InvoicingParty.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException(
                    "Unbekannter Rechnungssteller: '" + name + "'. Erlaubt: ROULETTE, SLOTS"
            );
        }
    }

    /**
     * HTTP-Self-Call: prüft ob der User existiert (GET /casino/bank/api/user/{id}).
     */
    private boolean userExists(Long userId) {
        try {
            restTemplate.getForEntity(selfUrl + "/casino/bank/api/user/" + userId, Object.class);
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw e;
        }
    }

    /**
     * HTTP-Self-Call: passt den Kontostand an (PUT /casino/bank/api/user/{id}/balance/adjust).
     */
    private void adjustUserBalance(Long userId, BigDecimal amount) {
        restTemplate.put(
                selfUrl + "/casino/bank/api/user/" + userId + "/balance/adjust",
                new BalanceAdjustRequest(amount)
        );
    }
}
