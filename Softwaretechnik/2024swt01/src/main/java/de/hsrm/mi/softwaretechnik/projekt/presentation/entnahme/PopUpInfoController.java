package de.hsrm.mi.softwaretechnik.projekt.presentation.entnahme;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutatenpaket;
import de.hsrm.mi.softwaretechnik.projekt.presentation.mainView.MainViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Controller für den Warenkorb-Bereich der Anwendung
 * Verantwortlich für die Darstellung und Verwaltung des Warenkorbinhalts
 */
public class PopUpInfoController {

    private MainViewController mainViewController;

    @FXML
    private Pane popUpInfo;

    @FXML
    private Button popUpCoseButton;

    @FXML
    private Text infoText;

    Label nameLabel;
    Label unvertraglichkeitenLabel;
    Label gewichtLabel;
    Label tragfaehigkeitLabel;


    /**
     * Konstruktor
     *
     * @param mainViewController Controller der MainView
     */
    public PopUpInfoController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;

        FXMLLoader popUpInfoLoader = new FXMLLoader(getClass().getResource("/platzieren/PopUpZutatenpaketInfo.fxml"));

        try {
            popUpInfoLoader.setController(this);
            this.popUpInfo = popUpInfoLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        initZutatenpaketVBox();
    }

    /**
     * Schließt das Info-Popup
     */
    @FXML
    private void closePopUp() {
        if (this.mainViewController != null) {
            mainViewController.hidePopUp(this.popUpInfo);
        }
    }

    private void erstelleZutatenPaketInfo(Zutatenpaket zutatenpaket){
        // Labels für die verschiedenen Kategorien
        nameLabel.setText("Name: " + zutatenpaket.getZutat().getName());
        unvertraglichkeitenLabel.setText("Unverträglichkeiten: " + zutatenpaket.getZutat().unvertraeglichkeitenAlsString());
        gewichtLabel.setText("Gewicht: " + zutatenpaket.getPaket().getGewicht());
        tragfaehigkeitLabel.setText("Tragfähigkeit: " + zutatenpaket.getTragfaehigkeit());
    }

    public Text getInfoText() {
        return infoText;
    }


    public void setZutatenpaketInfoText(Zutatenpaket zutatenpaket) {erstelleZutatenPaketInfo(zutatenpaket);  }

    public Pane getPopUpInfo() {
        return popUpInfo;
    }

    private void initZutatenpaketVBox(){
        VBox zutatenPaketInfoPane = new VBox();
        nameLabel = new Label();
        unvertraglichkeitenLabel = new Label();
        gewichtLabel = new Label();
        tragfaehigkeitLabel = new Label();

        // Labels für die Werte
        zutatenPaketInfoPane.setSpacing(10);
        zutatenPaketInfoPane.setPadding((new Insets(10)));
        zutatenPaketInfoPane.getChildren().addAll(
                nameLabel,
                unvertraglichkeitenLabel,
                gewichtLabel,
                tragfaehigkeitLabel
        );

        zutatenPaketInfoPane.setPrefHeight(300);
        zutatenPaketInfoPane.setPrefWidth(600);

        this.popUpInfo.getChildren().addFirst(zutatenPaketInfoPane);
    }
}
