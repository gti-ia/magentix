package es.upv.dsic.gti_ia.cAgents;

public class WaitState extends State{
	private long timeOut;
	
	public WaitState(String n, int timeOut) {
		super(n);
		type = State.WAIT;
		this.timeOut = timeOut;
	}
	
	public void setTimeOut(long time){
		timeOut = time;
	}
	
	public long getTimeOut(){
		return timeOut;
	}
}