package de.hsrm.mi.swt.gui;

import de.hsrm.mi.swt.anwendungslogik.AmpelModel;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Hauptfenster für Ampel-Anzeige, bettet im Wesentlichen
 * eine AmpelComponent zur Visualisierung des AmpelModel ein.
 */
public class AmpelFxApplication extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		AmpelModel model = new AmpelModel();
		AmpelComponent ampelcomp = new AmpelComponent(model);
		
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		
		vbox.getChildren().addAll(
				new Label("Hier kommt die unpolitische Java-Ampel:"),
				ampelcomp,
				new Label("... das war die Ampel.")
		);
		
		primaryStage.setScene(new Scene(vbox));
		primaryStage.setTitle("AmpelApp");
		primaryStage.show();
		
	}

}
