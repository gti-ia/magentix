/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when Unit Type value
 * is incorrect.
 * 
 * @author mrodrigo
 * 
 */
public class InvalidUnitTypeException extends THOMASException {

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
     * Creates a new InvalidUnitTypeException using the specified message.
     * 
     * @param message
     */
    public InvalidUnitTypeException(String message) {
        super(message);
        this.content = message;
    }

}
