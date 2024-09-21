package de.hsrm.mi.softwaretechnik.projekt.business.entities;

import de.hsrm.mi.softwaretechnik.projekt.business.exceptions.ObjektExistiertNichtException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Zutat implements Serializable {
    private String name;
    private String iconPath;
    private List<Zutat> unvertraeglichkeit = new ArrayList<>();

    public Zutat() {
    }

    public Zutat(String name) {
        unvertraeglichkeit = new ArrayList<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public List<Zutat> getUnvertraeglichkeit() {
        return unvertraeglichkeit;
    }

    public String unvertraeglichkeitenAlsString(){
        StringBuilder s = new StringBuilder();

        for (Zutat zutat : unvertraeglichkeit) {
            s.append(" ").append(zutat.getName());
        }

        return s.toString();
    }

    public void setUnvertraeglichkeit(List<Zutat> unvertraeglichkeit) {
        this.unvertraeglichkeit = unvertraeglichkeit;
    }

    public void addUnvertraeglichkeit(Zutat zutat) throws ObjektExistiertNichtException {
        if (zutat == null) {
            throw new ObjektExistiertNichtException("Zutat nicht initialisiert");
        }

        if (this.unvertraeglichkeit == null) {
            throw new ObjektExistiertNichtException("Keine Unverträglichkeitsliste initialisiert!");
        }

        this.unvertraeglichkeit.add(zutat);
    }

}