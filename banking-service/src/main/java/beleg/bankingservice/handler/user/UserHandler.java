package beleg.bankingservice.handler.user;

import beleg.bankingservice.model.User;
import beleg.bankingservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Handler = Geschäftslogik
 * Controller fragt Handler, Handler kennt Repository aber nicht HTTP und JSON
 */

@Service
public class UserHandler {

    private final UserRepository userRepository;

    // Constructor Injection — von Spring Boot automatisch befüllt
    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Liefert einen User anhand seiner ID.
     *
     * @param id User-ID
     * @return Optional mit User oder leer wenn nicht gefunden
     * Optional = kann auch nichts zurückgeben, anders als festen Datentyp,
     * der kann acuh null zurückgeben, was zu Exceptions führen kann
     */
    public Optional<User> getUser(Long id) {
        // findbyid methode kommt schon fertig von jpa repository
        return userRepository.findById(id);
    }

    /**
     * Liefert alle Users.
     *
     * @return Liste aller Users (leer wenn keine vorhanden)
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Legt einen neuen User an.
     *
     * @param firstName Vorname
     * @param lastName  Nachname
     * @return der gespeicherte User mit generierter ID
     * @throws IllegalArgumentException wenn Namen ungültig sind
     */
    public User createUser(String firstName, String lastName) {
        User user = new User(firstName, lastName);
        return userRepository.save(user);
    }

    /**
     * Aktualisiert den Namen eines bestehenden Users.
     *
     * @param id        User-ID
     * @param firstName neuer Vorname
     * @param lastName  neuer Nachname
     * @return Optional mit aktualisiertem User oder leer wenn nicht gefunden
     */
    public Optional<User> updateUser(Long id, String firstName, String lastName) {
        return userRepository.findById(id).map(user -> {
            user.updateName(firstName, lastName);
            return userRepository.save(user);
        });
    }

    /**
     * Löscht einen User anhand seiner ID.
     *
     * @param id User-ID
     * @return Optional mit dem gelöschten User oder leer wenn nicht gefunden
     */
    public Optional<User> deleteUser(Long id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return user;
        });
    }

    /**
     * Führt eine Einzahlung auf das Konto des Users durch.
     *
     * @param userId   User-ID
     * @param amount   Ganzzahliger Betrag (z.B. 10)
     * @param decimals Nachkommaanteil als ganze Zahl (z.B. 50 → 0.50)
     * @return Optional mit aktualisiertem User oder leer wenn nicht gefunden
     * @throws IllegalArgumentException wenn amount oder decimals ungültig sind
     */
    public Optional<User> deposit(Long userId, long amount, int decimals) {
        validateDepositParams(amount, decimals);

        BigDecimal depositAmount = buildDepositAmount(amount, decimals);

        return userRepository.findById(userId).map(user -> {
            user.adjustBalance(depositAmount);
            return userRepository.save(user);
        });
    }

    /**
     * Passt den Kontostand eines Users um einen Betrag an.
     * Wird vom TransactionController aufgerufen wenn eine
     * neue Transaktion gebucht wird.
     *
     * @param userId User-ID
     * @param amount positiver oder negativer Betrag
     * @return Optional mit aktualisiertem User oder leer wenn nicht gefunden
     */
    public Optional<User> adjustBalance(Long userId, BigDecimal amount) {
        return userRepository.findById(userId).map(user -> {
            user.adjustBalance(amount);
            return userRepository.save(user);
        });
    }

    // ── Private Hilfsmethoden ────────────────────────────

    /**
     * Validiert die Deposit-Parameter gemäß Anforderung:
     * amount und decimals dürfen nicht negativ sein,
     * decimals darf maximal 2 Stellen haben.
     */
    private void validateDepositParams(long amount, int decimals) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount darf nicht negativ sein.");
        }
        if (decimals < 0) {
            throw new IllegalArgumentException("decimals darf nicht negativ sein.");
        }
        if (decimals > 99) {
            throw new IllegalArgumentException("decimals darf maximal 2 Stellen haben (0–99).");
        }
    }

    /**
     * Baut aus amount und decimals einen BigDecimal-Betrag.
     * Beispiel: amount=10, decimals=50 → 10.50
     */
    private BigDecimal buildDepositAmount(long amount, int decimals) {
        BigDecimal base = BigDecimal.valueOf(amount);
        BigDecimal decimalPart = BigDecimal.valueOf(decimals)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY);
        return base.add(decimalPart);
    }
}