package es.upv.dsic.gti_ia.jason;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

/**
 * @author Ricard Lopez Fogues
 */

final public class JasonAgent extends BaseAgent {

	private MagentixAgArch agArch;
	
	public JasonAgent(AgentID aid, String filename, MagentixAgArch arch) throws Exception {
		super(aid);
		this.agArch = arch;
		this.agArch.init(filename, this);
	}
	
	public void run() {
		agArch.run();
	}
	
	protected void onMessage(ACLMessage msg) {
		agArch.addMessage(msg);
	}
	
	public void stopAg(){
		agArch.stopAg();
	}
	
	public MagentixAgArch getAgArch(){
		return agArch;
	}
}
