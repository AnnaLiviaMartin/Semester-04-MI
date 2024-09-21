package de.hsrm.mi.softwaretechnik.projekt.presentation;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Paket;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutat;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutatenpaket;
import de.hsrm.mi.softwaretechnik.projekt.business.services.ZutatenpaketverwaltungImpl;
import de.hsrm.mi.softwaretechnik.projekt.presentation.dragAndDrop.DragAndDrop;
import de.hsrm.mi.softwaretechnik.projekt.presentation.mainView.MainViewController;
import de.hsrm.mi.softwaretechnik.projekt.presentation.platzieren.PaketViewController;
import de.hsrm.mi.softwaretechnik.projekt.presentation.platzieren.ZutatViewController;
import de.hsrm.mi.softwaretechnik.projekt.presentation.platzieren.ZutatenpaketController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.text.View;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ViewBuilder {
    static MainViewController mainViewController;

    public ViewBuilder(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public static ZutatViewController createZutatView(Zutat zutat, ZutatenpaketverwaltungImpl zutatenpaketverwaltung) {
        return createZutat(zutat, zutatenpaketverwaltung);
    }

    private static ZutatViewController createZutat(Zutat zutat, ZutatenpaketverwaltungImpl zutatenpaketverwaltung) {
        FXMLLoader zutatLoader = new FXMLLoader(ViewBuilder.class.getResource("/platzieren/ZutatenView.fxml"));
        ZutatViewController zutatController = new ZutatViewController(zutatenpaketverwaltung);

        try {
            //erstelle Pane
            zutatLoader.setController(zutatController);
            StackPane zutatPane = zutatLoader.load();
            zutatController.setZutatPane(zutatPane);
            zutatController.setZutatText(zutat.getName());
            setBackgroundDefaultImageForZutat(zutatController.getZutatImage(), zutat);

            return zutatController;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setBackgroundDefaultImageForZutat(ImageView zutatImageView, Zutat zutat) {
        String backgroundFilePath = "src/main/resources/images/zutat/";
        try {
            Image backgroundImage = new Image(new FileInputStream(backgroundFilePath + zutat.getName() + ".png"));
            zutatImageView.setImage(backgroundImage);
            zutatImageView.setFitHeight(50);
            zutatImageView.setFitWidth(50);
            zutatImageView.setPreserveRatio(true);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static PaketViewController createPaketView(Paket paket, ZutatenpaketverwaltungImpl zutatenpaketverwaltung) {
        return createPaket(paket, zutatenpaketverwaltung);
    }

    private static PaketViewController createPaket(Paket paket, ZutatenpaketverwaltungImpl zutatenpaketverwaltung) {
        FXMLLoader paketLoader = new FXMLLoader(ViewBuilder.class.getResource("/platzieren/PaketView.fxml"));
        PaketViewController paketController = new PaketViewController(zutatenpaketverwaltung);

        try {
            //erstelle Pane
            paketLoader.setController(paketController);
            StackPane paketPane = paketLoader.load();
            paketController.setPaketPane(paketPane);
            paketController.setPaketGroesse(paket.getBreite(), paket.getHoehe());
            setBackgroundImageForPaket(paketController.getPaketRechteck(), paket.getBreite(), paket.getHoehe());

            return paketController;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setBackgroundImageForPaket(ImageView paketView, double breite, double hoehe) {
        try {
            Image backgroundImage = new Image(new FileInputStream("src/main/resources/images/paket/boxBild.jpg"));
            paketView.setImage(backgroundImage);
            paketView.setFitHeight(breite);
            paketView.setFitWidth(hoehe);
            paketView.setPreserveRatio(false);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static ZutatenpaketController createZutatenpaketView(BorderPane zutatenpaketBox, Zutatenpaket zutatenpaket, DragAndDrop dragAndDropEvent) {
        return createAndAddZutatenpaketView(zutatenpaketBox, zutatenpaket, dragAndDropEvent);
    }

    public static ZutatenpaketController createZutatenpaketView(Pane zutatenpaketBox, Zutatenpaket zutatenpaket, DragAndDrop dragAndDropEvent) {
        return createAndAddZutatenpaketView(zutatenpaketBox, zutatenpaket, dragAndDropEvent);
    }

    public static void addZutatenPaketToView(Pane view, ZutatenpaketController zutatenpaketController){
        view.getChildren().add(zutatenpaketController.getZutatenpaketPane());
    }

    private static void addKlickInfo(ZutatenpaketController zutatenpaketController){
        zutatenpaketController.getZutatenpaketPane().setOnMouseClicked(event -> {
            Zutatenpaket zutatenpaket = zutatenpaketController.getZutatenpaket();

            if(event.getButton() == MouseButton.PRIMARY){
                mainViewController.getPopUpInfoController().setZutatenpaketInfoText(zutatenpaket);
                mainViewController.showPopUp(mainViewController.getPopUpInfoController().getPopUpInfo());
            }

            if(event.getButton() == MouseButton.SECONDARY){
                if(zutatenpaket.isTopPaket()){
                    zutatenpaket.getAblagebereich().deleteZutatenpaket(zutatenpaket.getUuid());
                    zutatenpaketController.getZutatenpaketPane().getChildren().clear();
                    zutatenpaketController.getZutatenpaketPane().setVisible(false);
                }
            }
        });
    }

    public static ZutatenpaketController createAndAddZutatenpaketView(Pane zutatenpaketBox, Zutatenpaket zutatenpaket, DragAndDrop dragAndDropEvent) {
        FXMLLoader zutatenpaketLoader = new FXMLLoader(ViewBuilder.class.getResource("/platzieren/Zutatenpaket.fxml"));
        ZutatenpaketController zutatenpaketController = new ZutatenpaketController(zutatenpaket);

        try {
            //erstelle Pane
            zutatenpaketLoader.setController(zutatenpaketController);
            StackPane zutatenpaketPane = zutatenpaketLoader.load();
            zutatenpaketController.setZutatInhalt(zutatenpaket.getZutat().getName());
            zutatenpaketController.setPaketGroesse(zutatenpaket.getPaket().getBreite(), zutatenpaket.getPaket().getHoehe());
            zutatenpaketController.setZutatenpaketPane(zutatenpaketPane);
            setBackgroundImagesForZutatenpaket(zutatenpaketController.getHintergrundBox(), zutatenpaket.getPaket().getBreite(), zutatenpaket.getPaket().getHoehe(), zutatenpaketController.getZutatBox(), zutatenpaket.getZutat());

            addKlickInfo(zutatenpaketController);

            return zutatenpaketController;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ZutatenpaketController createAndAddZutatenpaketView(BorderPane zutatenpaketBox, Zutatenpaket zutatenpaket, DragAndDrop dragAndDropEvent) {
        FXMLLoader zutatenpaketLoader = new FXMLLoader(ViewBuilder.class.getResource("/platzieren/Zutatenpaket.fxml"));
        ZutatenpaketController zutatenpaketController = new ZutatenpaketController(zutatenpaket);

        try {
            //erstelle Pane
            zutatenpaketLoader.setController(zutatenpaketController);
            StackPane zutatenpaketPane = zutatenpaketLoader.load();
            zutatenpaketController.setZutatInhalt(zutatenpaket.getZutat().getName());
            zutatenpaketController.setPaketGroesse(zutatenpaket.getPaket().getBreite(), zutatenpaket.getPaket().getHoehe());
            zutatenpaketController.setZutatenpaketPane(zutatenpaketPane);
            setBackgroundImagesForZutatenpaket(zutatenpaketController.getHintergrundBox(), zutatenpaket.getPaket().getBreite(), zutatenpaket.getPaket().getHoehe(), zutatenpaketController.getZutatBox(), zutatenpaket.getZutat());
            //setzt Pane
            zutatenpaketBox.getChildren().clear();
            zutatenpaketBox.setCenter(null);
            zutatenpaketBox.setCenter(zutatenpaketPane);

            dragAndDropEvent.initDragPlatzierenToRegal(zutatenpaketPane, zutatenpaketController, zutatenpaket, zutatenpaketBox);
            return zutatenpaketController;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setBackgroundImagesForZutatenpaket(ImageView hintergrundView, double breite, double hoehe, ImageView zutatImage, Zutat zutat) {
        try {
            Image backgroundImage = new Image(new FileInputStream("src/main/resources/images/paket/boxBild.jpg"));
            hintergrundView.setImage(backgroundImage);
            hintergrundView.setFitHeight(breite);
            hintergrundView.setFitWidth(hoehe);
            hintergrundView.setPreserveRatio(false);

            Image zutatImageView = new Image(new FileInputStream("src/main/resources/images/zutat/" +  zutat.getName() + ".png"));
            zutatImage.setImage(zutatImageView);
            zutatImage.setFitHeight(breite * 0.5);
            zutatImage.setFitWidth(hoehe * 0.5);
            zutatImage.setPreserveRatio(true);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
