package de.hsrm.mi.softwaretechnik.projekt.presentation.mainView;

import de.hsrm.mi.softwaretechnik.projekt.business.Utilities;
import de.hsrm.mi.softwaretechnik.projekt.business.services.Zutatenpaketplatzierung;
import de.hsrm.mi.softwaretechnik.projekt.business.services.ZutatenpaketverwaltungImpl;
import de.hsrm.mi.softwaretechnik.projekt.presentation.ViewBuilder;
import de.hsrm.mi.softwaretechnik.projekt.presentation.dragAndDrop.DragAndDrop;
import de.hsrm.mi.softwaretechnik.projekt.presentation.editor.EditorController;
import de.hsrm.mi.softwaretechnik.projekt.presentation.entnahme.*;
import de.hsrm.mi.softwaretechnik.projekt.presentation.platzieren.*;
import de.hsrm.mi.softwaretechnik.projekt.presentation.regal.RegalController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.IOException;

public class MainViewController {
    private Pane mainView, regalView, editorView, platzierenView, entnahmeView, warenkorbView, neueZutatView,
    popUpErrorView, popUpInfoView;
    private EditorController editorController;
    private EntnahmeController entnahmeController;
    private PlatzierenController platzierenController;
    private WarenkorbController warenkorbController;
    private PopUpErrorController popUpErrorController;
    private PopUpInfoController popUpInfoController;
    private RegalController regalController;
    private NeueZutatController neueZutatController;

    //TODO darf nicht Impl sein, sondern das Interface!
    private ZutatenpaketverwaltungImpl zutatenpaketverwaltung;
    private Zutatenpaketplatzierung zutatenpaketplatzierung;
    private BorderPane arbeitsbereich;
    private DragAndDrop dragAndDropEvent;

    @FXML
    private AnchorPane mainContainer;

    @FXML
    private AnchorPane headerline;

    @FXML
    private Text ueberschriftLageransicht;

    public MainViewController() {
        dragAndDropEvent = new DragAndDrop(this);
        FXMLLoader mainViewLoader = new FXMLLoader(getClass().getResource("/mainView/MainView.fxml"));
        try {
            mainViewLoader.setController(this);
            mainView = mainViewLoader.load();
            arbeitsbereich = (BorderPane) mainView.lookup("#arbeitsbereich");

            loadEditorBearbeitungsView();
            loadRegalView();
            loadZutatenpaketVerwaltung();
            loadWarenkorbPopUp();
            loadNeueZutatPopUp();
            loadPlatzierenView();
            loadEntnahmeView();
            loadErrorPopUp();
            loadInfoPopUp();

            entnahmeController.setWarenkorbController(warenkorbController);
            platzierenController.setDragAndDrop(dragAndDropEvent);
            dragAndDropEvent.setZutatenpaketverwaltung(zutatenpaketverwaltung);
            dragAndDropEvent.setRegalController(regalController);
            dragAndDropEvent.setZutatenpaketplatzierung(zutatenpaketplatzierung);
            dragAndDropEvent.setRegalView(regalController.getRegalView());
            dragAndDropEvent.setEntnahmeController(entnahmeController);
            dragAndDropEvent.initDropRegalToWarenkorb(entnahmeController.getWarenkorbIcon());

            Utilities utils = new Utilities(this.getZutatenpaketverwaltung());
            ViewBuilder builder = new ViewBuilder(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public Pane initialize() {

        return mainView;
    }


    @FXML
    private void wechsleZuEditor() {

        arbeitsbereich.setRight(editorView);
        setzeUberschrift("Editor");
    }

    @FXML
    private void wechsleZuEntnahme() {

        this.arbeitsbereich.setRight(entnahmeView);
        setzeUberschrift("Entnahme");
    }

    @FXML
    private void wechsleZuPlatzieren() {

        arbeitsbereich.setRight(platzierenView);
        setzeUberschrift("Platzieren");
    }

    private void setzeUberschrift(String ueberschrift) {
        ueberschriftLageransicht.setText(ueberschrift);
    }

    private void loadEditorBearbeitungsView() {
        editorController = new EditorController(this, dragAndDropEvent);
        editorView = editorController.getEditorView();
        this.regalController = editorController.getRegalController();

        arbeitsbereich.setRight(editorView);
    }

    private void loadRegalView() {
        regalView = editorController.getRegalView();

        arbeitsbereich.setCenter(regalView);
    }

    private void loadPlatzierenView() {
        platzierenController = new PlatzierenController(zutatenpaketverwaltung, this);
        platzierenView = platzierenController.getPlatzierenView();
        zutatenpaketplatzierung = platzierenController.getZutatenpaketPlatzierung();
    }

    private void loadZutatenpaketVerwaltung() {
        zutatenpaketverwaltung = new ZutatenpaketverwaltungImpl(editorController.getRegal());
    }

    private void loadEntnahmeView() {
        entnahmeController = new EntnahmeController(this, zutatenpaketverwaltung, this.dragAndDropEvent);
        entnahmeView = entnahmeController.getEntnahmeView();
    }

    private void loadWarenkorbPopUp() {
        this.warenkorbController = new WarenkorbController(this);
        this.warenkorbView = warenkorbController.getWarenkorbView();
        this.mainContainer.getChildren().add(this.warenkorbView);
        this.warenkorbView.setVisible(false);
    }

    private void loadErrorPopUp() {
        this.popUpErrorController = new PopUpErrorController(this);
        this.popUpErrorView = popUpErrorController.getPopUpError();
        this.mainContainer.getChildren().add(this.popUpErrorView);
        this.popUpErrorView.setVisible(false);
    }

    private void loadInfoPopUp(){
        this.popUpInfoController = new PopUpInfoController(this);
        this.popUpInfoView = popUpInfoController.getPopUpInfo();
        this.mainContainer.getChildren().add(this.popUpInfoView);
        this.popUpInfoView.setVisible(false);
    }

    private void loadNeueZutatPopUp() {
        this.neueZutatController = new NeueZutatController(this);
        this.neueZutatView = neueZutatController.getNeueZutatView();
        this.mainContainer.getChildren().add(this.neueZutatView);
        this.neueZutatView.setVisible(false);
    }

    public Pane whichPopUp(Pane whichPopUp) {
        if (whichPopUp.equals(this.warenkorbView)) {
            return this.warenkorbView;
        } else if (whichPopUp.equals(this.neueZutatView)) {
            return this.neueZutatView;
        } else if (whichPopUp.equals(this.popUpErrorView)) {
            return this.popUpErrorView;
        } else if(whichPopUp.equals(this.popUpInfoView)) {
            return this.popUpInfoView;
        }
        else return null;
    }

    public void showPopUp(Pane whichPopUp) {
        Pane popUp = this.whichPopUp(whichPopUp);

        if (popUp != null) {
            Scene scene = this.mainView.getScene();

            double centerX = (scene.getWidth() - popUp.getPrefWidth()) / 2;
            double centerY = (scene.getHeight() - popUp.getPrefHeight()) / 2;

            popUp.setLayoutX(centerX);
            popUp.setLayoutY(centerY);

            BoxBlur blur = new BoxBlur(10, 10, 3);
            this.arbeitsbereich.setEffect(blur);
            this.headerline.setEffect(blur);
            popUp.setVisible(true);
        }
    }

    public void hidePopUp(Pane whichPopUp) {
        Pane popUp = this.whichPopUp(whichPopUp);

        if (popUp != null) {
            this.arbeitsbereich.setEffect(null);
            this.headerline.setEffect(null);
            popUp.setVisible(false);
        }
    }

    public Pane getWarenkorbView() {
        return warenkorbView;
    }

    public Pane getNeueZutatView() {
        return neueZutatView;
    }

    public ZutatenpaketverwaltungImpl getZutatenpaketverwaltung() {
        return zutatenpaketverwaltung;
    }

    public PlatzierenController getPlatzierenController() {
        return platzierenController;
    }

    public PopUpErrorController getPopUpErrorController() {
        return popUpErrorController;
    }

    public PopUpInfoController getPopUpInfoController() {
        return popUpInfoController;
    }
}
