package de.hsrm.mi.softwaretechnik.projekt;

import de.hsrm.mi.softwaretechnik.projekt.presentation.mainView.MainViewController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * @author Artur Konkel
 * @author Anna-Livia Martin
 * @author Sarah Schwarzer
 * @author Vivien Weber
 */
public class ProjektApplication extends Application {
    public final static double MAX_WIDTH = 1400;
    public final static double MAX_HEIGHT = 900;
    private Stage primaryStage;
    private Pane root;

    public static void main(String[] args) {
        launch();
    }

    /**
     * initialize the app
     */
    @Override
    public void init() {
    }

    public void mainWindow() {
        MainViewController mainViewController = new MainViewController();
        root = mainViewController.initialize();

        Scene scene = new Scene(root, MAX_WIDTH, MAX_HEIGHT - 20);
        // TODO in MainView muss entsprechend der Titel immer angepasst werden -> fx:id -> ueberschrift_lageransicht

        scene.getStylesheets().add((Objects.requireNonNull(getClass().getResource("/static/style.css")).toExternalForm()));

        primaryStage.setTitle("Pizza-Lager-Logistik");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }


    /**
     * starts the application
     *
     * @param primaryStage the primaryStage for this application, onto which the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be primaryStage stages.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mainWindow();
    }
}