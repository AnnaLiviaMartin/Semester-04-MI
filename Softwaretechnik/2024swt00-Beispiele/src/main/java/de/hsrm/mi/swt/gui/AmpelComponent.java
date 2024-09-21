package de.hsrm.mi.swt.gui;

import java.beans.PropertyChangeEvent;
import java.io.IOException;

import de.hsrm.mi.swt.anwendungslogik.AmpelModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * AmpelComponent zeigt Zustand des übergebenen AmpelModel an
 * und bietet Knöpfe zum Zurücksetzen / Weiterschalten.
 */
public class AmpelComponent extends VBox  {
	private AmpelModel ampelModel;

	@FXML
	private Circle rot;
	@FXML
	private Circle gelb;
	@FXML
	private Circle gruen;
		
	/**	
	 * Initialisierung der AmpelComponent
	 *
	 * zugehöriges Layout in FXML-Datei ausgelagert, diese ist unter
 	 * src/main/resources anzulegen und kann dann mit getResource() 
 	 * geladen werden.
     *
	 * @param m anzuzeigendes AmpelModel
	 * @throws IOException
	 */
	public AmpelComponent(AmpelModel m) throws IOException {
		this.ampelModel = m;
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/AmpelViewDing.fxml"));
		loader.setController(this);
		
		Node o = loader.load();
		this.getChildren().add(o);
	}


	/**
	 * Änderungs-Listener auf Model-Properties setzen, damit die AmpelComponent
	 * auf Änderungen im unterliegenden Model reagiert
	 */
	@FXML
	private void initialize() {
		// (Unbekannten!) Anfangszustand des darzustellenden Objekts 'ampelModel' uebernehmen
		rot.setFill(  ampelModel.isRot()?   Color.RED :    Color.LIGHTGRAY);
		gelb.setFill( ampelModel.isGelb()?  Color.ORANGE : Color.LIGHTGRAY);
		gruen.setFill(ampelModel.isGruen()? Color.GREEN :   Color.LIGHTGRAY);

		// auf kuenftige Aenderungsnachrichten (PropertyChangeEvents) reagieren
		ampelModel.addPropertyChangeListener( (PropertyChangeEvent e) -> {
			System.out.println(String.format("AmpelComponent: Event %-6s: %-5s -> %-5s ",e.getPropertyName(),e.getOldValue(),e.getNewValue()));
			if (e.getPropertyName().contentEquals(AmpelModel.ROTNAME))   rot.setFill((boolean)   e.getNewValue()? Color.RED : Color.LIGHTGRAY);
			if (e.getPropertyName().contentEquals(AmpelModel.GELBNAME))  gelb.setFill((boolean)  e.getNewValue()? Color.ORANGE: Color.LIGHTGRAY);
			if (e.getPropertyName().contentEquals(AmpelModel.GRUENNAME)) gruen.setFill((boolean) e.getNewValue()? Color.GREEN : Color.LIGHTGRAY);
		});
	}


	/**
	 * Callback für "tick"-Button aus FXML - AmpelModel weiterschalten lassen,
	 * Anzeige sollte sich automatisch anpassen - NICHT aktiv von hier aus Circles einfärben
	 */
	@FXML
	protected void tick() {
		System.out.println("\nAmpelComponent: tick()");
		ampelModel.tick();
	}

	/**
	 * Callback für "resest"-Button aus FXML - AmpelModel zurücksetzen,
	 * Anzeige sollte sich automatisch anpassen - NICHT aktiv von hier aus Circles einfärben
	 */
	@FXML
	protected void reset() {
		System.out.println("\nAmpelComponent: reset()");
		ampelModel.reset();
	}
		
}
