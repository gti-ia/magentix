package TestContractNet;

import java.util.concurrent.CountDownLatch;

import es.upv.dsic.gti_ia.architecture.FIPAContractNetResponder;
import es.upv.dsic.gti_ia.architecture.FIPANames;
import es.upv.dsic.gti_ia.architecture.FailureException;
import es.upv.dsic.gti_ia.architecture.MessageTemplate;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.NotUnderstoodException;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.RefuseException;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

public class Concessionaire extends QueueAgent {

	private CountDownLatch finished;

	public Concessionaire(AgentID aid, CountDownLatch finished)
			throws Exception {

		super(aid);
		this.finished = finished;

	}

	public void execute() {

		// Monitor m = new Monitor();
		System.out.printf("%s: Waiting for customers...\n", this.getName());

		// It creates a template that filters the messages to receive.
		MessageTemplate template = new MessageTemplate(
				FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);

		// We add behaviors to messages received
		CrearOferta oferta = new CrearOferta(this, template);

		this.addTask(oferta);

		try {
			finished.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// m.waiting();

	}

	// We make a simulation in order to give that car or not (about
	// 80% probab).
	private boolean existeCoche() {
		return (Math.random() * 100 > 20);
	}

	// We estimate a price for the car at random (it will be between 8000 and
	// 30000).
	private int obtenerPrecio() {
		return (int) (Math.random() * 22000) + 8000;
	}

	// Simulate failures in the calculation of prices.
	private boolean devolverPrecio() {
		return (int) (Math.random() * 10) > 1;
	}

	private class CrearOferta extends FIPAContractNetResponder {
		public CrearOferta(QueueAgent agente, MessageTemplate plantilla) {
			super(agente, plantilla);
		}

		protected ACLMessage prepareResponse(ACLMessage cfp)
				throws NotUnderstoodException, RefuseException {
			System.out.printf("%s: Request offer received from %s.\n",
					getName(), cfp.getSender().getLocalName());

			// We check for available offers
			if (Concessionaire.this.existeCoche()) {
				// We provide the information necessary
				int precio = Concessionaire.this.obtenerPrecio();
				System.out.printf("%s: Preparing Offer (%d euros).\n",
						getName(), precio);

				// You create the message
				ACLMessage oferta = cfp.createReply();
				oferta.setPerformative(ACLMessage.PROPOSE);
				oferta.setContent(String.valueOf(precio));
				return oferta;
			} else {
				// Please no offers reject the propose
				System.out.printf("%s: We have no offers available.\n",
						getName());
				throw new RefuseException("I fail in the evaluation.");
			}
		}

		protected ACLMessage prepareResultNotification(ACLMessage cfp,
				ACLMessage propose, ACLMessage accept) throws FailureException {
			// We have received an acceptance of our offer, we send the delivery
			// note
			System.out.printf("%s: There is a possible offer.\n", getName());

			if (devolverPrecio()) {
				System.out
						.printf("%s: Sending purchase contract.\n", getName());

				ACLMessage inform = accept.createReply();

				inform.setContent("Sending purchase contract.");
				inform.setPerformative(ACLMessage.INFORM);
				return inform;
			} else {
				System.out.printf(
						"%s: OHH!, has failed to send the contract.\n",
						getName());
				throw new FailureException("Error on having sent contract.");
			}
		}

		protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose,
				ACLMessage reject) {
			// Our offer for the car has been rejected
			System.out.printf("%s: Offer rejected by his excessive price.\n",
					getName());
		}
	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		// finished.countDown();
	}

}
