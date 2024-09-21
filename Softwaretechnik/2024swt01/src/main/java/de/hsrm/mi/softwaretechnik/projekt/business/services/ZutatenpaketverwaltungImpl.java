package de.hsrm.mi.softwaretechnik.projekt.business.services;

import de.hsrm.mi.softwaretechnik.projekt.business.Utilities;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Paket;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Regal;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutat;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutatenpaket;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Verwaltet Zutaten, Pakete und Zutatenpakete. Legt vordefinierte Zutaten und Pakete fest.
 */
public class ZutatenpaketverwaltungImpl implements Zutatenpaketverwaltung {
    private List<Zutat> zutatenListe;
    private List<Zutatenpaket> zutatenpaketListe;
    private List<Paket> pakete;
    private Regal regal;
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private Zutat selectedZutatForZutatenpaketCreation = null;
    private Paket selectedPaketForZutatenpaketCreation = null;

    public ZutatenpaketverwaltungImpl(Regal regal) {
        this.zutatenpaketListe = new ArrayList<>();
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
     * Sucht nach den Zutaten anhand des suchtextes.
     *
     * @param suchtext der Zutat
     * @return Liste der gefilterten Zutaten
     */
    @Override
    public List<Zutat> sucheZutat(String suchtext) {
        if (zutatenListe == null || suchtext == null) {
            return List.of();
        }
        return zutatenListe.stream()
                .filter(zutat -> zutat.getName() != null && zutat.getName().contains(suchtext))
                .collect(Collectors.toList());
    }

    /**
     * Prüft, ob eine Zutat bereits vorhanden ist. Falls nicht wird diese Zutat erstellt und der Zutatenliste
     * hinzugefügt.
     *
     * @param name Name der Zutat
     * @return erstellte Zutat
     */
    @Override
    public Zutat erstelleNeueZutat(String name) {
        for (Zutat zutat : zutatenListe) {
            if (zutat.getName().equalsIgnoreCase(name)) {
                return null;
            }
        }

        Zutat zutat = new Zutat(name);
        zutat.setUnvertraeglichkeit(new ArrayList<>());

        zutatenListe.add(zutat);
        return zutat;
    }

    /**
     * Erstellt ein Zutatenpaket.
     *
     * @param zutat als Inhalt des Zutatenpakets
     * @param paket als Box des Zutatenpakets
     * @return ein Zutatenpaket
     */
    @Override
    public Zutatenpaket erstelleZutatenpaket(Zutat zutat, Paket paket) {
        Zutatenpaket neuesZutatenpaket = new Zutatenpaket(paket, zutat);
        //Only add zutatenpaket, wenn nicht drin
        if(!checkIfZutatenpaketInListe(neuesZutatenpaket)){
            zutatenpaketListe.add(neuesZutatenpaket);
        }

        return neuesZutatenpaket;
    }

    private boolean checkIfZutatenpaketInListe(Zutatenpaket zutatenpaket) {
        for (Zutatenpaket zutatenpaketToCheck : zutatenpaketListe) {
            if (zutatenpaketToCheck.getUuid().equals(zutatenpaket.getUuid())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Getter für die Paketliste
     *
     * @return Paketliste
     */
    @Override
    public List<Paket> getPaketListe() {
        return pakete;
    }

    /**
     * Getter für die Zutatenliste
     *
     * @return Zutatenliste
     */
    @Override
    public List<Zutat> getZutatenliste() {
        return zutatenListe;
    }

    /**
     * Getter für die Zutatenpaketliste
     *
     * @return Liste der Zutatenpakete
     */
    @Override
    public List<Zutatenpaket> getZutatenpaketListe() {
        return zutatenpaketListe;
    }

    /**
     * Entfernt das gesuchte Zutatenpaket aus der Zutatenpaketliste
     *
     * @param id id
     */
    @Override
    public void loescheZutatenpaketMitId(UUID id) {
        this.zutatenpaketListe.removeIf(zutatenpaket -> zutatenpaket.getUuid().equals(id));
    }

    /**
     * Initialisiert vordefinierte Pakete
     */
    public void addVordefiniertePakete(List<Paket> pakete) {
        this.pakete = pakete;
        this.selectedPaketForZutatenpaketCreation = pakete.getFirst();
        //todo set style for selected paket (umrandung oder so)
    }

    /**
     * Initialisiert vordefinierte Zutaten
     */
    public void addVordefinierteZutaten(List<Zutat> zutatenListe) {
        this.zutatenListe = zutatenListe;
        this.selectedZutatForZutatenpaketCreation = zutatenListe.getFirst();
        //todo set style for selected zutat (umrandung oder so)
    }

    public Regal getRegal() {
        return regal;
    }

    public void setRegal(Regal regal) {
        this.regal = regal;
    }

    public void speichereRegal() {
        Utilities.saveRegalZutatenPakete(this.regal);
    }

    public void ladeRegal() {
        Regal oldRegalCopy = this.regal;
        //this.regal = Utilities.loadRegal();
        //todo comment in again
        // fire new regal
        changeSupport.firePropertyChange("regal", oldRegalCopy, this.regal);
    }

    public void setSelectedPaketForZutatenpaketCreationBySize(double breite, double hoehe) {
        for (Paket paket : this.pakete) {
            if (paket.getBreite() == breite && paket.getHoehe() == hoehe) {
                this.selectedPaketForZutatenpaketCreation = paket;
                break;
            }
        }
    }

    public void setSelectedZutatForZutatenpaketCreationByName(String name) {
        for (Zutat zutat : this.zutatenListe) {
            if (zutat.getName().equals(name)) {
                this.selectedZutatForZutatenpaketCreation = zutat;
                break;
            }
        }
    }

    public Zutat getSelectedZutatForZutatenpaketCreation() {
        return selectedZutatForZutatenpaketCreation;
    }

    public Paket getSelectedPaketForZutatenpaketCreation() {
        return selectedPaketForZutatenpaketCreation;
    }

}
