/**
 * La clase QueueAgent crea un nuevo agente.
 * 
 * @author  Joan Bellver Faus, GTI-IA, DSIC, UPV
 * @version 2009.9.07
 */

package es.upv.dsic.gti_ia.magentix2;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Iterator;
import java.util.Dictionary;
import java.util.HashMap;

import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Session;

import es.upv.dsic.gti_ia.proto.FIPARequestResponder;
import es.upv.dsic.gti_ia.proto.FIPARequestInitiator;
import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;
import es.upv.dsic.gti_ia.proto.MessageTemplate;

import es.upv.dsic.gti_ia.proto.Monitor;
import es.upv.dsic.gti_ia.proto.FIPAQueryResponder;
import es.upv.dsic.gti_ia.proto.FIPAQueryInitiator;
import es.upv.dsic.gti_ia.proto.FIPAContractNetInitiator;
import es.upv.dsic.gti_ia.proto.FIPAContractNetResponder;

public class QueueAgent extends BaseAgent {

	ArrayList<ACLMessage> messageList = new ArrayList<ACLMessage>();
	// LinkedBlockingQueue<MessageTransfer> internalQueue;
	//private AgentID aid = null;
	private Monitor monitor = null;
	private Monitor monitorAux = null;
	private Monitor readQueue = new Monitor();
	private volatile boolean stopThread = true;
	
	private int nRoles = 0;
	//para poder diferenciar cuando nos llega una conversació nueva
	private ArrayList<String> listaConversacionesActivas = new ArrayList<String>();
	//almacena la informacion de les servicios en thomas
	private ArrayList<SFAgentDescription> DescripcionesAgentes = new ArrayList<SFAgentDescription>();

	/**
	 * Create a QueueAgent.
	 * 
	 * @param aid
	 *            agent ID.
	 * @param connection
	 *            conexion con el broker.
	 */

	public QueueAgent(AgentID aid, Connection connection) {
		super(aid, connection);
		// internalQueue = new LinkedBlockingQueue<MessageTransfer>();


	}

	public int addRole()
	{
		this.nRoles++;
		return this.nRoles;
	}
	
	public int removeRole()
	{
		this.nRoles--;
		return this.nRoles;
	}
	
	public int getnRole()
	{
		return this.nRoles;
	}
	
	
	public Monitor addMonitor()
	{
		this.addRole();
		if (this.monitor==null)
			this.monitor = new Monitor();
		return monitor;
	}
	
	public void deleteMonitor()
	{
		this.removeRole();
		if(this.nRoles == 0)
		this.monitor = null;
	}
	
	/**
	 * Devuelve el array de servicios 
	 * @return
	 */
	public ArrayList<SFAgentDescription> getArraySFAgentDescription()
	{
		return this.DescripcionesAgentes;
	}
	
	/**
	 * Añade una nueva descripcion del servicio al arraylist
	 * @param sfa
	 */
	public void setSFAgentDescription(SFAgentDescription sfa)
	{
		//comprobar que no exista
		if (!this.DescripcionesAgentes.contains(sfa))
			this.DescripcionesAgentes.add(sfa);
		else //si existe quitamos primero uno y ponemos luego el otro
		{
			this.DescripcionesAgentes.remove(sfa);
			this.DescripcionesAgentes.add(sfa);
		}
	}

	/**
	 * Inserta el id de una conversacion que tengamos activa
	 * 
	 * @param conversacion
	 */
	public void setActiveConversation(String conversacion) {
		this.listaConversacionesActivas.add(conversacion);

	}

	/**
	 * Elimina la conversacion de la lista de activas
	 * 
	 * @param conversacion
	 */
	public void deleteActiveConversation(String conversacion) {
		for (String conv : this.listaConversacionesActivas) {
			if (conv.equals(conversacion)) {
				this.listaConversacionesActivas.remove(conversacion);
				break;
			}
		}
	}

	/**
	 * Elimina todas la conversaciones
	 * 
	 * @return
	 */
	public boolean deleteAllActiveConversation() {
		this.listaConversacionesActivas.clear();
		if (this.listaConversacionesActivas.size() == 0)
			return true;
		else
			return false;

	}



	public Monitor getMonitor() {
		return this.monitor;
	}



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

	public final void onMessage(Session ssn, MessageTransfer xfr) {
		// internalQueue.add(xfr);

		
		messageList.add(MessageTransfertoACLMessage(xfr));
		// clase encargada de despertar al agente, puede ser del rol responder o
		// del rol iniciator

		
		if (monitor != null)
			this.monitor.advise();
		if (monitorAux != null)
			this.monitorAux.advise();

	}
	/**
	 * Añade una nueva tarea (protocolo FIPA) al agente, creara un nuevo hilo
	 * @param obj objecto de tipo protocolo FIPA
	 */
	public void setTask(Object obj) {

	
		if (obj.getClass().getSuperclass().getName().equals(
				"es.upv.dsic.gti_ia.proto.FIPARequestInitiator"))

		{

			HiloIni h = new HiloIni(obj, 1);
			h.start();
			
		} else if (obj.getClass().getSuperclass().getName().equals(
				"es.upv.dsic.gti_ia.proto.FIPARequestResponder")) {

			HiloRes h = new HiloRes(obj, 1);
			h.start();
		}
		if (obj.getClass().getSuperclass().getName().equals(
				"es.upv.dsic.gti_ia.proto.FIPAQueryInitiator")) {

			HiloIni h = new HiloIni(obj, 2);
			h.start();
		} else if (obj.getClass().getSuperclass().getName().equals(
				"es.upv.dsic.gti_ia.proto.FIPAQueryResponder")) {

			HiloRes h = new HiloRes(obj, 2);
			h.start();
		}
		if (obj.getClass().getSuperclass().getName().equals(
				"es.upv.dsic.gti_ia.proto.FIPAContractNetInitiator")) {

			HiloIni h = new HiloIni(obj, 3);
			h.start();
		} else if (obj.getClass().getSuperclass().getName().equals(
				"es.upv.dsic.gti_ia.proto.FIPAContractNetResponder")) {

			HiloRes h = new HiloRes(obj, 3);
			h.start();
		}

		// es.upv.dsic.gti_ia.proto.Adviser adv = new Adviser();
		// adv.esperar();

	}

	public int getNMensajes() {
		return messageList.size();
	}

	

	
    
	
	/**
	 * Busca un mensaje dado un template
	 * @param template
	 * @return msg 
	 */
	public synchronized ACLMessage receiveACLMessage(MessageTemplate template, int tipo) {
		ACLMessage msgselect = null;
		boolean condition = false;
		// System.out.println("Numero de mensajes:" + messageList.size());
		
		
		
		
	    
		do{
			
	   ArrayList<ACLMessage> messageListAux = (ArrayList<ACLMessage>)messageList.clone();
		if (tipo == 1)
		{
		for (ACLMessage msg : messageListAux) {
			// comparamos los campos protocol y conversaciónID (para asegurarnos
			// que no es una conversacion existente)00

			if (template.getProtocol().equals(msg.getProtocol())) {
				// comprobar que sea una conversacion nueva, que no este en la
				// lista de conversaciones activas
				msgselect = msg;
				for (String conv : this.listaConversacionesActivas) {
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
			for (ACLMessage msg : messageListAux) {
				
				// comparamos los campos protocol, idcoversaciï¿½n y sender
				if (template.getProtocol().equals(msg.getProtocol())) {

					// miramos dentro de las conversaciones que tenemos
					for (String conversacion : template.getList_Conversaciones())
						if (conversacion.equals(msg.getConversationId())) {

							// miramos si pertenece algun agente

							if (template.existeReceiver(msg.getSender())) {

								msgselect = msg;
								break;

							}

						}
					

				}
				if (msgselect != null)
					break;
			}
		}

		if (msgselect == null)
		{
			
			
			if ((messageList.size() != messageListAux.size()))
			{
				condition = true;
			}
				
		}
		else
		{
			messageList.remove(msgselect);
			condition = false;
		}
		
	}while(condition);

		return msgselect;
	}

	
	public final ACLMessage receiveACLMessageT(MessageTemplate template,
			long timeout) {
		ACLMessage msgselect = null;
		int i = 0;
		// System.out.println("Numero de mensajes:" + messageList.size());
		do {
			for (ACLMessage msg : messageList) {

				// comparamos los campos performative y protocol
				if (template.getPerformative().equals(msg.getPerformative())) {

					if (template.getProtocol().equals(msg.getProtocol())) {

						msgselect = msg;
						messageList.remove(msg);
						// TODO recuperar quan es igual al template i esborrar
						// de la llista de missatges
						break;

					}

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

	/**
	 * Busqueda bloqueante, bloquea el agente hasta que llegue el mensaje
	 * @param template
	 * @return
	 */
	public final ACLMessage receiveACLMessageB(
			MessageTemplate template) {
		
		if (this.monitorAux == null)
			this.monitorAux = new Monitor();
		
		ACLMessage msgselect = null;
		boolean b = true;

		//Evitamos un acceso concurrente cuando nos llega un mensaje
		ArrayList<ACLMessage> messageListAux = (ArrayList<ACLMessage>)messageList.clone();

		do{
			
			
			for (ACLMessage msg : messageListAux) {
				
	
				
			// comparamos los campos protocol y conversaciónID (para asegurarnos
			// que no es una conversacion existente)00

				if (template.getProtocol().equals(msg.getProtocol())) {
				// comprobar que sea una conversacion nueva, que no este en la
				// lista de conversaciones activas
					msgselect = msg;
					for (String conv : this.listaConversacionesActivas) {
						// si existe, entonces debera trartalo el rol de iniciador
						if (conv.equals(msg.getConversationId())) {
							msgselect = null;
							break;
						}
					}	
				}
		
			}
		
			if (msgselect!=null)
			{
				b = false;
				messageList.remove(msgselect);
			}
			else
			{
			this.monitorAux.waiting();	
			}
		}while(b);
		
		
		return msgselect;
	}

	


	public synchronized ACLMessage receiveACLMessageIAUX(MessageTemplate template) {// comparacion
		// del
		// template
		// para
		// el
		// initiator
		ACLMessage msgselect = null;
		
		//Evitamos un acceso concurrente cuando nos llega un mensaje
		//ArrayList<ACLMessage> messageListAux = (ArrayList<ACLMessage>)messageList.clone();

		
		
		for (ACLMessage msg : messageList) {
			
			// comparamos los campos protocol, idcoversaciï¿½n y sender
			if (template.getProtocol().equals(msg.getProtocol())) {

				// miramos dentro de las conversaciones que tenemos
				for (String conversacion : template.getList_Conversaciones())
					if (conversacion.equals(msg.getConversationId())) {

						// miramos si pertenece algun agente

						if (template.existeReceiver(msg.getSender())) {

							msgselect = msg;
							break;

						}

					}
				

			}
			if (msgselect != null)
				break;
		}
			if (msgselect != null)
			{
				messageList.remove(msgselect);
			}
		

		return msgselect;
	}

	public class HiloIni extends Thread {

		Object iniciador;
		int tipo;

		public HiloIni(Object in, int tipo) {

			iniciador = in;
			this.tipo = tipo;

		}

		public void run() {

			switch (tipo) {
			case 1: {


				do {


					((FIPARequestInitiator) iniciador).action();

				} while (!((FIPARequestInitiator) iniciador).finalizado());
	
				
				break;
			}
			case 2: {

				do {

					((FIPAQueryInitiator) iniciador).action();

				} while (!((FIPAQueryInitiator) iniciador).finalizado());
				break;
			}
			case 3: {

				do {

					((FIPAContractNetInitiator) iniciador).action();

				} while (!((FIPAContractNetInitiator) iniciador).finalizado());
				break;
			}
			}

		}
	}

	public class HiloRes extends Thread {

		Object responder;
		int tipo;

		public HiloRes(Object res, int tipo) {

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

}
