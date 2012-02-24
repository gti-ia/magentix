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

	public ArrayList<String> registerService(String serviceURL) throws THOMASException {

		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("ServiceURL", serviceURL);

		call = st.buildServiceContent("RegisterService", inputs);

		return (ArrayList<String>) this.sendInform();
	}

	public String deregisterService(String serviceProfile) throws THOMASException {

		if (serviceProfile.equals("")) {
			logger.error("serviceProfile is  empty");
			return "";
		}

		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("ServiceProfile", serviceProfile);

		call = st.buildServiceContent("DeregisterService", inputs);
		return (String) this.sendInform();

	}

	public String getService(String serviceProfile) throws THOMASException {

		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("ServiceProfile", serviceProfile);

		call = st.buildServiceContent("GetService", inputs);

		return (String) this.sendInform();
	}

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

	/**
	 * Removes a provider from a registered service
	 * 
	 * @param serviceProfile
	 *            URI of the service to remove the provider
	 * @param providerID
	 *            of the provider to remove
	 * @return status which indicates if an error occurs (1:OK otherwise 0)
	 */
	public String removeProvider(String serviceProfile, String providerID) throws THOMASException {

		if (serviceProfile.equals("")) {
			logger.error("serviceProfile is empty");
			return "";

		}

		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("ServiceProfile", serviceProfile);
		inputs.put("ProviderID", providerID);

		call = st.buildServiceContent("RemoveProvider", inputs);

		return (String) this.sendInform();
	}

}
