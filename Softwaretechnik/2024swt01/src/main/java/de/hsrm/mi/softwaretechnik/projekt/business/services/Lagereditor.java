package de.hsrm.mi.softwaretechnik.projekt.business.services;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Regal;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Regalboden;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Regalwand;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.RegalkomponenteNichtLoeschbarException;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.RegalkomponenteNichtPlatzierbarException;

public interface Lagereditor {

    Regalwand erstelleRegalwand();

    Regalboden erstelleRegalboden();

    void platziereRegalwand(Regalwand regalwand, double posX) throws RegalkomponenteNichtPlatzierbarException;

    void platziereRegalboden(Regalboden regalboden, double yPos, double xPos) throws IllegalStateException, RegalkomponenteNichtPlatzierbarException;

    void loescheRegalwand(Regalwand regalwand) throws RegalkomponenteNichtLoeschbarException;

    void loescheRegalboden(Regalboden regalboden) throws RegalkomponenteNichtLoeschbarException;

    void editierenAbschliessen();

    void editierenAbbrechen();

    Regal erhalteRegal();

    void setzeNeuesRegal(Regal regal);

    boolean bereitsZutatenpaketePlatziert();

    void berechneAblagebereiche();

}
