package beleg.slotsservice.client;

import java.math.BigDecimal;

/**
 * Interface fuer alle Banking-Aufrufe, die der Slots-Service braucht.
 *
 * Der Slots-Service haengt damit nur von diesem Vertrag ab.
 * Wie Banking technisch erreicht wird, entscheidet die Implementierung.
 */
public interface IBankingClient {

    /**
     * Liefert den User aus dem Banking-Service.
     */
    BankingUserView getUser(Long userId);

    /**
     * Bucht Gewinn oder Verlust einer Slot-Runde im Banking-Service.
     */
    void createSlotsTransaction(Long userId, BigDecimal amount);
}
