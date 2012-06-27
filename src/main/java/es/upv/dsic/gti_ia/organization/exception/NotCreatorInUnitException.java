/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when the agent in unit
 * does not play roles with position creator.
 * 
 * @author mrodrigo
 * 
 */
public class NotCreatorInUnitException extends THOMASException {

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
	 * Creates a new NotCreatorInUnitException using the specified message.
	 * 
	 * @param message
	 */
	public NotCreatorInUnitException(String message) {
		super(message);
		this.content = message;
	}

}
