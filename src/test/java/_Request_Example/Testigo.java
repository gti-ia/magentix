package _Request_Example;

import org.apache.qpid.transport.Connection;

import org.apache.qpid.transport.Connection;

import s.dsic.gti_ia.fipa.ACLMessage;
import s.dsic.gti_ia.fipa.AgentID;
import s.dsic.gti_ia.proto.FIPARequestInitiator;
import s.dsic.gti_ia.proto.FIPANames.InteractionProtocol;

import _BaseAgent_Example.QueueAgent;

public class Testigo extends QueueAgent {

	public Principal_Grafico frame;
	private boolean condicion = true;
	private Llamadas llamada;
	int con = 0;

	// private ManejadorInitiator iniciador = null;

	public Testigo(AgentID aid, Connection connection,
			Principal_Grafico _frame, Llamadas _llamada) {

		super(aid, connection);

		this.frame = _frame;
		this.llamada = _llamada;

	}

	public Testigo(AgentID aid, Connection connection) {

		super(aid, connection);

	}

	public void enviarMensaje(int i) {
		// Object[] args = getArguments();
		// if (args != null && args.length > 0) {

		frame
				.getTextArea(2)
				.append(
						"Número accidente: "
								+ i
								+ " He visto un accidente! Solicitando ayuda a varios hospitales...\n");
		System.out
				.println("He visto un accidente! Solicitando ayuda a varios hospitales...");

		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		// for (int i = 0; i < args.length; ++i)
		msg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));
		msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		msg
				.setContent("accidente a " + frame.getTextField().getText()
						+ " kms");
		msg.setSender(this.getAid());

		this.setTask(new ManejadorInitiator(this, msg, i));

	}

	public void detenerHilo() {
		this.condicion = false;
	}

	protected void execute() {
		do {

			this.enviarMensaje(1);

			llamada.esperar();

		} while (condicion);

	}

	class ManejadorInitiator extends FIPARequestInitiator {
		int i;

		public ManejadorInitiator(QueueAgent a, ACLMessage msg, int aux) {
			super(a, msg);
			this.i = aux;
		}

		protected void handleAgree(ACLMessage agree) {

			frame
					.getTextArea(2)
					.append(
							i
									+ " Hospital "
									+ agree.getSender().getLocalName()
									+ " informa que han salido a atender el accidente.\n");
			System.out.println(i + " !!!!Hospital "
					+ agree.getSender().getLocalName()
					+ " informa que han salido a atender el accidente.");
		}

		protected void handleRefuse(ACLMessage refuse) {
			frame
					.getTextArea(2)
					.append(
							i
									+ " Hospital "
									+ refuse.getSender().getLocalName()
									+ " responde que el accidente esta fuera de su radio de accion. No llegaremos a tiempo!!!\n");

			System.out
					.println(i
							+ " !!!!Hospital "
							+ refuse.getSender().getLocalName()
							+ " responde que el accidente esta fuera de su radio de accion. No llegaremos a tiempo!!!");
		}

		protected void handleNotUnderstood(ACLMessage notUnderstood) {
			frame.getTextArea(2).append(
					i + " Hospital " + notUnderstood.getSender().getLocalName()
							+ " no puede entender el mensaje.\n");

			System.out.println(i + " !!!!Hospital "
					+ notUnderstood.getSender().getLocalName()
					+ " no puede entender el mensaje.");
		}

		protected void handleInform(ACLMessage inform) {
			frame.getTextArea(2).append(
					i + " Hospital " + inform.getSender().getLocalName()
							+ " informa que han atendido el accidente.\n");

			System.out.println(i + " !!!!!!Hospital "
					+ inform.getSender().getLocalName()
					+ " informa que han atendido el accidente.");
		}

		protected void handleFailure(ACLMessage fallo) {
			if (fallo.getSender().name.equals(myAgent.getName())) {
				frame.getTextArea(2).append(
						i + " Alguna de los hospitales no existe\n");
				System.out.println(i + " Alguna de los hospitales no existe");
			} else {
				frame.getTextArea(2).append(
						i
								+ " Fallo en el hospital "
								+ fallo.getSender().getLocalName()
								+ ": "
								+ fallo.getContent().substring(0,
										fallo.getContent().length()) + ".\n");
				System.out.println(i
						+ " Fallo en el hospital "
						+ fallo.getSender().getLocalName()
						+ ": "
						+ fallo.getContent().substring(0,
								fallo.getContent().length()));
			}
		}
	}
}
