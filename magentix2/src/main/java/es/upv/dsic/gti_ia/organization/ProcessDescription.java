package es.upv.dsic.gti_ia.organization;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.mindswap.owl.EntityFactory;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * This class is used to stored the complete description of a process to publish
 * in the organization
 * 
 * @author Joan Bellver Faus
 */

public class ProcessDescription {

	private String ImplementationID;
	private String servicemodel;
	private String URLProcess;
	private String ServiceID; // id profile
	private OWLKnowledgeBase kb = OWLFactory.createKB();

	/**
	 * 
	 * @param URLProcess
	 *            The URL where the owl's document (related with service
	 *            process) is located.
	 * @param proccessName
	 * 		Name to the service process description document.
	 * 
	 */
	public ProcessDescription(String URLProcess, String processName) {

		this.ServiceID = "";
		this.ImplementationID = "";
		this.URLProcess = URLProcess;
		this.servicemodel = this.URLProcess + "#" + processName;

	}

	/**
	 * Returns a URL which makes reference to the service process description document. 
	 * 
	 * @return serviceModel 
	 */
	String getServiceModel() {
		return this.servicemodel;
	}

	/**
	 * Change a URL which makes reference to the service process description document. 
	 * @param processName
	 */
	void setServiceModel(String processName) {

		this.servicemodel = this.URLProcess + "#" + processName;
	}

	/**
	 * This method changes the URL where the owl's document (related with service process) is located.
	 * 
	 * @param url
	 */
	public void setURLProcess(String url) {
		this.URLProcess = url;
	}

	/**
	 * This method returns the URL where the owl's document (related with service process) is
	 * located.
	 * 
	 * @return String
	 */
	public String getURLProcess() {
		return this.URLProcess;
	}

	/**
	 * This method changes ID of the profile which is associated the process
	 * 
	 * @param id this parameter is returned when we call the method searchService.
	 */
	public void setProfileID(String id) {
		this.ServiceID = id;
	}

	/**
	 * This method returns the ID of the profile which is associated the process
	 * 
	 * @return
	 */
	public String getProfileID() {
		return this.ServiceID;
	}

	/**
	 * This method adds the implementationID
	 * 
	 * @param im this parameter is automatically assigned when the method registerProcess is called.
	 */
	public void setImplementationID(String im) {
		this.ImplementationID = im;
	}

	/**
	 * This method returns implementationID
	 * 
	 * @return
	 */
	public String getImplementationID() {
		return this.ImplementationID;

	}

	/**
	 * This method returns a org.mindswap.owls.process.Process to use him to the execution of a service. 
	 * @param inmsg This message is sent by the client agent wants to make use of service, in the message content is the path of the service process
	 * @return Process is a mindswap.owls.process.Process;
	 * @throws Exception
	 */
	public Process getProcess(ACLMessage inmsg) throws Exception {

		// read msg content
		StringTokenizer Tok = new StringTokenizer(inmsg.getContent());

		// read in the service description
		String token_process = Tok.nextElement().toString();

		try {
			Service aService = kb.readService(token_process);

			// get the process for the server
			Process aProcess = aService.getProcess();

			return aProcess;

		} catch (Exception e) {

			e.printStackTrace();
			throw new Exception(e);

		}

	}
	
	/**
	 * This method returns a String with the process URL to use it in the execution of a service.
	 * @param inmsg This message is sent by the client agent wants to make use of service, in the message content is the path of the service process
	 * @return Process URL
	 * @throws Exception
	 */
	public String getProcessURL(ACLMessage inmsg) throws Exception {
		try{
			// read msg content
			StringTokenizer Tok = new StringTokenizer(inmsg.getContent());
	
			// read in the service description
			String token_process = Tok.nextElement().toString();
			
			return token_process;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e);
		}
	}
	
	/**
	 * This method returns a String with the process' local name to use it in the execution of a service.
	 * @param inmsg This message is sent by the client agent wants to make use of service, in the message content is the path of the service process
	 * @return Process' local name
	 * @throws ExceptiongetProcessURL(ACLMessage inmsg)
	 */
	public String getProcessLocalName(ACLMessage inmsg) throws Exception {
		
		try{
			// read in the service description
			String token_process = getProcessURL(inmsg);
			
			//extract process' local name
			int nameLength=token_process.split("\\.")[0].split("/").length;
			String process_localName= token_process.split("\\.")[0].split("/")[nameLength-1];
			
			return process_localName;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	public List<String> getServiceRequestValuesList(ACLMessage inmsg) throws Exception {
		try{
			// read msg content
			StringTokenizer Tok = new StringTokenizer(inmsg.getContent());
			Tok.nextElement().toString();
			
			List<String> params= new ArrayList<String>();
			while(Tok.hasMoreElements()){
				String paramComplete=Tok.nextElement().toString();
				String param="";
				if(paramComplete.split("=").length>1)
					param=paramComplete.split("=")[1];
				params.add(param);
				//System.out.println("[Provider]Value: " + param);
			}
			return params;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e);
		}
	}
	
	
	
	
	/**
	 * This method returns a ValueMap with the name of the field and the value that there gives us the client who calls to the service.
	 * @param inmsg This message is sent by the client agent wants to make use of service, in the message content are input values.
	 * @return ValueMap 
	 * @throws Exception
	 */

	public ValueMap getServiceRequestValues(ACLMessage inmsg) throws Exception {

		// read msg content
		StringTokenizer Tok = new StringTokenizer(inmsg.getContent());

		// read in the service description
		String token_process = Tok.nextElement().toString();

		//System.out.println("[Provider]Doc OWL-S: " + token_process);

		try {
			Service aService = kb.readService(token_process);

			// get the process for the server
			Process aProcess = aService.getProcess();

			// initialize the input values to be empty
			ValueMap values = new ValueMap();

			// get the input values
			// int n = 0;

			// int tokenCount = Tok.countTokens();
			for (int i = 0; i < aProcess.getInputs().size(); i++)
				values.setValue(aProcess.getInputs().inputAt(i), EntityFactory.createDataValue(""));

			while (Tok.hasMoreElements()) {
				String token = Tok.nextElement().toString();
				for (int i = 0; i < aProcess.getInputs().size(); i++) {
					String paramName = aProcess.getInputs().inputAt(i).getLocalName().toLowerCase();
					if (paramName.equalsIgnoreCase(token.split("=")[0].toLowerCase())) {
						if (token.split("=").length >= 2)
							values.setValue(aProcess.getInputs().inputAt(i), EntityFactory
									.createDataValue(token.split("=")[1]));
						else
							values.setValue(aProcess.getInputs().inputAt(i), EntityFactory
									.createDataValue(""));
						break;
					}
				}
			}// end while
			return values;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}

	}

}