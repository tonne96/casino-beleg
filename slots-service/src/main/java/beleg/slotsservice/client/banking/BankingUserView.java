package beleg.slotsservice.client.banking;

import java.math.BigDecimal;

/**
 * DTO fuer die Antwort des Banking-Service bei GET /casino/bank/api/user/{id}.
 *
 * Die Feldnamen passen absichtlich zur JSON-Antwort vom Banking-Service.
 */
public record BankingUserView(
        Long id,
        String first_name,
        String last_name,
        BigDecimal balance
) {}
