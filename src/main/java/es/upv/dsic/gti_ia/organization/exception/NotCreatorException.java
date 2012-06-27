/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when agents in unit does
 * not play roles with position creator.
 * 
 * @author mrodrigo
 * 
 */
public class NotCreatorException extends THOMASException {

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
	 * Creates a new NotCreatorException using the specified message.
	 * 
	 * @param message
	 */
	public NotCreatorException(String message) {
		super(message);
		this.content = message;
	}

}
