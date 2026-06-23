package beleg.bankingservice.controller.transaction;

import beleg.bankingservice.view.DeletedTransactionView;
import beleg.bankingservice.view.TransactionRequest;
import beleg.bankingservice.view.TransactionView;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/casino/bank/api")
public interface ITransactionController {

    @GetMapping("/transactions")
    ResponseEntity<List<TransactionView>> getAllTransactions();

    @GetMapping("/transactions/user/{id}")
    ResponseEntity<List<TransactionView>> getTransactionsByUser(@PathVariable Long id);

    @PostMapping("/transaction/user/{user_id}")
    ResponseEntity<TransactionView> createTransaction(
            @PathVariable Long user_id,
            @Valid @RequestBody TransactionRequest request);

    @PutMapping("/transaction/{transaction_id}")
    ResponseEntity<TransactionView> updateTransaction(
            @PathVariable Long transaction_id,
            @Valid @RequestBody TransactionRequest request);

    @DeleteMapping("/transaction/{transaction_id}")
    ResponseEntity<DeletedTransactionView> deleteTransaction(@PathVariable Long transaction_id);


}
