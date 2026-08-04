package beleg.bankingservice.view;

import beleg.bankingservice.model.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DeletedTransactionViewTest {

	@Test
	void fromMapsDeletedTransactionCorrectly() {
		Transaction transaction = new Transaction(Transaction.InvoicingParty.SLOTS, 1L, BigDecimal.TEN);

		DeletedTransactionView view = DeletedTransactionView.from(transaction);

		assertEquals("SLOTS", view.invoicing_party());
		assertEquals(1L, view.user());
		assertEquals(BigDecimal.TEN, view.amount());

	}

}