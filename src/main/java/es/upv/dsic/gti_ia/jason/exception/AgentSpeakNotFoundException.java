/**
 * 
 */
package es.upv.dsic.gti_ia.jason.exception;

/**
 * This class represents the exception that is launched when Agent not exists.
 * 
 * @author mrodrigo
 * 
 */
public class AgentSpeakNotFoundException extends Exception {

	// -----------------------------------------------------------------
	// CONSTANTS of the class
	// -----------------------------------------------------------------

	/**
	 * Serialization identifier.
	 */
	private static final long serialVersionUID = 1L;
	private String content;

	// -----------------------------------------------------------------
	// CONSTRUCTORS of the class
	// -----------------------------------------------------------------

	/**
	 * Creates a new AgentNotExistsException using the specified message.
	 * 
	 * @param message
	 */
	public AgentSpeakNotFoundException(String message) {
		super(message);
		this.content = message;
	}

}
