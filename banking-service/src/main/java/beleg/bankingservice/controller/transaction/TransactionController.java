package beleg.bankingservice.controller.transaction;

import beleg.bankingservice.handler.transaction.TransactionHandler;
import beleg.bankingservice.view.DeletedTransactionView;
import beleg.bankingservice.view.TransactionRequest;
import beleg.bankingservice.view.TransactionView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        List<TransactionView> transactions = transactionHandler.getAllTransactions()
                .stream()
                .map(TransactionView::from)
                .toList();
        return ResponseEntity.ok(transactions);
    }

    /**
     * GET /casino/bank/api/transactions/user/{id}
     * Leeres Optional vom Handler = User nicht gefunden → 404
     */
    @Override
    public ResponseEntity<List<TransactionView>> getTransactionsByUser(Long id) {
        return transactionHandler.getTransactionsByUser(id)
                .map(list -> ResponseEntity.ok(list.stream().map(TransactionView::from).toList()))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /casino/bank/api/transaction/user/{user_id}
     * Leeres Optional vom Handler = User nicht gefunden → 404
     */
    @Override
    public ResponseEntity<TransactionView> createTransaction(Long user_id, TransactionRequest request) {
        try {
            return transactionHandler.createTransaction(
                            request.invoicing_party(), user_id, request.amount())
                    .map(t -> ResponseEntity.status(HttpStatus.CREATED).body(TransactionView.from(t)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /casino/bank/api/transaction/{transaction_id}
     */
    @Override
    public ResponseEntity<TransactionView> updateTransaction(Long transaction_id, TransactionRequest request) {
        if (request.user() == null) {
            return ResponseEntity.badRequest().build();
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
     * Gibt die Transaktion ohne ID zurück (laut Spec)
     */
    @Override
    public ResponseEntity<DeletedTransactionView> deleteTransaction(Long transaction_id) {
        return transactionHandler.deleteTransaction(transaction_id)
                .map(t -> ResponseEntity.ok(DeletedTransactionView.from(t)))
                .orElse(ResponseEntity.notFound().build());
    }
}
