package hr.tvz.projekt.repository;

import hr.tvz.projekt.app.AppState;
import hr.tvz.projekt.entities.*;
import hr.tvz.projekt.log.ActionLog;
import hr.tvz.projekt.log.LogEntry;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileDataRepository {

    private static final Path SKILLS_JSON = Path.of("skills.json");
    private static final Path VOLUNTEERS_JSON = Path.of("volunteers.json");
    private static final Path EVENTS_JSON = Path.of("events.json");
    private static final Path ORGANIZERS_JSON = Path.of("organizers.json");
    private static final Path BACKUP_FILE = Path.of("backup.bin");
    private static final Path ACTION_LOG_XML = Path.of("action-log.xml");

    private final ActionLog actionLog = new ActionLog();

    /* ====== Generator interfejsi (da repo može pozvati tvoj generate* iz Main-a) ====== */
    @FunctionalInterface
    public interface SkillGenerator {
        Set<Skill> generate(Scanner sc);
    }

    @FunctionalInterface
    public interface VolunteerGenerator {
        List<Volunteer> generate(Scanner sc, List<Skill> skills);
    }

    @FunctionalInterface
    public interface EventGenerator {
        List<Event> generate(Scanner sc);
    }

    @FunctionalInterface
    public interface OrganizerGenerator {
        List<Organizer> generate(Scanner sc);
    }

    /* ====== Učitavanje ili generiranje + spremanje ====== */
    public List<Skill> loadSkillsOrGenerate(Scanner sc, SkillGenerator generator) {
        try {
            Jsonb jsonb = JsonbBuilder.create();
            String json = Files.readString(SKILLS_JSON);
            Skill[] skills = jsonb.fromJson(json, Skill[].class);
            System.out.println(" Vještine su učitane iz skills.json");
            return new ArrayList<>(Arrays.asList(skills));
        } catch (IOException e) {
            System.out.println("skills.json ne postoji ili je neispravan – unos s tipkovnice.");

            Set<Skill> skillsSet = generator.generate(sc);
            List<Skill> skillsList = new ArrayList<>(skillsSet);

            try {
                Jsonb jsonb = JsonbBuilder.create();
                Files.writeString(SKILLS_JSON, jsonb.toJson(skillsList));
                System.out.println(" Vještine spremljene u skills.json");
            } catch (IOException ex) {
                System.out.println(" Greška pri spremanju skills.json: " + ex.getMessage());
            }

            return skillsList;
        }
    }

    public List<Volunteer> loadVolunteersOrGenerate(Scanner sc,
                                                    List<Skill> skills,
                                                    VolunteerGenerator generator) {
        try {
            Jsonb jsonb = JsonbBuilder.create();
            String json = Files.readString(VOLUNTEERS_JSON);
            Volunteer[] arr = jsonb.fromJson(json, Volunteer[].class);
            System.out.println(" Volonteri su učitani iz volunteers.json");
            return new ArrayList<>(Arrays.asList(arr));
        } catch (IOException e) {
            System.out.println(" volunteers.json ne postoji ili je neispravan – unos s tipkovnice.");

            List<Volunteer> list = generator.generate(sc, skills);

            try {
                Jsonb jsonb = JsonbBuilder.create();
                Files.writeString(VOLUNTEERS_JSON, jsonb.toJson(list));
                System.out.println(" Volonteri su spremljeni u volunteers.json");
            } catch (IOException ex) {
                System.out.println(" Greška pri spremanju volunteers.json: " + ex.getMessage());
            }

            return list;
        }
    }

    public List<Event> loadEventsOrGenerate(Scanner sc, EventGenerator generator) {
        try {
            Jsonb jsonb = JsonbBuilder.create();
            String json = Files.readString(EVENTS_JSON);
            Event[] arr = jsonb.fromJson(json, Event[].class);
            System.out.println(" Događaji su učitani iz events.json");
            return new ArrayList<>(Arrays.asList(arr));
        } catch (IOException e) {
            System.out.println(" events.json ne postoji ili je neispravan – unos s tipkovnice.");

            List<Event> list = generator.generate(sc);

            try {
                Jsonb jsonb = JsonbBuilder.create();
                Files.writeString(EVENTS_JSON, jsonb.toJson(list));
                System.out.println(" Događaji su spremljeni u events.json");
            } catch (IOException ex) {
                System.out.println(" Greška pri spremanju events.json: " + ex.getMessage());
            }

            return list;
        }
    }

    public List<Organizer> loadOrganizersOrGenerate(Scanner sc, OrganizerGenerator generator) {
        try {
            Jsonb jsonb = JsonbBuilder.create();
            String json = Files.readString(ORGANIZERS_JSON);
            Organizer[] arr = jsonb.fromJson(json, Organizer[].class);
            System.out.println(" Organizatori su učitani iz organizers.json");
            return new ArrayList<>(Arrays.asList(arr));
        } catch (IOException e) {
            System.out.println(" organizers.json ne postoji ili je neispravan – unos s tipkovnice.");

            List<Organizer> list = generator.generate(sc);

            try {
                Jsonb jsonb = JsonbBuilder.create();
                Files.writeString(ORGANIZERS_JSON, jsonb.toJson(list));
                System.out.println(" Organizatori su spremljeni u organizers.json");
            } catch (IOException ex) {
                System.out.println(" Greška pri spremanju organizers.json: " + ex.getMessage());
            }

            return list;
        }
    }

    /* ====== Backup ====== */
    public void saveBackup(AppState state) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(Files.newOutputStream(BACKUP_FILE))) {
            oos.writeObject(state);
            System.out.println(" Pričuvna kopija (backup.bin) je uspješno spremljena.");
        } catch (IOException e) {
            System.out.println(" Greška pri spremanju backup.bin: " + e.getMessage());
        }
    }

    public AppState loadBackup() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(BACKUP_FILE))) {

            Object obj = ois.readObject();
            if (obj instanceof AppState state) {
                System.out.println(" Podaci su učitani iz backup.bin");
                return state;
            }
            System.out.println(" backup.bin ne sadrži ispravan AppState objekt.");
            return null;

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(" Greška pri učitavanju backup.bin: " + e.getMessage());
            return null;
        }
    }

    /* ====== XML log ====== */
    public void logAction(String action) {
        actionLog.addEntry(new LogEntry(action));
    }

    public void saveLogToXml() {
        try {
            JAXBContext context = JAXBContext.newInstance(ActionLog.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(actionLog, ACTION_LOG_XML.toFile());
            System.out.println(" Log spremljen u action-log.xml");
        } catch (Exception e) {
            System.out.println(" Greška kod spremanja XML loga: " + e.getMessage());
        }
    }

    public void printLogWithoutTags() {
        if (!Files.exists(ACTION_LOG_XML)) {
            System.out.println("Log datoteka ne postoji.");
            return;
        }
        try {
            List<String> lines = Files.readAllLines(ACTION_LOG_XML);
            for (String line : lines) {
                line = line.trim();
                if (!line.startsWith("<") && !line.endsWith(">")) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.out.println(" Greška pri čitanju XML loga: " + e.getMessage());
        }
    }
}
