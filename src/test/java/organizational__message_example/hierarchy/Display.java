package organizational__message_example.hierarchy;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

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

		

		String result = omsProxy.acquireRole("participant", "virtual");
		logger.info("["+this.getName()+"] Result acquire role participant: "+result);
		result = omsProxy.acquireRole("manager", "calculin");
		logger.info("["+this.getName()+"] Result acquire role manager: "+result);
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
		String result = omsProxy.leaveRole("manager", "calculin");
		logger.info("["+this.getName()+"] Result leaven role manager: "+result);
		result = omsProxy.leaveRole("participant", "virtual");
		logger.info("["+this.getName()+"] Result leave role participant: "+result);
		
		logger.info("["+this.getName()+" ] end execution!");
	}

	/**
	 * Display agent messages with position qual to supervisor 
	 * @param _msg
	 */
	public void displayMessage(ACLMessage _msg)
	{
		ACLMessage msg = _msg;
		ArrayList<String> unitRoles = omsProxy.informUnitRoles("calculin");
		
		
		for(String s : unitRoles)
		{  /**
			For every role that exists in the unit calculin we extract the members who have this role
		**/
			StringTokenizer st = new StringTokenizer(s," ");
			st.nextToken();// Eliminamos el <
			String rol = st.nextToken();
		
			ArrayList<String> inforMembers = omsProxy.informMembers("calculin",rol,"");	
	
			
			String position = "";
			
			//If the agent is equal to the sender, then the position is extracted
			for(String im : inforMembers)
			{
				StringTokenizer st1 = new StringTokenizer(im," ");
				st1.nextToken();//Quitamos el <
				String agent = st1.nextToken();
				st1.nextToken();//Quitamos el -
				rol = st1.nextToken();
				if (agent.toLowerCase().equals(msg.getSender().name.toLowerCase()))
				{
					ArrayList<String> informRole = omsProxy.informRole(rol, "calculin"); 
					position = informRole.get(2);
				}
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
