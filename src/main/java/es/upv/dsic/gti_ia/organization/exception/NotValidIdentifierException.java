/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when identifier is not allowed.
 * It uses any reserved word or invalid character.
 * 
 * @author mrodrigo
 * 
 */
public class NotValidIdentifierException extends THOMASException {

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
	 * Creates a new InvalidPositionException using the specified message.
	 * 
	 * @param message
	 */
	public NotValidIdentifierException(String message) {
		super(message);
		this.content = message;
	}

}
