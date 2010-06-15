package es.upv.dsic.gti_ia.core;

import org.apache.qpid.transport.Connection;

import es.upv.dsic.gti_ia.organization.Configuration;

/**
 * This class work to open a Qpid broker connection.
 * @author Sergio Pajares
 */
public class AgentsConnection {
	public static org.apache.qpid.transport.Connection connection;
	private static Configuration c = null;

	/**
	 * Connects with a Qpid broker taking the input connection parameters from the settings.xml file.
	 * 
	 */
	public static void connect() {
		c =  Configuration.getConfiguration();
		connection = new Connection();
		connection.connect(c.getqpidHost(),c.getqpidPort(), c.getqpidVhost(),c.getqpidUser(),c.getqpidPassword(),c.getqpidSSL());
	}

	
	/**
	 * Connects to Qpid broker taking into account all the parameters specified as input.
	 * @param qpidHost
	 * @param qpidPort
	 * @param qpidVhost
	 * @param qpdidUser
	 * @param qpidPassword
	 * @param qpidSSL
	 */
	public static void connect(String qpidHost, int qpidPort, String qpidVhost, String qpdidUser,
			String qpidPassword, boolean qpidSSL) {
		connection = new Connection();
		connection.connect(qpidHost, qpidPort, qpidVhost, qpdidUser, qpidPassword, qpidSSL);
	}
	

	
	/**
	 * Connects to Qpid broker taking into account the qpidhost parameter and considering the rest as defaults parameters.
	 * broker installation
	 * @param qpidHost
	 */
	public static void connect(String qpidHost) {
		connection = new Connection();
		connection.connect(qpidHost, 5672,  "test", "guest", "guest", false);
	}
	


}
