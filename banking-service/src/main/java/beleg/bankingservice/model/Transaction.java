package beleg.bankingservice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Domain-Entity: repräsentiert eine Finanztransaktion im Casino.
 *
 * Domainintegrität-Regeln:
 *  - invoicingParty muss ein bekannter Service-Name sein (Enum)
 *  - userId muss gesetzt sein (Fremdschlüssel zum User)
 *  - amount darf nicht null sein
 */
@Entity
@Table(name = "transaction")
public class Transaction {

    /**
     * Bekannte Rechnungssteller (Game-Services).
     * Neue Services müssen hier eingetragen werden —
     * der Beleg verlangt explizit, unbekannte Parteien abzulehnen.
     */
    public enum InvoicingParty {
        ROULETTE,
        SLOTS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "invoicing_party", nullable = false)
    private InvoicingParty invoicingParty;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    // ── JPA benötigt einen parameterlosen Konstruktor ──────
    protected Transaction() {}

    /**
     * Erstellt eine neue Transaktion.
     *
     * @param invoicingParty bekannter Rechnungssteller
     * @param userId         ID des betroffenen Users
     * @param amount         Betrag (positiv oder negativ)
     */
    public Transaction(InvoicingParty invoicingParty, Long userId, BigDecimal amount) {
        if (invoicingParty == null) {
            throw new IllegalArgumentException("InvoicingParty darf nicht null sein.");
        }
        if (userId == null) {
            throw new IllegalArgumentException("UserId darf nicht null sein.");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount darf nicht null sein.");
        }
        this.invoicingParty = invoicingParty;
        this.userId = userId;
        this.amount = amount;
    }

    // ── Getter ────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public InvoicingParty getInvoicingParty() {
        return invoicingParty;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    // ── Update (für PUT-Endpoint) ──────────────────────────

    /**
     * Aktualisiert alle veränderbaren Felder einer Transaktion.
     */
    public void update(InvoicingParty invoicingParty, Long userId, BigDecimal amount) {
        if (invoicingParty == null || userId == null || amount == null) {
            throw new IllegalArgumentException("Kein Parameter darf null sein.");
        }
        this.invoicingParty = invoicingParty;
        this.userId = userId;
        this.amount = amount;
    }

    // ── equals / hashCode ─────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction t)) return false;
        return Objects.equals(id, t.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", invoicingParty=" + invoicingParty
                + ", userId=" + userId + ", amount=" + amount + '}';
    }
}