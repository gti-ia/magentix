package es.upv.dsic.gti_ia.magentix2;

public class SFAgentDescription {

	private String goalprofile;
	private String urlprofile;
	private String newgoalprofile;
	
	
	public  SFAgentDescription()
	{
		this.goalprofile="";
		this.urlprofile="";
		this.newgoalprofile="";
	}
	
  public String getServiceGoal()
	{
		return this.goalprofile;
	}
	
  
  public void setServiceGoal(String goalProfile)
  {
	  this.goalprofile = goalProfile; 
  }
  
  public String getNewServiceGoal()
  {
	return this.newgoalprofile;  
  }
  
  public void setNewServiceGoal(String newgoalProfile)
  {
	  this.newgoalprofile = newgoalProfile;
  }
  
	

    
		
}
