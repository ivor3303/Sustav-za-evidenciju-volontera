package hr.tvz.projekt.entities;

import java.io.Serializable;

/**
 * Apstraktna klasa koja predstavlja osobu u sustavu evidencije volontera.
 * <p>
 * Sadrži osnovne osobne podatke kao što su ime, grad, e-mail adresa i dob.
 *
 * @author Ivor Županić
 * @version 1.0
 * @since Java 25
 * @see Volunteer
 * @see Organizer
 */
public abstract class Person implements Serializable {

    private String name;
    private String city;
    private String email;
    private int age;

    /**
     *
     * @param name ime i prezime osobe
     * @param city grad iz kojeg osoba dolazi
     * @param email email adresa osobe
     * @param age broj godina koliko osoba ima
     */
     public Person(String name, String city, String email, int age) {
        this.name = name;
        this.city = city;
        this.email = email;
        this.age = age;
    }

    /**
     *
     * @return ime i prezime osobe
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return grad iz kojeg osoba dolazi
     */
    public String getCity() {
        return city;
    }

    /**
     *
     * @return email adresu osobe
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @return broj godina osobe
     */
    public int getAge() {
        return age;
    }

    /**
     * postavlja novu dob osobe
     * @param age nova dob osobe
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * postavlja novi grad od kud osoba dolazi
     * @param city novi grad osobe
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * postavlja novu email adresu osobe
     * @param email nova email adresa
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Postavlja novo ime osobe
     * @param name novo ime osobe
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Apstraktna metoda koju moraju implementirati sve klase koje nasljeđuju Person.
     * <p>
     * Definira ulogu osobe u sustavu Volonter/Organizator.
     *
     * @return tekstualni naziv uloge osobe
     */
    public abstract String role(); // apstraktna metoda
}



