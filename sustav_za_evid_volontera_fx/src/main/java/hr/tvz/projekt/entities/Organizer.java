package hr.tvz.projekt.entities;

import java.io.Serializable;

/**
 * Predstavlja organizatora u sustavu evidencije volontera.
 * <p>
 * @author Ivor Županić
 * @version 1.0
 * @since Java 25
 * @see Person
 * @see Volunteer
 */
public class Organizer extends Person implements Serializable {

    /**
     *
     * @param name ime i prezime organizatora
     * @param city grad od kud dolazi organizator
     * @param email email adresa organizatora
     * @param age broj godina organizatora
     */
    public Organizer(String name, String city, String email, int age){

        super(name, city, email, age);
    }

    /**
     *
     * @return tekstualni naziv Organizator
     */
    @Override
    public String role() {
        return "Organizer";
    }
}
