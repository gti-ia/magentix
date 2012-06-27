/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when the role is played
 * by some agents.
 * 
 * @author mrodrigo
 * 
 */
public class RoleInUseException extends THOMASException {

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
	 * Creates a new RoleInUseException using the specified message.
	 * 
	 * @param message
	 */
	public RoleInUseException(String message) {
		super(message);
		this.content = message;
	}

}
