package Thomas_example;



import java.util.HashMap;
import java.util.Map.Entry;

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

public class Product extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	SFProxy sfProxy = new SFProxy(this);
	

	public Product(AgentID aid) throws Exception {
		super(aid);

	}


	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {



		try
		{

			String result = omsProxy.acquireRole("operation", "calculator");
			logger.info("["+this.getName()+"] Result acquire role participant: "+result);


			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");


			CFactory additionTalk = new myFIPA_REQUEST().newFactory("PRODUCT_TALK", null,
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
			
			double result=1;
			try{




				for (Entry<String, String> e : inputs.entrySet()) {

					result *= Double.parseDouble(e.getValue()); 
				}

				next = "INFORM";
				String resultXML ="";
				resultXML += "<serviceOutput>\n";
				resultXML += "<serviceName>"+serviceName+"</serviceName>\n";
				resultXML += "<outputs>\n";
				resultXML += "<Result>"+result+"</Result>\n";
				resultXML += "</outputs>\n";
				resultXML += "</serviceOutput>\n";
				
				myProcessor.getLastReceivedMessage().setContent(resultXML);

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

					if (serviceName.toLowerCase().contains("product"))
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

			logger.info("[Product]Sending First message:" + next);

			return next;
		}
	}













}


