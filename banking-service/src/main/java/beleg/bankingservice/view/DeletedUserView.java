package beleg.bankingservice.view;

import beleg.bankingservice.model.User;
import java.math.BigDecimal;

public record DeletedUserView(
        String first_name,
        String last_name,
        BigDecimal balance
) {
    public static DeletedUserView from(User user) {
        return new DeletedUserView(
                user.getFirstName(),
                user.getLastName(),
                user.getBalance()
        );
    }
}
