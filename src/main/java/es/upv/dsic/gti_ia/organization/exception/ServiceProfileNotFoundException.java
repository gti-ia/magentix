/**
 * 
 */
package exception;

/**
 * This class represents the exception that is launched when the profile of the
 * service is not found in Jena DB.
 * 
 * @author mrodrigo
 * 
 */
public class ServiceProfileNotFoundException extends THOMASException {

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
     * Creates a new ServiceProfileNotFoundException using the specified message.
     * 
     * @param message
     */
    public ServiceProfileNotFoundException(String message) {
        super(message);
        this.content = message;
    }

}
