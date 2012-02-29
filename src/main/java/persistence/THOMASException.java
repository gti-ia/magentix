package persistence;

/**
 * This class represents a generic THOMASException,
 * 
 * 
 */

public class THOMASException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String content;

	public THOMASException(String message) {
		super(message);
		this.content = message;
	}

	public String getContent() {
		return content;
	}
}
