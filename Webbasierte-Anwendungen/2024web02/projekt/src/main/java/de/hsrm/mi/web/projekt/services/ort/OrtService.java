package de.hsrm.mi.web.projekt.services.ort;

import de.hsrm.mi.web.projekt.entities.ort.Ort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface OrtService {
    List<Ort> holeAlleOrte();
    Optional<Ort> holeOrtMitId(long id);
    Ort speichereOrt(Ort o);
    void loescheOrtMitId(long id);
    List<Ort> findeOrtsvorschlaegeFuerAdresse(String ort);
}
