package de.hsrm.mi.swt.anwendungslogik;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AmpelModel {

	// Property-Namen zur tippfehlerfreien Verwendung in AmpelComponent
	public static final String ROTNAME = "rot";
	public static final String GELBNAME = "gelb";
	public static final String GRUENNAME = "gruen";

	/* 
	 * Die AmpelComponent UI reagiert auf diese Properties,
	 * um seine Anzeige zu aktualisieren
	 */
	private final PropertyChangeSupport propChgSupport = new PropertyChangeSupport(this);
	
	/**
	 * Ampelfarbenwechsel beobachtbar machen - PropertyChangeListener registrieren
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.propChgSupport.addPropertyChangeListener(listener);
	}
	
	@Override
	public String toString() {
		return "AmpelModel [rot=" + isRot() + ", gelb=" + isGelb() + ", gruen=" + isGruen() + "]";
	}

	/*
	 * ab hier "fachliche" Ampel-Methoden
	 */

	public boolean isRot() {

	}
		
	public boolean isGelb() {

	}

	public boolean isGruen() {

	}

	/**
	 * Ampel auf Phase "rot" setzen
	 */
	public void reset() {


	}
	
	
	/**
	 * Ampel auf die nächste Phase weiterschalten
	 */
	public void tick() {


	}
	
}
