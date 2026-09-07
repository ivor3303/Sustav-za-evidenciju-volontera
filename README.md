# Sustav za evidenciju volontera

Java projekt razvijen u sklopu kolegija na Tehničkom veleučilištu u Zagrebu (TVZ). Projekt sadrži dvije implementacije istog sustava za evidenciju volontera, organizatora, događaja i vještina.

## Struktura repozitorija

- **`sustav_za_evid_volontera/`** — konzolna verzija aplikacije. Podaci se pohranjuju u JSON datotekama (`volunteers.json`, `organizers.json`, `events.json`, `skills.json`). Uključuje vlastite iznimke za validaciju (nevažeći email, prazan email, negativna dob) te servise za pretragu i statistiku.
- **`sustav_za_evid_volontera_fx/`** — desktop verzija s grafičkim sučeljem izrađena u JavaFX-u, po MVC principu (kontroleri, entiteti, repozitoriji). Podaci se pohranjuju u H2 bazi podataka putem JDBC-a.

## Tehnologije

- Java (17 za konzolnu verziju, 25 za JavaFX verziju)
- Maven
- JavaFX 21
- H2 baza podataka
- SLF4J / Logback za logiranje
- JUnit 5 za testiranje

## Pokretanje

### Konzolna verzija
```
cd sustav_za_evid_volontera
mvn compile exec:java
```

### JavaFX verzija
```
cd sustav_za_evid_volontera_fx
mvn javafx:run
```
