package de.hsrm.mi.web.projekt.entities.benutzer;

import de.hsrm.mi.web.projekt.entities.tour.Tour;
import de.hsrm.mi.web.projekt.ui.annotations.GutesPasswort;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.FetchType.EAGER;

@Getter @Setter
@Entity
@Table(name="BENUTZER")
public class Benutzer implements Serializable {

    @Id @GeneratedValue
    @Column(name="ID", unique=true, nullable=false)
    private Long id;

    @Version // um optimistisches Sperren zu ermöglichen
    private long version;

    @Size(min = 3, max = 80)
    @NotBlank
    @Column(name="NAME")
    private String name;

    @NotBlank
    @Pattern(regexp="[a-zA-Z0-9öäüÖÄÜß]+[@][a-zA-Z0-9öäüÖÄÜß]+[.][a-zA-Z0-9öäüÖÄÜß]+", message = "{guteemail.fehler}")
    @Column(name="MAIL", unique=true)   // durch unique = true kann die email nur 1x in der Datenbank gespeichert werden
    private String email;

    //@GutesPasswort(message = "{gutespasswort.fehler}")
    @NotNull
    private String password;

    @NotNull @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name="DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @NotNull @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="BENUTZER_LIKES",
            joinColumns=@JoinColumn(name="BENUTZER_ID"))
    private Set<String> likeThings = new HashSet<>();

    @NotNull @ElementCollection
    @CollectionTable(
            name="BENUTZER_DISLIKES",
            joinColumns=@JoinColumn(name="BENUTZER_ID"))
    private Set<String> doNotLike = new HashSet<>();

    @OneToMany(mappedBy = "benutzer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Collection<Tour> angeboteneTouren = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "mitfahrgaeste")
    private Set<Tour> gebuchteTouren = new HashSet<>();
}
