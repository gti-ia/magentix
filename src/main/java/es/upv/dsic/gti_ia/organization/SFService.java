package es.upv.dsic.gti_ia.organization;



import java.util.HashMap;
import java.util.ArrayList;

import org.apache.log4j.Logger;


import es.upv.dsic.gti_ia.architecture.FIPARequestInitiator;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

public class SFService {

	private String SFServiceDesciptionLocation;
	private QueueAgent agent;
	private SFAgentDescription descripcion;
	private ArrayList<AgentID> agentes = new ArrayList<AgentID>();
	
	private HashMap<String, String> tablaSearchServiceProfile = new HashMap<String, String>();
	private boolean salida = true;
	private String salidaString = null;
	private ArrayList<String> idsSearchService = new ArrayList<String>();
	
	static Logger logger = Logger.getLogger(SFService.class);

	Configuration c = new Configuration();

	/**
	 * 
	 * @param SFServiceDesciptionLocation URLProcess The URL where the owl's document is located.
	 */
	public SFService(String SFServiceDesciptionLocation) {

		this.SFServiceDesciptionLocation = c.SFServiceDesciptionLocation;

	}


	
	/**
	 * 
	 */	
	public SFService() {

		this.SFServiceDesciptionLocation = 	"http://localhost:8080/sfservices/SFservices/owl/owls/";

	}
	
	
	private void addIDSearchService(String id)
	{
		
		this.idsSearchService.add(id);
	}
	
	
	/**
	 * 
	 * @return agent
	 */

	public QueueAgent getAgent() {
		return this.agent;
	}

	/**
	 * Inserts the service profile id returned by the searchService
	 * 
	 * @param id  returned by the SF when the service register
	 * @param profilename 
	 * name of the profile 
	 */

	void setSearchServiceProfile(String profilename, String ranking) {
		this.tablaSearchServiceProfile.put(profilename, ranking);
	}

	/**
	 * Return Service Profile
	 * @param serviceGoal
	 * @return ServiceProfile
	 */
	String getSearchServiceProfile(String serviceGoal) {
		return this.tablaSearchServiceProfile.get(serviceGoal);
	}

	/**
	 * 
	 * @return SFAgentDescription
	 */
	SFAgentDescription getDescription() {
		return this.descripcion;
	}
	
	private void sendInfo(QueueAgent agent, String call)
	{
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("SF"));

		logger.info("[QueryAgent]Sms to send: " + requestMsg.getContent());
		logger.info("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());

	}
	
	/**
	 * Remove provider agent
	 * @param agent
	 * @param sfAgentdescription
	 * @return Status
	 * @throws Exception 
	 */
	
	public boolean removeProvider(QueueAgent agent,
			SFAgentDescription sfAgentdescription)  {
		this.descripcion = sfAgentdescription;
		this.agent = agent;

		String call = SFServiceDesciptionLocation
				+ "RemoveProviderProcess.owl "+
				"RemoveProviderInputServiceImplementationID="
				+ descripcion.getImplementationID();

		this.sendInfo(agent, call);
		
		
		
		return salida;
}
	/**
	 * Return a service list
	 * @param agent
	 * @param sfAgentdescription
	 * @return ArrayList service list
	 * @throws Exception 
	 */
	public ArrayList<String> searchService(QueueAgent agent,
			String serviceGoal) {


		this.agent = agent;
		this.idsSearchService.clear();

	
		this.agentes.clear();
		String call = SFServiceDesciptionLocation
				+ "SearchServiceProcess.owl SearchServiceInputServicePurpose="
				+ serviceGoal;

		this.sendInfo(agent, call);
		return this.idsSearchService;

	}

	/**
	 * Modify Process
	 * @param agent
	 * @param sfAgentdescription
	 * @return Status
	 * @throws Exception 
	 */
	public boolean ModifyProcess(QueueAgent agent,
			SFAgentDescription sfAgentdescription) 

	{
		this.descripcion = sfAgentdescription;
		this.agent = agent;
		

		String call = SFServiceDesciptionLocation + "ModifyProcessProcess.owl"
				+ " ModifyProcessInputServiceGrounding= "
				+ " ModifyProcessInputServiceImplementationID="
				+ descripcion.getImplementationID()
				+ " ModifyProcessInputServiceModel="
				+ descripcion.getURLProcess() + descripcion.getServiceGoal()
				+ ".owl#" + descripcion.getServiceGoal();

		this.sendInfo(agent, call);
		
		return salida;
	}
	/**
	 * Modify Profile
	 * @param agent
	 * @param sfAgentdescription
	 * @return Status
	 * @throws Exception 
	 */
	public boolean ModifyProfile(QueueAgent agent,
			SFAgentDescription sfAgentdescription)  {

		this.descripcion = sfAgentdescription;
		this.agent = agent;

	
		String call = SFServiceDesciptionLocation + "ModifyProfileProcess.owl "
				+ "ModifyProfileInputServiceID=" + descripcion.getID()
				+ " ModifyProfileInputServiceGoal=" + " "
				+ " ModifyProfileInputServiceProfile="
				+ this.descripcion.getURLProfile()
				+ descripcion.getServiceGoal() + ".owl#"
				+ descripcion.getServiceGoal();

		this.sendInfo(agent, call);
		
		return salida;

	}

	/**
	 * Deregister Profile
	 * @param agent
	 * @param sfAgentdescription
	 * @return Status
	 * @throws Exception 
	 */
	public boolean DeregisterProfile(QueueAgent agent,
			SFAgentDescription sfAgentdescription) {

		this.agent = agent;
		this.descripcion = sfAgentdescription;

		

		// eliminar el servicio de la tabla de servicios de el agente

		String call = SFServiceDesciptionLocation
				+ "DeregisterProfileProcess.owl GetProcessInputServiceID="
				+ sfAgentdescription.getURLProfile()
				+ descripcion.getID()
				+ ".owl#"
				+ descripcion.getID();

		this.sendInfo(agent, call);
		return salida;

	}
	
	/**
	 * Return provider list
	 * @param agent
	 * @param id
	 * @return agents provider list 
	 * @throws Exception 
	 */

	public ArrayList<AgentID> getProcess(QueueAgent agent,String serviceID)  {

		this.agent = agent;
		this.agentes.clear();
		
		

		String call = SFServiceDesciptionLocation
				+ "GetProcessProcess.owl GetProcessInputServiceID="
				+ serviceID;
				/*
				+ sfAgentdescription.getURLProfile()
				+ descripcion.getID()
				+ ".owl#"
				+ descripcion.getID();
				 */
		this.sendInfo(agent, call);
		return this.agentes;
		

	}

	/**
	 * Return service profile ( the URL profile)
	 * @param agent
	 * @param sfAgentdescription
	 * @return Status
	 * @throws Exception 
	 */
	public String getProfile(QueueAgent agent, SFAgentDescription sfAgentdescription) 
	{
		this.descripcion = sfAgentdescription;
		this.agent = agent;

		

		String call = SFServiceDesciptionLocation
				+ "GetProfileProcess.owl GetProfileInputServiceID="
				+ sfAgentdescription.getID();
				//+ sfAgentdescription.getURLProfile()
			/*	+ descripcion.getID()
				+ ".owl#"
				+ descripcion.getID();*/

		this.sendInfo(agent, call);
		
		return salidaString;
	}

	// Devuelve el ID para poder modificar luego el servicioç
	
	/**
	 * 
	 * Register profile
	 * @param agent
	 * @param sfAgentDescription
	 * @return Status
	 * @throws Exception 
	 */
	public boolean registerProfile(QueueAgent agent,
			SFAgentDescription sfAgentdescription){

		this.descripcion = sfAgentdescription;
		this.agent = agent;


		String call = SFServiceDesciptionLocation
				+ "RegisterProfileProcess.owl "
				+ "RegisterProfileInputServiceGoal="
				+ descripcion.getServiceGoal()
				+ " RegisterProfileInputServiceProfile="
				+ descripcion.getServiceProfile();

		this.sendInfo(agent, call);
		
		return salida;

	}

	/**
	 * Register Process
	 * @param agent
	 * @param sfAgentdescription
	 * @return Status
	 * @throws Exception 
	 */
	public boolean registerProcess(QueueAgent agent,
			SFAgentDescription sfAgentdescription) {
		// montar string de conexion
		// Enviamos el mensaje

		this.descripcion = sfAgentdescription;
		this.agent = agent;

		

		String call = SFServiceDesciptionLocation
				+ "RegisterProcessProcess.owl"
				+ " RegisterProcessInputServiceID=" 
				+ descripcion.getID()
				+ " RegisterProcessInputServiceModel="
				+ descripcion.getServiceModel();

		this.sendInfo(agent, call);
		
		return salida;
	}

	private void setSalida(boolean valor) {
		this.salida = valor;
	}

	private void setSalidaString(String valor) {
		this.salidaString = valor;
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
			logger.info(myAgent.getName() + ": OOH! "
					+ msg.getSender().getLocalName()
					+ " Has agreed to excute the service!");

		}

		protected void handleRefuse(ACLMessage msg) {
			logger.info(myAgent.getName() + ": Oh no! "
					+ msg.getSender().getLocalName()
					+ " has rejected my proposal.");
			this.sf.setSalida(false);
	
		}

		protected void handleInform(ACLMessage msg) {
			logger.info(myAgent.getName() + ":"
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
					//this.sf.agent.setSFAgentDescription(this.sf.descripcion);
					this.sf.setSalida(true);
				} else
					this.sf.setSalida(false);

		
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


			}

			// si ejecutamos el DeregisterProfile
			if (patron.equals("DeregisterProfileProcess")) {

				if (arg2.equals("1"))// ha ido bien
				{
					// elimino del arrayList
					//this.sf.agent.getArraySFAgentDescription().remove(
						//	this.sf.descripcion);
					this.sf.setSalida(true);
				} else // ha ido mal
				{
					this.sf.setSalida(false);
				}

			}
			// si ejecutamos el GetProcess
			if (patron.equals("GetProcessProcess")) {

				agen = null;
				if (arg2.equals("0")) {

					this.sf.setSalida(false);

				} else {
					agen = arg1.split(",");
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

			
			}
			// si ejecutamos el searchService
			if (patron.equals("SearchServiceProcess")) {

				agen = null;
				
				
				if (arg2.equals("0")) {

					this.sf.setSalida(false);
					this.sf.addIDSearchService(arg1);
				}
				else
				{
				this.agen = arg1.split(",");
				
				for (String a : agen)
				{
					a = a.substring(0, arg1.indexOf(" "));
					this.sf.addIDSearchService(a);
				}
				}
				


			}

			// solo si ejecutamos el registerProfile
			if (patron.equals("RegisterProfileProcess")) {
				if (arg1.equals("1")) {
					// para guardar nuestros ID para poder modificar
					// posteriormente nuestro servicio
					this.sf.descripcion.setID(arg2);
					//this.sf.agent.setSFAgentDescription(this.sf.descripcion);
					this.sf.setSalida(true);
					// this.agent.setIDProfile(this.sf.getDescription()
					// .getServiceGoal(), arg2);
					// coger el segundo argumento i passar-lo a getProcess
					// this.sf.registerProcess(agent, this.sf.descripcion,
					// arg2);
				} else {
					this.sf.setSalida(false);

				}
		
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


			}
			if (patron.equals("ModifyProcessProcess")) {
				if (arg2.equals("1"))
				{
					this.sf.setSalida(true);
				} else if (arg2.equals("0")) {
					this.sf.setSalida(false);
				}
	

			}

		}

		protected void handleNotUnderstood(ACLMessage msg) {
			logger.info(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has indicated that they didn't understand.");
			this.sf.setSalida(false);
		}

		protected void handleOutOfSequence(ACLMessage msg) {
			logger.info(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has send me a message which i wasn't"
					+ " expecting in this conversation");
			this.sf.setSalida(false);
		}
	}

}
