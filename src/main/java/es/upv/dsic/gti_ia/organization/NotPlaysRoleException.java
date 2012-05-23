/**
 * 
 */
package es.upv.dsic.gti_ia.organization;

/**
 * This class represents the exception that is launched when the agent does not
 * play the role.
 * 
 * @author mrodrigo
 * 
 */
public class NotPlaysRoleException extends THOMASException {

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
     * Creates a new NotPlaysRoleException using the specified message.
     * 
     * @param message
     */
    public NotPlaysRoleException(String message) {
        super(message);
        this.content = message;
    }

}
