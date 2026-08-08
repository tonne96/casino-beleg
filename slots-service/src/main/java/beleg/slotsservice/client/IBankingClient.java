package beleg.slotsservice.client;

import java.math.BigDecimal;

/**
 * Vertrag für alle Banking-Aufrufe des Slots-Service.
 * Die technische Kommunikation bleibt der Implementierung überlassen.
 */
public interface IBankingClient {

    BankingUserView getUser(Long userId);

    void createSlotsTransaction(Long userId, BigDecimal amount);
}
