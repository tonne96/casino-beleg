package beleg.bankingservice.view;

import beleg.bankingservice.model.Transaction;
import java.math.BigDecimal;

/**
 * View (DTO) für die JSON-Außendarstellung einer Transaktion.
 */
public record TransactionView(
        Long id,
        String invoicing_party,
        Long user,
        BigDecimal amount
) {
    public static TransactionView from(Transaction transaction) {
        return new TransactionView(
                transaction.getId(),
                transaction.getInvoicingParty().name(),
                transaction.getUserId(),
                transaction.getAmount()
        );
    }
}