package de.hsrm.mi.softwaretechnik.projekt.presentation.platzieren;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutatenpaket;
import de.hsrm.mi.softwaretechnik.projekt.business.services.ZutatenpaketverwaltungImpl;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ZutatenpaketController {
    @FXML
    private ImageView hintergrundBox;

    @FXML
    private ImageView zutatBox;

    @FXML
    private Text zutatInhalt;

    @FXML
    private StackPane hintergrund;

    private StackPane zutatenpaketPane;

    private Zutatenpaket zutatenpaket;

    public ZutatenpaketController(Zutatenpaket zutatenpaket) {
        this.zutatenpaket = zutatenpaket;
    }

    public ImageView getHintergrundBox() {
        return hintergrundBox;
    }

    public ImageView getZutatBox() {
        return zutatBox;
    }

    public Text getZutatInhalt() {
        return zutatInhalt;
    }

    public void setZutatInhalt(String paketGroesse) {
        this.zutatInhalt.setText(paketGroesse);
    }

    public void setPaketGroesse(double width, double height) {
        this.hintergrund.setMaxWidth(width);
        this.hintergrund.setMaxHeight(height);
        this.hintergrund.setPrefSize(width, height);
    }

    public StackPane getHintergrund() {
        return hintergrund;
    }

    public StackPane getZutatenpaketPane() {
        return zutatenpaketPane;
    }

    public void setZutatenpaketPane(StackPane zutatenpaketPane) {
        this.zutatenpaketPane = zutatenpaketPane;
    }

    public String toString(){
        return zutatenpaket.toString();
    }

    public Zutatenpaket getZutatenpaket(){
        return zutatenpaket;
    }
}
