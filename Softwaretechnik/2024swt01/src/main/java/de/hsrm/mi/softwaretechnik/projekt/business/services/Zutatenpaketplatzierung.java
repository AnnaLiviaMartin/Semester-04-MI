package de.hsrm.mi.softwaretechnik.projekt.business.services;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Ablagebereich;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Ablageflaeche;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutatenpaket;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ObjektExistiertNichtException;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.PlatzierungNichtMoeglichException;

public interface Zutatenpaketplatzierung {

    boolean pruefePlatzierung(Platzierbar zutatenpaket, Ablagebereich ablagebereich, Ablageflaeche ablageflaeche, double xPos, double yPos) throws PlatzierungNichtMoeglichException;

    boolean platziereZutatenpaket(Platzierbar zutatenpaket, Ablagebereich ablagebereich, Ablageflaeche unterliegendeAblageflaeche, double xPos, double yPos) throws ObjektExistiertNichtException;

    boolean platzierbar(Platzierbar zutatenpaket, Ablagebereich newAblagebereich, Ablageflaeche unterliegendeAblageflaeche, double xPosDrop);

    boolean verschiebeZutatenpaket(Platzierbar zutatenpaket, Ablagebereich newAblagebereich, Ablageflaeche ablageflaeche, double xPos, double yPos) throws ObjektExistiertNichtException;

    void platzierenAbschliessen();

    void platzierenAbbrechen();

    Ablageflaeche getAblageflaeche(double xPosDrop, double yPosDrop, Zutatenpaket zutatenpaket, Ablagebereich ablagebereich);

    String getErrorMessage();
}
