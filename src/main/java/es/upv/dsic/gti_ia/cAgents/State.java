package es.upv.dsic.gti_ia.cAgents;

/**
 * 
 * @author Ricard Lopez Fogues
 * 
 */

public abstract class State implements Cloneable {

	// PENDIENTE: creo que es mejor usar enumerados en lugar de constantes
	// numericas

	final static int ACTION = 0;
	final static int BEGIN = 1;
	final static int FINAL = 2;
	final static int RECEIVE = 3;
	final static int SEND = 4;
	final static int WAIT = 5;
	final static int SENDING_ERRORS = 6;
	final static int CANCEL = 7;
	final static int TERMINATED_FATHER = 8;
	final static int NOT_ACCEPTED_MESSAGES = 9;
	final static int SHUTDOWN = 10;

	int type;

	private String name;
	
	/**
	 * Creates a new state
	 * @param n
	 */
	protected State(String n) {
		name = n;
	}

	/**
	 * Returns this state's name
	 * @return this state's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns this state's type
	 * @return this state's type
	 */
	int getType() {
		return type;
	}

	/**
	 * Sets this state's type
	 * @param t type of the state
	 */
	protected void setType(int t) {
		type = t;
	}

	/**
	 * Sets this state's name
	 * @param name of the state
	 */
	protected void setName(String name) {
		this.name = name;
	}

	protected Object clone() {
		Object obj = null;
		try {
			obj = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}

}
