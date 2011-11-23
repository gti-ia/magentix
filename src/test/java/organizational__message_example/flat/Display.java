package organizational__message_example.flat;


import java.util.ArrayList;
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
	ACLMessage mensaje_recibido = new ACLMessage();
	private Queue<ACLMessage> messageList = new LinkedList<ACLMessage>();

	public Display(AgentID aid) throws Exception {
		super(aid);

	}

	public void execute() {



		omsProxy.acquireRole("member", "virtual");
		omsProxy.acquireRole("manager", "calculin");

		do{
			m.waiting();  //Waiting messages. The method onMessage is in charge of warning when a new message arrive
			do{
				this.displayMessage(messageList.poll());
			}while(messageList.size() != 0);
		}while(true);



	}


	public void displayMessage(ACLMessage _msg)
	{
		ACLMessage msg = _msg; 
		ArrayList<String> roles = omsProxy.informMembers("manager","calculin");
		
		if (roles.contains(msg.getSender().name.toLowerCase()))//Si el agente que me envía el mensaje tiene rol manager
			System.out.println("[ "+this.getName()+" ]  "+ msg.getSender().name+" says " + msg.getContent());
	}



	public void onMessage(ACLMessage msg)
	{
		
		if (msg.getReceiver().name.equals("calculin")) //Me interesan los mensajes que llegan de la organización
		{
			messageList.add(msg);
			m.advise(); //Aviso de que ya tiene un nuevo mensaje
		}
		if (msg.getSender().name.equals("OMS") || msg.getSender().name.equals("SF"))
			super.onMessage(msg);

	}



}
