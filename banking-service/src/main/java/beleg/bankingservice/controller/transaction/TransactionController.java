package beleg.bankingservice.controller.transaction;

import beleg.bankingservice.handler.transaction.TransactionHandler;
import beleg.bankingservice.handler.user.UserHandler;
import beleg.bankingservice.view.TransactionRequest;
import beleg.bankingservice.view.TransactionView;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


// Controller nimmt HTTP-Requests von außen etgegen und gibt HTTP-Responses zurück

@RestController
public class TransactionController implements ITransactionController{

    // dependency injection von handler
    private final TransactionHandler transactionHandler;
    private final UserHandler userHandler;

    public TransactionController(TransactionHandler transactionHandler, UserHandler userHandler) {
        this.transactionHandler = transactionHandler;
        this.userHandler = userHandler;
    }

    /**
     * GET /casino/bank/api/transactions
     * Liefert alle Transaktionen
     */
    @Override
    public ResponseEntity<List<TransactionView>> getAllTransactions() {
        List<TransactionView> transactions = transactionHandler.getAllTransactions()
                .stream()
                .map(TransactionView::from)
                .toList();
        return ResponseEntity.ok(transactions);
    }

    /**
     * GET /casino/bank/api/transactions/user/{id}
     * Liefert alle Transaktionen eines Users
     */
    @Override
    public ResponseEntity<List<TransactionView>> getTransactionsByUser(Long id) {
        // Prüfen ob User existiert → 404 wenn nicht
        if (userHandler.getUser(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<TransactionView> transactions = transactionHandler.getTransactionsByUser(id)
                .stream()
                .map(TransactionView::from)
                .toList();
        return ResponseEntity.ok(transactions);
    }

    /**
     * POST /casino/bank/api/transaction/user/{user_id}
     * Legt eine neue Transaktion an und passt den Kontostand an
     */
    @Override
    public ResponseEntity<TransactionView> createTransaction(
            Long user_id,
            TransactionRequest request) {
        // User muss existieren
        if (userHandler.getUser(user_id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            // Transaktion speichern und Kontostand in einem Vorgang anpassen
            var transaction = transactionHandler.createTransaction(
                    request.invoicing_party(), user_id, request.amount());
            return ResponseEntity.status(HttpStatus.CREATED).body(TransactionView.from(transaction));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /casino/bank/api/transaction/{transaction_id}
     * Aktualisiert eine bestehende Transaktion
     */
    @Override
    public ResponseEntity<TransactionView> updateTransaction(
            Long transaction_id,
            TransactionRequest request) {
        // User muss existieren
        if (userHandler.getUser(request.user()).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            return transactionHandler.updateTransaction(
                            transaction_id,
                            request.invoicing_party(),
                            request.user(),
                            request.amount())
                    .map(t -> ResponseEntity.ok(TransactionView.from(t)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /casino/bank/api/transaction/{transaction_id}
     * Löscht eine Transaktion
     */
    @Override
    public ResponseEntity<TransactionView> deleteTransaction(Long transaction_id) {
        return transactionHandler.deleteTransaction(transaction_id)
                .map(t -> ResponseEntity.ok(TransactionView.from(t)))
                .orElse(ResponseEntity.notFound().build());
    }
}
