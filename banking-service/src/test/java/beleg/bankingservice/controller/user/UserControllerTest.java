package beleg.bankingservice.controller.user;

import beleg.bankingservice.handler.user.IUserHandler;
import beleg.bankingservice.model.User;
import beleg.bankingservice.view.BalanceAdjustRequest;
import beleg.bankingservice.view.DeletedUserView;
import beleg.bankingservice.view.UserRequest;
import beleg.bankingservice.view.UserView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

	private IUserHandler userHandler;
	private UserController userController;

	@BeforeEach
	void setUp() {
		userHandler = mock(IUserHandler.class);
		userController = new UserController(userHandler);
	}

	// Hilfsmethode
	private User createUser(String firstName, String lastName) {
		return new User(firstName, lastName);
	}

	@Test
	void getUserReturnsOkWhenUserExists() {
		Long userId = 1L;
		String firstName = "first";
		String lastName = "last";
		User user = createUser(firstName, lastName);
		when(userHandler.getUser(userId)).thenReturn(Optional.of(user));

		ResponseEntity<UserView> response = userController.getUser(userId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(firstName, response.getBody().first_name());
		assertEquals(lastName, response.getBody().last_name());
		assertEquals(BigDecimal.ZERO, response.getBody().balance());
		verify(userHandler).getUser(userId);
	}

	@Test
	void getUserReturnsNotFoundWhenUserDoesNotExist() {
		Long userId = 1L;
		when(userHandler.getUser(userId)).thenReturn(Optional.empty());

		ResponseEntity<UserView> response = userController.getUser(userId);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(userHandler).getUser(userId);
	}

	@Test
	void getAllUsersReturnsOkWhenUsersExist() {
		String firstName = "first";
		String lastName = "last";
		User user1 = createUser(firstName, lastName);
		User user2 = createUser("Max", "Mustermann");
		when(userHandler.getAllUsers()).thenReturn(List.of(user1, user2));

		ResponseEntity<List<UserView>> response = userController.getAllUsers();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(2, response.getBody().size());
		assertEquals(firstName, response.getBody().getFirst().first_name());
		assertEquals("Mustermann", response.getBody().getLast().last_name());
		assertEquals(BigDecimal.ZERO, response.getBody().getFirst().balance());
		verify(userHandler).getAllUsers();
	}

	@Test
	void getAllUsersReturnsEmptyListWhenNoUsersExist() {
		when(userHandler.getAllUsers()).thenReturn(List.of());

		ResponseEntity<List<UserView>> response = userController.getAllUsers();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().isEmpty());
	}

	@Test
	void createUserReturnsCreated() {
		String firstName = "first";
		String lastName = "last";
		User user = createUser(firstName, lastName);
		UserRequest request = new UserRequest(firstName, lastName);
		when(userHandler.createUser(firstName, lastName)).thenReturn(user);

		ResponseEntity<UserView> response = userController.createUser(request);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(firstName, response.getBody().first_name());
		verify(userHandler).createUser(firstName, lastName);
		assertEquals(BigDecimal.ZERO, response.getBody().balance());
	}

	@Test
	void updateUserReturnsOkWhenSuccessfullyUpdated() {
		Long userId = 1L;
		String newFirstName = "first";
		String newLastName = "last";
		User user = createUser(newFirstName, newLastName);
		UserRequest request = new UserRequest(newFirstName, newLastName);
		when(userHandler.updateUser(userId, newFirstName, newLastName)).thenReturn(Optional.of(user));

		ResponseEntity<UserView> response = userController.updateUser(userId, request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(newFirstName, response.getBody().first_name());
		assertEquals(newLastName, response.getBody().last_name());
		verify(userHandler).updateUser(userId, newFirstName, newLastName);
	}

	@Test
	void updateUserReturnsNotFoundWhenUserDoesNotExist() {
		Long userId = 1L;
		String firstName = "first";
		String lastName = "last";
		UserRequest request = new UserRequest(firstName, lastName);
		when(userHandler.updateUser(userId, firstName, lastName)).thenReturn(Optional.empty());

		ResponseEntity<UserView> response = userController.updateUser(userId, request);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void deleteUserReturnsOkWhenSuccessfullyDeleted() {
		Long userId = 1L;
		String firstName = "first";
		String lastName = "last";
		User user = createUser(firstName, lastName);
		when(userHandler.deleteUser(userId)).thenReturn(Optional.of(user));

		ResponseEntity<DeletedUserView> response = userController.deleteUser(userId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(firstName, response.getBody().first_name());
		assertEquals(lastName, response.getBody().last_name());
		verify(userHandler).deleteUser(userId);
	}

	@Test
	void deleteUserReturnsNotFoundWhenUserDoesNotExist() {
		Long userId = 1L;
		when(userHandler.deleteUser(userId)).thenReturn(Optional.empty());

		ResponseEntity<DeletedUserView> response = userController.deleteUser(userId);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void depositReturnsOkWhenSuccessfullyDeposited() {
		Long userId = 1L;
		long amount = 4;
		int decimals = 2;
		String firstName = "first";
		String lastName = "last";
		User user = createUser(firstName, lastName);
		when(userHandler.deposit(userId, amount, decimals)).thenReturn(Optional.of(user));

		ResponseEntity<UserView> response = userController.deposit(userId, amount, decimals);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(firstName, response.getBody().first_name());
		assertEquals(lastName, response.getBody().last_name());
		verify(userHandler).deposit(userId, amount, decimals);
	}

	@Test
	void depositReturnsNotFoundWhenUserDoesNotExist() {
		Long userId = 1L;
		long amount = 4;
		int decimals = 2;
		when(userHandler.deposit(userId, amount, decimals)).thenReturn(Optional.empty());

		ResponseEntity<UserView> response = userController.deposit(userId, amount, decimals);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void adjustBalanceReturnsOkWhenSuccessfullyAdjusted() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		String firstName = "first";
		String lastName = "last";
		User user = createUser(firstName, lastName);
		BalanceAdjustRequest request = new BalanceAdjustRequest(amount);
		when(userHandler.adjustBalance(userId, amount)).thenReturn(Optional.of(user));

		ResponseEntity<UserView> response = userController.adjustBalance(userId, request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(firstName, response.getBody().first_name());
		assertEquals(lastName, response.getBody().last_name());
		verify(userHandler).adjustBalance(userId, amount);
	}

	@Test
	void adjustBalanceReturnsNotFoundWhenUserDoesNotExist() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		BalanceAdjustRequest request = new BalanceAdjustRequest(amount);
		when(userHandler.adjustBalance(userId, amount)).thenReturn(Optional.empty());

		ResponseEntity<UserView> response = userController.adjustBalance(userId, request);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
	}
}