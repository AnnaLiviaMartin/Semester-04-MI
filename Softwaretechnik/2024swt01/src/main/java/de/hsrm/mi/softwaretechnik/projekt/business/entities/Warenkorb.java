package de.hsrm.mi.softwaretechnik.projekt.business.entities;

import java.util.ArrayList;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ListeIstLeerException;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ObjektExistiertNichtException;

import java.util.List;
import java.util.UUID;

public class Warenkorb {
    private List<Zutatenpaket> zutatenpakete;

    public Warenkorb(){
        this.zutatenpakete = new ArrayList<>();
    }

    public List<Zutatenpaket> getZutatenpakete() {
        return this.zutatenpakete;
    }

    public void addZutatenpaket(Zutatenpaket zutatenpaket) throws ObjektExistiertNichtException {
        if (zutatenpaket == null) {
            throw new ObjektExistiertNichtException("Zutatenpaket nicht initialisiert!");
        }

        this.zutatenpakete.add(zutatenpaket);
    }

    /**
     * Einzelnes Zutatenpaket mit id aus dem Warenkorb entfernen
     *
     * @param id id
     */
    public void deleteZutatenpaketMitId(UUID id) throws ObjektExistiertNichtException, ListeIstLeerException {
        if (this.zutatenpakete == null) {
            throw new ObjektExistiertNichtException("Keine Zutatenpaketliste initialisiert!");
        }

        if (this.zutatenpakete.isEmpty()) {
            throw new ListeIstLeerException("Liste der Zutatenpakete ist leer!");
        }

        for (Zutatenpaket zutatenpaket : this.zutatenpakete) {
            if (zutatenpaket.getUuid().equals(id)) {
                this.zutatenpakete.remove(zutatenpaket);
            } else throw new ObjektExistiertNichtException("Zutatenpaket mit ID " + id + " wurde nicht initialisiert!");
        }
    }

    /**
     * Entfernt alle Zutatenpakete aus dem Warenkorb
     */
    public void deleteAlleZutatenpakete() throws ObjektExistiertNichtException {
        if (this.zutatenpakete == null) {
            throw new ObjektExistiertNichtException("Keine Zutatenpaketliste initialisiert!");
        }
        for (Zutatenpaket zutatenpaket : this.zutatenpakete) {
            zutatenpaket.setImWarenkorb(false);
        }
        this.zutatenpakete.clear();
    }
}