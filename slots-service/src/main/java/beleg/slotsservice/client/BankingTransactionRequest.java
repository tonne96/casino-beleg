package beleg.slotsservice.client;

import java.math.BigDecimal;

/**
 * Request für eine Slots-Buchung beim Banking-Service.
 * Positive Werte sind Gewinne, negative Werte sind Verluste.
 */
public record BankingTransactionRequest(
        String invoicing_party,
        Long user,
        BigDecimal amount
) {}
