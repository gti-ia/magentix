/**
 * Create a new QueueAgent.
 * 
 * @author  Joan Bellver Faus, GTI-IA, DSIC, UPV
 * @version 2009.9.07
 */

package es.upv.dsic.gti_ia.architecture;

import java.util.ArrayList;
import java.util.Date;


import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Session;

import es.upv.dsic.gti_ia.magentix2.BaseAgent;
import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;




/**
 * Class QueueAgent extends BaseAgent
 * @author jbellver
 *
 */

public class QueueAgent extends BaseAgent {

	

	private ArrayList<ACLMessage> messageList = new ArrayList<ACLMessage>();
	
	private Monitor monitor = null;
	
	
	//para poder diferenciar cuando nos llega una conversació nueva
	private ArrayList<String> activeConversationsList = new ArrayList<String>();
	//almacena la informacion de los servicios en thomas
	//private ArrayList<SFAgentDescription> agentDescriptions = new ArrayList<SFAgentDescription>();
	
	private ArrayList<Object> roles = new ArrayList<Object>();

	/**
	 * Create a new QueueAgent.
	 * 
	 * @param aid
	 *            agent ID.
	 * @param connection
	 *            connection with the broker.
	 */

	public QueueAgent(AgentID aid, Connection connection) throws Exception{
		super(aid, connection);	
	}


	/**
	 * Transforms the message to ACLMessage
	 * @param xfr MessageTransfer
	 * @return ACLMessage
	 */

	public final ACLMessage MessageTransfertoACLMessage(MessageTransfer xfr) {

		// des-serializamos el mensaje
		// inicializaciones
		int indice1 = 0;
		int indice2 = 0;
		int aidindice1 = 0;
		int aidindice2 = 0;
		int tam = 0;
		String aidString;
		String body = xfr.getBodyString();

		//System.out.println("BODY: " + body);

		indice2 = body.indexOf('#', indice1);
		ACLMessage msg = new ACLMessage(Integer.parseInt(body.substring(
				indice1, indice2)));

		// deserializamos los diferentes AgentesID (Sender, Receiver, ReplyTo)
		for (int i = 0; i < 3; i++) {
			AgentID aid = new AgentID();
			aidindice1 = 0;
			aidindice2 = 0;
			indice1 = indice2 + 1 + tam;
			indice2 = body.indexOf('#', indice1);
			tam = Integer.parseInt(body.substring(indice1, indice2));
			aidString = body.substring(indice2 + 1, indice2 + 1 + tam);
			aidindice2 = aidString.indexOf(':');
			if (aidindice2 - aidindice1 <= 0)
				aid.protocol = "";
			else
				aid.protocol = aidString.substring(aidindice1, aidindice2);
			aidindice1 = aidindice2 + 3;
			aidindice2 = aidString.indexOf('@', aidindice1);
			if (aidindice2 - aidindice1 <= 0)
				aid.name = "";
			else
				aid.name = aidString.substring(aidindice1, aidindice2);
			aidindice1 = aidindice2 + 1;
			aidindice2 = aidString.indexOf(':', aidindice1);
			if (aidindice2 - aidindice1 <= 0)
				aid.host = "";
			else
				aid.host = aidString.substring(aidindice1, aidindice2);
			aid.port = aidString.substring(aidindice2 + 1);

			if (i == 0)
				msg.setSender(aid);
			if (i == 1)
				msg.setReceiver(aid);
			if (i == 2)
				msg.setReplyTo(aid);
		}
		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// language
		msg.setLanguage(body.substring(indice2 + 1, indice2 + 1 + tam));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// encoding
		msg.setEncoding(body.substring(indice2 + 1, indice2 + 1 + tam));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// ontologyencodingACLMessage template
		msg.setOntology(body.substring(indice2 + 1, indice2 + 1 + tam));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// Protocol
		msg.setProtocol(body.substring(indice2 + 1, indice2 + 1 + tam));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// Conversation id
		msg.setConversationId(body.substring(indice2 + 1, indice2 + 1 + tam));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// Reply with
		msg.setReplyWith(body.substring(indice2 + 1, indice2 + 1 + tam));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf("#", indice1);

		tam = Integer.parseInt(body.substring(indice1, indice2));
		// In reply to
		msg.setInReplyTo(body.substring(indice2 + 1, indice2 + 1 + tam));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// reply by

		if (tam != 0)
			msg.setReplyByDate(new Date(Integer.parseInt(body.substring(
					indice2 + 10, indice2 + tam))));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// Content
		msg.setContent(body.substring(indice2 + 1, indice2 + 1 + tam));

		return msg;
	}

	

	/**
	 * Function that will be executed when the agent gets a message
	 * @param ssn
	 * @param xfr
	 */
	public final void onMessage(Session ssn, MessageTransfer xfr) {
		
		this.writeQueue(xfr);
		
		// clase encargada de despertar al agente, puede ser del rol responder o
		// del rol iniciator
		
		
		if (monitor != null)
			this.monitor.advise();

	}
	
	
	
	private synchronized void writeQueue(MessageTransfer xfr)
	{
		messageList.add(MessageTransfertoACLMessage(xfr));
	}
	
    /**
     * Method to receive a magentix2 AclMessage 
     * @param template 
     * @return an ACLMessage
     */
	public synchronized ACLMessage receiveACLMessageSimple(MessageTemplate template) throws Exception{
		ACLMessage msgselect = null;
		
		if (template.getProtocol()=="")
			throw new Exception("The protocol field is empty");

		
		for (ACLMessage msg : messageList) {		
			// comparamos los campos protocol y conversaciónID (para asegurarnos
			// que no es una conversacion existente)00
				if (template.getProtocol().equals(msg.getProtocol())) {
				// comprobar que sea una conversacion nueva, que no este en la
				// lista de conversaciones activas
					msgselect = msg;

				}
		
			}
			if (msgselect!=null)
			{
				messageList.remove(msgselect);
			}
		return msgselect;
	}
	
    /**
     * Method to receive a magentix2 AclMessage 
     * @param template 
     * @param tipo 1 = rol responder other = rol initiator
     * @return an ACLMessage
     */
	synchronized ACLMessage receiveACLMessage(MessageTemplate template, int tipo) {
		ACLMessage msgselect = null;
		

			if (tipo == 1)
			{
				for (ACLMessage msg : messageList) {
			// comparamos los campos protocol y conversaciónID (para asegurarnos
			// que no es una conversacion existente)00

				if (template.getProtocol().equals(msg.getProtocol())) {
				// comprobar que sea una conversacion nueva, que no este en la
				// lista de conversaciones activas
					msgselect = msg;
					for (String conv : this.activeConversationsList) {
					// si existe, entonces debera trartalo el rol de iniciador
						if (conv.equals(msg.getConversationId())) {
							msgselect = null;
							break;
						}
					}
				}

				}
			}
			else
			{
				for (ACLMessage msg : messageList) {
				// comparamos los campos protocol, idcoversaciï¿½n y sender
					if (template.getProtocol().equals(msg.getProtocol())) {
					// miramos dentro de las conversaciones que tenemos
						for (String conversacion : template.getList_Conversation())
							if (conversacion.equals(msg.getConversationId())) {
							// miramos si pertenece algun agente
								if (template.existReceiver(msg.getSender())) {
									msgselect = msg;
									break;
								}
							}
					   }
					if (msgselect != null)
						break;
				}
			}
			if (msgselect != null)
			{	
				messageList.remove(msgselect);	
			}
		return msgselect;
	}

    /**
     * Method to receive a magentix2 AclMessage 
     * @param template 
     * @param timeout 
     * @return an ACLMessage
     */
	
	public synchronized ACLMessage receiveACLMessageT(MessageTemplate template,
			long timeout) throws Exception{
		ACLMessage msgselect = null;
		int i = 0;
		
		if (template.getProtocol()=="")
			//TODO traducir a ingles
			throw new Exception("The protocol field is empty");

		
		do {

			for (ACLMessage msg : messageList) {

				// comparamos los campos performative y protocol
				

					if (template.getProtocol().equals(msg.getProtocol())) {

						msgselect = msg;
						messageList.remove(msg);
		
						// de la llista de missatges
						break;

					}

				
			}
			if (msgselect == null)// no hay ningï¿½n mensaje
			{
				if (i == 0)// solo esperaremos una vez
					try {
						this.wait(timeout);
					} catch (InterruptedException e) {
					}
			} else
				i = 2;
			i++;
		} while (i < 2);
		return msgselect;
	}


		
	//}
	
	/**
	 * 
	 * @return String name 
	 */
	public String getAllName()
	{
		return this.getAid().toString();
	}
	
	/**
	 * 
	 * @return int number of roles 
	 */
	private synchronized int addRole(Object b)
	{
		this.roles.add(b);
		
		//this.nRoles++;
		//return this.nRoles;
		return this.roles.size();
	}
	
	/**
	 * 
	 * @return int remove a role
	 */
	private synchronized int removeRole(Object b)
	{
		this.roles.remove(b);
		//this.nRoles--;
		//return this.nRoles;
		return this.roles.size();
	}
	
	/**
	 * 
	 * @return int number of roles
	 */
	private synchronized int getnRole()
	{
		//return this.nRoles;
		return this.roles.size();
	}
	
	/**
	 * Add new monitor 
	 * @return Monitor
	 */
	synchronized Monitor addMonitor(Object b)
	{
		this.addRole(b);
		if (this.monitor==null)
			this.monitor = new Monitor();
		return monitor;
	}

	synchronized void deleteMonitor(Object b)
	{
		this.removeRole(b);
		if(this.roles.size() == 0)
			this.monitor = null;
	}
	

	synchronized void setActiveConversation(String agentID) {
		this.activeConversationsList.add(agentID);

	}

	/**
	 * Remove a agentID of the array of the active conversations. 
	 * 
	 * @param agentID
	 */
	synchronized void deleteActiveConversation(String agentID) {
		for (String conv : this.activeConversationsList) {
			if (conv.equals(agentID)) {
				this.activeConversationsList.remove(agentID);
				break;
			}
		}
	}

	/**
	 * Remove all active conversations.
	 * 
	 * @return boolean value 
	 */
	synchronized boolean deleteAllActiveConversation() {
		this.activeConversationsList.clear();
		if (this.activeConversationsList.size() == 0)
			return true;
		else
			return false;

	}



	/**
	 *  Return the monitor 
	 * @return Monitor 
	 */
	synchronized Monitor getMonitor() {
		return this.monitor;
	}
	
	protected void finalize()
	{
		
	}
	
	protected void terminate()
	{
		//mirar todos los roles activos

		this.finalize();
		
		if (this.getnRole()==0)
			System.out.println("Ternmino, no hay roles");
		else
		{
			
			for (Object obj:this.roles){
				
				String patron;
				
				patron =  obj.getClass().getSuperclass().getName().substring(obj.getClass().getSuperclass().getName().lastIndexOf(".")+1,obj.getClass().getSuperclass().getName().length());
			
				
				if (patron.equals(
						"FIPARequestInitiator"))

				{
					//TODO aqui deberiamos controlar en que estado esta y finalizar
					System.out.println("Estamos en estado: "+ ((FIPARequestInitiator) obj).getState());
					
				} else if (patron.equals(
						"FIPARequestResponder")) {

					System.out.println("Estamos en estado: "+ ((FIPARequestResponder) obj).getState());
				}
				if (patron.equals(
						"FIPAQueryInitiator")) {

					System.out.println("Estamos en estado: "+ ((FIPAQueryInitiator) obj).getState());
				} else if (patron.equals(
						"FIPAQueryResponder")) {

					System.out.println("Estamos en estado: "+ ((FIPAQueryInitiator) obj).getState());
				}
				if (patron.equals(
						"FIPAContractNetInitiator")) {

					System.out.println("Estamos en estado: "+ ((FIPAContractNetInitiator) obj).getState());
				} else if (patron.equals(
						"FIPAContractNetResponder")) {

					System.out.println("Estamos en estado: "+ ((FIPAContractNetInitiator) obj).getState());
				}
				
				

			}
				
			System.out.println("Ternmino, pero hay roles");
		}
		
		super.terminate();
		
		
	}
	
	
	
	
	
	/**
	 * Adds a new task (FIPA protocol) to the agent,was creating a new thread
	 * @param obj object of type FIPA protocol
	 */
	public void setTask(Object obj) {

	
		String patron;
		
		patron =  obj.getClass().getSuperclass().getName().substring(obj.getClass().getSuperclass().getName().lastIndexOf(".")+1,obj.getClass().getSuperclass().getName().length());
		if (patron.equals(
				"FIPARequestInitiator"))

		{

			ThreadInitiator h = new ThreadInitiator(obj, 1);
			h.start();
			
		} else if (patron.equals(
				"FIPARequestResponder")) {

			ThreadResponder h = new ThreadResponder(obj, 1);
			h.start();
		}
		if (patron.equals(
				"FIPAQueryInitiator")) {

			ThreadInitiator h = new ThreadInitiator(obj, 2);
			h.start();
		} else if (patron.equals(
				"FIPAQueryResponder")) {

			ThreadResponder h = new ThreadResponder(obj, 2);
			h.start();
		}
		if (patron.equals(
				"FIPAContractNetInitiator")) {

			
			ThreadInitiator h = new ThreadInitiator(obj, 3);
			h.start();
		} else if (patron.equals(
				"FIPAContractNetResponder")) {

			ThreadResponder h = new ThreadResponder(obj, 3);
			h.start();
		}




	}

	//#APIDOC_EXCLUDE_BEGIN
	public class ThreadInitiator extends Thread {

		Object iniciador;
		int tipo;

		public ThreadInitiator(Object in, int tipo) {
			iniciador = in;
			this.tipo = tipo;
		}

		public void run() {
			
			switch (tipo) {
			case 1: {
				do {
					((FIPARequestInitiator) iniciador).action();
				} while (!((FIPARequestInitiator) iniciador).finished());
				break;
			}
			case 2: {
				do {
					((FIPAQueryInitiator) iniciador).action();
				} while (!((FIPAQueryInitiator) iniciador).finished());
				break;
			}
			case 3: {
				do {
					((FIPAContractNetInitiator) iniciador).action();
				} while (!((FIPAContractNetInitiator) iniciador).finished());
				break;
			}
			}
		}
	}
	
	public class ThreadResponder extends Thread{
		Object responder;
		int tipo;

		public ThreadResponder(Object res, int tipo) {
			responder = res;
			this.tipo = tipo;
		}

		public void run() {
			switch (tipo) {
			case 1: {
				do {
					((FIPARequestResponder) responder).action();
				} while (true);

			}
			case 2: {
				do {
					((FIPAQueryResponder) responder).action();
				} while (true);
			}
			case 3: {
				do {
					((FIPAContractNetResponder) responder).action();
				} while (true);
			}

			}
		}
	}
	//#APIDOC_EXCLUDE_END
	
}
