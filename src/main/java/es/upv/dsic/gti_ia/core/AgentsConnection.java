package es.upv.dsic.gti_ia.core;


import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.ConnectionSettings;

import es.upv.dsic.gti_ia.organization.Configuration;

/**
 * This class work to open a Qpid broker connection.
 * @author Sergio Pajares
 */
public class AgentsConnection {
	public static org.apache.qpid.transport.Connection connection;

	private static Configuration c = null;

	/**
	 * Connects with a Qpid broker taking the input connection parameters from the Settings.xml file.
	 * 
	 */
	public static void connect() {
		c =  Configuration.getConfiguration();
		connection = new Connection();
	
		ConnectionSettings connectSettings = new ConnectionSettings();
		connectSettings.setHost(c.getqpidHost());
		connectSettings.setPort(c.getqpidPort());
		connectSettings.setVhost(c.getqpidVhost());
		connectSettings.setUsername(c.getqpidUser());
		connectSettings.setPassword(c.getqpidPassword());
		connectSettings.setUseSSL(c.getqpidSSL());

		connection.connect(connectSettings);

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
	
	public static void connect(String qpidHost, int qpidPort, String qpidVhost, String qpdidUser,
			String qpidPassword, boolean qpidSSL, String sasl_mechs) {
		connection = new Connection();
		connection.connect(qpidHost, qpidPort, qpidVhost, qpdidUser, qpidPassword, qpidSSL,sasl_mechs);
	
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
