package de.hsrm.mi.web.projekt.services.benutzer;

import de.hsrm.mi.web.projekt.entities.benutzer.Benutzer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface BenutzerService {
    List<Benutzer> holeAlleBenutzer();
    Optional<Benutzer> holeBenutzerMitId(long id);
    Benutzer speichereBenutzer(Benutzer b);
    void loescheBenutzerMitId(long id);
    Benutzer aktualisiereBenutzerAttribut(long id, String feldname, String wert);       //aktualisiert den Benutzer mit id den feldnamen mit neuem wert + zurücksetzen Benutzerobjekt
}
