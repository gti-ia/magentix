package es.upv.dsic.gti_ia.cAgents;

/**
 * 
 * @author Ricard Lopez Fogues
 *
 */

public abstract class State implements Cloneable{
	public final static int ACTION = 0;
	public final static int BEGIN = 1;
	public final static int FINAL = 2;
	public final static int RECEIVE = 3;
	public final static int SEND = 4;
	public final static int SENDT = 99;
	public final static int WAIT = 5;
	protected final static int SENDING_ERRORS = 6;
	protected final static int CANCEL = 7;
	protected final static int TERMINATED_FATHER = 8;
	protected final static int NOT_ACCEPTED_MESSAGES = 9;
	
	protected int type;
	
	private String name;
	
	public State(String n){
		name = n;
	}
	
	public String getName(){
		return name;
	}
	
	public int getType(){
		return type;
	}
	
	protected void setType(int t){
		type = t;
	}
	
	protected void setName(String name){
		this.name = name;
	}
	
	protected Object clone(){
		Object obj = null;
		try {
			obj = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();		
		}
		return obj;
	}

}
