/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when the parent unit is
 * the same than unit.
 * 
 * @author mrodrigo
 * 
 */
public class SameUnitException extends THOMASException {

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
     * Creates a new SameUnitException using the specified message.
     * 
     * @param message
     */
    public SameUnitException(String message) {
        super(message);
        this.content = message;
    }

}
