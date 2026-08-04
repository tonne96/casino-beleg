package beleg.bankingservice.handler.user;

import beleg.bankingservice.model.User;
import beleg.bankingservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserHandlerTest {
	private UserRepository userRepository;
	private UserHandler userHandler;

	@BeforeEach
	void setUp() {
		userRepository = mock(UserRepository.class);
		userHandler = new UserHandler(userRepository);
	}

	// Hilfsmethode
	private User createUser(String firstName, String lastName) {
		return new User(firstName, lastName);
	}

	@Test
	void getUserReturnsUserWhenUserExists() {
		Long userId = 1L;
		String firstName = "firstName";
		String lastName = "lastName";
		User user = createUser(firstName, lastName);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		Optional<User> result = userHandler.getUser(userId);

		assertTrue(result.isPresent());
		assertSame(user, result.get());
		verify(userRepository).findById(userId);
	}

	@Test
	void getUserReturnsEmptyWhenUserDoesNotExist() {
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		Optional<User> result = userHandler.getUser(userId);

		assertTrue(result.isEmpty());
		verify(userRepository).findById(userId);
	}

	@Test
	void getAllUsersReturnsAllUsers() {
		User user1 = createUser("firstName", "lastName");
		User user2 = createUser("Max", "Mustermann");
		when(userRepository.findAll()).thenReturn(List.of(user1, user2));

		List<User> result = userHandler.getAllUsers();

		assertEquals(2, result.size());
		assertEquals(List.of(user1, user2), result);
		verify(userRepository).findAll();
	}

	@Test
	void getAllUsersReturnsEmptyListWhenNoUsersExist() {
		when(userRepository.findAll()).thenReturn(List.of());

		List<User> result = userHandler.getAllUsers();

		assertTrue(result.isEmpty());
		verify(userRepository).findAll();
	}

	@Test
	void createUserCreatesAndSavesUser() {
		String firstName = "firstName";
		String lastName = "lastName";
		User user = createUser(firstName, lastName);
		when(userRepository.save(any(User.class))).thenReturn(user);

		User result = userHandler.createUser(firstName, lastName);

		assertSame(user, result);
		verify(userRepository).save(any(User.class));
	}

	@Test
	void updateUserReturnsUpdatedUserWhenUserExists() {
		Long userId = 1L;
		User user = createUser("oldFirstName", "oldLastName");
		String newFirstName = "newFirstName";
		String newLastName = "newLastName";
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userRepository.save(user)).thenReturn(user);

		Optional<User> result = userHandler.updateUser(userId, newFirstName, newLastName);

		assertTrue(result.isPresent());
		assertEquals(newFirstName, result.get().getFirstName());
		assertEquals(newLastName, result.get().getLastName());
		verify(userRepository).findById(userId);
		verify(userRepository).save(user);
	}

	@Test
	void updateUserReturnsEmptyWhenUserDoesNotExist() {
		Long userId = 1L;
		String firstName = "firstName";
		String lastName = "lastName";
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		Optional<User> result = userHandler.updateUser(userId, firstName, lastName);

		assertTrue(result.isEmpty());
		verify(userRepository).findById(userId);
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void updateUserThrowsExceptionWhenNewFirstNameIsEmpty() {
		Long userId = 1L;
		User user = createUser("oldFirstName", "oldLastName");
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		assertThrows(IllegalArgumentException.class, () -> userHandler.updateUser(userId, "", "newLastName"));
		verify(userRepository).findById(userId);
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void deleteUserReturnsDeletedUserWhenUserExists() {
		Long userId = 1L;
		String firstName = "firstName";
		String lastName = "lastName";
		User user = createUser(firstName, lastName);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		Optional<User> result = userHandler.deleteUser(userId);

		assertTrue(result.isPresent());
		assertSame(user, result.get());
		verify(userRepository).findById(userId);
		verify(userRepository).delete(user);
	}

	@Test
	void deleteUserReturnsEmptyWhenUserDoesNotExist() {
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		Optional<User> result = userHandler.deleteUser(userId);

		assertTrue(result.isEmpty());
		verify(userRepository).findById(userId);
		verify(userRepository, never()).delete(any(User.class));
	}

	@Test
	void depositUpdatesBalanceWhenUserExists() {
		Long userId = 1L;
		long amount = 4;
		int decimals = 20;
		String firstName = "firstName";
		String lastName = "lastName";
		User user = createUser(firstName, lastName);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userRepository.save(user)).thenReturn(user);

		Optional<User> result = userHandler.deposit(userId, amount, decimals);

		assertTrue(result.isPresent());
		assertEquals(new BigDecimal("4.20"), result.get().getBalance());
		verify(userRepository).findById(userId);
		verify(userRepository).save(user);
	}

	@Test
	void depositReturnsEmptyWhenUserDoesNotExist() {
		Long userId = 1L;
		long amount = 4;
		int decimals = 20;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		Optional<User> result = userHandler.deposit(userId, amount, decimals);

		assertTrue(result.isEmpty());
		verify(userRepository).findById(userId);
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void depositThrowsExceptionWhenAmountIsNegative() {
		Long userId = 1L;
		long amount = -42;
		int decimals = 1;

		assertThrows(IllegalArgumentException.class, () -> userHandler.deposit(userId, amount, decimals));
		verifyNoInteractions(userRepository);
	}

	@Test
	void depositThrowsExceptionWhenDecimalsAreNegative() {
		Long userId = 1L;
		long amount = 1;
		int decimals = -42;

		assertThrows(IllegalArgumentException.class, () -> userHandler.deposit(userId, amount, decimals));
		verifyNoInteractions(userRepository);
	}

	@Test
	void depositThrowsExceptionWhenDecimalsAreTooLarge() {
		Long userId = 1L;
		long amount = 1;
		int decimals = 100;

		assertThrows(IllegalArgumentException.class, () -> userHandler.deposit(userId, amount, decimals));
		verifyNoInteractions(userRepository);
	}

	@Test
	void adjustBalanceUpdatesUserBalanceWhenUserExists() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		User user = createUser("first", "last");
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userRepository.save(user)).thenReturn(user);

		Optional<User> result = userHandler.adjustBalance(userId, amount);

		assertTrue(result.isPresent());
		assertEquals(BigDecimal.TEN, result.get().getBalance());
		verify(userRepository).findById(userId);
		verify(userRepository).save(user);
	}

	@Test
	void adjustBalanceReturnsEmptyWhenUserDoesNotExist() {
		Long userId = 1L;
		BigDecimal amount = BigDecimal.TEN;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		Optional<User> result = userHandler.adjustBalance(userId, amount);

		assertTrue(result.isEmpty());
		verify(userRepository).findById(userId);
		verify(userRepository, never()).save(any(User.class));
	}


































}