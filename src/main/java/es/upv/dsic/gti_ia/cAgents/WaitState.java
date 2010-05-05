package es.upv.dsic.gti_ia.cAgents;

import java.util.Date;

/**
 * 
 * @author Ricard Lopez Fogues
 *
 */

public class WaitState extends State{
	private WaitStateMethod methodToRun = null;
	long timeout;
	
	public WaitState(String n, long timeout) {
		super(n);
		type = State.WAIT;
		this.timeout = timeout;
	}
	
	public void setMethod(WaitStateMethod method) {
		methodToRun = method;
	}
	
	public Date getTimeOut(){
		if(methodToRun != null)
			return methodToRun.run(timeout);
		else return null;
	}
}
