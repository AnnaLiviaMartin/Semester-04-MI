package de.hsrm.mi.softwaretechnik.projekt.business.entities;

import com.fasterxml.jackson.annotation.*;
import de.hsrm.mi.softwaretechnik.projekt.business.enums.AblageflaechenTyp;
import de.hsrm.mi.softwaretechnik.projekt.business.services.Platzierbar;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "id")
public class Zutatenpaket implements Ablageflaeche, Platzierbar, Serializable {
    private Paket paket;
    private Zutat zutat;
    @JsonBackReference
    private Ablagebereich ablagebereich;
    private boolean imWarenkorb;
    private Ablageflaeche ablageflaeche;    /* das Paket liegt auf ablageflaeche drauf */
    private final AblageflaechenTyp typ = AblageflaechenTyp.PAKET;
    private UUID uuid;
    private double xPos, yPos;
    private boolean isInEditor = true;

    public Zutatenpaket(Paket paket, Zutat zutat) {
        this.uuid = UUID.randomUUID();
        this.paket = paket;
        this.zutat = zutat;
        this.imWarenkorb = false;
    }

    public Paket getPaket() {
        return paket;
    }

    public void setPaket(Paket paket) {
        this.paket = paket;
    }

    public Zutat getZutat() {
        return zutat;
    }

    public void setZutat(Zutat zutat) {
        this.zutat = zutat;
    }

    public Ablagebereich getAblagebereich() {
        return ablagebereich;
    }

    public void setAblagebereich(Ablagebereich ablagebereich) {
        this.ablagebereich = ablagebereich;
    }

    @Override
    public Ablageflaeche getAblageflaeche() {
        return ablageflaeche;
    }

    @Override
    public double getAbstandVonLinks() {
        return this.paket.getAbstandVonLinks();
    }

    public void addAblageflaeche(Ablageflaeche ablageflaeche) {
        this.ablageflaeche = ablageflaeche;
    }

    public void setAblageflaeche(Ablageflaeche ablageflaeche) {
        this.ablageflaeche = ablageflaeche;
    }

    @Override
    public double getBreite() {
        return this.paket.getBreite();
    }

    @Override
    public double getHoehe() {
        return this.paket.getHoehe();
    }

    @Override
    public double getGewicht() {
        return this.paket.getGewicht();
    }

    @Override
    public List<Zutat> getUnvertraeglichkeit() {
        return this.zutat.getUnvertraeglichkeit();
    }


    public int getTragfaehigkeit() {
        return this.paket.getTragfaehigkeit();
    }

    @Override
    public AblageflaechenTyp getTyp() {
        return typ;
    }

    /**
     * Getter für die ID
     *
     * @return id
     */
    @Override
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Getter für den Zustand, ob sich das Zutatenpaket gerade im Warenkorb befindet
     *
     * @return befindet sich im Warenkorb
     */
    public boolean isImWarenkorb() {
        return imWarenkorb;
    }

    /**
     * Setter für den Zustand, ob sich das Zutatenpaket gerade im Warenkorb befindet
     *
     * @param imWarenkorb befindet sich im Warenkorb
     */
    public void setImWarenkorb(boolean imWarenkorb) {
        this.imWarenkorb = imWarenkorb;
    }

    public void setPositionen(double x, double y) {
        this.xPos = x;
        this.yPos = y;
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
    }

    @Override
    public String getZutatName() {
        return zutat.getName();
    }

    /**
     * Checkt ob ein Paket ganz oben liegt oder es Zutatenpakete auf sich drauf liegen hat
     *
     * @return true, wenn das Zutatenpaket keine pakete hat die auf ihm liegen.
     */
    public boolean isTopPaket() {
        for (Zutatenpaket zutatenpaketeVonAblagebereich : this.ablagebereich.getZutatenpakete()) {
            if (!zutatenpaketeVonAblagebereich.equals(this)) {
                // wenn zutatenpaket aus ablagebereich dieses paket als ablageflaeche hat -> liegt dieses paket nicht oben
                if (zutatenpaketeVonAblagebereich.getAblageflaeche().equals(this)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isInEditor() {
        return isInEditor;
    }

    public void setInEditor(boolean inEditor) {
        isInEditor = inEditor;
    }

    @Override
    public String toString() {
        return "Zutatenpaket{" +
                "paket=" + paket +
                ", zutat=" + zutat +
                ", ablagebereich=" + ablagebereich +
                ", imWarenkorb=" + imWarenkorb +
                ", ablageflaeche=" + ablageflaeche +
                ", typ=" + typ +
                ", uuid=" + uuid +
                ", xPos=" + xPos +
                ", yPos=" + yPos +
                ", isInEditor=" + isInEditor +
                '}';
    }
}
