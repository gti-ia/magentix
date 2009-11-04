package _Query_Example;

import org.apache.qpid.transport.Connection;

import s.dsic.gti_ia.fipa.*;
import s.dsic.gti_ia.proto.*;

import _BaseAgent_Example.QueueAgent;

import es.upv.ACLMessage;
import es.upv.AgentID;
import es.upv.FIPANames;
import es.upv.FIPAQueryInitiator;
import es.upv.Monitor;

public class Viajante extends QueueAgent {

	private Monitor adv = new Monitor();

	public Viajante(AgentID aid, Connection connection) {

		super(aid, connection);

	}

	protected void execute() {

		// Creamos el mensaje de la consulta.

		ACLMessage mensaje = new ACLMessage(ACLMessage.QUERY_IF);
		mensaje.setProtocol(FIPANames.InteractionProtocol.FIPA_QUERY);
		mensaje.setContent("" + "¿Tengo la reserva?");

		mensaje.setSender(getAid());
		mensaje
				.setReceiver(new AgentID("aeropuerto1", "qpid", "localhost", ""));

		// Añadimos el comportamiento de la consulta.
		this.setTask(new ComprobarInitiator(this, mensaje));
		adv.waiting();

	}

	class ComprobarInitiator extends FIPAQueryInitiator {
		public ComprobarInitiator(QueueAgent agente, ACLMessage mensaje) {
			super(agente, mensaje);
		}

		protected void handleAgree(ACLMessage agree) {
			System.out
					.printf(
							"Espere un momento por favor, estamos buscando en la Base de Datos.",
							agree.getSender().getLocalName());
		}

		protected void handleRefuse(ACLMessage refuse) {
			System.out
					.printf(
							"%s: En estos momentos todas las operadoras estan ocupadas. No podemos atenderle.",
							getName(), refuse.getSender().getLocalName());
		}

		protected void handleNotUnderstood(ACLMessage notUnderstood) {
			System.out.printf("%s: La operadora no entiende el mensaje.",
					getName(), notUnderstood.getSender().getLocalName());
		}

		protected void handleInform(ACLMessage inform) {
			System.out.printf("La operadora informa: %s.", inform.getContent());
		}

		protected void handleFailure(ACLMessage fallo) {
			System.out.println(getName() + ": Se ha producido un fallo.");
		}
	}
}
