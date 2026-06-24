package beleg.bankingservice.repository;

import beleg.bankingservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * direkte Verbindung zhur Datenbank
 * JpaRepo gibt ferrtige Sammlung an Methoden
 * z.B. findbyid, findall, save etc.
 * Spring Data JPA schreibt anfragen in SQL alleine um
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Liefert alle Transaktionen eines bestimmten Users.
     *
     * @param userId ID des Users
     * @return Liste der Transaktionen (leer wenn keine vorhanden)
     */
    List<Transaction> findByUserId(Long userId);
}
