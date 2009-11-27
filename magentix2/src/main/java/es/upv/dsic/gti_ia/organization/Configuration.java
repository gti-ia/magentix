package es.upv.dsic.gti_ia.organization;


import java.util.*;
import java.io.*;
import java.io.InputStream;



public class Configuration {
	
	
	private String databaseServer;
	private String databaseName;
	private String databaseUser;
	private String databasePassword;
	private String OMSServiceDesciptionLocation;
	private String SFServiceDesciptionLocation;
	private String THServiceDesciptionLocation;
	private String qpidHost;
	private String qpidPort;
	private String qpidVhost;
	private String qpidUser;
	private String qpidPassword;
	private String qpidSsl;
	private static Configuration configuration = null;
	


	private Configuration()
	{
		this.load();

	}
	
	/**
	 * 
	 * @return serverName 
	 */
	public String getdatabaseServer()
	{
		return this.databaseServer;
	}
	/**
	 * 
	 * @return databaseName
	 */
	public String getdatabaseName()
	{
		return this.databaseName;
	}
	/**
	 * 
	 * @return userName
	 */
	public String getdatabaseUser()
	{
		return this.databaseUser;
	}
	/**
	 * 
	 * @return password
	 */
	public String getdatabasePassword()
	{
		return this.databasePassword;
	}
	/**
	 * 
	 * @return OMSServiceDescriptionLocation
	 */
	public String getOMSServiceDesciptionLocation()
	{
		return this.OMSServiceDesciptionLocation;
	}
	/**
	 * 
	 * @return SFServiceDesciptionLocation
	 */
	public String getSFServiceDesciptionLocation()
	{
		return this.SFServiceDesciptionLocation;
	}
	/**
	 * 
	 * @return THServiceDesciptionLocation
	 */
	public String getTHServiceDesciptionLocation()
	{
		return this.THServiceDesciptionLocation;
	}
	/**
	 * 
	 * @return connection
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
	
	
	public int getqpidPort()
	{

		return Integer.parseInt(this.qpidPort);
		
	}
	public String getqpidVhost()
	{
		return this.qpidVhost;
	}
	
	public String getqpidUser()
	{
		return this.qpidUser;
	}
	
	public String getqpidPassword()
	{
		return this.qpidPassword;
	}
	public boolean getqpidSSL()
	{
		if (this.qpidSsl.equals("true"))
			return true;
		else
			return false;
		
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
			    }else    if (obj.toString().equalsIgnoreCase("THServiceDesciptionLocation"))
			    {
			    	THServiceDesciptionLocation= properties.getProperty(obj.toString()); 	
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
			    
			    
			}

	    } catch (IOException e) {
	    	System.out.print(e);
	    	return;
	    }
	}
	


}