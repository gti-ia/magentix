/**
 * 
 */
package es.upv.dsic.gti_ia.organization;

/**
 * This class represents the exception that is launched when when the selected
 * unit is the virtual unit.
 * 
 * @author mrodrigo
 * 
 */
public class VirtualUnitException extends THOMASException {

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
     * Creates a new VirtualUnitException using the specified message.
     * 
     * @param message
     */
    public VirtualUnitException(String message) {
        super(message);
        this.content = message;
    }

}
