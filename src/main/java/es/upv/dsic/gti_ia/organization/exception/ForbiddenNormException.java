/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when a norm forbidden the service execution..
 * 
 * 
 * 
 */
public class ForbiddenNormException extends THOMASException {

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
	 * Creates a new ForbiddenNormException using the specified message.
	 * 
	 * @param message
	 */
	public ForbiddenNormException(String message) {
		super(message);
		this.content = message;
	}

}
