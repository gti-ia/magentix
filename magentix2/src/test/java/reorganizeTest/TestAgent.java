package reorganizeTest;

import java.io.File;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import es.upv.dsic.gti_ia.organization.Organization;

public class TestAgent extends SingleAgent{
	
	private Organization org;

	public TestAgent(AgentID aid) throws Exception {
		super(aid);
	}
	
	public void execute(){
		File file = new File("/home/ricard/NetBeansProjects/reorganize/organization.xml");
		org = new Organization(file);
		
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent(org.getOrganizationXML());
		msg.setSender(getAid());
		msg.setReceiver(new AgentID("reorganizer"));
		this.send(msg);
		
		try {
			/**
			 * receiveACLMessage is a blocking function. its waiting a new
			 * ACLMessage
			 */
			msg = receiveACLMessage();
			System.out.println("Mensaje received in " + this.getName()
					+ " agent, by receiveACLMessage: " + msg.getContent());
			System.out.println(msg.getHeaderValue("Purpose"));
		} catch (Exception e) {
			logger.error(e.getMessage());
			System.out.println(e.getMessage());
			return;
		}
	}

}
