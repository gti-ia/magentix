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

import es.upv.dsic.gti_ia.proto.Adviser;
import es.upv.dsic.gti_ia.proto.FIPAQueryResponder;
import es.upv.dsic.gti_ia.proto.FIPAQueryInitiator;
import es.upv.dsic.gti_ia.proto.FIPAContractNetInitiator;
import es.upv.dsic.gti_ia.proto.FIPAContractNetResponder;

public class QueueAgent extends BaseAgent {

	ArrayList<ACLMessage> messageList = new ArrayList<ACLMessage>();
	// LinkedBlockingQueue<MessageTransfer> internalQueue;
	private AgentID aid = null;
	private Adviser advRes = null;
	private Adviser advIni = null;
	private Adviser advAux = null;
	private ArrayList<String> listaConversacionesActivas = new ArrayList<String>();
	private HashMap<String, String> tablaIDProfile = new HashMap<String, String>();

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
		this.aid = aid;

	}

	/**
	 * Inserta el ID del profile del servicio
	 * 
	 * @param id
	 *            id devuelto por el SF al registrar el servicio
	 * @param profilename
	 *            nombre del pfofile
	 */

	public void setIDProfile(String profilename, String id) {
		this.tablaIDProfile.put(profilename, id);
	}

	/**
	 * 
	 * @return tablaIDProfile
	 */
	public String getIDProfile(String serviceGoal) {
		return this.tablaIDProfile.get(serviceGoal);
	}

	/**
	 * 
	 * @return tablaIDProfile
	 */
	public String DeleteIDProfile(String serviceGoal) {
		return this.tablaIDProfile.remove(serviceGoal);
	}
	
	public HashMap<String,String> getTableIDProfile()
	{
		return this.tablaIDProfile;
	}

	/**
	 * Inserta el id de una conversacion que tengamos activa
	 * 
	 * @param conversacion
	 */
	public void setConversacionActiva(String conversacion) {
		this.listaConversacionesActivas.add(conversacion);

	}

	/**
	 * Elimina la conversacion de la lista de activas
	 * 
	 * @param conversacion
	 */
	public void deleteConversacionActivas(String conversacion) {
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
	public boolean deleteTodasConversacionActivas() {
		this.listaConversacionesActivas.clear();
		if (this.listaConversacionesActivas.size() == 0)
			return true;
		else
			return false;

	}

	/**
	 * Añade un avisador para el rol de responder
	 * 
	 * @param adv
	 */
	public void setAdviserRes(Adviser adv) {
		this.advRes = adv;
	}

	/**
	 * 
	 * @param adv
	 */
	public void setAdviserIni(Adviser adv) {
		this.advIni = adv;
	}

	public Adviser getAdviserRes() {
		return this.advRes;
	}

	public Adviser getAdviserIni() {
		return this.advIni;
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

		if (advRes != null)
			this.advRes.dar();
		if (advIni != null)
			this.advIni.dar();
		if (advAux != null)
			this.advAux.dar();

	}

	public void setTarea(Object obj) {

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

	public final ACLMessage receiveACLMessage(MessageTemplate template) {
		ACLMessage msgselect = null;

		// System.out.println("Numero de mensajes:" + messageList.size());
		ArrayList<ACLMessage> messageListAux = (ArrayList<ACLMessage>)messageList.clone();
		
		
		
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

		if (msgselect != null)
			messageList.remove(msgselect);

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

	public final ACLMessage receiveACLMessageB(
			MessageTemplate template) {
		
		if (this.advAux == null)
			this.advAux = new Adviser();
		
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
			this.advAux.esperar();	
			}
		}while(b);
		
		
		return msgselect;
	}

	public final ACLMessage receiveACLMessageI(MessageTemplate template) {// comparacion
		// del
		// template
		// para
		// el
		// initiator
		ACLMessage msgselect = null;
		
		//Evitamos un acceso concurrente cuando nos llega un mensaje
		ArrayList<ACLMessage> messageListAux = (ArrayList<ACLMessage>)messageList.clone();

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
