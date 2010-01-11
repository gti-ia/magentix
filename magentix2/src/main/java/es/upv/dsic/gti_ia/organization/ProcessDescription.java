package es.upv.dsic.gti_ia.organization;

import java.util.StringTokenizer;

import org.mindswap.owl.EntityFactory;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * This class is used to store the complete description of a process to publish
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
     * Returns a url which makes reference to the service process description document. 
     * 
     * @return serviceModel 
     */
    public String getServiceModel() {
	return this.servicemodel;
    }

    /**
     * Change a url which makes reference to the service process description document. 
     * @param processName
     */
    public void setServiceModel(String processName) {

	this.servicemodel = this.URLProcess + "#" + processName;
    }

    /**
     * Change The URL where the owl's document (related with service process) is
     * located.
     * 
     * @param url
     */
    public void setURLProcess(String url) {
	this.URLProcess = url;
    }

    /**
     * Return the URL where the owl's document (related with service process) is
     * located.
     * 
     * @return String
     */
    public String getURLProcess() {
	return this.URLProcess;
    }

    /**
     * Change ID of the profile which is associated the process
     * 
     * @param id this param is returned when we call the method searchService.
     */
    public void setProfileID(String id) {
	this.ServiceID = id;
    }

    /**
     * Returns the id of the profile which is associated the process
     * 
     * @return
     */
    public String getProfileID() {
	return this.ServiceID;
    }

    /**
     * Add the implementationID
     * 
     * @param im , this parameter is automatically assigned when the method  registerProcess is called.
     */
    public void setImplementationID(String im) {
	this.ImplementationID = im;
    }

    /**
     * Return implementationID
     * 
     * @return
     */
    public String getImplementationID() {
	return this.ImplementationID;

    }

    /**
     * Returns a org.mindswap.owls.process.Process   to use him to the execution of a service. 
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
     * Returns a ValueMap with the name of the field and the value that there gives us the client who calls to the service.
     * @param inmsg This message is sent by the client agent wants to make use of service, in the message content ara input values.
     * @return ValueMap 
     * @throws Exception
     */

    public ValueMap getServiceRequestValues(ACLMessage inmsg) throws Exception {

	// read msg content
	StringTokenizer Tok = new StringTokenizer(inmsg.getContent());

	// read in the service description
	String token_process = Tok.nextElement().toString();

	System.out.println("[Provider]Doc OWL-S: " + token_process);

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
