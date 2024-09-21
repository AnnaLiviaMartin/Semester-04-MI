package de.hsrm.mi.softwaretechnik.projekt.presentation.platzieren;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.*;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ObjektExistiertNichtException;
import de.hsrm.mi.softwaretechnik.projekt.business.services.*;
import de.hsrm.mi.softwaretechnik.projekt.presentation.ViewBuilder;
import de.hsrm.mi.softwaretechnik.projekt.presentation.dragAndDrop.DragAndDrop;
import de.hsrm.mi.softwaretechnik.projekt.presentation.mainView.MainViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlatzierenController {
    private Zutatenpaketplatzierung zutatenpaketPlatzierung;
    private ZutatenpaketverwaltungImpl zutatenpaketverwaltung;
    private final Pane platzierenView;
    private final Pane neueZutatView;
    private final int COLUMNS_ZUTAT = 2; // Anzahl der Spalten
    private final int COLUMNS_PAKET = 2; // Anzahl der Spalten
    @FXML
    private GridPane zutatenBox;
    @FXML
    private GridPane paketBox;
    @FXML
    private BorderPane zutatenpaketBox;

    private DragAndDrop dragAndDrop;
    private final MainViewController mainViewController;

    @FXML
    private Button zutatHinzufuegenButton;

    public PlatzierenController(ZutatenpaketverwaltungImpl zutatenpaketverwaltung, MainViewController mainViewController) {
        this.mainViewController = mainViewController;
        this.zutatenpaketverwaltung = zutatenpaketverwaltung;
        zutatenpaketPlatzierung = new ZutatenpaketplatzierungImpl(zutatenpaketverwaltung);
        this.neueZutatView = mainViewController.getNeueZutatView();
        FXMLLoader platzierenViewLoader = new FXMLLoader(getClass().getResource("/platzieren/PlatzierenView.fxml"));

        try {
            platzierenViewLoader.setController(this);
            platzierenView = platzierenViewLoader.load();
            addVordefinierteZutaten();
            addVordefiniertePakete();
            initZutaten();
            initPakete();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addVordefinierteZutaten() {
        List<Zutat> zutatenListe = new ArrayList<>();
        Zutat tomate = new Zutat("Tomate");
        zutatenListe.add(tomate);

        Zutat zwiebel = new Zutat("Zwiebel");
        zutatenListe.add(zwiebel);

        Zutat kaese = new Zutat("Käse");
        zutatenListe.add(kaese);

        Zutat tomatensauce = new Zutat("Tomatensauce");
        zutatenListe.add(tomatensauce);

        Zutat pilz = new Zutat("Pilz");
        zutatenListe.add(pilz);

        Zutat salami = new Zutat("Salami");
        zutatenListe.add(salami);

        Zutat schinken = new Zutat("Schinken");
        zutatenListe.add(schinken);

        Zutat pizzakarton = new Zutat("Pizzakarton");
        zutatenListe.add(pizzakarton);

        try {
            tomate.addUnvertraeglichkeit(zwiebel);
            kaese.addUnvertraeglichkeit(zwiebel);
            tomatensauce.addUnvertraeglichkeit(zwiebel);
            pilz.addUnvertraeglichkeit(zwiebel);
            salami.addUnvertraeglichkeit(zwiebel);
            schinken.addUnvertraeglichkeit(zwiebel);
            pizzakarton.addUnvertraeglichkeit(tomate);
            pizzakarton.addUnvertraeglichkeit(kaese);
            pizzakarton.addUnvertraeglichkeit(zwiebel);
            pizzakarton.addUnvertraeglichkeit(tomatensauce);
            pizzakarton.addUnvertraeglichkeit(pilz);
            pizzakarton.addUnvertraeglichkeit(salami);
            pizzakarton.addUnvertraeglichkeit(schinken);
        } catch (ObjektExistiertNichtException e) {
            throw new RuntimeException(e);
        }

        this.zutatenpaketverwaltung.addVordefinierteZutaten(zutatenListe);
    }

    private void addVordefiniertePakete() {
        List<Paket> pakete = new ArrayList<>();
        pakete.add(new Paket(30, 30, 3));
        pakete.add(new Paket(60, 60, 6));
        pakete.add(new Paket(90, 90, 9));
        pakete.add(new Paket(120, 120, 12));
        pakete.add(new Paket(70, 70, 7));
        pakete.add(new Paket(100, 100, 10));
        this.zutatenpaketverwaltung.addVordefiniertePakete(pakete);
    }

    @FXML
    public Pane initialize() {

        return platzierenView;
    }

    public Pane getPlatzierenView() {
        return platzierenView;
    }

    //TODO: unbedingt wider private und alternative für Aufruf
    public void initZutaten() {
        zutatenBox.setVgap(20);
        zutatenBox.setHgap(20);

        for (Zutat zutat : this.zutatenpaketverwaltung.getZutatenliste()) {
            ZutatViewController zutatViewController = ViewBuilder.createZutatView(zutat, this.zutatenpaketverwaltung);
            //setzt Pane
            addZutatToGridPane(zutatViewController.getZutatPane());
        }
    }

    private void addZutatToGridPane(StackPane zutatPane) {
        int childrenCount = zutatenBox.getChildren().size();
        int row = childrenCount / COLUMNS_ZUTAT;
        int column = childrenCount % COLUMNS_ZUTAT;

        zutatenBox.add(zutatPane, column, row);
    }

    private void initPakete() {
        paketBox.setVgap(20);
        paketBox.setHgap(20);

        for (Paket paket : this.zutatenpaketverwaltung.getPaketListe()) {
            PaketViewController paketViewController = ViewBuilder.createPaketView(paket, this.zutatenpaketverwaltung);
            //setzt Pane
            addPaketToGridPane(paketViewController.getPaketPane());
        }
    }

    private void addPaketToGridPane(StackPane paketPane) {
        int childrenCount = paketBox.getChildren().size();
        int row = childrenCount / COLUMNS_PAKET;
        int column = childrenCount % COLUMNS_PAKET;

        paketBox.add(paketPane, column, row);
        GridPane.setHalignment(paketPane, HPos.CENTER);
        GridPane.setValignment(paketPane, VPos.CENTER);
    }

    @FXML
    private void createZutatenpaket() {
        System.out.println("Erstellt neues Zutatenpaket");
        Zutatenpaket zutatenpaket = creatZutatenpaketInService();
        //erstelle zutatenpaket view
        createAndAddZutatenpaketView(zutatenpaket);
    }

    private Zutatenpaket creatZutatenpaketInService() {
        //erstelle Zutatenpaket
        Zutat selectedZutat = this.zutatenpaketverwaltung.getSelectedZutatForZutatenpaketCreation();
        Paket selectdPaket = this.zutatenpaketverwaltung.getSelectedPaketForZutatenpaketCreation();
        Zutatenpaket zutatenpaket = zutatenpaketverwaltung.erstelleZutatenpaket(selectedZutat, selectdPaket);

        return zutatenpaket;
    }

    private void createAndAddZutatenpaketView(Zutatenpaket zutatenpaket) {
        //todo: was passiert mit diesem controller für das zutatenpaket, wo wird der gespeichert??
        ZutatenpaketController zutatenpaketController = ViewBuilder.createZutatenpaketView(zutatenpaketBox, zutatenpaket, this.dragAndDrop);
    }

    public void setDragAndDrop(DragAndDrop dragAndDrop) {
        this.dragAndDrop = dragAndDrop;
    }

    public Zutatenpaketplatzierung getZutatenpaketPlatzierung() {
        return zutatenpaketPlatzierung;
    }

    @FXML
    private void neueZutatHinzufuegen() {
        if(mainViewController != null) {
            mainViewController.showPopUp(this.neueZutatView);
        } else {
            System.out.println("mainViewController ist null");
        }
    }
}
