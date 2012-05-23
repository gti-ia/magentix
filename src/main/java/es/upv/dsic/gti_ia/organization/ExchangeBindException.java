/**
 * 
 */
package es.upv.dsic.gti_ia.organization;

/**
 * This class represents the exception that is launched when occurs a exchange
 * bind error.
 * 
 * @author mrodrigo
 * 
 */
public class ExchangeBindException extends THOMASException {

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
     * Creates a new ExchangeBindException using the specified message.
     * 
     * @param message
     */
    public ExchangeBindException(String message) {
        super(message);
        this.content = message;
    }

}
