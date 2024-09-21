package de.hsrm.mi.web.projekt.ui.benutzer;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import de.hsrm.mi.web.projekt.entities.benutzer.Benutzer;
import de.hsrm.mi.web.projekt.ui.annotations.GutesPasswort;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BenutzerFormular {

    private Long id;

    @Size(min = 3, max = 80)
    @NotBlank
    private String name;

    @NotNull @NotBlank
    @Pattern(regexp="[a-zA-Z0-9öäüÖÄÜß]+[@][a-zA-Z0-9öäüÖÄÜß]+[.][a-zA-Z0-9öäüÖÄÜß]+", message = "{guteemail.fehler}")
    private String email;

    @GutesPasswort(message = "{gutespasswort.fehler}") @NotNull
    private String password;

    @Past @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    private Set<String> likeThings = new HashSet<>();

    private String inputLike;

    private Set<String> doNotLike = new HashSet<>();

    private String inputDoNotLike;

    private final int maxWunsch = 5;

    public void toBenutzer(Benutzer b) {
        b.setId(this.id);
        b.setName(this.name);
        b.setEmail(this.email);
        b.setDateOfBirth(this.dateOfBirth);
        b.setLikeThings(this.likeThings);
        b.setDoNotLike(this.doNotLike);
    }

    public void fromBenutzer(Benutzer b) {
        this.id = b.getId();
        this.name = b.getName();
        this.email = b.getEmail();
        this.dateOfBirth = b.getDateOfBirth();
        this.likeThings = b.getLikeThings();
        this.doNotLike = b.getDoNotLike();
    }
}
