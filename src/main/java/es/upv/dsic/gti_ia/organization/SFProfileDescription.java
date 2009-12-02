package es.upv.dsic.gti_ia.organization;

/**
 * This class is used to store the complete description of a profile to publish in the organization
 * 
 * @author Joan Bellver Faus 
 */

public class SFProfileDescription {
	private String serviceGoal;
	
	private String serviceID;
	
	private String serviceprofile;
	
	private String URLProfile;
	

	/**
	 * 
	 * @param URLProcess
	 *            The URL where the owl's document (related with service
	 *            process) is located.
	 * @param serviceGoal
	 * @param profileName
	 *            
	 */
	public SFProfileDescription(String URLProfile, String serviceGoal, String profileName) {
	    
	    	this.serviceGoal= serviceGoal;
		this.serviceID = "";
		this.URLProfile = URLProfile;
		this.serviceprofile = this.URLProfile+"#"+ profileName;

	}



	/**
	 * Returns the service profile
	 * 
	 * @return serviceprofile : URLProfile
	 *         #+profileName
	 */
	public String getServiceProfile() {
		return this.serviceprofile;

	}
	
	
	public void setServiceProfile(String profilename){
	    
	    this.serviceprofile = this.URLProfile+"#"+ profilename;
	}



	/**
	 * Change the URL where the owl's document (related with service profile) is
	 * located.
	 * 
	 * @param url
	 */
	public void setURLProfile(String url) {
		this.URLProfile = url;
	}

	/**
	 * Return the URL where the owl's document (related with service profile) is
	 * located.
	 * 
	 * @return
	 */
	public String getURLProfile() {
		return this.URLProfile;
	}

	/**
	 * Change ID of the SFAgentDescription
	 * 
	 * @param id
	 */
	public void setServiceID(String id) {
		this.serviceID = id;
	}

	/**
	 * Return an ID of the SFServiceDescription
	 * 
	 * @return
	 */
	public String getServiceID() {
		return this.serviceID;
	}





	/**
	 * Return Service Goal
	 * 
	 * @return
	 */
	public String getServiceGoal() {

		return this.serviceGoal;
	}

	/**
	 * Return Profile goal
	 * 
	 * @param goalProfile
	 */
	public void setServiceGoal(String goalProfile) {

		this.serviceGoal = goalProfile;
	}

}
