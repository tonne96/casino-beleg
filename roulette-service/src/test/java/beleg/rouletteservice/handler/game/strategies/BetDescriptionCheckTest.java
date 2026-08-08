package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.rules.RouletteBetType;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BetDescriptionCheckTest {

	@Test
	void constructorDoesNotThrowWhenAllDescriptionsExist() {
		MessageSource messages = mock(MessageSource.class);
		when(messages.getMessage(anyString(), isNull(), anyString(), eq(Locale.GERMAN)))
				.thenAnswer(invocation -> "Beschreibung vorhanden");

		assertDoesNotThrow(() -> new BetDescriptionCheck(messages));
	}

	@Test
	void constructorThrowsExceptionWhenDescriptionIsMissing() {
		MessageSource messages = mock(MessageSource.class);
		when(messages.getMessage(anyString(), isNull(), anyString(), eq(Locale.GERMAN)))
				.thenAnswer(invocation -> {
					String key = invocation.getArgument(0);

					if (key.equals("bet." + RouletteBetType.SINGLE.name() + ".description")) {
						return key;
					}

					return "Beschreibung vorhanden";
				});

		assertThrows(IllegalStateException.class, () -> new BetDescriptionCheck(messages));
	}
}