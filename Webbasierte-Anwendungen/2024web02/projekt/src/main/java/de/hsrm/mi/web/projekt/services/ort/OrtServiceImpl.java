package de.hsrm.mi.web.projekt.services.ort;

import de.hsrm.mi.web.projekt.entities.ort.Ort;
import de.hsrm.mi.web.projekt.entities.ort.OrtRepository;
import de.hsrm.mi.web.projekt.services.geo.GeoAdresse;
import de.hsrm.mi.web.projekt.services.geo.GeoService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class OrtServiceImpl implements OrtService{
    @Autowired
    private OrtRepository ortRepository;

    @Autowired
    private GeoService geoService;

    @Override
    public List<Ort> holeAlleOrte() {
        Sort sort = Sort.by(Sort.Order.asc("name"));
        List<Ort> ortListe = ortRepository.findAll(sort);
        log.info("Alle Orte (sortiert): {}", ortListe);
        return ortListe;
    }

    @Override
    public Optional<Ort> holeOrtMitId(long id) {
        if (ortRepository.existsById(id)) {
            log.info("Ort mit Id: {}", id);
            return ortRepository.findById(id);
        } else {
            log.info("Ort mit Id: {} existiert nicht", id);
            return Optional.empty();
        }
    }

    @Override
    public Ort speichereOrt(Ort o) {
        log.info("Speichere Ort: {}", o);
        return ortRepository.save(o);
    }

    @Override
    public void loescheOrtMitId(long id) {
        log.info("Lösche Ort mit Id: {}", id);
        ortRepository.deleteById(id);
    }

    @Override
    public List<Ort> findeOrtsvorschlaegeFuerAdresse(String ort) {
        List<GeoAdresse> geoAdressen = geoService.findeAdressen(ort);
        return geoAdressen.stream()
                .map(geoAdresse -> {
                    Ort neuerOrt = new Ort();
                    neuerOrt.setName(geoAdresse.display_name());
                    neuerOrt.setGeobreite(geoAdresse.lat());
                    neuerOrt.setGeolaenge(geoAdresse.lon());
                    return neuerOrt;
                }).collect(Collectors.toList());
    }
}
