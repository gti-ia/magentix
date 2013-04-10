/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when Accessibility value
 * is incorrect.
 * 
 * @author mrodrigo
 * 
 */
public class InvalidAccessibilityException extends THOMASException {

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
	 * Creates a new InvalidAccessibilityException using the specified message.
	 * 
	 * @param message
	 */
	public InvalidAccessibilityException(String message) {
		super(message);
		this.content = message;
	}

}
