package de.hsrm.mi.softwaretechnik.projekt.business.services;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Paket;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutat;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutatenpaket;

import java.util.List;
import java.util.UUID;

public interface Zutatenpaketverwaltung {

    List<Zutat> sucheZutat(String suchtext);

    Zutat erstelleNeueZutat(String name);

    Zutatenpaket erstelleZutatenpaket(Zutat zutat, Paket paket);

    List<Paket> getPaketListe();

    List<Zutat> getZutatenliste();

    List<Zutatenpaket> getZutatenpaketListe();

    void loescheZutatenpaketMitId(UUID id);
}
