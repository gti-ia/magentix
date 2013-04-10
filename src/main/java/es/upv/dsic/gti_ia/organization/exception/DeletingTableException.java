/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when occurs an error
 * trying to delete a value from a table.
 * 
 * @author mrodrigo
 * 
 */
public class DeletingTableException extends THOMASException {

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
	 * Creates a new DeletingTableException using the specified message.
	 * 
	 * @param message
	 */
	public DeletingTableException(String message) {
		super(message);
		this.content = message;
	}

}
