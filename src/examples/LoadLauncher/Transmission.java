package LoadLauncher;

import java.util.Date;

import es.upv.dsic.gti_ia.core.AgentID;

public class Transmission{
	public static final int CONTROL = 0;
	public static final int SYSTEM = 1;
	public static final int COMMUNICATION = 2;
	public static final int UNKNOWN = -1;
	private AgentID sender;
	private AgentID receiver;
	private Date sendingDate;
	private Date receptionDate;
	private int type;
	private String content;
	
	public Transmission(){
		this.sender=null;
		this.receiver=null;
		this.sendingDate=null;
		this.receptionDate=null;
		this.type=UNKNOWN;
		this.content="";
	}
	
	public Transmission(AgentID sender, AgentID receiver,
			Date sendingDate, Date receptionDate, int type, String content){
		this.sender=sender;
		this.receiver=receiver;
		this.sendingDate=sendingDate;
		this.receptionDate=receptionDate;
		if ((type < 0) || (type > 2)){
			this.type=UNKNOWN;
		}
		else {
			this.type=type;
		}
		this.content=content;
	}
	
	public AgentID getSender(){
		return this.sender;
	}
	
	public AgentID getReceiver(){
		return this.receiver;
	}
	
	public Date getSendingDate(){
		return this.sendingDate;
	}
	
	public Date getReceptionDate(){
		return this.receptionDate;
	}
	
	public int getType(){
		return this.type;
	}
	
	public String getContent(){
		return this.content;
	}
	
	public String toString(){
		String outString;
		
		if (this.getReceptionDate() != null){
			outString = Long.toString(this.getReceptionDate().getTime());
		}
		else{
			outString = "---";
		}
		if (this.getSendingDate() != null){
			outString = outString + "\t" + Long.toString(this.getSendingDate().getTime());
		}
		else{
			outString = outString + "\t---";
		}
		if (this.getSender() != null){
			outString = outString + "\t" + this.getSender().toString();
		}
		else{
			outString = outString + "\t---";
		}
		if (this.getReceiver() != null){
			outString = outString + "\t" + this.getReceiver().toString();
		}
		else{
			outString = outString + "\t---";
		}
		outString = outString + "\t" + String.valueOf(this.type);
		if (this.getContent() != null){
			outString = outString + "\t" + this.getContent();
		}
		else{
			outString = outString + "\t---";
		}
		
		return outString;
	}
}
