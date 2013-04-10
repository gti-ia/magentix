/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when the role not exists
 * in the unit.
 * 
 * @author mrodrigo
 * 
 */
public class RoleNotExistsException extends THOMASException {

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
	 * Creates a new RoleNotExistsException using the specified message.
	 * 
	 * @param message
	 */
	public RoleNotExistsException(String message) {
		super(message);
		this.content = message;
	}

}
