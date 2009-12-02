package es.upv.dsic.gti_ia.organization;

/**
 *This class provides access to methods that implements the SF agent
 * 
 * @author Joan Bellver Faus GTI-IA.DSIC.UPV
 */

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

public class SFProxy {

    
    private String SFServiceDesciptionLocation;
    private SFProcessDescription processDescripcion;
    private SFProfileDescription profileDescription;
    
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

    Configuration c = Configuration.getConfiguration();

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
     * This class gives us the support to accede to the services of the SF
     */
    public SFProxy() {

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
    SFProfileDescription getProfileDescription() {
	return this.profileDescription;
    }
    
    SFProcessDescription getProcessDescription(){
	
	return this.processDescripcion;
    }

    /**
     * When the service is not SF or OMS service
     * 
     * @param agent 
     * @param agentProvider The agent who offers the service
     * @param URLProfile The address this where the profile of the service 
     * @param URLProcess The address this where the process of the service
     * @param ArrayArguments Input arguments  of the service
     * @return outputs
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
	// ArrayList<String> outputs = oracle.getOutputs();

	// Build call arguments
	String arguments = "";
	int i = 0;
	for (String s : inputs) {
	    // arguments.concat(" ").concat(s).concat("=").concat(this.getAID().getLocalName());
	    // s = s.substring(1, s.length());
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
     * Remove provider agent
     * 
     * @param agent
     * @param SFProcessDescription
     * @return status RemoveProviderResponse contains an element: return which indicates if an error occurs (1:OK otherwise 0)
     * @throws Exception
     */

    public String removeProvider(QueueAgent agent, SFProcessDescription SFProcessDescription)
	    throws Exception {
	this.processDescripcion = SFProcessDescription;

	if (SFProcessDescription.getImplementationID().equals(""))
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
     * Return a service list
     * 
     * @param agent
     * @param serviceGoal service purpose (is a string: the service description).
     * @return services list (is a list of 
     * <service profile id, ranking: service profile id, ranking: ...>
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
     * Modify Process
     * 
     * @param agent
     * @param SFProcessDescription  contains two elements: service implementation ID (is a string: 
     * serviceprofile@servicenumdidagent), service model (is a string: urlprocess#processname).
     * @return ModifyProcessResponse contains return which indicates if an error occurs (1:OK,
     * otherwise 0).
     *
     * @throws Exception
     */
    public String modifyProcess(QueueAgent agent, SFProcessDescription SFProcessDescription)
	    throws Exception

    {
	this.processDescripcion = SFProcessDescription;

	if (SFProcessDescription.getImplementationID().equals("") || SFProcessDescription.getServiceModel().equals(""))
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
     * Modify Profile
     * 
     * @param agent
     * @param SFProfileDescription contains three elements: service id (is a string: service profile id), service
         * goal (currently is not in use),and service profile ( is a string 
         * urlprofile#profilename)
     * @return Status return which indicates if a problem occurs (1: ok, 0: there
         * are provider which implement the profile, -1: the service id is not valid).
     * @throws Exception
     */
    public String modifyProfile(QueueAgent agent, SFProfileDescription SFProfileDescription)
	    throws Exception {

	this.profileDescription = SFProfileDescription;

	if (SFProfileDescription.getServiceID().equals("") || SFProfileDescription.getServiceProfile().equals(""))
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
     * Deregister Profile
     * 
     * @param agent
     * @param SFProfileDescription contains one element: service id (is a string: service profile id)
     * @return Status DeregisterProfileResponse contains an element: return indicates if an error occurs (
         * 0: ok, 1:error).
     * @throws Exception
     */
    public String deregisterProfile(QueueAgent agent, SFProfileDescription SFProfileDescription)
	    throws Exception {

	this.profileDescription = SFProfileDescription;

	if (SFProfileDescription.getServiceID().equals(""))
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
     * Return provider list
     * 
     * @param agent
     * @param id the service ID (is a string: service profile id) and the 
	 * agent id (is a string).
     * @return provider list (is a string with the next template:
	 * [service implementation id urlprocess, service implementation id urlproces, ... ] 
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
     * Return service profile ( the URL profile)
     * 
     * @param agent
     * @param serviceID the service ID (is a string: service profile id)
     * @return Status contains three elements: service profile (is a string: the url profile), 
	 * the goal of the profile (currently is not in use) and the return (is an integer) which indicates if
	 * an error occurs. 
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
     * Register profile
     * 
     * @param agent
     * @param SFProfileDescription This parameter contains two elements: service goal ( is a string ),
	 * service profile ( is a string: urlprofile#profilename )
     * @return Status indicates if an error occurs.
     * @throws Exception
     */
    public String registerProfile(QueueAgent agent, SFProfileDescription SFProfileDescription)
	    throws Exception {

	this.profileDescription = SFProfileDescription;

	if (SFProfileDescription.getServiceProfile().equals("") || SFProfileDescription.getServiceGoal().equals(""))
	{
	    throw new Exception("Service Profile or Service Goal is  empty");
	    
	}
	
	String call = SFServiceDesciptionLocation + "RegisterProfileProcess.owl "
		+ "RegisterProfileInputServiceGoal=" + this.profileDescription.getServiceGoal()
		+ " RegisterProfileInputServiceProfile=" + this.profileDescription.getServiceProfile();

	this.sendInfo(agent, call);

	if (!salida)
	    throw new Exception("Register Profile: " + this.salidaString);
	else
	    return this.salidaString;

    }

    /**
     * Register Process
     * 
     * @param agent
        *@param SFProfileDescription. This parameter contains two elements: service id (is a string), and service
	 * model (is a string: urlprocess#urlprocessname).
	 * @return status  indicates if an error occurs (1:ok , 0: bad news).
     * @throws Exception
     */
    public String registerProcess(QueueAgent agent, SFProcessDescription SFProcessDescription)
	    throws Exception {
	// montar string de conexion
	// Enviamos el mensaje

	this.processDescripcion= SFProcessDescription;

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
