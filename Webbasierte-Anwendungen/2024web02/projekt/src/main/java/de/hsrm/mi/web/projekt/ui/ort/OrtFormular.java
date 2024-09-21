package de.hsrm.mi.web.projekt.ui.ort;

import de.hsrm.mi.web.projekt.entities.ort.Ort;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrtFormular {

    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private double geobreite;

    @NotNull
    private double geolaenge;

    public void toOrt(Ort o) {
        o.setId(this.id);
        o.setName(this.name);
        o.setGeobreite(this.geobreite);
        o.setGeolaenge(this.geolaenge);
    }

    public void fromOrt(Ort o) {
        this.id = o.getId();
        this.name = o.getName();
        this.geobreite = o.getGeobreite();
        this.geolaenge = o.getGeolaenge();
    }
}
