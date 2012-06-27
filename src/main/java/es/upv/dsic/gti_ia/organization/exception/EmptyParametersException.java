/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when any parameter is
 * empty.
 * 
 * @author mrodrigo
 * 
 */
public class EmptyParametersException extends THOMASException {

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
	 * Creates a new EmptyParametersException using the specified message.
	 * 
	 * @param message
	 */
	public EmptyParametersException(String message) {
		super(message);
		this.content = message;
	}

}
