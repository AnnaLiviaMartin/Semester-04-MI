package de.hsrm.mi.web.projekt.services.geo;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GeoService {
    List<GeoAdresse> findeAdressen(String ort);
}
