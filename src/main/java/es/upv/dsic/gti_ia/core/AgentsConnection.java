package es.upv.dsic.gti_ia.core;

import org.apache.qpid.transport.Connection;

import es.upv.dsic.gti_ia.organization.Configuration;

/**
 * This class work to open a Qpid broker connection, will be used in each
 * constuctor of a new agent.
 * 
 * @author Sergio Pajares
 * 
 */
public class AgentsConnection {
	public static org.apache.qpid.transport.Connection connection;
	private static Configuration c = null;

	/**
	 * Connect with a Qpid broker, take  the connection from the file settings.xml.
	 * 
	 */
	public static void connect() {
		c =  Configuration.getConfiguration();
		connection = new Connection();
		connection.connect(c.getqpidHost(),c.getqpidPort(), c.getqpidVhost(),c.getqpidUser(),c.getqpidPassword(),c.getqpidSSL());
	}

	
	/**
	 * Connect with a Qpid broker
	 * 
	 * @param url
	 * @param port
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param b
	 */
	public static void connect(String url, int port, String p1, String p2,
			String p3, boolean b) {
		connection = new Connection();
		connection.connect(url, port, p1, p2, p3, b);
	}
	
	/**
	 * Connect with a broker located in url, with the default settings of a Qpid
	 * broker installation
	 * 
	 * @param url
	 */
	public static void connect(String url) {
		connection = new Connection();
		connection.connect(url, 5672, "test", "guest", "guest", false);
	}
	


}
