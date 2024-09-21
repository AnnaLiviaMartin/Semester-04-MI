package de.hsrm.mi.web.projekt.entities.ort;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@Entity
@Table(name = "ORT")
public class Ort implements Serializable {

    @Id @GeneratedValue
    private Long id;

    @Version
    private long version;

    @NotBlank // nicht Null und mind. ein Nicht-Leerzeichen
    private String name;

    private double geobreite;

    private double geolaenge;
}
