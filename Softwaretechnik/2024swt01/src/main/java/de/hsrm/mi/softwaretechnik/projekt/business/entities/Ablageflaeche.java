package de.hsrm.mi.softwaretechnik.projekt.business.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.hsrm.mi.softwaretechnik.projekt.business.enums.AblageflaechenTyp;

import java.util.UUID;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Regalboden.class, name = "Regalboden"),
        @JsonSubTypes.Type(value = Zutatenpaket.class, name = "Zutatenpaket")})
public interface Ablageflaeche {
    double getBreite();

    double getHoehe();

    int getTragfaehigkeit();

    AblageflaechenTyp getTyp();

    Ablageflaeche getAblageflaeche();

    double getAbstandVonLinks();

    double getxPos();

    double getyPos();

    UUID getUuid();
}