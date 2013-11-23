package es.upv.dsic.gti_ia.trace.exception;

/**
 * Exception generated each time an operation forbidden by the trace mask have
 * been tried to be executed.
 * 
 * @author Jose Vicente Ruiz Cepeda (jruiz1@dsic.upv.es)
 */
public class TraceServiceNotAllowedException extends Exception {
	
	// -----------------------------------------------------------------
	// CONSTRUCTORS of the class
	// -----------------------------------------------------------------

	/**
	 * Creates a new TraceServiceNotAllowedException using the specified message.
	 * 
	 * @param message
	 */
	public TraceServiceNotAllowedException() {
		super("Operation forbidden by the trace mask have been tried to be executed.");
	}
}