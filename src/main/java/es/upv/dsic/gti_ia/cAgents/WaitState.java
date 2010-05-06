package es.upv.dsic.gti_ia.cAgents;

/**
 * 
 * @author Ricard Lopez Fogues
 *
 */

public class WaitState extends State{
	public static int ONESHOT = 1;
	public static int PERIODIC = 2;
	public static int ABSOLUT = 3;
	
	//private WaitStateMethod methodToRun = null;
	long period;
	public int waitType;
	
	public WaitState(String n, long period) {
		super(n);
		type = State.WAIT;
		this.period = period;
		waitType = WaitState.ONESHOT;
	}
	
	public void setWaitType(int type) {
		this.waitType = type;
	}
	
	public long getPeriod(){
		return period;
	}
	
	public int getWaitType(){
		return waitType;
	}
}
