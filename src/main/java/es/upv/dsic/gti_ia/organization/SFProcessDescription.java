package es.upv.dsic.gti_ia.organization;


/**
 * This class is used to store the complete description of a process to publish in the organization
 * 
 * @author Joan Bellver Faus 
 */

public class SFProcessDescription {
    
	private String ImplementationID;
	private String servicemodel;
	private String URLProcess;
	private String ServiceID; //id profile

	/**
	 * 
	 * @param URLProcess
	 *            The URL where the owl's document (related with service
	 *            process) is located.
	 * @param proccessName
	 *            
	 */
	public SFProcessDescription(String URLProcess, String processName) {
	
	    	this.ServiceID = "";
		this.ImplementationID = "";
		this.URLProcess = URLProcess;
		this.servicemodel = this.URLProcess+"#"+ processName;
		
	

	}

	/**
	 * Returns the service model
	 * 
	 * @return serviceModel : URLProcess + goalProfile+Process.owl#+goalProfile;
	 */
	public String getServiceModel() {
		return this.servicemodel;
	}


	public void setServiceModel(String processName){
	    
	    this.servicemodel = this.URLProcess+"#"+ processName;
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
	 * Change ID of the SFAgentDescription
	 * 
	 * @param id
	 */
	public void setProfileID(String id) {
		this.ServiceID = id;
	}

	/**
	 * Return an ID of the SFServiceDescription
	 * 
	 * @return
	 */
	public String getProfileID() {
		return this.ServiceID;
	}

	/**
	 * Add the implementationID
	 * 
	 * @param im
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

}
