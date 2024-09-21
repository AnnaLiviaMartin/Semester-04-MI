package de.hsrm.mi.web.projekt.ui.ort;

import de.hsrm.mi.web.projekt.entities.ort.Ort;
import de.hsrm.mi.web.projekt.services.ort.OrtServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/admin/ort")
@SessionAttributes(names = {"ortform", "ort"})
public class OrtController {
    @Autowired
    private OrtServiceImpl ortService;

    @ModelAttribute("ortform")
    public OrtFormular initOrtForm() {
        return new OrtFormular();
    }

    @GetMapping("/{id}")
    public String ortBearbeiten(@PathVariable long id,
                                Model model,
                                @ModelAttribute("ortform") OrtFormular ortFormular) {
        if (id == 0) {
            Ort neuerOrt = new Ort();
            model.addAttribute("ort", neuerOrt);
            model.addAttribute("id", id);
            resetFormularContent(ortFormular);
        } else if (id > 0) {
            Optional<Ort> potenziellExistierenderOrt = ortService.holeOrtMitId(id);
            if (potenziellExistierenderOrt.isPresent()) {
                Ort existierenderOrt = potenziellExistierenderOrt.get();
                model.addAttribute("ort", existierenderOrt);
                ortFormular.fromOrt(existierenderOrt);
                model.addAttribute("id", id);
            }
        }
        return "ort/ortbearbeiten";
    }

    private void resetFormularContent(OrtFormular ortFormular) {
        ortFormular.setName("");
        ortFormular.setGeobreite(0.0);
        ortFormular.setGeolaenge(0.0);
    }

    @PostMapping("/{id}")
    public String updateOrtBearbeiten(@PathVariable long id,
                                      @Valid @ModelAttribute("ortform") OrtFormular ortFormular,
                                      BindingResult formErrors,
                                      @ModelAttribute("ort") Ort ortSession,
                                      Model model) {
        log.info("Update Ort: {}", ortFormular);

        if (formErrors.hasErrors()) {
            logAllErrors(formErrors);
            return "ort/ortbearbeiten"; //bleibe auf gleicher Seite für Fehleranzeige
        }

        //wenn neuer Ort erstellt + Ortsname eingegeben + Breite/Länge = 0.0 -> Breite/Länge holen
        //todo aktuell ist es so dass man die breite und länge ändern MUSS um den Ort speichern zu können wenn er nicht in Realität existiert -> soll das so? ist auf jeden Fall so am einfachsten?!
        if (id == 0 && !ortFormular.getName().equals("") && ortFormular.getGeobreite() == 0.0 && ortFormular.getGeolaenge() == 0.0) {
            //formular füllen + nicht in db speichern
            List<Ort> answer = ortService.findeOrtsvorschlaegeFuerAdresse(ortFormular.getName());
            if (answer.isEmpty()){
                //fehler ausgeben
                model.addAttribute("info", "Keinen Vorschlag gefunden!");
            } else {
                //formular füllen
                Ort firstOrt = answer.get(0);
                ortFormular.setGeobreite(firstOrt.getGeobreite());
                ortFormular.setGeolaenge(firstOrt.getGeolaenge());
                //info ausgeben
                model.addAttribute("info", "Vorschlag bitte bestätigen oder ändern.");
            }
            return "ort/ortbearbeiten";
        } else {
            Ort ort = (Ort) model.getAttribute("ort");
            if (ort != null) ortFormular.toOrt(ort);
            try {
                ort = ortService.speichereOrt(ort);
                model.addAttribute("ort", ort);
                //lade Seite neu
                return "redirect:/admin/ort/" + ort.getId();
            } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
                model.addAttribute("info", "Es ist ein Fehler beim speichern aufgetreten. Bitte laden Sie die Seite neu!");
                log.error(e.getMessage());
                return "ort/ortbearbeiten"; //bleibe auf gleicher Seite für Fehleranzeige
            }
        }
    }

    private void logAllErrors(BindingResult bindingResult) {
        List<ObjectError> errors = bindingResult.getAllErrors();
        for (ObjectError error : errors) {
            log.error("Error while entering value: " + error.getDefaultMessage());
        }
    }

    @GetMapping
    public String ortListe(Model model) {
        List<Ort> ortListe = ortService.holeAlleOrte();
        model.addAttribute("ortListe", ortListe);
        return "ort/ortliste";
    }

    @GetMapping("/{id}/del")
    public String loescheOrt(@PathVariable long id) {
        ortService.loescheOrtMitId(id);
        return "redirect:/admin/ort";
    }
}
