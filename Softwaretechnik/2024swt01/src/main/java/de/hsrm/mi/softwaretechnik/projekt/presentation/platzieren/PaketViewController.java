package de.hsrm.mi.softwaretechnik.projekt.presentation.platzieren;

import de.hsrm.mi.softwaretechnik.projekt.business.services.ZutatenpaketverwaltungImpl;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class PaketViewController {
    private ZutatenpaketverwaltungImpl zutatenpaketverwaltung;
    @FXML
    private ImageView paketRechteck;

    @FXML
    private Text paketGroesse;

    @FXML
    private StackPane hintergrund;

    private StackPane paketPane;

    public PaketViewController(ZutatenpaketverwaltungImpl zutatenpaketverwaltung) {
        this.zutatenpaketverwaltung = zutatenpaketverwaltung;
    }

    public ImageView getPaketRechteck() {
        return paketRechteck;
    }

    public void setPaketRechteck(ImageView paketRechteck) {
        this.paketRechteck = paketRechteck;
    }

    public Text getPaketGroesse() {
        return paketGroesse;
    }

    public void setPaketGroesseText(String paketGroesse) {
        this.paketGroesse.setText(paketGroesse);
    }

    public void setPaketGroesse(double width, double height) {
        this.hintergrund.getStyleClass().add("auswaehlbares-objekt");
        this.hintergrund.setMaxWidth(width);
        this.hintergrund.setMaxHeight(height);
        this.hintergrund.setPrefSize(width, height);
    }

    public StackPane getHintergrund() {
        return hintergrund;
    }

    @FXML
    private void addSelectedPaketForZutatenpaketCreation() {
        System.out.println("Paket ausgewählt: " + this.paketGroesse);
        this.zutatenpaketverwaltung.setSelectedPaketForZutatenpaketCreationBySize(this.hintergrund.getMaxWidth(), this.hintergrund.getMaxHeight());
        //Todo set style for new selected paket
    }

    public StackPane getPaketPane() {
        return paketPane;
    }

    public void setPaketPane(StackPane paketPane) {
        this.paketPane = paketPane;
    }
}
