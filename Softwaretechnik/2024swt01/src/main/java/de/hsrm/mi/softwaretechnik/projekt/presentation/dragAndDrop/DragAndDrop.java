package de.hsrm.mi.softwaretechnik.projekt.presentation.dragAndDrop;

import com.sun.tools.javac.Main;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.*;
import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ObjektExistiertNichtException;
import de.hsrm.mi.softwaretechnik.projekt.business.services.Platzierbar;
import de.hsrm.mi.softwaretechnik.projekt.business.services.Zutatenpaketplatzierung;
import de.hsrm.mi.softwaretechnik.projekt.business.services.Zutatenpaketverwaltung;
import de.hsrm.mi.softwaretechnik.projekt.presentation.ViewBuilder;
import de.hsrm.mi.softwaretechnik.projekt.presentation.entnahme.EntnahmeController;
import de.hsrm.mi.softwaretechnik.projekt.presentation.mainView.MainViewController;
import de.hsrm.mi.softwaretechnik.projekt.presentation.platzieren.ZutatenpaketController;
import de.hsrm.mi.softwaretechnik.projekt.presentation.regal.RegalController;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DragAndDrop {
    private List<StackPane> ausgewaehlteZP;
    private List<Zutatenpaket> zutatenpakete;
    private List<StackPane> zutatenpaketViews;
    private Pane regalView;
    private RegalController regalController;
    private Zutatenpaketverwaltung zutatenpaketverwaltung;
    private Zutatenpaketplatzierung zutatenpaketplatzierung;
    private EntnahmeController entnahmeController;
    private static DataFormat zutatenpaketFormat;        /*Data format identifier used as means of identifying the data stored on a clipboard/dragboard.*/
    private static DataFormat zutatenpaketStapelFormat;
    private double xPosMouse;
    private double yPosMouse;
    private boolean transferModeAccepted;
    private MainViewController mainViewController;

    public DragAndDrop(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
        ausgewaehlteZP = new ArrayList<>();
        zutatenpaketViews = new ArrayList<>();
        zutatenpaketFormat = new DataFormat("zutatenpaket-id");
        zutatenpaketStapelFormat = new DataFormat("zutatenpaket-stapel-id");
        transferModeAccepted = false;
    }

    /**
     * Initializes the drag of the Zutatenpaket (into the Regal)
     *
     * @param zutatenpaketView
     * @param zutatenpaket
     */
    public void initDragPlatzierenToRegal(StackPane zutatenpaketView, ZutatenpaketController zutatenpaketController, Zutatenpaket zutatenpaket, BorderPane zutatenpaketBox) {
        //init drag detected -> 1
        zutatenpaketView.setOnDragDetected(event -> {
            Dragboard dragboard = zutatenpaketView.startDragAndDrop(TransferMode.MOVE);
            /*pack up data of the object*/
            ClipboardContent content = new ClipboardContent();
            content.put(zutatenpaketFormat, zutatenpaket);
            dragboard.setContent(content);
            event.consume();
        });

        /*if drag was successful - clean up -> 4*/
        zutatenpaketView.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                //hinzufügen zu verwaltung
                this.zutatenpaketViews.add(zutatenpaketView);
                this.zutatenpaketverwaltung.getZutatenpaketListe().add(zutatenpaket);
                //aus editor löschen
                zutatenpaketBox.getChildren().clear();
                zutatenpaketBox.setCenter(null);
            }
            event.consume();
        });
    }

    /**
     * Initializes the drop (of the Zutatenpaket) into the Regal
     *
     * @param ablagebereichView
     * @param ablagebereich
     */
    public void initDropIntoRegal(Pane ablagebereichView, Ablagebereich ablagebereich) {
        //accept drag if correct transfer mode + content offered -> 2
        ablagebereichView.setOnDragOver(event -> {
            boolean platzierbar = false;
            this.xPosMouse = event.getX();
            this.yPosMouse = event.getY();

            // wenn ein einziges Zutatenpaket verschoben wird
            if (event.getDragboard().hasContent(zutatenpaketFormat)) {
                Zutatenpaket zutatenpaket = (Zutatenpaket) event.getDragboard().getContent(zutatenpaketFormat);

                if (zutatenpaket.isInEditor()) {
                    for (Zutatenpaket zutatenpaketZuChecken : ablagebereich.getZutatenpakete()) {
                        if (!kollidiert(zutatenpaket, zutatenpaketZuChecken, xPosMouse, yPosMouse)) {
                            platzierbar = true;
                        } else {
                            //System.out.println("Kollidiert!");
                        }
                    }

                    if (ablagebereich.getZutatenpakete().isEmpty()) {
                        platzierbar = true;
                    }

                    if (event.getDragboard().hasContent(zutatenpaketFormat) && event.getTransferMode() == TransferMode.MOVE && platzierbar) {
                        event.acceptTransferModes(TransferMode.MOVE);
                        platzierbar = false;
                    }
                } else {
                    //lösche zutatenpaket überall raus und setze es einmal für den neuen ablagebereich
                    for (Ablagebereich ablagebereichToCheck : this.regalController.getRegal().getAblagebereiche()) {
                        for (Zutatenpaket zutatenpaketToCheck : ablagebereichToCheck.getZutatenpakete()) {
                            if (zutatenpaketToCheck.getUuid().equals(zutatenpaket.getUuid())) {
                                ablagebereich.deleteZutatenpaket(zutatenpaketToCheck.getUuid());
                                return;
                            }
                        }
                    }

                    // wenn zutatenpaket in regal ist
                    for (Zutatenpaket zutatenpaketZuChecken : ablagebereich.getZutatenpakete()) {
                        //wenn Paket nicht kollidiert und platzierbar ist -> platziere
                        if (!kollidiert(zutatenpaket, zutatenpaketZuChecken, xPosMouse, yPosMouse)) {
                            platzierbar = true;
                        } else {
                            //System.out.println("Kollidiert!");
                        }
                    }

                    if (ablagebereich.getZutatenpakete().isEmpty()) {
                        platzierbar = true;
                    }

                    if (event.getDragboard().hasContent(zutatenpaketFormat) && event.getTransferMode() == TransferMode.MOVE && platzierbar) {
                        event.acceptTransferModes(TransferMode.MOVE);
                        transferModeAccepted = true;
                    } else {
                        // wenn platzierung nicht möglich muss zutatenpaket wieder hinzugefügt werden
                        for (Zutatenpaket zp : ablagebereich.getZutatenpakete()) {
                            if (!zp.getUuid().equals(zutatenpaket.getUuid())) {
                                ablagebereich.getZutatenpakete().add(zutatenpaket);
                            }
                        }
                    }
                }

            } else if (event.getDragboard().hasContent(zutatenpaketStapelFormat)) { // wenn mehrere Pakete verschoben werden
                System.out.println("Zutatenpakete acceptance check - 2");

                ZutatenpaketStapel zutatenpaketStapel = (ZutatenpaketStapel) event.getDragboard().getContent(zutatenpaketStapelFormat);

                //lösche zutatenpaket überall raus und setze es einmal für den neuen ablagebereich
                List<Zutatenpaket> obernLiegendePakete = ablagebereich.findeAlleAufliegendenPakete(zutatenpaketStapel.getUnterstesZutatenpaket());
                obernLiegendePakete.add(zutatenpaketStapel.getUnterstesZutatenpaket());

                Iterator<Zutatenpaket> iterator = ablagebereich.getZutatenpakete().iterator();
                while (iterator.hasNext()) {
                    Zutatenpaket zutatenpaket = iterator.next();
                    for (Zutatenpaket zutatenPaketObenLiegend : obernLiegendePakete) {
                        if (zutatenpaket.getUuid().equals((zutatenPaketObenLiegend.getUuid()))) {
                            iterator.remove();
                            break; // Breche die innere Schleife ab, da das Paket bereits entfernt wurde
                        }
                    }
                }

                // wenn zutatenpaket in regal ist
                for (Zutatenpaket zutatenpaketZuChecken : ablagebereich.getZutatenpakete()) {
                    //wenn Paket nicht kollidiert und platzierbar ist -> platziere
                    if (!kollidiert(zutatenpaketStapel, zutatenpaketZuChecken, xPosMouse, yPosMouse)) {
                        platzierbar = true;
                    } else {
                        //System.out.println("Kollidiert!");
                    }
                }

                if (ablagebereich.getZutatenpakete().isEmpty()) {
                    platzierbar = true;
                }

                if (event.getDragboard().hasContent(zutatenpaketStapelFormat) && event.getTransferMode() == TransferMode.MOVE && platzierbar) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    transferModeAccepted = true;
                } else {
                    // wenn platzierung nicht möglich muss zutatenpaket wieder hinzugefügt werden
                    for (Zutatenpaket zp : ablagebereich.getZutatenpakete()) {
                        for (Zutatenpaket zutatenpaketFromStapel : zutatenpaketStapel.getZutatenpakete()) {
                            if (!zp.getUuid().equals(zutatenpaketFromStapel.getUuid())) {
                                ablagebereich.getZutatenpakete().add(zutatenpaketFromStapel);
                            }
                        }
                    }
                }

            }
            event.consume();
        });

        //import dragged data -> 3
        ablagebereichView.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();

            // wenn ein einziges Zutatenpaket verschoben wird
            if (dragboard.hasContent(zutatenpaketFormat)) {
                System.out.println("Zutatenpaket importieren - 3");
                if (event.getTransferMode() == TransferMode.MOVE) {
                    Zutatenpaket droppedZutatenpaket = (Zutatenpaket) dragboard.getContent(zutatenpaketFormat);
                    // todo: wo speichern wir diesen controller?

                    if (droppedZutatenpaket.isInEditor()) {
                        //ist im editor -> wird normal gesetzt
                        try {
                            droppedZutatenpaket.setInEditor(false);
                            //set business logik
                            Ablageflaeche unterliegendeAblageflaeche = this.zutatenpaketplatzierung.getAblageflaeche(xPosMouse, yPosMouse, droppedZutatenpaket, ablagebereich);
                            if (zutatenpaketplatzierung.platziereZutatenpaket(droppedZutatenpaket, ablagebereich, unterliegendeAblageflaeche, xPosMouse, yPosMouse)) {
                                ZutatenpaketController zutatenpaketViewController = ViewBuilder.createZutatenpaketView(ablagebereichView, droppedZutatenpaket, this);
                                ViewBuilder.addZutatenPaketToView(ablagebereichView, zutatenpaketViewController);

                                zutatenpaketViewController.getZutatenpaketPane().setLayoutX(droppedZutatenpaket.getxPos());
                                zutatenpaketViewController.getZutatenpaketPane().setLayoutY(droppedZutatenpaket.getyPos());

                                // allow drag within regal
                                initDragInsideRegal(zutatenpaketViewController.getZutatenpaketPane(), droppedZutatenpaket, ablagebereichView, ablagebereich);
                            } else {
                                mainViewController.getPopUpErrorController().setErrorText(this.zutatenpaketplatzierung.getErrorMessage());
                                mainViewController.showPopUp(mainViewController.getPopUpErrorController().getPopUpError());
                            }

                        } catch (ObjektExistiertNichtException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        //wird innerhalb des regals verschoben
                        try {
                            //ablagebereich.getZutatenpakete().add(droppedZutatenpaket);
                            //set business logik
                            Ablageflaeche unterliegendeAblageflaeche = this.zutatenpaketplatzierung.getAblageflaeche(xPosMouse, yPosMouse, droppedZutatenpaket, ablagebereich);
                            if (zutatenpaketplatzierung.platziereZutatenpaket(droppedZutatenpaket, ablagebereich, unterliegendeAblageflaeche, xPosMouse, yPosMouse)) {
                                ZutatenpaketController zutatenpaketViewController = ViewBuilder.createZutatenpaketView(ablagebereichView, droppedZutatenpaket, this);
                                ViewBuilder.addZutatenPaketToView(ablagebereichView, zutatenpaketViewController);

                                zutatenpaketViewController.getZutatenpaketPane().setLayoutX(droppedZutatenpaket.getxPos());
                                zutatenpaketViewController.getZutatenpaketPane().setLayoutY(droppedZutatenpaket.getyPos());

                                // allow drag within regal
                                initDragInsideRegal(zutatenpaketViewController.getZutatenpaketPane(), droppedZutatenpaket, ablagebereichView, ablagebereich);
                            } else {
                                transferModeAccepted = false;
                                mainViewController.getPopUpErrorController().setErrorText(this.zutatenpaketplatzierung.getErrorMessage());
                                mainViewController.showPopUp(mainViewController.getPopUpErrorController().getPopUpError());
                            }

                        } catch (ObjektExistiertNichtException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    event.setDropCompleted(true);
                }
            } else if (dragboard.hasContent(zutatenpaketStapelFormat)) {
                if (event.getTransferMode() == TransferMode.MOVE) {
                    System.out.println("Zutatenpaketstapel importieren - 3");

                    ZutatenpaketStapel droppedStapel = (ZutatenpaketStapel) dragboard.getContent(zutatenpaketStapelFormat);

                    if (event.getTransferMode() == TransferMode.MOVE) {
                        //wird innerhalb des regals verschoben
                        try {
                            //set business logik
                            Ablageflaeche unterliegendeAblageflaeche = this.zutatenpaketplatzierung.getAblageflaeche(xPosMouse, yPosMouse, droppedStapel.getUnterstesZutatenpaket(), ablagebereich);
                            if (zutatenpaketplatzierung.platziereZutatenpaket(droppedStapel, ablagebereich, unterliegendeAblageflaeche, xPosMouse, yPosMouse)) {

                                for (Zutatenpaket zutatenpaket : droppedStapel.getZutatenpakete()) {
                                    zeichneZutatenPaket(zutatenpaket, ablagebereichView, ablagebereich);
                                }
                            } else {
                                transferModeAccepted = false;
                                mainViewController.getPopUpErrorController().setErrorText(this.zutatenpaketplatzierung.getErrorMessage());
                                mainViewController.showPopUp(mainViewController.getPopUpErrorController().getPopUpError());
                            }

                        } catch (ObjektExistiertNichtException e) {
                            throw new RuntimeException(e);
                        }
                        event.setDropCompleted(true);
                    }
                    event.setDropCompleted(true);
                }
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
    }

    private void zeichneZutatenPaket(Zutatenpaket zutatenpaket, Pane ablagebereichView, Ablagebereich ablagebereich){
        ZutatenpaketController zutatenpaketViewController = ViewBuilder.createZutatenpaketView(ablagebereichView, zutatenpaket, this);
        ViewBuilder.addZutatenPaketToView(ablagebereichView, zutatenpaketViewController);

        zutatenpaketViewController.getZutatenpaketPane().setLayoutX(zutatenpaket.getxPos());
        zutatenpaketViewController.getZutatenpaketPane().setLayoutY(zutatenpaket.getyPos());

        // allow drag within regal
        initDragInsideRegal(zutatenpaketViewController.getZutatenpaketPane(), zutatenpaket, ablagebereichView, ablagebereich);

    }

    private boolean kollidiert(Platzierbar zuPlatzierendesZutatenpaket, Zutatenpaket zuCheckendesZutatenpaket,
                               double posX, double posY) {
        return posX < zuCheckendesZutatenpaket.getxPos() + zuCheckendesZutatenpaket.getBreite() &&
                posX + zuPlatzierendesZutatenpaket.getBreite() > zuCheckendesZutatenpaket.getxPos() &&
                posY < zuCheckendesZutatenpaket.getyPos() + zuCheckendesZutatenpaket.getHoehe() &&
                posY + zuPlatzierendesZutatenpaket.getHoehe() > zuCheckendesZutatenpaket.getyPos();
    }


    public void initDragInsideRegal(StackPane zutatenpaketView, Zutatenpaket zutatenpaket,
                                    Pane aktuelleAblagebereichsView, Ablagebereich neuerAblagebereich) {
        //init drag detected -> 1
        zutatenpaketView.setOnDragDetected(event -> {
            Dragboard dragboard = zutatenpaketView.startDragAndDrop(TransferMode.MOVE);
            /*pack up data of the object*/
            ClipboardContent content = new ClipboardContent();
            // zutatenpaket oder zutatenstapel?
            if (zutatenpaket.isTopPaket()) {
                content.put(zutatenpaketFormat, zutatenpaket);
            } else {
                System.out.println("Zutatenpaketstapel wird verschoben - 1");
                List<Zutatenpaket> zutatenpaketStapel = neuerAblagebereich.findeAlleAufliegendenPakete(zutatenpaket);
                zutatenpaketStapel.add(zutatenpaket);
                ZutatenpaketStapel stapel = new ZutatenpaketStapel(zutatenpaketStapel, zutatenpaket);
                content.put(zutatenpaketStapelFormat, stapel);
            }
            dragboard.setContent(content);
            event.consume();
        });

        /*if drag was successful or not - clean up -> 4*/
        zutatenpaketView.setOnDragDone(event -> {
            if (event.getDragboard().hasContent(zutatenpaketFormat)) {
                if (event.getTransferMode() == TransferMode.MOVE) {
                    // löschen aus altem bereich (wenn erfolgreich platziert -> sonst an alter Stelle hinzufügen
                    if (transferModeAccepted) {
                        aktuelleAblagebereichsView.getChildren().remove(zutatenpaketView);
                        transferModeAccepted = false;
                    }
                }
            } else if (event.getDragboard().hasContent(zutatenpaketStapelFormat)) {
                System.out.println("Zutatenpaketstapel wird aufgeräumt - 4");

                if (event.getTransferMode() == TransferMode.MOVE) {
                    // löschen aus altem bereich (wenn erfolgreich platziert -> sonst an alter Stelle hinzufügen
                    if (transferModeAccepted) {
                        transferModeAccepted = false;
                        //alle nodes aus ablagebereich entfernen
                        while (!aktuelleAblagebereichsView.getChildren().isEmpty())
                            aktuelleAblagebereichsView.getChildren().removeFirst();
                        // erneut alle benötigten views anzeigen
                        for (Zutatenpaket alleZutatenpaketeAusStapel : neuerAblagebereich.getZutatenpakete()) {
                            ZutatenpaketController zutatenpaketViewController = ViewBuilder.createZutatenpaketView(aktuelleAblagebereichsView, alleZutatenpaketeAusStapel, this);
                            ViewBuilder.addZutatenPaketToView(aktuelleAblagebereichsView, zutatenpaketViewController);

                            zutatenpaketViewController.getZutatenpaketPane().setLayoutX(alleZutatenpaketeAusStapel.getxPos());
                            zutatenpaketViewController.getZutatenpaketPane().setLayoutY(alleZutatenpaketeAusStapel.getyPos());

                            // allow drag within regal
                            initDragInsideRegal(zutatenpaketViewController.getZutatenpaketPane(), alleZutatenpaketeAusStapel, aktuelleAblagebereichsView, neuerAblagebereich);
                        }

                    }
                }
            }
            event.consume();
        });
    }

    public void initDragRegalToWarenkorb(StackPane zutatenpaketImRegal, Zutatenpaket zutatenpaket) {
        /* drag event from a Zutatenpaket is found */
        zutatenpaketImRegal.setOnDragDetected(event -> {
            Dragboard dragboard = zutatenpaketImRegal.startDragAndDrop(TransferMode.MOVE);
            /* pack object data */
            ClipboardContent content = new ClipboardContent();
            content.put(zutatenpaketFormat, zutatenpaket);
            dragboard.setContent(content);
            event.consume();

            StackPane pane = (StackPane) event.getSource();
        });

        /* Clean up if drag was successful */
        zutatenpaketImRegal.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                zutatenpaketImRegal.setOpacity(0.5);
            }
        });
    }

    public void initDropRegalToWarenkorb(ImageView warenkorbView) {
        warenkorbView.setOnDragOver(event -> {
            if (event.getDragboard().hasContent(zutatenpaketFormat) && event.getTransferMode() == TransferMode.MOVE) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        warenkorbView.setOnDragDropped(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                Zutatenpaket zutatenpaket = (Zutatenpaket) event.getDragboard().getContent(zutatenpaketFormat);

                try {
                    this.entnahmeController.addZutatenpaketInWarenkorb(zutatenpaket);
                    System.out.println("Zutatenpaket " + zutatenpaket.getZutat().getName() + " in Warenkorb hinzugefügt");
                } catch (ObjektExistiertNichtException e) {
                    throw new RuntimeException(e);
                }
                event.setDropCompleted(true);
            } else event.setDropCompleted(false);
            event.consume();
        });
    }

    public void setZutatenpaketverwaltung(Zutatenpaketverwaltung zutatenpaketverwaltung) {
        this.zutatenpaketverwaltung = zutatenpaketverwaltung;
    }

    public void setRegalController(RegalController regalController) {
        this.regalController = regalController;
    }

    public void setRegalView(Pane regalView) {
        this.regalView = regalView;
    }

    public Zutatenpaketplatzierung getZutatenpaketplatzierung() {
        return zutatenpaketplatzierung;
    }

    public void setZutatenpaketplatzierung(Zutatenpaketplatzierung zutatenpaketplatzierung) {
        this.zutatenpaketplatzierung = zutatenpaketplatzierung;
    }

    public void setEntnahmeController(EntnahmeController entnahmeController) {
        this.entnahmeController = entnahmeController;
    }
}
