package jasonAgentsConversations.agentNConv;


import java.util.concurrent.Semaphore;

import es.upv.dsic.gti_ia.cAgents.CProcessor;

/**
 * @author bexy
 * This class represents a conversation of the Jason agent. It has associated a semaphore to control
 * the conversation and its corresponding CProcessor. 
 */
public class Conversation {
	
	//CProcessor processor ;
	Semaphore Protocol_Semaphore;
	public String jasonConvID;
	public String internalConvID;
	
	public Conversation(/*CProcessor proc,*/ String jasonID, String internalID){
		Protocol_Semaphore = new Semaphore(0,true);
		//processor = proc;
		jasonConvID = jasonID;
		internalConvID = internalID;
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
