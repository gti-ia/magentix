/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when the norm is already
 * registered in the unit.
 * 
 * @author mrodrigo
 * 
 */
public class NormExistsInUnitException extends THOMASException {

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
	 * Creates a new RoleExistsInUnitException using the specified message.
	 * 
	 * @param message
	 */
	public NormExistsInUnitException(String message) {
		super(message);
		this.content = message;
	}

}
