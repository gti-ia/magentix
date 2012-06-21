/**
 * 
 */
package es.upv.dsic.gti_ia.organization.exception;

/**
 * This class represents the exception that is launched when occurs an error
 * trying to insert a value in a table.
 * 
 * @author mrodrigo
 * 
 */
public class InsertingTableException extends THOMASException {

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
     * Creates a new InsertingTableException using the specified message.
     * 
     * @param message
     */
    public InsertingTableException(String message) {
        super(message);
        this.content = message;
    }

}
