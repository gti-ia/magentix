/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when data type of input
 * or output is invalid.
 * 
 * @author mrodrigo
 * 
 */
public class InvalidDataTypeException extends THOMASException {

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
	 * Creates a new InvalidDataTypeException using the specified message.
	 * 
	 * @param message
	 */
	public InvalidDataTypeException(String message) {
		super(message);
		this.content = message;
	}

}
