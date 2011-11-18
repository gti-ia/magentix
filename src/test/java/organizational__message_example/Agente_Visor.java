package organizational__message_example;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;


public class Agente_Visor extends QueueAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	Monitor m = new Monitor();
	ACLMessage mensaje_recibido = new ACLMessage();
	private Queue<ACLMessage> messageList = new LinkedList<ACLMessage>();

	public Agente_Visor(AgentID aid) throws Exception {
		super(aid);

	}

	public void execute() {



		logger.info("Executing, I'm " + getName());
		/**
		 * This agent has no definite work. Wait infinitely the arrival of new
		 * messages.
		 */


		omsProxy.acquireRole("member", "virtual");


		omsProxy.acquireRole("manager", "calculin");

		do{
			m.waiting();  //Me espero a recibir un mensaje en el onMessage
			do{
				this.comprobarMensaje(messageList.poll());
			}while(messageList.size() != 0);
		}while(true);



	}


	public void comprobarMensaje(ACLMessage _msg)
	{
		ACLMessage msg = _msg; 
		ArrayList<String> roles = omsProxy.informMembers("manager","calculin"); //Miro que agentes tienen rol manager en la unidad
		
		if (roles.contains(msg.getSender().name))//Si el agente que me envía el mensaje tiene rol manager
			System.out.println("Informo que "+ msg.getSender().name+" ha dicho " + msg.getContent());
	}



	public void onMessage(ACLMessage msg)
	{
		//System.out.println("Me llega mensaje de: "+ msg.getSender().name);
		if (msg.getReceiver().name.equals("calculin")) //Me interesan los mensajes que llegan de la organización
		{
			messageList.add(msg);
			m.advise(); //Aviso de que ya tiene un nuevo mensaje
		}
		if (msg.getSender().name.equals("OMS") || msg.getSender().name.equals("SF"))
			super.onMessage(msg);

	}



}
