package de.hsrm.mi.web.projekt.messaging;

import lombok.Getter;

@Getter
public enum NachrichtenTyp {
    TOUR("TOUR"),
    ORT("ORT"),
    BENUTZER("BENUTZER");

    private final String nachrichtenTyp;

    NachrichtenTyp(String nachrichtenTyp) {
        this.nachrichtenTyp = nachrichtenTyp;
    }
}
