package de.hsrm.mi.softwaretechnik.projekt.business.entities;

import java.io.Serializable;

public class Paket implements Serializable {
    private double breite, hoehe, abstandVonLinks;
    private int gewicht, tragfaehigkeit;

    public Paket() {
    }

    public Paket(double breite, double hoehe, int gewicht) {
        this.breite = breite;
        this.hoehe = hoehe;
        this.gewicht = gewicht;
        this.tragfaehigkeit = gewicht;
    }

    public double getBreite() {
        return breite;
    }

    public double getHoehe() {
        return hoehe;
    }

    public int getGewicht() {
        return gewicht;
    }

    public int getTragfaehigkeit() {
        return tragfaehigkeit;
    }

    public double getAbstandVonLinks() {
        return abstandVonLinks;
    }

    public void setAbstandVonLinks(double abstandVonLinks) {
        this.abstandVonLinks = abstandVonLinks;
    }

}