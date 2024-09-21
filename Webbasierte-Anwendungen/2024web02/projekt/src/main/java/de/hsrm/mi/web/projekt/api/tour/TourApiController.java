package de.hsrm.mi.web.projekt.api.tour;

import de.hsrm.mi.web.projekt.services.tour.TourServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/api/tour")
@RestController
public class TourApiController {

    @Autowired
    private TourServiceImpl tourService;

    @GetMapping
    private List<TourDTO> getAllTourDTDs() {
        return tourService.holeAlleTouren().stream().map(TourDTO::fromTour).toList();
    }

}
