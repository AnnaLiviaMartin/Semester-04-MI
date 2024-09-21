package de.hsrm.mi.softwaretechnik.projekt.business.services;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Ablagebereich;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Regal;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Regalboden;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Regalwand;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.RegalkomponenteNichtPlatzierbarException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LagereditorTest {
    private final double WINDOW_WIDTH = 1400;
    private final double WINDOW_HEIGHT = 900;
    private final double REGAL_BODEN_HOEHE = 20;
    private final double REGAL_WAND_BREITE = 20;

    private LagereditorImpl lagereditor;
    private Regal regal;

    @BeforeEach
    void setUp() {
        try {
            lagereditor = new LagereditorImpl(WINDOW_WIDTH, WINDOW_HEIGHT, REGAL_WAND_BREITE, REGAL_BODEN_HOEHE);
        } catch (RegalkomponenteNichtPlatzierbarException e) {
            throw new RuntimeException(e);
        }
        regal = lagereditor.erhalteRegal();
    }

    @Test
    void testRegalrahmenInitialisierung() {
        // Überprüfen, ob die Wände und Böden korrekt initialisiert wurden
        assertEquals(2, regal.getRegalwaende().size(), "Es sollten zwei Wände initialisiert worden sein");
        assertEquals(2, regal.getRegalboeden().size(), "Es sollten zwei Böden initialisiert worden sein");

        // Überprüfen der Positionen und Größen der Wände
        Regalwand wandLinks = regal.getRegalwaende().get(0);
        assertEquals(REGAL_WAND_BREITE, wandLinks.getBreite(), "Die Breite der linken Wand sollte korrekt sein");
        assertEquals(regal.getHoehe(), wandLinks.getHoehe(), "Die Höhe der linken Wand sollte korrekt sein");
        assertEquals(0, wandLinks.getxPos(), "Die x-Position der linken Wand sollte korrekt sein");
        assertEquals(0, wandLinks.getyPos(), "Die y-Position der linken Wand sollte korrekt sein");

        Regalwand wandRechts = regal.getRegalwaende().get(1);
        assertEquals(REGAL_WAND_BREITE, wandRechts.getBreite(), "Die Breite der rechten Wand sollte korrekt sein");
        assertEquals(regal.getHoehe(), wandRechts.getHoehe(), "Die Höhe der rechten Wand sollte korrekt sein");
        assertEquals(regal.getBreite() - REGAL_WAND_BREITE, wandRechts.getxPos(), "Die x-Position der rechten Wand " +
                "sollte korrekt sein");
        assertEquals(0, wandRechts.getyPos(), "Die y-Position der liken Wand sollte korrekt sein");

        // Überprüfen der Positionen und Größen der Böden
        Regalboden regalDecke = regal.getRegalboeden().get(0);
        assertEquals(REGAL_BODEN_HOEHE, regalDecke.getHoehe(), "Die Höhe des Deckenbodens sollte korrekt sein");
        assertEquals(regal.getBreite() - wandLinks.getBreite() - wandRechts.getBreite(), regalDecke.getBreite(), "Die " +
                "Breite des Deckenbodens sollte korrekt sein");
        assertEquals(REGAL_WAND_BREITE, regalDecke.getxPos(), "Die x-Position des Deckenbodens sollte korrekt sein");
        assertEquals(0, regalDecke.getyPos(), "Die y-Position des Deckenbodens sollte korrekt sein");

        Regalboden bodenUnten = regal.getRegalboeden().get(1);
        assertEquals(REGAL_BODEN_HOEHE, bodenUnten.getHoehe(), "Die Höhe des Bodens unten sollte korrekt sein");
        assertEquals(regal.getBreite() - wandLinks.getBreite() - wandRechts.getBreite(), bodenUnten.getBreite(),
                "Die Breite des Regalbodens sollte korrekt sein.");
        assertEquals(REGAL_WAND_BREITE, bodenUnten.getxPos(), "Die x-Position des Bodens unten sollte korrekt sein.");
        assertEquals(regal.getHoehe() - REGAL_BODEN_HOEHE, bodenUnten.getyPos(), "Die y-Position des Bodens " +
                "unten sollte korrekt sein");

        //Prüfe, ob alle Komponenten des Regalrahmens teil der Aussenwand sind sind
        for (Regalwand wand : regal.getRegalwaende()) {
            assertTrue(wand.isTeilVonRahmen());
        }

        for (Regalboden regalboden : regal.getRegalboeden()) {
            assertTrue(regalboden.isTeilVonRahmen());
        }
    }

    @Test
    void testInitialenAblagebereich() {
        assertEquals(regal.getAblagebereiche().size(), 1, "Es darf am Anfang nur ein Ablagebereich geben.");

        Ablagebereich ersterAblagebereich = regal.getAblagebereiche().get(0);
        assertEquals(ersterAblagebereich.getHoehe(),
                regal.getAussenwandLinks().getHoehe() - regal.getRegalDecke().getHoehe() - regal.getRegalboden().getHoehe(),
                "Die Hoehe des ersten Lagerbereichs muss korrekt sein");
        assertEquals(ersterAblagebereich.getBreite(), regal.getRegalDecke().getBreite(), "Die Breite des ersten" +
                " Ablagebereichs muss korrekt sein");
        assertEquals(ersterAblagebereich.getPosX(), regal.getAussenwandLinks().getBreite(), "Die x-Position des" +
                " ersten Ablagebereichs muss korrekt sein");
        assertEquals(ersterAblagebereich.getPosY(), regal.getRegalDecke().getHoehe(), "Die y-Position des " +
                "ersten Ablagebereichs muss korrekt sein");

        assertEquals(ersterAblagebereich.getUntererRegalboden(), regal.getRegalboden(), "Der erste Ablagebereich " +
                "sollte auf dem unteren Regalboden liegen.");

    }

    @Test
    void testPlatziereRegalwand() {
        Regalwand regalwand1 = lagereditor.erstelleRegalwand();
        try {
            lagereditor.platziereRegalwand(regalwand1, 100);

            assertEquals(regal.getAussenwandLinks().getyPos() + regal.getRegalDecke().getHoehe(),
                    regalwand1.getyPos(), "Die y-Position der Regalwand muss korrekt sein");
            assertEquals(100, regalwand1.getxPos(), "Die x-Position der Regalwand muss korrekt sein");
            assertEquals(regal.getRegalwaende().size(), 3, "Das Regal muss die Regalwand in ihre Liste aufnehmen");
            assertEquals(regal.getAblagebereiche().size(), 2, "Das Regal muss einen weiteren Ablagebereich haben");

            Regalwand regalwand2 = lagereditor.erstelleRegalwand();
            lagereditor.platziereRegalwand(regalwand2, 200);
            assertEquals(regal.getRegalwaende().size(), 4, "Das Regal muss die Regalwand in ihre Liste aufnehmen");
            assertEquals(regal.getAblagebereiche().size(), 3, "Das Regal muss einen weiteren Ablagebereich haben");

        } catch (RegalkomponenteNichtPlatzierbarException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testPlatziereRegalbodenOhneWeiterenRegalwaenden() {
        Regalboden regalboden1 = new Regalboden(REGAL_BODEN_HOEHE);
        try {
            lagereditor.platziereRegalboden(regalboden1, 200, 100);


            Regalboden regalboden2 = new Regalboden(REGAL_BODEN_HOEHE);
            lagereditor.platziereRegalboden(regalboden2, 400, 50);

            Regalboden regalboden3 = new Regalboden(REGAL_BODEN_HOEHE);
            lagereditor.platziereRegalboden(regalboden3, 600, 30);

            assertEquals(regalboden1.getBreite(), regal.getRegalDecke().getBreite(), "Erster Regalboden sollte die selbe " +
                    "Breite sein, wie die Regaldecke");
            assertEquals(regalboden1.getxPos(), regal.getAussenwandLinks().getBreite(), "Die x-Position des ersten " +
                    "Regalbodens sollte korrekt sein");

            assertEquals(regalboden2.getBreite(), regal.getRegalDecke().getBreite(), "Zweiter Regalboden sollte die selbe" +
                    "Breite sein, wie die Regaldecke");
            assertEquals(regalboden2.getxPos(), regal.getAussenwandLinks().getBreite(), "Die x-Position des ersten " +
                    "Regalbodens sollte korrekt sein");

            assertEquals(regalboden3.getBreite(), regal.getRegalDecke().getBreite(), "Dritter Regalboden sollte die selbe" +
                    "Breite sein, wie die Regaldecke");
            assertEquals(regalboden2.getxPos(), regal.getAussenwandLinks().getBreite(), "Die x-Position des ersten " +
                    "Regalbodens sollte korrekt sein");

            assertEquals(regal.getAussenwandLinks().getTragendeRegalboeden().size(), 5, "Die Wand links müsste 5 tragende" +
                    " Regalboeden besitzen");
        } catch (RegalkomponenteNichtPlatzierbarException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testPlatziereRegalbodenMitRegalwaenden() {
        Regalwand regalwand1 = lagereditor.erstelleRegalwand();
        try {
            lagereditor.platziereRegalwand(regalwand1, 400);

            Regalwand regalwand2 = lagereditor.erstelleRegalwand();
            lagereditor.platziereRegalwand(regalwand2, 600);

            //Sollte an der Aussenwand links hängen
            Regalboden regalboden1 = new Regalboden(REGAL_BODEN_HOEHE);
            lagereditor.platziereRegalboden(regalboden1, 200, 100);

            //Sollte an der Wand 1 hängen
            Regalboden regalboden2 = new Regalboden(REGAL_BODEN_HOEHE);
            lagereditor.platziereRegalboden(regalboden2, 400, 450);

            //Sollte an der Wand 1 hängen
            Regalboden regalboden3 = new Regalboden(REGAL_BODEN_HOEHE);
            lagereditor.platziereRegalboden(regalboden3, 600, 500);

            //Sollte an der Wand 2 hängen
            Regalboden regalboden4 = new Regalboden(REGAL_BODEN_HOEHE);
            lagereditor.platziereRegalboden(regalboden4, 200, 650);

            //Sollte an der Wand 2 hängen
            Regalboden regalboden5 = new Regalboden(REGAL_BODEN_HOEHE);
            lagereditor.platziereRegalboden(regalboden5, 400, 700);

            //Sollte an der Wand 2 hängen
            Regalboden regalboden6 = new Regalboden(REGAL_BODEN_HOEHE);
            lagereditor.platziereRegalboden(regalboden6, 600, 750);

            //Prüfung Anzahl der Regalböden an den Wänden
            assertEquals(regal.getAussenwandLinks().getTragendeRegalboeden().size(), 3, "Die tragenenden Regalboeden der " +
                    " linken Aussenwand sollte korrekt sein");
            assertEquals(regalwand1.getTragendeRegalboeden().size(), 2, "Die tragenden Regalboeden der ersten Wand " +
                    "sollten korrekt sein.");
            assertEquals(regalwand2.getTragendeRegalboeden().size(), 3, "Die tragenden Regalboeden der zweiten Wand " +
                    "sollten korrekte sein.");

            assertEquals(regal.getAblagebereiche().size(), 9, "Die Anzahl der Ablagebereich müssen korrekt berechnet sein" +
                    ".");

            //Prüfung der Breiten der Regalboeden
            assertEquals(regalboden1.getBreite(),
                    regalwand1.getxPos() - (regal.getAussenwandLinks().getxPos() + regalwand1.getBreite()), "Die breite " +
                            "des Regalbodens an der Aussenwand sollte korrekt sein.");

            assertEquals(regalboden2.getBreite(), regalwand2.getxPos() - (regalwand1.getxPos() + regalwand1.getBreite()),
                    "Die Breite des ersten Regalbodens an der ersten Regalwand sollte korrekt sein.");
            assertEquals(regalboden3.getBreite(), regalwand2.getxPos() - (regalwand1.getxPos() + regalwand1.getBreite()),
                    "Die Breite des zweiten Regalbodens an der ersten Regalwand sollte korrekt sein.");

            assertEquals(regalboden4.getBreite(),
                    regal.getAussenwandRechts().getxPos() - (regalwand2.getxPos() + regalwand2.getBreite()), "Die Breite " +
                            "des ersten Regalbodens der zweiten Regalwand sollte korrekt sein.");

            assertEquals(regalboden5.getBreite(),
                    regal.getAussenwandRechts().getxPos() - (regalwand2.getxPos() + regalwand2.getBreite()), "Die Breite " +
                            "des zweiten Regalbodens der zweiten Regalwand sollte korrekt sein.");

            assertEquals(regalboden6.getBreite(),
                    regal.getAussenwandRechts().getxPos() - (regalwand2.getxPos() + regalwand2.getBreite()), "Die Breite " +
                            "des dritten Regalbodens der zweiten Regalwand sollte korrekt sein.");

        } catch (RegalkomponenteNichtPlatzierbarException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAblagebereicheNurMitRegalwaenden() {
        Regalwand regalwand1 = lagereditor.erstelleRegalwand();
        try {
            lagereditor.platziereRegalwand(regalwand1, 100);


            Regalwand regalwand2 = lagereditor.erstelleRegalwand();
            lagereditor.platziereRegalwand(regalwand2, 200);

            var linkeRegalaussenwand = regal.getAussenwandLinks();

            var ablagebereiche = regal.getAblagebereiche();
            var regalwaende = regal.getRegalwaende();

            assertEquals(ablagebereiche.size(), 3, "Das Regal sollte drei Ablagebereiche haben");

            var linkerAblagebereich = ablagebereiche.get(0);
            var mittlererAblagebereich = ablagebereiche.get(1);
            var rechterAblagebereich = ablagebereiche.get(2);

            //Ablagebereich von Aussenwand ist der erste Ablagebereich
            assertEquals(linkerAblagebereich.getPosY(), linkeRegalaussenwand.getyPos() + regal.getRegalDecke().getHoehe()
                    , "Die y-Position des ersten Ablagebereichs sollte korrekt sein");
            assertEquals(linkerAblagebereich.getPosX(), linkeRegalaussenwand.getxPos() + REGAL_WAND_BREITE, "Die " +
                    "x-Position des ersten Ablagebereichs sollte korrekt sein");
            assertEquals(linkerAblagebereich.getHoehe(), regalwaende.get(0).getHoehe() - 2 * REGAL_BODEN_HOEHE, "Die " +
                    "Hoehe des ersten Ablagebereichs sollte korrekt sein");
            assertEquals(linkerAblagebereich.getBreite(), regalwand1.getxPos() - REGAL_WAND_BREITE, "Die Breite des " +
                    "ersten Ablagebereichs sollte korrekt sein");
            assertEquals(linkerAblagebereich.getUntererRegalboden(), regal.getRegalboden(), "Der linke Ablagebereich " +
                    "sollte den Regalboden als unterenRegalboden haben.");


            //Mittlerer/Zweiter Ablagebereich
            assertEquals(mittlererAblagebereich.getPosY(), regalwand1.getyPos(), "Die y-Position des zweiten " +
                    "Ablagebereichs sollte korrekt sein");
            assertEquals(mittlererAblagebereich.getPosX(), regalwand1.getxPos() + regalwand1.getBreite(), "Die x-Position" +
                    " des zweiten Ablagebereichs sollte korrekt sein");
            assertEquals(mittlererAblagebereich.getHoehe(), regalwand1.getHoehe(), "Die Hoehe des zweiten Ablagebereichs " +
                    "sollte korrekt sein");
            assertEquals(mittlererAblagebereich.getBreite(), regalwand2.getxPos() - regalwand1.getxPos(), "Die Breite des" +
                    " zweiten Ablagebereichs sollte korrekt sein");
            assertEquals(mittlererAblagebereich.getUntererRegalboden(), regal.getRegalboden(), "Der mittlere Ablagebereich " +
                    "sollte den Regalboden als unterenRegalboden haben.");

            //Rechter/Dritter Ablagebereich
            assertEquals(rechterAblagebereich.getPosY(), regalwand2.getyPos(), "Die y-Position des dritten " +
                    "Ablagebereichs sollte korrekt sein");
            assertEquals(rechterAblagebereich.getPosX(), regalwand2.getxPos() + regalwand2.getBreite(), "Die x-Position" +
                    " des dritten Ablagebereichs sollte korrekt sein");
            assertEquals(rechterAblagebereich.getHoehe(), regalwand2.getHoehe(), "Die Hoehe des dritten Ablagebereichs " +
                    "sollte korrekt sein");
            assertEquals(rechterAblagebereich.getBreite(), regal.getAussenwandRechts().getxPos() - regalwand2.getxPos(), "Die Breite des" +
                    " dritten Ablagebereichs sollte korrekt sein");
            assertEquals(rechterAblagebereich.getUntererRegalboden(), regal.getRegalboden(), "Der rechte Ablagebereich " +
                    "sollte den Regalboden als unterenRegalboden haben.");
        } catch (RegalkomponenteNichtPlatzierbarException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCollisionDetected() {
        Regalboden regalboden1AnAussenwandLinks = new Regalboden(REGAL_BODEN_HOEHE);
        try {
            lagereditor.platziereRegalboden(regalboden1AnAussenwandLinks, 200, 100);

            Regalwand regalwand = lagereditor.erstelleRegalwand();
            RegalkomponenteNichtPlatzierbarException  exception = assertThrows(RegalkomponenteNichtPlatzierbarException .class, () -> {
                lagereditor.platziereRegalwand(regalwand, 300);
            });

            assertEquals(exception.getMessage(), "Platzierung der Regalwand nicht möglich, da sie mit einem Regalboden " +
                    "kollidiert.");
        } catch (RegalkomponenteNichtPlatzierbarException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAblagereichBerechnung() {
        var linkeRegalaussenwand = regal.getAussenwandLinks();

        Regalwand regalwand1 = lagereditor.erstelleRegalwand();
        try {
            lagereditor.platziereRegalwand(regalwand1, 400);

            //Sollte an der Aussenwand links hängen
            Regalboden regalboden1AnAussenwandLinks = new Regalboden(REGAL_BODEN_HOEHE);
            lagereditor.platziereRegalboden(regalboden1AnAussenwandLinks, 200, 100);

            //Sollte an der Aussenwand links hängen
            Regalboden regalboden2AnAussenwandLinks = new Regalboden(REGAL_BODEN_HOEHE);
            lagereditor.platziereRegalboden(regalboden2AnAussenwandLinks, 400, 200);

            //Sollte an der Aussenwand links hängen
            Regalboden regalboden3AnAussenwandLinks = new Regalboden(REGAL_BODEN_HOEHE);
            lagereditor.platziereRegalboden(regalboden3AnAussenwandLinks, 600, 250);

            //Sollte an Regalwand 1 hängen
            Regalboden regalboden1AnRegalwand1 = new Regalboden(REGAL_BODEN_HOEHE);
            lagereditor.platziereRegalboden(regalboden1AnRegalwand1, 200, 500);

            //Sollte an Regalwand 1 hängen
            Regalboden regalboden2AnRegalwand1 = new Regalboden(REGAL_BODEN_HOEHE);
            lagereditor.platziereRegalboden(regalboden2AnRegalwand1, 400, 500);

            //Sollte an Regalwand 1 hängen
            Regalboden regalboden3AnRegalwand1 = new Regalboden(REGAL_BODEN_HOEHE);
            lagereditor.platziereRegalboden(regalboden3AnRegalwand1, 600, 650);

            var ablagebereiche = regal.getAblagebereiche();

            assertEquals(ablagebereiche.size(), 8, "Die Anzahl an Ablagebereichen sollte korrekt sein.");

            assertEquals(linkeRegalaussenwand.getTragendeRegalboeden().size(), 5, "Die Anzahl an Regalboeden an der " +
                    "Linken Regalwand sollte korrekt sein.");

            assertEquals(regalwand1.getTragendeRegalboeden().size(), 3, "Die Anzahl an Regalboeden an der ersten " +
                    "Regalwand sollte korrekt sein.");

            var ablagebereich1 = ablagebereiche.get(0);
            var ablagebereich2 = ablagebereiche.get(1);
            var ablagebereich3 = ablagebereiche.get(2);
            var ablagebereich4 = ablagebereiche.get(3);
            var ablagebereich5 = ablagebereiche.get(4);
            var ablagebereich6 = ablagebereiche.get(5);
            var ablagebereich7 = ablagebereiche.get(6);
            var ablagebereich8 = ablagebereiche.get(7);

            //erster Ablagebereich
            assertEquals(ablagebereich1.getBreite(), regalboden1AnAussenwandLinks.getBreite(), "Der erste Ablagebereich " +
                    "sollte die korrekte Breite haben.");
            assertEquals(ablagebereich1.getHoehe(),
                    regalboden1AnAussenwandLinks.getyPos() - regal.getRegalDecke().getHoehe(), "Die Hoehe des Ersten " +
                            "Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich1.getPosY(), linkeRegalaussenwand.getyPos() + regal.getRegalDecke().getHoehe(),
                    "Die y-Position des ersten Ablagebereichs sollte korrekt sein");
            assertEquals(ablagebereich1.getPosX(), linkeRegalaussenwand.getxPos() + linkeRegalaussenwand.getBreite(),
                    "Die x-Position des ersten Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich1.getUntererRegalboden(), regalboden1AnAussenwandLinks, "Der Regalboden des ersten " +
                    "Ablagebereichs sollte korrekt sein.");

            //zweiter Ablagebereich
            assertEquals(ablagebereich2.getBreite(), regalboden1AnAussenwandLinks.getBreite(), "Der zweite Ablagebereich " +
                    "sollte die korrekte Breite haben.");
            assertEquals(ablagebereich2.getHoehe(),
                    regalboden2AnAussenwandLinks.getyPos() - regalboden1AnAussenwandLinks.getyPos() - regal.getRegalDecke().getHoehe(), "Die " +
                            "Hoehe des zweiten Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich2.getPosY(),
                    regalboden1AnAussenwandLinks.getyPos() + regalboden1AnAussenwandLinks.getHoehe(),
                    "Die y-Position des zweiten Ablagebereichs sollte korrekt sein");
            assertEquals(ablagebereich2.getPosX(), linkeRegalaussenwand.getxPos() + linkeRegalaussenwand.getBreite(),
                    "Die x-Position des zweiten Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich2.getUntererRegalboden(), regalboden2AnAussenwandLinks, "Der Regalboden des zweiten" +
                    "Ablagebereichs sollte korrekt sein.");

            //Dritter ablagebereich
            assertEquals(ablagebereich3.getBreite(), regalboden1AnAussenwandLinks.getBreite(), "Der dritte Ablagebereich " +
                    "sollte die korrekte Breite haben.");
            assertEquals(ablagebereich3.getHoehe(),
                    regalboden3AnAussenwandLinks.getyPos() - regalboden2AnAussenwandLinks.getyPos() - regal.getRegalDecke().getHoehe(), "Die " +
                            "Hoehe des dritten Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich3.getPosY(),
                    regalboden2AnAussenwandLinks.getyPos() + regalboden2AnAussenwandLinks.getHoehe(),
                    "Die y-Position des dritten Ablagebereichs sollte korrekt sein");
            assertEquals(ablagebereich3.getPosX(), linkeRegalaussenwand.getxPos() + linkeRegalaussenwand.getBreite(),
                    "Die x-Position des dritten Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich3.getUntererRegalboden(), regalboden3AnAussenwandLinks, "Der Regalboden des dritten" +
                    "Ablagebereichs sollte korrekt sein.");

            //Vierter Ablagebereich
            assertEquals(ablagebereich4.getBreite(), regalboden1AnAussenwandLinks.getBreite(), "Der vierte Ablagebereich " +
                    "sollte die korrekte Breite haben.");
            assertEquals(ablagebereich4.getHoehe(),
                    regal.getRegalboden().getyPos() - regalboden3AnAussenwandLinks.getyPos() - regal.getRegalDecke().getHoehe(), "Die " +
                            "Hoehe des vierten Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich4.getPosY(),
                    regalboden3AnAussenwandLinks.getyPos() + regalboden3AnAussenwandLinks.getHoehe(),
                    "Die y-Position des viertem Ablagebereichs sollte korrekt sein");
            assertEquals(ablagebereich4.getPosX(), linkeRegalaussenwand.getxPos() + linkeRegalaussenwand.getBreite(),
                    "Die x-Position des vierten Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich4.getUntererRegalboden(), regal.getRegalboden(), "Der Regalboden des vierten" +
                    "Ablagebereichs sollte korrekt sein.");


            //Fünfter Ablagebereich
            assertEquals(ablagebereich5.getBreite(), regalboden1AnRegalwand1.getBreite(), "Der fuenfte Ablagebereich " +
                    "sollte die korrekte Breite haben.");
            assertEquals(ablagebereich5.getHoehe(),
                    regalboden1AnRegalwand1.getyPos() - regal.getRegalDecke().getHoehe(),
                    "Die Hoehe des fuenften Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich5.getPosY(),
                    regalwand1.getyPos(),
                    "Die y-Position des fuenften Ablagebereichs sollte korrekt sein");
            assertEquals(ablagebereich5.getPosX(), regalwand1.getxPos() + regalwand1.getBreite(),
                    "Die x-Position des fuenften Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich5.getUntererRegalboden(), regalboden1AnRegalwand1, "Der Regalboden des fünften" +
                    "Ablagebereichs sollte korrekt sein.");

            //Sechster Ablagebereich
            assertEquals(ablagebereich6.getBreite(), regalboden1AnRegalwand1.getBreite(), "Der sechste Ablagebereich " +
                    "sollte die korrekte Breite haben.");
            assertEquals(ablagebereich6.getHoehe(),
                    regalboden2AnRegalwand1.getyPos() - regalboden1AnRegalwand1.getyPos() - regalboden1AnRegalwand1.getHoehe(),
                    "Die Hoehe des sechsten Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich6.getPosY(),
                    regalboden1AnRegalwand1.getyPos() + regalboden1AnRegalwand1.getHoehe(),
                    "Die y-Position des sechsten Ablagebereichs sollte korrekt sein");
            assertEquals(ablagebereich6.getPosX(), regalboden1AnRegalwand1.getxPos(),
                    "Die x-Position des sechsten Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich6.getUntererRegalboden(), regalboden2AnRegalwand1, "Der Regalboden des sechsten" +
                    "Ablagebereichs sollte korrekt sein.");

            //Siebter Ablagebereich
            assertEquals(ablagebereich7.getBreite(), regalboden2AnRegalwand1.getBreite(), "Der siebte Ablagebereich " +
                    "sollte die korrekte Breite haben.");
            assertEquals(ablagebereich7.getHoehe(),
                    regalboden3AnRegalwand1.getyPos() - regalboden2AnRegalwand1.getyPos() - regalboden2AnRegalwand1.getHoehe(),
                    "Die Hoehe des siebten Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich7.getPosY(),
                    regalboden2AnRegalwand1.getyPos() + regalboden2AnRegalwand1.getHoehe(),
                    "Die y-Position des siebten Ablagebereichs sollte korrekt sein");
            assertEquals(ablagebereich7.getPosX(), regalboden2AnRegalwand1.getxPos(),
                    "Die x-Position des siebten Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich7.getUntererRegalboden(), regalboden3AnRegalwand1, "Der Regalboden des siebten" +
                    "Ablagebereichs sollte korrekt sein.");


            //Achter Ablagebereich
            assertEquals(ablagebereich8.getBreite(), regalboden3AnRegalwand1.getBreite(), "Der achte Ablagebereich " +
                    "sollte die korrekte Breite haben.");
            assertEquals(ablagebereich8.getHoehe(),
                    regal.getRegalboden().getyPos() - regalboden3AnRegalwand1.getyPos() - regalboden3AnRegalwand1.getHoehe(),
                    "Die Hoehe des achten Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich8.getPosY(),
                    regalboden3AnRegalwand1.getyPos() + regalboden3AnRegalwand1.getHoehe(),
                    "Die y-Position des achten Ablagebereichs sollte korrekt sein");
            assertEquals(ablagebereich8.getPosX(), regalboden3AnRegalwand1.getxPos(),
                    "Die x-Position des achten Ablagebereichs sollte korrekt sein.");
            assertEquals(ablagebereich8.getUntererRegalboden(), regal.getRegalboden(), "Der Regalboden des achten" +
                    "Ablagebereichs sollte korrekt sein.");
        } catch (RegalkomponenteNichtPlatzierbarException e) {
            throw new RuntimeException(e);
        }
    }
}
