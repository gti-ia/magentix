/**
 * 
 */
package exception;

/**
 * This class represents the exception that is launched when can not connect to
 * DB.
 * 
 * @author mrodrigo
 * 
 */
public class DBConnectionException extends THOMASException {

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
     * Creates a new DBConnectionException using the specified message.
     * 
     * @param message
     */
    public DBConnectionException(String message) {
        super(message);
        this.content = message;
    }

}
