package de.hsrm.mi.softwaretechnik.projekt.presentation.platzieren;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutat;
import de.hsrm.mi.softwaretechnik.projekt.presentation.mainView.MainViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;


public class NeueZutatController {

    private MainViewController mainViewController;
    private final Pane neueZutatView;

    @FXML
    private TextField artZutat;

    @FXML
    private ImageView iconZutat;

    @FXML
    private Button bildHochladenButton;

    @FXML
    private Button speicherButton;

    @FXML
    private Button schliessenButton;

    private Image icon;

    public NeueZutatController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;

        FXMLLoader neueZutatViewLoader = new FXMLLoader(getClass().getResource("/platzieren/NeueZutatView.fxml"));

        try {
            neueZutatViewLoader.setController(this);
            this.neueZutatView = neueZutatViewLoader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    private void iconZutatHochladen() {
        FileChooser.ExtensionFilter icons = new FileChooser.ExtensionFilter("Bilder", "*.png", "*jpg");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(icons);

        File verzeichnis = new File(System.getProperty("user.dir"), "images/zutat");
        if (verzeichnis.exists()) {
            fileChooser.setInitialDirectory(verzeichnis);
        }

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            icon = new Image(file.toURI().toString());
            iconZutat.setImage(icon);
        }
    }

    @FXML
    private void speicherZutat() {
        String name = artZutat.getText();

        if(name != null && !name.isEmpty() && icon != null){

            if(mainViewController.getZutatenpaketverwaltung() == null){
                System.out.println("Zutatenpaketverwaltung ist null");
                return;
            }

            boolean exists = mainViewController.getZutatenpaketverwaltung().getZutatenliste().stream()
                    .anyMatch(z -> z.getName().equals(name));

            if(!exists) {
            Zutat neueZutat = mainViewController.getZutatenpaketverwaltung().erstelleNeueZutat(name);
            if(neueZutat != null){
                neueZutat.setIconPath(icon.getUrl());
                mainViewController.getZutatenpaketverwaltung().getZutatenliste().add(neueZutat);
                System.out.println("Neue Zutat hinzugefügt: " + neueZutat.getName());
                hidePopUp();

                mainViewController.getPlatzierenController().initZutaten();
                //TODO Methode implementieren

            } else {
                //TODO Fehlermeldung
            }
        } else {
                System.out.println("Zutat existiert bereits");
            //TODO Fehlermeldung
        }
        } else {
            //TODO Fehlermeldung
        }

    }

    public void hidePopUp() {
        if(this.mainViewController != null){
            mainViewController.hidePopUp(this.neueZutatView);
        }
    }

    @FXML
    private void schliessen() {
        hidePopUp();
    }

    @FXML
    public Pane getNeueZutatView() {
        return this.neueZutatView;
    }

}
