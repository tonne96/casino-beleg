package beleg.slotsservice.handler.info;

import beleg.slotsservice.view.SlotsChancesView;
import beleg.slotsservice.view.SlotsRulesView;

/**
 * Interface fuer die Informations-Endpunkte des Slots-Service.
 *
 * Der Controller braucht nur diesen Vertrag und nicht die konkrete Berechnung.
 */
public interface ISlotInfoHandler {

    /**
     * Liefert Spielregeln und Auszahlungsregeln.
     */
    SlotsRulesView getRules();

    /**
     * Liefert Gewinnchancen fuer die aktuellen Slot-Regeln.
     */
    SlotsChancesView getChances();
}
