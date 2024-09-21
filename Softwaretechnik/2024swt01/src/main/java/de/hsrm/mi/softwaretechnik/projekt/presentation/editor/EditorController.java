package de.hsrm.mi.softwaretechnik.projekt.presentation.editor;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Regal;
import de.hsrm.mi.softwaretechnik.projekt.presentation.dragAndDrop.DragAndDrop;
import de.hsrm.mi.softwaretechnik.projekt.presentation.mainView.MainViewController;
import de.hsrm.mi.softwaretechnik.projekt.presentation.regal.RegalController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class EditorController {
    private Pane editorView;
    private RegalController regalController;

    public EditorController(MainViewController mainViewController, DragAndDrop dragAndDropEvent) {
        regalController = new RegalController(mainViewController, dragAndDropEvent);

        FXMLLoader editorViewLoader = new FXMLLoader(getClass().getResource("/editor/EditorView.fxml"));

        try {
            editorViewLoader.setController(regalController);
            editorView = editorViewLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public Pane initialize() {

        return editorView;
    }

    public Pane getEditorView() {
        return editorView;
    }

    public Pane getRegalView() {
        return regalController.getRegalView();
    }

    public Regal getRegal() {
        return regalController.getRegal();
    }

    public RegalController getRegalController() {
        return regalController;
    }
}
