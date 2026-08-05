package beleg.rouletteservice.repository;

import beleg.rouletteservice.model.RouletteGameImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface IRouletteGameRepository extends JpaRepository<RouletteGameImpl,Long> {
    List<RouletteGameImpl> findByUserId(Long userId);
}

