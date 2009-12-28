/**
 * This package contains the definition of the classes for the interaction with the thomas organization
 */
package es.upv.dsic.gti_ia.organization;



import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import es.upv.dsic.gti_ia.architecture.FIPARequestInitiator;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 *This class provides access to methods that implements the SF agent
 * 
 * @author Joan Bellver Faus GTI-IA.DSIC.UPV
 */
public class SFProxy {

    
    private String SFServiceDesciptionLocation;
    private ProcessDescription processDescripcion;
    private ProfileDescription profileDescription;
    
    private Hashtable<AgentID, String> agentes = new Hashtable<AgentID, String>();
    private HashMap<String, String> tablaSearchServiceProfile = new HashMap<String, String>();
    
    private boolean salida = true;
    private String salidaString ="";
    
    private ArrayList<String> idsSearchService = new ArrayList<String>();
    private String[] agen;
    private Oracle oracle;
    static Logger logger = Logger.getLogger(SFProxy.class);
    private Hashtable<String, String> list = new Hashtable<String, String>();
    private boolean isgenericSerice = false;

    Configuration c;

    /**
     * This class gives us the support to accede to the services of the SF
     * 
     * @param SFServiceDesciptionLocation
     *            URLProcess The URL where the owl's document is located.
     */
    public SFProxy(String SFServiceDesciptionLocation) {

	this.SFServiceDesciptionLocation = SFServiceDesciptionLocation;

    }

    /**
     * This class gives us the support to accede to the services of the SF, checked that the data contained in the file settings.xml is the URL 
     * SFServiceDescriptionLocation is not empty and is the correct path.
     * 
     */
    public SFProxy() {
	
	 c = Configuration.getConfiguration();
	this.SFServiceDesciptionLocation = c.getSFServiceDesciptionLocation();

    }

    private void addIDSearchService(String id) {

	this.idsSearchService.add(id);
    }

    /**
     * Inserts the service profile id returned by the searchService
     * 
     * @param id
     *            returned by the SF when the service register
     * @param profilename
     *            name of the profile
     */

    void setSearchServiceProfile(String profilename, String ranking) {
	this.tablaSearchServiceProfile.put(profilename, ranking);
    }

    /**
     * Return Service Profile
     * 
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
    ProfileDescription getProfileDescription() {
	return this.profileDescription;
    }
    
    ProcessDescription getProcessDescription(){
	
	return this.processDescripcion;
    }

    /**
     * When the service is not SF or OMS service
     * 
     * @param agent  QueueAgent
     * @param agentProvider The agent who offers the service
     * @param URLProfile The address this where the profile of the service 
     * @param URLProcess The address this where the process of the service
     * @param ArrayArguments Input arguments  of the service
     * @return outputs
     * @throws Exception 
     */
    public Hashtable<String, String> genericService(QueueAgent agent, AgentID agentProvider,
	    String URLProfile, String URLProcess, ArrayList<String> ArrayArguments)
	    throws Exception {

	this.isgenericSerice = true;

	URL profile;
	try {
	    profile = new URL(URLProfile);
	} catch (MalformedURLException e) {
	    logger.error("ERROR: Profile URL Malformed!");
	    e.printStackTrace();
	    return list;
	}
	oracle = new Oracle(profile);

	// Get inputs
	ArrayList<String> inputs = oracle.getInputs();


	// Build call arguments
	String arguments = "";
	int i = 0;
	for (String s : inputs) {
	
	    if (i < ArrayArguments.size())
		arguments = arguments + " " + s + "=" + ArrayArguments.get(i);
	    i++;
	}

	// build the message to service provider
	String call = URLProcess + arguments;

	ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
	requestMsg.setSender(agent.getAid());
	requestMsg.setContent(call);
	requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
	requestMsg.setReceiver(agentProvider);

	logger.info("[QueryAgent]Sms to send: " + requestMsg.getContent());
	logger.info("[QueryAgent]Sending... ");

	TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

	do {
	    test.action();
	} while (!test.finished());
	
	this.isgenericSerice = false;

	if (!salida) {
	    throw new Exception("Error in generic funcion.");
	} else
	    return list;

    }

    private void sendInfo(QueueAgent agent, String call) {
	this.setSalida(true);
	
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
     * This service deletes the service process registrated in the sf database. 
     * 
     * @param agent is a QueueAgent, this agent implemented the  communication protocol
     * @param ProcessDescription Must have at least completed the field Implementation ID
     * @return status RemoveProviderResponse contains an element: return which indicates if an error occurs (1:OK otherwise 0)
     * @throws Exception if Implementation ID is empty
     */

    public String removeProvider(QueueAgent agent, ProcessDescription ProcessDescription)
	    throws Exception {
	this.processDescripcion = ProcessDescription;

	if (ProcessDescription.getImplementationID().equals(""))
	{
	    throw new Exception("ImplementationID is empty");
	    
	}
	String call = SFServiceDesciptionLocation + "RemoveProviderProcess.owl "
		+ "RemoveProviderInputServiceImplementationID=" + this.processDescripcion.getImplementationID();

	this.sendInfo(agent, call);

	if (!salida)
	    throw new Exception("Remove Provider: " + this.salidaString);
	else
	    return this.salidaString;
    }

    /**
     * His service searchs in the sf database the services which has the goal required by the client.
     * Currently this service makes a query to the database seaching the services whose service description 
     * field mathc with the client requirements (the input service purpose).
     * 
     * @param agent is a QueueAgent, this agent implemented the  communication protocol
     * @param serviceGoal service purpose (is a string: the service description).
     * @return services list (is a list of service profile id, ranking: service profile id, ranking: ...)
     * @exception Exception 
     */
    public ArrayList<String> searchService(QueueAgent agent, String serviceGoal) throws Exception{

	this.idsSearchService.clear();
	this.agentes.clear();

	String call = SFServiceDesciptionLocation
		+ "SearchServiceProcess.owl SearchServiceInputServicePurpose=" + serviceGoal;

	this.sendInfo(agent, call);
	if (!salida)
	    throw new Exception("search Service: " + this.salidaString);
	else
	    return this.idsSearchService;
	 

    }

    /**
     * This service deletes the service process kept in the sf database and inserts the new process
     * 
     * @param agent is a QueueAgent, this agent implemented the  communication protocol
     * @param ProcessDescription  contains two elements: service implementation ID (is a string: 
     * serviceprofile@servicenumdidagent), service model (is a string: urlprocess#processname).
     * @return ModifyProcessResponse contains return which indicates if an error occurs (1:OK,
     * otherwise 0).
     *
     * @throws Exception if ImplementationID or ServiceGoal is empty
     */
    public String modifyProcess(QueueAgent agent, ProcessDescription ProcessDescription)
	    throws Exception

    {
	this.processDescripcion = ProcessDescription;

	if (ProcessDescription.getImplementationID().equals("") || ProcessDescription.getServiceModel().equals(""))
	{
	    throw new Exception("ImplementationID or Service Goal is  empty");
	    
	}
	
	String call = SFServiceDesciptionLocation + "ModifyProcessProcess.owl"
		+ " ModifyProcessInputServiceGrounding= "
		+ " ModifyProcessInputServiceImplementationID=" + this.processDescripcion.getImplementationID()
		+ " ModifyProcessInputServiceModel=" + this.processDescripcion.getServiceModel();

	this.sendInfo(agent, call);
	if (!salida)
	    throw new Exception("Modify Process: " + this.salidaString);
	else
	    return this.salidaString;
    }

    /**
     * This service deletes the service profile kept in the sf database and inserts the new profile.
     * 
     * @param agent is a QueueAgent, this agent implemented the  communication protocol
     * @param ProfileDescription contains three elements: service id (is a string: service profile id), service
         * goal (currently is not in use),and service profile ( is a string 
         * urlprofile#profilename)
     * @return Status return which indicates if a problem occurs (1: ok, 0: there
         * are provider which implement the profile, -1: the service id is not valid).
     * @throws Exception if ServiceID or Service Goal is empty
     */
    public String modifyProfile(QueueAgent agent, ProfileDescription ProfileDescription)
	    throws Exception {

	this.profileDescription = ProfileDescription;

	if (ProfileDescription.getServiceID().equals("") || ProfileDescription.getServiceProfile().equals(""))
	{
	    throw new Exception("ID or Service Goal is  empty");
	    
	}
	
	String call = SFServiceDesciptionLocation + "ModifyProfileProcess.owl "
		+ "ModifyProfileInputServiceID=" + this.profileDescription.getServiceID()
		+ " ModifyProfileInputServiceGoal=" + " " + " ModifyProfileInputServiceProfile="
		+ this.profileDescription.getServiceProfile();

	this.sendInfo(agent, call);

	if (!salida)
	    throw new Exception("Modify Profile: " + this.salidaString);
	else
	    return this.salidaString;

    }

    /**
     * This service deletes the profile in the sf database.
     * 
     * @param agent is a QueueAgent, this agent implemented the  communication protocol
     * @param ProfileDescription contains one element: service id (is a string: service profile id)
     * @return Status DeregisterProfileResponse contains an element: return indicates if an error occurs (
         * 0: ok, 1:error).
     * @throws Exception if ServiceID is empty
     */
    public String deregisterProfile(QueueAgent agent, ProfileDescription ProfileDescription)
	    throws Exception {

	this.profileDescription = ProfileDescription;

	if (ProfileDescription.getServiceID().equals(""))
	{
	    throw new Exception("ID is  empty");
	    
	}
	
	// eliminar el servicio de la tabla de servicios de el agente

	String call = SFServiceDesciptionLocation
		+ "DeregisterProfileProcess.owl GetProcessInputServiceID="
		+ profileDescription.getServiceID();

	this.sendInfo(agent, call);

	if (!salida)
	    throw new Exception("Deregister Profile: " + this.salidaString);
	else
	    return this.salidaString;

    }

    /**
     * This service returns the providers which implements the required profile.
     * 
     * @param agent is a QueueAgent, this agent implemented the  communication protocol
     * @param id the service ID (is a string: service profile id) and the 
	 * agent id (is a string).
     * @return provider list (is a string with the next template:
	 * [service implementation id urlprocess, service implementation id urlproces, ... ] 
	 * @throws Exception
     */

    public Hashtable<AgentID, String> getProcess(QueueAgent agent, String serviceID) throws Exception{

	this.agentes.clear();

	String call = SFServiceDesciptionLocation
		+ "GetProcessProcess.owl GetProcessInputServiceID=" + serviceID;
	/*
	 * + sfAgentdescription.getURLProfile() + descripcion.getID() + ".owl#"
	 * + descripcion.getID();
	 */
	this.sendInfo(agent, call);
	if (!salida)
	    throw new Exception("get Process: " + this.salidaString);
	else
	return this.agentes;

    }

    /**
     * This service returns the url of the required profile.
     * 
     * @param agent is a QueueAgent, this agent implemented the  communication protocol
     * @param serviceID the service ID (is a string: service profile id)
     * @return Status contains three elements: service profile (is a string: the url profile), 
	 * the goal of the profile (currently is not in use) and the return (is an integer) which indicates if
	 * an error occurs. 
	 * @throws Exception
     */
    public String getProfile(QueueAgent agent, String serviceID) throws Exception{

	String call = SFServiceDesciptionLocation
		+ "GetProfileProcess.owl GetProfileInputServiceID=" + serviceID;

	this.sendInfo(agent, call);
	
	if (!salida)
	    throw new Exception("get Profile: " + this.salidaString);
	else
	return salidaString;
    }

    // Devuelve el ID para poder modificar luego el servicio�

    /**
     * 
     *  This service registers the profile of a service in the sf's database.
     * 
     * @param agent is a QueueAgent, this agent implemented the  communication protocol
     * @param SFProfileDescription This parameter contains one element:
	 * service profile ( is a string: urlprofile#profilename )
     * @return Status indicates if an error occurs.
     * @throws Exception
     */
    public String registerProfile(QueueAgent agent, ProfileDescription ProfileDescription)
	    throws Exception {

	this.profileDescription = ProfileDescription;

	if (ProfileDescription.getServiceProfile().equals(""))
	{
	    throw new Exception("Service Profile or Service Goal is  empty");
	    
	}
	
	//
	String call = SFServiceDesciptionLocation + "RegisterProfileProcess.owl "
		+ "RegisterProfileInputServiceGoal= " 
		+ " RegisterProfileInputServiceProfile=" + this.profileDescription.getServiceProfile();

	this.sendInfo(agent, call);

	if (!salida)
	    throw new Exception("Register Profile: " + this.salidaString);
	else
	    return this.salidaString;

    }

    /**
     * This service registers the process of a service in the sf's database.
     * 
     * @param agent is a QueueAgent, this agent implemented the  communication protocol
     *@param ProfileDescription. This parameter contains two elements: service id (is a string), and service
	 * model (is a string: urlprocess#urlprocessname).
	 * @return status  indicates if an error occurs (1:ok , 0: bad news).
     * @throws Exception
     */
    public String registerProcess(QueueAgent agent, ProcessDescription ProcessDescription)
	    throws Exception {
	// montar string de conexion
	// Enviamos el mensaje

	this.processDescripcion= ProcessDescription;

	if (this.processDescripcion.getProfileID().equals("") || this.processDescripcion.getServiceModel().equals(""))
	{
	    throw new Exception("ID or Service Model is  empty");
	    
	}
	
	String call = SFServiceDesciptionLocation + "RegisterProcessProcess.owl"
		+ " RegisterProcessInputServiceID=" + this.processDescripcion.getProfileID()
		+ " RegisterProcessInputServiceModel=" + this.processDescripcion.getServiceModel();

	this.sendInfo(agent, call);

	if (!salida)
	    throw new Exception("Register Process: " + this.salidaString);
	else
	    return this.salidaString;
    }

    private void setSalida(boolean valor) {
	this.salida = valor;
    }

    private void setSalidaString(String valor) {
	this.salidaString = valor;
    }
    


    void extractInfo(ACLMessage msg) {

	// Sacamos el patron
	String patron = msg.getContent().substring(0, msg.getContent().indexOf("="));
	String arg1 = "Check the error log file";

	
	// primer argumento si es un DeregisterProfileProcess no sacaremos
	// el arg1
	if (!patron.equals("DeregisterProfileProcess") && !patron.equals("ModifyProfileProcess")
		&& !patron.equals("ModifyProcessProcess")
		&& !patron.equals("RemoveProviderProcess")) {
	    arg1 = msg.getContent().substring(msg.getContent().indexOf("=") + 1,
		    msg.getContent().length());
	    arg1 = arg1.substring(arg1.indexOf("=") + 1, arg1.indexOf(","));
	}

	
	// segundo argumento
	String arg2 = msg.getContent();
	arg2 = arg2.substring((arg2.lastIndexOf("=")) + 1, arg2.length() - 1);

	// si ejecutamos el registerProcess

	if (patron.equals("RegisterProcessProcess")) {
	    if (arg2.equals("1")) {
		this.processDescripcion.setImplementationID(arg1);
		// this.sf.agent.setSFAgentDescription(this.sf.descripcion);
		this.setSalidaString(arg1);
	    } else {
		this.setSalidaString(arg1);
	    }

	}
	

	// si ejecutamos el GetProfile
	if (patron.equals("GetProfileProcess")) {
	    arg2 = msg.getContent().substring(msg.getContent().indexOf(",") + 1,
		    msg.getContent().length());
	    arg2 = arg2.substring(arg2.indexOf("=") + 1, arg2.indexOf(","));

	    if (arg2.equals("1"))// ha ido bien
	    {
		this.setSalidaString(arg1);
	    } else {

		this.setSalidaString(arg1);
	    }

	}
	

	// si ejecutamos el DeregisterProfile
	if (patron.equals("DeregisterProfileProcess")) {

	    if (arg2.equals("1"))// ha ido bien
	    {
		// elimino del arrayList
		// this.sf.agent.getArraySFAgentDescription().remove(
		// this.sf.descripcion);
		this.setSalidaString(arg2);
	    } else // ha ido mal
	    {
		this.salidaString = arg2;
	    }

	}
	
	
	
	// si ejecutamos el GetProcess
	if (patron.equals("GetProcessProcess")) {

	    agen = null;
	    if (arg2.equals("0")) {

		this.setSalidaString(arg1);

	    } else {
		agen = arg1.split(",");
		for (String a : agen) {
		    // sacamos el url process
		    String arg_aux = a.substring(arg1.indexOf(" ") + 1, arg1.length());

		    arg1 = a.substring(0, arg1.indexOf(" "));
		    arg1 = arg1.substring(arg1.indexOf("-") + 1, arg1.length());

		    // tenemos que controlar si existe 0, 1 o mas
		    // proveedores.

		    if (!arg1.equals("null"))// si existe algun provideer
		    {

			// a�adimos tantos agentes proveedores como nos
			// devuelva
			// this.agentes.add(new AgentID(arg1));
			this.agentes.put(new AgentID(arg1), arg_aux);
		    }

		}

	    }

	}
	
	
	
	// si ejecutamos el searchService
	if (patron.equals("SearchServiceProcess")) {

	    agen = null;

	    if (arg2.equals("1")) {

		// this.sf.addIDSearchService(arg2);
		// } else {
		this.agen = arg1.split(",");

		for (String a : agen) {
		    a = a.substring(0, arg1.indexOf(" "));
		    this.addIDSearchService(a);
		}
	    }
	    else
		this.setSalidaString(arg1);

	}

	
	// solo si ejecutamos el registerProfile
	if (patron.equals("RegisterProfileProcess")) {
	    if (arg1.equals("1")) {
		// para guardar nuestros ID para poder modificar
		// posteriormente nuestro servicio
		this.profileDescription.setServiceID(arg2);
		this.setSalidaString(arg2);

	    } else {
		this.setSalidaString(arg2);
	    }

	}


	// Si ejecutamos el ModifyProfile

	if (patron.equals("ModifyProfileProcess")) {
	    if (arg2.equals("1"))// ha hido todo bien
	    {
		this.setSalidaString(arg2);
	    } else if (arg2.equals("0"))// existen profile ligados a este
	    // process, por tanto no puede
	    // modificar-lo
	    {
		this.setSalidaString(arg2);
	    } else// el id del servicio no es valido
	    {
		this.salidaString = arg1;
	    }

	}
	
	
	
	// Si ejecutamos el ModifyProcess
	if (patron.equals("ModifyProcessProcess")) {
	  this.setSalidaString(arg2);

	}
	
	if (patron.equals("RemoveProvider"))
	{
	    this.setSalidaString(arg2); 
	}

	
	//si ejecutamos un servicio generico
	if (this.isgenericSerice) {
	
	    // sino no es un servicio del oms o del sf, segun los outputs
	    // sacamos los resultados.
	    String sub = msg.getContent().substring(msg.getContent().indexOf("=")+1);
	    String[] aux = sub.split(",");
	    

	    
	    
	    for (String output : oracle.getOutputs()) {
		for (int i = 0; i < aux.length; i++) {
	    	    String a = aux[i];
	    	    
		    if (i != (aux.length - 1))// menos el ultimo
		    {
			if (a.substring(a.indexOf("#")+1,a.indexOf("=")).equals(output))
			{
			    this.list.put(output, a.substring(a.indexOf("=") + 1));
			}
		    } else {
			if (a.substring(a.indexOf("#")+1,a.indexOf("=")).equals(output))
			{
			    this.list
				   .put(output, a.substring(a.indexOf("=") + 1, (a.length() - 1)));

			}

		    }
		}
	    }

	

	}

    }

    /**
     * TestAgentClient handles the messages received from the SF
     */
    static class TestAgentClient extends FIPARequestInitiator {
	SFProxy sf;
	String[] agen;

	protected TestAgentClient(QueueAgent agent, ACLMessage msg, SFProxy sf) {
	    super(agent, msg);
	    this.sf = sf;

	}

	protected void handleAgree(ACLMessage msg) {
	    logger.info(myAgent.getName() + ": OOH! " + msg.getSender().getLocalName()
		    + " Has agreed to excute the service!");

	}

	protected void handleRefuse(ACLMessage msg) {
	    logger.info(myAgent.getName() + ": Oh no! " + msg.getSender().getLocalName()
		    + " has rejected my proposal.");
	    this.sf.setSalida(false);
	    this.sf.setSalidaString(msg.getContent());

	}

	protected void handleInform(ACLMessage msg) {
	    logger.info(myAgent.getName() + ":" + msg.getSender().getLocalName()
		    + " has informed me of the status of my request." + " They said : "
		    + msg.getContent());
	    this.sf.extractInfo(msg);

	}

	protected void handleNotUnderstood(ACLMessage msg) {
	    logger.info(myAgent.getName() + ":" + msg.getSender().getLocalName()
		    + " has indicated that they didn't understand.");
	    this.sf.setSalida(false);
	    this.sf.setSalidaString(msg.getContent());
	}

	protected void handleOutOfSequence(ACLMessage msg) {
	    logger.info(myAgent.getName() + ":" + msg.getSender().getLocalName()
		    + " has send me a message which i wasn't" + " expecting in this conversation");
	    this.sf.setSalida(false);
	    this.sf.setSalidaString(msg.getContent());
	}
    }

}
