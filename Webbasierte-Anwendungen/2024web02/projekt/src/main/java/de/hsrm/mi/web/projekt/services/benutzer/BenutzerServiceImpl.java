package de.hsrm.mi.web.projekt.services.benutzer;

import de.hsrm.mi.web.projekt.entities.benutzer.Benutzer;
import de.hsrm.mi.web.projekt.entities.benutzer.BenutzerRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class BenutzerServiceImpl implements BenutzerService {
    @Autowired
    private BenutzerRepository benutzerRepository;

    @Autowired
    private Validator validator;

    public List<Benutzer> holeAlleBenutzer() {
        Sort sort = Sort.by(Sort.Order.asc("name"), Sort.Order.asc("email"));
        List<Benutzer> benutzerListe = benutzerRepository.findAll(sort);
        log.info("Alle Benutzer (sortiert): {}", benutzerListe);
        return benutzerListe;
    }

    public Optional<Benutzer> holeBenutzerMitId(long id) {
        if (benutzerRepository.existsById(id)) {
            log.info("Benutzer mit Id: {}", id);
            return benutzerRepository.findById(id);
        } else {
            log.info("Benutzer mit Id: {} existiert nicht", id);
            return Optional.empty();
        }
    }

    public Benutzer speichereBenutzer(Benutzer b) {
        log.info("Speichere Benutzer: {}", b);
        return benutzerRepository.save(b);
    }

    public void loescheBenutzerMitId(long id) {
        log.info("Lösche Benutzer mit Id: {}", id);
        benutzerRepository.deleteById(id);
    }

    @Override
    public Benutzer aktualisiereBenutzerAttribut(long id, String feldname, String wert) {
        log.info("Aktualisiere Benutzer mit id: {}", id);
        Optional<Benutzer> potenziellExistierenderBenutzer = benutzerRepository.findById(id);
        if (potenziellExistierenderBenutzer.isPresent()) {
            //aktualisiere
            Benutzer benutzer = potenziellExistierenderBenutzer.get();
            if (feldname.equals("email")) {
                benutzer.setEmail(wert);
            } else if (feldname.equals("name")) {
                benutzer.setName(wert);
            }

            // Validierung durchführen
            Set<ConstraintViolation<Benutzer>> violations = validator.validate(benutzer);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }

            //speichere
            return benutzerRepository.save(benutzer);
        }
        return null;
    }
}
