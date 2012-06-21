/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when service URL is not
 * a valid OWL-S document.
 * 
 * @author mrodrigo
 * 
 */
public class InvalidServiceURLException extends THOMASException {

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
     * Creates a new InvalidServiceURLException using the specified message.
     * 
     * @param message
     */
    public InvalidServiceURLException(String message) {
        super(message);
        this.content = message;
    }

}
