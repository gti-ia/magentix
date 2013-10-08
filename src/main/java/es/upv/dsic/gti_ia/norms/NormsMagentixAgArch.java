package es.upv.dsic.gti_ia.norms;

import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.Circumstance;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.LogicalFormula;
import jason.asSyntax.Rule;
import jason.asSyntax.StringTermImpl;
import jason.asSyntax.Term;
import jason.runtime.Settings;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.jason.MagentixAgArch;
import es.upv.dsic.gti_ia.organization.exception.MySQLException;

/**
 * @author Ricard Lopez Fogues
 * @author Jose Alemany Bordera - jalemany1@dsic.upv.es
 */

public class NormsMagentixAgArch extends MagentixAgArch {

	private static Logger logger = Logger.getLogger(NormsMagentixAgArch.class.getName());

	private BeliefDataBaseInterface bdbi = null;
	ArrayList<Literal> dataBasePercepts = null;
	ArrayList<Rule> rules = new ArrayList<Rule>();

	/**
	 * Starts the architecture
	 * 
	 * @param filename
	 *            File with the AgentSepak code
	 * @param agent
	 *            Agent with this architecture
	 */
	@Override
	protected void init(String filename, CAgent agent) {
		try {
			super.init(filename, agent);
			bdbi = new BeliefDataBaseInterface();
			dataBasePercepts = new ArrayList<Literal>();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Init error", e);
		}
	}

	protected Map<String, String> conversationIds = new HashMap<String, String>();

	@Override
	public void sendMsg(jason.asSemantics.Message m) throws Exception {

		ACLMessage acl = jasonToACL(m);
		if (m.isTell()) {
			acl.setHeader("activatedNorm", "true");
		} else if (m.isUnTell()) {
			acl.setHeader("activatedNorm", "false");
		}

		// Removes the beliefs of the belief base

		for (Literal l : dataBasePercepts) {
			this.getTS().getAg().getBB().remove(l);
		}

		// Removes the rules of the belief base
		for (Rule r : rules) {
			this.getTS().getAg().getBB().remove(r);
		}
		acl.addReceiver(new AgentID(m.getReceiver()));
		if (m.getInReplyTo() != null) {
			String convid = conversationIds.get(m.getInReplyTo());
			if (convid != null) {
				acl.setConversationId(convid);
			}
		}
		this.getJasonAgent().send(acl);
	}

	@Override
	public void checkMail() {
		ACLMessage m;

		do {

			m = getMessageList().poll();

			if (m != null) {

				String ilForce = aclToKqml(m.getPerformativeInt());
				String sender = m.getSender().name;
				String receiver = m.getReceiver().name;
				String replyWith = m.getReplyWith();
				String irt = m.getInReplyTo();

				// Llega un mensaje con un arrayList de String. EL primer campo
				// será la pregunta, el resto las reglas.

				ArrayList<String> messageContent = (ArrayList<String>) m
						.getContentObject();

				String actionQuery = messageContent.get(0);
				messageContent.remove(0);

				for (String jasonSentence : messageContent) {

					if (jasonSentence.contains(":-")) { // Adding a normRule

						StringTokenizer stRule = new StringTokenizer(
								jasonSentence, ":-");
						String action = stRule.nextToken();
						String description = stRule.nextToken();
						Rule normRule = null;

						normRule = new Rule(Literal.parseLiteral(action),
								(LogicalFormula) ListTermImpl
										.parse(description));

						this.getTS().getAg().getBB().add(normRule); // We do
																	// here the
																	// action

						rules.add(normRule);

					} else { // Adding a belief

						StringTokenizer stBelief = new StringTokenizer(
								jasonSentence, ".");
						String action = stBelief.nextToken();
						Literal belief = null;

						belief = new LiteralImpl(Literal.parseLiteral(action));

						this.getTS().getAg().getBB().add(belief); // We do here
																	// the
																	// action

						dataBasePercepts.add(belief);

					}
				}

				try {

					dataBasePercepts.addAll(bdbi.getIsUnit());
					dataBasePercepts.addAll(bdbi.getHasType());
					dataBasePercepts.addAll(bdbi.getHasParent());

					dataBasePercepts.addAll(bdbi.getIsRole());
					dataBasePercepts.addAll(bdbi.getHasAccessibility());
					dataBasePercepts.addAll(bdbi.getHasVisibility());
					dataBasePercepts.addAll(bdbi.getHasPosition());

					dataBasePercepts.addAll(bdbi.getIsAgent());
					dataBasePercepts.addAll(bdbi.getPlaysRole());

					dataBasePercepts.addAll(bdbi.getRoleCardinality());
					dataBasePercepts.addAll(bdbi.getPositionCardinality());

					dataBasePercepts.addAll(bdbi.getIsNorm());
					dataBasePercepts.addAll(bdbi.getHasDeontic());

				} catch (MySQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				for (Literal l : dataBasePercepts) {
					this.getTS().getAg().getBB().add(l);
				}

				m.setContent(actionQuery);
				System.out.println("Me llega un mensaje: " + ilForce);

				// also remembers conversation ID
				if (replyWith != null && replyWith.length() > 0) {
					if (m.getConversationId() != null) {
						conversationIds.put(replyWith, m.getConversationId());
					}
				} else {
					replyWith = "noid";
				}

				Object propCont = translateContentToJason(m);
				if (propCont != null) {
					jason.asSemantics.Message im = new jason.asSemantics.Message(
							ilForce, sender, receiver, propCont, replyWith);
					if (irt != null) {
						im.setInReplyTo(irt);
					}

					this.getTS().getC().getMailBox().add(im);
				}
			}

		} while (m != null);
	}

}