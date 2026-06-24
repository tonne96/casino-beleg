package beleg.bankingservice.model;

import java.math.BigDecimal;

public interface IUser {

    Long getId();

    String getFirstName();

    String getLastName();

    BigDecimal getBalance();

}
