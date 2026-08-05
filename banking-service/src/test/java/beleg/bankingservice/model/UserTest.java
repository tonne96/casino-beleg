package beleg.bankingservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setup() {
        user = new User("first", "last");
    }

    @Test
    void initialBalanceIsZero() {
        assertEquals(BigDecimal.ZERO, user.getBalance());
    }

    @Test
    void getFirstName() {
        assertEquals("first", user.getFirstName());
    }

    @Test
    void getLastName() {
        assertEquals("last", user.getLastName());
    }

    @Test
    void getBalance() {
        BigDecimal amount = BigDecimal.valueOf(10);
        System.out.println(user.getBalance());
        user.adjustBalance(amount);

        assertEquals(amount, user.getBalance());
    }

    @Test
    void adjustBalanceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> user.adjustBalance(null));
    }

    @Test
    void adjustBalance() {
        user.adjustBalance(BigDecimal.valueOf(10));
        assertEquals(BigDecimal.valueOf(10), user.getBalance());
    }

    @Test
    void adjustBalance_randomPositiveAmount() {
        Random random = new Random();
        double randomValue = random.nextDouble(1, 1000);
        BigDecimal amount = BigDecimal.valueOf(randomValue).setScale(2, RoundingMode.HALF_UP);

        user.adjustBalance(amount);

        assertEquals(amount, user.getBalance());
    }

    @Test
    void constructorThrowsExceptionWhenFirstNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new User(null, "last"));
    }

    @Test
    void constructorThrowsExceptionWhenFirstNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new User("   ", "last"));
    }

    @Test
    void constructorThrowsExceptionWhenLastNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new User("first", null));
    }

    @Test
    void constructorThrowsExceptionWhenLastNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new User("first", "   "));
    }

    @Test
    void constructorTrimsNames() {
        User user = new User("  Max  ", "  Mustermann  ");

        assertEquals("Max", user.getFirstName());
        assertEquals("Mustermann", user.getLastName());
    }

    @Test
    void adjustBalance_subtractNegativeAmount() {
        user.adjustBalance(BigDecimal.valueOf(100));
        user.adjustBalance(BigDecimal.valueOf(-30));
        assertEquals(BigDecimal.valueOf(70), user.getBalance());
    }

    @Test
    void updateName() {
        user.updateName("new", "name");

        assertEquals("new", user.getFirstName());
        assertEquals("name", user.getLastName());
    }

    @Test
    void updateNameThrowsExceptionWhenFirstNameIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> user.updateName("", "newLast"));
    }

    @Test
    void updateNameThrowsExceptionWhenLastNameIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> user.updateName("newFirst", null));
    }
}