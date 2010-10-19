package es.upv.dsic.gti_ia.jason;

import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.Circumstance;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

/**
 * @author Ricard Lopez Fogues
 */

public class MagentixAgArch extends AgArch{
	
	private CAgent jasonAgent;
	private static Logger logger = Logger.getLogger(MagentixAgArch.class.getName());
	private Queue<ACLMessage> messageList = new LinkedList<ACLMessage>();
	protected boolean running = true;
	
	public void init(String filename, CAgent agent){
		try {
			this.jasonAgent = agent;
			Agent ag = new Agent();
			new TransitionSystem(ag, new Circumstance(), new Settings(), this);
			ag.initAg(filename);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Init error", e);
		}
	}
			
	public void run() {
		RunCentralisedMAS.setupLogger();
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
	
	/** returns the content of the message m and implements some pro-processing of the content, if necessary */
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
	
	protected ACLMessage jasonToACL(Message m) throws IOException {
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

	public static final int UNTELL    = 1001;
	public static final int ASKALL    = 1002;
	public static final int UNACHIEVE = 1003;
	public static final int TELLHOW   = 1004;
	public static final int UNTELLHOW = 1005;
	public static final int ASKHOW    = 1006;

	public static int kqmlToACL(String p) {
		if (p.equals("tell")) {
			return ACLMessage.INFORM;
		} else if (p.equals("askOne")) {
			return ACLMessage.QUERY_REF;
		} else if (p.equals("achieve")) {
			return ACLMessage.REQUEST;
		} else if (p.equals("untell")) {
			return UNTELL;
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

	public static String aclToKqml(int p) {
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
	
	public void addMessage(ACLMessage msg){
		this.messageList.add(msg);
	}
	
	public void stopAg(){
		running = false;
	}

}
