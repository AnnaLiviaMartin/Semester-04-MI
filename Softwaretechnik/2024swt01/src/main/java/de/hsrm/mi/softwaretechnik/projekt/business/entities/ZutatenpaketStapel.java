package de.hsrm.mi.softwaretechnik.projekt.business.entities;

import de.hsrm.mi.softwaretechnik.projekt.business.enums.AblageflaechenTyp;
import de.hsrm.mi.softwaretechnik.projekt.business.services.Platzierbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZutatenpaketStapel implements Platzierbar, Serializable {
    private List<Zutatenpaket> zutatenpakete;
    private Ablagebereich ablagebereich;
    private Ablageflaeche ablageflaeche;    /*das Paket liegt auf ablageflaeche drauf*/
    private final AblageflaechenTyp typ = AblageflaechenTyp.PAKET;
    private double breite, absoluteHoehe, xPos, yPos;
    private int gesamtgewicht;
    List<Zutat> unvertraeglichkeiten = new ArrayList<>();
    private Zutatenpaket unterstesZutatenpaket;
    private Map<Zutatenpaket, Double> lokaleAbstaendeZwischenAblageflaecheUndZutatenpaket = new HashMap<>();

    public ZutatenpaketStapel(List<Zutatenpaket> zutatenpakete, Zutatenpaket unterstesZutatenpaket) {
        this.zutatenpakete = zutatenpakete;
        if (!zutatenpakete.isEmpty())
            this.ablagebereich = unterstesZutatenpaket.getAblagebereich();
        this.breite = unterstesZutatenpaket.getBreite();
        this.absoluteHoehe = calculateHoehe();
        this.xPos = unterstesZutatenpaket.getxPos();
        this.yPos = unterstesZutatenpaket.getyPos() - (this.absoluteHoehe - unterstesZutatenpaket.getHoehe());
        this.gesamtgewicht = calculateGewicht();
        addUnvertraeglichkeitenZusammen();
        this.unterstesZutatenpaket = unterstesZutatenpaket;
        this.ablageflaeche = unterstesZutatenpaket.getAblageflaeche();
        calcLokaleAbstaendeZwischenAblageflaecheUndZutatenpaket();
    }

    private void calcLokaleAbstaendeZwischenAblageflaecheUndZutatenpaket() {
        for (Zutatenpaket zutatenpaket : this.zutatenpakete) {
            if (!zutatenpaket.equals(this.unterstesZutatenpaket)) {
                double abstand = zutatenpaket.getxPos() - zutatenpaket.getAblageflaeche().getxPos();
                this.lokaleAbstaendeZwischenAblageflaecheUndZutatenpaket.put(zutatenpaket, abstand);
            }
        }
    }

    private void addUnvertraeglichkeitenZusammen() {
        for (Zutatenpaket zutatenpaket : zutatenpakete) {
            if(!zutatenpaket.getUnvertraeglichkeit().isEmpty())
                unvertraeglichkeiten.addAll(zutatenpaket.getUnvertraeglichkeit());
        }
    }

    private double calculateHoehe() {
        double gesHoehe = 0;
        for (Zutatenpaket packet : this.zutatenpakete) {
            gesHoehe += packet.getHoehe();
        }
        return gesHoehe;
    }

    private int calculateGewicht() {
        int gesGewicht = 0;
        for (Zutatenpaket packet : this.zutatenpakete) {
            gesGewicht += packet.getGewicht();
        }
        return gesGewicht;
    }

    @Override
    public double getBreite() {
        return this.breite;
    }

    public double getAbsoluteHoehe() {
        return this.absoluteHoehe;
    }

    @Override
    public double getHoehe() {
        return this.unterstesZutatenpaket.getHoehe();
    }

    @Override
    public double getGewicht() {
        return this.gesamtgewicht;
    }

    @Override
    public List<Zutat> getUnvertraeglichkeit() {
        return this.unvertraeglichkeiten;
    }

    @Override
    public void setAblagebereich(Ablagebereich ablagebereich) {
        for (Zutatenpaket paket : this.zutatenpakete) {
            paket.setAblagebereich(ablagebereich);
        }
    }

    @Override
    public void setAblageflaeche(Ablageflaeche ablageflaeche) {
        this.unterstesZutatenpaket.setAblageflaeche(ablageflaeche);
    }

    @Override
    public void setPositionen(double x, double y) {
        xPos = x;
        yPos = y;
        // unterstes Paket hat diese Positionen
        this.unterstesZutatenpaket.setPositionen(xPos, yPos);
        // Position für andere berechnen
        for (Zutatenpaket zutatenpaket : this.zutatenpakete) {
            if (!zutatenpaket.equals(this.unterstesZutatenpaket)) {
                Ablageflaeche unterliegendeAblageflaeche = zutatenpaket.getAblageflaeche();
                x = zutatenpaket.getAblageflaeche().getxPos() + this.lokaleAbstaendeZwischenAblageflaecheUndZutatenpaket.get(zutatenpaket);
                y = unterliegendeAblageflaeche.getyPos() - zutatenpaket.getHoehe();
                zutatenpaket.setPositionen(x, y);
            }
        }
    }

    public List<Zutatenpaket> getZutatenpakete() {
        return this.zutatenpakete;
    }

    public Ablagebereich getAblagebereich() {
        return ablagebereich;
    }

    public Ablageflaeche getAblageflaeche() {
        return ablageflaeche;
    }

    public AblageflaechenTyp getTyp() {
        return typ;
    }

    public int getGesamtgewicht() {
        return gesamtgewicht;
    }

    public List<Zutat> getUnvertraeglichkeiten() {
        return unvertraeglichkeiten;
    }

    public Zutatenpaket getUnterstesZutatenpaket() {
        return unterstesZutatenpaket;
    }

    @Override
    public double getxPos() {
        return xPos;
    }

    @Override
    public double getyPos() {
        return yPos;
    }

    @Override
    public void setxPos(double x) {
        xPos = x;
        // unterstes Paket hat diese Positionen
        this.unterstesZutatenpaket.setxPos(xPos);
        // Position für andere berechnen
        for (Zutatenpaket zutatenpaket : this.zutatenpakete) {
            if (!zutatenpaket.equals(this.unterstesZutatenpaket)) {
                x = zutatenpaket.getAblageflaeche().getxPos() + this.lokaleAbstaendeZwischenAblageflaecheUndZutatenpaket.get(zutatenpaket);
                zutatenpaket.setxPos(x);
            }
        }
    }

    @Override
    public String getZutatName() {
        String namen = "";
        for(Zutatenpaket zutenpaket: zutatenpakete){
            namen += zutenpaket.getZutatName() + ",";
        }
        return namen;
    }
}
