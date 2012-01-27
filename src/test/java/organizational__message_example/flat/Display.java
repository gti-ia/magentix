package organizational__message_example.flat;


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
	ACLMessage mensaje_recibido = new ACLMessage();
	private Queue<ACLMessage> messageList = new LinkedList<ACLMessage>();

	public Display(AgentID aid) throws Exception {
		super(aid);

	}

	public void execute() {



		String result = omsProxy.acquireRole("participant", "virtual");
		System.out.println("["+this.getName()+"] result acquire role: "+ result);
		result = omsProxy.acquireRole("manager", "calculin");
		System.out.println("["+this.getName()+"] result acquire role: "+ result);

		do{
			m.waiting();  //Waiting messages. The method onMessage is in charge of warning when a new message arrive
			do{
				this.displayMessage(messageList.poll());
			}while(messageList.size() != 0);
		}while(true);



	}

	/**
	 * Display agent messages with position qual to supervisor 
	 * @param _msg
	 */
	public void displayMessage(ACLMessage _msg)
	{
		ACLMessage msg = _msg; 
		ArrayList<String> roles = omsProxy.informMembers("calculin","manager","");
		
		for(String im : roles)
		{
			StringTokenizer st1 = new StringTokenizer(im," ");
			st1.nextToken();//Quitamos el <
			String agent = st1.nextToken();
		//If sender agent has rol manager, it shows the message
		if (agent.toLowerCase().equals(msg.getSender().name.toLowerCase()))
			System.out.println("[ "+this.getName()+" ]  "+ msg.getSender().name+" says " + msg.getContent());
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
