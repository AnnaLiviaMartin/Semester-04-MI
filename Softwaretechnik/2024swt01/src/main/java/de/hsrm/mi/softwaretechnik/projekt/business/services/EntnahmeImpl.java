package de.hsrm.mi.softwaretechnik.projekt.business.services;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Warenkorb;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutatenpaket;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ListeIstLeerException;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ObjektExistiertNichtException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class EntnahmeImpl implements Entnahme {
    private final Warenkorb warenkorb;
    private final ZutatenpaketverwaltungImpl zutatenpaketverwaltung;
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    /**
     * Constructor
     *
     * @param zutatenpaketverwaltung Zutatenpaketverwaltung
     */
    public EntnahmeImpl(ZutatenpaketverwaltungImpl zutatenpaketverwaltung) {
        this.zutatenpaketverwaltung = zutatenpaketverwaltung;
        this.warenkorb = new Warenkorb();
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
     * Fügt ein Zutatenpaket aus dem Ablagebereich dem Warenkorb hinzu
     *
     * @param zutatenpaket Zutatenpaket
     */
    @Override
    public void addZutatenpaketInWarenkorb(Zutatenpaket zutatenpaket) throws ObjektExistiertNichtException {
        if (zutatenpaket == null) {
            throw new ObjektExistiertNichtException("Zutatenpaket nicht initialisiert!");
        }

        zutatenpaket.setImWarenkorb(true);
        this.warenkorb.addZutatenpaket(zutatenpaket);
    }

    /**
     * Möglichkeit zur Suche des gewünschten Zutatenpakets im Lager, funktioniert als Filter
     *
     * @param suchtext gesuchte Zutat
     *
     * @return Liste der Zutatenpakete, die die gewünschte Zutat enthalten
     */
    @Override
    public List<Zutatenpaket> sucheZutatenPaket(String suchtext) {
        List<Zutatenpaket> gesuchteZutatenpakete = new ArrayList<>();
        String textTeil = (suchtext).toLowerCase();

        for (Zutatenpaket gesuchteZutat : this.zeigeAlleZutatenPaketeImLager()) {
            String zutatenName = gesuchteZutat.getZutat().getName().toLowerCase();

            if (zutatenName.contains(textTeil)) {
                gesuchteZutatenpakete.add(gesuchteZutat);
            }
        }
        return gesuchteZutatenpakete;
    }

    /**
     * Anzeige der bereits enthaltenen Zutatenpakete im Warenkorb
     *
     * @return Zutatenpakete im Warenkorb
     */
    @Override
    public List<Zutatenpaket> zeigeAlleZutatenpaketeImWarenkorb() {
        return this.warenkorb.getZutatenpakete();
    }

    /**
     * Anzeige der im Lager befindlichen Zutatenpakete
     *
     * @return Zutatenpakete im Regal (Lager)
     */
    @Override
    public List<Zutatenpaket> zeigeAlleZutatenPaketeImLager() {
        return this.zutatenpaketverwaltung.getZutatenpaketListe();
    }

    /**
     * Abschließen des Entnahmeprozesses und dessen Speicherung
     */
    @Override
    public void entnahmeAbschliessen() throws ObjektExistiertNichtException {
        // über jedes einzelne Zutatenpaket im Warenkorb iterieren
        for (Zutatenpaket zutatenpaketToDelete : this.warenkorb.getZutatenpakete()) {
            this.zutatenpaketverwaltung.loescheZutatenpaketMitId(zutatenpaketToDelete.getUuid());
        }
        // dann Warenkorb leeren
        this.warenkorb.deleteAlleZutatenpakete();
    }

    /**
     * Entfernt ein einzelnes Zutatenpaket aus dem Warenkorb
     *
     * @param zutatenpaket Zutatenpaket
     */
    @Override
    public void zutatMitIdAusWarenkorbEntfernen(Zutatenpaket zutatenpaket) throws ObjektExistiertNichtException, ListeIstLeerException {
        this.warenkorb.deleteZutatenpaketMitId(zutatenpaket.getUuid());
    }

    /**
     * Getter für den Warenkorb
     *
     * @return Warenkorb als Objekt
     */
    @Override
    public Warenkorb getWarenkorb() {
        return warenkorb;
    }
}
