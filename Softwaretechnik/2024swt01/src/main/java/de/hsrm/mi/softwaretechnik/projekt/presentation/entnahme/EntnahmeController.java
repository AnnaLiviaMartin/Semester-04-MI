package de.hsrm.mi.softwaretechnik.projekt.presentation.entnahme;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutatenpaket;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ObjektExistiertNichtException;
import de.hsrm.mi.softwaretechnik.projekt.business.services.*;
import de.hsrm.mi.softwaretechnik.projekt.presentation.dragAndDrop.DragAndDrop;
import de.hsrm.mi.softwaretechnik.projekt.presentation.mainView.MainViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Controller für die Entnahme-Funktionen, einschließlich der Suche nach Zutatenpaketen,
 * Markieren von gefundenen Zutatenpaketen im Regal und der Interaktion mit dem Warenkorb
 */
public class EntnahmeController {

    private MainViewController mainViewController;
    private WarenkorbController warenkorbController;
    private DragAndDrop dragAndDropEvent;
    private final Entnahme entnahmeService;
    private final Pane entnahmeView;
    private final Pane warenkorbView;

    @FXML
    private ListView<Zutatenpaket> entnahmeListView;

    @FXML
    private GridPane zutatenliste;

    @FXML
    private ImageView warenkorbIcon;

    /**
     * Konstruktor
     *
     * @param mainViewController     Hauptcontroller, der Zugriff auf die Hauptanwendungsfunktionalität bietet
     * @param zutatenpaketverwaltung Dienst zur Verwaltung von Zutatenpaketen
     * @param dragAndDropEvent       Ereignishandler für Drag-and-Drop-Operationen
     */
    public EntnahmeController(MainViewController mainViewController, Zutatenpaketverwaltung zutatenpaketverwaltung, DragAndDrop dragAndDropEvent) {
        this.mainViewController = mainViewController;
        this.dragAndDropEvent = dragAndDropEvent;
        this.warenkorbView = mainViewController.getWarenkorbView();
        this.entnahmeService = new EntnahmeImpl((ZutatenpaketverwaltungImpl) zutatenpaketverwaltung);

        FXMLLoader entnahmeViewLoader = new FXMLLoader(getClass().getResource("/entnahme/EntnahmeView.fxml"));

        try {
            entnahmeViewLoader.setController(this);
            this.entnahmeView = entnahmeViewLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialisierung
     */
    @FXML
    public void initialize() {
        this.setWarenkorbBackgroundImage();
    }

    /**
     * Getter für die Entnahme-View
     *
     * @return Entnahme View
     */
    @FXML
    public Pane getEntnahmeView() {
        return this.entnahmeView;
    }

    /**
     * Setzt das Hintergrundbild des Warenkorbs
     */
    private void setWarenkorbBackgroundImage() {
        try {
            String backgroundFilePath = "src/main/resources/images/zutat/warenkorb.png";
            Image backgroundImage = new Image(new FileInputStream(backgroundFilePath));
            this.warenkorbIcon.setImage(backgroundImage);
            this.warenkorbIcon.setFitHeight(200);
            this.warenkorbIcon.setFitWidth(200);
            this.warenkorbIcon.setPreserveRatio(true);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Öffnet das Warenkorb-Pop Up
     */
    @FXML
    private void oeffneWarenkorbPopUp() {
        this.warenkorbController.createListView();
        if (this.mainViewController != null) mainViewController.showPopUp(this.warenkorbView);
        this.entnahmeService.zeigeAlleZutatenpaketeImWarenkorb();
    }

    /**
     * Fügt ein Zutatenpaket zum Warenkorb hinzu
     *
     * @param zutatenpaket Das Zutatenpaket, das hinzugefügt werden soll
     * @throws ObjektExistiertNichtException Wenn das angegebene Zutatenpaket nicht existiert
     */
    @FXML
    public void addZutatenpaketInWarenkorb(Zutatenpaket zutatenpaket) throws ObjektExistiertNichtException {
        entnahmeService.addZutatenpaketInWarenkorb(zutatenpaket);
        this.createListViewInEntnahme();
    }

    /**
     * Erstellt die ListView für den Warenkorb
     */
    private void createListViewInEntnahme() {
        this.warenkorbController.getWarenkorbInhalt();
        this.entnahmeListView.setItems(this.warenkorbController.getZutatenpaketObservableList());
        this.entnahmeListView.setCellFactory(param -> new WarenkorbListCellController());
    }

    /**
     * Aktualisiert die ListView, nachdem die Entnahme abgeschlossen wurde
     */
    public void aktualisiereListViewNachEntnahme() {
        this.createListViewInEntnahme();
    }

    /**
     * Getter für die ImageView des Warenkorb-Icons
     *
     * @return ImageView mit Warenkorb Icon
     */
    public ImageView getWarenkorbIcon() {
        return warenkorbIcon;
    }

    /**
     * Setter für den WarenkorbController, um die Interaktion mit dem Warenkorb zu ermöglichen
     *
     * @param warenkorbController Der WarenkorbController, der die Warenkorb-Logik verwaltet
     */
    public void setWarenkorbController(WarenkorbController warenkorbController) {
        this.warenkorbController = warenkorbController;
        this.warenkorbController.setEntnahmeService(this.entnahmeService);
        this.warenkorbController.setEntnahmeController(this);
    }
}

