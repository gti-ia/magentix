/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when the agent does not
 * play any role with position supervisor or creator in unit.
 * 
 * @author mrodrigo
 * 
 */
public class NotSupervisorOrCreatorInUnitException extends THOMASException {

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
	 * Creates a new NotSupervisorOrCreatorInUnitException using the specified
	 * message.
	 * 
	 * @param message
	 */
	public NotSupervisorOrCreatorInUnitException(String message) {
		super(message);
		this.content = message;
	}

}
