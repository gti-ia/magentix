/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when the agent is
 * already playing the role.
 * 
 * @author mrodrigo
 * 
 */
public class PlayingRoleException extends THOMASException {

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
     * Creates a new PlayingRoleException using the specified message.
     * 
     * @param message
     */
    public PlayingRoleException(String message) {
        super(message);
        this.content = message;
    }

}
