/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when the agent is not
 * inside the unit or the parent unit.
 * 
 * @author mrodrigo
 * 
 */
public class NotInUnitOrParentUnitException extends THOMASException {

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
	 * Creates a new NotInUnitOrParentUnitException using the specified message.
	 * 
	 * @param message
	 */
	public NotInUnitOrParentUnitException(String message) {
		super(message);
		this.content = message;
	}

}
