package de.hsrm.mi.softwaretechnik.projekt.presentation.entnahme;

import de.hsrm.mi.softwaretechnik.projekt.presentation.mainView.MainViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Controller für den Warenkorb-Bereich der Anwendung
 * Verantwortlich für die Darstellung und Verwaltung des Warenkorbinhalts
 */
public class PopUpErrorController {

    private MainViewController mainViewController;

    @FXML
    private Pane popUpError;

    @FXML
    private Button popUpCoseButton;

    @FXML
    private Text errorText;

    /**
     * Konstruktor
     *
     * @param mainViewController Controller der MainView
     */
    public PopUpErrorController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;

        FXMLLoader popUpErrorLoader = new FXMLLoader(getClass().getResource("/entnahme/PopUpError.fxml"));

        try {
            popUpErrorLoader.setController(this);
            this.popUpError = popUpErrorLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Schließt das Error-Popup
     */
    @FXML
    private void closePopUp() {
        if (this.mainViewController != null) {
            mainViewController.hidePopUp(this.popUpError);
        }
    }

    public Text getErrorText() {
        return errorText;
    }

    public void setErrorText(String text) {
        errorText.setText(text);
    }

    public Pane getPopUpError() {
        return popUpError;
    }
}
