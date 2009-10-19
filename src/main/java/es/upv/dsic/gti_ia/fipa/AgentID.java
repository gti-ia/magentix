package es.upv.dsic.gti_ia.fipa;

import java.io.Serializable;

/**
 * @author  Ricard Lopez Fogues
 */

public final class AgentID implements Serializable{
    
	private static final long serialVersionUID = 1L;
	//	instance variables
    public String name = "";
    public String protocol = "";
    public String host = "";
    public String port = "";
    //constructors
    /**
     * Creates a new blank Agent ID
     */
    public AgentID() { }
    
    /**
     * Creates a new Agent ID
     * @param __name
     * @param __protocol
     * @param __host
     * @param __port
     */
    public AgentID(String __name, String __protocol, String __host, String __port) {
		name = __name;
		protocol = __protocol;
		port = __port;
		host = __host;
    }
    //constructor a partir de una sola cadena del tipo http://nombreagente@localhost:8080
    /**
     * Creates a new Agent ID from a string like qpid://agentname@localhost:8080
     * @param id
     */
    public AgentID(String id){
    	protocol = id.substring(0, id.indexOf(':'));
    	name = id.substring(id.indexOf(':')+3, id.indexOf('@'));
    	host = id.substring(id.indexOf('@')+1, id.indexOf(':', id.indexOf('@')+1));
    	port = id.substring(id.indexOf(':', id.indexOf('@'))+1);
    }
    
    /**
     * @return Agent ID in string format 
     */
    public String toString(){
    	String cadena = protocol + "://" + name + "@" + host + ":" + port;
    	return cadena;
    }
    
    /**
     * Returns long agent's name
     * @return long agent's name
     */
    public String name_all()
    {
    	String cadena = name + "@" + host + ":" + port; //+"/JADE";
    	return cadena;
    }
    
    /**
     * Returns long agent's address
     * @return long agent's address
     */
    public String addresses_all()
    {
    	String cadena = protocol + "://" +host + ":" + port;
    	return cadena;
    }
    
    /**
     * Returns short agent's address
     * @return short agent's address
     */
    public String addresses_single()
    {
    	String cadena = host+ ":" + port;;
    	return cadena;
    }
    
    /**
     * Returns agent's local name
     * @return agemt's local name
     */
	public String getLocalName() {
		int atPos = name.lastIndexOf('@');
		if(atPos == -1)
			return name;
		else
			return name.substring(0, atPos);
	}
    
}