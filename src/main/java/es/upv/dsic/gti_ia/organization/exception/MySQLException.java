/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when occurs a MySQL
 * error.
 * 
 * @author mrodrigo
 * 
 */
public class MySQLException extends THOMASException {

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
	 * Creates a new MySQLException using the specified message.
	 * 
	 * @param message
	 */
	public MySQLException(String message) {
		super(message);
		this.content = message;
	}

}
