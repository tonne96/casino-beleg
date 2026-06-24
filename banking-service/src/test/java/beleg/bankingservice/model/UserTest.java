package beleg.bankingservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setup() {
        user = new User("first", "last");
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
    void adjustBalance() {
    }

    @Test
    void updateName() {
        user.updateName("new", "name");

        assertEquals("new", user.getFirstName());
        assertEquals("name", user.getLastName());
    }
}