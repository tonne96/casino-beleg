package beleg.bankingservice.view;

import beleg.bankingservice.model.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionViewTest {

	@Test
	void fromMapsTransactionCorrectly() {
		Transaction transaction = new Transaction(Transaction.InvoicingParty.SLOTS, 1L, BigDecimal.TEN);

		TransactionView view = TransactionView.from(transaction);

		assertEquals("SLOTS", view.invoicing_party());
		assertEquals(1L, view.user());
		assertEquals(BigDecimal.TEN, view.amount());
	}
}