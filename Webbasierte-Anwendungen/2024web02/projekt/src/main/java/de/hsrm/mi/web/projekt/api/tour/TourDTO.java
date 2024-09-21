package de.hsrm.mi.web.projekt.api.tour;

import de.hsrm.mi.web.projekt.entities.tour.Tour;
import de.hsrm.mi.web.projekt.services.tour.GeoDistanz;

import java.time.LocalDate;

public record TourDTO(long id, LocalDate abfahrDateTime, int preis, int plaetze, int buchungen,
                      String startOrtName, long startOrtId, String zielOrtName, long zielOrtId,
                      String anbieterName, long anbieterId, double distanz, String info) {

    public static TourDTO fromTour(Tour t) {
        double distanzStartUndZielort = GeoDistanz.calculateDistance(
                t.getStartOrt().getGeolaenge(), t.getStartOrt().getGeobreite(),
                t.getZielOrt().getGeolaenge(), t.getZielOrt().getGeobreite());

        return new TourDTO(t.getId(), t.getAbfahrDateTime(), t.getPreis(), t.getPlaetze(), t.getMitfahrgaeste().size(),
                t.getStartOrt().getName(), t.getStartOrt().getId(), t.getZielOrt().getName(), t.getZielOrt().getId(),
                t.getBenutzer().getName(), t.getBenutzer().getId(), distanzStartUndZielort, t.getInfo());
    }
}