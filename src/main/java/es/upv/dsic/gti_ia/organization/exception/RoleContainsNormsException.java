/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when the role contains
 * associated norms.
 * 
 * @author mrodrigo
 * 
 */
public class RoleContainsNormsException extends THOMASException {

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
	 * Creates a new RoleContainsNormsException using the specified message.
	 * 
	 * @param message
	 */
	public RoleContainsNormsException(String message) {
		super(message);
		this.content = message;
	}

}
