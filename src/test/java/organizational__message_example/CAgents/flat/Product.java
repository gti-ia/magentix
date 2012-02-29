package organizational__message_example.CAgents.flat;

import java.util.ArrayList;

import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.NotAcceptedMessagesState;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class Product extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	int result=0;


	public Product(AgentID aid) throws Exception {
		super(aid);

	}

	protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {


		try
		{


			omsProxy.acquireRole("participant", "virtual");

			ArrayList<ArrayList<String>> roles;
			boolean exists = false;

			do
			{
				roles = omsProxy.informUnitRoles("calculin");
				for(ArrayList<String> role : roles)
				{
					if (role.get(0).equals("operador"))
						exists = true;
				}
			}while(!exists);



			omsProxy.acquireRole("operador", "calculin");

			//------------------------------------------------------------------------
			//-----------------------CFactory definition------------------------------
			//------------------------------------------------------------------------
			MessageFilter filter = new MessageFilter("performative = REQUEST");
			CFactory additionTalk = new CFactory("PRODUCT_TALK", null, 1,this);

			//----------------------------BEGIN STATE----------------------------------
			BeginState BEGIN = (BeginState) additionTalk.cProcessorTemplate().getState("BEGIN");
			BEGIN.setMethod(new BEGIN_Method());




			//----------------------------BEGIN STATE----------------------------------
			WaitState WAIT = new WaitState("WAIT", 0);
			additionTalk.cProcessorTemplate().addTransition(BEGIN, WAIT);
			additionTalk.cProcessorTemplate().registerState(WAIT);

			//----------------------------RECEIVE STATE----------------------------------
			ReceiveState RECEIVE = new ReceiveState("RECEIVE");
			RECEIVE.setAcceptFilter(filter); // null -> accept any message
			RECEIVE.setMethod(new RECEIVE_Method());
			additionTalk.cProcessorTemplate().registerState(RECEIVE);
			additionTalk.cProcessorTemplate().addTransition(WAIT, RECEIVE);

			//----------------------------RECEIVE SHUTDOWN STATE----------------------------------
			ReceiveState RECEIVE_SHUTDOWN = new ReceiveState("RECEIVE_SHUTDOWN");
			RECEIVE_SHUTDOWN.setAcceptFilter(new MessageFilter("shutdown = true")); // null -> accept any message
			RECEIVE_SHUTDOWN.setMethod(new RECEIVE_Shutdown_Method());
			additionTalk.cProcessorTemplate().registerState(RECEIVE_SHUTDOWN);
			additionTalk.cProcessorTemplate().addTransition(WAIT, RECEIVE_SHUTDOWN);

			//----------------------------RECEIVE OTHERS STATE----------------------------------
			ReceiveState RECEIVE_OTHERS= new ReceiveState("RECEIVE_OTHERS");
			RECEIVE_OTHERS.setAcceptFilter(new MessageFilter("performative = INFORM")); // null -> accept any message
			RECEIVE_OTHERS.setMethod(new RECEIVE_OTHERS_Method());
			additionTalk.cProcessorTemplate().registerState(RECEIVE_OTHERS);
			additionTalk.cProcessorTemplate().addTransition(WAIT, RECEIVE_OTHERS);

			//----------------------------SEND RESULT STATE----------------------------------
			SendState SEND_RESULT = new SendState("SEND_RESULT");
			SEND_RESULT.setMethod(new RESPONSE_Method());
			additionTalk.cProcessorTemplate().registerState(SEND_RESULT);
			additionTalk.cProcessorTemplate().addTransition(RECEIVE, SEND_RESULT);

			//----------------------------FINAL STATE----------------------------------
			FinalState FINAL = new FinalState("FINAL");
			FINAL.setMethod(new FINAL_Method());
			additionTalk.cProcessorTemplate().registerState(FINAL);
			additionTalk.cProcessorTemplate().addTransition(RECEIVE, FINAL);
			additionTalk.cProcessorTemplate().addTransition(SEND_RESULT, FINAL);
			additionTalk.cProcessorTemplate().addTransition(RECEIVE_OTHERS, FINAL);
			
			//----------------------------FINAL SHUTDOWN STATE----------------------------------
			FinalState FINAL_SHUTDOWN = new FinalState("FINAL_SHUTDOWN");
			FINAL_SHUTDOWN.setMethod(new FINAL_SHUTDOWN_Method());
			additionTalk.cProcessorTemplate().registerState(FINAL_SHUTDOWN);
			additionTalk.cProcessorTemplate().addTransition(RECEIVE_SHUTDOWN, FINAL_SHUTDOWN);
			
			//---------------------------NOT RECEIVE STATE----------------------------------
			additionTalk.cProcessorTemplate().registerState(new not_accepted());


			this.addFactoryAsParticipant(additionTalk);




		}catch(THOMASException e)
		{
			e.printStackTrace();
		}

	}


	public void setResult(int r)
	{
		result = r;
	}

	public int getResult()
	{
		return result;
	}

	//------------------------------------------------------------------------
	//-----------------------CFactory implementation--------------------------
	//------------------------------------------------------------------------


	class not_accepted extends NotAcceptedMessagesState
	{

		String content;
		@Override
		protected int run(ACLMessage exceptionMessage, String next) {

			content = exceptionMessage.getContent();

			return NotAcceptedMessagesState.IGNORE;
		}

		@Override
		protected String getNext(String previousState) {
			// TODO Auto-generated method stub
			String state = "WAIT";

			return state;
		}

	}

	class BEGIN_Method implements BeginStateMethod {

		public String run(CProcessor myProcessor, ACLMessage msg) {

			// In this example there is nothing more to do than continue
			// to the next state which will send the message.

			return "WAIT";
		};

	}

	class RECEIVE_Shutdown_Method implements ReceiveStateMethod
	{

		@Override
		public String run(CProcessor myProcessor, ACLMessage receivedMessage) {



			String state = "FINAL_SHUTDOWN";

			return state;
		}

	}

	class RECEIVE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String state = "SEND_RESULT";



			int p1,p2, result = 0;



			p1 = Integer.parseInt(messageReceived.getContent().split(" ")[0]);
			p2 = Integer.parseInt(messageReceived.getContent().split(" ")[1]);

			result = p1 * p2;

			((Product)myProcessor.getMyAgent()).setResult(result);


			return state;
		}

	}


	class RECEIVE_OTHERS_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String state = "FINAL";



			return state;
		}

	}

	class RESPONSE_Method implements SendStateMethod {


		public String run(CProcessor myProcessor, ACLMessage messageToSend) {


			String state = "FINAL";


			int result = ((Product)myProcessor.getMyAgent()).getResult();

			ACLMessage msgReply = myProcessor.getLastReceivedMessage().createReply();

			messageToSend.copyFromAsTemplate(msgReply);

			messageToSend.setContent(""+result);

			messageToSend.setPerformative(ACLMessage.INFORM);

			messageToSend.setSender(myProcessor.getMyAgent().getAid());
			messageToSend.setHeader("result", "true");



			return state;

		}

	}

	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage responseMessage) {




		}

	}

	class FINAL_SHUTDOWN_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage responseMessage) {



			try {

				
				
				//In order to agent creator deallocate the role of the agent product
				try {
					Thread.sleep(10*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				omsProxy.leaveRole("participant", "virtual");
			} catch (THOMASException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			myProcessor.ShutdownAgent();


		}

	}

	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		System.out.println("[ "+this.getName()+" ] end execution!");



	}


}


