package es.upv.dsic.gti_ia.magentix2;

import java.util.Hashtable;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.*;

import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;
import es.upv.dsic.gti_ia.magentix2.OMSService.TestAgentClient;
import es.upv.dsic.gti_ia.proto.FIPARequestInitiator;
import es.upv.dsic.gti_ia.proto.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.proto.*;

public class SFService {

	private String SFServiceDesciptionLocation;
	private String conection;
	private QueueAgent agent;
	private SFAgentDescription descripcion;
	private ArrayList<AgentID> agentes = new ArrayList<AgentID>();
	//
	private HashMap<String, String> tablaSearchServiceProfile = new HashMap<String, String>();
	private Monitor adv = new Monitor();
	private boolean salida = true;
	private String salidaString = null;

	/**
	 * 
	 * @return agent
	 */

	public QueueAgent getAgent() {
		return this.agent;
	}

	/**
	 * Inserta el service profile ID devuelto por el searchService
	 * 
	 * @param id
	 *            id devuelto por el SF al registrar el servicio
	 * @param profilename
	 *            nombre del pfofile
	 */

	public void setSearchServiceProfile(String profilename, String ranking) {
		this.tablaSearchServiceProfile.put(profilename, ranking);
	}

	/**
	 * 
	 * @return
	 */
	public String getSearchServiceProfile(String serviceGoal) {
		return this.tablaSearchServiceProfile.get(serviceGoal);
	}

	public SFAgentDescription getDescription() {
		return this.descripcion;
	}

	public SFService(String SFServiceDesciptionLocation) {

		this.SFServiceDesciptionLocation = SFServiceDesciptionLocation;

	}

	public void search(QueueAgent agente, SFAgentDescription descripcion) {
		this.searchService(agente, descripcion);

	}

	public boolean removeProvider(QueueAgent agente,
			SFAgentDescription descripcion) {
		this.descripcion = descripcion;
		this.agent = agente;

		String call = SFServiceDesciptionLocation
				+ "RemoveProviderProcess.owl "+
				"RemoveProviderInputServiceImplementationID="
				+ descripcion.getImplementationID();

		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("SF", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");

		// crear un fil en el fipa request initiator

		agent.setTask(new TestAgentClient(agent, requestMsg, this));

		// esperar a que se completen la busqueda de agentes

		adv.waiting();

		return salida;
	}

	public ArrayList<AgentID> searchService(QueueAgent agente,
			SFAgentDescription descripcion) {

		this.descripcion = descripcion;
		this.agent = agente;

		this.agentes.clear();
		String call = SFServiceDesciptionLocation
				+ "SearchServiceProcess.owl SearchServiceInputServicePurpose="
				+ descripcion.getServiceGoal();

		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("SF", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");

		// crear un fil en el fipa request initiator

		agent.setTask(new TestAgentClient(agent, requestMsg, this));

		// esperar a que se completen la busqueda de agentes

		adv.waiting();

		return this.agentes;

	}

	public void setSalida(boolean valor) {
		this.salida = valor;
	}

	public void setSalidaString(String valor) {
		this.salidaString = valor;
	}

	// TODO falta modificar

	public boolean ModifyProcess(QueueAgent agente,
			SFAgentDescription descripcion)

	{
		String call = SFServiceDesciptionLocation + "ModifyProcessProcess.owl"
				+ " ModifyProcessInputServiceGrounding= "
				+ " ModifyProcessInputServiceImplementationID="
				+ descripcion.getImplementationID()
				+ " ModifyProcessInputServiceModel="
				+ descripcion.getURIProcess() + descripcion.getServiceGoal()
				+ ".owl#" + descripcion.getServiceGoal();

		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("SF", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		// registar el agente en la plataforma
		this.adv.waiting();

		return salida;
	}

	public boolean ModifyProfile(QueueAgent agente,
			SFAgentDescription descripcion) {

		this.descripcion = descripcion;
		this.agent = agente;

		String call = SFServiceDesciptionLocation + "ModifyProfileProcess.owl "
				+ "ModifyProfileInputServiceID=" + descripcion.getID()
				+ " ModifyProfileInputServiceGoal=" + " "
				+ " ModifyProfileInputServiceProfile="
				+ this.descripcion.getURIProfile()
				+ descripcion.getServiceGoal() + ".owl#"
				+ descripcion.getServiceGoal();

		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("SF", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");

		// crear un fil en el fipa request initiator
		agent.setTask(new TestAgentClient(agent, requestMsg, this));

		this.adv.waiting();

		return salida;

	}

	public boolean DeregisterProfile(QueueAgent agente,
			SFAgentDescription descripcion) {

		this.agent = agente;
		this.descripcion = descripcion;

		// eliminar el servicio de la tabla de servicios de el agente

		String call = SFServiceDesciptionLocation
				+ "DeregisterProfileProcess.owl GetProcessInputServiceID="
				+ descripcion.getID();

		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("SF", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");

		// crear un fil en el fipa request initiator
		agent.setTask(new TestAgentClient(agent, requestMsg, this));

		this.adv.waiting();

		return salida;

	}

	public void getProcess(QueueAgent agente, SFAgentDescription descripcion) {

		this.descripcion = descripcion;
		this.agent = agente;

		String call = SFServiceDesciptionLocation
				+ "GetProcessProcess.owl GetProcessInputServiceID="
				+ descripcion.getID();

		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("SF", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");

		// crear un fil en el fipa request initiator
		agent.setTask(new TestAgentClient(agent, requestMsg, this));

	}

	// Devuelve un string con la direccion o null si algo ha ido mal
	public String getProfile(QueueAgent agente, SFAgentDescription descripcion)// 
	{
		this.descripcion = descripcion;
		this.agent = agente;

		String call = SFServiceDesciptionLocation
				+ "GetProfileProcess.owl GetProfileInputServiceID="
				+ descripcion.getID();

		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("SF", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");

		// crear un fil en el fipa request initiator
		agent.setTask(new TestAgentClient(agent, requestMsg, this));

		this.adv.waiting();

		return salidaString;
	}

	// Devuelve el ID para poder modificar luego el servicio
	public boolean registerProfile(QueueAgent agente,
			SFAgentDescription descripcion) {

		this.descripcion = descripcion;
		this.agent = agente;

		String call = SFServiceDesciptionLocation
				+ "RegisterProfileProcess.owl "
				+ "RegisterProfileInputServiceGoal="
				+ descripcion.getServiceGoal()
				+ " RegisterProfileInputServiceProfile="
				+ this.descripcion.getURIProfile()
				+ descripcion.getServiceGoal() + ".owl#"
				+ descripcion.getServiceGoal();

		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("SF", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");

		// crear un fil en el fipa request initiator
		agent.setTask(new TestAgentClient(agent, requestMsg, this));

		this.adv.waiting();
		return salida;

	}

	public boolean registerProcess(QueueAgent agente,
			SFAgentDescription descripcion) {
		// montar string de conexion
		// Enviamos el mensaje

		this.descripcion = descripcion;
		this.agent = agente;

		String call = SFServiceDesciptionLocation
				+ "RegisterProcessProcess.owl"
				+ " RegisterProcessInputServiceID=" + descripcion.getID()
				+ " RegisterProcessInputServiceModel="
				+ this.descripcion.getURIProcess()
				+ descripcion.getServiceGoal() + ".owl#"
				+ descripcion.getServiceGoal();

		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("SF", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		// registar el agente en la plataforma
		this.adv.waiting();

		return salida;
	}

	/**
	 * TestAgentClient handles the messages received from the SF
	 */
	static class TestAgentClient extends FIPARequestInitiator {
		QueueAgent agent;
		SFService sf;
		String[] agen;

		protected TestAgentClient(QueueAgent agent, ACLMessage msg, SFService sf) {
			super(agent, msg);
			this.agent = agent;
			this.sf = sf;

		}

		protected void handleAgree(ACLMessage msg) {
			System.out.println(myAgent.getName() + ": OOH! "
					+ msg.getSender().getLocalName()
					+ " Has agreed to excute the service!");

		}

		protected void handleRefuse(ACLMessage msg) {
			System.out.println(myAgent.getName() + ": Oh no! "
					+ msg.getSender().getLocalName()
					+ " has rejected my proposal.");
			this.sf.setSalida(false);
			this.sf.adv.advise();
		}

		protected void handleInform(ACLMessage msg) {
			System.out.println(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has informed me of the status of my request."
					+ " They said : " + msg.getContent());

			// Sacamos el patron
			String patron = msg.getContent().substring(0,
					msg.getContent().indexOf("="));
			String arg1 = "";

			// primer argumento si es un DeregisterProfileProcess no sacaremos
			// el arg1
			if (!patron.equals("DeregisterProfileProcess")
					&& !patron.equals("ModifyProfileProcess")
					&& !patron.equals("ModifyProcessProcess")
					&& !patron.equals("RemoveProviderProcess")) {
				arg1 = msg.getContent().substring(
						msg.getContent().indexOf("=") + 1,
						msg.getContent().length());
				arg1 = arg1.substring(arg1.indexOf("=") + 1, arg1.indexOf(","));
			}

			// segundo argumento
			String arg2 = msg.getContent();
			arg2 = arg2.substring((arg2.lastIndexOf("=")) + 1,
					arg2.length() - 1);


			// si ejecutamos el registerProcess

			if (patron.equals("RegisterProcessProcess")) {
				if (arg2.equals("1")) {
					this.sf.descripcion.setImplementationID(arg1);
					this.sf.agent.setSFAgentDescription(this.sf.descripcion);
					this.sf.setSalida(true);
				} else
					this.sf.setSalida(false);

				this.sf.adv.advise();
			}

			// si ejecutamos el GetProfile
			if (patron.equals("GetProfileProcess")) {
				arg2 = msg.getContent().substring(
						msg.getContent().indexOf(",") + 1,
						msg.getContent().length());
				arg2 = arg2.substring(arg2.indexOf("=") + 1, arg2.indexOf(","));

				if (arg2.equals("1"))// ha ido bien
				{
					this.sf.setSalidaString(arg1);
				} else {
					this.sf.setSalidaString(null);
				}

				this.sf.adv.advise();
				System.out.println("ARG2: " + arg2);

			}

			// si ejecutamos el DeregisterProfile
			if (patron.equals("DeregisterProfileProcess")) {

				if (arg2.equals("1"))// ha ido bien
				{
					// elimino del arrayList
					this.sf.agent.getArraySFAgentDescription().remove(
							this.sf.descripcion);
					this.sf.setSalida(true);
				} else // ha ido mal
				{
					this.sf.setSalida(false);
				}
				this.sf.adv.advise();

			}
			// si ejecutamos el GetProcess
			if (patron.equals("GetProcessProcess")) {

				if (arg2.equals("0")) {

					this.sf.setSalida(false);

				} else {
					agen = arg1.split(",");

					for (String s : agen)

						this.sf.setSalida(true);
					for (String a : agen) {
						arg1 = a.substring(0, arg1.indexOf(" "));
						arg1 = arg1.substring(arg1.indexOf("-") + 1, arg1
								.length());

						// tenemos que controlar si existe 0, 1 o mas
						// proveedores.

						if (!arg1.equals("null"))// si existe algun provideer
						{

							// añadimos tantos agentes proveedores como nos
							// devuelva
							this.sf.agentes.add(new AgentID(arg1));
						}

					}

				}

				this.sf.adv.advise();
			}
			// si ejecutamos el searchService
			if (patron.equals("SearchServiceProcess")) {

				arg1 = arg1.substring(0, arg1.indexOf(" "));
				this.sf.descripcion.setID(arg1);
				this.sf.agent.setSFAgentDescription(this.sf.descripcion);
				// this.sf.getProcess(agent, arg1);

				this.sf.getProcess(agent, this.sf.descripcion);

			}

			// solo si ejecutamos el registerProfile
			if (patron.equals("RegisterProfileProcess")) {
				if (arg1.equals("1")) {
					// para guardar nuestros ID para poder modificar
					// posteriormente nuestro servicio
					this.sf.descripcion.setID(arg2);
					this.sf.agent.setSFAgentDescription(this.sf.descripcion);
					this.sf.setSalida(true);
					// this.agent.setIDProfile(this.sf.getDescription()
					// .getServiceGoal(), arg2);
					// coger el segundo argumento i passar-lo a getProcess
					// this.sf.registerProcess(agent, this.sf.descripcion,
					// arg2);
				} else {
					this.sf.setSalida(false);

				}
				this.sf.adv.advise();
			}
			// this.sf.setID(Integer.parseInt(id));

			// Si ejecutamos el ModifyProfile

			if (patron.equals("ModifyProfileProcess")) {
				if (arg2.equals("1"))// ha hido todo bien
				{
					this.sf.setSalida(true);
				} else if (arg2.equals("0"))// existen profile ligados a este
				// process, por tanto no puede
				// modificar-lo
				{
					this.sf.setSalida(false);
				} else// el id del servicio no es valido
				{
					this.sf.setSalida(false);
				}
				this.sf.adv.advise();

			}
			if (patron.equals("ModifyProcessProcess")) {
				if (arg2.equals("1"))// ha hido todo bien
				{
					this.sf.setSalida(true);
				} else if (arg2.equals("0")) {
					this.sf.setSalida(false);
				}
				this.sf.adv.advise();

			}

		}

		protected void handleNotUnderstood(ACLMessage msg) {
			System.out.println(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has indicated that they didn't understand.");
			this.sf.setSalida(false);
			this.sf.adv.advise();
		}

		protected void handleOutOfSequence(ACLMessage msg) {
			System.out.println(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has send me a message which i wasn't"
					+ " expecting in this conversation");
			this.sf.setSalida(false);
			this.sf.adv.advise();
		}
	}

}
