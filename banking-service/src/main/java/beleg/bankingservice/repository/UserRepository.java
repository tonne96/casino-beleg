package beleg.bankingservice.repository;

import beleg.bankingservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * direkte Verbindung zhur Datenbank
 * JpaRepo gibt ferrtige Sammlung an Methoden
 * z.B. findbyid, findall, save etc.
 * Spring Data JPA schreibt anfragen in SQL alleine um
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Keine zusätzlichen Methoden nötig — alle benötigten
    // Operationen sind im Standard-Interface enthalten.
}