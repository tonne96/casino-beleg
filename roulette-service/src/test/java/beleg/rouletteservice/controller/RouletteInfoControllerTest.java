package beleg.rouletteservice.controller;

import beleg.rouletteservice.handler.info.IRouletteInfoHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouletteInfoControllerTest {

	private IRouletteInfoHandler infoService;
	private RouletteInfoController controller;

	@BeforeEach
	void setUp() {
		infoService = mock(IRouletteInfoHandler.class);
		controller = new RouletteInfoController(infoService);
	}

	@Test
	void getRulesReturnsRulesFromInfoService() {
		String rules = "Roulette rules";
		when(infoService.getRules()).thenReturn(rules);

		ResponseEntity<String> response = controller.getRules();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(rules, response.getBody());
		verify(infoService).getRules();
	}

	@Test
	void getChancesReturnsChancesFromInfoService() {
		String chances = "Roulette chances";
		when(infoService.getChances()).thenReturn(chances);

		ResponseEntity<String> response = controller.getChances();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(chances, response.getBody());
		verify(infoService).getChances();
	}
}