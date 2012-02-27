/**
 * This package contains the definition of the classes for the interaction with the THOMAS organization
 */
package es.upv.dsic.gti_ia.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import es.upv.dsic.gti_ia.core.BaseAgent;

/**
 * This class provides access to services that implements the SF agent.
 * 
 * @author Joan Bellver Faus GTI-IA.DSIC.UPV
 */
public class SFProxy extends THOMASProxy {

	ServiceTools st = new ServiceTools();

	/**
	 * This class gives us the support to accede to the services of the SF
	 * 
	 * @param agent
	 *            is a Magentix2 Agent, this agent implemented the communication
	 *            protocol
	 * @param SFServiceDescriptionLocation
	 *            The URL where the owl-s documents are located
	 */
	public SFProxy(BaseAgent agent, String SFServiceDescriptionLocation) {

		super(agent, "SF", SFServiceDescriptionLocation);

	}

	/**
	 * 
	 * This class gives us the support to accede to the services of the SF,
	 * Checked that the data contained in the file configuration/Settings.xml,
	 * the URL ServiceDescriptionLocation is not empty and is the correct path.
	 * 
	 * @param agent
	 *            is a Magentix2 Agent, this agent implemented the communication
	 *            protocol
	 * 
	 */
	public SFProxy(BaseAgent agent) {

		super(agent, "SF");
		ServiceDescriptionLocation = c.getSFServiceDescriptionLocation();

	}

	/**
	 * The Register Service tries to register the service that is specified as
	 * parameter. In the specification, if there is one or more groundings, it
	 * means that the service is provided by a Web Service. If one or more
	 * providers (agents or organization) are specified in the
	 * profile:contactInformation of the service, it means that the service is
	 * provided by agents or/and organizations
	 * 
	 * @param serviceURL
	 *            the original URL of the OWL-S specification of the service
	 * @return A description of the changes made, and an OWL-S specification of
	 *         the registered services or all data of the already registered
	 *         service in the SF
	 * @throws THOMASException
	 *             If there is any error result
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> registerService(String serviceURL) throws THOMASException {

		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("ServiceURL", serviceURL);

		call = st.buildServiceContent("RegisterService", inputs);

		return (ArrayList<String>) this.sendInform();
	}

	/**
	 * The Deregister Service deregisters the specified service deleting all the
	 * related data from the SF.
	 * 
	 * @param serviceProfile
	 *            the URI representing the service profile to deregister
	 * @return A description of the result of the service execution.
	 * @throws THOMASException
	 *             If there is any error result
	 */
	public String deregisterService(String serviceProfile) throws THOMASException {

		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("ServiceProfile", serviceProfile);

		call = st.buildServiceContent("DeregisterService", inputs);
		return (String) this.sendInform();

	}

	/**
	 * Removes a provider: agent, organization or web service (grounding); from
	 * a registered service profile
	 * 
	 * @param serviceProfile
	 *            URI of the service profile to remove the provider
	 * @param providerID
	 *            of the provider to remove (provider name or grounding ID)
	 * @return A description of the result of the service execution
	 * @throws THOMASException
	 *             If there is any error result
	 */
	public String removeProvider(String serviceProfile, String providerID) throws THOMASException {

		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("ServiceProfile", serviceProfile);
		inputs.put("ProviderID", providerID);

		call = st.buildServiceContent("RemoveProvider", inputs);

		return (String) this.sendInform();
	}

	/**
	 * Returns an OWL-S specification with the all data of the specified service
	 * profile as parameter
	 * 
	 * @param serviceProfile
	 *            URI of the service profile to get its OWL-S specification
	 * @return an OWL-S specification with the all data of the specified service
	 *         profile as parameter
	 * @throws THOMASException
	 *             If there is any error result
	 */
	public String getService(String serviceProfile) throws THOMASException {

		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("ServiceProfile", serviceProfile);

		call = st.buildServiceContent("GetService", inputs);

		return (String) this.sendInform();
	}

	/**
	 * Searches the most similar services profiles to the given data type
	 * inputs, data type outputs and keywords in the description. Returns an
	 * ordered list of the services found with a similarity degree obtained in
	 * function of the similarity to the given parameters.
	 * 
	 * @param inputs
	 *            data type inputs to search a service with these inputs.
	 *            Example:
	 *            \"http://127.0.0.1/ontology/books.owl#Novel\"^^xsd:anyURI
	 * @param outputs
	 *            data type outputs to search a service with these outputs.
	 *            Example:
	 *            \"http://127.0.0.1/ontology/books.owl#Novel\"^^xsd:anyURI
	 * @param keywords
	 *            list to search in the text description of the service
	 * @return an ordered list of the services found with a similarity degree
	 * @throws THOMASException
	 *             If there is any error result
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<String>> searchService(ArrayList<String> inputs, ArrayList<String> outputs, ArrayList<String> keywords) throws THOMASException {

		String inputsStr = "";
		if (inputs != null && !inputs.isEmpty()) {
			Iterator<String> iterInputs = inputs.iterator();
			while (iterInputs.hasNext()) {
				String in = iterInputs.next();
				inputsStr += in + "|";
			}
		}
		String outputsStr = "";
		if (outputs != null && !outputs.isEmpty()) {
			Iterator<String> iterOutputs = outputs.iterator();
			while (iterOutputs.hasNext()) {
				String out = iterOutputs.next();
				outputsStr += out + "|";
			}
		}
		String keywordsStr = "";
		if (keywords != null && !keywords.isEmpty()) {
			Iterator<String> iterKeywords = keywords.iterator();
			while (iterKeywords.hasNext()) {
				String key = iterKeywords.next();
				keywordsStr += key + "|";
			}
		}

		HashMap<String, String> inputsService = new HashMap<String, String>();
		inputsService.put("Inputs", inputsStr);
		inputsService.put("Outputs", outputsStr);
		inputsService.put("Keywords", keywordsStr);

		call = st.buildServiceContent("SearchService", inputsService);

		return (ArrayList<ArrayList<String>>) this.sendInform();
	}

}
