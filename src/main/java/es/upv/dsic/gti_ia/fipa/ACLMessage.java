package es.upv.dsic.gti_ia.fipa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


import es.upv.dsic.gti_ia.fipa.ISO8601;



/**
 * @author  Ricard Lopez Fogues
 */
@SuppressWarnings("unchecked")
public class ACLMessage implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//miembros
	/** constant identifying the FIPA performative **/
	public static final int ACCEPT_PROPOSAL = 0;
	/** constant identifying the FIPA performative **/
	public static final int AGREE = 1;
	/** constant identifying the FIPA performative **/
	public static final int CANCEL = 2;
	/** constant identifying the FIPA performative **/
	public static final int CFP = 3;
	/** constant identifying the FIPA performative **/
	public static final int CONFIRM = 4;
	/** constant identifying the FIPA performative **/
	public static final int DISCONFIRM = 5;
	/** constant identifying the FIPA performative **/
	public static final int FAILURE = 6;
	/** constant identifying the FIPA performative **/
	public static final int INFORM = 7;
	/** constant identifying the FIPA performative **/
	public static final int INFORM_IF = 8;
	/** constant identifying the FIPA performative **/
	public static final int INFORM_REF = 9;
	/** constant identifying the FIPA performative **/
	public static final int NOT_UNDERSTOOD = 10;
	/** constant identifying the FIPA performative **/
	public static final int PROPOSE = 11;
	/** constant identifying the FIPA performative **/
	public static final int QUERY_IF = 12;
	/** constant identifying the FIPA performative **/
	public static final int QUERY_REF = 13;
	/** constant identifying the FIPA performative **/
	public static final int REFUSE = 14;
	/** constant identifying the FIPA performative **/
	public static final int REJECT_PROPOSAL = 15;
	/** constant identifying the FIPA performative **/
	public static final int REQUEST = 16;
	/** constant identifying the FIPA performative **/
	public static final int REQUEST_WHEN = 17;
	/** constant identifying the FIPA performative **/
	public static final int REQUEST_WHENEVER = 18;
	/** constant identifying the FIPA performative **/
	public static final int SUBSCRIBE = 19;
	/** constant identifying the FIPA performative **/
	public static final int PROXY = 20;
	/** constant identifying the FIPA performative **/
	public static final int PROPAGATE = 21;
	/** constant identifying an unknown performative **/
	public static final int UNKNOWN = -1;
	
	private static final String[] performatives = new String[22];
	static { // initialization of the Vector of performatives
		performatives[ACCEPT_PROPOSAL]="ACCEPT-PROPOSAL";
		performatives[AGREE]="AGREE";
		performatives[CANCEL]="CANCEL";
		performatives[CFP]="CFP";
		performatives[CONFIRM]="CONFIRM";
		performatives[DISCONFIRM]="DISCONFIRM";
		performatives[FAILURE]="FAILURE";
		performatives[INFORM]="INFORM";
		performatives[INFORM_IF]="INFORM-IF";
		performatives[INFORM_REF]="INFORM-REF";
		performatives[NOT_UNDERSTOOD]="NOT-UNDERSTOOD";
		performatives[PROPOSE]="PROPOSE";
		performatives[QUERY_IF]="QUERY-IF";
		performatives[QUERY_REF]="QUERY-REF";
		performatives[REFUSE]="REFUSE";
		performatives[REJECT_PROPOSAL]="REJECT-PROPOSAL";
		performatives[REQUEST]="REQUEST";
		performatives[REQUEST_WHEN]="REQUEST-WHEN";
		performatives[REQUEST_WHENEVER]="REQUEST-WHENEVER";
		performatives[SUBSCRIBE]="SUBSCRIBE";
		performatives[PROXY]="PROXY";
		performatives[PROPAGATE]="PROPAGATE";
	}
	
	
	/**
	 * @uml.property  name="performative"
	 */
	private int performative;
	/**
	 * @uml.property  name="sender"
	 * @uml.associationEnd  
	 */
	private AgentID sender = new AgentID();
	//private AgentID receiver = new AgentID();
	
	/*
	 * List of receivers to enable Multi-Cast
	 */
	
	/**
	 * @uml.property  name="receiver"
	 */
	private ArrayList<AgentID> receiver = new ArrayList();
	
	/**
	 * @uml.property  name="reply_to"
	 * @uml.associationEnd  
	 */
	private AgentID reply_to = new AgentID();
	/**
	 * @uml.property  name="content"
	 */
	private String content = "";
	/**
	 * @uml.property  name="language"
	 */
	private String language = "";
	/**
	 * @uml.property  name="encoding"
	 */
	private String encoding = "";
	/**
	 * @uml.property  name="ontology"
	 */
	private String ontology = "";
	/**
	 * @uml.property  name="protocol"
	 */
	private String protocol = "";
	/**
	 * @uml.property  name="conversation_id"
	 */
	private String conversation_id = "";
	/**
	 * @uml.property  name="reply_with"
	 */
	private String reply_with = "";
	/**
	 * @uml.property  name="in_reply_to"
	 */
	private String in_reply_to = "";
	/**
	 * @uml.property  name="reply_byInMillisec"
	 */
	private long reply_byInMillisec = 0;
	
	
	//constructores
	/*public ACLMessage(){
		performative = UNKNOWN;
	}*/
	
	public ACLMessage(int performative)
	{
		this.performative = performative;		
	}
	
	/**
	 * Sets performative type
	 * @param performative
	 * @uml.property  name="performative"
	 */
	public void setPerformative(int performative){
		this.performative = performative;
	}
	
	/**
	 * @return Performative type
	 * @uml.property  name="performative"
	 */
	public String getPerformative(){
		try {
			return performatives[performative];
		} catch (Exception e) {
			return performatives[NOT_UNDERSTOOD];
		}
	}
	
	public int getPerformativeInt(){
		return performative;
	}
	
	/**
	 * Set sender
	 * @param sender
	 * @uml.property  name="sender"
	 */
	public void setSender(AgentID sender){
		this.sender = sender;
	}
	
	/**
	 * @return sender
	 * @uml.property  name="sender"
	 */
	public AgentID getSender(){
		return sender;
	}
	
	/**
	 * Set the receiver. It deletes the receivers list and creates a new one with
	 * the new receiver
	 * @param receiver
	 */
	public void setReceiver(AgentID receiver){
		this.receiver.clear();
		this.receiver.add(receiver);
	}
	
	/**
	 * @return First receiver in receivers list
	 * @uml.property  name="receiver"
	 */
	public AgentID getReceiver(){
		return receiver.get(0);
	}
	
	/**
	 * Returns receiver in index position in receivers list
	 * @param index
	 * @return receiver
	 */
	public AgentID getReceiver(int index){
		return receiver.get(index);
	}
	
	/**
	 * Sets ReplyTo field
	 * @param reply
	 */
	public void setReplyTo(AgentID reply){
		reply_to = reply;
	}
	
	/**
	 * Sets ReplyTo field
	 * @return
	 */
	public AgentID getReplyTo(){
		return reply_to;
	}
	
	/**
	 * Sets content field.
	 * @param cont
	 * @uml.property  name="content"
	 */
	public void setContent(String cont){
		content = cont;
	}
	
	/**
	 * @return Content of content field
	 * @uml.property  name="content"
	 */
	public String getContent(){
		return content;
	}
	
	/**
	 * Sets languange field
	 * @param lang
	 * @uml.property  name="language"
	 */
	public void setLanguage(String lang){
		language = lang;
	}
	
	/**
	 * @return content of languange field
	 * @uml.property  name="language"
	 */
	public String getLanguage(){
		return language;
	}
	
	/**
	 * Set encoding of the message
	 * @param encoding
	 * @uml.property  name="encoding"
	 */
	public void setEncoding(String encoding){
		this.encoding = encoding;
	}
	
	/**
	 * @return
	 * @uml.property  name="encoding"
	 */
	public String getEncoding(){
		return encoding;
	}
	
	/**
	 * @param ontology
	 * @uml.property  name="ontology"
	 */
	public void setOntology(String ontology){
		this.ontology = ontology;
	}
	
	/**
	 * @return
	 * @uml.property  name="ontology"
	 */
	public String getOntology(){
		return ontology;
	}
	
	/**
	 * @param protocol
	 * @uml.property  name="protocol"
	 */
	public void setProtocol(String protocol){
		this.protocol = protocol;
	}
	
	/**
	 * @return
	 * @uml.property  name="protocol"
	 */
	public String getProtocol(){
		return protocol;
	}
	
	public void setConversationId(String id){
		conversation_id = id;
	}
	
	public String getConversationId(){
		return conversation_id;
	}
	
	public void setReplyWith(String rw){
		reply_with = rw;
	}
	
	public String getReplyWith(){
		return reply_with;
	}
	
	public void setInReplyTo(String irt){
		in_reply_to = irt;
	}
	
	public String getInReplyTo(){
		return in_reply_to;
	}
	
	public void setReplyByDate(Date date) {
		reply_byInMillisec = (date==null?0:date.getTime());
	}
	
	public Date getReplyByDate() {
		if(reply_byInMillisec != 0)
			return new Date(reply_byInMillisec);
		else
			return null;
	}
	
	
	public String getReplyBy() {
		if(reply_byInMillisec != 0)
			return ISO8601.toString(new Date(reply_byInMillisec));
		else
			return "";
	}
	
	public void setPerformative(String performative){
		for(int i=0; i< performatives.length; i++){
			if(performative.compareTo(performatives[i]) == 0){
				this.performative = i;
				break;
			}
		}
	}
	
	public void clearAllReceiver() {
		this.receiver.clear();
	}
	
	/**
	 * A�ade un receiver a la lista. �til para hacer multiples envios.
	 * @param r
	 * Devuelve -1, si el receiver ya estaba en la lista. 1 en caso contrario
	 */
	/**
	 * A�ade un receiver a la lista. �til para hacer multiples envios.
	 * @param r
	 * Devuelve -1, si el receiver ya estaba en la lista. 1 en caso contrario
	 */
	public int addReceiver(AgentID r)
	{
		//Comprovem si existeix l'agent, si existeix tornem -1
		for(int i = 0; i<receiver.size(); i++)
		{
			if( receiver.get(i).name.equals(r.name) && receiver.get(i).host.equals(r.host) 
				&& receiver.get(i).port.equals(r.port) && receiver.get(i).protocol.equals(r.protocol))
				{
					return -1;
				}
		}
		receiver.add(r);
		return 1;
	}
	
	public ArrayList<AgentID> getReceiverList() {
		return receiver;
	}

	/*public void setReceiver_list(ArrayList<AgentID> receiver_list) {
		this.receiver_list = receiver_list;
	}*/
	public int getTotalReceivers(){
		return receiver.size();
	}

	/**
	 * @return
	 * @uml.property  name="reply_to"
	 */
	public AgentID getReply_to() {
		return reply_to;
	}

	/**
	 * @param reply_to
	 * @uml.property  name="reply_to"
	 */
	public void setReply_to(AgentID reply_to) {
		this.reply_to = reply_to;
	}

	/**
	 * @return
	 * @uml.property  name="conversation_id"
	 */
	public String getConversation_id() {
		return conversation_id;
	}

	/**
	 * @param conversation_id
	 * @uml.property  name="conversation_id"
	 */
	public void setConversation_id(String conversation_id) {
		this.conversation_id = conversation_id;
	}

	/**
	 * @return
	 * @uml.property  name="reply_with"
	 */
	public String getReply_with() {
		return reply_with;
	}

	/**
	 * @param reply_with
	 * @uml.property  name="reply_with"
	 */
	public void setReply_with(String reply_with) {
		this.reply_with = reply_with;
	}

	/**
	 * @return
	 * @uml.property  name="in_reply_to"
	 */
	public String getIn_reply_to() {
		return in_reply_to;
	}

	/**
	 * @param in_reply_to
	 * @uml.property  name="in_reply_to"
	 */
	public void setIn_reply_to(String in_reply_to) {
		this.in_reply_to = in_reply_to;
	}

	/**
	 * @return
	 * @uml.property  name="reply_byInMillisec"
	 */
	public long getReply_byInMillisec() {
		return reply_byInMillisec;
	}

	/**
	 * @param reply_byInMillisec
	 * @uml.property  name="reply_byInMillisec"
	 */
	public void setReply_byInMillisec(long reply_byInMillisec) {
		this.reply_byInMillisec = reply_byInMillisec;
	}
	

	public synchronized Object clone() {
		ACLMessage result;
		
		try{
			result = (ACLMessage)super.clone();
			
		}catch(CloneNotSupportedException cnse) {
			throw new InternalError(); // This should never happen
		}
		
		return result;
	}
	
	
	public ACLMessage createReply() {
		ACLMessage m = (ACLMessage)clone();
		
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
		//#CUSTOM_EXCLUDE_BEGIN
		//Set the Aclrepresentation of the reply message to the aclrepresentation of the sent message 
	
		//#CUSTOM_EXCLUDE_END
		return m;
	}
}
