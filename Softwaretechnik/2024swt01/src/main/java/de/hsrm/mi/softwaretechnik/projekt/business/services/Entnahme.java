package de.hsrm.mi.softwaretechnik.projekt.business.services;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Warenkorb;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutatenpaket;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ListeIstLeerException;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ObjektExistiertNichtException;

import java.util.List;

public interface Entnahme {

    void addZutatenpaketInWarenkorb(Zutatenpaket zutatenpaket) throws ObjektExistiertNichtException;

    List<Zutatenpaket> sucheZutatenPaket(String suchtext);

    List<Zutatenpaket> zeigeAlleZutatenpaketeImWarenkorb();

    List<Zutatenpaket> zeigeAlleZutatenPaketeImLager();

    void entnahmeAbschliessen() throws ObjektExistiertNichtException;

    void zutatMitIdAusWarenkorbEntfernen(Zutatenpaket zutatenpaket) throws ObjektExistiertNichtException, ListeIstLeerException;

    Warenkorb getWarenkorb();
}
