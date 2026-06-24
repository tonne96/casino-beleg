package beleg.bankingservice.controller.transaction;

import beleg.bankingservice.handler.transaction.TransactionHandler;
import beleg.bankingservice.model.Transaction;
import beleg.bankingservice.view.DeletedTransactionView;
import beleg.bankingservice.view.TransactionRequest;
import beleg.bankingservice.view.TransactionView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class TransactionController implements ITransactionController {

    private final TransactionHandler transactionHandler;

    public TransactionController(TransactionHandler transactionHandler) {
        this.transactionHandler = transactionHandler;
    }

    /**
     * GET /casino/bank/api/transactions
     */
    @Override
    public ResponseEntity<List<TransactionView>> getAllTransactions() {
        List<Transaction> all = transactionHandler.getAllTransactions();

        List<TransactionView> views = new ArrayList<>();
        for (Transaction t : all) {
            views.add(TransactionView.from(t));
        }

        return ResponseEntity.ok(views);
    }

    /**
     * GET /casino/bank/api/transactions/user/{id}
     * Leeres Optional vom Handler = User nicht gefunden → 404
     * Leere Liste im Optional = User hat noch keine Transaktionen → 200 []
     */
    @Override
    public ResponseEntity<List<TransactionView>> getTransactionsByUser(Long id) {
        Optional<List<Transaction>> found = transactionHandler.getTransactionsByUser(id);

        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Transaction> transactions = found.get();
        List<TransactionView> views = new ArrayList<>();
        for (Transaction t : transactions) {
            views.add(TransactionView.from(t));
        }

        return ResponseEntity.ok(views);
    }

    /**
     * POST /casino/bank/api/transaction/user/{user_id}
     * Leeres Optional vom Handler = User nicht gefunden → 404
     */
    @Override
    public ResponseEntity<TransactionView> createTransaction(Long user_id, TransactionRequest request) {
        Optional<Transaction> created;

        try {
            created = transactionHandler.createTransaction(
                    request.invoicing_party(), user_id, request.amount());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        if (created.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Transaction transaction = created.get();
        TransactionView view = TransactionView.from(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    /**
     * PUT /casino/bank/api/transaction/{transaction_id}
     * user im Body ist beim PUT Pflicht (beim POST kommt er aus dem Pfad)
     */
    @Override
    public ResponseEntity<TransactionView> updateTransaction(Long transaction_id, TransactionRequest request) {
        if (request.user() == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Transaction> updated;

        try {
            updated = transactionHandler.updateTransaction(
                    transaction_id,
                    request.invoicing_party(),
                    request.user(),
                    request.amount());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        if (updated.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Transaction transaction = updated.get();
        TransactionView view = TransactionView.from(transaction);
        return ResponseEntity.ok(view);
    }

    /**
     * DELETE /casino/bank/api/transaction/{transaction_id}
     * Gibt die Transaktion ohne ID zurück (laut Spec)
     */
    @Override
    public ResponseEntity<DeletedTransactionView> deleteTransaction(Long transaction_id) {
        Optional<Transaction> deleted = transactionHandler.deleteTransaction(transaction_id);

        if (deleted.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Transaction transaction = deleted.get();
        DeletedTransactionView view = DeletedTransactionView.from(transaction);
        return ResponseEntity.ok(view);
    }
}
