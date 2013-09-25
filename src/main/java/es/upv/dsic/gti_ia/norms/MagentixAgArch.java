package es.upv.dsic.gti_ia.norms;

import jason.RevisionFailedException;
import jason.architecture.AgArch;
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
import jason.infra.centralised.RunCentralisedMAS;
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
import es.upv.dsic.gti_ia.organization.exception.MySQLException;

/**
 * @author Ricard Lopez Fogues
 */

public class MagentixAgArch extends AgArch{

	private CAgent jasonAgent;
	private static Logger logger = Logger.getLogger(MagentixAgArch.class.getName());
	private Queue<ACLMessage> messageList = new LinkedList<ACLMessage>();
	protected boolean running = true;
	private Agent ag;
	private BeliefDataBaseInterface bdbi = null;
	ArrayList<Literal> dataBasePercepts = null;
	ArrayList<Rule> rules = new ArrayList<Rule>();
	/**
	 * Starts the architecture
	 * @param filename File with the AgentSepak code
	 * @param agent Agent with this architecture
	 */
	protected void init(String filename, CAgent agent){
		try {
			this.jasonAgent = agent;
			ag = new Agent();
			new TransitionSystem(ag, new Circumstance(), new Settings(), this);
			ag.initAg(filename);
			bdbi = new BeliefDataBaseInterface();
			dataBasePercepts = new ArrayList<Literal>();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Init error", e);
		}
	}

	public Agent getJasonAgent()
	{
		return ag;
	}

	/**
	 * Runs the reasoning cycle
	 */
	public void run() {
		//RunCentralisedMAS.setupLogger();
		try {
			while (isRunning()) {
				// calls the Jason engine to perform one reasoning cycle
				logger.fine("Reasoning....");
				// parche para arreglar la sincronizacion, buscar mejor solucion y porque occure esto
				if(this.jasonAgent.getMutexHoldCount() > 0)
					this.jasonAgent.unlock();
				getTS().reasoningCycle();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Run error", e);
		}
	}

	public String getAgName() {
		return jasonAgent.getAid().name;
	}

	// TODO implementar agente environment y interacción de agentes con environment	
	@Override
	public List<Literal> perceive() 
	{
		List<Literal> l = new ArrayList<Literal>();
		return l;
	}

	// TODO implementar agente environment y interacción de agentes con environment
	@Override
	public void act(ActionExec action, List<ActionExec> feedback)
	{
		getTS().getLogger().info("Agent " + getAgName() + 
				" is doing: " + action.getActionTerm());
		// return confirming the action execution was OK
		action.setResult(true);
		feedback.add(action);
	}

	@Override
	public boolean canSleep() {
		return true;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	// a very simple implementation of sleep
	@Override
	public void sleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
	}

	protected Map<String,String> conversationIds = new HashMap<String,String>();

	@Override
	public void sendMsg(jason.asSemantics.Message m) throws Exception {


		ACLMessage acl = jasonToACL(m);
		if (m.isTell())
		{
			acl.setHeader("activatedNorm", "true");
		}else if (m.isUnTell())
		{
			acl.setHeader("activatedNorm", "false");
		}

		//Removes the beliefs of the belief base

		for(Literal l : dataBasePercepts)
		{
			this.getTS().getAg().getBB().remove(l);
		}

		//Removes the rules of the belief base
		for (Rule r : rules)
		{
			this.getTS().getAg().getBB().remove(r);
		}
		acl.addReceiver(new AgentID(m.getReceiver()));
		if (m.getInReplyTo() != null) {
			String convid = conversationIds.get(m.getInReplyTo());
			if (convid != null) {
				acl.setConversationId(convid);
			}
		}
		this.jasonAgent.send(acl);
	}

	@Override
	public void checkMail() {
		ACLMessage m;


		do{

			m = messageList.poll();

			if(m != null){

				String ilForce = aclToKqml(m.getPerformativeInt());
				String sender = m.getSender().name;
				String receiver = m.getReceiver().name;
				String replyWith = m.getReplyWith();
				String irt = m.getInReplyTo();



				//Llega un mensaje con un arrayList de String. EL primer campo será la pregunta, el resto las reglas.


				ArrayList<String> messageContent = (ArrayList<String>) m.getContentObject();

				String actionQuery = messageContent.get(0);
				messageContent.remove(0);

				
				for (String jasonSentence : messageContent) {
					
					if (jasonSentence.contains(":-")) { //Adding a normRule
						
						StringTokenizer stRule = new StringTokenizer(jasonSentence, ":-");
						String action = stRule.nextToken();
						String description = stRule.nextToken();
						Rule normRule = null;

						normRule = new Rule(Literal.parseLiteral(action),(LogicalFormula) ListTermImpl.parse(description));
					
						this.getTS().getAg().getBB().add(normRule); //We do here the action
						
						rules.add(normRule);
						
					} else { //Adding a belief
						
						StringTokenizer stBelief = new StringTokenizer(jasonSentence, ".");
						String action = stBelief.nextToken();
						Literal belief = null;
						
						belief = new LiteralImpl(Literal.parseLiteral(action));
					
						this.getTS().getAg().getBB().add(belief); //We do here the action
						
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

				for(Literal l : dataBasePercepts)
				{
					this.getTS().getAg().getBB().add(l);
				}


				m.setContent(actionQuery);
				System.out.println("Me llega un mensaje: "+ ilForce);

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
					jason.asSemantics.Message im = new jason.asSemantics.Message(ilForce, sender, receiver, propCont, replyWith);
					if (irt != null) {
						im.setInReplyTo(irt);
					}

					this.getTS().getC().getMailBox().add(im);
				}
			}
		
	} while(m != null);
}

/** 
 * returns the content of the message m and implements some pro-processing of the content, if necessary 
 * @param m Message to translate
 * */
protected Object translateContentToJason(ACLMessage m) {
	Object propCont = null;
	try {
		propCont = m.getContentObject();
		if (propCont instanceof String) {
			// try to parse as term
			try {
				propCont = ASSyntax.parseTerm((String)propCont);
			} catch (Exception e) {  // no problem 
			}
		}            
	} catch (Exception e) { // no problem try another thing
	}

	if (propCont == null) { // still null
		// try to parse as term
		try {
			propCont = ASSyntax.parseTerm(m.getContent());
		} catch (Exception e) {
			// not AS messages are treated as string 
			propCont = new StringTermImpl(m.getContent());
		}
	}
	return propCont;
}


@Override
public void broadcast(jason.asSemantics.Message m) throws Exception {
}

/**
 * Converts a jason message into an ACLMessage
 * @param m
 * @return
 * @throws IOException
 */
private ACLMessage jasonToACL(Message m) throws IOException {
	ACLMessage acl = new ACLMessage(kqmlToACL(m.getIlForce()));
	// send content as string if it is a Term/String (it is better for interoperability)
	if (m.getPropCont() instanceof Term || m.getPropCont() instanceof String) {
		acl.setContent(m.getPropCont().toString());       
	} else {
		acl.setContentObject((Serializable)m.getPropCont());
	}
	acl.setReceiver(new AgentID(m.getReceiver()));
	acl.setSender(this.jasonAgent.getAid());
	acl.setReplyWith(m.getMsgId());
	acl.setLanguage("AgentSpeak");
	if (m.getInReplyTo() != null) {
		acl.setInReplyTo(m.getInReplyTo());
	}
	return acl;
}

private static final int UNTELL    = 1001;
private static final int ASKALL    = 1002;
private static final int UNACHIEVE = 1003;
private static final int TELLHOW   = 1004;
private static final int UNTELLHOW = 1005;
private static final int ASKHOW    = 1006;

/**
 * Converts a kqml performative into a fipa performative
 * @param p
 * @return
 */
private static int kqmlToACL(String p) {
	if (p.equals("tell")) {
		return ACLMessage.INFORM;
	} else if (p.equals("askOne")) {
		return ACLMessage.QUERY_REF;
	} else if (p.equals("achieve")) {
		return ACLMessage.REQUEST;
	} else if (p.equals("untell")) {
		return ACLMessage.INFORM;
	} else if (p.equals("unachieve")) {
		return UNACHIEVE;
	} else if (p.equals("askAll")) {
		return ASKALL;
	} else if (p.equals("askHow")) {
		return ASKHOW;
	} else if (p.equals("tellHow")) {
		return TELLHOW;
	} else if (p.equals("untellHow")) {
		return UNTELLHOW;
	}
	return ACLMessage.getPerformative(p);       
}

/**
 * Converts a fipa performative into a kqml one
 * @param p
 * @return
 */
private static String aclToKqml(int p) {
	switch(p) {
	case ACLMessage.INFORM: return "tell"; 
	case ACLMessage.QUERY_REF: return "askOne";
	case ACLMessage.REQUEST: return "achieve";
	case UNTELL: return "untell";
	case UNACHIEVE: return "unachieve";
	case ASKALL: return "askAll";
	case ASKHOW: return "askHow";
	case TELLHOW: return "tellHow";
	case UNTELLHOW: return "untellHow";
	}
	return ACLMessage.getPerformative(p).toLowerCase().replaceAll("-", "_");
}

/**
 * Adds a message to the message list
 * @param msg
 */
protected void addMessage(ACLMessage msg){
	this.messageList.add(msg);
}

public void stopAg(){
	running = false;
}

}
