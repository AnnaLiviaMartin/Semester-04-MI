package de.hsrm.mi.web.projekt.ui.tour;

import de.hsrm.mi.web.projekt.entities.benutzer.Benutzer;
import de.hsrm.mi.web.projekt.entities.ort.Ort;
import de.hsrm.mi.web.projekt.entities.tour.Tour;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourFormular {

    private Long id;

    @ManyToOne @NotNull
    private Benutzer anbieter;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate abfahrDateTime;

    @Min(0)
    private int preis;

    @Min(1)
    private int plaetze;

    @Size(max = 400)
    private String info;

    @ManyToOne @NotNull
    private Ort startOrt;

    @ManyToOne @NotNull
    private Ort zielOrt;

    public void toTour(Tour t) {
        t.setId(this.id);
        t.setBenutzer(this.anbieter);
        t.setAbfahrDateTime(this.abfahrDateTime);
        t.setPreis(this.preis);
        t.setPlaetze(this.plaetze);
        t.setStartOrt(this.startOrt);
        t.setZielOrt(this.zielOrt);
        t.setInfo(this.info);
    }

    public void fromTour(Tour t) {
        this.id = t.getId();
        this.anbieter = t.getBenutzer();
        this.abfahrDateTime = t.getAbfahrDateTime();
        this.preis = t.getPreis();
        this.plaetze = t.getPlaetze();
        this.startOrt = t.getStartOrt();
        this.zielOrt = t.getZielOrt();
        this.info = t.getInfo();
    }
}
