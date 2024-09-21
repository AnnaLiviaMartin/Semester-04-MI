package de.hsrm.mi.softwaretechnik.projekt.business.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.hsrm.mi.softwaretechnik.projekt.business.enums.AblageflaechenTyp;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ObjektExistiertNichtException;
import de.hsrm.mi.softwaretechnik.projekt.business.services.Platzierbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.UUIDGenerator.class,
        property = "id")
public class Ablagebereich implements Serializable {
    @JsonManagedReference
    private List<Zutatenpaket> zutatenpakete = new ArrayList<>();
    private double hoehe, breite;       /*die Höhe des Ablagebereichs*/
    private double posY, posX;
    private UUID id;
    private Regalboden untererRegalboden;

    public Ablagebereich() {
    }

    public Ablagebereich(double hoehe, double breite, Regalboden untererRegalboden) {
        id = UUID.randomUUID();
        zutatenpakete = new ArrayList<>();
        this.hoehe = hoehe;
        this.breite = breite;
        this.untererRegalboden = untererRegalboden;
    }

    public void setPositions(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Gibt alle Zutatenpakete aus, die auf einer Ablageflaeche mit
     * einem anderen Zutatenpaket liegen.
     * Beispiel: findet c und b, wenn Ablageflaeche a hineingegeben wird
     * -----
     * | e |
     * ------
     * |    |
     * |  d |
     * --------   -----
     * |      |   |   |
     * |  c   |   | b |
     * -----------------
     * |               |
     * |       a       |
     * |-------------------------------------------|
     * <- Regalboden->
     *
     * @param ablageflaeche auf der die Pakete liegen
     * @return eine Liste der Pakete, die auf der Ablagefläche liegen
     */
    public List<Zutatenpaket> findeAlleAufEinerAblageflaecheLiegendenZutatenpakete(Ablageflaeche ablageflaeche) {
        List<Zutatenpaket> nebeneinanderLiegendePakete = new ArrayList<>();

        for (Zutatenpaket paket : this.zutatenpakete) {
            if (paket.getAblageflaeche().equals(ablageflaeche)) {
                nebeneinanderLiegendePakete.add(paket);
            }
        }

        return nebeneinanderLiegendePakete;
    }

    /**
     * Findet alle Pakete, die genau unter der angegebenen Ablageflaeche liegen (nicht
     * ganze Paketstapel).
     * Beispiel: findet acd als Stapel, wenn Ablageflaeche e hineingegeben wird
     * -----
     * | e |
     * ------
     * |    |
     * |  d |
     * -------   -----
     * |      |   |   |
     * |  c   |   | b |
     * -----------------
     * |               |
     * |       a       |
     * |-------------------------------------------|
     * <- Regalboden->
     *
     * @param ablageflaeche für die man alle darunterliegenden Pakete möchte
     * @return Liste aller darunterliegenden Pakete
     */
    public List<Zutatenpaket> findeUnmittelbarUnterliegendePakete(Ablageflaeche ablageflaeche) {
        List<Zutatenpaket> darunterliegendePakete = new ArrayList<>();
        if (ablageflaeche.getTyp() == AblageflaechenTyp.REGALBODEN) {
            return darunterliegendePakete;
        }

        Ablageflaeche unterereAblageflaeche = ablageflaeche.getAblageflaeche();

        while (unterereAblageflaeche.getTyp() != AblageflaechenTyp.REGALBODEN) {
            darunterliegendePakete.add((Zutatenpaket) unterereAblageflaeche);
            unterereAblageflaeche = unterereAblageflaeche.getAblageflaeche();
        }

        return darunterliegendePakete;
    }

    /**
     * Gibt alle Pakete aus, die auf der spezifizierten Ablagefläche (einem Paket
     * oder einem unteren Regalboden) im Ablagebereich liegen.
     * Beispiel: findet cbde, wenn Ablageflaeche a hineingegeben wird
     * -----
     * | e |
     * ------
     * |    |
     * |  d |
     * -------   -----
     * |      |   |   |
     * |  c   |   | b |
     * -----------------
     * |               |
     * |       a       |
     * |-------------------------------------------|
     * <- Regalboden->
     *
     * @param ablageflaeche auf der die Pakete liegen
     * @return eine Liste der Pakete, die auf der Ablagefläche liegen
     */
    public List<Zutatenpaket> findeAlleAufliegendenPakete(Ablageflaeche ablageflaeche) {
        List<Zutatenpaket> aufliegendePakete = new ArrayList<>();
        aufliegendePakete = findeAufliegendePakete(ablageflaeche, aufliegendePakete);
        return aufliegendePakete;
    }

    /**
     * Findet alle Pakete, die auf einer Ablageflaeche liegen.
     * Beispiel: Paket a wird hereingegeben, findet im 1. Schritt cb und
     * findet dann rekursiv noch d und dann e
     * -----
     * | e |
     * ------
     * |    |
     * |  d |
     * -------   -----
     * |      |   |   |
     * |  c   |   | b |
     * -----------------
     * |               |
     * |       a       |
     * |-------------------------------------------|
     * <- Regalboden ->
     *
     * @param ablageflaeche     auf der alle Zutatenpakete gesucht werden
     * @param aufliegendePakete die Liste aller bereits gefundenen Zutatenpakete
     * @return eine Liste aller Zutatenpakete die auf der ablageflaeche liegen
     */
    private List<Zutatenpaket> findeAufliegendePakete(Ablageflaeche ablageflaeche, List<Zutatenpaket> aufliegendePakete) {
        for (Zutatenpaket zutatenpaket : this.zutatenpakete) {
            //wenn Zutatenpaket auf Ablagefläche liegt -> füge Liste hinzu
            if (zutatenpaket.getAblageflaeche().getUuid().equals(ablageflaeche.getUuid())) {
                if(!aufliegendePakete.contains(zutatenpaket)){
                    aufliegendePakete.add(zutatenpaket);
                    // suche für dieses Paket nach allen folgenden daraufliegenden Paketen
                    aufliegendePakete = findeAufliegendePakete(zutatenpaket, aufliegendePakete);
                }
            }
        }
        return aufliegendePakete;
    }

    /**
     * Berechnet das Gesamtgewicht aller auf einer Ablagefläche liegenden Pakete.
     *
     * @param ablageflaeche auf der die Pakete liegen
     * @return das Gesamtgewicht aller aufliegenden Pakete
     */
    public double getGewichtAllerAufliegendenPakete(Ablageflaeche ablageflaeche) {
        List<Zutatenpaket> aufliegendePakete = findeAlleAufliegendenPakete(ablageflaeche);
        double gesamtgewicht = 0.0;

        for (Zutatenpaket paket : aufliegendePakete) {
            gesamtgewicht += paket.getPaket().getGewicht();
        }

        return gesamtgewicht;
    }

    public List<Zutatenpaket> getZutatenpakete() {
        return zutatenpakete;
    }

    public double getHoehe() {
        return hoehe;
    }

    public double getBreite() {
        return this.breite;
    }

    public void addZutatenpaket(Platzierbar zutatenpaket) throws ObjektExistiertNichtException {
        if (zutatenpaket == null) {
            throw new ObjektExistiertNichtException("Zutatenpaket nicht initialisiert!");
        }

        if (this.zutatenpakete == null) {
            throw new ObjektExistiertNichtException("Keine Zutatenpaketliste initialisiert!");
        }

        if (zutatenpaket instanceof Zutatenpaket) {
            this.zutatenpakete.add((Zutatenpaket) zutatenpaket);
        } else if (zutatenpaket instanceof ZutatenpaketStapel) {
            ZutatenpaketStapel stapel = (ZutatenpaketStapel) zutatenpaket;
            this.zutatenpakete.addAll(stapel.getZutatenpakete());
        }


    }

    /**
     * Entfernt ein Zutatenpaket anhand seiner ID
     *
     * @param id id
     */
    public void deleteZutatenpaket(UUID id) {
        if (!this.zutatenpakete.isEmpty()) {
            zutatenpakete.removeIf(zutatenpaketToDelete -> zutatenpaketToDelete.getUuid() == id);
        }

        System.out.println(zutatenpakete.size());

        /* TODO: kann entfernt werden, falls das oben drüber klappt
        if (!this.zutatenpakete.isEmpty()) {
            for (Zutatenpaket zutatenpaketToDelete : zutatenpakete) {
                if (zutatenpaketToDelete.getId() == id) {
                    zutatenpakete.remove(zutatenpaketToDelete);
                }
            }
        }
         */
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public Regalboden getUntererRegalboden() {
        return untererRegalboden;
    }

    public UUID getId() {
        return id;
    }

    public void setUntererRegalboden(Regalboden untererRegalboden) {
        this.untererRegalboden = untererRegalboden;
    }

    public void deleteZutatenpaketByUUID(Zutatenpaket toBeDeletedZutatenpaket){
        for(Zutatenpaket zutatenpaket: zutatenpakete){
            if(toBeDeletedZutatenpaket.getUuid().equals(zutatenpaket.getUuid())){
                zutatenpakete.remove(zutatenpaket);
            }
        }
    }

}
