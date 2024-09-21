package de.hsrm.mi.web.springhtmx;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;



@Controller
@SessionAttributes({ "datadict" })
public class SpringHtmxController {
    Logger logger = LoggerFactory.getLogger(getClass());

    /*
     * Testdaten bereitlegen
     */
    @ModelAttribute("datadict")
    public Map<Long, TabellenEintrag> init(Model m) {
        Map<Long, TabellenEintrag> liste = new HashMap<>();
        liste.put(1L, new TabellenEintrag(1, "Mandli", false));
        liste.put(2L, new TabellenEintrag(2, "Schockchi", false));
        liste.put(3L, new TabellenEintrag(3, "Nussi", false));
        liste.put(4L, new TabellenEintrag(4, "Brotli", false)); // Kompressionsalgo
        liste.put(5L, new TabellenEintrag(5, "Guetzli", false)); // JPEG Encoder
        return liste;
    }


    /*
     * Startseite 'index.html' ausliefern
     */
    @GetMapping("/")
    public String getIndex(Model m) {
        logger.info("Startseite: getIndex() - ahoi!");
        // Vorbelegung für Fragment 'schnipsel :: zeitstempel'
        m.addAttribute("zeitstempel", "(Platzhalter für alle paar Sekunden aktualisierte Zeitausgabe)");
        return "index";
    }


    /*
     * HTMX-Endpunkt für regelmäßige Update-Abfragen des Zeitstempels
     * bedient zeitgetaktete Aktualisierung in Fragment 'zeitstempel'
     * (Ausschnitt der Startseite index.html)
     */
    @GetMapping("/hx/zeitstempel")
    public String getHxZeitstempel(Model m) {
        var zs = new Date().toString();
        var threadid = Thread.currentThread().getId();
        m.addAttribute("zeitstempel", zs);
        m.addAttribute("threadid", threadid);
        logger.info("getHxZeitstempel(): zeitstempel={} // bedient von Thread #{}", zs, threadid);
        return "index :: zeitstempel";
    }


    /*
     * HTMX-Endpunkt zum Aktualisieren einer Listen/Tabellen-Zeile
     * bedient Fragment 'listenzeile' in schnipsel.html
     */
    @SuppressWarnings({ "null", "unchecked" })
    @PutMapping("/hx/tabellenzeile")
    public String puthxtabellenzeile(@RequestParam("key") long key, Model m) {
        Map<Long, TabellenEintrag> dict = (Map<Long, TabellenEintrag>) m.getAttribute("datadict");
        assert dict != null;
        var e = dict.get(key);
        assert e != null;
        // "check"-Zustand toggeln
        e.setChecked(!e.getChecked());
        // Daten für Fragent 'schnipsel :: listenzeile' bereitstellen
        m.addAttribute("key", key)
                .addAttribute("ele", e);
        logger.info("puthxtabellenzeile(): key={}, ele={}",key, e);
        return "schnipsel :: hxtabellenzeile";
    }


    /*
     * HTMX-Endpunkt für Zähl-Button
     * bedient Fragment 'hxzaehlbutton' in schnipsel.html
     * Request erhält aktuellen Wert aus hx-vals und
     * liefert HTML-Schnipsel mit erhöhtem Zähler und Kommentar zurück
     */
    @PutMapping("/hx/zaehlbutton")
    public String putHxButton(@RequestParam(name = "zaehlerstand") int zaehlerstand, Model m) {
        zaehlerstand++;
        var info = zaehlerstand % 2 == 0 ? "Eine gerade Zahl" : "Oh, eine ungerade Zahl";

        m.addAttribute("zaehlerstand", zaehlerstand)
                .addAttribute("zaehlerinfo", info);
        logger.info("putHxButton(): neuer zaehlerstand={}, zaehlerinfo={}", zaehlerstand, info);
        return "schnipsel :: hxzaehlbutton";
    }


    /*
     * HTMX-Endpunkt für INPUT-Checker (kommentiert numerische Eingaben)
     * bedient Fragment 'hxinputchecker' in schnipsel.html
     */
    @PostMapping("/hx/inputchecker")
    public String postHxInputChecker(@RequestParam("eingabe") int eingabe, Model m) {
        var vortrefflich = eingabe % 17 == 0;
        var beurteilung = vortrefflich
                ? String.format("Die %d ist vortrefflich, denn sie ist ein ganzzahliges Vielfaches von 17", eingabe)
                : String.format("Die %d ist eine Zahl, aber mehr auch nicht", eingabe);

        // Reaktion künstlich verzögern, um langsamen Aufruf zu simulieren
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            // Exc. bei Unterbrechung des sleep() bewusst ignoriert
        }

        m.addAttribute("eingabebeurteilung", beurteilung)
         .addAttribute("classzusatz", vortrefflich? "praechtig" : "beklagenswert"); // CSS-Klassen

        logger.info("postHxInputCheck(): eingabe={} -> beurteilung={}", eingabe, beurteilung);
        return "schnipsel :: hxinputchecker";
    }


    /*
     * HTMX-Endpunkt: Gibt einfachen String mit aktueller Zeit auf dem Server zurück
     */
    @GetMapping("/hx/serverzeit")
    @ResponseBody
    public String getServerzeit() {
        var antwort = "Es ist "+LocalTime.now()+ "Uhr";
        logger.info("getServerzeit(): {}", antwort);
        return antwort;
    }


    /*
     * HTMX-Endpunkt: gibt fünf HTML "<LI>"-Listeneinträge zurück
     */
    @GetMapping("/hx/mausliste")
    @ResponseBody
    public String getMausListe() {
        List<String> liste = new ArrayList<>();
        for (int i=0; i < 5; i++) {
            liste.add("<li>Element "+i+" um "+new Date());
        }
        String ergebnis = liste.stream().collect(Collectors.joining());
        logger.info("getMausListe(): {}", ergebnis);
        return ergebnis;
    }
    
 
    /*
     * HTMX-Endpunkt: gibt alle Parameter als HTML-<LI>-Listenenelement aus,
     * die im gePOSTeten Request hereingegeben wurden (als Schlüssel/Wert-Paare)
     */
    @PostMapping("/hx/requestdatenzeigen")
    @ResponseBody
    public String postRequestDatenAnzeigen(@RequestParam MultiValueMap<String,String> alleparams) {
        List<String> liste = new ArrayList<>();
        liste.add(new Date().toString());
        for (var e: alleparams.entrySet()) {
            liste.add("<li>"+e.getKey()+" = "+e.getValue().stream().collect(Collectors.joining(",")));
        }   
        var ergebnis = liste.stream().collect(Collectors.joining());
        logger.info("postRequestDatenAnzeigen: {}", ergebnis);
        return ergebnis;
    }


    /*
     * HTMX-Endpunkt: Beispiel für Nutzung mit Thymeleaf-Fragmenten
     * gibt zufällig entweder das "schnipsel :: schwipp" oder das
     * "schnipsel :: schwapp"-Thymeleaf-Fragment zurück, beide 
     * befüllt aus Model ("name"-Attribut)
     */
    @PostMapping("/hx/zufallsfragment")
    public String postZufallsFragment(Model m) {
        m.addAttribute("name", "Höböt");
        var zufall = new Random().nextInt(2) == 0;
        var fragmentname = zufall? "schnipsel :: schwipp" : "schnipsel :: schwapp";
        logger.info("postZufallsFragment: zufall={}, fragmentname={}", zufall, fragmentname);
        return fragmentname;
    }
    
}
