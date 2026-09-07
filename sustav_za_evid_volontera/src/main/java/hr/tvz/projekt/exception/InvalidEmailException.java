package hr.tvz.projekt.exception;


/**
 * Ova iznimka oznacava situaciju kada unesena email adresa nije valjana.
 * <p>
 * Baca se kada unos email adrese ne sadrzi potrebne elemente formata,
 * poput znaka '@' ili ispravne domene (npr. ".com", ".hr", ".org").
 * <p>
 * Riječ je o checked exceptionu
 *
 * @author Ivor Županić
 * @version 1.0
 * @since Java 25
 * @see EmptyEmailException
 */
public class InvalidEmailException extends Exception {

    /**
     * Inicijalizira iznimku s unaprijed definiranom porukom o pogresci
     * <p>
     * Koristi se kada email adresa ne ispunjava osnovne uvjete ispravnosti
     */
    public InvalidEmailException() {
        super("Email adresa nije valjana!");
    }

    /**
     * Inicijalizira iznimku s prilagodenom porukom o pogresci
     *
     * @param message opis pogreske koji preciznije objasnjava problem
     */
    public InvalidEmailException(String message) {
        super(message);
    }
    /**
     * Inicijalizira iznimku s porukom i uzrokom
     *
     * @param message tekstualni opis pogreške
     * @param cause   uzrok iznimke (npr druga iznimka koja je dovela do ove)
     */
    public InvalidEmailException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Inicijalizira iznimku samo s uzrokom, bez vlastite poruke.
     *
     * @param cause uzrok iznimke
     */
    public InvalidEmailException(Throwable cause) {
        super(cause);
    }
}
