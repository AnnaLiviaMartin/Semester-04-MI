package de.hsrm.mi.web.projekt.ui.benutzer;

import de.hsrm.mi.web.projekt.entities.benutzer.Benutzer;
import de.hsrm.mi.web.projekt.services.benutzer.BenutzerServiceImpl;
import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin/benutzer")
@SessionAttributes(names = {"userform", "benutzer"})    //zur Bearbeitung aus db geholter Benutzer
public class BenutzerController {
    @Autowired
    private BenutzerServiceImpl benutzerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /* @ModelAttribute is opened before Requesthandler-Methods */
    @ModelAttribute("userform")
    public BenutzerFormular initUserForm() {
        return new BenutzerFormular();
    }

    @GetMapping("/{id}")
    public String benutzerBearbeiten(@PathVariable long id,
                                     Model model,
                                     @ModelAttribute("userform") BenutzerFormular benutzerFormular,
                                     Locale locale) {
        model.addAttribute("sprache", locale.getDisplayLanguage());

        if (id == 0) {      //wenn id = 0 → neuer Benutzer wird angelegt
            Benutzer neuerBenutzer = new Benutzer();
            model.addAttribute("benutzer", neuerBenutzer);
            model.addAttribute("id", id);
            resetFormularContent(benutzerFormular);
            return "benutzer/benutzerbearbeiten";     //ausgeben "Neues Benutzerprofil" in html
        } else if (id > 0) {            //sonst wird Info von bestehender id geladen
            Optional<Benutzer> potenziellExistierenderBenutzer = benutzerService.holeBenutzerMitId(id);
            if (potenziellExistierenderBenutzer.isPresent()) {
                Benutzer existierenderBenutzer = potenziellExistierenderBenutzer.get();
                model.addAttribute("benutzer", existierenderBenutzer);      //db user in session speichern
                benutzerFormular.fromBenutzer(existierenderBenutzer);                   //benutzerformular mit inhalt der db füllen
                model.addAttribute("id", id);
                return "benutzer/benutzerbearbeiten";           //zurückgeben ausgefülltes formular
            }
        }
        return "benutzer/benutzerbearbeiten";
    }

    private void resetFormularContent(BenutzerFormular benutzerFormular) {
        benutzerFormular.setName("");
        benutzerFormular.setPassword("");
        benutzerFormular.setInputDoNotLike("");
        benutzerFormular.setInputLike("");
        benutzerFormular.setEmail("");
        benutzerFormular.setDateOfBirth(null);
        benutzerFormular.setDoNotLike(new HashSet<>());
        benutzerFormular.setLikeThings(new HashSet<>());
    }

    @PostMapping("/{id}")
    public String updateBenutzerBearbeiten(@PathVariable long id,
                                           @Valid @ModelAttribute("userform") BenutzerFormular benutzerFormular,
                                           BindingResult formErrors,
                                           @ModelAttribute("benutzer") Benutzer benutzerSession,
                                           Model model) {
        log.info("Update benutzer: {}", benutzerFormular);

        updatePreferences(benutzerFormular);

        if (formErrors.hasErrors()) {
            logAllErrors(formErrors);
            //speichere password (abhängig ob neuer Nutzer oder bestehender)
            if (id == 0) {
                //neuer Benutzer → Passwort ist nötig
                if (isPasswordFieldEmpty(benutzerFormular)) {
                    formErrors.rejectValue("password",         // Formularfeld
                            "benutzer.passwort.ungesetzt",          // Message-Key
                            "Passwort wurde noch nicht gesetzt");   // Default-Meldung
                }
            }
            // auf gleiche Seite zurück da Fehler
            return "benutzer/benutzerbearbeiten";
        } else {
            //benutzerformular in benutzer (aus session)
            Benutzer benutzer = (Benutzer) model.getAttribute("benutzer");
            if (benutzer != null) benutzerFormular.toBenutzer(benutzer);
            //speichere eventuell password (abhängig ob neuer Nutzer oder bestehender)
            if (id != 0) {
                //bestehender Benutzer aus db → bleibt Passwort erhalten, bis im Bearbeitungsformular neu gesetzt
                if (!isPasswordFieldEmpty(benutzerFormular)) {
                    // neues pw setzen
                    String password = benutzerFormular.getPassword();
                    benutzer.setPassword(password);
                }
            }
            // String redirect = checkPassword(id, benutzerFormular, formErrors, benutzer);
            // Benutzer in db speichern
            try {
                benutzer.setPassword(passwordEncoder.encode(benutzer.getPassword()));       //kodiere Password vor Speichern
                benutzer = benutzerService.speichereBenutzer(benutzer);
                //benutzer session attribut reloaden
                model.addAttribute("benutzer", benutzer);
                //nach dem Speichern die benutzerseite anzeigen
                return "redirect:/admin/benutzer/" + benutzer.getId();
            } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
                //Model-Attribut info mit der Fehlermeldung aus der Exception füllen
                model.addAttribute("info", e.getMessage());
                log.error(e.getMessage());
                return "benutzer/benutzerbearbeiten";
            }
        }
    }

    private boolean isPasswordFieldEmpty(BenutzerFormular benutzerFormular) {
        return benutzerFormular.getPassword().isEmpty() || benutzerFormular.getPassword() == null;
    }

    private void updatePreferences(BenutzerFormular benutzerFormular) {
        if (benutzerFormular.getLikeThings().size() < benutzerFormular.getMaxWunsch()) {
            String currInput = benutzerFormular.getInputLike().trim();
            if (!currInput.isEmpty()) {
                benutzerFormular.getLikeThings().add(currInput);
            }
        }

        if (benutzerFormular.getDoNotLike().size() < benutzerFormular.getMaxWunsch()) {
            String currInput = benutzerFormular.getInputDoNotLike().trim();
            if (!currInput.isEmpty()) {
                benutzerFormular.getDoNotLike().add(currInput);
            }
        }
        benutzerFormular.setInputLike("");
        benutzerFormular.setInputDoNotLike("");
    }

    private void logAllErrors(BindingResult bindingResult) {
        List<ObjectError> errors = bindingResult.getAllErrors();
        for (ObjectError error : errors) {
            log.error("Error while entering value: " + error.getDefaultMessage());
        }
    }

    @GetMapping
    public String benutzerliste(Model model) {
        List<Benutzer> benutzerListe = benutzerService.holeAlleBenutzer();
        model.addAttribute("benutzerListe", benutzerListe);
        return "benutzer/benutzerliste";
    }

    @GetMapping("/{id}/del")
    public String loescheBenutzer(@PathVariable long id,
                                  Model model) {
        benutzerService.loescheBenutzerMitId(id);
        return "redirect:/admin/benutzer";
    }

    @GetMapping("/{id}/hx/feld/{feldname}")
    public String feldausgeben(Model model, @PathVariable long id, @PathVariable String feldname) {
        model.addAttribute("feldname", feldname);
        model.addAttribute("benutzerid", id);

        //wert wird in ermittelt in Handlermethode
        Optional<Benutzer> potenziellExistierenderBenutzer = benutzerService.holeBenutzerMitId(id);
        if (potenziellExistierenderBenutzer.isPresent()) {
            Benutzer existierenderBenutzer = potenziellExistierenderBenutzer.get();
            if(feldname.equals("email")){
                model.addAttribute("wert", existierenderBenutzer.getEmail());
            } else {
                model.addAttribute("wert", existierenderBenutzer.getName());
            }
        }

        return "benutzer/benutzerliste-zeile :: feldbearbeiten";
    }

    @PutMapping("/{id}/hx/feld/{feldname}")
    public String feldbearbeiten(Model model, @PathVariable long id, @PathVariable String feldname, @RequestParam("wert") String wert) {
        model.addAttribute("feldname", feldname);
        model.addAttribute("benutzerid", id);
        //wert wird in ermittelt in Handlermethode
        try {
            Benutzer benutzer = benutzerService.aktualisiereBenutzerAttribut(id, feldname, wert);
            model.addAttribute("benutzer", benutzer);
            model.addAttribute("wert", wert);
            return "benutzer/benutzerliste-zeile :: feldausgeben";
        } catch (ConstraintViolationException e) {
            // Handle validation errors
            String errorMessage = e.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            model.addAttribute("wert", wert);
            model.addAttribute("error", errorMessage);
            return "benutzer/benutzerliste-zeile :: feldbearbeiten";
        }
    }
}
