package beleg.rouletteservice.rules;

public enum RouletteBetType {
    RED_OR_BLACK, //rot oder schwarze zahl
    ODD_OR_EVEN, //gerade oder ungerade zahl
    LOW_OR_HIGH, // 1-18 oder 19-36
    DOZEN,      // 1-12, 13-24, 25-36
    COLUMN,     // Spalte 34,35 oder 36 aufwärts
    SIX_LINE,   // 6 aufeinanderfolgende Zahlen,  1. Zahl entität linker Spalte
    CORNER,     // 4 Zahlen die in einem viereck liegen Bsp.: 26,27,29,30 --> a linke Spalte oder Mitte , a+1 Mitte oder Rechts , a+3 linke oder Mitte, a+4 Mitte oder Rechts
    STREET,     // 3 aufeinander folgende Zahlen, 1. Zahl entität linker Spalte
    SPLIT,      // zwei benachbarte Zahlen Mitte hat in der Regel 4 mögliche Nachbarn, Linke u Rechte 3
    SINGLE      // eine einzelne Zahl
}

