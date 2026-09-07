package hr.tvz.projekt.app;

import hr.tvz.projekt.entities.*;

import hr.tvz.projekt.exception.EmptyEmailException;
import hr.tvz.projekt.exception.InvalidEmailException;
import hr.tvz.projekt.exception.NegativeAgeException;
import hr.tvz.projekt.log.ActionLog;
import hr.tvz.projekt.log.LogEntry;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import hr.tvz.projekt.repository.FileDataRepository;
import hr.tvz.projekt.service.SearchService;
import hr.tvz.projekt.service.StatsService;
import hr.tvz.projekt.util.DialogUtil;





public class Main {

    /**
     * Glavna klasa aplikacije za evidenciju volontera i organizatora.
     * <p>
     * Omogućuje unos, validaciju i pretraživanje volontera, događaja i zadataka,
     * uz rukovanje checked i unchecked iznimkama te zapisivanje najvažnijih događaja
     * pomoću SLF4J logiranja (TRACE, DEBUG, INFO, WARN, ERROR).
     * <p>
     * Aplikacija uključuje metode za generiranje entiteta, statističke prikaze,
     * validaciju unosa i osnovnu obradu grešaka prilikom interakcije korisnika.
     *
     * @author Ivor Županić
     * @version 1.0
     * @since Java 25
     * @see hr.tvz.projekt.entities.Volunteer
     * @see hr.tvz.projekt.entities.Organizer
     * @see hr.tvz.projekt.exception.InvalidEmailException
     * @see hr.tvz.projekt.exception.NegativeAgeException
     */

    private static final Integer NUMBER_OF_VOLUNTEERS = 5;
    private static final Integer NUMBER_OF = 5;

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final Path SKILLS_JSON = Path.of("skills.json");
    private static final Path VOLUNTEERS_JSON = Path.of("volunteers.json");
    private static final Path EVENTS_JSON = Path.of("events.json");
    private static final Path ORGANIZERS_JSON = Path.of("organizers.json");
    private static final Path BACKUP_FILE = Path.of("backup.bin");
    private static final Path ACTION_LOG_XML = Path.of("action-log.xml");
    private static final ActionLog ACTION_LOG = new ActionLog();


    /**
     * Ulazna točka aplikacije.
     * <p>
     * Pokreće cijeli program, inicijalizira unos korisničkih podataka i
     * poziva sve pomoćne metode za generiranje, pretraživanje i ispis podataka.
     * Također uključuje centralizirano rukovanje iznimkama i evidentiranje logova.
     *
     * @param args argumenti komandne linije (ne koriste se u ovoj aplikaciji)
     */

    public static void main(String[] args) {

        log.info("Pokretanje aplikacije za evidenciju volontera.");
        Scanner sc = new Scanner(System.in);
        FileDataRepository repo = new FileDataRepository();
        SearchService searchService = new SearchService();
        StatsService statsService = new StatsService();



        System.out.println("Dobar dan, želite li unositi podatke za evidenciju volontera? ");
        String confirmation = "DA";
        String answer = "DA";

        try {
            if (("DA").equals(confirmation)) {

                List<Skill> skillsList = repo.loadSkillsOrGenerate(sc, Main::generateSkills);

                List<Volunteer> volunteers =
                        repo.loadVolunteersOrGenerate(sc, skillsList, Main::generateVolunteers);

                List<Event> events =
                        repo.loadEventsOrGenerate(sc, Main::generateEvents);

                Map<Volunteer, Assignment> assignments = generateAssignments(sc, volunteers, events);
                List<Organizer> organizers =
                        repo.loadOrganizersOrGenerate(sc, Main::generateOrganizers);


                System.out.print("\nŽelite li spremiti pričuvnu kopiju (backup.bin)? (DA/NE): ");
                String odgovor = sc.nextLine();
                logAction("Backup: korisnik upitan želi li spremiti pričuvnu kopiju");


                if (odgovor.equalsIgnoreCase("DA")) {
                    saveBackup(skillsList, volunteers, events, assignments, organizers);
                }


                System.out.print("\nŽelite li učitati podatke iz backup.bin i pregaziti trenutne? (DA/NE): ");
                odgovor = sc.nextLine();
                logAction("Backup: korisnik upitan želi li UČITATI pričuvnu kopiju (odgovor: " + odgovor + ")");


                if (odgovor.equalsIgnoreCase("DA")) {

                    AppState state = loadBackup();


                    if (state != null) {

                        logAction("Backup: podaci učitani iz pričuvne kopije");
                        skillsList.clear();
                        skillsList.addAll(state.getSkills());

                        volunteers.clear();
                        volunteers.addAll(state.getVolunteers());

                        events.clear();
                        events.addAll(state.getEvents());

                        assignments.clear();
                        assignments.putAll(state.getAssignments());

                        organizers.clear();
                        organizers.addAll(state.getOrganizers());

                        System.out.println("♻ Podaci su uspješno vraćeni iz backup.bin.");
                    }
                }

                runSearchMenu(sc, volunteers, events, assignments, skillsList, searchService);

                runStatsMenu(sc, volunteers, assignments, statsService);

                List<Person> persons = new ArrayList<>();
                addAllPersons(persons, volunteers);
                addAllPersons(persons, organizers);
                Collections.shuffle(persons);


                Optional<Person> youngest = statsService.youngest(persons);
                Optional<Person> oldest = statsService.oldest(persons);


                youngest.ifPresent(osoba ->
                        System.out.println("Najmlađa osoba: " + osoba.getName() + " (" + osoba.getAge() + " god.) - " + osoba.role())
                );

                oldest.ifPresent(starac ->
                        System.out.println("Najstarija osoba: " + starac.getName() + " (" + starac.getAge() + " god.) - " + starac.role())
                );


            } else {
                System.out.println("Hvala Vam na svemu, doviđenja!");
            }
        } catch (Exception e) {
            log.error("Neočekivana greška u programu: {}", e.getMessage(), e);
        }
        saveLogToXml();
        System.out.print("Želite li ispis XML loga? (DA/NE): ");
        String p = sc.nextLine();
        if (p.equalsIgnoreCase("DA")) {
            printLogWithoutTags();
        }

    }

    /**
     * Omogućuje unos i validaciju podataka o volonterima.
     * <p>
     * Tijekom unosa provjerava se ispravnost email adrese i dobi.
     * U slučaju pogrešnog unosa, korisniku se prikazuje poruka i ponavlja unos.
     *
     * @param sc objekt klase {@link Scanner} za unos podataka
     * @param skills popis dostupnih vještina koje volonter može imati
     * @return polje objekata tipa {@link Volunteer}
     * @throws InvalidEmailException ako uneseni email nije valjan
     * @throws EmptyEmailException ako je uneseni email prazan
     * @throws NegativeAgeException ako je unesena dob izvan raspona
     */


    public static List<Volunteer> generateVolunteers(Scanner sc, List<Skill> skills) {

        log.trace("Metoda generateVolunteers() pozvana.");
       List<Volunteer> volunteers = new ArrayList<>();

        log.debug("Započet unos volontera. Ukupan broj: {}", NUMBER_OF_VOLUNTEERS);

        for (int i = 0; i < NUMBER_OF_VOLUNTEERS; i++) {
            System.out.println("Unesite ime i prezime " + (i + 1) + ". volontera: ");
            String name  = sc.nextLine();

            System.out.println("Unesite iz kojeg grada dolazi " + (i + 1) + ". volonter: ");
            String city  = sc.nextLine();

            // ispisi dostupne vjestine
            int ordinal = 1;
            for (Skill s : skills) {
                System.out.println(ordinal + ") " + s.getSkillName());
                ordinal++;
            }

            // odabir vjestine
            System.out.println("Odaberite vještinu (1-" + (ordinal - 1)+ "): ");
            int skillChoice = sc.nextInt();
            sc.nextLine(); // pojedi Enter
            log.debug("Odabran skillChoice: {}", skillChoice);

            String email = null;
            boolean validEmail;
            do{
                validEmail = false;
                try{

                    System.out.println("Unesite email " + (i + 1) + ". volontera:");
                     email = sc.nextLine();

                    if(email == null || email.isEmpty()){
                        throw new EmptyEmailException();
                    }
                    if(!email.contains("@") || email.contains("@@")){
                        throw new InvalidEmailException();
                    }
                } catch (InvalidEmailException e) {

                    System.out.println(e.getMessage());
                    log.warn("Neispravna email adresa: {}",e.getMessage());
                    validEmail = true;

                } catch (EmptyEmailException e){
                    System.out.println(e.getMessage());
                    log.warn("Prazan unos email adrese volontera: {}", e.getMessage());
                    validEmail = true;
                }
            }while(validEmail);

            boolean validAge;
            int age = 0;
            do{
                validAge = false;
                try{

                    System.out.println("Unesite dob " + (i + 1) + ". volontera:");
                     age = sc.nextInt();
                    sc.nextLine(); // pojedi Enter

                    if(age < 0 || age > 100){
                        throw new NegativeAgeException();
                    }
                } catch (NegativeAgeException ex) {
                    System.out.println(ex.getMessage()); // defaultna poruka
                    log.warn("Neispravna dob volontera: {}", ex.getMessage());
                    validAge = true;
                } catch (InputMismatchException ex){
                    System.out.println("❌ Morate unijeti brojčane vrijednosti!");
                    log.warn("Neispravan tip podataka za unos dobi (unesen tekst umjesto broja).");
                    sc.nextLine();
                    validAge = true;
                }
            }while(validAge);

            Skill selectedSkill = skills.get(skillChoice - 1);

           Volunteer newVolunteer = new Volunteer(name, city, email, age, selectedSkill);
           volunteers.add(newVolunteer);
            log.info("Uspješno kreiran volonter: {}", name);

        }
        return volunteers;
    }

    /**
     * Generira skup dostupnih vještina volontera.
     *
     *
     * @param sc objekt klase {@link Scanner} za unos podataka
     * @return polje objekata tipa {@link Skill}
     */

    public static Set<Skill> generateSkills(Scanner sc) {
        Set<Skill> skills = new LinkedHashSet<>();

        for (int i = 0; i < NUMBER_OF_VOLUNTEERS; i++) {
            System.out.println("Unesite naziv vještine " + (i + 1) + ": ");
            String skillName = sc.nextLine();

            System.out.println("Unesite opis vještine: ");
            String category = sc.nextLine();

            System.out.println("Odaberite tip vještine:");
            SkillType[] types = SkillType.values();
            for (int j = 0; j < types.length; j++) {
                System.out.println((j + 1) + ") " + types[j]);
            }

            int typeChoice = sc.nextInt();
            sc.nextLine();

            Skill newSkill = new Skill(types[typeChoice - 1], skillName, category);
            skills.add(newSkill);
        }

        return skills;
    }


    /**
     * Omogućuje unos podataka o događajima u kojima volonteri sudjeluju.
     *
     * @param sc objekt klase {@link Scanner} za unos podataka
     * @return polje objekata tipa {@link Event}
     */

    public static List<Event> generateEvents(Scanner sc) {
        log.trace("Metoda generateEvents() pozvana.");

        List<Event> events = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_VOLUNTEERS; i++) {

            System.out.println("Unesite naziv " + (i + 1) + ". događaja: ");
            String nameEvent = sc.nextLine();

            System.out.println("Unesite grad " + (i + 1) + ". događaja: ");
            String cityEvent = sc.nextLine();

            System.out.println("Unesite adresu " + (i + 1) + ". događaja: ");
            String eventAddress = sc.nextLine();

            System.out.println("Odaberite tip događaja:");
            EventType[] types = EventType.values();
            for (int j = 0; j < types.length; j++) {
                System.out.println((j + 1) + ") " + types[j]);
            }
            int typeChoice = sc.nextInt();
            sc.nextLine(); // pojedi Enter

            EventType chosenType = types[typeChoice - 1];

            Event newEvent = new Event(nameEvent, cityEvent, eventAddress, chosenType);
            events.add(newEvent);

            log.debug("Dodan događaj: {} ({}, {}) - {}", nameEvent, cityEvent, eventAddress, chosenType);
        }

        return events;
    }


    /**
     * Generira zadatke i povezuje ih s volonterima i događajima.
     *
     * @param sc objekt klase {@link Scanner} za unos podataka
     * @param volunteers polje dostupnih volontera
     * @param events polje dostupnih događaja
     * @return polje objekata tipa {@link Assignment}
     */

    public static Map<Volunteer, Assignment> generateAssignments(Scanner sc, List<Volunteer> volunteers, List<Event> events) {
        log.trace("Metoda generateAssignments() pozvana.");
        Map<Volunteer, Assignment> assignments = new HashMap<>();

        for (int i = 0; i < volunteers.size(); i++) {
            System.out.println("Unos zadatka za volontera: " + volunteers.get(i).getName());
            System.out.println("Unesite naziv zadatka: ");
            String name = sc.nextLine();

            System.out.println("Unesite opis zadatka: ");
            String description = sc.nextLine();

            System.out.println("Unesite broj sati trajanja: ");
            int durationHours = sc.nextInt();
            sc.nextLine();

            System.out.println("Dostupni događaji: ");
            int ordinal = 1;
            for (Event e : events) {
                System.out.println(ordinal + ") " + e.getTitle() + " - " + e.getCity() + ", " + e.getAddress());
                ordinal++;
            }
            System.out.println("Odaberite događaj (1-" + (ordinal - 1) + "): ");
            int eventChoice = sc.nextInt();
            sc.nextLine();

            Event selectedEvent = events.get(eventChoice - 1);

            Assignment newAssignment = new Assignment.Builder()
                    .setName(name)
                    .setDescription(description)
                    .setDurationHours(durationHours)
                    .setVolunteer(volunteers.get(i))
                    .setEvent(selectedEvent)
                    .build();

            assignments.put(volunteers.get(i), newAssignment);
        }

        return assignments;
    }

    /**
     * Pokreće izbornik za pretraživanje volontera, događaja i zadataka.
     * <p>
     * Korisnik putem konzole odabire željenu opciju pretraživanja.
     * Metoda omogućuje višekratno pretraživanje dok se ne odabere izlaz.
     *
     * @param sc objekt klase {@link Scanner} za unos podataka
     * @param volunteers polje dostupnih volontera
     * @param events polje dostupnih događaja
     * @param assignments polje zadataka
     * @param skills polje vještina dostupnih volonterima
     */

    public static void runSearchMenu(
            Scanner sc,
            List<Volunteer> volunteers,
            List<Event> events,
            Map<Volunteer, Assignment> assignments,
            List<Skill> skills,
            SearchService searchService) {


        while (true) {
            System.out.println();
            System.out.println("=== IZBORNIK PRETRAŽIVANJA I SORTIRANJA ===");
            System.out.println(" 1) Pretraži volontere po imenu ili prezimenu");
            System.out.println(" 2) Pretraži događaje po gradu");
            System.out.println(" 3) Prikaži zadatke za volontera");
            System.out.println(" 4) Sortiraj volontere po imenu (A–Z)");
            System.out.println(" 5) Sortiraj volontere po dobi (najmlađi prvo)");
            System.out.println(" 6) Sortiraj događaje po gradu (A–Z)");
            System.out.println(" 0) Izlaz iz pretraživanja");
            System.out.print("Odabir > ");

            int choice = sc.nextInt();
            sc.nextLine(); // pojedi Enter

            switch (choice) {
                case 1 -> {
                    System.out.print("Unesite dio imena ili prezimena volontera: ");
                    String term = sc.nextLine();
                    var found = searchService.searchVolunteersByName(volunteers, term);
                    if (found.isEmpty()) DialogUtil.info("Rezultat", "Nema volontera.");

                    else found.forEach(v -> System.out.println("- " + v.getName() + " (" + v.getCity() + ")"));
                }

                case 2 -> {
                    System.out.print("Unesite grad za pretraživanje događaja: ");
                    String city = sc.nextLine();

                    var found = searchService.searchEventsByCity(events, city);

                    if (found.isEmpty())
                        System.out.println("Nema događaja u tom gradu.");
                    else
                        found.forEach(e ->
                                System.out.println("- " + e.getTitle() + " @ " + e.getAddress())
                        );
                }

                case 3 -> {
                    System.out.println("Odaberite volontera:");

                    for (int i = 0; i < volunteers.size(); i++) {
                        System.out.println((i + 1) + ") " + volunteers.get(i).getName());
                    }

                    int idx = sc.nextInt();
                    sc.nextLine();

                    if (idx < 1 || idx > volunteers.size()) {
                        System.out.println("Neispravan odabir.");
                        break;
                    }

                    Volunteer v = volunteers.get(idx - 1);
                    var opt = searchService.findAssignmentForVolunteer(assignments, v);

                    opt.ifPresentOrElse(
                            a -> System.out.println(a.getName() + " (" + a.getDurationHours() + " h)"),
                            () -> System.out.println("Nema zadatka.")
                    );
                }

                case 4 -> {
                    var sorted = searchService.sortVolunteersByName(volunteers);
                    sorted.forEach(v ->
                            System.out.println(v.getName() + " (" + v.getCity() + ")")
                    );
                }


                case 5 -> {
                    var sorted = searchService.sortVolunteersByAge(volunteers);
                    sorted.forEach(v ->
                            System.out.println(v.getName() + " - " + v.getAge())
                    );
                }


                case 6 -> {
                    var sorted = searchService.sortEventsByCityThenTitle(events);
                    sorted.forEach(e ->
                            System.out.println(e.getCity() + " - " + e.getTitle())
                    );
                }

                case 0 -> {
                    System.out.println("Izlaz iz pretraživanja.");
                    return;
                }

                default -> {
                    System.out.println("Neispravan odabir.");
                    log.warn("Korisnik unio neispravnu opciju u izborniku pretraživanja.");
                }
            }
        }
    }

    /**
     * Pokreće izbornik za prikaz statističkih podataka o zadacima i volonterima.
     * <p>
     * Omogućuje prikaz najduljeg i najkraćeg zadatka te volontera
     * s najvećim ukupnim brojem sati.
     *
     * @param sc objekt klase {@link Scanner} za unos opcije
     * @param assignments polje zadataka
     * @param volunteers polje volontera
     */

    public static void runStatsMenu(Scanner sc,
                                    List<Volunteer> volunteers,
                                    Map<Volunteer, Assignment> assignments,
                                    StatsService statsService)
    {
        while (true) {
            System.out.println("\n=== STATISTIKA I ANALIZA ===");
            System.out.println(" 1) Najduži zadatak (max sati)");
            System.out.println(" 2) Najkraći zadatak (min sati)");
            System.out.println(" 3) Volonter s najviše sati (po zadatku)");
            System.out.println(" 4) Prikaži punoljetne i maloljetne volontere (partitioningBy)");
            System.out.println(" 5) Grupiraj volontere po tipu vještine (groupingBy)");
            System.out.println(" 6) Prvi i zadnji uneseni volonter");
            System.out.println(" 7) Najveći broj sati zadatka (multiple bounds)");
            System.out.println(" 0) Izlaz");
            System.out.print("Odabir > ");

            int c = sc.nextInt();
            sc.nextLine();

            switch (c) {
                case 1 -> statsService.maxAssignment(assignments)
                        .ifPresentOrElse(
                                a -> System.out.println("Najduži: " + a.getName() + " (" + a.getDurationHours() + " h)"),
                                () -> System.out.println("Nema podataka.")
                        );

                case 2 -> statsService.minAssignment(assignments)
                        .ifPresentOrElse(
                                a -> System.out.println("Najkraći: " + a.getName() + " (" + a.getDurationHours() + " h)"),
                                () -> System.out.println("Nema podataka.")
                        );

                case 3 -> statsService.volunteerWithMostHours(assignments)
                        .ifPresentOrElse(
                                e -> System.out.println("Najviše sati: " + e.getKey().getName()
                                        + " (" + e.getValue().getDurationHours() + " h)"),
                                () -> System.out.println("Nema podataka o satima.")
                        );

                case 4 -> {
                    var p = statsService.partitionAdults(volunteers);

                    System.out.println("\nPunoljetni volonteri (>= 18):");
                    p.get(true).forEach(v ->
                            System.out.println(" - " + v.getName() + " (" + v.getAge() + " god.)")
                    );

                    System.out.println("\nMaloljetni volonteri (< 18):");
                    p.get(false).forEach(v ->
                            System.out.println(" - " + v.getName() + " (" + v.getAge() + " god.)")
                    );
                }

                case 5 -> {
                    var grouped = statsService.groupBySkillType(volunteers);

                    System.out.println("\nVolonteri grupirani po tipu vještine:");
                    grouped.forEach((type, list) -> {
                        System.out.println("[" + type + "]");
                        list.forEach(v ->
                                System.out.println("  - " + v.getName() + " (" + v.getSkill().getSkillName() + ")")
                        );
                    });
                }

                case 6 -> printFirstAndLastVolunteer(volunteers);
               // case 7 -> printAssignmentWindows(assignments);
                case 0 -> {
                    System.out.println("Povratak u glavni izbornik.");
                    return;
                }
                default -> {
                    System.out.println("Neispravan odabir.");
                    log.warn("Korisnik unio neispravnu opciju u izborniku statistike.");
                }
            }
        }
    }

    private static void printFirstAndLastVolunteer(List<Volunteer> volunteers) {
        if (volunteers.isEmpty()) {
            System.out.println("Nema unesenih volontera.");
            return;
        }

        // ekvivalent Sequenced Collections API-ja
        Volunteer first = volunteers.get(0);
        Volunteer last = volunteers.get(volunteers.size() - 1);

        System.out.println("Prvi uneseni volonter: " + first.getName() + " (" + first.getCity() + ")");
        System.out.println("Zadnji uneseni volonter: " + last.getName() + " (" + last.getCity() + ")");
    }
//    private static void printAssignmentWindows(Map<Volunteer, Assignment> assignments) {
//        List<Assignment> lista = new ArrayList<>(assignments.values());
//
//        if (lista.isEmpty()) {
//            System.out.println("Nema unesenih zadataka.");
//            return;
//        }
//
//        List<List<Assignment>> grupe = lista.stream()
//                .gather(Gatherers.windowFixed(3))
//                .toList();
//
//        System.out.println("\nZadaci u grupama po 3 (Gatherers.windowFixed):");
//        int idx = 1;
//        for (List<Assignment> grupa : grupe) {
//            System.out.println("Grupa " + idx++ + ":");
//            grupa.forEach(a ->
//                    System.out.println("  - " + a.getName() + " (" + a.getDurationHours() + "h)")
//            );
//        }
//    }

    /**
     * Omogućuje unos i validaciju podataka o organizatorima.
     * <p>
     * Tijekom unosa provjerava se ispravnost email adrese i dobi organizatora.
     *
     * @param sc objekt klase {@link Scanner} za unos podataka
     * @return polje objekata tipa {@link Organizer}
     * @throws InvalidEmailException ako uneseni email nije valjan
     * @throws EmptyEmailException ako je uneseni email prazan
     * @throws NegativeAgeException ako je unesena dob izvan raspona
     */

    public static List<Organizer> generateOrganizers(Scanner sc){
        log.trace("Metoda generateOrganizers() pozvana.");
        List<Organizer> organizers = new ArrayList<>();

        for (Integer i = 0; i < NUMBER_OF; i++) {

            System.out.println("Unesite ime i prezime organizatora: ");
            String organizerName = sc.nextLine();

            System.out.println("Unesite iz kojeg grada dolazi organizator: ");
            String organizerCity = sc.nextLine();

            String organizerEmail = null;
            boolean nastaviPetlju;
            do{
                nastaviPetlju = false;
                try{

                    System.out.println("Unesite email " + (i + 1) + ". organizatora:");
                    organizerEmail = sc.nextLine();

                    if(!organizerEmail.contains("@")){
                        throw new InvalidEmailException();
                    }
                    if(organizerEmail == null || organizerEmail.isEmpty()){
                        throw new EmptyEmailException();
                    }

                } catch (InvalidEmailException e) {

                    System.out.println(e.getMessage());
                    log.warn("Neispravna email adresa organizatora: {}", e.getMessage());
                    nastaviPetlju = true;

                } catch (EmptyEmailException e){
                    System.out.println(e.getMessage());
                    log.warn("Unesena prazna email adresa organizatora: {}", e.getMessage());
                    nastaviPetlju = true;
                }
            }while(nastaviPetlju);


            boolean validAge;
            int organizerAge = 0;
            do{
                validAge = false;
                try{

                    System.out.println("Unesite dob " + (i + 1) + ". organizatora:");
                    organizerAge = sc.nextInt();
                    sc.nextLine(); // pojedi Enter

                    if(organizerAge < 0 || organizerAge > 100){
                        throw new NegativeAgeException();
                    }
                } catch (NegativeAgeException ex) {
                    System.out.println(ex.getMessage()); // defaultna poruka
                    log.warn("Neispravna dob organizatora: {}", ex.getMessage());
                    validAge = true;
                } catch (InputMismatchException ex){
                    System.out.println("❌ Morate unijeti brojčane vrijednosti!");
                    log.warn("Neispravan tip podataka za unos dobi organizatora (unesen tekst umjesto broja).");
                    sc.nextLine();
                    validAge = true;
                }
            }while(validAge);

            Organizer newOrganizer = new Organizer(organizerName, organizerCity, organizerEmail, organizerAge);
           organizers.add(newOrganizer);
            log.info("Uspješno kreiran organizator: {}", organizerName);
        }
        return organizers;
    }

    private static <T> void addAllPersons(List<? super T> target, List<? extends T> source) {
        target.addAll(source);
    }
    public static List<Skill> loadSkills(Scanner sc){
        try{

            Jsonb jsonb = JsonbBuilder.create();
            String json = Files.readString(SKILLS_JSON);

            Skill[] skills = jsonb.fromJson(json, Skill[].class);
            List<Skill> skillsList = new ArrayList<>(Arrays.asList(skills));
            System.out.println("✅ Vještine su učitane iz skills.json");

            return skillsList;

            //JSON-B ne može direktno deserializirati u List<T> zbog type erasure,
            // pa najprije učitam u T[] i onda pretvorim u List<T>.

        }catch (IOException e) {

            System.out.println("📂 skills.json ne postoji ili je neispravan – unos s tipkovnice.");

            //rucni unos
            Set<Skill> skillsSet = generateSkills(sc);
            // idem po stare podatke
            List<Skill> skillsList = new ArrayList<>(skillsSet);

            try {
                // te podatke spremam u json
                Jsonb jsonb = JsonbBuilder.create();
                String json = jsonb.toJson(skillsList);
                Files.writeString(SKILLS_JSON, json);

                System.out.println(" Vještine spremljene u skills.json");

            } catch (IOException ex) {
                System.out.println(" Greška pri spremanju skills.json: " + ex.getMessage());
            }

            return skillsList;
        }
    }
    public static List<Volunteer> loadVolunteers(Scanner sc, List<Skill> skillsList) {

        try {
            Jsonb jsonb = JsonbBuilder.create();
            String json = Files.readString(VOLUNTEERS_JSON);

            // JSON-B ne može direktno deserializirati u List<T> zbog type erasure,
            // pa najprije učitamo u Volunteer[] i pretvorimo u List<Volunteer>.
            Volunteer[] volunteersArray = jsonb.fromJson(json, Volunteer[].class);
            List<Volunteer> volunteersList = new ArrayList<>(Arrays.asList(volunteersArray));

            System.out.println(" Volonteri su učitani iz volunteers.json");
            return volunteersList;

        } catch (IOException e) {

            System.out.println(" volunteers.json ne postoji ili je neispravan – unos s tipkovnice.");
            //rucni unos
            List<Volunteer> volunteersList = generateVolunteers(sc, skillsList);

            try {

                Jsonb jsonb = JsonbBuilder.create();
                String json = jsonb.toJson(volunteersList);
                Files.writeString(VOLUNTEERS_JSON, json);

                System.out.println("💾 Volonteri su spremljeni u volunteers.json");
            } catch (IOException ex) {
                System.out.println("❌ Greška pri spremanju volunteers.json: " + ex.getMessage());
            }

            return volunteersList;
        }

    }
    public static List<Event> loadEvents(Scanner sc) {
        try {
            Jsonb jsonb = JsonbBuilder.create();
            String json = Files.readString(EVENTS_JSON);

            // JSON-B ne može direktno deserializirati u List<T>,
            // pa najprije učitavamo u Event[] i pretvaramo u List<Event>.
            Event[] eventsArray = jsonb.fromJson(json, Event[].class);
            List<Event> eventsList = new ArrayList<>(Arrays.asList(eventsArray));

            System.out.println("✅ Događaji su učitani iz events.json");
            return eventsList;

        } catch (IOException e) {

            System.out.println("📂 events.json ne postoji ili je neispravan – unos s tipkovnice.");

            List<Event> eventsList = generateEvents(sc);
            try {
                Jsonb jsonb = JsonbBuilder.create();
                String json = jsonb.toJson(eventsList);
                Files.writeString(EVENTS_JSON, json);
                System.out.println("💾 Događaji su spremljeni u events.json");
            } catch (IOException ex) {
                System.out.println("❌ Greška pri spremanju events.json: " + ex.getMessage());
            }

            return eventsList;
        }
    }
    public static List<Organizer> loadOrganizers(Scanner sc) {
        try {
            Jsonb jsonb = JsonbBuilder.create();
            String json = Files.readString(ORGANIZERS_JSON);

            Organizer[] organizersArray = jsonb.fromJson(json, Organizer[].class);
            List<Organizer> organizersList = new ArrayList<>(Arrays.asList(organizersArray));

            System.out.println("✅ Organizatori su učitani iz organizers.json");
            return organizersList;

        } catch (IOException e) {

            System.out.println("📂 organizers.json ne postoji ili je neispravan – unos s tipkovnice.");

            List<Organizer> organizersList = generateOrganizers(sc);

            try {
                Jsonb jsonb = JsonbBuilder.create();
                String json = jsonb.toJson(organizersList);
                Files.writeString(ORGANIZERS_JSON, json);
                System.out.println("💾 Organizatori su spremljeni u organizers.json");
            } catch (IOException ex) {
                System.out.println("❌ Greška pri spremanju organizers.json: " + ex.getMessage());
            }

            return organizersList;
        }
    }
    public static void saveBackup(List<Skill> skills,
                                  List<Volunteer> volunteers,
                                  List<Event> events,
                                  Map<Volunteer, Assignment> assignments,
                                  List<Organizer> organizers) {

        AppState state = new AppState(skills, volunteers, events, assignments, organizers);

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(Files.newOutputStream(BACKUP_FILE))) {

            oos.writeObject(state);
            System.out.println(" Pričuvna kopija (backup.bin) je uspješno spremljena.");

        } catch (IOException e) {
            System.out.println(" Greška pri spremanju backup.bin: " + e.getMessage());
        }
    }
    public static AppState loadBackup() {

        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(BACKUP_FILE))) {

            Object obj = ois.readObject();

            if (obj instanceof AppState state) {
                System.out.println("✅ Podaci su učitani iz backup.bin");
                return state;
            } else {
                System.out.println(" backup.bin ne sadrži ispravan AppState objekt.");
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(" Greška pri učitavanju backup.bin: " + e.getMessage());
        }

        return null;
    }
    public static void logAction(String action) {
        ACTION_LOG.addEntry(new LogEntry(action));
    }
    public static void saveLogToXml() {
        try {
            JAXBContext context = JAXBContext.newInstance(ActionLog.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(ACTION_LOG, ACTION_LOG_XML.toFile());
            System.out.println("📄 Log spremljen u action-log.xml");
        } catch (Exception e) {
            System.out.println("Greška kod spremanja XML loga: " + e.getMessage());
        }
    }
    public static void printLogWithoutTags() {
        if (!Files.exists(ACTION_LOG_XML)) {
            System.out.println(" Log datoteka ne postoji.");
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
