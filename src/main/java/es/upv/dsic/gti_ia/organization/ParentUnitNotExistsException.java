/**
 * 
 */
package es.upv.dsic.gti_ia.organization;

/**
 * This class represents the exception that is launched when the parent unit
 * does not exists.
 * 
 * @author mrodrigo
 * 
 */
public class ParentUnitNotExistsException extends THOMASException {

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
     * Creates a new ParentUnitNotExistsException using the specified message.
     * 
     * @param message
     */
    public ParentUnitNotExistsException(String message) {
        super(message);
        this.content = message;
    }

}
