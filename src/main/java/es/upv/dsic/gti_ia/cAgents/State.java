package es.upv.dsic.gti_ia.cAgents;

/**
 * 
 * @author Ricard Lopez Fogues
 * 
 */

public abstract class State implements Cloneable {

	// PENDIENTE: creo que es mejor usar enumerados en lugar de constantes
	// numéricas

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

	public State(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}

	int getType() {
		return type;
	}

	void setType(int t) {
		type = t;
	}

	void setName(String name) {
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
