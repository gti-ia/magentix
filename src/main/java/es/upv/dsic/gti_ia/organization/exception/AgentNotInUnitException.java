/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when Agent is not in the
 * specified unit.
 * 
 * @author mrodrigo
 * 
 */
public class AgentNotInUnitException extends THOMASException {

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
     * Creates a new AgentNotInUnitException using the specified message.
     * 
     * @param message
     */
    public AgentNotInUnitException(String message) {
        super(message);
        this.content = message;
    }

}
