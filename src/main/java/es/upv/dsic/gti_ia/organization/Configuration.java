package es.upv.dsic.gti_ia.organization;


import java.util.*;
import java.io.*;
import java.io.InputStream;



public class Configuration {
	
	
	private String serverName;
	private String databaseName;
	private String userName;
	private String password;
	private  String OMSServiceDesciptionLocation;
	private  String SFServiceDesciptionLocation;
	private  String THServiceDesciptionLocation;
	private String connection;
	private static Configuration configuration = null;
	


	private Configuration()
	{
		this.cargar();
	}
	
	public String getServerName()
	{
		return this.serverName;
	}
	public String getdatabaseName()
	{
		return this.databaseName;
	}
	public String getuserName()
	{
		return this.userName;
	}
	public String getpassword()
	{
		return this.password;
	}
	public String getOMSServiceDesciptionLocation()
	{
		return this.OMSServiceDesciptionLocation;
	}
	public String getSFServiceDesciptionLocation()
	{
		return this.SFServiceDesciptionLocation;
	}
	public String getTHServiceDesciptionLocation()
	{
		return this.THServiceDesciptionLocation;
	}
	public String getConnection()
	{
		return this.connection;
	}
	
	public static Configuration getConfiguration(){
		
		if (configuration == null)
			configuration = new Configuration();
		return configuration;
		
	}
	
	
	
	public void cargar()
	{
		//Cargamos los valores desde un archivo .xml 
		Properties properties = new Properties();

	   try {
		   
		   
		   
		   //String fileName = "Settings.xml";
		   
		   //InputStream is = new FileInputStream("configuration/"+fileName);
		   
		   //InputStream is = new FileInputStream(fileName);

			  
			  properties.loadFromXML(Configuration.class.getResourceAsStream("/"+"Settings.xml"));	  
			  

		 
		  
			for (Enumeration<Object> e = properties.keys(); e.hasMoreElements() ; ) {
			    // Obtenemos el objeto
			    Object obj = e.nextElement();
			    if (obj.toString().equalsIgnoreCase("serverName"))
			    {
			    	serverName= properties.getProperty(obj.toString());	
			    }
			    else if (obj.toString().equalsIgnoreCase("databaseName"))
			    {
			    	databaseName= properties.getProperty(obj.toString());
			    }
			    else    if (obj.toString().equalsIgnoreCase("userName"))
			    {
			    	userName= properties.getProperty(obj.toString());
			    }
			    else    if (obj.toString().equalsIgnoreCase("password"))
			    {
			    	password= properties.getProperty(obj.toString());
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
			    }else    if (obj.toString().equalsIgnoreCase("connection"))
			    {
			    	connection= properties.getProperty(obj.toString()); 	
			    }
			    
			    
			}

	    } catch (IOException e) {
	    	System.out.print(e);
	    }
	}
	


}