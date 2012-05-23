/**
 * 
 */
package es.upv.dsic.gti_ia.organization;

/**
 * This class represents the exception that is launched when the unit is already
 * registered.
 * 
 * @author mrodrigo
 * 
 */
public class UnitExistsException extends THOMASException {

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
     * Creates a new UnitExistsException using the specified message.
     * 
     * @param message
     */
    public UnitExistsException(String message) {
        super(message);
        this.content = message;
    }

}
