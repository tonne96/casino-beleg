package beleg.bankingservice.view;

import beleg.bankingservice.model.User;
import java.math.BigDecimal;

// klassisches DTO für Kapselung nach außen

public record UserView(
        Long id,
        String first_name,
        String last_name,
        BigDecimal balance
) {

    public static UserView from(User user) {
        return new UserView(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBalance()
        );
    }
}