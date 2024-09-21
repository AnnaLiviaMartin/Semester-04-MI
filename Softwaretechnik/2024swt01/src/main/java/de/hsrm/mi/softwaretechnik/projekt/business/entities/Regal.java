package de.hsrm.mi.softwaretechnik.projekt.business.entities;

import java.util.ArrayList;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ObjektExistiertNichtException;

import java.util.List;

public class Regal {
    private double hoehe, breite;
    private List<Regalwand> regalwaende;
    private List<Regalboden> regalboeden;
    private List<Ablagebereich> ablagebereiche;
    private Regalboden regalboden, regalDecke;
    private Regalwand aussenwandLinks, aussenwandRechts;

    public Regal() {
    }

    public Regal(List<Regalwand> regalwaende, List<Regalboden> regalboeden, double hoehe, double breite) {
        ablagebereiche = new ArrayList<>();

        if (regalwaende.size() < 2) {
            throw new IllegalArgumentException("Ein Regal muss mindestens zwei Regalwände haben.");
        }

        if (regalboeden.isEmpty()) {
            throw new IllegalArgumentException("Ein Regal benötigt mindestens einen Regalboden.");
        }

        this.regalwaende = regalwaende;
        this.regalboeden = regalboeden;
        this.hoehe = hoehe;
        this.breite = breite;

        richteRahmenEin();
    }

    private void richteRahmenEin(){
        if(regalwaende.getFirst().getxPos() < regalwaende.get(1).getxPos()){
            aussenwandLinks = regalwaende.getFirst();
            aussenwandRechts = regalwaende.get(1);
        } else {
            aussenwandLinks = regalwaende.get(1);
            aussenwandRechts = regalwaende.getFirst();
        }

        if(regalboeden.getFirst().getyPos() < regalboeden.get(1).getyPos()){
            regalDecke = regalboeden.getFirst();
            regalboden = regalboeden.get(1);
        } else {
            regalDecke = regalboeden.get(1);
            regalboden = regalboeden.getFirst();
        }

    }

    public List<Regalwand> getRegalwaende() {
        return regalwaende;
    }

    public void addRegalwand(Regalwand regalwand) throws ObjektExistiertNichtException {
        if (regalwand == null) {
            throw new ObjektExistiertNichtException("Keine Regalwand initialisiert!");
        }

        this.regalwaende.add(regalwand);
    }

    public List<Regalboden> getRegalboeden() {
        return regalboeden;
    }

    public void addRegalboden(Regalboden regalboden) throws ObjektExistiertNichtException {
        if (regalboden == null) {
            throw new ObjektExistiertNichtException("Kein Regalboden initialisiert!");
        }

        this.regalboeden.add(regalboden);
    }

    public List<Ablagebereich> getAblagebereiche() {
        return ablagebereiche;
    }

    public double getHoehe() {
        return hoehe;
    }

    public double getBreite() {
        return breite;
    }

    public Regalboden getRegalboden() {
        return regalboden;
    }

    public Regalboden getRegalDecke() {
        return regalDecke;
    }

    public Regalwand getAussenwandLinks() {
        return aussenwandLinks;
    }

    public Regalwand getAussenwandRechts() {
        return aussenwandRechts;
    }
}
