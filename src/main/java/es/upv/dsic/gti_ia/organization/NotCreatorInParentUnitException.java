/**
 * 
 */
package es.upv.dsic.gti_ia.organization;

/**
 * This class represents the exception that is launched when none of the agents
 * in unit plays roles with position creator.
 * 
 * @author mrodrigo
 * 
 */
public class NotCreatorInParentUnitException extends THOMASException {

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
     * Creates a new NotCreatorInParentUnitException using the specified message.
     * 
     * @param message
     */
    public NotCreatorInParentUnitException(String message) {
        super(message);
        this.content = message;
    }

}
