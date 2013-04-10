/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when the agent in unit
 * or parent unit does not play roles with position creator.
 * 
 * @author mrodrigo
 * 
 */
public class NotCreatorInUnitOrParentUnitException extends THOMASException {

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
	 * Creates a new NotCreatorInUnitOrParentUnitException using the specified
	 * message.
	 * 
	 * @param message
	 */
	public NotCreatorInUnitOrParentUnitException(String message) {
		super(message);
		this.content = message;
	}

}
