package organizational__message_example.flat;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class Display extends QueueAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	Monitor m = new Monitor();
	ACLMessage mensaje_recibido = new ACLMessage();
	private Queue<ACLMessage> messageList = new LinkedList<ACLMessage>();
	boolean active = true;

	public Display(AgentID aid) throws Exception {
		super(aid);

	}

	public void execute() {

try
{

		String result = omsProxy.acquireRole("participant", "virtual");
		logger.info("["+this.getName()+"] result acquire role: "+ result);
		result = omsProxy.acquireRole("manager", "calculin");
		logger.info("["+this.getName()+"] result acquire role: "+ result);
		
	}catch(THOMASException e)
	{
		e.printStackTrace();
	}

		do{
			m.waiting();  //Waiting messages. The method onMessage is in charge of warning when a new message arrive
			do{
				if (!messageList.isEmpty())
					this.displayMessage(messageList.poll());
			}while(messageList.size() != 0);
		}while(active);



	}

	public void finalize()
	{
		
		try
		{
		String result = omsProxy.leaveRole("manager", "calculin");
		System.out.println("["+this.getName()+"] Result leaven role manager: "+result);
		result = omsProxy.leaveRole("participant", "virtual");
		System.out.println("["+this.getName()+"] Result leave role participant: "+result);
		
		logger.info("["+this.getName()+" ] end execution!");
		
	}catch(THOMASException e)
	{
		e.printStackTrace();
	}
	}
	
	public void conclude()
	{
		active = false;
		m.advise();
		
	}
	
	/**
	 * Display agent messages with position qual to supervisor 
	 * @param _msg
	 */
	public void displayMessage(ACLMessage _msg)
	{
		
		try
		{
		ACLMessage msg = _msg; 
		ArrayList<ArrayList<String>> roles = omsProxy.informMembers("calculin","manager","");
		
		for(ArrayList<String> im : roles)
		{
			
			String agent = im.get(0);
		//If sender agent has rol manager, it shows the message
		if (agent.toLowerCase().equals(msg.getSender().name.toLowerCase()))
			System.out.println("[ "+this.getName()+" ]  "+ msg.getSender().name+" says " + msg.getContent());
		}
		
		}catch(THOMASException e)
		{
			e.printStackTrace();
		}
	}



	public void onMessage(ACLMessage msg)
	{
		
		if (msg.getReceiver().name.equals("calculin")) //Me interesan los mensajes que llegan de la organización
		{
			messageList.add(msg);
			m.advise(); //When a new message arrives, it advise the main thread
		}
		if (msg.getSender().name.equals("OMS") || msg.getSender().name.equals("SF"))
			super.onMessage(msg);

	}



}
