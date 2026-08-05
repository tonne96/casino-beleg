package beleg.rouletteservice.handler.info;

import beleg.rouletteservice.rules.RouletteBetType;
import beleg.rouletteservice.rules.RoulettePayoutRules;
import org.springframework.stereotype.Service;

@Service
public class RouletteInfoHandlerImpl implements IRouletteInfoHandler {

    private final RoulettePayoutRules payoutRules;

    public RouletteInfoHandlerImpl(RoulettePayoutRules payoutRules) {
        this.payoutRules = payoutRules;
    }

    @Override
    public String getRules() {
        return """
                Roulette - Spielregeln
                Jede Runde besteht aus genau einem Einsatz und einer Ziehung der
                Gewinnzahl (0 bis 36). Ein Einsatz wird ueber "bet_type" und
                "bet_numbers" angegeben. Was in "bet_numbers" stehen muss, haengt
                vom gewaehlten "bet_type" ab:
                
                Aussenwetten (Quote ca. 1:1 bis 2:1):
                - RED_OR_BLACK -> bet_numbers: [0|1] (0 = Schwarz, 1 = Rot).
                    Die 0 gewinnt bei keiner Farbe.
                - ODD_OR_EVEN -> bet_numbers: [0|1] (0 = Gerade, 1 = Ungerade).
                    Die 0 zaehlt als keins von beidem.
                - LOW_OR_HIGH -> bet_numbers: [0|1] (0 = Niedrig 1-18, 1 = Hoch 19-36).
                - DOZEN -> bet_numbers: [1|2|3] fuer 1., 2. bzw. 3. Dutzend
                    (1-12, 13-24, 25-36).
                - COLUMN -> bet_numbers: [1|2|3] fuer die 1., 2. bzw. 3. Spalte
                    des Tableaus.
                
                Innenwetten (hoeheres Risiko, hoehere Quote):
                - SIX_LINE -> bet_numbers: [a], a aus {1,4,7,...,31}. Deckt die
                    6 Zahlen a bis a+5 ab (zwei zusammenhaengende Reihen).
                - CORNER -> bet_numbers: [a], a von 1-32 mit a modulo 3 ungleich 0.
                    Deckt das Viereck a, a+1, a+3, a+4 ab.
                - STREET -> bet_numbers: [a], a aus {1,4,7,...,34}. Deckt die
                    3 Zahlen a bis a+2 ab (eine Reihe).
                - SPLIT -> bet_numbers: [a, b], zwei auf dem Tableau echt
                    benachbarte Zahlen (horizontal in derselben Reihe oder
                    vertikal in derselben Spalte), je 0-36.
                    Die 0 ist mit 1,2 und 3 benachtbart.
                - SINGLE -> bet_numbers: [zahl], genau eine Zahl von 0 bis 36.
                
                Nach der Ziehung wird pro Runde ein einzelner Betrag verbucht:
                bei Gewinn der Nettogewinn (Einsatz * Auszahlungsquote), bei
                Verlust der negative Einsatzbetrag.
                """;
    }

    @Override
    public String getChances() {
        StringBuilder text = new StringBuilder();
        text.append("Gewinnchancen und Auszahlungsquoten\n\n");
        text.append("Berechnung: Nettogewinn = Einsatz * Auszahlungsquote\n\n");

        for (RouletteBetType betType : RouletteBetType.values()) {
            int multiplier = payoutRules.getPayoutMultiplier(betType);
            text.append(String.format("%-13s Quote %d:1%n", betType.name(), multiplier));
        }

        return text.toString();
    }
}

