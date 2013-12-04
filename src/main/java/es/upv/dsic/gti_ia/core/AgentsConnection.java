package es.upv.dsic.gti_ia.core;

import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.ConnectionSettings;

import es.upv.dsic.gti_ia.organization.Configuration;

/**
 * This class work to open a Qpid broker connection.
 * 
 * @author Sergio Pajares
 * @author Javier Jorge Cano
 */
public class AgentsConnection {

	/**
	 * Used to establish a communication with a Qpid broker. Can be initialized
	 * according to the parameters specified in the settings for an agent.
	 */
	public static org.apache.qpid.transport.Connection connection = null;

	private static Configuration c = null;

	private static int numConnections = 0;
	
	public static final int MAX_CONS_PER_CON = 120;

	/**
	 * Connects with a Qpid broker taking the input connection parameters from
	 * the Settings.xml file.
	 * 
	 */
	public static void connect() {

		c = Configuration.getConfiguration();
		if (c.isSecureMode())
			return;

		if (connection != null)
			return;

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
	 * Connects to Qpid broker taking into account all the parameters specified
	 * as input.
	 * 
	 * @param qpidHost
	 *            Host where the broker is.
	 * @param qpidPort
	 *            Port where the broker is listening.
	 * @param qpidVhost
	 *            Name for the Vhost to pass to the QPid broker.
	 * @param qpdidUser
	 *            Username to pass to the QPid broker.
	 * @param qpidPassword
	 *            Password to pass to the QPid broker.
	 * @param qpidSSL
	 *            Boolean indicating whether SSL is being used or not.
	 */
	public static void connect(String qpidHost, int qpidPort, String qpidVhost,
			String qpdidUser, String qpidPassword, boolean qpidSSL) {
		connection = new Connection();
		connection.connect(qpidHost, qpidPort, qpidVhost, qpdidUser,
				qpidPassword, qpidSSL);
	}

	/**
	 * Connects to Qpid broker taking into account all the parameters specified
	 * as input.
	 * 
	 * @param qpidHost
	 *            Host where the broker is.
	 * @param qpidPort
	 *            Port where the broker is listening.
	 * @param qpidVhost
	 *            Name for the Vhost to pass to the QPid broker.
	 * @param qpdidUser
	 *            Username to pass to the QPid broker.
	 * @param qpidPassword
	 *            Password to pass to the QPid broker.
	 * @param qpidSSL
	 *            Boolean indicating whether SSL is being used or not.
	 * @param sasl_mechs
	 *            SASL mechanism used for the secure communication with the
	 *            broker.
	 */
	public static void connect(String qpidHost, int qpidPort, String qpidVhost,
			String qpdidUser, String qpidPassword, boolean qpidSSL,
			String sasl_mechs) {
		connection = new Connection();
		connection.connect(qpidHost, qpidPort, qpidVhost, qpdidUser,
				qpidPassword, qpidSSL, sasl_mechs);

	}

	/**
	 * Connects to Qpid broker taking into account the qpidhost parameter and
	 * considering the rest as defaults parameters. broker installation
	 * 
	 * @param qpidHost
	 *            Host where the broker is.
	 */
	public static void connect(String qpidHost) {
		connection = new Connection();
		connection.connect(qpidHost, 5672, "test", "guest", "guest", false);
	}

	/**
	 * Disconnects the current connection
	 * 
	 */

	public static void disconnect() {
		connection.close();
		connection = null;
	}

	/**
	 * Method to obtain a connection, it creates a new one when the object have
	 * more than 120 connections,
	 * 
	 * @return A Connection object with the configuration of the file
	 */
	public static Connection getConnection() {

		numConnections++;
		if (numConnections < MAX_CONS_PER_CON) {
			return connection;
		} else {
			numConnections = 0;
			connection = null;
			connect();
			return connection;
		}

	}

	/**
	 * @return the numConnections
	 */
	public int getNumConnections() {

		return numConnections;
	}

	/**
	 * @param numConnections
	 *            the numConnections to set
	 */
	public void setNumConnections(int numConnections) {
		this.numConnections = numConnections;
	}

}
