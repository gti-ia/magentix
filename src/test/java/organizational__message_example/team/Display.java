package organizational__message_example.team;


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
	boolean active = true;
	ACLMessage mensaje_recibido = new ACLMessage();
	private Queue<ACLMessage> messageList = new LinkedList<ACLMessage>();
	boolean finished = false;

	public Display(AgentID aid) throws Exception {
		super(aid);

	}

	public void execute() {

		try
		{
			omsProxy.acquireRole("participant", "virtual");


			do{
				m.waiting();  //Waiting messages. The method onMessage is in charge of warning when a new message arrive

				do{
					this.displayMessage(messageList.poll());
				}while(messageList.size() != 0);
			}while(!finished);

			ArrayList<String> result = omsProxy.informUnit("externa");

			System.out.println("---------------------");
			System.out.println("unit external");
			System.out.println("type: "+ result.get(0));
			System.out.println("parent unit: "+ result.get(1));
			System.out.println("---------------------");

			ArrayList<ArrayList<String>> informUnitRoles = omsProxy.informUnitRoles("calculin");

			for(ArrayList<String> unitRole : informUnitRoles)
			{
				System.out.println("---------------------");

				System.out.println("role name: "+unitRole.get(0));
				System.out.println("position: "+ unitRole.get(1));
				System.out.println("visibility: "+ unitRole.get(2));
				System.out.println("accesibility: "+ unitRole.get(3));

				System.out.println("---------------------");
			}


		}catch(THOMASException e)
		{
			e.printStackTrace();
		}

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

			/**
			For every role that exists in the unit calculin we extract the members who have this role
			 **/


			if (msg.getContent().equals("shut down"))
				finished = true;
			ArrayList<ArrayList<String>> informMembers = omsProxy.informMembers("calculin","manager","");
		
			
			for(ArrayList<String> informMember : informMembers)
			{
				//If the agent is equal to the sender, then the position is extracted
				if (informMember.get(0).equals(msg.getSender().name))
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
