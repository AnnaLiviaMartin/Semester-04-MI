package de.hsrm.mi.softwaretechnik.projekt.business.services;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Ablagebereich;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Regal;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Regalboden;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Regalwand;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ObjektExistiertNichtException;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.RegalkomponenteNichtLoeschbarException;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.RegalkomponenteNichtPlatzierbarException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Verwaltet die Regalböden und Regalwände, die gemeinsam das Regal bilden. Bietet Funktionalität, um diese
 * zu erstellen und zu verschieben. Positionen werden in Pixel angegeben.
 * Die gegebenen Positionen beginnen oben links vom Regalboden/Regalwand.
 * X---------------|
 * |---------------|
 */
public class LagereditorImpl implements Lagereditor {
    private final double REGAL_WAND_BREITE, REGAL_BODEN_HOEHE;
    private Regal regal;
    private double width, height;


    public LagereditorImpl(double windowWidth, double windowHeight, double regalWandBreite, double regalBodenHoehe) throws RegalkomponenteNichtPlatzierbarException {
        this.REGAL_WAND_BREITE = regalWandBreite;
        this.REGAL_BODEN_HOEHE = regalBodenHoehe;
        this.width = windowWidth;
        this.height = windowHeight;

        initialisiereRegalrahmen();
    }

    /**
     * Initialisiert den Rahmen eines Regals durch Erstellen und Positionieren von Wänden und dem Boden.
     *
     * Diese Methode erstellt vier Hauptkomponenten des Regals:
     * {@link Regalwand} für die linkse und rechte Wand
     * {@link Regalboden} für den oberen und unteren Boden
     * Jede Komponente wird entsprechend ihrer Größe und Position im Raum initialisiert und platziert.
     * Anschließend werden alle Komponenten in einem {@link Regal} zusammengefasst.
     */
    private void initialisiereRegalrahmen() throws RegalkomponenteNichtPlatzierbarException {
        Regalboden regalboden, regalDecke;
        Regalwand aussenwandLinks, aussenwandRechts;

        aussenwandLinks = new Regalwand(this.getHeight(), REGAL_WAND_BREITE, true);
        aussenwandLinks.setPositionen(0, 0);

        aussenwandRechts = new Regalwand(this.getHeight(), REGAL_WAND_BREITE, true);
        aussenwandRechts.setPositionen(this.getWidth() - REGAL_WAND_BREITE, 0);

        regalDecke = new Regalboden(REGAL_BODEN_HOEHE, true);
        regalDecke.setPositionen(0 + aussenwandLinks.getBreite(), 0);

        regalboden = new Regalboden(REGAL_BODEN_HOEHE, true);
        regalboden.setPositionen(0 + aussenwandLinks.getBreite(), this.getHeight() - REGAL_BODEN_HOEHE);

        List<Regalboden> initRegalboeden = new ArrayList<>();
        Collections.addAll(initRegalboeden, regalDecke, regalboden);

        List<Regalwand> initRegalwaende = new ArrayList<>();
        Collections.addAll(initRegalwaende, aussenwandLinks, aussenwandRechts);

        regal = new Regal(initRegalwaende, initRegalboeden, height, width);

        platziereRegalwand(aussenwandRechts, aussenwandRechts.getxPos());
        platziereRegalwand(aussenwandLinks, aussenwandLinks.getxPos());
        platziereRegalboden(regalDecke, 0, aussenwandLinks.getBreite());
        platziereRegalboden(regalboden, regal.getHoehe() - REGAL_BODEN_HOEHE, aussenwandLinks.getBreite());

    }

    @Override
    public Regalwand erstelleRegalwand() {
        return new Regalwand(regal.getHoehe() - 2 * REGAL_BODEN_HOEHE, REGAL_WAND_BREITE);
    }

    @Override
    public Regalboden erstelleRegalboden() {
        return new Regalboden(REGAL_BODEN_HOEHE);
    }

    /**
     * Platziert eine Regalwand an einer bestimmten Position im Regal.
     *
     * Diese Methode prüft, ob die Platzierung der Regalwand erfolgen kann, basierend darauf,
     * ob sie mit bestehenden Regalböden kollidieren würde. Wenn die Platzierung erfolgreich ist,
     * wird die Regalwand an der angegebenen Position platziert und zum Regal hinzugefügt.
     * Andernfalls wird eine Ausnahme geworfen.
     *
     * Beachtet wird, dass eine Regalwand nicht platziert werden kann, wenn bereits tragende Regalböden vorhanden sind.
     *
     * @param regalwand Die {@link Regalwand}, die platziert werden soll.
     * @param posX      Die x-Koordinate, an der die Regalwand platziert werden soll.
     * @throws ObjektExistiertNichtException Eine mögliche Ausnahme, die beim Hinzufügen der Regalwand zum Regal geworfen werden könnte.
     */
    @Override
    public void platziereRegalwand(Regalwand regalwand, double posX) throws RegalkomponenteNichtPlatzierbarException {
        double posY = REGAL_BODEN_HOEHE;

        if (bereitsZutatenpaketePlatziert()) {
            throw new RegalkomponenteNichtPlatzierbarException(("Regalwand kann nicht platziert oder verschoben werden, da im Regal " +
                    "bereits Zutatenpakete vorhanden sind."));
        }

        if (!regalwand.getTragendeRegalboeden().isEmpty()) {
            throw new RegalkomponenteNichtPlatzierbarException("Regalwand kann nicht platziert oder verschoben werden, da " +
                    "Regalböden auf dieser stützen.");
        }

        if (regalwand.isTeilVonRahmen()) {
            posY = regal.getAussenwandLinks().getyPos();
        }

        // Speichern der ursprünglichen Positionen der Regalwand
        double originalPosX = regalwand.getxPos();
        double originalPosY = regalwand.getyPos();

        //Wenn erstmal gesetzt, platzierung nur in x-position möglich.
        if (regalwand.isIstGesetzt()) {
            regalwand.setPositionen(posX, originalPosY);
            posY = originalPosY;
        }

        // Prüfen, ob da wo die Regalwand platziert werden soll, keine Regalböden sind.
        boolean platzierungErfolgreich = true;
        for (Regalboden regalboden : regal.getRegalboeden()) {
            if (kreuzt(regalwand, regalboden, posX, posY)) {
                platzierungErfolgreich = false;
                break;
            }
        }

        if (platzierungErfolgreich) {
            regalwand.setPositionen(posX, posY);
            if (!regal.getRegalwaende().contains(regalwand)) {
                try {
                    regal.addRegalwand(regalwand);
                } catch (ObjektExistiertNichtException e) {
                    throw new RuntimeException(e);
                }
            }

            berechneAblagebereiche();
        } else {
            // Zurücksetzen auf die ursprünglichen Positionen
            regalwand.setPositionen(originalPosX, originalPosY);
            throw new RegalkomponenteNichtPlatzierbarException("Platzierung der Regalwand nicht möglich, da sie mit einem Regalboden kollidiert.");
        }
    }

    /**
     * Prüft, ob im Regal bereits Zutatenpakete vorhanden sind.
     *
     * @return boolean wenn Zutatenpakete vorhanden oder nicht
     */
    @Override
    public boolean bereitsZutatenpaketePlatziert() {
        for (Ablagebereich ablagebereich : regal.getAblagebereiche()) {
            if (!ablagebereich.getZutatenpakete().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Überprüft, ob eine Regalwand mit einem Regalboden kollidiert
     *
     * @param regalwand  Die zu überprüfende Regalwand
     * @param regalboden Der zu überprüfende Regalboden
     * @param posX       Die x-Position der Regalwand
     * @param posY       Die y-Position der Regalwand
     * @return true, wenn eine Kollision vorliegt, ansonsten false
     */
    private boolean kreuzt(Regalwand regalwand, Regalboden regalboden, double posX, double posY) {
        return posX < regalboden.getxPos() + regalboden.getBreite() &&
                posX + regalwand.getBreite() > regalboden.getxPos() &&
                posY < regalboden.getyPos() + regalboden.getHoehe() &&
                posY + regalwand.getHoehe() > regalboden.getyPos();
    }

    /**
     * Platziert einen Regalboden an einer bestimmten Höhenposition im Regal.
     *
     * Diese Methode sucht nach der nächstgelegenen Regalwand, um den Regalboden zu tragen.
     * Zusätzlich wird die Breite des Regalbodens entsprechend der Position und der tragenden Regalwand angepasst.
     *
     * @param regalboden Der {@link Regalboden}, der platziert werden soll.
     * @param yPos       Die y-Koordinate, an der der Regalboden platziert werden soll.
     * @throws ObjektExistiertNichtException Eine mögliche Ausnahme, die beim Hinzufügen des Regalbodens zum Regal geworfen werden könnte.
     */
    @Override
    public void platziereRegalboden(Regalboden regalboden, double yPos, double xPos) throws RegalkomponenteNichtPlatzierbarException {

        if (bereitsZutatenpaketePlatziert()) {
            throw new RegalkomponenteNichtPlatzierbarException("Regalboden kann nicht platziert oder verschoben werden, da im Regal " +
                    "bereits Zutatenpakete vorhanden sind.");
        }

        if (xPos < 0) {
            return;
        }

        // Speichern der ursprünglichen Positionen der Regalwand
        double originalPosX = regalboden.getxPos();
        double originalPosY = regalboden.getyPos();

        regalboden.setPositionen(xPos, yPos);
        Regalwand tragendeRegalwand = findeNaehesteRegalwand(regalboden);

        if (tragendeRegalwand == null) {
            if (originalPosX < REGAL_WAND_BREITE) {
                regalboden.setPositionen(REGAL_WAND_BREITE, originalPosY);
            }
            return;
        }

        //Löschen des Regalbodens aus der vorherigen regalwand
        for (Regalwand regalwand : regal.getRegalwaende()) {
            regalwand.getTragendeRegalboeden().remove(regalboden);
        }

        if (!tragendeRegalwand.getTragendeRegalboeden().contains(regalboden)) {
            tragendeRegalwand.getTragendeRegalboeden().add(regalboden);
        }

        if (!regal.getRegalboeden().contains(regalboden)) {
            try {
                regal.addRegalboden(regalboden);
            } catch (ObjektExistiertNichtException e) {
                throw new RuntimeException(e);
            }
        }

        double regalboedenBreite = bestimmeRegalbodenBreite(regalboden);
        regalboden.setBreite(regalboedenBreite);
        regalboden.setPositionen(tragendeRegalwand.getxPos() + tragendeRegalwand.getBreite(), yPos);

        berechneAblagebereiche();
    }

    /**
     * Sortiert eine Liste von {@link Regalwand}-Objekten nach ihrer X-Position.
     *
     * @param regalwaende Die Liste von Regalwand-Objekten, die sortiert werden soll.
     */
    private void sortiereRegalwaendeNachXPos(List<Regalwand> regalwaende) {
        Collections.sort(regalwaende, Comparator.comparingDouble(Regalwand::getxPos));
    }

    /**
     * Sortiert eine Liste von {@link Regalboden}-Objekten nach ihrer Y-Position.
     *
     * @param regalboeden Die Liste von {@link Regalboden}-Objekten, die sortiert werden soll.
     */
    private void sortiereRegalboedenNachYPos(List<Regalboden> regalboeden) {
        Collections.sort(regalboeden, Comparator.comparingDouble(Regalboden::getyPos));
    }


    /**
     * Berechnet den Ablagebereich, falls keine Regalböden an der Aussenwand vorhanden sind.
     * <p>
     * Diese Methode prüft, ob die übergebene {@link Regalwand} Teil einer Außenwand ist und ob sie weniger als zwei tragende Regalböden hat.
     * Unter diesen Bedingungen berechnet sie den Ablagebereich.
     * Der berechnete Bereich berücksichtigt die Höhe und Breite der Außenwand sowie die Positionen der beteiligten Wände.
     * Anschließend wird der Ablagebereich initialisiert.
     *
     * @param regalwand            Die {@link Regalwand}, deren Ablagebereich berechnet werden soll.
     * @param regalWandRechtsDavon Die rechte benachbarte {@link Regalwand}, die zur Bestimmung der Breite des
     *                             Ablagebereichs benötigt wird.
     */
    private void berechneAblagebereichVonAussenwandWennKeineRegalboeden(Regalwand regalwand,
                                                                        Regalwand regalWandRechtsDavon) {
        if (!regalwand.isTeilVonRahmen()) {
            return;
        }

        if (regalwand.getTragendeRegalboeden().size() < 2) {
            return;
        }

        double bereichHoehe = regalwand.getHoehe() - 2 * REGAL_BODEN_HOEHE;
        double bereichBreite =
                regalWandRechtsDavon.getxPos() - regalwand.getxPos() - regalwand.getBreite();

        initialisiereAblagebereich(bereichHoehe, bereichBreite, regalwand.getxPos() + regalwand.getBreite(),
                regalwand.getyPos() + REGAL_BODEN_HOEHE, regal.getRegalboden());
    }

    /**
     * Berechnet den oberen Ablagebereich bei einer Außenwand, vorausgesetzt, es gibt mindestens drei tragende Regalböden.
     * <p>
     * Diese Methode prüft, ob die übergebene {@link Regalwand} Teil einer Außenwand ist und ob sie mindestens drei tragende Regalböden hat.
     * Unter diesen Bedingungen berechnet sie den oberen Ablagebereich, der für die Platzierung zusätzlicher Gegenstände benötigt wird.
     * Der berechnete Bereich berücksichtigt die Höhe und Breite des mittleren Regalbodens sowie die Positionen der beteiligten Wände.
     * Anschließend wird der Ablagebereich initialisiert.
     *
     * @param regalBoeden Die Liste der {@link Regalboden}-Objekte, die zur Berechnung des oberen Ablagebereichs herangezogen werden.
     * @param regalwand   Die {@link Regalwand}, deren oberer Ablagebereich berechnet werden soll.
     */
    private void berechneOberenAblagebereichBeiAussenwand(List<Regalboden> regalBoeden, Regalwand regalwand) {
        if (!regalwand.isTeilVonRahmen())
            return;

        if (regalwand.getTragendeRegalboeden().size() < 3)
            return;

        double bereichHoehe = regalBoeden.get(1).getyPos() - REGAL_BODEN_HOEHE;
        double bereichBreite = regalBoeden.get(1).getBreite();

        initialisiereAblagebereich(bereichHoehe, bereichBreite, regalwand.getxPos() + regalwand.getBreite(),
                regalwand.getyPos() + REGAL_BODEN_HOEHE, regalBoeden.get(1));

    }

    /**
     * Berechnet die Ablagebereiche zwischen den Regalböden einer Außenwand, vorausgesetzt, es gibt mindestens drei tragende Regalböden.
     * <p>
     * Diese Methode prüft, ob die übergebene {@link Regalwand} Teil einer Außenwand ist und ob sie mindestens drei tragende Regalböden hat.
     * Unter diesen Bedingungen berechnet sie die Ablagebereiche zwischen jedem Paar von aufeinanderfolgenden Regalböden.
     * Der Ablagebereich wird dann an der Position des ersten Bodens in der Reihe initialisiert.
     *
     * @param regalBoeden Die Liste der {@link Regalboden}-Objekte, die zur Berechnung der Ablagebereiche herangezogen werden.
     * @param regalwand   Die {@link Regalwand}, deren Ablagebereiche zwischen den Regalböden berechnet werden sollen.
     */
    private void berechneAblagebereicheZwischenBoedenVonAussenwand(List<Regalboden> regalBoeden, Regalwand regalwand) {
        if (!regalwand.isTeilVonRahmen())
            return;

        if (regalwand.getTragendeRegalboeden().size() < 3)
            return;

        // -3, da mit Berücksichtigt werden muss, dass die Böden oben und unten an der Aussenwand hängen
        for (int j = 0; j < regalBoeden.size() - 3; j++) {
            Regalboden currRegalboden = regalBoeden.get(j + 1);
            Regalboden nextRegalboden = regalBoeden.get(j + 2);
            double bereichHoehe =
                    nextRegalboden.getyPos() - currRegalboden.getyPos() - REGAL_BODEN_HOEHE;
            double bereichBreite = currRegalboden.getBreite();

            initialisiereAblagebereich(bereichHoehe, bereichBreite, currRegalboden.getxPos(),
                    currRegalboden.getyPos() + currRegalboden.getHoehe(), nextRegalboden);
        }
    }

    /**
     * Berechnet den untersten Ablagebereich bei einer Außenwand, vorausgesetzt, es gibt mindestens drei tragende Regalböden.
     * <p>
     * Diese Methode prüft, ob die übergebene {@link Regalwand} Teil einer Außenwand ist und ob sie mindestens drei tragende Regalböden hat.
     * Unter diesen Bedingungen berechnet sie den untersten Ablagebereich, der für die Platzierung zusätzlicher Gegenstände benötigt wird.
     * Anschließend wird der Ablagebereich an der Position des letzten Regalbodens initialisiert.
     *
     * @param regalBoeden Die Liste der {@link Regalboden}-Objekte, die zur Berechnung des untersten Ablagebereichs herangezogen werden.
     * @param regalwand   Die {@link Regalwand}, deren unterster Ablagebereich berechnet werden soll.
     */
    private void berechneUnterstenAblagebereichVonAussenwand(List<Regalboden> regalBoeden, Regalwand regalwand) {
        if (!regalwand.isTeilVonRahmen())
            return;

        if (regalwand.getTragendeRegalboeden().size() < 3)
            return;

        int regalBodenLaenge = regalBoeden.size();
        var lastRegalboden = regalBoeden.get(regalBodenLaenge - 2);

        double bereichHoehe = regalwand.getHoehe() - lastRegalboden.getyPos() - lastRegalboden.getHoehe() - REGAL_BODEN_HOEHE;
        double bereichBreite = lastRegalboden.getBreite();

        initialisiereAblagebereich(bereichHoehe, bereichBreite, lastRegalboden.getxPos(),
                lastRegalboden.getyPos() + lastRegalboden.getHoehe(), regalBoeden.getLast());
    }

    /**
     * Berechnet den Ablagebereich einer Außenwand basierend auf den tragenden Regalböden.
     * <p>
     * Diese Methode prüft, ob die übergebene {@link Regalwand} Teil einer Außenwand ist und berechnet dann den Ablagebereich entsprechend.
     * Wenn die Außenwand nur zwei tragende Regalböden hat, die Regaldecke und den Regalboden, wird ein spezieller
     * Berechnungsprozess gestartet.
     * In allen anderen Fällen werden die tragenden Regalböden nach ihrer Y-Position sortiert und dann die Ablagebereiche für verschiedene Bereiche der Außenwand berechnet:
     * - Oberer Ablagebereich oberhalb des ersten Regalbodens.
     * - Ablagebereiche zwischen den Regalböden.
     * - Unterster Ablagebereich bis zum Boden.
     *
     * @param regalwand            Die {@link Regalwand}, deren Ablagebereich berechnet werden soll.
     * @param regalWandRechtsDavon Die rechte benachbarte {@link Regalwand}, die zur Bestimmung des Ablagebereichs benötigt wird.
     */
    private void berechneAblagebereichVonAussenwand(Regalwand regalwand, Regalwand regalWandRechtsDavon) {
        if (!regalwand.isTeilVonRahmen())
            return;

        //Aussenwand hat 2 Regalböden am Anfang. Nur dann soll ein Ablagebereich errechnet werden
        if (regalwand.getTragendeRegalboeden().size() == 2) {
            berechneAblagebereichVonAussenwandWennKeineRegalboeden(regalwand, regalWandRechtsDavon);

        } else {
            //Erst sortieren
            var regalBoeden = regalwand.getTragendeRegalboeden();
            sortiereRegalboedenNachYPos(regalBoeden);

            if (!regalBoeden.isEmpty()) {
                //Ersten Bereich oberhalb des ersten Regalbodens innerhalb der Regalspalte bestimmen.
                berechneOberenAblagebereichBeiAussenwand(regalBoeden, regalwand);

                //Bereiche zwischen Regalböden
                berechneAblagebereicheZwischenBoedenVonAussenwand(regalBoeden, regalwand);

                //Letzten Bereich bis zum Boden bestimmen
                berechneUnterstenAblagebereichVonAussenwand(regalBoeden, regalwand);

            }

        }
    }

    /**
     * Berechnet den Ablagebereich einer Außenwand, falls keine tragenden Regalböden vorhanden sind.
     * <p>
     * Diese Methode prüft, ob die übergebene {@link Regalwand} keine tragenden Regalböden hat.
     * Der berechnete Bereich berücksichtigt die Höhe der Außenwand und die Positionen der beteiligten Wände.
     * Anschließend wird der Ablagebereich initialisiert.
     *
     * @param regalwand            Die {@link Regalwand}, deren Ablagebereich berechnet werden soll.
     * @param regalWandRechtsDavon Die rechte benachbarte {@link Regalwand}, die zur Bestimmung der Breite des Ablagebereichs benötigt wird.
     */
    private void berechneAblagebereichWennKeineBoeden(Regalwand regalwand, Regalwand regalWandRechtsDavon) {
        if (!regalwand.getTragendeRegalboeden().isEmpty())
            return;

        double bereichHoehe = regalwand.getHoehe();
        double bereichBreite = regalWandRechtsDavon.getxPos() - regalwand.getxPos();

        initialisiereAblagebereich(bereichHoehe, bereichBreite, regalwand.getxPos() + regalwand.getBreite(),
                regalwand.getyPos(), regal.getRegalboden());
    }

    /**
     * Berechnet den oberen Ablagebereich einer Außenwand basierend auf den tragenden Regalböden.
     * <p>
     * Diese Methode berechnet den oberen Ablagebereich, der sich oberhalb des ersten Regalbodens innerhalb der Regalspalte befindet.
     * Der berechnete Bereich berücksichtigt die Höhe des ersten Regalbodens und seine Breite sowie die Positionen der beteiligten Wände.
     * Anschließend wird der Ablagebereich initialisiert.
     *
     * @param regalBoeden Die Liste der {@link Regalboden}-Objekte, die zur Berechnung des oberen Ablagebereichs herangezogen werden.
     * @param regalwand   Die {@link Regalwand}, deren oberer Ablagebereich berechnet werden soll.
     */
    private void berechneOberenAblagebereich(List<Regalboden> regalBoeden, Regalwand regalwand) {
        //Ersten Bereich oberhalb des ersten Regalbodens innerhalb der Regalspalte bestimmen.
        double bereichHoehe = regalBoeden.getFirst().getyPos() - REGAL_BODEN_HOEHE;
        double bereichBreite = regalBoeden.getFirst().getBreite();

        initialisiereAblagebereich(bereichHoehe, bereichBreite, regalwand.getxPos() + regalwand.getBreite(),
                regalwand.getyPos(), regalBoeden.getFirst());
    }

    /**
     * Berechnet die Ablagebereiche zwischen den Regalböden.
     *
     * @param regalBoeden Die Liste der {@link Regalboden}-Objekte, die zur Berechnung der Ablagebereiche herangezogen werden.
     */
    private void berechneAblagebereichZwischenRegalboeden(List<Regalboden> regalBoeden) {
        //Bestimmen der Bereiche zwischen Regalböden
        for (int j = 0; j < regalBoeden.size() - 1; j++) {
            Regalboden currRegalboden = regalBoeden.get(j);
            Regalboden nextRegalboden = regalBoeden.get(j + 1);
            double bereichHoehe =
                    nextRegalboden.getyPos() - currRegalboden.getyPos() - currRegalboden.getHoehe();
            double bereichBreite = currRegalboden.getBreite();

            initialisiereAblagebereich(bereichHoehe, bereichBreite, currRegalboden.getxPos(),
                    currRegalboden.getyPos() + currRegalboden.getHoehe(), nextRegalboden);

        }
    }

    /**
     * Berechnet den untersten Ablagebereich einer Außenwand basierend auf den tragenden Regalböden.
     *
     * @param regalBoeden Die Liste der {@link Regalboden}-Objekte, die zur Berechnung des untersten Ablagebereichs herangezogen werden.
     * @param regalwand   Die {@link Regalwand}, deren unterster Ablagebereich berechnet werden soll.
     */
    private void berechneUnterstenAblagebereich(List<Regalboden> regalBoeden, Regalwand regalwand) {
        //Letzten Bereich bis zum Boden bestimmen
        var lastRegalboden = regalBoeden.getLast();

        double bereichHoehe = regalwand.getHoehe() - lastRegalboden.getyPos();
        double bereichBreite = lastRegalboden.getBreite();

        initialisiereAblagebereich(bereichHoehe, bereichBreite, lastRegalboden.getxPos(),
                lastRegalboden.getyPos() + lastRegalboden.getHoehe(), regal.getRegalboden());
    }

    /**
     * Berechnet alle Ablagebereiche einer Außenwand basierend auf den tragenden Regalböden.
     * <p>
     * Diese Methode führt eine Reihe von Schritten durch, um die Ablagebereiche einer Außenwand zu berechnen:
     * 1. Sortiert die Regalböden nach ihrer Y-Position.
     * 2. Berechnet den oberen Ablagebereich oberhalb des ersten Regalbodens.
     * 3. Berechnet die Ablagebereiche zwischen den Regalböden.
     * 4. Bestimmt den untersten Ablagebereich bis zum Boden.
     * <p>
     * Durch diese Schritte wird sichergestellt, dass alle notwendigen Ablagebereiche korrekt berechnet und initialisiert werden.
     *
     * @param regalBoeden Die Liste der {@link Regalboden}-Objekte, die zur Berechnung der Ablagebereiche herangezogen werden.
     * @param regalwand   Die {@link Regalwand}, deren Ablagebereiche berechnet werden sollen.
     */
    private void berechneAblagebereicheMitBoeden(List<Regalboden> regalBoeden, Regalwand regalwand) {
        sortiereRegalboedenNachYPos(regalBoeden);

        berechneOberenAblagebereich(regalBoeden, regalwand);

        berechneAblagebereichZwischenRegalboeden(regalBoeden);

        berechneUnterstenAblagebereich(regalBoeden, regalwand);
    }


    /**
     * Berechnet die Ablagebereiche für alle Regalwände innerhalb eines Regals.
     * <p>
     * Diese Methode führt eine Reihe von Schritten durch, um die Ablagebereiche für alle Regalwände innerhalb eines gegebenen Regalsystems zu berechnen:
     * 1. Leert zunächst die vorhandenen Ablagebereiche im Regal.
     * 2. Holt die Liste aller Regalwände aus dem Regal.
     * 3. Sortiert die Regalwände nach ihrer X-Position.
     * 4. Iteriert über die sortierte Liste der Regalwände und berechnet die Ablagebereiche je nach Bedingungen:
     * - Wenn die aktuelle Regalwand Teil einer Außenwand ist, berechnet sie den Ablagebereich für diese Außenwand.
     * - Wenn die aktuelle Regalwand keine tragenden Regalböden hat, berechnet sie den Ablagebereich entsprechend.
     * - Wenn die aktuelle Regalwand tragende Regalböden hat, berechnet sie die Ablagebereiche mithilfe dieser Böden.
     * <p>
     * Durch diese Schritte wird sichergestellt, dass alle Ablagebereiche innerhalb des Regals korrekt berechnet und
     * aktualisiert werden.
     */
    @Override
    public void berechneAblagebereiche() {
        regal.getAblagebereiche().clear();
        List<Regalwand> regalwaende = regal.getRegalwaende();

        //Sortieren der Regalwaende nach xPos
        sortiereRegalwaendeNachXPos(regalwaende);

        for (int i = 0; i < regalwaende.size() - 1; i++) {
            Regalwand currRegalwand = regalwaende.get(i);
            Regalwand nextRegalwand = regalwaende.get(i + 1);

            if (currRegalwand.isTeilVonRahmen()) {
                berechneAblagebereichVonAussenwand(currRegalwand, nextRegalwand);

            } else if (currRegalwand.getTragendeRegalboeden().isEmpty()) { //Wenn Regalwand keine Regalböden
                berechneAblagebereichWennKeineBoeden(currRegalwand, nextRegalwand);
            } else {
                var regalBoeden = currRegalwand.getTragendeRegalboeden();
                berechneAblagebereicheMitBoeden(regalBoeden, currRegalwand);
            }
        }
    }

    /**
     * Initialisiert einen neuen Ablagebereich mit den angegebenen Dimensionen und Positionen.
     * <p>
     * Diese Methode erstellt ein neues {@link Ablagebereich}-Objekt mit den angegebenen Höhe und Breite,
     * setzt die Positionen des Ablagebereichs entsprechend den übergebenen x- und y-Koordinaten und fügt
     * das neue Objekt zur Liste der Ablagebereiche im Regal hinzu.
     *
     * @param hoehe  Die Höhe des Ablagebereichs.
     * @param breite Die Breite des Ablagebereichs.
     * @param xPos   Die x-Position des Ablagebereichs.
     * @param yPos   Die y-Position des Ablagebereichs.
     */
    private void initialisiereAblagebereich(double hoehe, double breite, double xPos, double yPos,
                                            Regalboden untererRegalboden) {
        Ablagebereich ablagebereich = new Ablagebereich(hoehe, breite, untererRegalboden);
        ablagebereich.setPositions(xPos, yPos);

        regal.getAblagebereiche().add(ablagebereich);
    }

    /**
     * Bestimmt die Breite eines Regalbodens basierend auf seiner Position zwischen zwei benachbarten Regalwänden.
     * <p>
     * Diese Methode sortiert zunächst die Regalwände nach ihrer X-Position, um sicherzustellen, dass sie in der richtigen Reihenfolge betrachtet werden.
     * Dann iteriert sie über die sortierte Liste der Regalwände und überprüft, ob der übergebene Regalboden als tragender Regalboden in der aktuellen Regalwand enthalten ist.
     * Wenn dies der Fall ist, berechnet sie die Breite des Regalbodens basierend auf der Position zwischen der aktuellen und der nächsten Regalwand.
     * Falls der Regalboden nicht zwischen zwei Regalwänden gefunden wird, gibt die Methode die Breite des Regalbodens zurück, sofern sie bereits bekannt ist.
     *
     * @param regalboden Der {@link Regalboden}, dessen Breite bestimmt werden soll.
     * @return Die Breite des Regalbodens, berechnet basierend auf seiner Position zwischen zwei benachbarten Regalwänden, oder die bekannte Breite des Regalbodens, wenn kein solcher Bereich gefunden wurde.
     */
    private double bestimmeRegalbodenBreite(Regalboden regalboden) {
        //Sortieren der Regalwaende nach xPos
        sortiereRegalwaendeNachXPos(regal.getRegalwaende());

        for (int i = 0; i < regal.getRegalwaende().size() - 1; i++) {
            Regalwand currRegalwand = regal.getRegalwaende().get(i);
            Regalwand nextRegalwand = regal.getRegalwaende().get(i + 1);
            var tragendeRegalboeden = currRegalwand.getTragendeRegalboeden();

            if (tragendeRegalboeden.contains(regalboden)) {
                // Berechnung der Breite des Regalbodens zwischen zwei Regalwaenden
                return nextRegalwand.getxPos() - (currRegalwand.getxPos() + currRegalwand.getBreite());
            }
        }
        return regalboden.getBreite();
    }


    /**
     * Findet die nächstgelegene Regalwand links vom gegebenen Regalboden.
     * <p>
     * Diese Methode durchsucht die Liste aller Regalwände und sucht nach derjenigen, die links vom übergebenen Regalboden liegt.
     * Sie berechnet die vertikale Distanz zwischen dem Regalboden und jeder Regalwand und vergleicht diese Distanz mit einem Startwert für die minimale Distanz.
     * Wenn die Distanz kleiner ist als der bisher gemessene minimaler Wert, wird die Distanz aktualisiert und die aktuelle Regalwand als nächstgelegene gespeichert.
     * Am Ende der Suche wird die Regalwand mit der kleinsten Distanz zurückgegeben, falls eine solche gefunden wurde.
     *
     * @param regalboden Der {@link Regalboden}, für den die nächstgelegene Regalwand links gesucht wird.
     * @return Die nächstgelegene {@link Regalwand} links vom Regalboden, oder {@code null}, wenn keine passende Regalwand gefunden wurde.
     */
    private Regalwand findeNaehesteRegalwand(Regalboden regalboden) {
        Regalwand naehesteRegalwand = null;
        double minDistance = Double.MAX_VALUE; // Startwert für die minimale Distanz

        for (Regalwand regalwand : regal.getRegalwaende()) {
            // Prüfen, ob die Regalwand links vom Regalboden liegt
            if (regalboden.getxPos() >= regalwand.getxPos()) {
                // Berechnen der Distanz zwischen den X-Positionen
                double distance = regalboden.getxPos() - regalwand.getxPos();
                if (distance < minDistance) {
                    minDistance = distance;
                    naehesteRegalwand = regalwand;
                }
            }
        }

        return naehesteRegalwand;
    }

    /**
     * Löscht eine ausgewählte Regalwand aus dem System.
     * <p>
     * Diese Methode überprüft zunächst, ob die übergebene {@link Regalwand} gültig ist, indem sie sicherstellt, dass sie nicht null ist und nicht Teil einer Außenwand ist.
     * Darüber hinaus muss die Regalwand keine tragenden Regalböden mehr enthalten, bevor sie gelöscht werden kann.
     * Nachdem all diese Bedingungen erfüllt sind, entfernt die Methode die Regalwand aus der Liste der Regalwände im Regalsystem.
     *
     * @param regalwand Die {@link Regalwand}, die aus dem System gelöscht werden soll.
     * @throws IllegalStateException Wenn die Regalwand ungültig ist (null, Teil einer Außenwand oder noch tragende Regalböden enthält).
     */
    @Override
    public void loescheRegalwand(Regalwand regalwand) throws RegalkomponenteNichtLoeschbarException {
        if (regalwand == null) {
            throw new RegalkomponenteNichtLoeschbarException("Keine Regalwand ausgewaehlt.");
        }

        if (regalwand.isTeilVonRahmen()) {
            throw new RegalkomponenteNichtLoeschbarException("Regalwand kann nicht gelöscht werden, da es den Aussenwände bildet.");
        }

        if (!regalwand.getTragendeRegalboeden().isEmpty()) {
            throw new RegalkomponenteNichtLoeschbarException("Regalwand kann nicht gelöscht werden, da sie noch Regalböden trägt.");
        }

        //Löschen aller Referenzen
        regal.getRegalwaende().remove(regalwand);
    }

    /**
     * Löscht einen ausgewählten Regalboden aus dem System.
     * <p>
     * Diese Methode überprüft zunächst, ob die übergebene {@link Regalboden} gültig ist, indem sie sicherstellt, dass sie nicht null ist und nicht Teil einer Außenwand ist.
     * Wenn diese Bedingungen erfüllt sind, entfernt die Methode den Regalboden aus der Liste der Regalböden im Regalsystem und aktualisiert die Liste der tragenden Regalböden in allen Regalwänden entsprechend.
     * Fällt die übergebene Regalboden jedoch nicht unter diese Kriterien, wird eine IllegalStateException ausgelöst.
     *
     * @param regalboden Der {@link Regalboden}, der aus dem System gelöscht werden soll.
     * @throws IllegalStateException Wenn kein gültiger Regalboden ausgewählt wurde (null oder Teil einer Außenwand).
     */
    @Override
    public void loescheRegalboden(Regalboden regalboden) throws RegalkomponenteNichtLoeschbarException {
        if (regalboden != null && !regalboden.isTeilVonRahmen()) {
            //Löschen aller Referenzen
            regal.getRegalboeden().remove(regalboden);

            for (Regalwand regalwand : regal.getRegalwaende()) {
                regalwand.getTragendeRegalboeden().remove(regalboden);
            }

        } else {

            throw new RegalkomponenteNichtLoeschbarException("Kein Regalboden ausgewaehlt.");
        }
    }


    @Override
    public void editierenAbschliessen() {

    }

    @Override
    public void editierenAbbrechen() {

    }

    @Override
    public void setzeNeuesRegal(Regal regal) {
        this.regal = regal;
    }

    @Override
    public Regal erhalteRegal() {
        return regal;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
