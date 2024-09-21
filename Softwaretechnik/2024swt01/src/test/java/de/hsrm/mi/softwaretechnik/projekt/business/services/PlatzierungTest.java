package de.hsrm.mi.softwaretechnik.projekt.business.services;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.*;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ObjektExistiertNichtException;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.RegalkomponenteNichtPlatzierbarException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

public class PlatzierungTest {
    private final double WINDOW_WIDTH = 1400;
    private final double WINDOW_HEIGHT = 900;
    private final double REGAL_BODEN_HOEHE = 20;
    private final double REGAL_WAND_BREITE = 20;
    private ZutatenpaketverwaltungImpl zutatenpaketverwaltung;
    private LagereditorImpl lagereditor;
    private Regal regal;
    private Zutatenpaketplatzierung zutatenpaketplatzierung;

    @BeforeEach
    void setUp() throws RegalkomponenteNichtPlatzierbarException {
        lagereditor = new LagereditorImpl(WINDOW_WIDTH, WINDOW_HEIGHT, REGAL_WAND_BREITE, REGAL_BODEN_HOEHE);
        regal = lagereditor.erhalteRegal();
        zutatenpaketverwaltung = new ZutatenpaketverwaltungImpl(regal);
        zutatenpaketplatzierung = new ZutatenpaketplatzierungImpl(zutatenpaketverwaltung);
    }

    @Test
    public void testZutatenpaketBreiterAlsAblageflaeche() {
        List<Zutatenpaket> zpe = new LinkedList<>();
        Regalboden regalboden = new Regalboden(50);
        List<Ablagebereich> ablagebereiche = new LinkedList<>();
        Ablagebereich ablagebereich = new Ablagebereich(50, 50, regalboden);
        ablagebereiche.add(ablagebereich);
        List<Zutat> unvertraeglichkeit = new LinkedList<>();
        unvertraeglichkeit.add(new Zutat("Salami"));

        Zutatenpaket untenLinks = new Zutatenpaket(new Paket(5, 20, 7), new Zutat("Salami"));
        untenLinks.getPaket().setAbstandVonLinks(0);
        untenLinks.getZutat().setUnvertraeglichkeit(unvertraeglichkeit);
        untenLinks.setAblagebereich(ablagebereiche.get(0));
        untenLinks.setAblageflaeche(regalboden);
        untenLinks.setPositionen(untenLinks.getAbstandVonLinks(), 80);
        zpe.add(untenLinks);
        zpe.add(untenLinks);
        Zutatenpaket untenMitte = new Zutatenpaket(new Paket(4, 10, 5), new Zutat("Salami"));
        untenMitte.getPaket().setAbstandVonLinks(11);
        untenMitte.getZutat().setUnvertraeglichkeit(unvertraeglichkeit);
        untenMitte.setAblagebereich(ablagebereiche.get(0));
        untenMitte.setAblageflaeche(regalboden);
        untenMitte.setPositionen(untenMitte.getAbstandVonLinks(), 80);
        zpe.add(untenMitte);

        assertEquals(true, this.zutatenpaketplatzierung.pruefePlatzierung(untenMitte, ablagebereich, untenLinks, 0, 0));
    }


    @Test
    public void testZutatenpaketNichtBreiterAlsAblageflaeche() {
        List<Zutatenpaket> zpe = new LinkedList<>();
        Regalboden regalboden = new Regalboden(50);
        List<Ablagebereich> ablagebereiche = new LinkedList<>();
        Ablagebereich ablagebereich = new Ablagebereich(50, 50, regalboden);
        ablagebereiche.add(ablagebereich);
        List<Zutat> unvertraeglichkeit = new LinkedList<>();
        unvertraeglichkeit.add(new Zutat("Salami"));

        Zutatenpaket untenLinks = new Zutatenpaket(new Paket(5, 20, 7), new Zutat("Salami"));
        untenLinks.getPaket().setAbstandVonLinks(0);
        untenLinks.getZutat().setUnvertraeglichkeit(unvertraeglichkeit);
        untenLinks.setAblagebereich(ablagebereiche.get(0));
        untenLinks.setAblageflaeche(regalboden);
        untenLinks.setPositionen(untenLinks.getAbstandVonLinks(), 80);
        zpe.add(untenLinks);
        zpe.add(untenLinks);
        Zutatenpaket untenMitte = new Zutatenpaket(new Paket(5, 10, 7), new Zutat("Salami"));
        untenMitte.getPaket().setAbstandVonLinks(11);
        untenMitte.getZutat().setUnvertraeglichkeit(unvertraeglichkeit);
        untenMitte.setAblagebereich(ablagebereiche.get(0));
        untenMitte.setAblageflaeche(regalboden);
        untenMitte.setPositionen(untenMitte.getAbstandVonLinks(), 80);
        zpe.add(untenMitte);

        Zutatenpaket test = new Zutatenpaket(new Paket(7, 10, 7), new Zutat("Salami"));
        test.getPaket().setAbstandVonLinks(11);
        test.getZutat().setUnvertraeglichkeit(unvertraeglichkeit);
        test.setAblagebereich(ablagebereiche.get(0));
        test.setAblageflaeche(untenMitte);
        test.setPositionen(test.getAbstandVonLinks(), 80);
        zpe.add(test);

        assertEquals(true, this.zutatenpaketplatzierung.pruefePlatzierung(test, ablagebereich, untenMitte, 0, 0));
    }

    @Test
    public void testZutatenpaketHoeherAlsAblageflaeche() {
        List<Zutatenpaket> zpe = new LinkedList<>();
        Regalboden regalboden = new Regalboden(50);
        List<Ablagebereich> ablagebereiche = new LinkedList<>();
        Ablagebereich ablagebereich = new Ablagebereich(50, 50, regalboden);
        ablagebereiche.add(ablagebereich);
        List<Zutat> unvertraeglichkeit = new LinkedList<>();
        unvertraeglichkeit.add(new Zutat("Salami"));

        Zutatenpaket untenLinks = new Zutatenpaket(new Paket(5, 20, 7), new Zutat("Salami"));
        untenLinks.getPaket().setAbstandVonLinks(0);
        untenLinks.getZutat().setUnvertraeglichkeit(unvertraeglichkeit);
        untenLinks.setAblagebereich(ablagebereiche.get(0));
        untenLinks.setAblageflaeche(regalboden);
        untenLinks.setPositionen(untenLinks.getAbstandVonLinks(), 80);
        zpe.add(untenLinks);
        zpe.add(untenLinks);
        Zutatenpaket untenMitte = new Zutatenpaket(new Paket(5, 10, 7), new Zutat("Salami"));
        untenMitte.getPaket().setAbstandVonLinks(11);
        untenMitte.getZutat().setUnvertraeglichkeit(unvertraeglichkeit);
        untenMitte.setAblagebereich(ablagebereiche.get(0));
        untenMitte.setAblageflaeche(regalboden);
        untenMitte.setPositionen(untenMitte.getAbstandVonLinks(), 80);
        zpe.add(untenMitte);

        Zutatenpaket test = new Zutatenpaket(new Paket(2, 320, 7), new Zutat("Salami"));
        test.getPaket().setAbstandVonLinks(11);
        test.getZutat().setUnvertraeglichkeit(unvertraeglichkeit);
        test.setAblagebereich(ablagebereiche.get(0));
        test.setAblageflaeche(untenMitte);
        test.setPositionen(test.getAbstandVonLinks(), 80);
        zpe.add(test);

        assertEquals(false, this.zutatenpaketplatzierung.pruefePlatzierung(test, ablagebereich, untenMitte, 0, 0));
    }

    @Test
    public void testZutatenpaketHatUnvertraeglichkeitMitAblagebereich() throws ObjektExistiertNichtException {
        List<Zutatenpaket> zpe = new LinkedList<>();
        Regalboden regalboden = new Regalboden(50);
        List<Ablagebereich> ablagebereiche = new LinkedList<>();
        Ablagebereich ablagebereich = new Ablagebereich(50, 50, regalboden);
        ablagebereiche.add(ablagebereich);
        List<Zutat> unvertraeglichkeit = new LinkedList<>();
        Zutat zutat = new Zutat("Käse");
        Zutat zutat2 = new Zutat("Salami");
        unvertraeglichkeit.add(zutat);

        Zutatenpaket untenLinks = new Zutatenpaket(new Paket(5, 20, 7), zutat);
        untenLinks.getPaket().setAbstandVonLinks(0);
        untenLinks.getZutat().setUnvertraeglichkeit(unvertraeglichkeit);
        untenLinks.setAblagebereich(ablagebereiche.get(0));
        untenLinks.setAblageflaeche(regalboden);
        untenLinks.setPositionen(untenLinks.getAbstandVonLinks(), 80);
        zpe.add(untenLinks);
        ablagebereich.addZutatenpaket(untenLinks);

        Zutatenpaket test = new Zutatenpaket(new Paket(2, 2, 7), zutat2);
        test.getPaket().setAbstandVonLinks(0);
        test.getZutat().setUnvertraeglichkeit(unvertraeglichkeit);
        test.setAblagebereich(ablagebereiche.get(0));
        test.setAblageflaeche(untenLinks);
        test.setPositionen(test.getAbstandVonLinks(), 80);
        zpe.add(test);

        assertEquals(false, this.zutatenpaketplatzierung.pruefePlatzierung(test, ablagebereich, untenLinks, 0, 0));
    }

    @Test
    public void testAblageflaecheTragfaehigGenug() throws ObjektExistiertNichtException {
        List<Zutatenpaket> zpe = new LinkedList<>();
        Regalboden regalboden = new Regalboden(50);
        List<Ablagebereich> ablagebereiche = new LinkedList<>();
        Ablagebereich ablagebereich = new Ablagebereich(50, 50, regalboden);
        ablagebereiche.add(ablagebereich);
        List<Zutat> unvertraeglichkeit = new LinkedList<>();
        Zutat zutat = new Zutat("Käse");
        Zutat zutat2 = new Zutat("Salami");
        unvertraeglichkeit.add(zutat);

        Zutatenpaket untenLinks = new Zutatenpaket(new Paket(5, 20, 5), zutat);
        untenLinks.getPaket().setAbstandVonLinks(0);
        untenLinks.setAblagebereich(ablagebereiche.get(0));
        untenLinks.setAblageflaeche(regalboden);
        untenLinks.setPositionen(untenLinks.getAbstandVonLinks(), 80);
        zpe.add(untenLinks);
        ablagebereich.addZutatenpaket(untenLinks);

        Zutatenpaket test = new Zutatenpaket(new Paket(2, 2, 7), zutat2);
        test.getPaket().setAbstandVonLinks(0);
        test.setAblagebereich(ablagebereiche.get(0));
        test.setAblageflaeche(untenLinks);
        test.setPositionen(test.getAbstandVonLinks(), 80);
        zpe.add(test);

        assertEquals(false, this.zutatenpaketplatzierung.pruefePlatzierung(test, ablagebereich, untenLinks, 0, 0));
    }

    @Test
    public void testPaketstapelHoeherAlsAblagebereich() throws ObjektExistiertNichtException {
        List<Zutatenpaket> zpe = new LinkedList<>();
        Regalboden regalboden = new Regalboden(50);
        List<Ablagebereich> ablagebereiche = new LinkedList<>();
        Ablagebereich ablagebereich = new Ablagebereich(10, 50, regalboden);
        ablagebereiche.add(ablagebereich);
        Zutat zutat = new Zutat("Käse");
        Zutat zutat2 = new Zutat("Salami");

        Zutatenpaket untenLinks = new Zutatenpaket(new Paket(5, 8, 5), zutat);
        untenLinks.getPaket().setAbstandVonLinks(0);
        untenLinks.setAblagebereich(ablagebereiche.get(0));
        untenLinks.setAblageflaeche(regalboden);
        untenLinks.setPositionen(untenLinks.getAbstandVonLinks(), 80);
        zpe.add(untenLinks);
        ablagebereich.addZutatenpaket(untenLinks);

        Zutatenpaket test = new Zutatenpaket(new Paket(2, 3, 2), zutat2);
        test.getPaket().setAbstandVonLinks(0);
        test.setAblagebereich(ablagebereiche.get(0));
        test.setAblageflaeche(untenLinks);
        test.setPositionen(test.getAbstandVonLinks(), 80);
        zpe.add(test);

        assertEquals(false, this.zutatenpaketplatzierung.pruefePlatzierung(test, ablagebereich, untenLinks, 0, 0));
    }

}