package de.hsrm.mi.web.projekt.entities.tour;

import de.hsrm.mi.web.projekt.entities.benutzer.Benutzer;
import de.hsrm.mi.web.projekt.entities.ort.Ort;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@Entity
@Table(name = "TOUR")
public class Tour implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private long version;

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

    @ManyToOne @NotNull
    private Benutzer benutzer;

    @ManyToMany
    private Set<Benutzer> mitfahrgaeste = new HashSet<>();

}
