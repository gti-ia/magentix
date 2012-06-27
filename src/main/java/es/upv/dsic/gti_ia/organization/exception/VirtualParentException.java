/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when trying to change
 * the parent unit.
 * 
 * @author mrodrigo
 * 
 */
public class VirtualParentException extends THOMASException {

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
	 * Creates a new VirtualParentException using the specified message.
	 * 
	 * @param message
	 */
	public VirtualParentException(String message) {
		super(message);
		this.content = message;
	}

}
