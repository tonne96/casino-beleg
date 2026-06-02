package beleg.slotsservice.client.banking;

import java.math.BigDecimal;

/**
 * Request-DTO fuer POST /casino/bank/api/transaction/user/{user_id}.
 *
 * Damit meldet der Slots-Service dem Banking-Service, welcher Betrag gebucht
 * werden soll. Positive Werte sind Gewinne, negative Werte sind Verluste.
 */
public record BankingTransactionRequest(
        String invoicing_party,
        Long user,
        BigDecimal amount
) {}
