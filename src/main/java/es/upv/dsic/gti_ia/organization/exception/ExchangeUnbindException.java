/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when occurs an exchange
 * unbind error.
 * 
 * @author mrodrigo
 * 
 */
public class ExchangeUnbindException extends THOMASException {

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
	 * Creates a new ExchangeUnbindException using the specified message.
	 * 
	 * @param message
	 */
	public ExchangeUnbindException(String message) {
		super(message);
		this.content = message;
	}

}
