package de.hsrm.mi.softwaretechnik.projekt.presentation.entnahme;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutatenpaket;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ObjektExistiertNichtException;
import de.hsrm.mi.softwaretechnik.projekt.business.services.Entnahme;
import de.hsrm.mi.softwaretechnik.projekt.presentation.mainView.MainViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Controller für den Warenkorb-Bereich der Anwendung
 * Verantwortlich für die Darstellung und Verwaltung des Warenkorbinhalts
 */
public class WarenkorbController {

    private MainViewController mainViewController;
    private EntnahmeController entnahmeController;
    private Entnahme entnahmeService;
    private final Pane warenkorbView;
    private ObservableList<Zutatenpaket> zutatenpaketObservableList;

    @FXML
    private BorderPane popUpWarenkorb;

    @FXML
    private ListView<Zutatenpaket> warenkorbInhalt;

    @FXML
    private Button popUpCoseButton;

    /**
     * Konstruktor
     *
     * @param mainViewController Controller der MainView
     */
    public WarenkorbController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;

        FXMLLoader warenkorbViewLoader = new FXMLLoader(getClass().getResource("/entnahme/WarenkorbView.fxml"));

        try {
            warenkorbViewLoader.setController(this);
            this.warenkorbView = warenkorbViewLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Erstellt die ListView für den Warenkorb und fügt die Zutatenpakete hinzu
     */
    public void createListView() {
        this.getWarenkorbInhalt();
        this.warenkorbInhalt.setItems(this.zutatenpaketObservableList);
        this.warenkorbInhalt.setCellFactory(param -> new WarenkorbListCellController());
    }

    /**
     * Schließt das Warenkorb-Popup
     */
    @FXML
    private void closePopUp() {
        if (this.mainViewController != null) {
            mainViewController.hidePopUp(this.warenkorbView);
        }
    }

    /**
     * Beendet die aktuelle Entnahme
     *
     * @throws ObjektExistiertNichtException Exception, wenn ein erforderliches Objekt nicht gefunden wird
     */
    @FXML
    private void entnahmeAbschliessen() throws ObjektExistiertNichtException {
        this.entnahmeService.entnahmeAbschliessen();
        this.entnahmeController.aktualisiereListViewNachEntnahme();
        this.closePopUp();
    }

    /**
     * Gibt die View für den Warenkorb zurück
     *
     * @return View für den Warenkorb
     */
    @FXML
    public Pane getWarenkorbView() {
        return this.warenkorbView;
    }

    /**
     * Holt den aktuellen Inhalt des Warenkorbs und aktualisiert die ListView
     */
    public void getWarenkorbInhalt() {
        this.zutatenpaketObservableList = FXCollections.observableArrayList(this.entnahmeService.zeigeAlleZutatenpaketeImWarenkorb());
    }

    /**
     * Setzt den EntnahmeService, der für die Verwaltung von Entnahmen zuständig ist
     *
     * @param entnahmeService Entnahmeservice
     */
    public void setEntnahmeService(Entnahme entnahmeService) {
        this.entnahmeService = entnahmeService;
    }

    /**
     * Getter für Zutatenpaket-Liste
     *
     * @return Zutatenpaket-Liste
     */
    public ObservableList<Zutatenpaket> getZutatenpaketObservableList() {
        return zutatenpaketObservableList;
    }

    /**
     * Setzt den EntnahmeController
     *
     * @param entnahmeController Entnahme-Controller
     */
    public void setEntnahmeController(EntnahmeController entnahmeController) {
        this.entnahmeController = entnahmeController;
    }
}
