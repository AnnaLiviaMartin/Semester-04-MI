package de.hsrm.mi.softwaretechnik.projekt.business.services;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.*;
import de.hsrm.mi.softwaretechnik.projekt.business.enums.AblageflaechenTyp;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ObjektExistiertNichtException;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.PlatzierungNichtMoeglichException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.stream.Collectors;

public class ZutatenpaketplatzierungImpl implements Zutatenpaketplatzierung {
    private ZutatenpaketverwaltungImpl zutatenpaketverwaltung;
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private String errorMessage = "";

    /**
     * Konstruktor
     *
     * @param zutatenpaketverwaltung die das Regal und die Zutatenpakete verwaltet
     */
    public ZutatenpaketplatzierungImpl(ZutatenpaketverwaltungImpl zutatenpaketverwaltung) {
        this.zutatenpaketverwaltung = zutatenpaketverwaltung;
    }

    /**
     * Fügt einen PropertyChangeListener hinzu, der benachrichtigt wird, wenn sich
     * eine Property dieses Modells ändert.
     *
     * @param listener Der hinzuzufügende PropertyChangeListener.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Prüft, ob eine Platzierung möglich ist.
     * Annahme: Das übergebene Zutatenpaket weiß bereits, wer sein potenziell zukünftiger Ablagebereich und seine Ablageflaeche ist!
     *
     * @param zutatenpaket  welches platziert werden soll
     * @param ablagebereich in welchem das Zutatenpaket platziert werden soll.
     * @return true wenn das Paket platzierbar ist, sonst false
     */
    @Override
    public boolean pruefePlatzierung(Platzierbar zutatenpaket, Ablagebereich ablagebereich, Ablageflaeche ablageflaeche, double posX, double posY) throws PlatzierungNichtMoeglichException {
        pruefeObRagtRausAusAblagebereich(ablagebereich, zutatenpaket);
        paketstapelHoeherAlsAblagebereich(zutatenpaket, ablagebereich, ablageflaeche);
        keineUnvertraeglichkeitenMitAblagebereich(zutatenpaket, ablagebereich);
        darunterliegendeAblageflaecheTragfaehigGenug(zutatenpaket, ablagebereich, ablageflaeche);
        pruefeKollision(ablagebereich, zutatenpaket, posX, posY);
        return true;
    }

    private boolean pruefeKollision(Ablagebereich ablagebereich, Platzierbar zuPlatzierendesZutatenpaket, double posX, double posY) throws PlatzierungNichtMoeglichException {
        for (Zutatenpaket zutatenPaketToCheck : ablagebereich.getZutatenpakete()) {
            if (posX < zutatenPaketToCheck.getxPos() + zutatenPaketToCheck.getBreite() &&
                    posX + zuPlatzierendesZutatenpaket.getBreite() > zutatenPaketToCheck.getxPos() &&
                    posY < zutatenPaketToCheck.getyPos() + zutatenPaketToCheck.getHoehe() &&
                    posY + zuPlatzierendesZutatenpaket.getHoehe() > zutatenPaketToCheck.getyPos()) {

                throw new PlatzierungNichtMoeglichException("Zutatenpaket kann aufgrund einer Kollision bei " + posX + " " + posY + " nicht platziert werden.");
            }
        }
        return true;
    }

    private boolean pruefeObRagtRausAusAblagebereich(Ablagebereich ablagebereich, Platzierbar zuPlatzierendesZutatenpaket) throws PlatzierungNichtMoeglichException {
        if (zuPlatzierendesZutatenpaket.getxPos() + zuPlatzierendesZutatenpaket.getBreite() > ablagebereich.getBreite()) {

            throw new PlatzierungNichtMoeglichException("Zutatenpaket ragt aus dem Ablagebereich raus und kann deshalb nicht platziert werden.");
        }

        return true;
    }

    /**
     * Das Paket darf nicht breiter sein als die Ablagefläche, auf der es liegt,
     * sonst wird es immer überstehen.
     *
     * @param zutatenpaket  welches platziert werden soll
     * @param ablageflaeche auf welche das Zutatenpaket platziert werden soll.
     * @return true, wenn das Paket nicht breiter ist als die Ablageflaeche.
     */
    private boolean paketbreiteNichtHoeherAlsAblageflaechenbreite(Platzierbar zutatenpaket, Ablageflaeche ablageflaeche) throws PlatzierungNichtMoeglichException {
        double paketbreite = zutatenpaket.getBreite();
        double maxBreite = ablageflaeche.getBreite();
        if (paketbreite <= maxBreite) {
            return true;
        } else {
            throw new PlatzierungNichtMoeglichException("Das Zutatenpaket ist zu breit um auf seine Ablagefläche zu passen.");
        }
    }

    /**
     * Das Paket steht über die Ablagefläche hinaus.
     * Dieser Fall wird überprüft:
     * x----------------x2     -> Paket x: abstandVonLinksBisPaketanfang, x2: abstandVonLinksBisPaketende
     * |               |
     * |               |
     * |y-------------------------------------------y2|
     * <- breite des Regalbodens/Zutatenpakets->
     * -> mit y: abstandAbAblageflaechenanfang und y2: abstandBisAblageflachenende
     *
     * @param zutatenpaket  welches platziert werden soll
     * @param ablageflaeche auf welche das Zutatenpaket platziert werden soll.
     * @return true, wenn das Paket nicht übersteht.
     */
    private boolean paketStehtNichtUeber(Platzierbar zutatenpaket, Ablageflaeche ablageflaeche) throws PlatzierungNichtMoeglichException {
        double abstandVonLinksBisPaketanfang = zutatenpaket.getxPos() - ablageflaeche.getAbstandVonLinks();   /*nicht globaler Abstand ab Regalbeginn, sondern Abstand ab Ablagebereichbeginn*/
        double abstandVonLinksBisPaketende = abstandVonLinksBisPaketanfang + zutatenpaket.getBreite();
        double abstandAbAblageflaechenanfang = 0;
        double abstandBisAblageflachenende = ablageflaeche.getBreite();

        if (abstandVonLinksBisPaketanfang >= abstandAbAblageflaechenanfang && abstandVonLinksBisPaketende <= abstandBisAblageflachenende) {
            return true;
        } else {
            throw new PlatzierungNichtMoeglichException("Das Zutatenpaket würde über seine Ablagefläche stehen.");
        }
    }

    /**
     * Der einzufügende Paketstapel ODER das einzelne Paket darf durch Einfügen
     * nicht höher werden als der Ablagebereich hoch ist.
     *
     * @param zutatenpaket  welches platziert werden soll
     * @param ablagebereich in welche das Zutatenpaket platziert werden soll.
     * @return true, wenn die neue Höhe des Zutatenstapels geringer ist als die maximal mögliche Höhe des Ablagebereichs
     */
    private boolean paketstapelHoeherAlsAblagebereich(Platzierbar zutatenpaket, Ablagebereich ablagebereich, Ablageflaeche ablageflaeche) throws PlatzierungNichtMoeglichException {
        double hoeheZutatenpaket = zutatenpaket.getHoehe();
        double maxHoehe = ablagebereich.getHoehe();
        double aktHoehe = 0.0;
        List<Zutatenpaket> darunterliegendePakete = ablagebereich.findeUnmittelbarUnterliegendePakete(ablageflaeche);
        if (!darunterliegendePakete.isEmpty()) {  //wenn empty ist Ablagefläche ein Regalboden
            darunterliegendePakete.add((Zutatenpaket) ablageflaeche);
            for (Zutatenpaket paket : darunterliegendePakete) {
                aktHoehe += paket.getHoehe();
            }
            aktHoehe += hoeheZutatenpaket;
        } else {
            aktHoehe = hoeheZutatenpaket;
        }

        if (aktHoehe <= maxHoehe) {
            return true;
        } else {
            throw new PlatzierungNichtMoeglichException("Das neue Zutatenpaket ist zu hoch, um auf diesen (Stapel) draufgelegt zu werden.");
        }
    }

    /**
     * Das Paket darf nicht breiter sein als dort wo es eingefügt wird (es muss also z.B. in eine Lücke passen).
     * Beispiel: man möchte d zwischen c und b platzieren (was offensichtlich nicht geht)
     * -------------
     * |           |
     * |     d     |
     * x1---------x2
     * |
     * v
     * -------   -----
     * |      |   |   |
     * |  c   |   | b |
     * y1    y2  y1  y2
     * -----------------
     * |               |
     * |       a       |
     * |-------------------------------------------|
     * <- Regalboden->
     * x1: abstandVonLinksBisPaketanfangNeuesZP
     * x2: abstandVonLinksBisPaketendeNeuesZP
     * y1: abstandVonLinksBisPaketanfangBestehendesPaket
     * y2: abstandVonLinksBisPaketendeBestehendesPaket
     * Wenn: y1 + y2 < x1 + x2 ODER y1 + y2 ></> x1 + x2 kann nun platziert werden.
     *
     * @param zutatenpaket  welches platziert werden soll
     * @param ablagebereich in den das Paket platziert werden soll
     * @param ablageflaeche auf der das Zutatenpaket liegen soll
     * @return true, wenn das Paket dort genug Platz hat
     */
    private boolean genugPlatzFuerZutatenpaket(Platzierbar zutatenpaket, Ablagebereich ablagebereich, Ablageflaeche ablageflaeche) throws PlatzierungNichtMoeglichException {
        double abstandVonLinksBisPaketanfangNeuesZP = zutatenpaket.getxPos();   /*nicht globaler Abstand ab Regalbeginn, sondern Abstand ab Ablagebereichbeginn*/
        double abstandVonLinksBisPaketendeNeuesZP = abstandVonLinksBisPaketanfangNeuesZP + zutatenpaket.getBreite();
        List<Zutatenpaket> nebeneinanderLiegendePakete = ablagebereich.findeAlleAufEinerAblageflaecheLiegendenZutatenpakete(ablageflaeche);
        boolean zuWenigPlatz = false;

        for (Zutatenpaket paket : nebeneinanderLiegendePakete) {
            if (!paket.equals(zutatenpaket)) {
                // checke, ob Zutatenpaket an einer Stelle etwas "überlagern" würde
                double abstandVonLinksBisPaketanfangBestehendesPaket = paket.getAbstandVonLinks();   /*nicht globaler Abstand ab Regalbeginn, sondern Abstand ab Ablagebereichbeginn*/
                double abstandVonLinksBisPaketendeBestehendesPaket = abstandVonLinksBisPaketanfangBestehendesPaket + paket.getBreite();
                boolean bestehendesPaketLinksVonNeuem = abstandVonLinksBisPaketanfangBestehendesPaket < abstandVonLinksBisPaketanfangNeuesZP && abstandVonLinksBisPaketendeBestehendesPaket < abstandVonLinksBisPaketendeNeuesZP;
                boolean bestehendesPaketRechtsVonNeuem = abstandVonLinksBisPaketanfangBestehendesPaket > abstandVonLinksBisPaketanfangNeuesZP && abstandVonLinksBisPaketendeBestehendesPaket > abstandVonLinksBisPaketendeNeuesZP;

                if (!(bestehendesPaketLinksVonNeuem || bestehendesPaketRechtsVonNeuem)) {
                    /*wenn Paket*/
                    zuWenigPlatz = true;
                }
            }
        }

        if (!zuWenigPlatz) {
            return true;
        } else {
            throw new PlatzierungNichtMoeglichException("Das neue Zutatenpaket überlappt ein anderes.");
        }
    }

    /**
     * Die Ablagefläche muss das Gewicht des Paketes unterstützen.
     * Es darf nicht insgesamt schwerer sein als das Gewicht der Ablagefläche.
     *
     * @param zutatenpaket   das Paket, welches platziert werden soll
     * @param ablageflaeche  auf dem das Paket liegen soll
     * @param ablagebereich, der Ablagebereich in dem das Paket liegen soll
     * @return true, wenn die Ablageflaeche das Gewicht des Zutatenpakets tragen kann.
     */
    private boolean darunterliegendeAblageflaecheTragfaehigGenug(Platzierbar zutatenpaket, Ablagebereich ablagebereich, Ablageflaeche ablageflaeche) throws PlatzierungNichtMoeglichException {
        /*Pakete können nicht mehr als ihr eigenes Gewicht tragen*/
        double gewichtZutatenpaket = zutatenpaket.getGewicht();
        double maxTragfaehigkeit = ablageflaeche.getTragfaehigkeit();
        double aktGewicht = ablagebereich.getGewichtAllerAufliegendenPakete(ablageflaeche);

        if (aktGewicht + gewichtZutatenpaket <= maxTragfaehigkeit || ablageflaeche.getTyp() == AblageflaechenTyp.REGALBODEN) {    /*Regalböden können unendlich viel Gewicht tragen*/
            return true;
        } else {
            throw new PlatzierungNichtMoeglichException("Das neue Zutatenpaket ist zu schwer, um auf diese Ablagefläche (dieses Zutatenpaket) gestellt zu werden.");
        }
    }

    /**
     * Checkt, ob in dem Ablagebereich Pakete liegen, mit denen das Zutatenpaket eine
     * Unverträglichkeit hat.
     *
     * @param zutatenpaket   das Paket, welches getestet werden soll
     * @param ablagebereich, der Ablagebereich für den das Paket getestet werden soll
     * @return true, wenn es keine Unverträglichkeit gibt
     */
    private boolean keineUnvertraeglichkeitenMitAblagebereich(Platzierbar zutatenpaket, Ablagebereich ablagebereich) throws PlatzierungNichtMoeglichException {
        List<Zutat> unvertraeglichkeitenDesZutatenpakets = zutatenpaket.getUnvertraeglichkeit();
        Set<Zutat> vorhandeneZutaten = ablagebereich.getZutatenpakete().stream().map(Zutatenpaket::getZutat).collect(Collectors.toSet());

        // check ob zutatenpaket unvertraeglichkeiten mit ablagebereich hat
        for (Zutat unvertraeglichkeit : unvertraeglichkeitenDesZutatenpakets) {
            for (Zutat vorhanden : vorhandeneZutaten) {
                if (unvertraeglichkeit.getName().contains(vorhanden.getName())) {
                    throw new PlatzierungNichtMoeglichException("Es wurden Lebensmittelunverträglichkeiten gefunden.");
                }
            }
        }

        //check ob ablagebereich unverträglichkeiten mit zutatenpaket hat
        for (Zutat zutatenpaketeDesAblagebereichs : vorhandeneZutaten) {
            for (Zutat unvertraeglichkeitDesZutatenpaketsDesAblagebereichs : zutatenpaketeDesAblagebereichs.getUnvertraeglichkeit()) {
                if (zutatenpaket.getZutatName().contains(unvertraeglichkeitDesZutatenpaketsDesAblagebereichs.getName())) {
                    throw new PlatzierungNichtMoeglichException("Es wurden Lebensmittelunverträglichkeiten gefunden.");
                }
            }

        }

        return true;
    }

    /**
     * platziert ein Zutatenpaket in einem Ablagebereich
     * Aufruf: Drag & Drop von Paket
     * TODO Annahme: Das übergebene Zutatenpaket weiß bereits, wer sein potenziell zukünftiger Ablagebereich und seine Ablageflaeche etc. ist ABER hat es noch nicht gesetzt!
     *
     * @param zutatenpaket               das Paket, welches platziert werden soll
     * @param newAblagebereich           der Ablagebereich in den das Paket abgelegt werden soll
     * @param unterliegendeAblageflaeche die Fläche auf die das Zutatenpaket gestellt werden soll
     * @param xPosDrop                   der Abstand den das Zutatenpaket (wenn die Platzierung möglich ist) zum linken Rand des Ablagebereiches haben wird
     * @return true, wenn die Platzierung möglich ist
     */
    @Override
    public boolean platziereZutatenpaket(Platzierbar zutatenpaket, Ablagebereich newAblagebereich, Ablageflaeche unterliegendeAblageflaeche, double xPosDrop, double yPosDrop) throws ObjektExistiertNichtException {
        double oldXPos = zutatenpaket.getxPos();
        double oldYPos = zutatenpaket.getyPos();

        //setzt Werte der Platzierung
        if (unterliegendeAblageflaeche.getTyp() == AblageflaechenTyp.REGALBODEN) {
            zutatenpaket.setPositionen(xPosDrop, newAblagebereich.getHoehe() - zutatenpaket.getHoehe());
        } else {
            zutatenpaket.setPositionen(xPosDrop, unterliegendeAblageflaeche.getyPos() - zutatenpaket.getHoehe());
        }

        try {
            if (pruefePlatzierung(zutatenpaket, newAblagebereich, unterliegendeAblageflaeche, zutatenpaket.getxPos(), zutatenpaket.getyPos())) {
                //platziere Zutatenpaket, Ablageflaeche ist bereits gesetzt
                zutatenpaket.setAblagebereich(newAblagebereich);
                zutatenpaket.setAblageflaeche(unterliegendeAblageflaeche);
                //TODO wenn ablagebereich wechselt, dann muss er im alten rausgenommen werden
                // todo strange sollte wenn stapel ist alle pakete einzeln hinzufügen
                if (!newAblagebereich.getZutatenpakete().contains(zutatenpaket)) {
                    newAblagebereich.addZutatenpaket(zutatenpaket);
                }

                //fire regal change
                //changeSupport.firePropertyChange("regal", null, this.zutatenpaketverwaltung.getRegal());  //todo gibt keine Kopie daher gleiche
                return true;
            } else {
                /*Platzierung nicht möglich*/
                //TODO wenn verschieben fehlschlägt, dann alte position setzen
                zutatenpaket.setPositionen(oldXPos, oldYPos);

                return false;
            }
        } catch (PlatzierungNichtMoeglichException e) {
            errorMessage = e.getMessage();
            return false;
        }
    }

    @Override
    public boolean platzierbar(Platzierbar zutatenpaket, Ablagebereich newAblagebereich, Ablageflaeche unterliegendeAblageflaeche, double xPosDrop) {
        double oldXPos = zutatenpaket.getxPos();
        double oldYPos = zutatenpaket.getyPos();

        //setzt Werte der Platzierung
        if (unterliegendeAblageflaeche.getTyp() == AblageflaechenTyp.REGALBODEN) {
            zutatenpaket.setPositionen(xPosDrop, newAblagebereich.getHoehe() - zutatenpaket.getHoehe());
        } else {
            zutatenpaket.setPositionen(xPosDrop, unterliegendeAblageflaeche.getyPos() - zutatenpaket.getHoehe());
        }
        try {
            if (pruefePlatzierung(zutatenpaket, newAblagebereich, unterliegendeAblageflaeche, xPosDrop, newAblagebereich.getHoehe() - zutatenpaket.getHoehe())) {
                return true;
            } else {
                zutatenpaket.setPositionen(oldXPos, oldYPos);
                return false;
            }
        } catch (PlatzierungNichtMoeglichException e) {
            errorMessage = e.getMessage();
            return false;
        }
    }

    /**
     * Verschiebt das Zutatenpaket in einen neuen Bereich
     *
     * @param zutatenpaket,     das verschoben werden soll
     * @param newAblagebereich, in den verschoben werden soll
     */
    @Override
    public boolean verschiebeZutatenpaket(Platzierbar zutatenpaket, Ablagebereich newAblagebereich, Ablageflaeche ablageflaeche, double abstandVonLinks, double y) throws ObjektExistiertNichtException {
        return platziereZutatenpaket(zutatenpaket, newAblagebereich, ablageflaeche, abstandVonLinks, y);
    }

    @Override
    public void platzierenAbschliessen() {
        this.zutatenpaketverwaltung.speichereRegal();
    }

    @Override
    public void platzierenAbbrechen() {
        this.zutatenpaketverwaltung.ladeRegal();
    }

    @Override
    public Ablageflaeche getAblageflaeche(double xPosDrop, double yPosDrop, Zutatenpaket zuPlatzierendesZutatenpaket, Ablagebereich ablagebereich) {
        double xObenLinks = xPosDrop;
        double xObenRechts = xPosDrop + zuPlatzierendesZutatenpaket.getBreite();
        double abstandNachUnten = yPosDrop;
        // hole alle Pakete die unterhalb des neuen Zutatenpakets liegen
        List<Ablageflaeche> unterliegendeAblageflaechen = getUnterliegendeAblageFlaechen(xObenLinks, xObenRechts, abstandNachUnten, ablagebereich);
        // gehe schrittweise nach unten und finde erstes Zutatenpaket auf dem das neue liegt
        unterliegendeAblageflaechen.sort((o1, o2) -> Double.compare(o1.getyPos(), o2.getyPos()));

        for (Ablageflaeche ablageFleache : unterliegendeAblageflaechen) {
            System.out.println(ablageFleache.getyPos());
        }

        return unterliegendeAblageflaechen.getFirst();
    }

    /**
     * Gibt alle Pakete aus die unter der spezifizierten x Position liegen
     *
     * @param xObenLinks
     * @param xObenRechts
     * @param ablagebereich
     * @return
     */
    private List<Ablageflaeche> getUnterliegendeAblageFlaechen(double xObenLinks, double xObenRechts, double abstandNachUnten, Ablagebereich ablagebereich) {
        List<Ablageflaeche> unterliegendeZutatenpakete = new ArrayList<>();

        for (Zutatenpaket zutatenpaketToCheck : ablagebereich.getZutatenpakete()) {
            if (zutatenpaketToCheck.getxPos() <= xObenLinks && zutatenpaketToCheck.getxPos() + zutatenpaketToCheck.getBreite() >= xObenRechts) {
                unterliegendeZutatenpakete.add(zutatenpaketToCheck);
            }
        }

        if (unterliegendeZutatenpakete.isEmpty()) {
            unterliegendeZutatenpakete.add(ablagebereich.getUntererRegalboden());
        }

        return unterliegendeZutatenpakete;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
