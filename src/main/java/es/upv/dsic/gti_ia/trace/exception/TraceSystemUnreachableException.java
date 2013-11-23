/**
 * 
 */
package es.upv.dsic.gti_ia.trace.exception;

/**
 * This class represents the exception that is launched when Trace Manager is unreachable.
 * 
 * @author Jose Alemany Bordera - jalemany1@dsic.upv.es
 * 
 */
public class TraceSystemUnreachableException extends Exception {

	// -----------------------------------------------------------------
	// CONSTRUCTORS of the class
	// -----------------------------------------------------------------

	/**
	 * Creates a new TraceSystemUnreachableException using the specified message.
	 * 
	 * @param message
	 */
	public TraceSystemUnreachableException() {
		super("Trace Manager is unreachable.");
	}

}
