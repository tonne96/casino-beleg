package beleg.slotsservice.model;

/**
 * Domain-Enum: alle Symbole, die auf einer Slot-Walze erscheinen können.
 *
 * Ein Enum verhindert Tippfehler wie "chery" oder "Seven", weil nur diese
 * festen Werte im Code erlaubt sind.
 */
public enum SlotSymbol {
    CHERRY,
    LEMON,
    BELL,
    BAR,
    SEVEN
}
