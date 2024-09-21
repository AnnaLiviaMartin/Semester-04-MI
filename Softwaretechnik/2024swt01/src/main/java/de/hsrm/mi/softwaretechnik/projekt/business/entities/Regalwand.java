package de.hsrm.mi.softwaretechnik.projekt.business.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Regalwand {
    private UUID id;
    private double xPos, yPos;
    private double hoehe;
    private double breite;
    private boolean istGesetzt, teilVonRahmen;
    private List<Regalboden> tragendeRegalboeden;

    public Regalwand() {
    }

    public Regalwand(double hoehe, double breite) {
        tragendeRegalboeden = new ArrayList<>();
        this.id = UUID.randomUUID();
        this.hoehe = hoehe;
        this.breite = breite;

        teilVonRahmen = false;
        istGesetzt = false;
    }

    public boolean isTeilVonRahmen() {
        return teilVonRahmen;
    }

    public Regalwand(double hoehe, double breite, boolean bautRahmen) {
        this(hoehe, breite);
        this.teilVonRahmen = bautRahmen;
    }

    public void setPositionen(double xPos, double yPos) {
        istGesetzt = true;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public double getHoehe() {
        return hoehe;
    }

    public void setHoehe(double hoehe) {
        this.hoehe = hoehe;
    }

    public double getBreite() {
        return breite;
    }

    public void setBreite(double breite) {
        this.breite = breite;
    }

    public double getxPos() {
        return xPos;
    }

    public boolean isIstGesetzt() {
        return istGesetzt;
    }

    public double getyPos() {
        return yPos;
    }

    public UUID getId() {
        return id;
    }

    public List<Regalboden> getTragendeRegalboeden() {
        return tragendeRegalboeden;
    }

}