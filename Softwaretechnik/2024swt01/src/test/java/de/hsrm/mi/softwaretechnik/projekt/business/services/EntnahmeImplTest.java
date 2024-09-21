package de.hsrm.mi.softwaretechnik.projekt.business.services;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Paket;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutat;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutatenpaket;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ListeIstLeerException;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ObjektExistiertNichtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

public class EntnahmeImplTest {

    private ZutatenpaketverwaltungImpl zutatenpaketverwaltung;
    private EntnahmeImpl entnahme;

    @BeforeEach
    public void setUp() {
        zutatenpaketverwaltung = mock(ZutatenpaketverwaltungImpl.class);
        entnahme = new EntnahmeImpl(zutatenpaketverwaltung);
    }

    @Test
    public void testAddZutatenpaketInWarenkorb() throws ObjektExistiertNichtException {
        Zutatenpaket zutatenpaket = new Zutatenpaket(new Paket(4, 2, 2), new Zutat("Zitrone"));

        entnahme.addZutatenpaketInWarenkorb(zutatenpaket);
        List<Zutatenpaket> warenkorbInhalt = entnahme.zeigeAlleZutatenpaketeImWarenkorb();

        assertTrue(warenkorbInhalt.contains(zutatenpaket));
        assertTrue(zutatenpaket.isImWarenkorb());
    }

    @Test
    public void testSucheZutatenPaket() {
        Zutatenpaket zutatenpaket1 = new Zutatenpaket(new Paket(4, 2, 2), new Zutat("Zitrone"));
        Zutatenpaket zutatenpaket2 = new Zutatenpaket(new Paket(8, 8, 10), new Zutat("Melone"));

        List<Zutatenpaket> allePakete = new ArrayList<>();
        allePakete.add(zutatenpaket1);
        allePakete.add(zutatenpaket2);

        when(zutatenpaketverwaltung.getZutatenpaketListe()).thenReturn(allePakete);

        List<Zutatenpaket> gesuchtePakete = entnahme.sucheZutatenPaket("Zit");

        assertEquals(1, gesuchtePakete.size());
        assertTrue(gesuchtePakete.contains(zutatenpaket1));
    }

    @Test
    public void testZeigeAlleZutatenpaketeImWarenkorb() throws ObjektExistiertNichtException {
        Zutatenpaket zutatenpaket1 = new Zutatenpaket(new Paket(4, 2, 2), new Zutat("Zitrone"));
        Zutatenpaket zutatenpaket2 = new Zutatenpaket(new Paket(8, 8, 10), new Zutat("Melone"));

        entnahme.addZutatenpaketInWarenkorb(zutatenpaket1);
        entnahme.addZutatenpaketInWarenkorb(zutatenpaket2);

        List<Zutatenpaket> warenkorbInhalt = entnahme.zeigeAlleZutatenpaketeImWarenkorb();

        assertEquals(2, warenkorbInhalt.size());
        assertTrue(warenkorbInhalt.contains(zutatenpaket1));
        assertTrue(warenkorbInhalt.contains(zutatenpaket2));
    }

    @Test
    public void testZeigeAlleZutatenPaketeImLager() {
        Zutatenpaket zutatenpaket1 = new Zutatenpaket(new Paket(4, 2, 2), new Zutat("Zitrone"));
        Zutatenpaket zutatenpaket2 = new Zutatenpaket(new Paket(8, 8, 10), new Zutat("Melone"));

        List<Zutatenpaket> allePakete = new ArrayList<>();
        allePakete.add(zutatenpaket1);
        allePakete.add(zutatenpaket2);

        when(zutatenpaketverwaltung.getZutatenpaketListe()).thenReturn(allePakete);

        List<Zutatenpaket> lagerInhalt = entnahme.zeigeAlleZutatenPaketeImLager();

        assertEquals(2, lagerInhalt.size());
        assertTrue(lagerInhalt.contains(zutatenpaket1));
        assertTrue(lagerInhalt.contains(zutatenpaket2));
    }

    @Test
    public void testEntnahmeAbschliessen() throws ObjektExistiertNichtException {
        Zutatenpaket zutatenpaket1 = new Zutatenpaket(new Paket(4, 2, 2), new Zutat("Zitrone"));
        Zutatenpaket zutatenpaket2 = new Zutatenpaket(new Paket(8, 8, 10), new Zutat("Melone"));

        entnahme.addZutatenpaketInWarenkorb(zutatenpaket1);
        entnahme.addZutatenpaketInWarenkorb(zutatenpaket2);

        entnahme.entnahmeAbschliessen();

        verify(zutatenpaketverwaltung).loescheZutatenpaketMitId(zutatenpaket1.getUuid());
        verify(zutatenpaketverwaltung).loescheZutatenpaketMitId(zutatenpaket2.getUuid());
        verify(zutatenpaketverwaltung).speichereRegal();

        List<Zutatenpaket> warenkorbInhalt = entnahme.zeigeAlleZutatenpaketeImWarenkorb();
        assertTrue(warenkorbInhalt.isEmpty());
    }

    @Test
    public void testZutatMitIdAusWarenkorbEntfernen() throws ObjektExistiertNichtException, ListeIstLeerException {
        Zutatenpaket zutatenpaket1 = new Zutatenpaket(new Paket(4, 2, 2), new Zutat("Zitrone"));
        Zutatenpaket zutatenpaket2 = new Zutatenpaket(new Paket(8, 8, 10), new Zutat("Melone"));

        entnahme.addZutatenpaketInWarenkorb(zutatenpaket1);
        entnahme.addZutatenpaketInWarenkorb(zutatenpaket2);

        entnahme.zutatMitIdAusWarenkorbEntfernen(zutatenpaket1);

        List<Zutatenpaket> warenkorbInhalt = entnahme.zeigeAlleZutatenpaketeImWarenkorb();
        assertFalse(warenkorbInhalt.contains(zutatenpaket1));
        assertTrue(warenkorbInhalt.contains(zutatenpaket2));
    }
}