package beleg.slotsservice.repository;

import beleg.slotsservice.model.SlotGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Datenbankzugriff für gespeicherte Slot-Ergebnisse.
 */
@Repository
public interface IGameResultRepository extends JpaRepository<SlotGame, Long> {

    List<SlotGame> findByUserId(Long userId);
}
