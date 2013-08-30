package es.upv.dsic.gti_ia.organization;

import java.util.*;
import java.io.*;
import java.io.InputStream;

/**
 * This class reads the contents of Settings.xml file that found in the
 * configuration directory
 */
public class Configuration {
	private static final String TRACE_MASK_KEY = "traceMask";

	private String databaseServer;
	private String databaseName;
	private String databaseUser;
	private String databasePassword;
	private String OMSServiceDescriptionLocation;
	private String SFServiceDescriptionLocation;
	private String qpidHost;
	private String qpidPort;
	private String qpidVhost;
	private String qpidUser;
	private String qpidPassword;
	private String qpidSsl;
	private String saslMechs;
	private String jenadbURL;
	private String jenadbType;
	private String jenadbDriver;
	private String isTomcat;
	private String pathTomcat;
	private String os;
	private String Bridge_http_port;
	private String HttpInterface_port;
	private String traceMask;
	private static Configuration configuration = null;
	private boolean isSecure = false;

	private Configuration() {
		this.load();

	}

	/**
	 * Http port where BridgeAgentOutIn runs.
	 * 
	 * @return
	 */
	public String getBridgeHttpPort() {
		return this.Bridge_http_port;
	}

	/**
	 * Http port where HttpInterface runs.
	 * 
	 * @return
	 */
	public String getHttpInterfacepPort() {
		return this.HttpInterface_port;
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
	 * If TOMCAT is active
	 * 
	 * @return
	 */
	public boolean getIsTomcat() {
		if (this.isTomcat.equals("true"))
			return true;
		else
			return false;
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
	 * Qpid host
	 * 
	 * @return qpidHost
	 */
	public String getqpidHost() {
		return this.qpidHost;
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
	 * Qpid port
	 * 
	 * @return port
	 */
	public int getqpidPort() {

		return Integer.parseInt(this.qpidPort);

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
		if (this.qpidSsl.equals("true"))
			return true;
		else
			return false;

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
		return traceMask;
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

			for (Enumeration<Object> e = properties.keys(); e.hasMoreElements();) {
				// Get the object
				Object obj = e.nextElement();
				if (obj.toString().equalsIgnoreCase("serverName")) {
					this.databaseServer = properties
							.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("databaseName")) {
					databaseName = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("userName")) {
					this.databaseUser = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("password")) {
					this.databasePassword = properties.getProperty(obj
							.toString());
				} else if (obj.toString().equalsIgnoreCase(
						"OMSServiceDescriptionLocation")) {
					OMSServiceDescriptionLocation = properties.getProperty(obj
							.toString());
				} else if (obj.toString().equalsIgnoreCase(
						"SFServiceDescriptionLocation")) {
					SFServiceDescriptionLocation = properties.getProperty(obj
							.toString());
				} else if (obj.toString().equalsIgnoreCase("host")) {

					this.qpidHost = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("port")) {

					this.qpidPort = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("vhost")) {
					this.qpidVhost = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("user")) {
					this.qpidUser = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("pass")) {
					this.qpidPassword = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("ssl")) {
					this.qpidSsl = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("saslMechs")) {
					this.saslMechs = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("dbURL")) {
					this.jenadbURL = properties.getProperty(obj.toString());
				}

				else if (obj.toString().equalsIgnoreCase("dbType")) {
					this.jenadbType = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("dbDriver")) {
					this.jenadbDriver = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("tomcat")) {
					this.isTomcat = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("pathTomcat")) {
					this.pathTomcat = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("os")) {
					this.os = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase(
						"BridgeAgentOutInPort")) {
					this.Bridge_http_port = properties.getProperty(obj
							.toString());
				} else if (obj.toString().equalsIgnoreCase("HttpInterfacePort")) {
					this.HttpInterface_port = properties.getProperty(obj
							.toString());
				} else if (obj.toString().equalsIgnoreCase(TRACE_MASK_KEY)) {
					this.traceMask = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("secureMode")) {
					if (properties.getProperty(obj.toString()).equals("true"))
						this.isSecure = true;
					else
						this.isSecure = false;
				}

			}

		} catch (IOException e) {
			System.out.print(e);
			return;
		}
	}
}