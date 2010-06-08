package timerTest;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

import es.upv.dsic.gti_ia.cAgents.ActionState;
import es.upv.dsic.gti_ia.cAgents.ActionStateMethod;
import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;

public class TimerAgent extends CAgent{

	public TimerAgent(AgentID aid) throws Exception {
		super(aid);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void Initialize(CProcessor firstProcessor,
			ACLMessage welcomeMessage) {
		

		// Creamos una f�brica para enviar una propuesta
		// y esperar respuesta

		MessageFilter filter = new MessageFilter("performative = PROPOSE");

		CProcessorFactory talk = new CProcessorFactory("TALK", filter, 1, this);

		// Un CProcessor siempre comienza en el estado predefinido BEGIN.
		// Debemos asociar un m�todo que se ejecutar� al transitar este estado.

		///////////////////////////////////////////////////////////////////////////////
		// BEGIN state

		BeginState BEGIN = (BeginState) talk.cProcessorTemplate().getState(
				"BEGIN");

		class BEGIN_Method implements BeginStateMethod {
			public String run(CProcessor myProcessor, ACLMessage msg) {
				// En este ejemplo no hay nada m�s que hacer que pasar al estado
				// WAIT que activará el timer
				return "WAIT";
			};
		}
		BEGIN.setMethod(new BEGIN_Method());

	
		///////////////////////////////////////////////////////////////////////////////
		// WAIT State
		
		WaitState timer = new WaitState("WAIT", 3000);
		timer.setWaitType(WaitState.PERIODIC);
		talk.cProcessorTemplate().registerState(timer);
		talk.cProcessorTemplate().addTransition(BEGIN, timer);

		///////////////////////////////////////////////////////////////////////////////
		// RECEIVE State

		ReceiveState RECEIVE = new ReceiveState("RECEIVE");

		class RECEIVE_Method implements ReceiveStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageReceived) {
				Date now = new Date();
				DateFormat df = DateFormat.getTimeInstance();
				System.out.println("Hora : "+df.format(now));
				return "ACTION";
			}
		}
		
		RECEIVE.setAcceptFilter(null); // null -> aceptar cualquier mensaje
		RECEIVE.setMethod(new RECEIVE_Method());
		talk.cProcessorTemplate().registerState(RECEIVE);
		talk.cProcessorTemplate().addTransition(timer, RECEIVE);

		class action_method implements ActionStateMethod{

			@Override
			public String run(CProcessor myProcessor) {
				System.out.println("Fem accions");
				Random rand = new Random();
				int x = rand.nextInt(100);
				System.out.println("Rand "+x);
				if(x > 50){
					try {
						System.out.println("Esperem 4 segons");
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{
					try {
						System.out.println("Esperem 0.5 segons");
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return "WAIT";
			}
			
		}
		ActionState action = new ActionState("ACTION");
		action.setMethod(new action_method());
		talk.cProcessorTemplate().registerState(action);
		talk.cProcessorTemplate().addTransition(RECEIVE, action);
		talk.cProcessorTemplate().addTransition(action, timer);
		
		
		
		// El procesador "molde" est� listo. Activamos la f�brica.

		this.addFactoryAsInitiator(talk);

		// Finalmente Harry inicia la conversaci�n.
		// Para ello debe crear un mensaje admisible por la f�brica, en este
		// caso la performativa debe ser PURPOSE

		ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
		
		ACLMessage response = firstProcessor.createSyncConversation(msg);		
	}
	
	@Override
	protected void Finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		// TODO Auto-generated method stub
		
	}	

}
