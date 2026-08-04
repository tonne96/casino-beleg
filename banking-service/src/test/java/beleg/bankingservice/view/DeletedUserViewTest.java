package beleg.bankingservice.view;

import beleg.bankingservice.model.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DeletedUserViewTest {

	@Test
	void fromMapsDeletedUserCorrectly() {
		User user = new User("firstName", "lastName");

		DeletedUserView view = DeletedUserView.from(user);

		assertEquals("firstName", view.first_name());
		assertEquals("lastName", view.last_name());
		assertEquals(BigDecimal.ZERO, view.balance());
	}
}