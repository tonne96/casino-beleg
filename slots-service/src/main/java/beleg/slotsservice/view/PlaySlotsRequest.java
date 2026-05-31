package beleg.slotsservice.view;

import java.math.BigDecimal;

/**
 * Request-DTO fuer das Spielen einer Slot-Runde.
 *
 * Dieses Objekt beschreibt nur, was von aussen in den Slots-Service reinkommt.
 * Die eigentliche Spiellogik bleibt im SlotGameHandler.
 */
public record PlaySlotsRequest(
        Long user,
        BigDecimal betAmount
) {
    public void validate() {
        if (user == null) {
            throw new IllegalArgumentException("User darf nicht null sein.");
        }
        if (user <= 0) {
            throw new IllegalArgumentException("User muss groesser als 0 sein.");
        }
        if (betAmount == null) {
            throw new IllegalArgumentException("BetAmount darf nicht null sein.");
        }
        if (betAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("BetAmount muss groesser als 0 sein.");
        }
    }
}
