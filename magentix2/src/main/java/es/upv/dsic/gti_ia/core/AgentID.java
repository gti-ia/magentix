package es.upv.dsic.gti_ia.core;

import java.io.Serializable;

/**
 * Represents an identifier used to univocally recognize an agent.
 * 
 */
public final class AgentID implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// instance variables
	public String name = "";
	public String protocol = "";
	public String host = "";
	public String port = "";

	// constructors
	/**
	 * Empty constructor.
	 */
	public AgentID() {
	}

	/**
	 * Constructor for the ID.
	 * 
	 * @param __name The name of the agent.
	 * @param __protocol The protocol being used.
	 * @param __host The host where the agent is located.
	 * @param __port The port in the host where the agent can listen for messages.
	 * @see AgentID#AgentID(String) 
	 */
	public AgentID(String __name, String __protocol, String __host,
			String __port) {
		name = __name;
		protocol = __protocol;
		port = __port;
		host = __host;
	}

	/**
	 * Constructor allows a input like http://nombreagente@localhost:8080 or a
	 * input like AgentName, in this case there are defaults options, like
	 * protocol = "qpid", host="localhost", port="8080".
	 * 
	 * @param id The String containing the ID coded as the example.
	 */
	public AgentID(String id) {
		try {
			protocol = id.substring(0, id.indexOf(':'));
			name = id.substring(id.indexOf(':') + 3, id.indexOf('@'));
			host = id.substring(id.indexOf('@') + 1, id.indexOf(':', id
					.indexOf('@') + 1));
			port = id.substring(id.indexOf(':', id.indexOf('@')) + 1);
		} catch (StringIndexOutOfBoundsException e) {
			protocol = "qpid";
			name = id;
			host = "localhost";
			port = "8080";
		}

	}

	/**
	 * Creates a String containing the ID as a String.
	 * 
	 * @see AgentID#AgentID(String) 
	 */
	public String toString() {
		String cadena = protocol + "://" + name + "@" + host + ":" + port;
		return cadena;
	}

	/**
	 * Builds a string identifying this agent. The string is similar to the identifiers used in Jade.
	 * 
	 * @return The name, host and port coded in a similar fashion as the ToString method
	 * @see AgentID#toString()
	 */
	public String name_all() {
		String cadena = name + "@" + host + ":" + port; // +"/JADE";
		return cadena;
	}

	/**
	 * Builds a string identifying this agent. This string is of the form of an URL.
	 * 
	 * @return The protocol, host and port of this ID, similar to an URL.
	 */
	public String addresses_all() {
		String cadena = protocol + "://" + host + ":" + port;
		return cadena;
	}

	/**
	 * @return The host and port separated by the ':' character.
	 */
	private String addresses_single() {
		String cadena = host + ":" + port;
		;
		return cadena;
	}

	/**
	 * Builds a string identifying this agent. The string is valid inside the local domain, 
	 * since it does not qualify the machine where the agent is located.
	 * 
	 * @return Returns only the name of the agent.
	 * @see AgentID#AgentID(String, String, String, String)
	 */
	public String getLocalName() {
		int atPos = name.lastIndexOf('@');
		if (atPos == -1)
			return name;
		else
			return name.substring(0, atPos);
	}
	


}