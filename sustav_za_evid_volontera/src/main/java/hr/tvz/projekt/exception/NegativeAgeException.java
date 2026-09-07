package hr.tvz.projekt.exception;

/**
 * Ova iznimka oznacava situaciju kada je unesena neispravna dob korisnika.
 * <p>
 * rijec je o unchecked exceptionu koji se nasljeđuje iz {@link RuntimeException},
 * @author Ivor Županić
 * @version 1.0
 * @since Java 25
 * @see InvalidEmailException
 * @see EmptyEmailException
 */
public class NegativeAgeException extends RuntimeException {
    /**
     * Inicijalizira iznimku s unaprijed definiranom porukom o pogresci.
     * <p>
     * Koristi se kada unesena dob nije u dozvoljenom rasponu.
     */
    public NegativeAgeException() {
        System.out.println("Neispravan unos godina");
    }
    /**
     * Inicijalizira iznimku s prilagodenom porukom o pogresci
     *
     * @param message tekstualni opis pogreske
     */
    public NegativeAgeException(String message) {
        super(message);
    }
    /**
     * Inicijalizira iznimku s porukom i uzrokom
     *
     * @param message opis pogreske
     * @param cause   uzrok iznimke (druga iznimka koja je dovela do ove)
     */
    public NegativeAgeException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Inicijalizira iznimku samo s uzrokom, bez vlastite poruke.
     *
     * @param cause uzrok iznimke
     */
    public NegativeAgeException(Throwable cause) {
        super(cause);
    }
}
