package TestContractNet;

import es.upv.dsic.gti_ia.architecture.FIPAContractNetInitiator;
import es.upv.dsic.gti_ia.architecture.FIPANames;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

import java.util.Date;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Client extends QueueAgent {

	private int precionMaximo;
	private int numeroDeOfertas = 0;

	public String state = null;
	public String status = null;
	private CountDownLatch finished;

	public Client(AgentID aid, CountDownLatch finished) throws Exception {

		super(aid);
		this.finished = finished;
	}

	protected void execute() {
		this.precionMaximo = 20000000;

		// Create the message CFP (Call For Proposal) by completing its
		// parameters
		ACLMessage mensajeCFP = new ACLMessage(ACLMessage.CFP);

		for (int i = 0; i < 5; i++) {
			// Send message to twenty agents that offer the service
			mensajeCFP.addReceiver(new AgentID("Autos" + i));
		}

		// Protocol that we will use
		mensajeCFP.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
		mensajeCFP.setContent("I look for car, do you propose to prices?");

		// Indicates how long we will wait for offers
		mensajeCFP
				.setReplyByDate(new Date(System.currentTimeMillis() + 1500000));

		// Behavior is added to handle the bids

		this.addTask(new ManejoOpciones(this, mensajeCFP));

		Monitor adv = new Monitor();
		adv.waiting();

	}

	private class ManejoOpciones extends FIPAContractNetInitiator {

		public ManejoOpciones(QueueAgent agente, ACLMessage plantilla) {
			super(agente, plantilla);
		}

		// handle of propositions.
		protected void handlePropose(ACLMessage propuesta,
				ArrayList<ACLMessage> aceptadas) {
			System.out
					.printf("%s: Received offer of cars %s. A car offers for %s Euros.\n",
							this.myAgent.getName(), propuesta.getSender()
									.getLocalName(), propuesta.getContent());

			status = propuesta.getContent();
			state = "handle";

		}

		// handel of rejections of propositions.
		protected void handleRefuse(ACLMessage rechazo) {
			System.out.printf(
					"%s: Cars %s does not have cars that to offer.\n",
					this.myAgent.getName(), rechazo.getSender().getLocalName());

			status = rechazo.getContent();
			state = "refuse";

		}

		// Handle failure replies
		protected void handleFailure(ACLMessage fallo) {

			System.out
					.println("AMS: This sale of cars does not exist or is accessible");

			System.out.printf("%s: Cars %s has been a failure.\n",
					this.myAgent.getName(), fallo.getSender().getLocalName());

			// He failed, therefore, receive no response from this agent
			Client.this.numeroDeOfertas--;

			status = fallo.getContent();
			state = "failure";
			finished.countDown();

		}

		// Method collective called after end timeout or receive all proposals
		protected void handleAllResponses(ArrayList<ACLMessage> respuestas,
				ArrayList<ACLMessage> aceptados) {

			// Check if a sale of cars passed the deadline for submission of
			// tenders.
			if (respuestas.size() < numeroDeOfertas) {
				System.out.printf("%s: %d Car sales are late.\n",
						this.myAgent.getName(), Client.this.numeroDeOfertas
								- respuestas.size());
			}

			// We choose the best offer
			int mejorOferta = Integer.MAX_VALUE;
			AgentID mejorAutos = null;
			ACLMessage aceptado = null;
			for (Object resp : respuestas) {
				ACLMessage mensaje = (ACLMessage) resp;
				if (mensaje.getPerformativeInt() == ACLMessage.PROPOSE) {
					ACLMessage respuesta = mensaje.createReply();
					respuesta.setPerformative(ACLMessage.REJECT_PROPOSAL);
					aceptados.add(respuesta);

					// If the offer is the best (lower than all others)
					// It stores its price and the AID of the sale of cars that
					// made it.
					int oferta = Integer.parseInt(mensaje.getContent());
					if (oferta <= precionMaximo && oferta <= mejorOferta) {
						mejorOferta = oferta;
						mejorAutos = mensaje.getSender();
						aceptado = respuesta;
					}
				}
			}

			// If there is an accepted offer his performative is modified.
			if (aceptado != null) {
				System.out.printf("%s: Determined! Sell Car of the %s\n",
						this.myAgent.getName(), mejorAutos.getLocalName());
				aceptado.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			}
		}

		// Manager of information messages.
		protected void handleInform(ACLMessage inform) {
			System.out.printf("%s: %s has sent the contract.\n",
					this.myAgent.getName(), inform.getSender().getLocalName());

			status = inform.getContent();

			state = "inform";
			finished.countDown();

		}
	}

	public String getState() {
		return state;
	}

	public String getStatus() {

		return status;
	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {

	}

}