/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when Agent not exists.
 * 
 * @author mrodrigo
 * 
 */
public class AgentNotExistsException extends THOMASException {

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
	public AgentNotExistsException(String message) {
		super(message);
		this.content = message;
	}

}
