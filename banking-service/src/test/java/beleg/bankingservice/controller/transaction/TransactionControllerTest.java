package beleg.bankingservice.controller.transaction;

import beleg.bankingservice.handler.transaction.ITransactionHandler;
import beleg.bankingservice.model.Transaction;
import beleg.bankingservice.view.DeletedTransactionView;
import beleg.bankingservice.view.TransactionRequest;
import beleg.bankingservice.view.TransactionView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionControllerTest {
	
	private ITransactionHandler transactionHandler;
	private TransactionController transactionController;
	
	@BeforeEach
	void setUp() {
		this.transactionHandler = mock(ITransactionHandler.class);
		this.transactionController = new TransactionController(transactionHandler);
	}
	
	// Hilfsmethode
	private Transaction createTransaction(Long userId, BigDecimal amount) {
		return new Transaction(Transaction.InvoicingParty.SLOTS, userId, amount);
	}
	
	@Test
	void getAllTransactionsReturnsOkWithTransactions() {
		Transaction transaction1 = createTransaction(1L, BigDecimal.valueOf(10));
		Transaction transaction2 = createTransaction(2L, BigDecimal.valueOf(20));
		when(transactionHandler.getAllTransactions()).thenReturn(List.of(transaction1, transaction2));
		
		ResponseEntity<List<TransactionView>> response = transactionController.getAllTransactions();
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(2, response.getBody().size());
		assertEquals(1L, response.getBody().getFirst().user());
		assertEquals(BigDecimal.valueOf(10), response.getBody().getFirst().amount());
		verify(transactionHandler).getAllTransactions();
	}
	
	@Test
	void getAllTransactionsReturnsEmptyList() {
		when(transactionHandler.getAllTransactions()).thenReturn(List.of());
		
		ResponseEntity<List<TransactionView>> response = transactionController.getAllTransactions();
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().isEmpty());
		verify(transactionHandler).getAllTransactions();
	}
	
	@Test
	void getTransactionsByUserReturnsNotFoundWhenUserDoesNotExist() {
		Long userId = 1L;
		when(transactionHandler.getTransactionsByUser(userId)).thenReturn(Optional.empty());
		
		ResponseEntity<List<TransactionView>> response = transactionController.getTransactionsByUser(userId);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(transactionHandler).getTransactionsByUser(userId);
	}
	
	@Test
	void getTransactionsByUserReturnsEmptyListWhenUserHasNoTransactions() {
		Long userId = 1L;
		when(transactionHandler.getTransactionsByUser(userId)).thenReturn(Optional.of(List.of()));
		
		ResponseEntity<List<TransactionView>> response = transactionController.getTransactionsByUser(userId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().isEmpty());
		verify(transactionHandler).getTransactionsByUser(userId);
	}
	
	@Test
	void getTransactionsByUsersReturnsTransactionsWhenUserHasTransactions() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.valueOf(10);
		Transaction transaction1 = createTransaction(1L, amount);
		Transaction transaction2 = createTransaction(1L, amount);
		when(transactionHandler.getTransactionsByUser(userId)).thenReturn(Optional.of(List.of(transaction1, transaction2)));
		
		ResponseEntity<List<TransactionView>> response = transactionController.getTransactionsByUser(userId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(2, response.getBody().size());
		assertEquals(1L, response.getBody().getFirst().user());
		assertEquals(amount, response.getBody().getFirst().amount());
		verify(transactionHandler).getTransactionsByUser(userId);
	}
	
	@Test
	void createTransactionReturnsCreatedWhenTransactionIsCreated() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.valueOf(10);
		TransactionRequest request = new TransactionRequest("SLOTS", null, amount);
		Transaction transaction = createTransaction(userId, amount);
		when(transactionHandler.createTransaction("SLOTS", userId, amount)).thenReturn(Optional.of(transaction));
		
		ResponseEntity<TransactionView> response = transactionController.createTransaction(userId, request);
		
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(amount, response.getBody().amount());
		verify(transactionHandler).createTransaction("SLOTS", userId, amount);
	}
	
	@Test
	void createTransactionReturnsNotFoundWhenUserDoesNotExist() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.valueOf(10);
		TransactionRequest request = new TransactionRequest("SLOTS", null, amount);
		when(transactionHandler.createTransaction("SLOTS", userId, amount)).thenReturn(Optional.empty());
		
		ResponseEntity<TransactionView> response = transactionController.createTransaction(userId, request);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(transactionHandler).createTransaction("SLOTS", userId, amount);
	}
	
	@Test
	void createTransactionReturnsBadRequestWhenHandlerRejectsRequest() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.valueOf(10);
		TransactionRequest request = new TransactionRequest("SLOTS", null, amount);
		when(transactionHandler.createTransaction("SLOTS", userId, amount)).thenThrow(new IllegalArgumentException()); // TODO checken ob das hier mit den Argumenten Sinn ergibt
		
		ResponseEntity<TransactionView> response = transactionController.createTransaction(userId, request);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNull(response.getBody());
		verify(transactionHandler).createTransaction("SLOTS", userId, amount);
	}
	
	@Test
	void updateTransactionReturnsBadRequestWhenRequestUserIsNull() {
		Long transactionId = 42L;
		BigDecimal amount = BigDecimal.valueOf(10);
		TransactionRequest request = new TransactionRequest("SLOTS", null, amount);
		
		ResponseEntity<TransactionView> response = transactionController.updateTransaction(transactionId, request);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNull(response.getBody());
		verifyNoInteractions(transactionHandler);
	}
	
	@Test
	void updateTransactionReturnsBadRequestWhenHandlerRejectsRequest() {
		Long transactionId = 42L;
		String invoicingParty = "SLOTS";
		Long userId = 1L;
		BigDecimal amount = BigDecimal.valueOf(10);
		TransactionRequest request = new TransactionRequest(invoicingParty, userId, amount);
		when(transactionHandler.updateTransaction(transactionId, invoicingParty, userId, amount)).thenThrow(new IllegalArgumentException());
		
		ResponseEntity<TransactionView> response = transactionController.updateTransaction(transactionId, request);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNull(response.getBody());
		verify(transactionHandler).updateTransaction(transactionId, invoicingParty, userId, amount);
	}
	
	@Test
	void updateTransactionReturnsNotFoundWhenTransactionDoesNotExist() {
		Long transactionId = 42L;
		String invoicingParty = "SLOTS";
		Long userId = 1L;
		BigDecimal amount = BigDecimal.valueOf(10);
		TransactionRequest request = new TransactionRequest(invoicingParty, userId, amount);
		when(transactionHandler.updateTransaction(transactionId, invoicingParty, userId, amount)).thenReturn(Optional.empty());
		
		ResponseEntity<TransactionView> response = transactionController.updateTransaction(transactionId, request);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(transactionHandler).updateTransaction(transactionId, invoicingParty, userId, amount);
	}
	
	@Test
	void updateTransactionReturnsOkWhenTransactionIsUpdated() {
		Long transactionId = 42L;
		String invoicingParty = "SLOTS";
		Long userId = 1L;
		BigDecimal amount = BigDecimal.valueOf(10);
		TransactionRequest request = new TransactionRequest(invoicingParty, userId, amount);
		Transaction transaction = createTransaction(userId, amount);
		when(transactionHandler.updateTransaction(transactionId, invoicingParty, userId, amount)).thenReturn(Optional.of(transaction));
		
		ResponseEntity<TransactionView> response = transactionController.updateTransaction(transactionId, request);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(amount, response.getBody().amount());
		verify(transactionHandler).updateTransaction(transactionId, invoicingParty, userId, amount);
	}
	
	@Test
	void deleteTransactionReturnsNotFoundWhenTransactionDoesNotExist() {
		Long transactionId = 42L;
		when(transactionHandler.deleteTransaction(transactionId)).thenReturn(Optional.empty());
		
		ResponseEntity<DeletedTransactionView> response = transactionController.deleteTransaction(transactionId);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(transactionHandler).deleteTransaction(transactionId);
	}
	
	@Test
	void deleteTransactionReturnsOkWhenTransactionExists() {
		Long transactionId = 42L;
		Long userId = 1L;
		BigDecimal amount = BigDecimal.valueOf(10);
		Transaction transaction = createTransaction(userId, amount);
		when(transactionHandler.deleteTransaction(transactionId)).thenReturn(Optional.of(transaction));
		
		ResponseEntity<DeletedTransactionView> response = transactionController.deleteTransaction(transactionId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(amount, response.getBody().amount());
		assertEquals(userId, response.getBody().user());
		verify(transactionHandler).deleteTransaction(transactionId);
	}
}