package es.upv.dsic.gti_ia.core;

import java.io.Serializable;

public class MessageContent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String stringContent = null;
	private byte[] binaryContent = null;
	
	public void setStringContent(String content){
		this.stringContent = content;
	}
	
	public void setBinaryContent(byte[] content){
		this.binaryContent = content;
	}
	
	public String getStringContent(){
		return this.stringContent;
	}
	
	public byte[] getBinaryContent(){
		return this.binaryContent;
	}

}
