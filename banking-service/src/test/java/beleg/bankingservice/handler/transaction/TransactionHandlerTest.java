package beleg.bankingservice.handler.transaction;

import beleg.bankingservice.model.Transaction;
import beleg.bankingservice.repository.TransactionRepository;
import beleg.bankingservice.view.BalanceAdjustRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionHandlerTest {
	private TransactionRepository transactionRepository;
	private RestTemplate restTemplate;
	private TransactionHandler transactionHandler;

	@BeforeEach
	void setUp() {
		transactionRepository = mock(TransactionRepository.class);
		restTemplate = mock(RestTemplate.class);
		transactionHandler = new TransactionHandler(transactionRepository, restTemplate);
	}

	// Hilfsmethode
	private Transaction createTransaction(Long userId, BigDecimal amount) {
		return new Transaction(Transaction.InvoicingParty.SLOTS, userId, amount);
	}

	@Test
	void getAllTransactionsReturnsAllTransactions() {
		Long userId1 = 1L;
		Long userId2 = 2L;
		BigDecimal amount1 = BigDecimal.ONE;
		BigDecimal amount2 = BigDecimal.TWO;
		Transaction transaction1 = createTransaction(userId1, amount1);
		Transaction transaction2 = createTransaction(userId2, amount2);
		when(transactionRepository.findAll()).thenReturn(List.of(transaction1, transaction2));

		List<Transaction> result = transactionHandler.getAllTransactions();

		assertTrue(result.contains(transaction1));
		assertTrue(result.contains(transaction2));
		assertEquals(amount1, result.getFirst().getAmount());
		assertEquals(userId2, result.getLast().getUserId());
		verify(transactionRepository).findAll();
	}

	@Test
	void getTransactionsByUserReturnsTransactionsWhenUserExistsAndHasTransactions() {
		Long userId = 1L;
		Transaction transaction1 = createTransaction(userId, BigDecimal.TEN);
		Transaction transaction2 = createTransaction(userId, BigDecimal.TWO);
		when(restTemplate.getForEntity(anyString(), eq(Object.class))).thenReturn(ResponseEntity.ok().build());
		when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction1, transaction2));

		Optional<List<Transaction>> result = transactionHandler.getTransactionsByUser(userId);

		assertTrue(result.isPresent());
		assertEquals(2, result.get().size());
		assertEquals(transaction1, result.get().getFirst());
		assertEquals(transaction2, result.get().getLast());
		verify(restTemplate).getForEntity(anyString(), eq(Object.class));
		verify(transactionRepository).findByUserId(userId);
	}

	@Test
	void getTransactionsByUserReturnsEmptyWhenUserDoesNotExist() {
		Long userId = 1L;
		when(restTemplate.getForEntity(anyString(), eq(Object.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

		Optional<List<Transaction>> result = transactionHandler.getTransactionsByUser(userId);

		assertTrue(result.isEmpty());
		verify(restTemplate).getForEntity(anyString(), eq(Object.class));
		verify(transactionRepository, never()).findByUserId(anyLong());
	}

	@Test
	void getTransactionsByUserReturnsEmptyListWhenUserHasNoTransactions() {
		Long userId = 1L;
		when(restTemplate.getForEntity(anyString(), eq(Object.class))).thenReturn(ResponseEntity.ok().build());
		when(transactionRepository.findByUserId(userId)).thenReturn(List.of());

		Optional<List<Transaction>> result = transactionHandler.getTransactionsByUser(userId);

		assertTrue(result.isPresent());
		assertTrue(result.get().isEmpty());
		verify(transactionRepository).findByUserId(userId);
	}

	@Test
	void getTransactionsByUserThrowsExceptionWhenUserServiceFails() {
		Long userId = 1L;
		when(restTemplate.getForEntity(anyString(), eq(Object.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

		assertThrows(HttpClientErrorException.class, () -> transactionHandler.getTransactionsByUser(userId));
		verify(transactionRepository, never()).findByUserId(anyLong());
	}

	@Test
	void getTransactionReturnsTransactionWhenTransactionExists() {
		Long userId = 1L;
		Long transactionId = 10L;
		BigDecimal amount = BigDecimal.TEN;
		Transaction transaction = createTransaction(userId, amount);
		when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

		Optional<Transaction> result = transactionHandler.getTransaction(transactionId);

		assertTrue(result.isPresent());
		assertSame(transaction, result.get());
		verify(transactionRepository).findById(transactionId);
	}

	@Test
	void getTransactionReturnsEmptyWhenTransactionDoesNotExist() {
		Long transactionId = 10L;
		when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

		Optional<Transaction> result = transactionHandler.getTransaction(transactionId);

		assertTrue(result.isEmpty());
		verify(transactionRepository).findById(transactionId);
	}

	@Test
	void createTransactionReturnsCreatedTransactionWhenUserExists() {
		String invoicingParty = "SLOTS";
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		when(restTemplate.getForEntity(anyString(), eq(Object.class))).thenReturn(ResponseEntity.ok().build());
		when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
		doNothing().when(restTemplate).put(anyString(), any(BalanceAdjustRequest.class));

		Optional<Transaction> result = transactionHandler.createTransaction(invoicingParty, userId, amount);

		assertTrue(result.isPresent());
		assertEquals(Transaction.InvoicingParty.SLOTS, result.get().getInvoicingParty());
		assertEquals(userId, result.get().getUserId());
		assertEquals(amount, result.get().getAmount());
		verify(transactionRepository).save(any(Transaction.class));
		verify(restTemplate).put(anyString(), any(BalanceAdjustRequest.class));
	}

	@Test
	void createTransactionThrowsExceptionWhenInvoicingPartyIsInvalid() {
		String invoicingParty = "Gandalf";
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;

		assertThrows(IllegalArgumentException.class, () -> transactionHandler.createTransaction(invoicingParty, userId, amount));
		verifyNoInteractions(transactionRepository);
		verifyNoInteractions(restTemplate);
	}

	@Test
	void createTransactionReturnsEmptyWhenUserDoesNotExist() {
		String invoicingParty = "SLOTS";
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		when(restTemplate.getForEntity(anyString(), eq(Object.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

		Optional<Transaction> result = transactionHandler.createTransaction(invoicingParty, userId, amount);

		assertTrue(result.isEmpty());
		verify(restTemplate).getForEntity(anyString(), eq(Object.class));
		verify(transactionRepository, never()).save(any(Transaction.class));
		verify(restTemplate, never()).put(anyString(), any(BalanceAdjustRequest.class));
	}

	@Test
	void createTransactionThrowsExceptionWhenInvoicingPartyIsNull() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;

		assertThrows(IllegalArgumentException.class, () -> transactionHandler.createTransaction(null, userId, amount));
		verifyNoInteractions(transactionRepository);
		verifyNoInteractions(restTemplate);
	}

	@Test
	void createTransactionThrowsExceptionWhenUserServiceFails() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;

		when(restTemplate.getForEntity(anyString(), eq(Object.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

		assertThrows(HttpClientErrorException.class, () -> transactionHandler.createTransaction("SLOTS", userId, amount));
		verify(transactionRepository, never()).save(any(Transaction.class));
	}

	@Test
	void updateTransactionThrowsExceptionWhenInvoicingPartyIsInvalid() {
		Long transactionId = 10L;
		String invoicingParty = "Gandalf";
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;

		assertThrows(IllegalArgumentException.class, () -> transactionHandler.updateTransaction(transactionId, invoicingParty, userId, amount));
		verifyNoInteractions(transactionRepository);
	}

	@Test
	void updateTransactionReturnsTransactionWhenTransactionExists() {
		Long transactionId = 10L;
		String invoicingParty = "ROULETTE";
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		Transaction transaction = createTransaction(userId, amount);
		when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(transaction)).thenReturn(transaction);

		Optional<Transaction> result = transactionHandler.updateTransaction(transactionId, invoicingParty, userId, amount);

		assertTrue(result.isPresent());
		assertEquals(invoicingParty, result.get().getInvoicingParty().name());
		assertEquals(userId, result.get().getUserId());
		assertEquals(amount, result.get().getAmount());
		verify(transactionRepository).findById(transactionId);
		verify(transactionRepository).save(transaction);
	}

	@Test
	void updateTransactionReturnsTransactionWhenInvoicingPartyIsMixedCase() {
		Long transactionId = 10L;
		String invoicingParty = "sLOts";
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		Transaction transaction = createTransaction(userId, amount);
		when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(transaction)).thenReturn(transaction);

		Optional<Transaction> result = transactionHandler.updateTransaction(transactionId, invoicingParty, userId, amount);

		assertTrue(result.isPresent());
		assertEquals(invoicingParty.toUpperCase(), result.get().getInvoicingParty().name());
		assertEquals(userId, result.get().getUserId());
		assertEquals(amount, result.get().getAmount());
		verify(transactionRepository).findById(transactionId);
		verify(transactionRepository).save(transaction);
	}

	@Test
	void updateTransactionReturnsEmptyWhenTransactionDoesNotExist() {
		Long transactionId = 10L;
		String invoicingParty = "SLOTS";
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

		Optional<Transaction> result = transactionHandler.updateTransaction(transactionId, invoicingParty, userId, amount);

		assertTrue(result.isEmpty());
		verify(transactionRepository).findById(transactionId);
		verify(transactionRepository, never()).save(any(Transaction.class));
	}

	@Test
	void updateTransactionThrowsExceptionWhenAmountIsNull() {
		Long transactionId = 10L;
		Transaction transaction = createTransaction(1L, BigDecimal.TEN);
		when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

		assertThrows(IllegalArgumentException.class, () -> transactionHandler.updateTransaction(transactionId, "SLOTS", 1L, null));
		verify(transactionRepository).findById(transactionId);
		verify(transactionRepository, never()).save(any(Transaction.class));
	}

	@Test
	void deleteTransactionReturnsTransactionWhenTransactionExists() {
		Long userId = 1L;
		Long transactionId = 10L;
		BigDecimal amount = BigDecimal.TEN;
		Transaction transaction = createTransaction(userId, amount);
		when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

		Optional<Transaction> result = transactionHandler.deleteTransaction(transactionId);

		assertTrue(result.isPresent());
		assertSame(transaction, result.get());
		verify(transactionRepository).findById(transactionId);
		verify(transactionRepository).delete(transaction);
	}

	@Test
	void deleteTransactionReturnsEmptyWhenTransactionDoesNotExist() {
		Long transactionId = 10L;
		when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

		Optional<Transaction> result = transactionHandler.deleteTransaction(transactionId);

		assertTrue(result.isEmpty());
		verify(transactionRepository).findById(transactionId);
		verify(transactionRepository, never()).delete(any(Transaction.class));
	}
}