package es.upv.dsic.gti_ia.norms;


import java.util.ArrayList;

/**
 * This class represents an object norm formed by @id[deontic, target, action, activation, expiration].
 * @author root
 *
 */
public class Norm {

	




	


	String id;
	String deontic;
	String targetType;
	String targetValue;
	String actionName;
	ArrayList<String> actionParams = new ArrayList<String>();
	String action;
	String activation;
	String expiration;
	
	

	/**
	 * Creates a new norm.
	 * @param id @id
	 * @param deontic (f | o | p)
	 * @param targetType (roleName, agentName, positionName)
	 * @param targetValue (name, _)
	 * @param actionName (registerUnit, registerRole, ...)
	 * @param actionParams 
	 * @param action
	 * @param activation
	 * @param expiration
	 */
	public Norm(String id, String deontic, String targetType, String targetValue, ArrayList<String> action,  String activation, String expiration)
	{
	
			this.id = id;
			this.deontic = deontic;
			this.targetType = targetType;
			this.targetValue = targetValue;
			this.actionName = action.get(0);
			for(int i=2; i < action.size();i++)
			{
				this.actionParams.add(action.get(i));	
			}
			this.action = action.get(1);
			this.activation =activation;
			this.expiration = expiration;
			
			

	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getDeontic() {
		return deontic;
	}



	public void setDeontic(String deontic) {
		this.deontic = deontic;
	}



	public String getTargetType() {
		return targetType;
	}



	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}



	public String getTargetValue() {
		return targetValue;
	}



	public void setTargetValue(String targetValue) {
		this.targetValue = targetValue;
	}



	public String getActionName() {
		return actionName;
	}



	public void setActionName(String actionName) {
		this.actionName = actionName;
	}



	public ArrayList<String> getActionParams() {
		return actionParams;
	}



	public void setActionParams(ArrayList<String> actionParams) {
		this.actionParams = actionParams;
	}



	public String getAction() {
		return action;
	}



	public void setAction(String action) {
		this.action = action;
	}



	public String getActivation() {
		return activation;
	}



	public void setActivation(String activation) {
		this.activation = activation;
	}



	public String getExpiration() {
		return expiration;
	}



	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}



}
