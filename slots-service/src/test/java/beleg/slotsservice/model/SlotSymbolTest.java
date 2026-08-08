package beleg.slotsservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Tests für die erlaubten Symbole des Spielautomaten.
 */
class SlotSymbolTest {

    @Test
    void valuesContainsAllSupportedSlotSymbols() {
        SlotSymbol[] expected = {
                SlotSymbol.CHERRY,
                SlotSymbol.LEMON,
                SlotSymbol.BELL,
                SlotSymbol.BAR,
                SlotSymbol.SEVEN
        };

        assertArrayEquals(expected, SlotSymbol.values());
    }

    @Test
    void valueOfUsesExactEnumName() {
        assertEquals(SlotSymbol.SEVEN, SlotSymbol.valueOf("SEVEN"));
    }
}
