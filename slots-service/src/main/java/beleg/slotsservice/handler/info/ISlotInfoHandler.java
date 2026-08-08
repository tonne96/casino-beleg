package beleg.slotsservice.handler.info;

import beleg.slotsservice.view.SlotsChancesView;
import beleg.slotsservice.view.SlotsRulesView;

/**
 * Vertrag für Spielregeln und Gewinnchancen.
 */
public interface ISlotInfoHandler {

    SlotsRulesView getRules();

    SlotsChancesView getChances();
}
