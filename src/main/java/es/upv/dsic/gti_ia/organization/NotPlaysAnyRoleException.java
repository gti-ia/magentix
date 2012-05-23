/**
 * 
 */
package es.upv.dsic.gti_ia.organization;

/**
 * This class represents the exception that is launched when the agent does not
 * play any role.
 * 
 * @author mrodrigo
 * 
 */
public class NotPlaysAnyRoleException extends THOMASException {

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
     * Creates a new NotPlaysAnyRoleException using the specified message.
     * 
     * @param message
     */
    public NotPlaysAnyRoleException(String message) {
        super(message);
        this.content = message;
    }

}
