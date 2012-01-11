package organizational__message_example.hierarchy;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;


public class Display extends QueueAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	Monitor m = new Monitor();
	ACLMessage receivedMsg = new ACLMessage();
	Queue<ACLMessage> messageList = new LinkedList<ACLMessage>();
	int active = 0;
	
	public Display(AgentID aid) throws Exception {
		super(aid);

	}

	public void execute() {

		

		omsProxy.acquireRole("member", "virtual");
		omsProxy.acquireRole("manager", "calculin");

		while(active < 6)
		{
			m.waiting();  //Waiting messages. The method onMessage is in charge of warning when a new message arrive
			do{
				this.displayMessage(messageList.poll());
				
				active++;
			}while(messageList.size() != 0);
		}



	}
	
	public void finalize()
	{
		omsProxy.leaveRole("manager", "calculin");
		omsProxy.leaveRole("member", "virtual");
		
		logger.info("[ "+this.getName()+" ] end execution!");
	}

	/**
	 * Display agent messages with position qual to supervisor 
	 * @param _msg
	 */
	public void displayMessage(ACLMessage _msg)
	{
		ACLMessage msg = _msg;
		ArrayList<String> unitRoles = omsProxy.informUnitRoles("calculin");
		Iterator<String> unitRoleIterator = unitRoles.iterator();
		
		while(unitRoleIterator.hasNext())
		{  /**
			For every role that exists in the unit calculin we extract the members who have this role
		**/
			String rol = unitRoleIterator.next();
			ArrayList<String> roles = omsProxy.informMembers(rol,"calculin","");			
			Iterator<String> i = roles.iterator();
			String position = "";
			
			//If the agent is equal to the sender, then the position is extracted
			while(i.hasNext())
			{
				String agent = i.next().toString();
				rol = i.next().toString();
			
				if (agent.equals(msg.getSender().name.toLowerCase()))
					position = omsProxy.getAgentPosition(msg.getSender().name, "calculin",rol, "hierarchy");	
			}
			
			//If the position is equal to supervisor, it shows the message
			if (position.equals("supervisor"))
				System.out.println("[ "+this.getName()+" ]  "+ msg.getSender().name+" says " + msg.getContent());
		}
	}


	public void conclude()
	{
		m.advise();
	}
	public void onMessage(ACLMessage msg)
	{
		
		if (msg.getReceiver().name.equals("calculin")) //Messages that come from the organization
		{
			
			messageList.add(msg);	
			m.advise(); //When a new message arrives, it advise the main thread
		}
		if (msg.getSender().name.equals("OMS") || msg.getSender().name.equals("SF"))
			super.onMessage(msg);

	}



}
