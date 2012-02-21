/**
 * This package contains the definition of the classes for the interaction with the THOMAS organization
 */
package es.upv.dsic.gti_ia.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import es.upv.dsic.gti_ia.core.BaseAgent;

/**
 *This class provides access to services that implements the SF agent.
 * 
 * @author Joan Bellver Faus GTI-IA.DSIC.UPV
 */
public class SFProxy extends THOMASProxy {

	//TODO this variables in a configuration file!!!
	String separatorToken=" ";
	private static HashMap<String, String> sfServicesURLs=new HashMap<String, String>();
	
	/**
	 * This class gives us the support to accede to the services of the SF
	 * @param agent is a Magentix2 Agent, this agent implemented the communication protocol
	 * @param SFServiceDescriptionLocation The URL where the owl-s documents are located
	 */
	public SFProxy(BaseAgent agent, String SFServiceDescriptionLocation) {

		super(agent,"SF",SFServiceDescriptionLocation);
		
		//TODO in the config file!!!
		sfServicesURLs.put("RegisterService", "http://localhost:8080/sfservices/SFservices/owl/owls/RegisterService.owl");
		sfServicesURLs.put("DeregisterService", "http://localhost:8080/sfservices/SFservices/owl/owls/DeregisterService.owl");
		sfServicesURLs.put("GetService", "http://localhost:8080/sfservices/SFservices/owl/owls/GetService.owl");
		sfServicesURLs.put("SearchService", "http://localhost:8080/sfservices/SFservices/owl/owls/SearchService.owl");
		sfServicesURLs.put("RemoveProvider", "http://localhost:8080/sfservices/SFservices/owl/owls/RemoveProvider.owl");

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

		sfServicesURLs.put("RegisterService", "http://localhost:8080/sfservices/SFservices/owl/owls/RegisterService.owl");
		sfServicesURLs.put("DeregisterService", "http://localhost:8080/sfservices/SFservices/owl/owls/DeregisterService.owl");
		sfServicesURLs.put("GetService", "http://localhost:8080/sfservices/SFservices/owl/owls/GetService.owl");
		sfServicesURLs.put("SearchService", "http://localhost:8080/sfservices/SFservices/owl/owls/SearchService.owl");
		sfServicesURLs.put("RemoveProvider", "http://localhost:8080/sfservices/SFservices/owl/owls/RemoveProvider.owl");

	}





	/**
	 * Removes a provider from a registered service
	 * @param serviceProfile URI of the service to remove the provider
	 * @param providerID of the provider to remove
	 * @return status which indicates if an error occurs (1:OK otherwise 0)
	 */
	public String removeProvider(String serviceProfile, String providerID) throws THOMASException{
		
		serviceName = sfServicesURLs.get("RemoveProvider");

		if (serviceProfile.equals("") ) {
			logger.error("serviceProfile is empty");
			return "";

		}
		call = serviceName+separatorToken+
		"ServiceProfile="+ serviceProfile+separatorToken+
		"ProviderID="+providerID;

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
	public ArrayList<ArrayList<String>> searchService(ArrayList<String> inputs, ArrayList<String> outputs, ArrayList<String> keywords) throws THOMASException
	{

		String inputsStr="";
		Iterator<String> iterInputs=inputs.iterator();
		while(iterInputs.hasNext()){
			String in=iterInputs.next();
			inputsStr+=in+"|";
		}
		String outputsStr="";
		Iterator<String> iterOutputs=outputs.iterator();
		while(iterOutputs.hasNext()){
			String out=iterOutputs.next();
			outputsStr+=out+"|";
		}
		String keywordsStr="";
		Iterator<String> iterKeywords=keywords.iterator();
		while(iterKeywords.hasNext()){
			String key=iterKeywords.next();
			keywordsStr+=key+"|";
		}
		
		
		serviceName = sfServicesURLs.get("SearchService");
		call = serviceName +separatorToken+
		"Inputs="+inputsStr+separatorToken+
		"Outputs="+outputsStr+separatorToken+
		"Keywords="+keywordsStr;

		return (ArrayList<ArrayList<String>>) this.sendInform();
	}

	

	/**
	 * It is used to delete a service description.
	 * 
	 * @param ProfileDescription
	 *            in this structure a one element is required: service id (is a string: service profile
	 *            id)
	 * @return Status  return indicates if an error occurs.
	 */
	public String deregisterService(String serviceProfile) throws THOMASException {

		
		serviceName = sfServicesURLs.get("DeregisterService");
		if (serviceProfile.equals("")) {
			logger.error("serviceProfile is  empty");
			return "";
		}

		call = serviceName+separatorToken+
		"ServiceProfile="+serviceProfile;

		return (String) this.sendInform();

	}
	
	public String getService(String serviceProfile) throws THOMASException
	{
		serviceName=sfServicesURLs.get("GetService");
		
		call=serviceName+separatorToken+
		"ServiceProfile="+serviceProfile;
		
		return (String) this.sendInform();
	}
	
	public String registerService(String serviceURL) throws THOMASException
	{
		serviceName=sfServicesURLs.get("RegisterService");
		
		call=serviceName+separatorToken+
		"ServiceURL="+serviceURL;
		
		return (String) this.sendInform();
	}

	
}

