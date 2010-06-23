package es.upv.dsic.gti_ia.secure;

import java.util.ArrayList;
import java.util.Properties;

import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.organization.Configuration;

import org.apache.log4j.Logger;
import org.apache.qpid.console.Session;
import org.apache.qpid.console.QMFObject;

public class MMS extends BaseAgent {

	private static MMS mms = null;
	static Logger logger = Logger.getLogger(MMS.class);

	private MMS(AgentID aid) throws Exception {
		super(aid);

	}

	/**
	 * Returns an instance of the agents MMS
	 * 
	 * @param agent
	 *            a new Agent ID
	 * @return mms agent MMS
	 */
	public static MMS getMMS(AgentID agent) {
		if (mms == null) {
			try {
				mms = new MMS(agent);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return mms;
	}

	/**
	 * Returns an instance of the agents MMS, the agentID of the agent is
	 * AgentID("MMS")
	 * 
	 * @return mms agent MMS
	 */
	public static MMS getMMS() {
		if (mms == null) {
			try {
				mms = new MMS(new AgentID("MMS"));
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return mms;
	}

	private Configuration c;
	Monitor m;
	FileWriter fichero = null;
	PrintWriter pw = null;
	private ArrayList<QMFObject> qmf = new ArrayList<QMFObject>();
	private HashMap<String, Object> hash = new HashMap<String, Object>();
	private Session sess = new Session();
	static Properties propsSecurity = new Properties();

	public void init() {
		try {
			propsSecurity.load(new FileInputStream("./configuration/security.properties"));
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			
			logger.error(e);
		}
		// Especificar la ubicación del archivo del almacén de claves
		// públicas (truststore) 
		System.setProperty("javax.net.ssl.trustStore", propsSecurity.getProperty("TrustStorePath"));
		System.setProperty("javax.net.ssl.trustStorePassword",propsSecurity.getProperty("TrustStorePassword"));

		// Especificar la ubicación del archivo keyStore 
		System.setProperty("javax.net.ssl.keyStore",propsSecurity.getProperty("KeyStorePath"));
		System.setProperty("javax.net.ssl.keyStorePassword", propsSecurity.getProperty("keyStorePassword"));

		
		c = Configuration.getConfiguration();
		m = new Monitor();
	}

	public void execute() {

		// conectamos el agente con el broker.
		this.conectToBroker();
		m.waiting();
	}

	
	public void writeAclFile(String command) {
		PrintWriter aclFile = ACLFile();
		try {
			aclFile.println(command);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Nuevamente aprovechamos el finally para
				// asegurarnos que se cierra el fichero.
				if (null != fichero)
					fichero.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}

	public void clearACLFile() {
		File fichero = new File("./certificates/broker.acl");
		fichero.delete();
		try {
			fichero.createNewFile();
		} catch (IOException ex) {
			// Logger.getLogger(ThomasGUI.class.getName()).log(Level.SEVERE,
			// null, ex);
			logger.error(ex.getMessage());
		}
	}

	private PrintWriter ACLFile() {

		try {
			// fichero = new FileWriter("./certificates/broker.acl");
			fichero = new FileWriter("./certificates/broker.acl", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw = new PrintWriter(fichero);

		return pw;
	}

	public void conectToBroker() {

		String connectionBroker = "amqp://" + c.getqpidUser() + ":"
				+ c.getqpidPassword() + "@/" + c.getqpidVhost()
				+ "?brokerlist='tcp://" + c.getqpidHost() + ":"
				+ c.getqpidPort() + "?ssl='" + c.getqpidSSL()
				+ "',sasl_mechs='" + c.getqpidsaslMechs() + "''";
		sess = new Session();
		sess.addBroker(connectionBroker);

	}

	public void readACLFile() {
		// sess.addBroker("amqp://guest:guest@/?brokerlist='tcp://gtiiaprojects.dsic.upv.es:5671?ssl='true',saslMechs='PLAIN',saslProtocol='qpidd',saslServerName='produccion''");

		// ArrayList<ClassKey> array = sess.getClasses("org.apache.qpid.acl");
		hash.put("_class", "acl");
		qmf = sess.getObjects(hash);
		qmf.get(0).invokeMethod("reloadACLFile", "");
	}

	public void finalize() {

		sess.close();
		m.advise();
	}
}
