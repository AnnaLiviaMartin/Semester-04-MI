package de.hsrm.mi.softwaretechnik.projekt.presentation.platzieren;

import de.hsrm.mi.softwaretechnik.projekt.business.services.ZutatenpaketverwaltungImpl;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ZutatViewController {

    private ZutatenpaketverwaltungImpl zutatenpaketverwaltung;

    @FXML
    private ImageView zutatImage;

    @FXML
    private Text zutatText;

    private StackPane zutatPane;

    public ZutatViewController(ZutatenpaketverwaltungImpl zutatenpaketverwaltung) {
        this.zutatenpaketverwaltung = zutatenpaketverwaltung;
    }

    public void setZutatText(String text) {
        zutatText.setText(text);
    }

    public ImageView getZutatImage() {
        return zutatImage;
    }

    public Text getZutatText() {
        return zutatText;
    }

    @FXML
    private void addSelectedZutatForZutatenpaketCreation() {
        System.out.println("Zutat ausgewählt: " + this.zutatText);
        this.zutatenpaketverwaltung.setSelectedZutatForZutatenpaketCreationByName(this.zutatText.getText());
        //Todo set style for new selected zutat
    }

    public StackPane getZutatPane() {
        return zutatPane;
    }

    public void setZutatPane(StackPane zutatPane) {
        this.zutatPane = zutatPane;
    }
}
