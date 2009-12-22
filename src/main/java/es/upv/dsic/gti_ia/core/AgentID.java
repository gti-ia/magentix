package es.upv.dsic.gti_ia.core;

import java.io.Serializable;

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
	public AgentID() {
	}

	public AgentID(String __name, String __protocol, String __host,
			String __port) {
		name = __name;
		protocol = __protocol;
		port = __port;
		host = __host;
	}

	/**
	 * Constructor allow a input like http://nombreagente@localhost:8080 or a
	 * input like AgentName, in this case there are defaults options, like
	 * protocol = "qpid", host="localhost", port="8080".
	 * 
	 * @param id
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

	public String toString() {
		String cadena = protocol + "://" + name + "@" + host + ":" + port;
		return cadena;
	}

	public String name_all() {
		String cadena = name + "@" + host + ":" + port; // +"/JADE";
		return cadena;
	}

	public String addresses_all() {
		String cadena = protocol + "://" + host + ":" + port;
		return cadena;
	}

	public String addresses_single() {
		String cadena = host + ":" + port;
		;
		return cadena;
	}

	public String getLocalName() {
		int atPos = name.lastIndexOf('@');
		if (atPos == -1)
			return name;
		else
			return name.substring(0, atPos);
	}

}