package beleg.slotsservice.handler.game;

import beleg.slotsservice.model.SlotGameResult;
import beleg.slotsservice.model.SlotSymbol;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SlotGameHandler = Implementierung der Slot-Spiellogik.
 *
 * Diese Klasse kennt kein HTTP, keine Datenbank und kein Banking.
 * Sie entscheidet nur, welche Symbole erscheinen und wie die Runde bewertet wird.
 */
@Service
public class SlotGameHandler implements ISlotGameHandler {

    private static final int REEL_COUNT = 3;
    private static final int JACKPOT_MULTIPLIER = 10;
    private static final int THREE_EQUAL_MULTIPLIER = 3;
    private static final int TWO_EQUAL_MULTIPLIER = 1;

    private final Random random;

    public SlotGameHandler() {
        this.random = new SecureRandom();
    }

    /**
     * Spielt eine echte zufaellige Slot-Runde.
     */
    @Override
    public SlotGameResult play(BigDecimal betAmount) {
        validateBetAmount(betAmount);
        return evaluate(betAmount, generateSlotStates());
    }

    /**
     * Bewertet eine Slot-Runde mit vorgegebenen Symbolen.
     * Diese Methode ist besonders gut testbar, weil kein Zufall beteiligt ist.
     */
    @Override
    public SlotGameResult evaluate(BigDecimal betAmount, List<SlotSymbol> slotStates) {
        validateBetAmount(betAmount);
        validateSlotStates(slotStates);

        int multiplier = calculatePayoutMultiplier(slotStates);
        boolean winning = multiplier > 0;

        BigDecimal amount = calculateNetAmount(betAmount, multiplier);

        return new SlotGameResult(winning, amount, slotStates, multiplier);
    }

    /**
     * Berechnet den Netto-Betrag fuer Banking und Response.
     *
     * Der Einsatz wird bei jeder Runde zuerst riskiert. Bei einem Gewinn wird
     * die Auszahlung dagegen gerechnet:
     *  - Verlust: 0x Auszahlung - 1x Einsatz = -1x Einsatz
     *  - Zwei gleiche: 1x Auszahlung - 1x Einsatz = 0
     *  - Drei gleiche: 3x Auszahlung - 1x Einsatz = +2x Einsatz
     *  - Jackpot: 10x Auszahlung - 1x Einsatz = +9x Einsatz
     */
    private BigDecimal calculateNetAmount(BigDecimal betAmount, int payoutMultiplier) {
        BigDecimal payoutAmount = betAmount.multiply(BigDecimal.valueOf(payoutMultiplier));
        return payoutAmount.subtract(betAmount);
    }

    private List<SlotSymbol> generateSlotStates() {
        SlotSymbol[] symbols = SlotSymbol.values();
        List<SlotSymbol> slotStates = new ArrayList<>();

        for (int reel = 0; reel < REEL_COUNT; reel++) {
            slotStates.add(symbols[random.nextInt(symbols.length)]);
        }

        return slotStates;
    }

    private int calculatePayoutMultiplier(List<SlotSymbol> slotStates) {
        if (isJackpot(slotStates)) {
            return JACKPOT_MULTIPLIER;
        }
        if (allSymbolsEqual(slotStates)) {
            return THREE_EQUAL_MULTIPLIER;
        }
        if (hasTwoEqualSymbols(slotStates)) {
            return TWO_EQUAL_MULTIPLIER;
        }
        return 0;
    }

    private boolean isJackpot(List<SlotSymbol> slotStates) {
        for (SlotSymbol symbol : slotStates) {
            if (symbol != SlotSymbol.SEVEN) {
                return false;
            }
        }

        return true;
    }

    private boolean allSymbolsEqual(List<SlotSymbol> slotStates) {
        SlotSymbol firstSymbol = slotStates.get(0);
        SlotSymbol secondSymbol = slotStates.get(1);
        SlotSymbol thirdSymbol = slotStates.get(2);

        return firstSymbol == secondSymbol && firstSymbol == thirdSymbol;
    }

    private boolean hasTwoEqualSymbols(List<SlotSymbol> slotStates) {
        SlotSymbol first = slotStates.get(0);
        SlotSymbol second = slotStates.get(1);
        SlotSymbol third = slotStates.get(2);

        return first == second || first == third || second == third;
    }

    private void validateBetAmount(BigDecimal betAmount) {
        if (betAmount == null) {
            throw new IllegalArgumentException("BetAmount darf nicht null sein.");
        }
        if (betAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("BetAmount muss groesser als 0 sein.");
        }
    }

    private void validateSlotStates(List<SlotSymbol> slotStates) {
        if (slotStates == null || slotStates.size() != REEL_COUNT) {
            throw new IllegalArgumentException("Eine Slot-Runde braucht genau drei Symbole.");
        }

        for (SlotSymbol symbol : slotStates) {
            if (symbol == null) {
                throw new IllegalArgumentException("Slot-Symbole duerfen nicht null sein.");
            }
        }
    }
}
