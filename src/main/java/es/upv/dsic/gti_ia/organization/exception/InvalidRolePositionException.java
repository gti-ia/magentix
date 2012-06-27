/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when Role Position value
 * is incorrect.
 * 
 * @author mrodrigo
 * 
 */
public class InvalidRolePositionException extends THOMASException {

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
	 * Creates a new InvalidRolePositionException using the specified message.
	 * 
	 * @param message
	 */
	public InvalidRolePositionException(String message) {
		super(message);
		this.content = message;
	}

}
