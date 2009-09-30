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
	private String THServiceDesciptionLocation;
	private QueueAgent agent;
	private SFAgentDescription descripcion;
	private ArrayList<AgentID> agentes = new ArrayList<AgentID>();
	//
	private HashMap<String,String> tablaSearchServiceProfile = new HashMap<String,String>();
	private Adviser adv = new Adviser();
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

	public SFService(String SFServiceDesciptionLocation,
			String THServiceDesciptionLocation) {

		this.SFServiceDesciptionLocation = SFServiceDesciptionLocation;
		this.THServiceDesciptionLocation = THServiceDesciptionLocation;
	}

	public void search(QueueAgent agente, SFAgentDescription descripcion) {
		this.searchService(agente, descripcion);

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

		agent.setTarea(new TestAgentClient(agent, requestMsg, this));

		// esperar a que se completen la busqueda de agentes

		adv.esperar();

		return this.agentes;

	}

	public void setSalida(boolean valor) {
		this.salida = valor;
	}

	public void setSalidaString(String valor)
	{
		this.salidaString = valor;
	}
	public boolean DeregisterProfile(QueueAgent agente, String serviceGoal) {

		this.agent = agente;

		// eliminar el servicio de la tabla de servicios de el agente

		
		
		
		
		if (agent.getTableIDProfile().containsKey(serviceGoal)) // si contiene															// la clave,
																// sino error
		{

			agent.DeleteIDProfile(serviceGoal);

			String call = SFServiceDesciptionLocation
					+ "DeregisterProfileProcess.owl GetProcessInputServiceID="
					+ agent.getIDProfile(serviceGoal);

			ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
			requestMsg.setSender(agent.getAid());
			requestMsg.setContent(call);
			requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
			requestMsg.setReceiver(new AgentID("SF", "qpid", "localhost", ""));

			System.out.println("[QueryAgent]Sms to send: "
					+ requestMsg.toString());
			System.out.println("[QueryAgent]Sending... ");

			// crear un fil en el fipa request initiator
			agent.setTarea(new TestAgentClient(agent, requestMsg, this));

			this.adv.esperar();

		} else {
			System.out.println("El servicio "+serviceGoal+" no existe.");
			salida = false;
		}

		return salida;

	}

	public void getProcess(QueueAgent agente, String id) {

		this.agent = agente;

		String call = SFServiceDesciptionLocation
				+ "GetProcessProcess.owl GetProcessInputServiceID=" + id;

		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("SF", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");

		// crear un fil en el fipa request initiator
		agent.setTarea(new TestAgentClient(agent, requestMsg, this));

	}

	//Devuelve un string con la direccion o null si algo ha ido mal
	public String getProfile(QueueAgent agente, String id)// TODO completar
	{
		String call = SFServiceDesciptionLocation
				+ "GetProfileProcess.owl GetProfileInputServiceID=" + id;

		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("SF", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");

		// crear un fil en el fipa request initiator
		agent.setTarea(new TestAgentClient(agent, requestMsg, this));
		
		
		this.adv.esperar();
		
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
				+ THServiceDesciptionLocation + descripcion.getServiceGoal()
				+ "Profile.owl#" + descripcion.getServiceGoal();

		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("SF", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");

		// crear un fil en el fipa request initiator
		agent.setTarea(new TestAgentClient(agent, requestMsg, this));

		this.adv.esperar();
		return salida;

	}

	public void registerProcess(QueueAgent agente,
			SFAgentDescription descripcion, String Id) {
		// montar string de conexion
		// Enviamos el mensaje

		String call = SFServiceDesciptionLocation
				+ "RegisterProcessProcess.owl"
				+ " RegisterProcessInputServiceID=" + Id
				+ " RegisterProcessInputServiceModel="
				+ THServiceDesciptionLocation + descripcion.getServiceGoal()
				+ "Process.owl#" + descripcion.getServiceGoal();

		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("SF", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTarea(new TestAgentClient(agente, requestMsg, this));

		// registar el agente en la plataforma

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
			if (!patron.equals("DeregisterProfileProcess")) {
				arg1 = msg.getContent().substring(
						msg.getContent().indexOf("=") + 1,
						msg.getContent().length());
				arg1 = arg1.substring(arg1.indexOf("=") + 1, arg1.indexOf(","));
			}

			// segundo argumento
			String arg2 = msg.getContent();
			arg2 = arg2.substring((arg2.lastIndexOf("=")) + 1,
					arg2.length() - 1);

			System.out.println("PATRON: " + patron);
			System.out.println("ARG1: " + arg1);
			System.out.println("ARG2: " + arg2);

			// TODO si ejecutamos el registerProcess

			if (patron.equals("RegisterProfileProcess")) {
				if (arg2.equals("1"))
					this.sf.setSalida(true);
				else
					this.sf.setSalida(false);

				this.sf.adv.dar();
			}

			// TODO si ejecutamos el GetProfile
			if (patron.equals("GetProfileProcess"))
			{
				arg2 = msg.getContent().substring(msg.getContent().indexOf(",")+1,msg.getContent().length());
				arg2 = arg2.substring(arg2.indexOf("=") + 1,arg2.indexOf(","));
				
				if (arg2.equals("1"))//ha ido bien
				{
					this.sf.setSalidaString(arg1);
				}
				else
				{
					this.sf.setSalidaString(null);
				}
				
				this.sf.adv.dar();
				System.out.println("ARG2: "+ arg2);
				
			}
			

			// TODO si ejecutamos el DeregisterProfile
			if (patron.equals("DeregisterProfileProcess")) {

				System.out.println("ARG1 " + arg1 + " ARG2 " + arg2);

				if (arg2.equals("0"))// ha ido bien
				{
					this.sf.setSalida(true);
				} else // ha ido mal
				{
					this.sf.setSalida(false);
				}
				this.sf.adv.dar();

			}
			// TODO si ejecutamos el GetProcess
			if (patron.equals("GetProcessProcess")) {

				if (arg2.equals("0")) {

					this.sf.setSalida(false);

				} else {
					agen = arg1.split(",");

					this.sf.setSalida(true);
					for (String a : agen) {
						arg1 = a.substring(0, arg1.indexOf(" "));
						arg1 = arg1.substring(arg1.indexOf("-") + 1, arg1
								.length());

						// TODO tenemos que controlar si existe 0, 1 o mas
						// proveedores.

						if (!arg1.equals("null"))// si existe algun provideer
						{

							// añadimos tantos agentes proveedores como nos
							// devuelva
							this.sf.agentes.add(new AgentID(arg1));
						}
						
						System.out.println("He añadido: " + arg1 + "\n");
					}

				}

				this.sf.adv.dar();
			}
			// TODO si ejecutamos el searchService
			if (patron.equals("SearchServiceProcess")) {

				arg1 = arg1.substring(0, arg1.indexOf(" "));
				//this.sf.getProcess(agent, arg1);
				
				this.sf.getProfile(agent, arg1);

			}

			// TODO solo si ejecutamos el registerProfile
			if (patron.equals("RegisterProfileProcess")) {
				if (arg1.equals("1")) {
					// para guardar nuestros ID para poder modificar
					// posteriormente nuestro servicio
					this.agent.setIDProfile(this.sf.getDescription()
							.getServiceGoal(), arg2);
					// coger el segundo argumento i passar-lo a getProcess
					this.sf.registerProcess(agent, this.sf.descripcion, arg2);
				} else {
					this.sf.setSalida(false);
					this.sf.adv.dar();

				}
			}
			// this.sf.setID(Integer.parseInt(id));

		}

		protected void handleNotUnderstood(ACLMessage msg) {
			System.out.println(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has indicated that they didn't understand.");
		}

		protected void handleOutOfSequence(ACLMessage msg) {
			System.out.println(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has send me a message which i wasn't"
					+ " expecting in this conversation");
		}
	}

}
