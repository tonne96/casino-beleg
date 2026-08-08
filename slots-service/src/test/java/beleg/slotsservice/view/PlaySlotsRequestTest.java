package beleg.slotsservice.view;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit-Tests für Eingaben des Play-Endpunkts.
 */
class PlaySlotsRequestTest {

    @Test
    void validateAcceptsRandomizedPositiveValues() {
        Random testRandom = new Random(20260802L);

        for (int testRun = 0; testRun < 250; testRun++) {
            Long userId = testRandom.nextLong(1, Long.MAX_VALUE);
            BigDecimal betAmount = BigDecimal.valueOf(testRandom.nextLong(1, 100_000_000), 2);
            PlaySlotsRequest request = new PlaySlotsRequest(userId, betAmount);

            request.validate();

            assertEquals(userId, request.user());
            assertEquals(betAmount, request.betAmount());
        }
    }

    @Test
    void validateAcceptsPositiveExtremeValues() {
        PlaySlotsRequest smallestBet = new PlaySlotsRequest(1L, new BigDecimal("0.01"));
        PlaySlotsRequest largestValues = new PlaySlotsRequest(
                Long.MAX_VALUE,
                new BigDecimal("99999999999999999.99")
        );

        smallestBet.validate();
        largestValues.validate();
    }

    @Test
    void validateRejectsNullUser() {
        expectInvalidRequest(new PlaySlotsRequest(null, BigDecimal.TEN));
    }

    @Test
    void validateRejectsZeroAndNegativeUsers() {
        expectInvalidRequest(new PlaySlotsRequest(0L, BigDecimal.TEN));
        expectInvalidRequest(new PlaySlotsRequest(-1L, BigDecimal.TEN));
        expectInvalidRequest(new PlaySlotsRequest(Long.MIN_VALUE, BigDecimal.TEN));
    }

    @Test
    void validateRejectsNullBetAmount() {
        expectInvalidRequest(new PlaySlotsRequest(1L, null));
    }

    @Test
    void validateRejectsZeroAndNegativeBetAmounts() {
        expectInvalidRequest(new PlaySlotsRequest(1L, BigDecimal.ZERO));
        expectInvalidRequest(new PlaySlotsRequest(1L, new BigDecimal("-0.01")));
        expectInvalidRequest(new PlaySlotsRequest(1L, new BigDecimal("-99999999999999999.99")));
    }

    private void expectInvalidRequest(PlaySlotsRequest request) {
        try {
            request.validate();
            fail("Ungueltige Request-Werte muessen abgelehnt werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }
}
