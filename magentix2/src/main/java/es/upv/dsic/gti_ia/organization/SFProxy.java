/**
 * This package contains the definition of the classes for the interaction with the THOMAS organization
 */
package es.upv.dsic.gti_ia.organization;

import java.net.MalformedURLException;
import java.net.URL;
//import java.util.HashMap;
import java.util.ArrayList;
import java.util.Hashtable;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

/**
 *This class provides access to services that implements the SF agent.
 * 
 * @author Joan Bellver Faus GTI-IA.DSIC.UPV
 */
public class SFProxy extends THOMASProxy {





//	private HashMap<String, String> tablaSearchServiceProfile = new HashMap<String, String>();

	/**
	 *  This class gives us the support to accede to the services of the SF
	 * 
	 * @param agent
	 *            is a Magentix2 Agent, this agent implemented the communication
	 *            protocol.
	 * @param SFServiceDesciptionLocation
	 *            URLProcess The URL where the owl's document is located.
	 */
	public SFProxy(BaseAgent agent, String SFServiceDesciptionLocation) {

		super(agent,"SF",SFServiceDesciptionLocation);

	}

	/**
	 * 
	 * This class gives us the support to accede to the services of the SF,
	 * Checked that the data contained in the file configuration/Settings.xml, the URL
	 * ServiceDescriptionLocation is not empty and is the correct path.
	 * 
	 * @param agent
	 *            is a Magentix2 Agent, this agent implemented the communication
	 *            protocol
	 * 
	 * 
	 */
	public SFProxy(BaseAgent agent) {

		super(agent, "SF");
		ServiceDescriptionLocation = c.getSFServiceDesciptionLocation();

	}

	/**
	 * When the service is not SF or OMS service. This method is recommend used when an other provider agent offer a new service 
	 * 
	 * @param agentProvider
	 *            The agent who offers the service. Returned by the method getProcess.
	 * @param URLProfile
	 *            Returned by method getProfile.
	 * @param URLProcess
	 *            Returned by method getProcess.
	 * @param ArrayArguments
	 *            Input arguments of the service.
	 *             
	 * @return Hashtable<String, String> is a Hashtable with a pair of key and value. 
	 * The key is name of output, and value is the value returned.
	 */
	@SuppressWarnings("unchecked")
	public Hashtable<String, String> genericService(
			AgentID agentProvider, String URLProfile, String URLProcess,
			ArrayList<String> ArrayArguments){

		isgenericSerice = true;
		serviceName = "Generic";


		URL profile;
		try {
			profile = new URL(URLProfile);
		} catch (MalformedURLException e) {
			logger.error("ERROR: Profile URL Malformed!");
			e.printStackTrace();
			return new Hashtable<String,String>();
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
		call = URLProcess + arguments;

		clientProvider = agentProvider.name;



		return  (Hashtable<String, String>) this.sendInform();

	}



	/**
	 * It deletes a provider from a service implementation. If this is a last provider, the implementation is
	 * automatically erased.
	 * 
	 * @param ProcessDescription
	 *            Must have at least completed the field Implementation ID
	 * @return status RemoveProviderResponse contains an element: return which
	 *         indicates if an error occurs (1:OK otherwise 0)
	 */

	public String removeProvider(
			ProcessDescription ProcessDescription) {
		this.processDescripcion = ProcessDescription;
		serviceName = "RemoveProviderProcess";

		if (ProcessDescription.getImplementationID().equals("")) {
			logger.error("ImplementationID is empty");
			return "";

		}
		call = ServiceDescriptionLocation
		+ "RemoveProviderProcess.owl "
		+ "RemoveProviderInputServiceImplementationID="
		+ this.processDescripcion.getImplementationID()+" RemoveProviderInputProviderID="+agent.getAid().toString();

		return (String) this.sendInform();
	}

	/**
	 * It searches a service whose description satisfies the client request. 
	 * 

	 * @param serviceGoal
	 *            service purpose (is a string: the service description).
	 * @return services list (is a list of service profile id, ranking: service
	 *         profile id, ranking: ...) or return which
	 *         indicates if an error occurs
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> searchService(String serviceGoal)
	{

		serviceName = "SearchServiceProcess";
		call = ServiceDescriptionLocation
		+ "SearchServiceProcess.owl SearchServiceInputServicePurpose="
		+ serviceGoal;

		return (ArrayList<String>) this.sendInform();	
	}

	/**
	 * It is used to modify the implementation of a registered service. The client specifies 
	 * the part of the service to be modified. The service Id will not change.
	 * @param ProcessDescription
	 *            contains two elements: service implementation ID (is a string:
	 *            serviceprofile@servicenumidagent), service model (is a
	 *            string: urlprocess#processname, this parameter is entered when
	 *            creating the instance of ProcessDescription)).
	 * @return ModifyProcessResponse contains return which indicates if an error
	 *         occurs (1:OK, otherwise 0).
	 * 
	 */
	public String modifyProcess(
			ProcessDescription ProcessDescription) 

	{
		this.processDescripcion = ProcessDescription;
		serviceName = "ModifyProcessProcess";
		if (ProcessDescription.getImplementationID().equals("")
				|| ProcessDescription.getServiceModel().equals("")) {
			logger.error("ImplementationID or Service Goal is  empty");
			return "";

		}

		call = ServiceDescriptionLocation + "ModifyProcessProcess.owl"
		+ " ModifyProcessInputServiceGrounding= "
		+ " ModifyProcessInputServiceImplementationID="
		+ this.processDescripcion.getImplementationID()
		+ " ModifyProcessInputServiceModel="
		+ this.processDescripcion.getServiceModel();

		return (String) this.sendInform();

	}

	/**
	 * It is used to modify the description (profile) of a registered service. The client specifies the part of the service 
	 * to be modified. The service Id not change.
	 * 
	 * @param ProfileDescription
	 *            contains three elements: service id (is a string: service
	 *            profile id), service goal (currently is not in use),and
	 *            service profile ( is a string urlprofile#profilename, this
	 *            parameter is entered when creating the instance of
	 *            ProfileDescription))
	 * @return Status return which indicates if a problem occurs (1: ok, 0:
	 *         there are provider which implement the profile, -1: the service
	 *         id is not valid).
	 */
	public String modifyProfile(
			ProfileDescription ProfileDescription){

		this.profileDescription = ProfileDescription;
		serviceName = "ModifyProfileProcess";

		if (ProfileDescription.getServiceID().equals("")
				|| ProfileDescription.getServiceProfile().equals("")) {
			logger.error("ID or Service Goal is  empty");
			return "";


		}

		call = ServiceDescriptionLocation + "ModifyProfileProcess.owl "
		+ "ModifyProfileInputServiceID="
		+ this.profileDescription.getServiceID()
		+ " ModifyProfileInputServiceGoal=" + " "
		+ " ModifyProfileInputServiceProfile="
		+ this.profileDescription.getServiceProfile();

		return (String) this.sendInform();

	}

	/**
	 * It is used to delete a service description.
	 * 
	 * @param ProfileDescription
	 *            in this structure a one element is required: service id (is a string: service profile
	 *            id)
	 * @return Status  return indicates if an error occurs.
	 */
	public String deregisterProfile(
			ProfileDescription ProfileDescription)  {

		this.profileDescription = ProfileDescription;
		serviceName = "DeregisterProfileProcess";
		if (ProfileDescription.getServiceID().equals("")) {
			logger.error("ID is  empty");
			return "";
		}



		call = ServiceDescriptionLocation
		+ "DeregisterProfileProcess.owl DeregisterProfileInputServiceID="
		+ profileDescription.getServiceID();

		return (String) this.sendInform();

	}

	/**
	 * This service returns the providers which implements the required profile.
	 * 
	 * @param serviceID
	 *            the service ID (is a string: service profile id).
	 * @return provider list (is a hashtable with a pair of key and value. 
	 * The key is agent that offered the service and value is a URL process).
	 */

	@SuppressWarnings("unchecked")
	public Hashtable<AgentID, String> getProcess(
			String serviceID){

		serviceName = "GetProcessProcess";
		call = ServiceDescriptionLocation
		+ "GetProcessProcess.owl GetProcessInputServiceID=" + serviceID;
		/*
		 * + sfAgentdescription.getURLProfile() + descripcion.getID() + ".owl#"
		 * + descripcion.getID();
		 */
		return (Hashtable<AgentID, String>) this.sendInform();

	}

	/**
	 * This service returns the URL of the required profile.
	 * 

	 * @param serviceID
	 *            the service ID (is a string: service profile id)
	 * @return Status contains three elements: service profile (is a string: the
	 *         URL profile), or indicates if an error occurs.
	 */
	public String getProfile(String serviceID)
	{

		call = ServiceDescriptionLocation
		+ "GetProfileProcess.owl GetProfileInputServiceID=" + serviceID;

		serviceName = "GetProfileProcess";
		return (String) this.sendInform();

	}



	/**
	 * 
	 * It is used when an autonomous entity wants to register a service description. To do this the following structure has
	 * to be completed in order the service description (ProfileDescription).
	 * This method assigns an Id to the structure ProfileDescription. This result implies that the service is publicly available.
	 * 
	 * The execution of this service implies:
	 *	- Checks if ServiceProfile isn't null
	 * @param ProfileDescription
	 *            This parameter contains one element necessary: service profile ( is a
	 *            string: urlprofile#profilename, this parameter is entered when
	 *            creating the instance of ProfileDescription, therefore it is not necessary to be add it.) )
	 * @return Status indicates if an error occurs (1:OK , 0: bad news).

	 */
	public String registerProfile(
			ProfileDescription ProfileDescription){

		this.profileDescription = ProfileDescription;
		serviceName = "RegisterProfileProcess";
		if (ProfileDescription.getServiceProfile().equals("")) {
			logger.error("Service Profile or Service Goal is empty");
			return "";

		}


		call = ServiceDescriptionLocation
		+ "RegisterProfileProcess.owl "
		+ "RegisterProfileInputServiceGoal= "
		+ " RegisterProfileInputServiceProfile="
		+ this.profileDescription.getServiceProfile();

		return (String) this.sendInform();



	}

	/**
	 * It is used when an agent wants to register a particular implementation of a given service.
	 * Internally this method assigns an ImplementationId to the structure
	 * ProcessDescription

	 *@param ProcessDescription
	 *            this parameter contains two elements necessary: service profile id,
	 *            this parameter is returned when we call the method
	 *            searchService (use ProcessDescription method setProfileID to add), and service model, this
	 *            parameter is entered when creating the instance of
	 *            ProcessDescription.
	 * @return status indicates if an error occurs (1:OK , 0: bad news).
	 */
	public String registerProcess(
			ProcessDescription ProcessDescription) {


		this.processDescripcion = ProcessDescription;
		serviceName = "RegisterProcessProcess";
		if (this.processDescripcion.getProfileID().equals("")
				|| this.processDescripcion.getServiceModel().equals("")) {
			logger.error("ID or Service Model is empty");
			return "";

		}

		call = ServiceDescriptionLocation
		+ "RegisterProcessProcess.owl"
		+ " RegisterProcessInputServiceID="
		+ this.processDescripcion.getProfileID()
		+ " RegisterProcessInputServiceModel="
		+ this.processDescripcion.getServiceModel();

		return (String) this.sendInform();


	}


}
