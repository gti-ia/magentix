package _ContractNet_Example;

import org.apache.qpid.transport.Connection;

import org.apache.qpid.transport.Connection;

import es.upv.dsic.gti_ia.architecture.FIPAContractNetInitiator;
import es.upv.dsic.gti_ia.architecture.FIPAContractNetResponder;
import es.upv.dsic.gti_ia.architecture.FIPANames;
import es.upv.dsic.gti_ia.architecture.FailureException;
import es.upv.dsic.gti_ia.architecture.MessageTemplate;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.NotUnderstoodException;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.RefuseException;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

public class Concesionario extends QueueAgent {

	public Concesionario(AgentID aid) throws Exception {

		super(aid);

	}

	public void execute() {

		Monitor m = new Monitor();
		System.out.printf("Autos %s: A la espera de clientes...\n", this
				.getName());

		// Se crea una plantilla que filtre los mensajes a recibir.
		MessageTemplate template = new MessageTemplate(
				FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);

		// Añadimos los comportamientos ante mensajes recibidos
		CrearOferta oferta = new CrearOferta(this, template);

		this.setTask(oferta);
		m.waiting();
		/*
		 * do { oferta.action();
		 * 
		 * }while(true);
		 */

	}

	// Hacemos una simulación para que pueda dar que existe o no coche (sobre un
	// 80% probab).
	private boolean existeCoche() {
		return (Math.random() * 100 > 20);
	}

	// Calculamos un precio para el coche aleatoriamente (estará entre 8000 y
	// 30000).
	private int obtenerPrecio() {
		return (int) (Math.random() * 22000) + 8000;
	}

	// Simula fallos en el cálculo de precios.
	private boolean devolverPrecio() {
		return (int) (Math.random() * 10) > 1;
	}

	private class CrearOferta extends FIPAContractNetResponder {
		public CrearOferta(QueueAgent agente, MessageTemplate plantilla) {
			super(agente, plantilla);
		}

		protected ACLMessage prepareResponse(ACLMessage cfp)
				throws NotUnderstoodException, RefuseException {
			System.out.printf("Autos %s: Peticion de oferta recibida de %s.\n",
					getName(), cfp.getSender().getLocalName());

			// Comprobamos si existen ofertas disponibles
			if (Concesionario.this.existeCoche()) {
				// Proporcionamos la información necesaria
				int precio = Concesionario.this.obtenerPrecio();
				System.out.printf("Autos %s: Preparando oferta (%d euros).\n",
						getName(), precio);

				// Se crea el mensaje
				ACLMessage oferta = cfp.createReply();
				oferta.setPerformative(ACLMessage.PROPOSE);
				oferta.setContent(String.valueOf(precio));
				return oferta;
			} else {
				// Si no hay ofertas disponibles rechazamos el propose
				System.out.printf(
						"Autos %s: No tenemos ofertas disponibles.\n",
						getName());
				throw new RefuseException("Fallo en la evaluación.");
			}
		}

		protected ACLMessage prepareResultNotification(ACLMessage cfp,
				ACLMessage propose, ACLMessage accept) throws FailureException {
			// Hemos recibido una aceptación de nuestra oferta, enviamos el
			// albarán
			System.out.printf("Autos %s: Hay una posible oferta.\n", getName());

			if (devolverPrecio()) {
				System.out.printf("Autos %s: Enviando contrato de compra.\n",
						getName());

				ACLMessage inform = accept.createReply();

				inform.setPerformative(ACLMessage.INFORM);
				return inform;
			} else {
				System.out.printf(
						"Autos %s: Vaya!, ha fallado al enviar el contrato.\n",
						getName());
				throw new FailureException("Error al enviar contrato.");
			}
		}

		protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose,
				ACLMessage reject) {
			// Nuestra oferta por el coche ha sido rechazada
			System.out.printf(
					"Autos %s: Oferta rechazada por su excesivo precio.\n",
					getName());
		}
	}

}
