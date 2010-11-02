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
	
	/**
	 * Creates a new wait state
	 * @param name of the state
	 * @param period timeout
	 */
	public WaitState(String n, long period) {
		super(n);
		type = State.WAIT;
		this.period = period;
		waitType = WaitState.ONESHOT;
	}
	
	/**
	 * Sets type of the wait state
	 * @param type
	 */
	public void setWaitType(int type) {
		this.waitType = type;
	}
	
	/**
	 * @return the timeout period
	 */
	public long getPeriod(){
		return period;
	}
	
	/**
	 * 
	 * @return type of the wait state
	 */
	public int getWaitType(){
		return waitType;
	}
}
