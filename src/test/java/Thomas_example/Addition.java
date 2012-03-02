package Thomas_example;
/**
 * In this class the agent addition is represented. 
 * Functions:
 *  -	Acquire role operation inside the unit calculator.
 * 	-	Registers a new service. The function of this service is to add the values of entry and to return the result.
 *  - 	Provide the execution of the service addition.
 *  - 	Implements a new FIPA REQUEST protocol in order to accept the client requests and execute the service.
 *  
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.ServiceTools;
import es.upv.dsic.gti_ia.organization.THOMASException;

public class Addition extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	SFProxy sfProxy = new SFProxy(this);
	
	

	public Addition(AgentID aid) throws Exception {
		super(aid);

	}


	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		try
		{

			String result = omsProxy.acquireRole("operation", "calculator");
			logger.info("["+this.getName()+"] Result acquire role participant: "+result);

			ArrayList<String> resultRegister=sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			Iterator<String> iterRes=resultRegister.iterator();
			String registerRes="";
			while(iterRes.hasNext()){
				registerRes+=iterRes.next()+"\n";
			}
			logger.info("["+this.getName()+"] Result registerService: "+registerRes);

			CFactory additionTalk = new myFIPA_REQUEST().newFactory("ADDITION_TALK", null,
					0, myProcessor.getMyAgent());


			this.addFactoryAsParticipant(additionTalk);
			

		}catch(THOMASException e)
		{
			e.printStackTrace();
		}

	}


	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		System.out.println("["+firstProcessor.getMyAgent().getName()+"] end execution!");	
		
//		try {
//			String resultDeregister = sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
//			logger.info("["+this.getName()+"] Result deregisterService: "+resultDeregister);
//			String resultLeaveRole =omsProxy.leaveRole("operation", "calculator");
//			logger.info("["+this.getName()+"] Result leave role operation: "+resultLeaveRole);
//		} catch (THOMASException e) {
//			
//			e.printStackTrace();
//		}
	}


	//------------------------------------------------------------------------
	//-----------------------CFactory implementation--------------------------
	//------------------------------------------------------------------------



	class myFIPA_REQUEST extends FIPA_REQUEST_Participant {

		ServiceTools st=new ServiceTools();
		HashMap<String,String> inputs=new HashMap<String, String>();
		String serviceName="";


		@Override
		protected String doAction(CProcessor myProcessor) {
			String next = "";
			Double resultContent = 0.0;
			try{

				String serviceWSDLURL= "http://localhost:8080/testSFservices/services/Addition?wsdl";
				HashMap<String,Object> result=st.executeWebService(serviceWSDLURL, inputs);

				next = "INFORM";

				resultContent=(Double)result.get("Result");
				
				String resultXML ="";
				resultXML += "<serviceOutput>\n";
				resultXML += "<serviceName>"+serviceName+"</serviceName>\n";
				resultXML += "<outputs>\n";
				resultXML += "<Result>"+resultContent+"</Result>\n";
				resultXML += "</outputs>\n";
				resultXML += "</serviceOutput>\n";
				
				myProcessor.getLastReceivedMessage().setContent(""+resultXML);

			} catch (Exception e) {
				next = "FAILURE";
			}

			return next;
		}

		@Override
		protected void doInform(CProcessor myProcessor, ACLMessage response) {
			ACLMessage lastReceivedMessage = myProcessor.getLastReceivedMessage();
			response.setContent(lastReceivedMessage.getContent());				
		}

		@Override
		protected String doReceiveRequest(CProcessor myProcessor,
				ACLMessage request) {
			String next = "";
			ACLMessage msg = request;

			if (msg != null) {

				try {

					inputs.clear();
					serviceName = st.extractServiceContent(msg.getContent(), inputs);

					if (serviceName.toLowerCase().contains("addition"))
					{

						logger.info("AGREE");
						next = "AGREE";

					} else {

						logger.info("REFUSE");
						next = "REFUSE";
					}

				} catch (Exception e) {

					logger.info("EXCEPTION");
					System.out.println(e);
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());

				}

			} else {

				logger.info("NOTUNDERSTOOD");
				next = "NOT_UNDERSTOOD";
			}

			logger.info("[Addition]Sending First message:" + next);

			return next;
		}
	}
}


