package beleg.bankingservice.handler.user;

import beleg.bankingservice.model.User;
import beleg.bankingservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class UserHandler implements IUserHandler {

    private final UserRepository userRepository;

    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(String firstName, String lastName) {
        User user = new User(firstName, lastName);
        return userRepository.save(user);
    }

    public Optional<User> updateUser(Long id, String firstName, String lastName) {
        Optional<User> found = userRepository.findById(id);

        if (found.isEmpty()) {
            return Optional.empty();
        }

        User user = found.get();
        user.updateName(firstName, lastName);
        User saved = userRepository.save(user);
        return Optional.of(saved);
    }

    public Optional<User> deleteUser(Long id) {
        Optional<User> found = userRepository.findById(id);

        if (found.isEmpty()) {
            return Optional.empty();
        }

        User user = found.get();
        userRepository.delete(user);
        return Optional.of(user);
    }

    public Optional<User> deposit(Long userId, long amount, int decimals) {
        validateDepositParams(amount, decimals);

        BigDecimal depositAmount = buildDepositAmount(amount, decimals);

        Optional<User> found = userRepository.findById(userId);

        if (found.isEmpty()) {
            return Optional.empty();
        }

        User user = found.get();
        user.adjustBalance(depositAmount);
        User saved = userRepository.save(user);
        return Optional.of(saved);
    }

    public Optional<User> adjustBalance(Long userId, BigDecimal amount) {
        Optional<User> found = userRepository.findById(userId);

        if (found.isEmpty()) {
            return Optional.empty();
        }

        User user = found.get();
        user.adjustBalance(amount);
        User saved = userRepository.save(user);
        return Optional.of(saved);
    }

    private void validateDepositParams(long amount, int decimals) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount darf nicht negativ sein.");
        }
        if (decimals < 0) {
            throw new IllegalArgumentException("decimals darf nicht negativ sein.");
        }
        if (decimals > 99) {
            throw new IllegalArgumentException("decimals darf maximal 2 Stellen haben (0–99).");
        }
    }

    private BigDecimal buildDepositAmount(long amount, int decimals) {
        BigDecimal base = BigDecimal.valueOf(amount);
        BigDecimal decimalPart = BigDecimal.valueOf(decimals)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY);
        return base.add(decimalPart);
    }
}
