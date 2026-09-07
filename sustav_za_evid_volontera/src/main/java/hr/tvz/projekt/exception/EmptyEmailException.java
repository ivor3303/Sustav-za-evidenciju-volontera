package hr.tvz.projekt.exception;
/**
 * Ova iznimka opisuje situaciju kada korisnik pokusava unijeti praznu email adresu.
 * <p>
 * Koristi se kao checked exception
 * @author Ivor Županić
 * @version 1.0
 * @since Java 25
 * @see InvalidEmailException
 */
public class EmptyEmailException extends Exception{

    /**
     * Inicijalizira iznimku s unaprijed definiranom porukom o pogresci
     * <p>
     * Koristi se kada se ne unese nikakva email adresa.
     */
    public EmptyEmailException() { // naprimjer naslov greske
        super("Ne može se unjeti prazna email adresa!");
    }
    /**
     * Inicijalizira iznimku s prilagodenom porukom
     *
     * @param message detaljnija poruka o pogrešci
     */
    public EmptyEmailException(String message) { // koja se tocno greska dogodila
        super(message);
    }
    /**
     * Inicijalizira iznimku s prilagođenom porukom i uzrokom.
     *
     * @param message opis pogreške
     * @param cause   uzrok iznimke (druga iznimka koja ju je izazvala)
     */
    public EmptyEmailException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Inicijalizira iznimku s uzrokom, bez vlastite poruke.
     *
     * @param cause uzrok iznimke
     */
    public EmptyEmailException(Throwable cause) {
        super(cause);
    }

}
