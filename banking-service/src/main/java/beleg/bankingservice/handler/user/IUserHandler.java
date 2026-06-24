package beleg.bankingservice.handler.user;

import beleg.bankingservice.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IUserHandler {

    Optional<User> getUser(Long id);

    List<User> getAllUsers();

    User createUser(String firstName, String lastName);

    Optional<User> updateUser(Long id, String filename, String lastName);

    Optional<User> deleteUser(Long id);

    Optional<User> deposit(Long userId, long amount, int decimals);

    Optional<User> adjustBalance(Long userId, BigDecimal amount);

}
