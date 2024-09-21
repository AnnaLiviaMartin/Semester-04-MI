package de.hsrm.mi.web.projekt.api.ort;

import de.hsrm.mi.web.projekt.entities.ort.Ort;
import de.hsrm.mi.web.projekt.services.ort.OrtServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping("/api/ort")
@RestController
public class OrtAPIController {

    @Autowired
    private OrtServiceImpl ortService;

    @GetMapping
    public List<OrtDTO> getAllOrtDTOs() {
        final List<Ort> ortList = ortService.holeAlleOrte();
        log.info("Alle Orte: {}", ortList);
        return ortList.stream().map(OrtDTO::fromOrt).toList();
    }

    @GetMapping("/{id}")
    public OrtDTO getOrtDTO(@PathVariable("id") long id) {
        Optional<Ort> optionalOrt = ortService.holeOrtMitId(id);
        log.info("Ort {} mit ID: {}", optionalOrt, id);
        if (optionalOrt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ort nicht gefunden");
        }

        return OrtDTO.fromOrt(optionalOrt.get());
    }
}
