package es.upv.dsic.gti_ia.jason.conversationsFactory;


import java.util.concurrent.Semaphore;

import es.upv.dsic.gti_ia.core.AgentID;

/**
 * This class represents a conversation of the Jason agent. It has associated a semaphore to control
 * the conversation. 
 * @author Bexy Alfonso Espinosa
 */
public class Conversation {
	public String factoryName = "";
	Semaphore Protocol_Semaphore;
	public String jasonConvID;
	public String internalConvID;

	public AgentID initiator;

	public Conversation( String jasonID,  String internalID, AgentID initiatorAg, String factName){
		Protocol_Semaphore = new Semaphore(0,true);
		jasonConvID = jasonID;
		internalConvID = internalID;
		initiator = initiatorAg;
		factoryName = factName;
	}

	public void aquire_semaphore(){
		try 
		{
			Protocol_Semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void release_semaphore(){
		Protocol_Semaphore.release();
	}
}
