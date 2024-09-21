package de.hsrm.mi.softwaretechnik.projekt.business.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.hsrm.mi.softwaretechnik.projekt.business.enums.AblageflaechenTyp;

import java.io.Serializable;
import java.util.UUID;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "id")
public class Regalboden implements Ablageflaeche, Serializable {
    private double hoehe;
    private UUID uuid;
    private double breite;
    private double xPos, yPos;
    private final int tragfaehigkeit = 1000000;
    private boolean teilVonRahmen;
    private final AblageflaechenTyp typ = AblageflaechenTyp.REGALBODEN;
    private Ablageflaeche ablageflaeche;
    private double abstandVonLinks;

    public Regalboden() {
    }

    public Regalboden(double hoehe) {
        this.uuid = UUID.randomUUID();
        this.hoehe = hoehe;
        teilVonRahmen = false;
    }

    public Regalboden(double hoehe, boolean bautRahmen) {
        this(hoehe);
        this.teilVonRahmen = bautRahmen;
    }

    public boolean isTeilVonRahmen() {
        return teilVonRahmen;
    }

    public void setPositionen(double x, double y) {
        xPos = x;
        yPos = y;
    }

    public double getHoehe() {
        return hoehe;
    }

    public double getBreite() {
        return breite;
    }

    public void setBreite(double breite) {
        this.breite = breite;
    }

    @Override
    public double getxPos() {
        return xPos;
    }

    public void setxPos(double xPos) {
        this.xPos = xPos;
    }

    @Override
    public double getyPos() {
        return yPos;
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    public void setyPos(double yPos) {
        this.yPos = yPos;
    }

    public int getTragfaehigkeit() {
        return this.tragfaehigkeit;
    }

    @Override
    public AblageflaechenTyp getTyp() {
        return typ;
    }

    @Override
    public double getAbstandVonLinks() {
        return abstandVonLinks;
    }


    // Optional: Eine Methode, um die Ablagefläche zu setzen
    public void setAblageflaeche(Ablageflaeche ablageflaeche) {
        this.ablageflaeche = ablageflaeche;
    }

    // Optional: Eine Methode, um die Ablagefläche zu bekommen
    public Ablageflaeche getAblageflaeche() {
        return this.ablageflaeche;
    }

}