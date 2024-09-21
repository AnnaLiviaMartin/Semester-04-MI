package de.hsrm.mi.softwaretechnik.projekt.business.services;

import de.hsrm.mi.softwaretechnik.projekt.business.Utilities;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Regal;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.RegalkomponenteNichtPlatzierbarException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UtilitiesTest {
    private final double WINDOW_WIDTH = 1400;
    private final double WINDOW_HEIGHT = 900;
    private final double REGAL_BODEN_HOEHE = 20;
    private final double REGAL_WAND_BREITE = 20;

    private LagereditorImpl lagereditor;
    private Regal regal;
    private  Utilities utils;
    private ZutatenpaketverwaltungImpl zutatenpaketverwaltung;

    @BeforeEach
    void setUp() {
        try {
            lagereditor = new LagereditorImpl(WINDOW_WIDTH, WINDOW_HEIGHT, REGAL_WAND_BREITE, REGAL_BODEN_HOEHE);
        } catch (RegalkomponenteNichtPlatzierbarException e) {
            throw new RuntimeException(e);
        }
        regal = lagereditor.erhalteRegal();

        zutatenpaketverwaltung = new ZutatenpaketverwaltungImpl(regal);
        utils = new Utilities(zutatenpaketverwaltung);
    }

    @Test
    void testSaveRegal(){
     //   Utilities.speichereRegalAufbauMitInhalt(regal);


    }




}
