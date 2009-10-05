package es.upv.dsic.gti_ia.magentix2;

public class SFAgentDescription {

	private String goalprofile;
	private String ID;
	private String ImplementationID;
	private String newgoalprofile;
	private String URLProfile;
	private String URLProcess;

	public SFAgentDescription(String URLProcess, String URLProfile) {
		this.goalprofile = "";
		this.ID = "";
		this.ImplementationID = "";
		this.URLProcess=URLProcess;
		this.URLProfile =URLProfile;
		
		
	}

	
	public void setURLProcess(String url)
	{
		this.URLProcess = url;
	}
	
	public String getURIProcess()
	{
		return this.URLProcess;
	}
	
	public void setURLProfile(String url)
	{
		this.URLProfile = url;
	}
	
	public String getURIProfile()
	{
		return this.URLProfile;
	}
	
	public void setID(String id) {
		this.ID = id;
	}

	public String getID() {
		return this.ID;
	}

	public void setImplementationID(String im) {
		this.ImplementationID = im;
	}

	public String getImplementationID() {
		return this.ImplementationID;

	}

	public String getServiceGoal() {
		return this.goalprofile;
	}

	public void setServiceGoal(String goalProfile) {
		this.goalprofile = goalProfile;
	}


}
