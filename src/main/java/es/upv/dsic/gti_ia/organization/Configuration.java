package es.upv.dsic.gti_ia.organization;


import java.util.*;
import java.io.*;
import java.io.InputStream;


/**
 * This class reads the contents of settings.xml file found in the configuration directory
 *
 *
 */
public class Configuration {
	
	
	private String databaseServer;
	private String databaseName;
	private String databaseUser;
	private String databasePassword;
	private String OMSServiceDesciptionLocation;
	private String SFServiceDesciptionLocation;
	private String qpidHost;
	private String qpidPort;
	private String qpidVhost;
	private String qpidUser;
	private String qpidPassword;
	private String qpidSsl;
	private String saslMechs;
	private String saslProtocol;
	private String saslServerName;
		
	private String jenadbURL;
	private String jenadbType;
	private String jenadbDriver;
	private static Configuration configuration = null;
	


	private Configuration()
	{
		this.load();

	}
	
	/**
	 * Name of the database server
	 * @return serverName ej. localhost 
	 */
	public String getdatabaseServer()
	{
		return this.databaseServer;
	}
	/**
	 * Name of the database 
	 * @return databaseName
	 */
	public String getdatabaseName()
	{
		return this.databaseName;
	}
	/**
	 * User name of the database 
	 * @return userName
	 */
	public String getdatabaseUser()
	{
		return this.databaseUser;
	}
	/**
	 * User password of the database
	 * @return password
	 */
	public String getdatabasePassword()
	{
		return this.databasePassword;
	}
	/**
	 * Full path where are the owl's deployed the services of the OMS 
	 * @return OMSServiceDescriptionLocation
	 */
	public String getOMSServiceDesciptionLocation()
	{
		return this.OMSServiceDesciptionLocation;
	}
	/**
	 * Full path where are the owl's deployed the services of the SF
	 * @return SFServiceDesciptionLocation
	 */
	public String getSFServiceDesciptionLocation()
	{
		return this.SFServiceDesciptionLocation;
	}

	/**
	 * Qpid host
	 * @return qpidHost
	 */
	public String getqpidHost()
	{
		return this.qpidHost;
	}
	/**
	 * This method returns the  instance configuration using singleton
	 * @return configuration
	 */
	public static Configuration getConfiguration(){
		
		if (configuration == null)
			configuration = new Configuration();
		return configuration;
		
	}
	
	/**
	 * Qpid port
	 * @return port
	 */
	public int getqpidPort()
	{

		return Integer.parseInt(this.qpidPort);
		
	}
	/**
	 * Virtual qpid host
	 * @return Virtual host
	 */
	public String getqpidVhost()
	{
		return this.qpidVhost;
	}
	
	/**
	 * Qpid user
	 * @return user
	 */
	public String getqpidUser()
	{
		return this.qpidUser;
	}
	
	/**
	 * Qpid user password
	 * @return password
	 */
	public String getqpidPassword()
	{
		return this.qpidPassword;
	}
	
	/**
	 * Qpid ssl
	 * @return SSl
	 */
	public boolean getqpidSSL()
	{
		if (this.qpidSsl.equals("true"))
			return true;
		else
			return false;
		
	}
	
	/**
	 * Qpid sasl Mechs
	 */
	public String getqpidsaslMechs()
	{
		return this.saslMechs;
	}
	
	/**
	 * Qpid sasl protocol
	 */
	public String getqpidsaslProtocol()
	{
		return this.saslProtocol;
	}
	
	/**
	 * Qpid server name
	 */
	public String getqpidServerName()
	{

		return this.saslServerName;
	}
	
	/**
	 * Jena database URL
	 * @return URL
	 */
	public String getjenadbURL()
	{
	    return this.jenadbURL;
	}
	

	
	/**
	 * Type of jena database
	 * @return type
	 */
	public String getjenadbType()
	{
	    return this.jenadbType;
	}

	/**
	 * Diver of jena database
	 * @return driver
	 */
	public String getjenadbDriver()
	{
	    return this.jenadbDriver;
	}
	
	private void load()
	{
		//Cargamos los valores desde un archivo .xml 
		Properties properties = new Properties();

	   try {
		   
		   
		   
		   String fileName = "Settings.xml";
		   
		   InputStream is = new FileInputStream("configuration/"+fileName);
		   


			  properties.loadFromXML(is);
			 // properties.loadFromXML(Configuration.class.getResourceAsStream("/"+"Settings.xml"));	  
			  

		 
		  
			for (Enumeration<Object> e = properties.keys(); e.hasMoreElements() ; ) {
			    // Obtenemos el objeto
			    Object obj = e.nextElement();
			    if (obj.toString().equalsIgnoreCase("serverName"))
			    {
			    	this.databaseServer= properties.getProperty(obj.toString());	
			    }
			    else if (obj.toString().equalsIgnoreCase("databaseName"))
			    {
			    	databaseName= properties.getProperty(obj.toString());
			    }
			    else    if (obj.toString().equalsIgnoreCase("userName"))
			    {
			    	this.databaseUser= properties.getProperty(obj.toString());
			    }
			    else    if (obj.toString().equalsIgnoreCase("password"))
			    {
			    	this.databasePassword= properties.getProperty(obj.toString());
			    }
			    else    if (obj.toString().equalsIgnoreCase("OMSServiceDesciptionLocation"))
			    {
			    	OMSServiceDesciptionLocation= properties.getProperty(obj.toString());
			    }
			    else    if (obj.toString().equalsIgnoreCase("SFServiceDesciptionLocation"))
			    {
			    	SFServiceDesciptionLocation= properties.getProperty(obj.toString()); 	
			    }else    if (obj.toString().equalsIgnoreCase("host"))
			    {
	
			    	this.qpidHost= properties.getProperty(obj.toString()); 	
			    }
			    else    if (obj.toString().equalsIgnoreCase("port"))
			    {
			    	
			    	this.qpidPort= properties.getProperty(obj.toString()); 	
			    }
			    else    if (obj.toString().equalsIgnoreCase("vhost"))
			    {
			    	this.qpidVhost= properties.getProperty(obj.toString()); 	
			    }
			    else    if (obj.toString().equalsIgnoreCase("user"))
			    {
			    	this.qpidUser = properties.getProperty(obj.toString()); 	
			    }
			    else    if (obj.toString().equalsIgnoreCase("pass"))
			    {
			    	this.qpidPassword= properties.getProperty(obj.toString()); 	
			    }
			    else    if (obj.toString().equalsIgnoreCase("ssl"))
			    {
			    	this.qpidSsl = properties.getProperty(obj.toString()); 	
			    }
			    else    if (obj.toString().equalsIgnoreCase("saslMechs"))
			    {
			    	this.saslMechs = properties.getProperty(obj.toString()); 	
			    }
			    else    if (obj.toString().equalsIgnoreCase("saslProtocol"))
			    {
			    	this.saslProtocol = properties.getProperty(obj.toString()); 	
			    }
			    else    if (obj.toString().equalsIgnoreCase("saslServerName"))
			    {
			    	this.saslServerName = properties.getProperty(obj.toString()); 	
			    }
			    else    if (obj.toString().equalsIgnoreCase("dbURL"))
			    {
			    	this.jenadbURL = properties.getProperty(obj.toString()); 	
			    }
	
			    else    if (obj.toString().equalsIgnoreCase("dbType"))
			    {
			    	this.jenadbType = properties.getProperty(obj.toString()); 	
			    }
			    else    if (obj.toString().equalsIgnoreCase("dbDriver"))
			    {
			    	this.jenadbDriver = properties.getProperty(obj.toString()); 	
			    }
	
			}

	    } catch (IOException e) {
	    	System.out.print(e);
	    	return;
	    }
	}
	


}