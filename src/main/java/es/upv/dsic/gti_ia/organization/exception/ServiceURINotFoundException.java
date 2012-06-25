/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when the URI of the
 * service is not found in Jena DB.
 * 
 * @author mrodrigo
 * 
 */
public class ServiceURINotFoundException extends THOMASException {

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
     * Creates a new ServiceURINotFoundException using the specified message.
     * 
     * @param message
     */
    public ServiceURINotFoundException(String message) {
        super(message);
        this.content = message;
    }

}
