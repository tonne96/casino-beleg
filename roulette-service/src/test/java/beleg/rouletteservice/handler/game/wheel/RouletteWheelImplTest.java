package beleg.rouletteservice.handler.game.wheel;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RouletteWheelImplTest {
	@Test
	void spinReturnsOnlyNumbersBetweenZeroAndThirtySix() {
		RouletteWheelImpl wheel = new RouletteWheelImpl();
		Set<Integer> results = new HashSet<>();

		for (int i = 0; i < 1000; i++) {
			results.add(wheel.spin());
		}

		assertFalse(results.isEmpty());
		assertTrue(results.stream().allMatch(number -> number >= 0 && number <= 36));
	}
}