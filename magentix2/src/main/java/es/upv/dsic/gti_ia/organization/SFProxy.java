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

	
	/**
	 * This class gives us the support to accede to the services of the SF
	 * @param agent is a Magentix2 Agent, this agent implemented the communication protocol
	 * @param SFServiceDescriptionLocation The URL where the owl-s documents are located
	 */
	public SFProxy(BaseAgent agent, String SFServiceDescriptionLocation) {

		super(agent,"SF",SFServiceDescriptionLocation);

	}

	/**
	 * 
	 * This class gives us the support to accede to the services of the SF,
	 * Checked that the data contained in the file configuration/Settings.xml, the URL
	 * ServiceDescriptionLocation is not empty and is the correct path.
	 * @param agent is a Magentix2 Agent, this agent implemented the communication protocol
	 * 
	 */
	public SFProxy(BaseAgent agent) {

		super(agent, "SF");
		ServiceDescriptionLocation = c.getSFServiceDescriptionLocation();

	}

	
	//TODO revisar esto...
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
			AgentID agentProvider, String URLProfile,
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
				arguments = arguments + " -- " + s + "=" + ArrayArguments.get(i);
			i++;
		}

		// build the message to service provider
		call = URLProfile+" -- "+arguments;

		clientProvider = agentProvider.name;



		return  (Hashtable<String, String>) this.sendInform();

	}



	/**
	 * Removes a provider from a registered service
	 * @param serviceProfile URI of the service to remove the provider
	 * @param providerName of the provider to remove
	 * @return status which indicates if an error occurs (1:OK otherwise 0)
	 */
	public String removeProvider(String serviceProfile, String providerName) {
		
		serviceName = "RemoveProvider";

		if (serviceProfile.equals("") ) {
			logger.error("serviceProfile is empty");
			return "";

		}
		call = serviceName+" -- "+
		" RemoveProviderInputServiceProfile="+ serviceProfile+" -- "+
		" RemoveProviderInputProviderName="+providerName;

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
	public String searchService(String inputs, String outputs, String keywords)
	{

		serviceName = "SearchService";
		call = serviceName +" -- "+
		" SearchServiceInputInputs="+inputs+" -- "+
		" SearchServiceInputOutputs="+outputs+" -- "+
		" SearchServiceInputKeywords="+keywords;

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
	public String deregisterService(String serviceProfile)  {

		
		serviceName = "DeregisterService";
		if (serviceProfile.equals("")) {
			logger.error("serviceProfile is  empty");
			return "";
		}

		call = serviceName+" -- "+
		" DeregisterServiceInputServiceProfile="+serviceProfile;

		return (String) this.sendInform();

	}
	
	public String getService(String serviceProfile)
	{
		serviceName="GetService";
		
		call=serviceName+" -- "+
		" GetServiceInputServiceProfile="+serviceProfile;
		
		return (String) this.sendInform();
	}
	
	public String registerService(String serviceURL)
	{
		serviceName="RegisterService";
		
		call=serviceName+" -- "+
		" RegisterServiceInputServiceURL="+serviceURL;
		
		return (String) this.sendInform();
	}

	
}
