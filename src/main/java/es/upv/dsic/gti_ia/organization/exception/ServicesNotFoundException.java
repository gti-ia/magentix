/**
 * 
 */
package exception;

/**
 * This class represents the exception that is launched when the search of
 * services does not return any value.
 * 
 * @author mrodrigo
 * 
 */
public class ServicesNotFoundException extends THOMASException {

    // -----------------------------------------------------------------
    // CONSTANTS of the class
    // -----------------------------------------------------------------

    /**
     * Serialization identifier.
     */
    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------
    // CONSTRUCTORS of the class
    // -----------------------------------------------------------------

    /**
     * Creates a new ServicesNotFoundException using the specified message.
     * 
     * @param message
     */
    public ServicesNotFoundException(String message) {
        super(message);
        this.content = message;
    }

}
