package beleg.bankingservice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Domain-Entity: repräsentiert einen Casino-Nutzer mit Kontostand.
 *
 * Domainintegrität-Regeln (werden im Konstruktor erzwungen):
 *  - firstName und lastName dürfen nicht null/leer sein
 *  - balance darf niemals null sein (Startwert: 0.00)
 *
 * Instanzen dürfen zu keinem Zeitpunkt die Domainintegrität
 * verletzen — daher kein public Setter für balance.
 * Kontostandänderungen nur über die Methode adjustBalance().
 */
@Entity
@Table(name = "casino_user")  // "user" ist ein reserviertes Wort in PostgreSQL
public class User implements IUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    // ── parameterloser Konstruktor für JPA ──────
    protected User() {}

    /**
     * Erstellt einen neuen User mit Startguthaben 0.
     *
     * @param firstName Vorname — darf nicht null oder leer sein
     * @param lastName  Nachname — darf nicht null oder leer sein
     * @throws IllegalArgumentException bei ungültigen Parametern
     */
    public User(String firstName, String lastName) {
        setFirstName(firstName);
        setLastName(lastName);
        this.balance = BigDecimal.ZERO;
    }

    // ── Getter ────────────────────────────────────────────
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    // ── Domainmethoden ────────────────────────────────────

    /**
     * Ändert den Kontostand um den angegebenen Betrag.
     * Positive Werte = Einzahlung, negative Werte = Auszahlung.
     *
     * @param amount Betrag (darf nicht null sein)
     * @throws IllegalArgumentException wenn amount null ist
     */
    public void adjustBalance(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount darf nicht null sein.");
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * Aktualisiert den Namen des Users.
     * Wird beim PUT-Endpoint genutzt.
     */
    public void updateName(String firstName, String lastName) {
        setFirstName(firstName);
        setLastName(lastName);
    }

    // private helper

    private void setFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("firstName cannot be empty.");
        }
        this.firstName = firstName.trim();
    }

    private void setLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("lastName cannot be empty.");
        }
        this.lastName = lastName.trim();
    }

    // overwrites

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", firstName='" + firstName + "', lastName='" + lastName + "', balance=" + balance + '}';
    }
}
