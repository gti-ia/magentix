package es.upv.dsic.gti_ia.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import es.upv.dsic.gti_ia.core.ISO8601;

/**
 * This class represents a message sent between agents.
 * @author Ricard Lopez Fogues
 * @author Luis Burdalo (Added toString and fromString methods)
 */
@SuppressWarnings("unchecked")
public class ACLMessage implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// miembros
	/** constant identifying the FIPA performative */
	public static final int ACCEPT_PROPOSAL = 0;
	/** constant identifying the FIPA performative */
	public static final int AGREE = 1;
	/** constant identifying the FIPA performative */
	public static final int CANCEL = 2;
	/** constant identifying the FIPA performative */
	public static final int CFP = 3;
	/** constant identifying the FIPA performative */
	public static final int CONFIRM = 4;
	/** constant identifying the FIPA performative */
	public static final int DISCONFIRM = 5;
	/** constant identifying the FIPA performative */
	public static final int FAILURE = 6;
	/** constant identifying the FIPA performative */
	public static final int INFORM = 7;
	/** constant identifying the FIPA performative */
	public static final int INFORM_IF = 8;
	/** constant identifying the FIPA performative */
	public static final int INFORM_REF = 9;
	/** constant identifying the FIPA performative */
	public static final int NOT_UNDERSTOOD = 10;
	/** constant identifying the FIPA performative */
	public static final int PROPOSE = 11;
	/** constant identifying the FIPA performative */
	public static final int QUERY_IF = 12;
	/** constant identifying the FIPA performative */
	public static final int QUERY_REF = 13;
	/** constant identifying the FIPA performative */
	public static final int REFUSE = 14;
	/** constant identifying the FIPA performative */
	public static final int REJECT_PROPOSAL = 15;
	/** constant identifying the FIPA performative */
	public static final int REQUEST = 16;
	/** constant identifying the FIPA performative */
	public static final int REQUEST_WHEN = 17;
	/** constant identifying the FIPA performative */
	public static final int REQUEST_WHENEVER = 18;
	/** constant identifying the FIPA performative */
	public static final int SUBSCRIBE = 19;
	/** constant identifying the FIPA performative */
	public static final int PROXY = 20;
	/** constant identifying the FIPA performative */
	public static final int PROPAGATE = 21;
	/** constant identifying an unknown performative */
	public static final int UNKNOWN = -1;

	private static final String[] performatives = new String[22];
	static { // initialization of the Vector of performatives
		performatives[ACCEPT_PROPOSAL] = "ACCEPT-PROPOSAL";
		performatives[AGREE] = "AGREE";
		performatives[CANCEL] = "CANCEL";
		performatives[CFP] = "CFP";
		performatives[CONFIRM] = "CONFIRM";
		performatives[DISCONFIRM] = "DISCONFIRM";
		performatives[FAILURE] = "FAILURE";
		performatives[INFORM] = "INFORM";
		performatives[INFORM_IF] = "INFORM-IF";
		performatives[INFORM_REF] = "INFORM-REF";
		performatives[NOT_UNDERSTOOD] = "NOT-UNDERSTOOD";
		performatives[PROPOSE] = "PROPOSE";
		performatives[QUERY_IF] = "QUERY-IF";
		performatives[QUERY_REF] = "QUERY-REF";
		performatives[REFUSE] = "REFUSE";
		performatives[REJECT_PROPOSAL] = "REJECT-PROPOSAL";
		performatives[REQUEST] = "REQUEST";
		performatives[REQUEST_WHEN] = "REQUEST-WHEN";
		performatives[REQUEST_WHENEVER] = "REQUEST-WHENEVER";
		performatives[SUBSCRIBE] = "SUBSCRIBE";
		performatives[PROXY] = "PROXY";
		performatives[PROPAGATE] = "PROPAGATE";
	}

	/**
	 * @uml.property name="performative"
	 */
	private int performative;
	/**
	 * @uml.property name="sender"
	 * @uml.associationEnd
	 */
	private AgentID sender = new AgentID();
	// private AgentID receiver = new AgentID();

	/*
	 * List of receivers to enable Multi-Cast
	 */

	/**
	 * @uml.property name="receiver"
	 */
	private ArrayList<AgentID> receiver = new ArrayList();

	/**
	 * @uml.property name="reply_to"
	 * @uml.associationEnd
	 */
	private AgentID reply_to = new AgentID();
	/**
	 * @uml.property name="content"
	 */
	private String content = "";
	
	private byte[] byteSequenceContent = null;
	
	/**
	 * @uml.property name="language"
	 */
	private String language = "";
	/**
	 * @uml.property name="encoding"
	 */
	private String encoding = "";
	/**
	 * @uml.property name="ontology"
	 */
	private String ontology = "";
	/**
	 * @uml.property name="protocol"
	 */
	private String protocol = "";
	/**
	 * @uml.property name="conversation_id"
	 */
	private String conversation_id = "";
	/**
	 * @uml.property name="reply_with"
	 */
	private String reply_with = "";
	/**
	 * @uml.property name="in_reply_to"
	 */
	private String in_reply_to = "";
	/**
	 * @uml.property name="reply_byInMillu isec"
	 */
	private long reply_byInMillisec = 0;

	private Map<String, String> headers = new HashMap<String, String>();

	// constructores

	/**
	 * Empty constructor. The performative is set to UNKNOWN
	 * @see setPerformative
	 */
	public ACLMessage() {
		performative = UNKNOWN;
	}

	/**
	 * Constructor for the class. Only sets the performative.
	 * @param performative The performative to be used in this message.
	 * @see setPerformative
	 */
	public ACLMessage(int performative) {
		this.performative = performative;
		headers.put("ERROR", ""); // ???
	}

	/**
	 * Sets performative type
	 * 
	 * @param performative
	 * @uml.property name="performative"
	 */
	public void setPerformative(int performative) {
		this.performative = performative;
	}

	/**
	 * @return Performative type as a String
	 * @uml.property name="performative"
	 */
	public String getPerformative() {
		try {
			if(performative == -1) return "UNKNOWN";
			return performatives[performative];
		} catch (Exception e) {
			return performatives[NOT_UNDERSTOOD];
		}
	}

	/**
	 * 
	 * @return Performative type as an integer
	 */
	public int getPerformativeInt() {
		return performative;
	}

	/**
	 * Set sender
	 * 
	 * @param sender The AgentID of the sender of this message.
	 * @uml.property name="sender"
	 * @see AgentID
	 */
	public void setSender(AgentID sender) {
		this.sender = sender;
	}

	/**
	 * Returns the sender of this message.
	 * @return sender The AgentID of the sender of this message.
	 * @uml.property name="sender"
	 * @see AgentID
	 */
	public AgentID getSender() {
		return sender;
	}

	/**
	 * Sets the receiver. It deletes the receivers list and creates a new one
	 * with the new receiver
	 * 
	 * @param receiver
	 */
	public void setReceiver(AgentID receiver) {
		this.receiver.clear();
		this.receiver.add(receiver);
	}

	/**
	 * Obtains the AgentID of the first receiver of the (possibly many) receivers of this message.
	 * @return First receiver in receivers list
	 * @uml.property name="receiver"
	 * @see AgentID
	 */
	public AgentID getReceiver() {
		if (receiver.isEmpty()) {
			return null;
		} else {
			return receiver.get(0);
		}
	}

	/**
	 * Returns receiver in index position in receivers list
	 * 
	 * @param index
	 * @return receiver
	 */
	public AgentID getReceiver(int index) {
		return receiver.get(index);
	}

	/**
	 * Sets ReplyTo field
	 * 
	 * @param reply
	 */
	public void setReplyTo(AgentID reply) {
		reply_to = reply;
	}

	/**
	 * Gets the ReplyTo field
	 * 
	 * @return AgentID of ReplyTo field
	 */
	public AgentID getReplyTo() {
		return reply_to;
	}

	/**
	 * Sets content field.
	 * 
	 * @param cont
	 * @uml.property name="content"
	 */
	public void setContent(String cont) {
		byteSequenceContent = null; //make to null the other variable
		content = cont;
	}

	/**
	 * Gets the content of this message.
	 * 
	 * @return Content of content field as a String
	 * @uml.property name="content"
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets language field
	 * 
	 * @param lang
	 * @uml.property name="language"
	 */
	public void setLanguage(String lang) {
		language = lang;
	}

	/**
	 * Gets the language field.
	 * 
	 * @return content of language field
	 * @uml.property name="language"
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Set encoding of the message
	 * 
	 * @param encoding
	 * @uml.property name="encoding"
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Get the encoding for this message.
	 * @return encoding
	 * @uml.property name="encoding"
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Set ontology of the message
	 * 
	 * @param ontology
	 * @uml.property name="ontology"
	 */
	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	/**
	 * Get the ontology for the message
	 * 
	 * @return ontology
	 * @uml.property name="ontology"
	 */
	public String getOntology() {
		return ontology;
	}

	/**
	 * Sets the protocol for the message
	 * 
	 * @param protocol
	 * @uml.property name="protocol"
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * Gets the protocol for the message
	 * 
	 * @return protocol
	 * @uml.property name="protocol"
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * Sets the conversationID of this message.
	 * 
	 * @param id A String containing the conversationID to be set.
	 */
	public void setConversationId(String id) {
		conversation_id = id;
	}

	/**
	 * Gets the conversationID of this message.
	 * 
	 * @return conversation id
	 */
	public String getConversationId() {
		return conversation_id;
	}

	/**
	 * Sets the replyWith field of this message.
	 * 
	 * @param replyWith
	 */
	public void setReplyWith(String rw) {
		reply_with = rw;
	}

	/**
	 * Gets the replyWith field of this message.
	 * 
	 * @return reply with
	 */
	public String getReplyWith() {
		return reply_with;
	}

	/**
	 * Sets the inReplyTo field of this message.
	 * 
	 * @param inReplyTo 
	 */
	public void setInReplyTo(String irt) {
		in_reply_to = irt;
	}

	/**
	 * Gets the inReplyTo field of this message.
	 * 
	 * @return in reply to
	 */
	public String getInReplyTo() {
		return in_reply_to;
	}

	/**
	 * Sets the replyByDate field for this message.
	 * 
	 * @param date If the date is null, the current time and date is used.
	 */
	public void setReplyByDate(Date date) {
		reply_byInMillisec = (date == null ? 0 : date.getTime());
	}

	/**
	 * Gets the replyByDate field for this message.
	 * @return reply by time in date format
	 */
	public Date getReplyByDate() {
		if (reply_byInMillisec != 0)
			return new Date(reply_byInMillisec);
		else
			return null;
	}

	/**
	 * Gets the replyBy field.
	 * 
	 * @return reply by time in string format
	 */
	public String getReplyBy() {
		if (reply_byInMillisec != 0)
			return ISO8601.toString(new Date(reply_byInMillisec));
		else
			return "";
	}

	/**
	 * Sets the performative field in the message.
	 * 
	 * @param performative Must contain a valid performative (e.g. INFORM) in order to be set.
	 * See the performatives in this class to notice which ones are valid.
	 * @see ACLMessage
	 */
	public void setPerformative(String performative) {
		for (int i = 0; i < performatives.length; i++) {
			if (performative.compareTo(performatives[i]) == 0) {
				this.performative = i;
				break;
			}
		}
	}

	/**
	 * Clears receivers list
	 */
	public void clearAllReceiver() {
		this.receiver.clear();
	}

	/**
	 * Adds a receiver to the receivers list
	 * 
	 * @param receiver AgentID of the receiver to be added.
	 * @return -1 if the agent already exists in the list, 1 otherwise
	 */
	public int addReceiver(AgentID r) {
		// Comprovem si existeix l'agent, si existeix tornem -1
		for (int i = 0; i < receiver.size(); i++) {
			if (receiver.get(i).name.equals(r.name)
					&& receiver.get(i).host.equals(r.host)
					&& receiver.get(i).port.equals(r.port)
					&& receiver.get(i).protocol.equals(r.protocol)) {
				return -1;
			}
		}
		receiver.add(r);
		return 1;
	}

	/**
	 * Returns the whole list of the AgentID of the receivers.
	 * @return receivers list
	 * @see AgentID
	 */
	public ArrayList<AgentID> getReceiverList() {
		return receiver;
	}

	/**
	 * Gets the count of receivers of this message.
	 * 
	 * @return total number of receivers
	 */
	public int getTotalReceivers() {
		return receiver.size();
	}

	/**
	 * Clones Message
	 * 
	 * @return Another ACLMessage that is a clone from this one
	 */
	public synchronized ACLMessage clone() {
		ACLMessage result;

		try {
			result = (ACLMessage) super.clone();

		} catch (CloneNotSupportedException cnse) {
			throw new InternalError(); // This should never happen
		}

		return result;
	}

	/**
	 * Creates an ACLMessage that is a reply to this one
	 * 
	 * @return ACLMessage that is a reply to this one
	 */
	public ACLMessage createReply() {
		ACLMessage m = (ACLMessage) clone();

		m.clearAllReceiver();

		m.setReceiver(getSender());
		m.setLanguage(getLanguage());
		m.setOntology(getOntology());
		m.setProtocol(getProtocol());
		m.setSender(null);
		m.setInReplyTo(getReplyWith());
		m.setConversationId(getConversationId());
		m.setReplyByDate(null);
		m.setContent("");
		m.setEncoding("");
		// #CUSTOM_EXCLUDE_BEGIN
		// Set the Aclrepresentation of the reply message to the
		// aclrepresentation of the sent message

		// #CUSTOM_EXCLUDE_END
		return m;
	}

	/**
	 * Copies the fields of the message to this one, when those fields have a correct value. If they don't, they are not copied.
	 * 
	 * @param msg From which the fields are to be copied.
	 */
	public void copyFromAsTemplate(ACLMessage msg) {		

		if (msg.getPerformativeInt() != ACLMessage.UNKNOWN) {
			this.setPerformative(msg.getPerformativeInt());
		}
		
		this.setSender(msg.getSender());
		
		if (msg.getReceiverList() != null){
			for(int i = 0; i<msg.getReceiverList().size();i++)
				this.addReceiver(msg.getReceiver(i));
		}
		
		this.setReplyTo(msg.getReplyTo());
		
		if (msg.getContent() != null) {
			this.setContent(msg.getContent());
		}
		
		this.setLanguage(msg.getLanguage());
		
		this.setEncoding(msg.getEncoding());
		
		if (msg.getOntology() != null) {
			this.setOntology(msg.getOntology());
		}
		
		if (msg.getProtocol() != null) {
			this.setProtocol(msg.getProtocol());
		}
		
		if (msg.getConversationId() != null) {
			this.setConversationId(this.getConversationId());
		}
		
		this.setReplyWith(msg.getReplyWith());
		
		this.setInReplyTo(msg.getInReplyTo());
		
		this.setReplyByDate(msg.getReplyByDate());
		
		Iterator it = this.headers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			this.setHeader(String.valueOf(pairs.getKey()), String.valueOf(pairs.getValue()));
		}
	}

	/**
	 * Sets the value of a header for this message.
	 * 
	 * @param key The name of the header
	 * @param value The content for the header
	 */
	public void setHeader(String key, String value) {
		headers.put(key, value);
	}

	/**
	 * Gets the value of a header for this message.
	 * 
	 * @param key The name of the header
	 * @return The value of the header specified
	 */
	public String getHeaderValue(String key) {
		if (headers.get(key) != null)
			return headers.get(key);
		else
			return "";
	}

	/**
	 * Obtains the whole set of headers in this message.
	 * 
	 * @return A Map containing all the headers (and its contents) in this message
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	private boolean headersAreEqual(ACLMessage msg) {
		Iterator<String> itr = headers.keySet().iterator();
		// iterate through HashMap values iterator
		String key1;
		while (itr.hasNext()) {
			key1 = itr.next();
			if (!key1.equals("ERROR")
					&& !headers.get(key1).equals(msg.getHeaderValue(key1)))
				return false;
		}
		return true;
	}
	
	/**
	 Sets the value of byte sequence content
	 @param serializable object to store
	 */
	public void setContentObject(java.io.Serializable s) throws IOException
	{
		ByteArrayOutputStream c = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(c);
		oos.writeObject(s);
		oos.flush();
		setByteSequenceContent(c.toByteArray());
	}
	
	/**
	 Sets the value of byte sequence content
	 @param byte array to store
	 */
	public void setByteSequenceContent(byte[] content) {
		this.content = null; //make to null the other variable
		byteSequenceContent = content;
	}
	
	/**
	 Returns the integer corresponding to the performative
	 @return the integer corresponding to the performative; -1 otherwise
	 */
	public static int getPerformative(String perf)
	{
		String tmp = perf.toUpperCase();
		for (int i=0; i<performatives.length; i++)
			if (performatives[i].equals(tmp))
				return i;
		return -1;
	}
	
	/**
	 Returns the string corresponding to the integer for the performative
	 @return the string corresponding to the integer for the performative; 
	 "NOT-UNDERSTOOD" if the integer is out of range.
	 */
	public static String getPerformative(int perf){
		try {
			return performatives[perf];
		} catch (Exception e) {
			return performatives[NOT_UNDERSTOOD];
		}
	}
	
	/**
	 * Reads <code>:content</code> slot. <p>
	 * @return The value of <code>:content</code> slot.
	 */
	public byte[] getByteSequenceContent() {
		if (content != null) 
			return new StringBuffer(content).toString().getBytes();
		else if (byteSequenceContent != null)
			return byteSequenceContent;
		return null;
	}
	
	
	/**
	 * This method returns the content of this ACLMessage when they have
	 * been written via the method <code>setContentObject</code>.
	 * 
	 * @return the object read from the content of this ACLMessage
	 */
	public Object getContentObject() {
		
		Object o = null;
		if(content != null && content != "")
			return (Object)content;
		else if(this.byteSequenceContent != null){
			try{
				byte[] data = getByteSequenceContent();
				if (data == null)
					return null;
				ByteArrayInputStream bis = new ByteArrayInputStream(data);
				ObjectInputStream oin = new ObjectInputStream(bis);
				o = (java.io.Serializable)oin.readObject();
				return o;
			}
			catch (java.lang.Error e) {
				e.printStackTrace();
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
			catch(ClassNotFoundException e2) {
				e2.printStackTrace();
			}
		}
		return o;
	}
	
	/**
	 * Serializes this message to a String, using all the fields in the message.
	 * The fields are separated using the char '#'
	 */
	public String toString(){
		// Serialize message content
		String strMsg;
		
		// Performative
		strMsg = this.getPerformativeInt() + "#";
		// Sender
		if (this.getSender() == null){
			strMsg = strMsg + "0#";
		}
		else{
			strMsg = strMsg + this.getSender().toString().length() + "#"
						+ this.getSender().toString();
		}
		// receiver
		if (this.getReceiver() == null){
			strMsg = strMsg + "0#";
		}
		else{
			strMsg = strMsg + this.getReceiver().toString().length() + "#"
						+ this.getReceiver().toString();
		}
		// reply to
		if (this.getReplyTo() == null){
			strMsg = strMsg + "0#";
		}
		else{
			strMsg = strMsg + this.getReplyTo().toString().length() + "#"
						+ this.getReplyTo().toString();
		}
		// language
		if (this.getLanguage() == null){
			strMsg = strMsg + "0#";
		}
		else{
			strMsg = strMsg + this.getLanguage().length() + "#" + this.getLanguage();
		}
		// encoding
		if (this.getEncoding() == null){
			strMsg = strMsg + "0#";
		}
		else{
			strMsg = strMsg + this.getEncoding().length() + "#" + this.getEncoding();
		}
		// ontology
		if (this.getOntology() == null){
			strMsg = strMsg + "0#";
		}
		else{
			strMsg = strMsg + this.getOntology().length() + "#" + this.getOntology();
		}
		// protocol
		if (this.getProtocol() == null){
			strMsg = strMsg + "0#";
		}
		else{
			strMsg = strMsg + this.getProtocol().length() + "#" + this.getProtocol();
		}
		// conversation id
		if (this.getConversationId() == null){
			strMsg = strMsg + "0#";
		}
		else{
			strMsg = strMsg + this.getConversationId().length() + "#"
						+ this.getConversationId();
		}
		// reply with
		if (this.getReplyWith() == null){
			strMsg = strMsg + "0#";
		}
		else{
			strMsg = strMsg + this.getReplyWith().length() + "#" + this.getReplyWith();
		}
		// in reply to
		if (this.getInReplyTo() == null){
			strMsg = strMsg + "0#";
		}
		else{
			strMsg = strMsg + this.getInReplyTo().length() + "#" + this.getInReplyTo();
		}
		// reply by
		if (this.getReplyBy() == null){
			strMsg = strMsg + "0#";
		}
		else{
			strMsg = strMsg + this.getReplyBy().length() + "#" + this.getReplyBy();
		}
		// content
		if (this.getContent() == null){
			strMsg = strMsg + "0#";
		}
		else{
			strMsg = strMsg + this.getContent().length() + "#" + this.getContent();
		}
		
		return strMsg;
	}
	
	/**
	 * Parses the message given as a String and creates an object of type ACLMessage.
	 * 
	 * @param strMsg String containing a serialization of an ACLMessage
	 * 
	 * @return An object of type ACLMessage created from the given String
	 * 
	 * @see ACLMessage#toString()
	 */
	public static ACLMessage fromString (String strMsg){
		// Unserialize message content
		ACLMessage msg;
		int indice1 = 0;
		int indice2 = 0;
		int aidindice1 = 0;
		int aidindice2 = 0;
		int tam = 0;
		String aidString;
		
		indice2 = strMsg.indexOf('#', indice1);
		msg = new ACLMessage(Integer.parseInt(strMsg.substring(indice1, indice2)));
		
		// Unserialize different AgentID's (Sender, Receiver, ReplyTo)
		for (int i = 0; i < 3; i++)
		{
			AgentID aid = new AgentID();
			aidindice1 = 0;
			aidindice2 = 0;
			indice1 = indice2 + 1 + tam;
			indice2 = strMsg.indexOf('#', indice1);
			tam = Integer.parseInt(strMsg.substring(indice1, indice2));
			aidString = strMsg.substring(indice2 + 1, indice2 + 1 + tam);
			aidindice2 = aidString.indexOf(':');
			if (aidindice2 - aidindice1 <= 0)
				aid.protocol = "";
			else
				aid.protocol = aidString.substring(aidindice1, aidindice2);
			aidindice1 = aidindice2 + 3;
			aidindice2 = aidString.indexOf('@', aidindice1);
			if (aidindice2 - aidindice1 <= 0)
				aid.name = "";
			else
				aid.name = aidString.substring(aidindice1, aidindice2);
			aidindice1 = aidindice2 + 1;
			aidindice2 = aidString.indexOf(':', aidindice1);
			if (aidindice2 - aidindice1 <= 0)
				aid.host = "";
			else
				aid.host = aidString.substring(aidindice1, aidindice2);
			aid.port = aidString.substring(aidindice2 + 1);
			
			if (i == 0)
				msg.setSender(aid);
			if (i == 1)
				msg.setReceiver(aid);
			if (i == 2)
				msg.setReplyTo(aid);
		}
		indice1 = indice2 + 1 + tam;
		indice2 = strMsg.indexOf('#', indice1);
		tam = Integer.parseInt(strMsg.substring(indice1, indice2));
		// language
		msg.setLanguage(strMsg.substring(indice2 + 1, indice2 + 1 + tam));
		
		indice1 = indice2 + 1 + tam;
		indice2 = strMsg.indexOf('#', indice1);
		tam = Integer.parseInt(strMsg.substring(indice1, indice2));
		// encoding
		msg.setEncoding(strMsg.substring(indice2 + 1, indice2 + 1 + tam));
		
		indice1 = indice2 + 1 + tam;
		indice2 = strMsg.indexOf('#', indice1);
		tam = Integer.parseInt(strMsg.substring(indice1, indice2));
		// ontologyencodingACLMessage template
		msg.setOntology(strMsg.substring(indice2 + 1, indice2 + 1 + tam));
		
		indice1 = indice2 + 1 + tam;
		indice2 = strMsg.indexOf('#', indice1);
		tam = Integer.parseInt(strMsg.substring(indice1, indice2));
		// Protocol
		msg.setProtocol(strMsg.substring(indice2 + 1, indice2 + 1 + tam));
		
		indice1 = indice2 + 1 + tam;
		indice2 = strMsg.indexOf('#', indice1);
		tam = Integer.parseInt(strMsg.substring(indice1, indice2));
		// Conversation id
		msg.setConversationId(strMsg.substring(indice2 + 1, indice2 + 1 + tam));
		
		indice1 = indice2 + 1 + tam;
		indice2 = strMsg.indexOf('#', indice1);
		tam = Integer.parseInt(strMsg.substring(indice1, indice2));
		// Reply with
		msg.setReplyWith(strMsg.substring(indice2 + 1, indice2 + 1 + tam));
		
		indice1 = indice2 + 1 + tam;
		indice2 = strMsg.indexOf("#", indice1);
		
		tam = Integer.parseInt(strMsg.substring(indice1, indice2));
		// In reply to
		msg.setInReplyTo(strMsg.substring(indice2 + 1, indice2 + 1 + tam));
		
		indice1 = indice2 + 1 + tam;
		indice2 = strMsg.indexOf('#', indice1);
		tam = Integer.parseInt(strMsg.substring(indice1, indice2));
		// reply by
		
		if (tam != 0)
			msg.setReplyByDate(new Date(Integer.parseInt(strMsg.substring(indice2 + 10, indice2 + tam))));
		
		indice1 = indice2 + 1 + tam;
		indice2 = strMsg.indexOf('#', indice1);
		tam = Integer.parseInt(strMsg.substring(indice1, indice2));
		// Content
		msg.setContent(strMsg.substring(indice2 + 1, indice2 + 1 + tam));
		
		return msg;
	}
}
