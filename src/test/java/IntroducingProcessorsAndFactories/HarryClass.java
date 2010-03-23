package IntroducingProcessorsAndFactories;

import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;


class HarryClass extends CAgent {

	public HarryClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void Initialize(CProcessor myProcessor, ACLMessage welcomeMessage) {

		ACLMessage template;


		// Creamos una fábrica para enviar una propuesta
		// y esperar respuesta

		template = new ACLMessage(ACLMessage.PROPOSE);

		CProcessorFactory talk = new CProcessorFactory("TALK", template, 1,
				myProcessor.getMyAgent());

		// Un CProcessor siempre comienza en el estado predefinido BEGIN.
		// Debemos asociar un método que se ejecutará al transitar este estado.

		///////////////////////////////////////////////////////////////////////////////
		// BEGIN state

		BeginState BEGIN = (BeginState) talk.cProcessorTemplate().getState(
				"BEGIN");

		class BEGIN_Method implements BeginStateMethod {
			public String run(CProcessor myProcessor, ACLMessage msg) {
				// En este ejemplo no hay nada más que hacer que pasar al estado
				// PURPOSE que enviará el mensaje
				return "PURPOSE";
			};
		}
		BEGIN.setMethod(new BEGIN_Method());

		///////////////////////////////////////////////////////////////////////////////
		// PURPOSE state

		SendState PURPOSE = new SendState("PURPOSE");

		class PURPOSE_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {

				messageToSend.copyFromAsTemplate(myProcessor
						.getLastReceivedMessage());
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				System.out.println(myProcessor.getMyAgent().getName() + " : I tell " + messageToSend.getReceiver().name + " "
						+ messageToSend.getPerformative() + " " + messageToSend.getContent());

				return "WAIT";
			}
		}
		PURPOSE.setMethod(new PURPOSE_Method());

		talk.cProcessorTemplate().registerState(PURPOSE);
		talk.cProcessorTemplate().addTransition("BEGIN", "PURPOSE");

		///////////////////////////////////////////////////////////////////////////////
		// WAIT State

		talk.cProcessorTemplate().registerState(new WaitState("WAIT", 0));
		talk.cProcessorTemplate().addTransition("PURPOSE", "WAIT");

		///////////////////////////////////////////////////////////////////////////////
		// RECEIVE State

		ReceiveState RECEIVE = new ReceiveState("RECEIVE");

		class RECEIVE_Method implements ReceiveStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageReceived) {
				return "FINAL";
			}
		}
		
		RECEIVE.setAcceptFilter(null); // null -> aceptar cualquier mensaje
		RECEIVE.setMethod(new RECEIVE_Method());
		talk.cProcessorTemplate().registerState(RECEIVE);
		talk.cProcessorTemplate().addTransition("WAIT", "RECEIVE");

		///////////////////////////////////////////////////////////////////////////////
		// FINAL state

		FinalState FINAL = new FinalState("FINAL");

		class FINAL_Method implements FinalStateMethod {
			public void run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend.copyFromAsTemplate(myProcessor
						.getLastReceivedMessage());
			}
		}
		FINAL.setMethod(new FINAL_Method());

		talk.cProcessorTemplate().registerState(FINAL);
		talk.cProcessorTemplate().addTransition("PURPOSE", "FINAL");

		///////////////////////////////////////////////////////////////////////////////
		
		
		// El procesador "molde" está listo. Activamos la fábrica.

		this.addFactoryAsInitiator(talk);

		// Finalmente Harry inicia la conversación.
		// Para ello debe crear un mensaje admisible por la fábrica, en este
		// caso la performativa debe ser PURPOSE

		ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
		msg.setReceiver(new AgentID("Sally"));
		msg.setContent("Will you come with me to a movie?");
		ACLMessage response = myProcessor.createSyncConversation(msg);

		System.out.println(myProcessor.getMyAgent().getName() + " : Sally tell me "
				+ response.getPerformative() + " " + response.getContent());

		// myProcessor.ShutdownAgent();
	}

	protected void Finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {

		System.out.println(finalizeMessage.getContent());
	}
}