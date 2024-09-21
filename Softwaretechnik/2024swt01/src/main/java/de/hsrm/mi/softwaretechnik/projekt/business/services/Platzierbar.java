package de.hsrm.mi.softwaretechnik.projekt.business.services;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Ablagebereich;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Ablageflaeche;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutat;

import java.util.List;

public interface Platzierbar {
    double getBreite();

    double getxPos();

    double getyPos();

    double getHoehe();

    double getGewicht();

    List<Zutat> getUnvertraeglichkeit();

    void setAblagebereich(Ablagebereich ablagebereich);

    void setAblageflaeche(Ablageflaeche ablageflaeche);

    void setPositionen(double x, double y);

    void setxPos(double x);

    String getZutatName();

}
