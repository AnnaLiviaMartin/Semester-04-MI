package de.hsrm.mi.softwaretechnik.projekt.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsrm.mi.softwaretechnik.projekt.business.entities.*;
import de.hsrm.mi.softwaretechnik.projekt.business.services.ZutatenpaketverwaltungImpl;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Utilities {
    private static ZutatenpaketverwaltungImpl zutatenpaketverwaltung;
    private static String filePath = "src/main/resources/jsons/";

    public Utilities(ZutatenpaketverwaltungImpl zutatenpaketverwaltung) {
        Utilities.zutatenpaketverwaltung = zutatenpaketverwaltung;
    }

    public static Regal loadRegal(){
        try {
            return loadRegalFromJsonFile("regal.json");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static List<Zutat> loadZutaten(){
        try {
            return loadZutatenFromJsonFile("zutaten.json");
        } catch (IOException e) {
            e.getMessage();
            return null;
        }
    }

    public static List<Paket> loadPakete(){
        try {
            return loadPaketeFromJsonFile("pakete.json");
        } catch (IOException e) {
            e.getMessage();
            return null;


        }
    }

    /**
     * Speichert das angegebene Regal sowie die Zutaten und Pakete aus der Zutatenpaketverwaltung.
     * @param regal welches gespeichert werden soll
     */
    public static void saveRegalZutatenPakete(Regal regal){
        try {
            convertRegalToJsonFile(regal, "regal.json");

            List<Zutat> zutaten = zutatenpaketverwaltung.getZutatenliste();
            convertZutatToJsonFile(zutaten, "zutaten.json");

            List<Paket> pakete = zutatenpaketverwaltung.getPaketListe();
            convertPaketToJsonFile(pakete, "pakete.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void convertZutatToJsonFile(List<Zutat> zutaten, String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(filePath + fileName);
        mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, zutaten);
    }

    private static void convertPaketToJsonFile(List<Paket> pakete, String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(filePath + fileName);
        mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, pakete);
    }

    private static void convertRegalToJsonFile(Regal regal, String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(filePath + fileName);
        mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, regal);
    }

    private static Regal loadRegalFromJsonFile(String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(filePath + fileName);
        Regal regal = mapper.readValue(jsonFile, Regal.class);
        return regal;
    }

    private static List<Zutat> loadZutatenFromJsonFile(String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(filePath + fileName);
        List<Zutat> myObjects = mapper.readValue(jsonFile, mapper.getTypeFactory().constructCollectionType(List.class, Zutat.class));
        return myObjects;
    }

    private static List<Paket> loadPaketeFromJsonFile(String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(filePath + fileName);
        List<Paket> myObjects = mapper.readValue(jsonFile, mapper.getTypeFactory().constructCollectionType(List.class, Paket.class));
        return myObjects;
    }

}
