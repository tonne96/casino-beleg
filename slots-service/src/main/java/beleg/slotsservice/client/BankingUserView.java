package beleg.slotsservice.client;

import java.math.BigDecimal;

/**
 * Antwort des Banking-Service bei GET /casino/bank/api/user/{id}.
 * Die Feldnamen entsprechen dessen JSON-Vertrag.
 */
public record BankingUserView(
        Long id,
        String first_name,
        String last_name,
        BigDecimal balance
) {}
