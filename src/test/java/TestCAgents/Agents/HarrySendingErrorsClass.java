package TestCAgents.Agents;

import java.util.concurrent.CountDownLatch;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;

public class HarrySendingErrorsClass extends CAgent {

	// Variables for testing
	public String informMsg;
	private CountDownLatch finished;
	public String acceptMsg;
	public String refuseMsg;
	private int mode;
	public String rejectMsg;
	public String receiveFailure;
	public String notUnderstood;
	public int received = 0;
	public boolean first = true;
	public int counter = 0;
	private boolean def = true;

	public HarrySendingErrorsClass(AgentID aid, CountDownLatch finished,
			boolean def) throws Exception {
		super(aid);
		this.finished = finished;
		this.def = def;
		informMsg = "";
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		ACLMessage msg;

		class mySendingErrorCustom extends SENDING_ERROR_PROTOCOL_Custom {

			protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
				myProcessor.getInternalData().put("InitialMessage", msg);
			}

			protected void doSolicitProposals(CProcessor myProcessor,
					ACLMessage messageToSend) {

				messageToSend.setProtocol("fipa-contract-net");
				messageToSend.setPerformative(ACLMessage.CFP);
				messageToSend.setSender(myProcessor.getMyAgent().getAid());

				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < 1000000; ++i) {

					sb.append(Math.random() * 1000);
				}

				messageToSend.setContent(sb.toString());
				messageToSend.setReceiver(new AgentID("Sally"));
				// messageToSend.setReceiver(null);

			}

			protected void doFinal(CProcessor myProcessor,
					ACLMessage messageToSend) {

				logger.info(myProcessor.getMyAgent().getName()
						+ " says Goodbye");

			}

		}

		class mySendingErrorDefault extends SENDING_ERROR_PROTOCOL_Default {

			protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
				myProcessor.getInternalData().put("InitialMessage", msg);
			}

			protected void doSolicitProposals(CProcessor myProcessor,
					ACLMessage messageToSend) {

				messageToSend.setProtocol("fipa-contract-net");
				messageToSend.setPerformative(ACLMessage.CFP);
				messageToSend.setSender(myProcessor.getMyAgent().getAid());

				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < 1000000; ++i) {

					sb.append(Math.random() * 1000);
				}

				messageToSend.setContent(sb.toString());
				messageToSend.setReceiver(new AgentID("Sally"));
				// messageToSend.setReceiver(null);

			}

			protected void doFinal(CProcessor myProcessor,
					ACLMessage messageToSend) {

				logger.info(myProcessor.getMyAgent().getName()
						+ " says Goodbye");

			}

		}

		msg = new ACLMessage(ACLMessage.CFP);
		msg.setProtocol("fipa-contract-net");

		MessageFilter filter = null;
		ACLMessage template = msg;
		int availableConversations = 1;

		CFactory mySendingError;

		if (def)
			mySendingError = new mySendingErrorCustom().newFactory(
					"mySendingError", filter, template, availableConversations,
					myProcessor.getMyAgent());
		else
			mySendingError = new mySendingErrorDefault().newFactory(
					"mySendingError", filter, template, availableConversations,
					myProcessor.getMyAgent());

		this.addFactoryAsInitiator(mySendingError);

		this.startSyncConversation("mySendingError");

		myProcessor.ShutdownAgent();
	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		finished.countDown();
	}

	public void setMode(int mode) {
		this.mode = mode;

	}

	public int getMode() {
		return this.mode;

	}
}
