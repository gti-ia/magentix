package myFirstCProcessorFactories;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

import es.upv.dsic.gti_ia.cAgents.*;

class SallyClass extends CAgent {

	public SallyClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void Initialize(CProcessor myProcessor, ACLMessage welcomeMessage) {

		ACLMessage template;

		// Creamos una f�brica para tratar propuestas

		template = new ACLMessage(ACLMessage.PROPOSE);

		CProcessorFactory talk = new CProcessorFactory("TALK", template, 1,
				myProcessor.getMyAgent());

		// Un CProcessor siempre comienza en el estado predefinido BEGIN.
		// Debemos asociar un m�todo que se ejecutar� al transitar este estado.

		///////////////////////////////////////////////////////////////////////////////
		// BEGIN state

		BeginState BEGIN = (BeginState) talk.cProcessorTemplate().getState(
				"BEGIN");

		class BEGIN_Method implements BeginStateMethod {
			public String run(CProcessor myProcessor, ACLMessage msg) {
				// En este ejemplo no hay nada m�s que hacer que pasar al estado
				// REFUSE que enviar� la respuesta
				return "REFUSE";
			};
		}
		BEGIN.setMethod(new BEGIN_Method());

		///////////////////////////////////////////////////////////////////////////////
		// REFUSE state

		SendState REFUSE = new SendState("REFUSE");

		class REFUSE_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {

				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
				messageToSend.setContent("Maybe someday");
				return "FINAL";
			}
		}
		REFUSE.setMethod(new REFUSE_Method());
		
		template = new ACLMessage(ACLMessage.REFUSE);
		REFUSE.setMessageTemplate(template);

		talk.cProcessorTemplate().registerState(REFUSE);
		talk.cProcessorTemplate().addTransition("BEGIN", "REFUSE");

		///////////////////////////////////////////////////////////////////////////////
		// FINAL state

		FinalState FINAL = new FinalState("FINAL");

		class FINAL_Method implements FinalStateMethod {
			public void run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend.setContent("Done");
			}
		}
		FINAL.setMethod(new FINAL_Method());

		talk.cProcessorTemplate().registerState(FINAL);
		talk.cProcessorTemplate().addTransition("REFUSE", "FINAL");

		// El procesador "molde" est� listo. Activamos la f�brica
		// como participante. Todo mensaje que llege al agente
		// con la performativa PURPOSE har� que la f�brica TALK
		// cree un procesador para atender la conversaci�n

		this.addFactoryAsParticipant(talk);

	}

	protected void Finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {

		System.out.println(finalizeMessage.getContent());
	}
}