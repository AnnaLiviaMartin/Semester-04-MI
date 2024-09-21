package de.hsrm.mi.web.projekt.ui.tour;

import de.hsrm.mi.web.projekt.entities.benutzer.Benutzer;
import de.hsrm.mi.web.projekt.entities.ort.Ort;
import de.hsrm.mi.web.projekt.entities.tour.Tour;
import de.hsrm.mi.web.projekt.messaging.FrontendNachrichtEvent;
import de.hsrm.mi.web.projekt.messaging.FrontendNachrichtService;
import de.hsrm.mi.web.projekt.messaging.NachrichtenTyp;
import de.hsrm.mi.web.projekt.messaging.Operation;
import de.hsrm.mi.web.projekt.services.tour.TourServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/admin")
@SessionAttributes(names = {"tourform"})
public class TourController {

    @Autowired
    private TourServiceImpl tourService;

    @Autowired
    FrontendNachrichtService frontendNachrichtService;

    @ModelAttribute("tourform")
    public TourFormular initTourFormular() {
        return new TourFormular();
    }

    @GetMapping
    public String adminShortcut() {
        return "redirect:/admin/tour";
    }

    @GetMapping("/tour")
    public String tourListe(Model model) {
        List<Tour> tourListe = tourService.holeAlleTouren();
        model.addAttribute("tourListe", tourListe);
        return "tour/tourliste";
    }

    @GetMapping("/tour/{id}")
    public String tourBearbeiten(@PathVariable long id,
                                 Model model,
                                 @ModelAttribute("tourform") TourFormular tourFormular) {

        List<Benutzer> benutzerListe = tourService.holeAlleBenutzer();
        List<Ort> ortListe = tourService.holeAlleOrte();
        model.addAttribute("benutzerListe", benutzerListe);
        model.addAttribute("ortListe", ortListe);

        if (id == 0) {
            model.addAttribute("tour", new Tour());
            model.addAttribute("id", id);
            this.resetTourContent(tourFormular);
            return "tour/tourbearbeiten";
        } else if (id > 0) {
            Optional<Tour> potentielleTour = tourService.holeTourMitId(id);
            if (potentielleTour.isPresent()) {
                Tour tour = potentielleTour.get();
                model.addAttribute("tour", tour);
                tourFormular.fromTour(tour);
                model.addAttribute("id", id);
                return "tour/tourbearbeiten";
            }
        }

        return "tour/tourbearbeiten";
    }

    @PostMapping("/tour/{id}")
    public String updateTourBearbeiten(@PathVariable long id,
                                       @Valid @ModelAttribute("tourform") TourFormular tourFormular,
                                       BindingResult formErrors,
                                       @ModelAttribute("tour") Tour tourSession,
                                       Model model) {
        log.info("Update Tour: {}", tourFormular);

        if (formErrors.hasErrors()){
            logAllErrors(formErrors);
            return "tour/tourbearbeiten";
        }

        try {
            // Speichern der Tour mit den neuen Beziehungen
            Tour tour = (Tour) model.getAttribute("tour");
            if (tour != null) tourFormular.toTour(tour);

            tour = tourService.speichereTourAngebot(
                    tourFormular.getAnbieter().getId(),
                    tourFormular.getStartOrt().getId(),
                    tourFormular.getZielOrt().getId(),
                    tour);

            // um auf sicher zu gehen, dass die Tour auch wirklich gespeichert wurde, wird die Message erst hier gesendet
            frontendNachrichtService.sendEvent(new FrontendNachrichtEvent(NachrichtenTyp.TOUR, tour.getId(), Operation.CREATE));
            model.addAttribute("tour", tour);
            return "redirect:/admin/tour/" + tour.getId();
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            return handleSavingExceptions(e, model);
        }
    }

    @GetMapping("/tour/{id}/del")
    public String loescheTour(@PathVariable long id) {
        Optional<Tour> optionalTour = tourService.holeTourMitId(id);
        if(optionalTour.isPresent()){
            Tour tour = optionalTour.get();
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            String loginEmail = authentication.getName();
            if(loginEmail.equals(tour.getBenutzer().getEmail())){
                tourService.loescheTourMitId(id);
            } else {
                log.error("Das kann nicht gelöscht werden! Du bist kein Eigentümer dieser Tour.");
            }
        }
        return "redirect:/admin/tour";
    }

    // TODO separat in eine Klasse, wird von allen Controllern genutzt
    private void logAllErrors(BindingResult bindingResult) {
        List<ObjectError> errors = bindingResult.getAllErrors();
        for (ObjectError error : errors) {
            log.error("Error while entering value: " + error.getDefaultMessage());
        }
    }

    // TODO separat in eine Klasse, wird von allen Controllern genutzt
    private String handleSavingExceptions(RuntimeException e, Model model) {
        model.addAttribute("info", e.getMessage());
        log.error(e.getMessage());
        return "tour/tourbearbeiten";
    }

    private void resetTourContent(TourFormular tourFormular) {
        tourFormular.setAnbieter(null);
        tourFormular.setAbfahrDateTime(null);
        tourFormular.setPreis(0);
        tourFormular.setPlaetze(0);
        tourFormular.setStartOrt(null);
        tourFormular.setZielOrt(null);
        tourFormular.setInfo("");
    }
}
