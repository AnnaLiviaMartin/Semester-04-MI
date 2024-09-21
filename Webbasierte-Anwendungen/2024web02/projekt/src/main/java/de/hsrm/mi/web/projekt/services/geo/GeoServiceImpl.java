package de.hsrm.mi.web.projekt.services.geo;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class GeoServiceImpl implements GeoService{
    private String baseUri = "https://nominatim.openstreetmap.org";
    private String uri = "/search?format=json&countrycodes=de&q=";

    @Override
    public List<GeoAdresse> findeAdressen(String ort) {
        if(ort == null){
            log.error("Ort is null");
            return new ArrayList<GeoAdresse>();
        }

        //get the webclients answer
        WebClient client = WebClient.create(baseUri);
        List <GeoAdresse> geoAdressen = client.get()
                .uri(uri + ort)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(GeoAdresse.class)
                .collectList()
                .block();

        return geoAdressen.stream()
                .filter(adresse -> adresse.addresstype().equals("city")
                        || adresse.addresstype().equals("town")
                        || adresse.addresstype().equals("village"))
                .collect(Collectors.toList());
    }
}
