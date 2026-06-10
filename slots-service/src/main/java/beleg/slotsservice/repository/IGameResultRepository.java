package beleg.slotsservice.repository;

import beleg.slotsservice.model.SlotGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository = direkter Datenbankzugriff fuer gespeicherte Slot-Ergebnisse.
 *
 * JpaRepository bringt Standardmethoden wie findAll, findById, save und delete mit.
 */
@Repository
public interface IGameResultRepository extends JpaRepository<SlotGame, Long> {

    /**
     * Liefert alle gespeicherten Slot-Ergebnisse eines bestimmten Users.
     */
    List<SlotGame> findByUserId(Long userId);
}
