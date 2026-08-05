package beleg.rouletteservice.repository;

import beleg.rouletteservice.model.RouletteGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface IRouletteGameRepository extends JpaRepository<RouletteGame,Long> {
    List<RouletteGame> findByUserId(Long userId);
}

