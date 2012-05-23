/**
 * 
 */
package es.upv.dsic.gti_ia.organization;

/**
 * This class represents the exception that is launched when the agent is not
 * inside the unit and does not play any role with position creator.
 * 
 * @author mrodrigo
 * 
 */
public class NotInUnitAndNotCreatorException extends THOMASException {

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
     * Creates a new NotInUnitAndNotCreatorException using the specified
     * message.
     * 
     * @param message
     */
    public NotInUnitAndNotCreatorException(String message) {
        super(message);
        this.content = message;
    }

}
