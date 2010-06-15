package es.upv.dsic.gti_ia.core;

/**
 * @author Ricard Lopez Fogues
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
	
	private byte[] byteSequenceContent = null;

	/**
	 * @uml.property name="reply_to"
	 * @uml.associationEnd
	 */
	private AgentID reply_to = new AgentID();
	/**
	 * @uml.property name="content"
	 */
	private String content = "";
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

	public ACLMessage() {
		performative = UNKNOWN;
	}

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
	 * @return Performative type
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

	public int getPerformativeInt() {
		return performative;
	}

	/**
	 * Set sender
	 * 
	 * @param sender
	 * @uml.property name="sender"
	 */
	public void setSender(AgentID sender) {
		this.sender = sender;
	}

	/**
	 * @return sender
	 * @uml.property name="sender"
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
	 * @return First receiver in receivers list
	 * @uml.property name="receiver"
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
	 * Sets ReplyTo field
	 * 
	 * @return
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
		byteSequenceContent = null;
		content = cont;
	}

	/**
	 * @return Content of content field
	 * @uml.property name="content"
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets languange field
	 * 
	 * @param lang
	 * @uml.property name="language"
	 */
	public void setLanguage(String lang) {
		language = lang;
	}

	/**
	 * @return content of languange field
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
	 * @return encoding
	 * @uml.property name="encoding"
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param ontology
	 * @uml.property name="ontology"
	 */
	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	/**
	 * @return ontology
	 * @uml.property name="ontology"
	 */
	public String getOntology() {
		return ontology;
	}

	/**
	 * @param protocol
	 * @uml.property name="protocol"
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @return protocol
	 * @uml.property name="protocol"
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * 
	 * @param id
	 */
	public void setConversationId(String id) {
		conversation_id = id;
	}

	/**
	 * 
	 * @return conversation id
	 */
	public String getConversationId() {
		return conversation_id;
	}

	/**
	 * 
	 * @param reply
	 *            With
	 */
	public void setReplyWith(String rw) {
		reply_with = rw;
	}

	/**
	 * 
	 * @return reply with
	 */
	public String getReplyWith() {
		return reply_with;
	}

	/**
	 * 
	 * @param in
	 *            reply to
	 */
	public void setInReplyTo(String irt) {
		in_reply_to = irt;
	}

	/**
	 * 
	 * @return in reply to
	 */
	public String getInReplyTo() {
		return in_reply_to;
	}

	/**
	 * Default value date.getTime()
	 * 
	 * @param date
	 */
	public void setReplyByDate(Date date) {
		reply_byInMillisec = (date == null ? 0 : date.getTime());
	}

	/**
	 * 
	 * @return reply by time in date format
	 */
	public Date getReplyByDate() {
		if (reply_byInMillisec != 0)
			return new Date(reply_byInMillisec);
		else
			return null;
	}

	/**
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
	 * 
	 * @param performative
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
	 * @param receiver
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
	 * 
	 * @return receivers list
	 */
	public ArrayList<AgentID> getReceiverList() {
		return receiver;
	}

	/**
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

	public void setHeader(String key, String value) {
		headers.put(key, value);
	}

	public String getHeaderValue(String key) {
		if (headers.get(key) != null)
			return headers.get(key);
		else
			return "";
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public boolean headersAreEqual(ACLMessage msg) {
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
	
	public void setContentObject(java.io.Serializable s) throws IOException
	{
		ByteArrayOutputStream c = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(c);
		oos.writeObject(s);
		oos.flush();
		setByteSequenceContent(c.toByteArray());
	}
	
	public void setByteSequenceContent(byte[] content) {
		this.content = null; //make to null the other variable
		byteSequenceContent = content;
	}
	
	public java.io.Serializable getContentObject() throws IOException,
			ClassNotFoundException {
		byte[] data = getByteSequenceContent();
		if (data == null)
			return null;
		ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(
				data));
		java.io.Serializable s = (java.io.Serializable) oin.readObject();
		return s;
	}
	
	public byte[] getByteSequenceContent() {
		if (byteSequenceContent != null)
			return byteSequenceContent;
		return null;
	}
	
	/**
	 Returns the integer corresponding to the performative
	 
	 @returns the integer corresponding to the performative; -1 otherwise
	 */
	public static int getIntegerPerformative(String performative){
		String tmp = performative.toUpperCase();
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
	public static String getStringPerformative(int perf){
		try {
			return performatives[perf];
		} catch (Exception e) {
			return performatives[NOT_UNDERSTOOD];
		}
	}
}
