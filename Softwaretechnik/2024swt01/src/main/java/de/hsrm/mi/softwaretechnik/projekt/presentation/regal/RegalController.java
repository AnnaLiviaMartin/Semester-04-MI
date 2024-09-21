package de.hsrm.mi.softwaretechnik.projekt.presentation.regal;

import de.hsrm.mi.softwaretechnik.projekt.business.Utilities;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.*;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.RegalkomponenteNichtLoeschbarException;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.RegalkomponenteNichtPlatzierbarException;
import de.hsrm.mi.softwaretechnik.projekt.business.services.Lagereditor;
import de.hsrm.mi.softwaretechnik.projekt.business.services.LagereditorImpl;
import de.hsrm.mi.softwaretechnik.projekt.presentation.ViewBuilder;
import de.hsrm.mi.softwaretechnik.projekt.presentation.dragAndDrop.DragAndDrop;
import de.hsrm.mi.softwaretechnik.projekt.presentation.mainView.MainViewController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Random;

public class RegalController {
    private final double REGAL_WAND_BREITE = 25;
    private final double REGAL_BODEN_HOEHE = 10;
    private Regal regal;
    private Regalboden ausgewaehlterRegalboden;
    private Regalwand ausgewaehlteRegalwand;
    private MainViewController mainViewController;
    private DragAndDrop dragAndDropEvent;

    @FXML
    private Pane regalView;

    @FXML
    private Button regalbodenButton;

    private Pane ablagebereichPane;

    private Lagereditor lagereditor;

    public RegalController(MainViewController mainViewController, DragAndDrop dragAndDropEvent) {
        this.mainViewController = mainViewController;
        this.dragAndDropEvent = dragAndDropEvent;
        FXMLLoader regalViewLoader = new FXMLLoader(getClass().getResource("/regal/RegalView.fxml"));
        regalViewLoader.setController(this);

        try {
            regalView = regalViewLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //Platform.runLater, da der canvas erst initialisiert sein muss, damit alle Werte richtig gesetzt sind.
        Platform.runLater(() -> {
            try {
                lagereditor = new LagereditorImpl(regalView.getWidth(), regalView.getHeight(), REGAL_WAND_BREITE, REGAL_BODEN_HOEHE);
            } catch (RegalkomponenteNichtPlatzierbarException e) {
                throw new RuntimeException(e);
            }
            baueInitialRegalRahmen();

            ablagebereichPane = new Pane();
            regalView.getChildren().addFirst(ablagebereichPane);

            zeichneAblagebereiche();
        });
    }

    public Pane getRegalView() {
        return regalView;
    }

    @FXML
    public void initialize() {
        //Initialize the Lagereditor with window dimensions
    }

    private void zeichneAblagebereiche() {
        var ablagebereiche = regal.getAblagebereiche();

        ablagebereichPane.getChildren().clear();
        for (Ablagebereich ablagebereich : ablagebereiche) {
            Pane newAblagebereichPane = new Pane();

            newAblagebereichPane.setLayoutX(ablagebereich.getPosX());
            newAblagebereichPane.setLayoutY(ablagebereich.getPosY());
            newAblagebereichPane.setPrefWidth(ablagebereich.getBreite());
            newAblagebereichPane.setPrefHeight(ablagebereich.getHoehe());

            newAblagebereichPane.setStyle("-fx-background-color: white;");
            ablagebereichPane.getChildren().add(newAblagebereichPane);

            //init drop
            this.dragAndDropEvent.initDropIntoRegal(newAblagebereichPane, ablagebereich);
        }
    }

    /**
     * Erstellt ein initiales Regal mit Regalkomponenten und dessen Repräsentation. Fügt dem Model die Infos hinzu
     * und erstellt dann diese als Panes.
     */
    private void baueInitialRegalRahmen() {
        regal = lagereditor.erhalteRegal();

        for (Regalwand regalwand : regal.getRegalwaende()) {
            try {
                FXMLLoader regalWandLoader = new FXMLLoader(getClass().getResource("/regal/Regalwand.fxml"));
                Pane regalWandPane = regalWandLoader.load();

                regalWandPane.setLayoutX(regalwand.getxPos());
                regalWandPane.setLayoutY(regalwand.getyPos());
                regalWandPane.setPrefWidth(regalwand.getBreite());
                regalWandPane.setPrefHeight(regalwand.getHoehe());

                regalView.getChildren().add(regalWandPane);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (Regalboden regalboden : regal.getRegalboeden()) {
            try {
                FXMLLoader regalBodenLoader = new FXMLLoader(getClass().getResource("/regal/Regalboden.fxml"));
                Pane regalBodenPane = regalBodenLoader.load();

                regalBodenPane.setLayoutX(regalboden.getxPos());
                regalBodenPane.setLayoutY(regalboden.getyPos());
                regalBodenPane.setPrefWidth(regalboden.getBreite());
                regalBodenPane.setPrefHeight(regalboden.getHoehe());

                regalView.getChildren().add(regalBodenPane);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void zeichneRegalboden(Regalboden regalboden) {
        // Create a new Regalboden Pane
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/regal/Regalboden.fxml"));
        Pane regalbodenPane;

        try {
            regalbodenPane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Setzt die Höhe und Breite
        regalbodenPane.setPrefWidth(regalboden.getBreite());
        regalbodenPane.setPrefHeight(regalboden.getHoehe());

        //Setzt Positionen im Pane
        regalbodenPane.setLayoutX(regalboden.getxPos());
        regalbodenPane.setLayoutY(regalboden.getyPos());

        //Fügt es dem Pane hinzu
        regalView.getChildren().add(regalbodenPane);

        // Set up drag and drop
        enableDragRegalboden(regalbodenPane, regalboden);
        Platform.runLater(() -> {
            this.zeichneAblagebereiche();
        });
    }

    @FXML
    private void createRegalboden() {

        // Add the Regalboden to the business logic
        var regalboden = lagereditor.erstelleRegalboden();
        try {
            lagereditor.platziereRegalboden(regalboden, 100, 400);
        } catch (RegalkomponenteNichtPlatzierbarException e) {
            throw new RuntimeException(e);
        }
        zeichneRegalboden(regalboden);
    }

    private void zeichenRegalwand(Regalwand regalwand) {
        // Create a new Regalboden Pane
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/regal/Regalwand.fxml"));
        Pane regalwandPane;

        try {
            regalwandPane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Setzt die Höhe und Breite
        regalwandPane.setPrefWidth(regalwand.getBreite());
        regalwandPane.setPrefHeight(regalwand.getHoehe());

        //Setzt Positionen im Pane
        regalwandPane.setLayoutX(regalwand.getxPos());
        regalwandPane.setLayoutY(regalwand.getyPos());

        //Fügt es dem Pane hinzu
        regalView.getChildren().add(regalwandPane);

        //lagereditor.platziereRegalwand(regalwand, 100, 100);
        enableDragRegalwand(regalwandPane, regalwand);

        Platform.runLater(() -> {
            this.zeichneAblagebereiche();
        });
    }

    @FXML
    private void createRegalwand() {
        // Add the Regalboden to the business logic
        var regalwand = lagereditor.erstelleRegalwand();
        try {
            lagereditor.platziereRegalwand(regalwand, getRegalView().getWidth() - 4 * REGAL_WAND_BREITE);
        } catch (RegalkomponenteNichtPlatzierbarException e) {
            throw new RuntimeException(e);
        }

        zeichenRegalwand(regalwand);
    }

    private void enableDragRegalboden(Pane pane, Regalboden regalboden) {
        final Delta dragDelta = new Delta();

        pane.setOnMousePressed(event -> {
            dragDelta.x = pane.getLayoutX() - event.getSceneX();
            dragDelta.y = pane.getLayoutY() - event.getSceneY();

            //Setzen des ausgewaehlten Regalbodens
            ausgewaehlterRegalboden = regalboden;
        });

        pane.setOnMouseDragged(event -> {
            Platform.runLater(() -> {
                try {
                    lagereditor.platziereRegalboden(ausgewaehlterRegalboden, event.getSceneY() + dragDelta.y, event.getSceneX() + dragDelta.x);
                } catch (RegalkomponenteNichtPlatzierbarException e) {
                    throw new RuntimeException(e);
                }

                // Update the position in the business logic
                pane.setLayoutX(ausgewaehlterRegalboden.getxPos());
                pane.setLayoutY(ausgewaehlterRegalboden.getyPos());
                pane.setPrefWidth(ausgewaehlterRegalboden.getBreite());
                pane.setPrefHeight(ausgewaehlterRegalboden.getHoehe());

                zeichneAblagebereiche();
            });
        });

    }

    @FXML
    private void loescheAusgewaehlteRegalwand() {
        if (ausgewaehlteRegalwand != null) {
            try {
                lagereditor.loescheRegalwand(ausgewaehlteRegalwand);
            } catch (RegalkomponenteNichtLoeschbarException e) {
                throw new RuntimeException(e);
            }
            regalView.getChildren().removeIf(node -> node.getLayoutX() == ausgewaehlteRegalwand.getxPos() &&
                    node.getLayoutY() == ausgewaehlteRegalwand.getyPos());
            ausgewaehlteRegalwand = null;
        }
    }

    @FXML
    private void loescheAusgewaehltenRegalboden() {
        if (ausgewaehlterRegalboden != null) {
            try {
                lagereditor.loescheRegalboden(ausgewaehlterRegalboden);
            } catch (RegalkomponenteNichtLoeschbarException e) {
                throw new RuntimeException(e);
            }
            regalView.getChildren().removeIf(node -> node.getLayoutX() == ausgewaehlterRegalboden.getxPos() &&
                    node.getLayoutY() == ausgewaehlterRegalboden.getyPos());
            ausgewaehlterRegalboden = null;
        }
    }

    private void enableDragRegalwand(Pane pane, Regalwand regalwand) {
        final Delta dragDelta = new Delta();

        pane.setOnMousePressed(event -> {
            dragDelta.x = pane.getLayoutX() - event.getSceneX();
            dragDelta.y = pane.getLayoutY() - event.getSceneY();

            //setzen der geklickten regalwand
            ausgewaehlteRegalwand = regalwand;
        });

        pane.setOnMouseDragged(event -> {
            // Update the position in the business logic
            try {
                lagereditor.platziereRegalwand(regalwand, event.getSceneX() + dragDelta.x);
            } catch (RegalkomponenteNichtPlatzierbarException e) {
                throw new RuntimeException(e);
            }

            pane.setLayoutX(regalwand.getxPos());
            pane.setLayoutY(regalwand.getyPos());
            pane.setPrefWidth(regalwand.getBreite());
            pane.setPrefHeight(regalwand.getHoehe());
            zeichneAblagebereiche();

        });
    }

    public Regal getRegal() {
        return regal;
    }

    public void baueRegalNeu() {
        ausgewaehlteRegalwand = null;
        ausgewaehlterRegalboden = null;
        regalView.getChildren().clear();
        regalView.getChildren().addFirst(ablagebereichPane);
        for (Regalboden regalboden : regal.getRegalboeden()) {
            zeichneRegalboden(regalboden);
        }

        for (Regalwand regalwand : regal.getRegalwaende()) {
            zeichenRegalwand(regalwand);
        }

        zeichneAblagebereiche();
    }

    @FXML
    private void saveRegal() {
        //TODO fix speichern mit zutatenpaketen
        if(!lagereditor.bereitsZutatenpaketePlatziert()){
            Utilities.saveRegalZutatenPakete(regal);
            System.out.println("Regal wurde gespeichert.");
        } else {
            System.err.println("Regal kann nicht gespeichert werden, da Zutatenpakete drin sind.");
        }

    }

    @FXML
    private void ladeNeuesRegal() {
        regal = Utilities.loadRegal();
        if(regal != null){
            lagereditor.setzeNeuesRegal(regal);
            baueRegalNeu();

            //TODO Fix laden mit zutatenpaketen
            /*
            for(Ablagebereich ablagebereich: regal.getAblagebereiche()){
                for(Zutatenpaket zutatenpaket: ablagebereich.getZutatenpakete()){
                    ViewBuilder.createAndAddZutatenpaketView(null, zutatenpaket, null);

                }
            }

 */
            //zeichnePaketeNeu();
        } else {
            System.err.println("Das Regal ist null!");
        }


        //List<Zutat> zs = Utilities.loadZutaten();
        //List<Paket> ss = Utilities.loadPakete();
    }

    // Helper class for drag-and-drop
    private static class Delta {
        double x, y;
    }
}

