/**
 * 
 */
package es.upv.dsic.gti_ia.organization;

/**
 * This class represents the exception that is launched when IDUnitType is not found.
 * 
 * @author mrodrigo
 * 
 */
public class IDUnitTypeNotFoundException extends THOMASException {

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
     * Creates a new IDUnitTypeNotFoundException using the specified message.
     * 
     * @param message
     */
    public IDUnitTypeNotFoundException(String message) {
        super(message);
        this.content = message;
    }

}
