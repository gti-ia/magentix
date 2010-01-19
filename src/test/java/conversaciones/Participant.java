package conversaciones;

import es.upv.dsic.gti_ia.cAgents.ActionState;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.RequestResponderFactory;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

public class Participant extends CAgent{

	public Participant(AgentID aid) throws Exception {
		super(aid);
	}

	@Override
	protected void setFactories() {
		ACLMessage template = new ACLMessage(ACLMessage.REQUEST);
		
		//R
		ReceiveState1 R = new ReceiveState1("R");
		ACLMessage receiveFilter = new ACLMessage(ACLMessage.REQUEST);
		R.setAcceptFilter(receiveFilter);
				
		RequestResponderFactory factory = new RequestResponderFactory("Participant", template, 10, R, new ActionState1("A"));
				
		//attach factory to agent
		this.addFactory(factory);
		
	}
	
	class ReceiveState1 extends ReceiveState{

		public ReceiveState1(String n) {
			super(n);
		}

		@Override
		protected String run(CProcessor myProcessor, ACLMessage msg) {
			String next = "S3";
			return next;
		}
	}
		
	class ActionState1 extends ActionState{

		public ActionState1(String n) {
			super(n);
		}

		@Override
		protected String run(CProcessor myProcessor) {
			System.out.println("Performing an action...");		
			
			String next = "S5";
			return next;
		}
	}

}
