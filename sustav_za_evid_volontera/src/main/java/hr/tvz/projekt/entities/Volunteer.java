package hr.tvz.projekt.entities;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Predstavlja volontera u sustavu evidencije.
 * <p>
 * Klasa nasljeđuje {@link Person} i dodaje atribut {@code skill} koji opisuje
 * specifičnu vještinu volontera. Koristi se za praćenje kompetencija
 * i dodjeljivanje zadataka unutar događaja.
 *
 * <p>
 *
 * @author Ivor Županić
 * @version 1.0
 * @since Java 25
 * @see Person
 * @see Skill
 */
public class Volunteer extends Person implements Serializable {

    private Skill skill;

    /**
     *
     * @param name ime i prezime volontera
     * @param city grad iz kojeg volonter dolazi
     * @param email email adresa volontera
     * @param age koliko godina ima volonter
     * @param skill objekt tipa {@link Skill }, vjestine koje volonter posjeduje
     */
    public Volunteer(String name, String city, String email , int age, Skill skill) {
        super(name, city, email, age);
        this.skill = skill;
    }

    /**
     *
     * @return objekt tipa {@link Skill} koji predstavlja vjestinu volontera
     */
    public Skill getSkill() {

        return skill;
    }

    /**
     *
     * @param skill objekt tipa{@link Skill} predstavlja novu vjestinu volontera
     */
    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    /**
     *
     * @return tekstualni naziv uloge volonter
     */
    @Override
    public String role() {

        return "Volunteer";
    }
}
