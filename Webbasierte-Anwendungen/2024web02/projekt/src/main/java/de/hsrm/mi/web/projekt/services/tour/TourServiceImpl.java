package de.hsrm.mi.web.projekt.services.tour;

import de.hsrm.mi.web.projekt.entities.benutzer.Benutzer;
import de.hsrm.mi.web.projekt.entities.benutzer.BenutzerRepository;
import de.hsrm.mi.web.projekt.entities.ort.Ort;
import de.hsrm.mi.web.projekt.entities.ort.OrtRepository;
import de.hsrm.mi.web.projekt.entities.tour.Tour;
import de.hsrm.mi.web.projekt.entities.tour.TourRepository;
import de.hsrm.mi.web.projekt.messaging.FrontendNachrichtEvent;
import de.hsrm.mi.web.projekt.messaging.FrontendNachrichtService;
import de.hsrm.mi.web.projekt.messaging.NachrichtenTyp;
import de.hsrm.mi.web.projekt.messaging.Operation;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class TourServiceImpl implements TourService {

    @Autowired
    FrontendNachrichtService frontendNachrichtService;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private OrtRepository ortRepository;

    @Autowired
    private BenutzerRepository benutzerRepository;

    @Override
    public List<Tour> holeAlleTouren() {
        Sort sort = Sort.by(Sort.Order.asc("abfahrDateTime"));
        List<Tour> tours = this.tourRepository.findAll(sort);
        log.info("Alle Touren (zeitlich aufsteigend sortiert): {}", tours);
        return tours;
    }

    @Override
    public Optional<Tour> holeTourMitId(long id) {
        if (this.tourRepository.existsById(id)) {
            log.info("Tour mit ID: {}", id);
            frontendNachrichtService.sendEvent(new FrontendNachrichtEvent(NachrichtenTyp.TOUR, id, Operation.GET));
            return this.tourRepository.findById(id);
        } else {
            log.info("Tour mit ID: {} nicht gefunden", id);
            return Optional.empty();
        }
    }

    @Override
    public void loescheTourMitId(long id) {
        log.info("Lösche Tour mit ID: {}", id);
        this.tourRepository.deleteById(id);
        frontendNachrichtService.sendEvent(new FrontendNachrichtEvent(NachrichtenTyp.TOUR, id, Operation.DELETE));
    }

    @Override
    public List<Benutzer> holeAlleBenutzer() {
        Sort sort = Sort.by(Sort.Order.asc("name"));
        List<Benutzer> anbieter = this.benutzerRepository.findAll(sort);
        log.info("Alle Anbieter (nach Namen aufsteigend sortiert): {}", anbieter);
        return anbieter;
    }

    @Override
    public List<Ort> holeAlleOrte() {
        Sort sort = Sort.by(Sort.Order.asc("name"));
        List<Ort> orte = this.ortRepository.findAll(sort);
        log.info("Alle Orte (alphabetisch sortiert): {}", orte);
        return orte;
    }

    @Override
    public Tour speichereTourAngebot(long benutzerId, long startOrtId, long zielOrtId, Tour tour) {

        // Laden der Benutzer- und Ort-Entitäten basierend auf den IDs
        Optional<Benutzer> optionalAnbieter = benutzerRepository.findById(benutzerId);
        Optional<Ort> optionalStartOrt = ortRepository.findById(startOrtId);
        Optional<Ort> optionalZielOrt = ortRepository.findById(zielOrtId);

        if (optionalAnbieter.isEmpty() || optionalStartOrt.isEmpty() || optionalZielOrt.isEmpty()) {
            throw new IllegalArgumentException("Ein oder mehrere der angegebenen IDs sind nicht vorhanden.");
        }

        Benutzer anbieter = optionalAnbieter.get();
        Ort startOrt = optionalStartOrt.get();
        Ort zielOrt = optionalZielOrt.get();

        tour.setBenutzer(anbieter);
        tour.setStartOrt(startOrt);
        tour.setZielOrt(zielOrt);

        // Speichern der Tour in einer Transaktion
        return this.tourRepository.save(tour);
    }
}
