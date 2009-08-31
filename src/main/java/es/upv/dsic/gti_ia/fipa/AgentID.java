package es.upv.dsic.gti_ia.fipa;

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
    public String toString(){
    	String cadena = protocol + "://" + name + "@" + host + ":" + port;
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