package fipaRecruitingTest;

import java.util.Iterator;
import java.util.Map;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_RECRUITING_Initiator;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;

public class PolicePatrol extends CAgent{
	
	ACLMessage messageToProxy = new ACLMessage(ACLMessage.REQUEST);

	public PolicePatrol(AgentID aid) throws Exception {
		super(aid);
	}

	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void execution(CProcessor firstProcessor,
			ACLMessage welcomeMessage) {
		class myFIPA_Recruiting_Initiator extends FIPA_RECRUITING_Initiator{

			@SuppressWarnings("unchecked")
			@Override
			protected void setProxyMessage(CProcessor myProcessor,
					ACLMessage messageToSend) {
				if(messageToProxy.getHeaderValue("target").equals("ambulance"))
					messageToProxy.setHeader("target", "backup");
				else
					messageToProxy.setHeader("target", "ambulance");
				//messageToSend.setSender(getAid());
				messageToSend.setReceiver(new AgentID("policeCentral"));
				messageToSend.setPerformative(messageToProxy.getPerformative());
				messageToSend.setContent(messageToProxy.getContent());
				Iterator it = messageToProxy.getHeaders().entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
			        messageToSend.setHeader((String)pairs.getKey(), (String)pairs.getValue());
			    }
			}
			
		}
				
		// In order to start a conversation the agent creates a message
		// that can be accepted by one of its initiator factories.
	
		ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);
		msg.setProtocol("fipa-recruiting");
		//msg.setReceiver(new AgentID("policeCentral"));
			
		// The agent creates the CFactory that creates processors that initiate
		// CONTRACT_NET protocol conversations. In this
		// example the CFactory gets the name "TALK", we don't add any
		// additional message acceptance criterion other than the required
		// by the CONTRACT_NET protocol (null) and we do not limit the number of simultaneous
		
		MessageFilter filter = new MessageFilter("protocol = fipa-recruiting");
		// processors (value 0)
		
		CFactory talk = new myFIPA_Recruiting_Initiator().newFactory("TALK", filter, msg, 1, this, 0);
	
		// The factory is setup to answer start conversation requests from the agent
		// using the CONTRACT_NET protocol.
	
		this.addFactoryAsInitiator(talk);
	
		// finally the new conversation starts. Because it is synchronous, 
		// the current interaction halts until the new conversation ends.
		int i = 1;
		while(i <= 10){
			System.out.println("Empezando petición proxy numero "+i);
			firstProcessor.createSyncConversation(msg);
			i++;
		}
		firstProcessor.ShutdownAgent();
	}

}
