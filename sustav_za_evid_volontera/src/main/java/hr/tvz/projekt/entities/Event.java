package hr.tvz.projekt.entities;

import java.io.Serializable;

/**
 * Predstavlja događaj u sustavu evidencije volontera.
 * <p>
 * Klasa sadrži osnovne informacije o događaju, uključujući grad u kojem se održava,
 * naziv događaja i njegovu adresu.
 * @author Ivor Županić
 * @version 1.0
 * @since Java 25
 * @see Volunteer
 * @see Organizer
 */
public class Event implements Serializable
{
    private String city;
    private String title;
    private String address;
    private final EventType type;


    /**
     *
     * @param city grad u kojem se dogada Event
     * @param title naziv dogadaja
     * @param address adresa dogadaja
     */
    public Event(String title, String city, String address, EventType type) {
        this.title = title;
        this.city = city;
        this.address = address;
        this.type = type;
    }



    /**
     *
     * @return grad dogadaja
     */
    public String getCity() {
        return city;
    }

    /**
     *
     * @param city novi grad dogadaja
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     *
     * @return naziv dogadaja
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title novi naziv dogadaja
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return adresu dogadaja
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address novu adresu dogadaja
     */
    public void setAddress(String address) {
        this.address = address;
    }

    public EventType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type + " - " + title + " (" + city + ", " + address + ")";
    }



}
