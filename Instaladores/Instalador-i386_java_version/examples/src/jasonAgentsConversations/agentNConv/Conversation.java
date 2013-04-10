package jasonAgentsConversations.agentNConv;


import java.util.concurrent.Semaphore;

import es.upv.dsic.gti_ia.core.AgentID;

/**
 * @author Bexy
 * This class represents a conversation of the Jason agent. It has associated a semaphore to control
 * the conversation. 
 */
public class Conversation {
	
	//CProcessor processor ;
	Semaphore Protocol_Semaphore;
	public String jasonConvID;
	public String internalConvID;
	
	public AgentID initiator;

	
	
	public Conversation(/*CProcessor proc,*/ String jasonID, String internalID, AgentID initiatorAg){
		Protocol_Semaphore = new Semaphore(0,true);
		jasonConvID = jasonID;
		internalConvID = internalID;
		initiator = initiatorAg;
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
