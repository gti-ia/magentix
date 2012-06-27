/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when the visibility of
 * the role is private and the agent does not play any role in the unit.
 * 
 * @author mrodrigo
 * 
 */
public class VisibilityRoleException extends THOMASException {

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
	 * Creates a new VisibilityRoleException using the specified message.
	 * 
	 * @param message
	 */
	public VisibilityRoleException(String message) {
		super(message);
		this.content = message;
	}

}
