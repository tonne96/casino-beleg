package beleg.bankingservice.view;

import beleg.bankingservice.model.Transaction;
import java.math.BigDecimal;

public record DeletedTransactionView(
        String invoicing_party,
        Long user,
        BigDecimal amount
) {
    public static DeletedTransactionView from(Transaction t) {
        return new DeletedTransactionView(
                t.getInvoicingParty().name(),
                t.getUserId(),
                t.getAmount()
        );
    }
}
