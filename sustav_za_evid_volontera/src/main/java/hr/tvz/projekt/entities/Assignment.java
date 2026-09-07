package hr.tvz.projekt.entities;

import java.io.Serializable;

/**
 * Predstavlja zadatak  koji je dodijeljen određenom volonteru na konkretnom događaju.
 * <p>
 * Klasa koristi <b>Builder dizajnerski obrazac</b>
 * <p>
 * Svaki zadatak sadrži naziv, opis, trajanje izraženo u satima, pripadajućeg volontera i događaj.
 *
 * @author Ivor Županić
 * @version 1.0
 * @since Java 25
 * @see Volunteer
 * @see Event
 */
public class Assignment implements Serializable {
    private String name;
    private String description;
    private int durationHours;
    private Volunteer volunteer;
    private Event event;

    /**
     * Privatni konstruktor koji se koristi iskljucivo unutar {@link Builder} klase.
     * <p>
     * sprjecava izravno instanciranje objekta te osigurava kontrolirano
     * kreiranje putem Builder obrasca.
     *
     * @param builder instanca {@link Builder} klase koja sadrži sve potrebne podatke
     */
   private Assignment(Builder builder) {
       this.name = builder.name;
       this.description = builder.description;
       this.durationHours = builder.durationHours;
       this.volunteer = builder.volunteer;
       this.event = builder.event;
   }

    /**
     * Unutarnja staticka klasa koja implementira <b>Builder obrazac</b>
     * za izgradnju instanci klase {@link Assignment}.
     */
    public static class Builder {
        private String name;
        private String description;
        private int durationHours;
        private Volunteer volunteer;
        private Event event;

        /**
         *
         * @param name nov naziv zadatka
         * @return instanca {@link Builder} radi ulančanog pozivanja
         */
        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        /**
         * @param description opis zadatka
         * @return instanca {@link Builder}
         */
        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }
        /**
         * Postavlja trajanje zadatka u satima.
         *
         * @param durationHours broj sati trajanja zadatka
         * @return instanca {@link Builder}
         */
        public Builder setDurationHours(int durationHours) {
            this.durationHours = durationHours;
            return this;
        }
        /**
         * Dodjeljuje volontera koji je odgovoran za izvršenje zadatka.
         *
         * @param volunteer objekt tipa {@link Volunteer}
         * @return instanca {@link Builder}
         */
        public Builder setVolunteer(Volunteer volunteer) {
            this.volunteer = volunteer;
            return this;
        }
        /**
         * Dodjeljuje događaj na kojem se zadatak odvija.
         *
         * @param event objekt tipa {@link Event}
         * @return instanca {@link Builder}
         */
        public Builder setEvent(Event event) {
            this.event = event;
            return this;
        }
        /**
         * Kreira instancu klase {@link Assignment} s postavljenim parametrima.
         *
         * @return novi objekt tipa {@link Assignment}
         */
        public Assignment build() {
            return new Assignment(this);
        }
    }

    /**
     *
     * @return naziv zadatka
     */
    public String getName() {
            return name;
    }

    /**
     *
     * @return opis zadatka
     */
    public String getDescription() {
       return description;
    }

    /**
     *
     * @return duljina trajanja zadatka u satima
     */
    public int getDurationHours() {
       return durationHours;
    }

    /**
     * vraca volontera kojemu je dodjeljen zadatak
     * @return instanca {@link Volunteer}
     */
    public Volunteer getVolunteer() {
       return volunteer;
    }

    /**
     * vraca dogadaj na kojem se zadatak izvršava.
     *
     * @return instanca {@link Event}
     */
    public Event getEvent() {
       return event;
    }

}
