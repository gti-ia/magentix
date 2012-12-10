/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when parameter is not valid.
 * 
 * @author jbellver
 * 
 */
public class InvalidExpressionException extends THOMASException {

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
	public InvalidExpressionException(String message) {
		super(message);
		this.content = message;
	}

}
