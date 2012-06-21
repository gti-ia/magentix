/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when the agent does not
 * play any role with position member or creator in unit.
 * 
 * @author mrodrigo
 * 
 */
public class NotMemberOrCreatorInUnitException extends THOMASException {

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
     * Creates a new NotMemberOrCreatorInUnitException using the specified
     * message.
     * 
     * @param message
     */
    public NotMemberOrCreatorInUnitException(String message) {
        super(message);
        this.content = message;
    }

}
