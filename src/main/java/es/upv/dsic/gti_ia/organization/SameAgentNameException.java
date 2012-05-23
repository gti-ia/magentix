/**
 * 
 */
package es.upv.dsic.gti_ia.organization;

/**
 * This class represents the exception that is launched when the TargetAgentName
 * is the same than AgentName.
 * 
 * @author mrodrigo
 * 
 */
public class SameAgentNameException extends THOMASException {

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
     * Creates a new SameAgentNameException using the specified message.
     * 
     * @param message
     */
    public SameAgentNameException(String message) {
        super(message);
        this.content = message;
    }

}
