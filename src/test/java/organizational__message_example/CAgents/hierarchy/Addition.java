package organizational__message_example.CAgents.hierarchy;



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

public class Addition extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	int result=0;
	int received=0;

	public Addition(AgentID aid) throws Exception {
		super(aid);

	}


	protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {



		try
		{

			
			ArrayList<ArrayList<String>> roles;

			boolean exists = false;

			String result = omsProxy.acquireRole("participant", "virtual");
			logger.info("["+this.getName()+"] Result acquire role participant: "+result);

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
			CFactory additionTalk = new CFactory("ADDITION_TALK", null, 10,this);


			//----------------------------BEGIN STATE----------------------------------
			BeginState BEGIN = (BeginState) additionTalk.cProcessorTemplate().getState("BEGIN");
			BEGIN.setMethod(new BEGIN_Method());

			//----------------------------WAIT STATE----------------------------------
			WaitState WAIT = new WaitState("WAIT",0);
			additionTalk.cProcessorTemplate().registerState(WAIT);
			additionTalk.cProcessorTemplate().addTransition(BEGIN, WAIT);


			//----------------------------NOT ACCEPTED STATE----------------------------------
			additionTalk.cProcessorTemplate().registerState(new not_accepted());

			//----------------------------RECEIVE STATE----------------------------------
			ReceiveState RECEIVE = new ReceiveState("RECEIVE");
			RECEIVE.setAcceptFilter(filter); // null -> accept any message
			RECEIVE.setMethod(new RECEIVE_Method());

			additionTalk.cProcessorTemplate().registerState(RECEIVE);
			additionTalk.cProcessorTemplate().addTransition(WAIT, RECEIVE);


			//----------------------------SEND RESULT STATE----------------------------------
			SendState SEND_RESULT = new SendState("SEND_RESULT");
			SEND_RESULT.setMethod(new RESPONSE_Method());
			additionTalk.cProcessorTemplate().registerState(SEND_RESULT);
			additionTalk.cProcessorTemplate().addTransition(RECEIVE, SEND_RESULT);
			additionTalk.cProcessorTemplate().addTransition(SEND_RESULT, WAIT);

			//----------------------------FINAL STATE----------------------------------
			FinalState FINAL = new FinalState("FINAL");
			additionTalk.cProcessorTemplate().addTransition(SEND_RESULT, FINAL);
			FINAL.setMethod(new FINAL_Method());

			
			additionTalk.cProcessorTemplate().registerState(FINAL);
			this.addFactoryAsParticipant(additionTalk);





		}catch(THOMASException e)
		{
			e.printStackTrace();
		}

	}

	synchronized public boolean exit()
	{
		received++;
		if (received > 1)
			return true;
		else
			return false;
	}

	public void setResult(int r)
	{
		result = r;
	}

	public int getResult()
	{
		return result;
	}
	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		System.out.println("["+firstProcessor.getMyAgent().getName()+"] end execution!");
	}


	//------------------------------------------------------------------------
	//-----------------------CFactory implementation--------------------------
	//------------------------------------------------------------------------



	class not_accepted extends NotAcceptedMessagesState
	{

		String content;
		@Override
		protected int run(ACLMessage exceptionMessage, String next) {

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

			return "WAIT";
		};

	}



	class RECEIVE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String state = "SEND_RESULT";
			int p1,p2, result = 0;


			p1 = Integer.parseInt(messageReceived.getContent().split(" ")[0]);
			p2 = Integer.parseInt(messageReceived.getContent().split(" ")[1]);

			result = p1 + p2;

			((Addition)myProcessor.getMyAgent()).setResult(result);

			return state;
		}

	}

	class RESPONSE_Method implements SendStateMethod {

		int n=0;

		public String run(CProcessor myProcessor, ACLMessage messageToSend) {


			String state = "FINAL";
			int result = ((Addition)myProcessor.getMyAgent()).getResult();
			

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

			if (((Addition)myProcessor.getMyAgent()).exit())
			{
				OMSProxy omsProxy = new OMSProxy(myProcessor);
			
				try {
					omsProxy.leaveRole("operador", "calculin");
					omsProxy.leaveRole("participant", "virtual");
				} catch (THOMASException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				myProcessor.ShutdownAgent();
			}

		}

	}

}


