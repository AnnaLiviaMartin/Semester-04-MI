package de.hsrm.mi.web.projekt.api.ort;

import de.hsrm.mi.web.projekt.entities.ort.Ort;

public record OrtDTO(Long id, String name, double geoBreite, double geoLaenge) {

    public static OrtDTO fromOrt(Ort o) {
        return new OrtDTO(o.getId(), o.getName(), o.getGeobreite(), o.getGeolaenge());
    }

    public static Ort toOrt(OrtDTO dto) {
        Ort ort = new Ort();
        ort.setId(dto.id);
        ort.setName(dto.name);
        ort.setGeobreite(dto.geoBreite);
        ort.setGeolaenge(dto.geoLaenge);

        return ort;
    }
}
