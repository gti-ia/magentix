package es.upv.dsic.gti_ia.organization;

import java.util.*;
import java.io.*;

/**
 * This class reads the contents of Settings.xml file that found in the
 * configuration directory
 * 
 * @author Jose Alemany Bordera - jalemany1@dsic.upv.es
 */
public class Configuration {
	
	private static Configuration configuration = null;

	private String OMSServiceDescriptionLocation;
	private String SFServiceDescriptionLocation;
	private int Bridge_http_port;
	private int HttpInterface_port;
	
	private boolean isTomcat;
	private String os;
	private String pathTomcat;
	
	private String databaseServer;
	private String databaseName;
	private String databaseUser;
	private String databasePassword;
	
	private String qpidHost;
	private int qpidPort;
	private String qpidVhost;
	private String qpidUser;
	private String qpidPassword;
	private boolean qpidSsl;
	private boolean isSecure;
	private String saslMechs;
	
	private String jenadbURL;
	private String jenadbType;
	private String jenadbDriver;

	private char traceMask[] = new char[10];

	
	private Configuration() {
		this.load();
	}

	/**
	 * This method returns the instance configuration using singleton
	 * 
	 * @return configuration
	 */
	public static Configuration getConfiguration() {
		if (configuration == null)
			configuration = new Configuration();
		return configuration;
	}
	
	/**
	 * Full path where are the owl's deployed the services of the OMS
	 * 
	 * @return OMSServiceDescriptionLocation
	 */
	public String getOMSServiceDescriptionLocation() {
		return this.OMSServiceDescriptionLocation;
	}

	/**
	 * Full path where are the owl's deployed the services of the SF
	 * 
	 * @return SFServiceDesciptionLocation
	 */
	public String getSFServiceDescriptionLocation() {
		return this.SFServiceDescriptionLocation;
	}

	/**
	 * Http port where BridgeAgentOutIn runs.
	 * 
	 * @return
	 */
	public int getBridgeHttpPort() {
		return this.Bridge_http_port;
	}

	/**
	 * Http port where HttpInterface runs.
	 * 
	 * @return
	 */
	public int getHttpInterfacepPort() {
		return this.HttpInterface_port;
	}
	
	/**
	 * If TOMCAT is active
	 * 
	 * @return
	 */
	public boolean getIsTomcat() {
		return this.isTomcat;
	}
	
	/**
	 * Path where the TOMCAT is located
	 * 
	 * @return
	 */
	public String getOS() {
		return this.os;
	}

	/**
	 * Path where the TOMCAT is located
	 * 
	 * @return
	 */
	public String getPathTomcat() {
		return this.pathTomcat;
	}

	/**
	 * Name of the database server
	 * 
	 * @return serverName ex. localhost
	 */
	public String getdatabaseServer() {
		return this.databaseServer;
	}

	/**
	 * Name of the database
	 * 
	 * @return databaseName
	 */
	public String getdatabaseName() {
		return this.databaseName;
	}

	/**
	 * User name of the database
	 * 
	 * @return userName
	 */
	public String getdatabaseUser() {
		return this.databaseUser;
	}

	/**
	 * User password of the database
	 * 
	 * @return password
	 */
	public String getdatabasePassword() {
		return this.databasePassword;
	}

	/**
	 * Qpid host
	 * 
	 * @return qpidHost
	 */
	public String getqpidHost() {
		return this.qpidHost;
	}

	/**
	 * Qpid port
	 * 
	 * @return port
	 */
	public int getqpidPort() {

		return this.qpidPort;

	}

	/**
	 * Virtual Qpid host
	 * 
	 * @return Virtual host
	 */
	public String getqpidVhost() {
		return this.qpidVhost;
	}

	/**
	 * Qpid user
	 * 
	 * @return user
	 */
	public String getqpidUser() {
		return this.qpidUser;
	}

	/**
	 * Qpid user password
	 * 
	 * @return password
	 */
	public String getqpidPassword() {
		return this.qpidPassword;
	}

	/**
	 * Qpid ssl
	 * 
	 * @return SSl
	 */
	public boolean getqpidSSL() {
		return this.qpidSsl;

	}

	/**
	 * If the platform is in secure mode or not
	 * 
	 * @return isSecure
	 */
	public boolean isSecureMode() {
		return this.isSecure;
	}

	/**
	 * Qpid sasl Mechs
	 */
	public String getqpidsaslMechs() {
		return this.saslMechs;
	}

	/**
	 * Jena database URL
	 * 
	 * @return URL
	 */
	public String getjenadbURL() {
		return this.jenadbURL;
	}

	/**
	 * Type of jena database
	 * 
	 * @return type
	 */
	public String getjenadbType() {
		return this.jenadbType;
	}

	/**
	 * Driver of jena database
	 * 
	 * @return driver
	 */
	public String getjenadbDriver() {
		return this.jenadbDriver;
	}

	/**
	 * Initial mask of the TraceManager.
	 * 
	 * @return an encoded string with the mask.
	 */
	public String getTraceMask() {
		return new String(this.traceMask);
	}

	/**
	 * This method load all Settings.xml values in properties.
	 */
	private void load() {
		Properties properties = new Properties();

		try {
			
			String fileName = "Settings.xml";

			InputStream is = new FileInputStream("configuration/" + fileName);

			properties.loadFromXML(is);
		
			//Thomas Properties
			this.OMSServiceDescriptionLocation = properties.getProperty("OMSServiceDescriptionLocation", "http://localhost:8080/omsservices/services/");
			this.SFServiceDescriptionLocation = properties.getProperty("SFServiceDescriptionLocation", "http://localhost:8080/sfservices/services/");
			this.Bridge_http_port = Integer.parseInt(properties.getProperty("BridgeAgentOutInPort", "8082"));
			this.HttpInterface_port = Integer.parseInt(properties.getProperty("HttpInterfacePort", "8081"));

			//Servlets Server Properties
			this.isTomcat = Boolean.parseBoolean(properties.getProperty("tomcat", "false"));
			this.os = properties.getProperty("os", "linux");
			this.pathTomcat = properties.getProperty("pathTomcat", "../apache-tomcat-6.0.20");
			
			//MySQL Properties
			this.databaseServer = properties.getProperty("serverName", "localhost");
			this.databaseName = properties.getProperty("databaseName", "thomas");
			this.databaseUser = properties.getProperty("userName", "thomas");
			this.databasePassword = properties.getProperty("password", "thomas");
			
			//Qpid Broker Properties
			this.qpidHost = properties.getProperty("host", "localhost");
			this.qpidPort = Integer.parseInt(properties.getProperty("port", "5672"));
			this.qpidVhost = properties.getProperty("vhost", "test");
			this.qpidUser = properties.getProperty("user", "guest");
			this.qpidPassword = properties.getProperty("pass", "guest");
			this.qpidSsl = Boolean.parseBoolean(properties.getProperty("ssl", "false"));
			this.isSecure = Boolean.parseBoolean(properties.getProperty("secureMode", "false"));
			this.saslMechs = properties.getProperty("saslMechs", "EXTERNAL");
			
			//Jena Properties
			this.jenadbURL = properties.getProperty("dbURL", "jdbc:mysql://localhost/thomas");
			this.jenadbType = properties.getProperty("dbType", "MySQL");
			this.jenadbDriver = properties.getProperty("dbDriver", "com.mysql.jdbc.Driver");
			
			//Trace Properties
			this.traceMask[0] = Boolean.parseBoolean(properties.getProperty("TraceLifeCycleServices", "true")) ? '1' : '0';
			this.traceMask[1] = Boolean.parseBoolean(properties.getProperty("TraceCustomServices", "true")) ? '1' : '0';
			this.traceMask[2] = Boolean.parseBoolean(properties.getProperty("TraceMessages", "true")) ? '1' : '0';
			this.traceMask[3] = Boolean.parseBoolean(properties.getProperty("TraceMessagesDetail", "true")) ? '1' : '0';
			this.traceMask[4] = Boolean.parseBoolean(properties.getProperty("TraceListEntities", "true")) ? '1' : '0';
			this.traceMask[5] = Boolean.parseBoolean(properties.getProperty("TraceListServices", "true")) ? '1' : '0';
			this.traceMask[6] = Boolean.parseBoolean(properties.getProperty("TraceSubscribeToAllServices", "false")) ? '1' : '0';
			this.traceMask[7] = Boolean.parseBoolean(properties.getProperty("TraceWelcomeServices", "true")) ? '1' : '0';
			this.traceMask[8] = Boolean.parseBoolean(properties.getProperty("TraceUpdateServices", "false")) ? '1' : '0';
			this.traceMask[9] = Boolean.parseBoolean(properties.getProperty("TraceDieServices", "false")) ? '1' : '0';

		} catch (IOException e) {
			System.out.print(e);
			return;
		}
	}
}