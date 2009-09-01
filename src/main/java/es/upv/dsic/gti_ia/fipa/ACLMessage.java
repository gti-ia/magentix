package es.upv.dsic.gti_ia.fipa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


@SuppressWarnings("unchecked")
public class ACLMessage implements Serializable {
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
	
	
	private int performative;
	private AgentID sender = new AgentID();
	private AgentID receiver = new AgentID();
	
	/*
	 * List of receivers to enable Multi-Cast
	 */
	
	@SuppressWarnings("unused")
	private ArrayList<AgentID> receiver_list = new ArrayList();
	
	private AgentID reply_to = new AgentID();
	private String content = "";
	private String language = "";
	private String encoding = "";
	private String ontology = "";
	private String protocol = "";
	private String conversation_id = "";
	private String reply_with = "";
	private String in_reply_to = "";
	private long reply_byInMillisec = 0;
	
	
	//constructores
	/*public ACLMessage(){
		performative = UNKNOWN;
	}*/
	
	public ACLMessage(int performative)
	{
		this.performative = performative;		
	}
	
	public void setPerformative(int performative){
		this.performative = performative;
	}
	
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
	
	public void setSender(AgentID sender){
		this.sender = sender;
	}
	
	public AgentID getSender(){
		return sender;
	}
	
	public void setReceiver(AgentID receiver){
		this.receiver = receiver;
	}
	
	public AgentID getReceiver(){
		return receiver;
	}
	
	public void setReplyTo(AgentID reply){
		reply_to = reply;
	}
	
	public AgentID getReplyTo(){
		return reply_to;
	}
	
	public void setContent(String cont){
		content = cont;
	}
	
	public String getContent(){
		return content;
	}
	
	public void setLanguage(String lang){
		language = lang;
	}
	
	public String getLanguage(){
		return language;
	}
	
	public void setEncoding(String encoding){
		this.encoding = encoding;
	}
	
	public String getEncoding(){
		return encoding;
	}
	
	public void setOntology(String ontology){
		this.ontology = ontology;
	}
	
	public String getOntology(){
		return ontology;
	}
	
	public void setProtocol(String protocol){
		this.protocol = protocol;
	}
	
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
	
	public String getReplyBy() {
		if(reply_byInMillisec != 0)
			return new Date(reply_byInMillisec).toString();
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
	/**
	 * Añade un receiver a la lista. Útil para hacer multiples envios.
	 * @param r
	 * Devuelve -1, si el receiver ya estaba en la lista. 1 en caso contrario
	 */
	public int add_receiver(AgentID r)
	{
		for(int i = 0; i<receiver_list.size(); i++)
		{
			if( receiver_list.get(i).name.equals(r.name) && receiver_list.get(i).host.equals(r.host) 
				&& receiver_list.get(i).port.equals(r.port) && receiver_list.get(i).protocol.equals(r.protocol))
				{
					return -1;
				}
		}
		receiver_list.add(r);
		return 1;
	}
	public ArrayList<AgentID> getReceiver_list() {
		return receiver_list;
	}

	public void setReceiver_list(ArrayList<AgentID> receiver_list) {
		this.receiver_list = receiver_list;
	}

	public AgentID getReply_to() {
		return reply_to;
	}

	public void setReply_to(AgentID reply_to) {
		this.reply_to = reply_to;
	}

	public String getConversation_id() {
		return conversation_id;
	}

	public void setConversation_id(String conversation_id) {
		this.conversation_id = conversation_id;
	}

	public String getReply_with() {
		return reply_with;
	}

	public void setReply_with(String reply_with) {
		this.reply_with = reply_with;
	}

	public String getIn_reply_to() {
		return in_reply_to;
	}

	public void setIn_reply_to(String in_reply_to) {
		this.in_reply_to = in_reply_to;
	}

	public long getReply_byInMillisec() {
		return reply_byInMillisec;
	}

	public void setReply_byInMillisec(long reply_byInMillisec) {
		this.reply_byInMillisec = reply_byInMillisec;
	}
}
