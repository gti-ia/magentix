package es.upv.dsic.gti_ia.organization;

/**
 * This class is used to stored the complete description of a profile to publish in the organization
 * 
 * @author Joan Bellver Faus 
 */

public class ProfileDescription {
	
    
	private String serviceID;
	
	private String serviceprofile;
	
	private String URLProfile;
	

	/**
	 * 
	 * @param URLProfile
	 *            The URL where the profile owl document is located
	 * 		
	 * @param profileName
	 *            Name to the service profile description document.
	 */
	public ProfileDescription(String URLProfile,String profileName) {
	    
	    	
		this.serviceID = "";
		this.URLProfile = URLProfile;
		this.serviceprofile = this.URLProfile+"#"+ profileName;

	}



	/**
	 * This method returns the URL which makes reference to the service profile description document
	 * 
	 * @return serviceprofile 
	 *         
	 */
	public String getServiceProfile() {
		return this.serviceprofile;

	}
	
	
	/**
	 * This method changes a URL which makes reference to the service profile description document 
	 * @param profilename 
	 */
	public void setServiceProfile(String profilename){
	    
	    this.serviceprofile = this.URLProfile+"#"+ profilename;
	}



	/**
	 * This method changes the URL where the owl's document (related with service profile) is
	 * located.
	 * 
	 * @param url
	 */
	public void setURLProfile(String url) {
		this.URLProfile = url;
	}

	/**
	 * This method returns the URL where the owl's document (related with service profile) is
	 * located.
	 * 
	 * @return URLProfile
	 */
	public String getURLProfile() {
		return this.URLProfile;
	}

	/**
	 * This method changes ID of the service
	 * 
	 * @param id is generated automatically by the database. This parameter is automatically assigned when the method  registerProfile is called.
	 */
	public void setServiceID(String id) {
		this.serviceID = id;
	}

	/**
	 * This method returns an ID of the service
	 * 
	 * @return serviceID
	 */
	public String getServiceID() {
		return this.serviceID;
	}
}
