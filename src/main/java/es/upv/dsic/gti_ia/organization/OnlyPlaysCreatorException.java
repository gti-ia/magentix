/**
 * 
 */
package es.upv.dsic.gti_ia.organization;

/**
 * This class represents the exception that is launched when the agent is only
 * playing the role creator.
 * 
 * @author mrodrigo
 * 
 */
public class OnlyPlaysCreatorException extends THOMASException {

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
     * Creates a new OnlyPlaysCreatorException using the specified message.
     * 
     * @param message
     */
    public OnlyPlaysCreatorException(String message) {
        super(message);
        this.content = message;
    }

}
