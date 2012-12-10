/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when the action is not valid OMS Action.
 * 
 * @author jbellver
 * 
 */
public class InvalidOMSActionException extends THOMASException {

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
	 * Creates a new AgentNotExistsException using the specified message.
	 * 
	 * @param message
	 */
	public InvalidOMSActionException(String message) {
		super(message);
		this.content = message;
	}

}
