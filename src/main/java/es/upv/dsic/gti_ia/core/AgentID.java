package es.upv.dsic.gti_ia.core;

import java.io.Serializable;

public final class AgentID implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//	instance variables
    public String name = "";
    public String protocol = "";
    public String host = "";
    public String port = "";
    //constructors
    public AgentID() { }
    public AgentID(String __name, String __protocol, String __host, String __port) {
		name = __name;
		protocol = __protocol;
		port = __port;
		host = __host;
    }
    //constructor a partir de una sola cadena del tipo http://nombreagente@localhost:8080
    public AgentID(String id){
    	protocol = id.substring(0, id.indexOf(':'));
    	name = id.substring(id.indexOf(':')+3, id.indexOf('@'));
    	host = id.substring(id.indexOf('@')+1, id.indexOf(':', id.indexOf('@')+1));
    	port = id.substring(id.indexOf(':', id.indexOf('@'))+1);
    	
  
    }
    
    public String toString(){
    	String cadena = protocol + "://" + name + "@" + host + ":" + port;
    	return cadena;
    }
    public String name_all()
    {
    	String cadena = name + "@" + host + ":" + port; //+"/JADE";
    	return cadena;
    }
    public String addresses_all()
    {
    	String cadena = protocol + "://" +host + ":" + port;
    	return cadena;
    }
    public String addresses_single()
    {
    	String cadena = host+ ":" + port;;
    	return cadena;
    }
	public String getLocalName() {
		int atPos = name.lastIndexOf('@');
		if(atPos == -1)
			return name;
		else
			return name.substring(0, atPos);
	}
	
    
}