package es.upv.dsic.gti_ia.organization;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

public class ReorganizingAgent extends BaseAgent{

	private boolean finalized = false;
	private Organization organization;
	public String RUTA = "/home/ricard/NetBeansProjects/reorganize";

	public ReorganizingAgent(AgentID aid) throws Exception {
		super(aid);
	}

	/**
	 * Waits new package, and redirects packets to another platform
	 */
	public void execute() {
		while (!finalized) {
			try {
				Thread.sleep(Long.MAX_VALUE);
			} 
			catch (Exception e) {
			}
		}

	}

	public void finalize() {
		this.finalized = true;
		System.out.println("Reorganizing Agent leaves the system");
	}
	
	private String getReorganizationResult(File file){
		String res="";
		try{
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(file);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			res += "<?xml version=\"1.0\"?>\n";
			res += "<reorganization>\n";
			String strLine;
			int linecont = 0;			
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				if(linecont == 3){
					res += "<agentassignment>"+strLine+"</agentassignment>\n";
					res += "<events>\n";
				}
				if(linecont >= 5){
					String eventNumber;
					String eventCost;
					String event;
					eventNumber = strLine.substring(0, strLine.indexOf(':'));
					int index = strLine.indexOf(':');
					if(strLine.charAt(index+1) != ' '){ //hi ha un cost
						eventCost = strLine.substring(index+1, strLine.indexOf(' ', index+1));
					}
					else{ //no hi ha cost
						eventCost = "0";
					}
					index = strLine.indexOf(' ', index+1);
					event = strLine.substring(index);
					res += "<event id=\""+eventNumber+"\" cost=\""+eventCost+"\">"+event+"</event>\n";
				}
				linecont++;
			}
			res += "</events>\n";
			res += "</reorganization>\n";
			// Close the input stream
			in.close();
		}
		catch(Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		return res;
	}

	public void onMessage(ACLMessage msg) {
		/**
		 * When a message arrives, its shows on screen
		 */
		logger.info("Mensaje received in " + this.getName()
				+ " agent, by onMessage: " + msg.getContent());
		try {
			this.organization = new Organization(msg.getContent());
			this.organization.print_organization();			
			Date date = new Date();
			String filename = "org"+date.getTime();
			this.organization.writeTxtFile(RUTA+"/"+filename+".txt");			
			Runtime r = Runtime.getRuntime();
			Process process = r.exec("sh "+RUTA+"/procediment.sh "+RUTA+" "+filename+" "+organization.roleIndex+" "+organization.agentIndex+" "+organization.serviceIndex+" "+organization.goalIndex);
			process.waitFor();
			
			//quan acaba tornem el xml amb la nova assignacio d'agents i la llista d'events
			ACLMessage response = new ACLMessage(ACLMessage.INFORM);			
			File file = new File(RUTA+"/"+filename+"events_resultants.txt");
			response.setContent(getReorganizationResult(file));
			response.setSender(getAid());
			response.setReceiver(msg.getSender());
			this.send(response);
		} 
		catch (Exception e) {
		}
	}	
}
