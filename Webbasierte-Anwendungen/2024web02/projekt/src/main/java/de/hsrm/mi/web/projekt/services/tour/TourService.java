package de.hsrm.mi.web.projekt.services.tour;

import de.hsrm.mi.web.projekt.entities.benutzer.Benutzer;
import de.hsrm.mi.web.projekt.entities.ort.Ort;
import de.hsrm.mi.web.projekt.entities.tour.Tour;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TourService {
    List<Tour> holeAlleTouren();
    Optional<Tour> holeTourMitId(long id);
    void loescheTourMitId(long id);
    List<Benutzer> holeAlleBenutzer();
    List<Ort> holeAlleOrte();
    Tour speichereTourAngebot(long anbieterId, long startOrtId, long zielOrtId, Tour tour);
}
