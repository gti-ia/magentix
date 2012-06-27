/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when information is
 * already included in service.
 * 
 * @author mrodrigo
 * 
 */
public class AlreadyRegisteredException extends THOMASException {

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
	 * Creates a new AlreadyRegisteredException using the specified message.
	 * 
	 * @param message
	 */
	public AlreadyRegisteredException(String message) {
		super(message);
		this.content = message;
	}

}
