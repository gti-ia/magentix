

/**
 * The class messageTemplate create a new template to compare messages.
 * 
 * @author  Joan Bellver Faus, GTI-IA, DSIC, UPV
 * @version 2009.9.07
 */


package es.upv.dsic.gti_ia.proto;

import java.util.ArrayList;
import java.util.Date;

import es.upv.dsic.gti_ia.proto.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.fipa.AgentID;
import es.upv.dsic.gti_ia.fipa.ACLMessage;

public class MessageTemplate {
	
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
	
	
	private int performative = -2;
	private AgentID sender = new AgentID();
	private AgentID receiver = new AgentID();
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
	
	private ArrayList<AgentID> receiver_list = new ArrayList<AgentID>();
	private ArrayList<String> listaConversaciones = new ArrayList<String>(); 
	
	
	//constructores
	/*public ACLMessage(){
		performative = UNKNOWN;
	}*/
	
	
	
    /**
     * Create a FIPARequestInitiator.
     * @param iprotocol    
     */
	
	public MessageTemplate(String iprotocol)
	{
		if (iprotocol.equals(InteractionProtocol.FIPA_REQUEST))
		{
			this.performative = ACLMessage.REQUEST;
			this.protocol = InteractionProtocol.FIPA_REQUEST;
		}
		else
			this.protocol =  iprotocol;
				
		
		
		
		
	}
	
	/**
	 * Return Conversations list
	 * @return
	 */
	public ArrayList<String> getList_Conversation()
	{
		
		return this.listaConversaciones;
	}
	
	/**
	 * Add new conversation id in the list of conversations
	 * @param conversacion
	 */
	public void addConversation(String conversacion)
	{
		this.listaConversaciones.add(conversacion);
		
	}
	/**
	 * Delete all conversations in the list of conversations
	 * @return
	 */
	public boolean deleteAllConversation()
	{
		this.listaConversaciones.clear();
		if (this.listaConversaciones.size()==0)
			return true;
		else
			return false;
		
	}
	
	/**
	 * Delete a conversation id in the list of conversations
	 * @param conversacion
	 */
	public void deleteConversation(String conversation)
	{
		for(String conv : this.listaConversaciones){
			if (conv.equals(conversation))
			{
				this.listaConversaciones.remove(conversation);
				break;
			}
			
		}
		
	}
	
	/**
	 * Add a new receiver
	 * @param a AgentID
	 * @return status 1: OK -1: error
	 */
	public int add_receiver(AgentID a)
	{
		for(int i = 0; i<receiver_list.size(); i++)
		{
			if( receiver_list.get(i).name.equals(a.name) && receiver_list.get(i).host.equals(a.host) 
				&& receiver_list.get(i).port.equals(a.port) && receiver_list.get(i).protocol.equals(a.protocol))
				{
					return -1;
				}
		}
		receiver_list.add(a);
		return 1;
	}
	
	/**
	 * Indicates if a receiver exists in the list of receivers
	 * @param a AgentID
	 * @return
	 */
	public boolean existReceiver(AgentID a)
	{
		for(int i = 0; i<receiver_list.size(); i++)
		{
			if( receiver_list.get(i).name.equals(a.name) && receiver_list.get(i).host.equals(a.host) 
				&& receiver_list.get(i).port.equals(a.port) && receiver_list.get(i).protocol.equals(a.protocol))
				{
					return true;
				}
		}
		return false;
	}
	
	/**
	 * Delete all receivers in the list of receivers
	 * @return
	 */
	public boolean deleteAllReceiver()
	{
		this.receiver_list.clear();
		if (this.receiver_list.size()==0)
			return true;
		else
			return false;
	}
	
	/**
	 * Return Receiver list
	 * @return
	 */
	public ArrayList<AgentID> getReceiver_list() {
		return receiver_list;
	}

	/**
	 * Set a new receiver list
	 * @param receiver_list
	 */
	public void setReceiver_list(ArrayList<AgentID> receiver_list) {
		this.receiver_list = receiver_list;
	}
	
	/**
	 * Set a new performative
	 * @param performative 
	 */
	public void setPerformative(int performative){
		this.performative = performative;
	}
	
	/**
	 * Set a new performative 
	 * @param performative 
	 */
	public void setPerformative(String performative){
		for(int i=0; i< performatives.length; i++){
			if(performative.compareTo(performatives[i]) == 0 ){
				this.performative = i;
				break;
			}
		}
	}
	
	/**
	 * 
	 * @return performative
	 */
	public String getPerformative(){
		try {
			return performatives[performative];
		} catch (Exception e) {
			return performatives[NOT_UNDERSTOOD];
		}
	}
	
	/**
	 * 
	 * @return performative
	 */
	public int getPerformativeInt(){
		return performative;
	}
	
	/**
	 * 
	 * @param sender
	 */
	public void setSender(AgentID sender){
		this.sender = sender;
	}
	
	/**
	 * 
	 * @return sender 
	 */
	public AgentID getSender(){
		return sender;
	}
	
	/**
	 * 
	 * @param receiver
	 */
	public void setReceiver(AgentID receiver){
		this.receiver = receiver;
	}
	
	/**
	 * 
	 * @return receiver 
	 */
	public AgentID getReceiver(){
		return receiver;
	}
	
	/**
	 * 
	 * @param reply 
	 */
	public void setReplyTo(AgentID reply){
		reply_to = reply;
	}
	
	/**
	 * 
	 * @return reply_to
	 */
	public AgentID getReplyTo(){
		return reply_to;
	}
	
	/**
	 * 
	 * @param content
	 */
	public void setContent(String cont){
		content = cont;
	}
	
	/**
	 * 
	 * @return content 
	 */
	public String getContent(){
		return content;
	}
	
	/**
	 * 
	 * @param language 
	 */
	public void setLanguage(String lang){
		language = lang;
	}
	
	/**
	 * 
	 * @return language 
	 */
	public String getLanguage(){
		return language;
	}
	
	/**
	 * 
	 * @param encoding 
	 */
	public void setEncoding(String encoding){
		this.encoding = encoding;
	}
	
	/**
	 * 
	 * @return encoding 
	 */
	public String getEncoding(){
		return encoding;
	}
	
	/**
	 * 
	 * @param ontology 
	 */
	public void setOntology(String ontology){
		this.ontology = ontology;
	}
	
	/**
	 * 
	 * @return ontology 
	 */
	public String getOntology(){
		return ontology;
	}
	
	/**
	 * 
	 * @param protocol 
	 */
	public void setProtocol(String protocol){
		this.protocol = protocol;
	}
	
	/**
	 * 
	 * @return protocol
	 */
	public String getProtocol(){
		return protocol;
	}
	
	/**
	 * 
	 * @param conversation id
	 */
	public void setConversationId(String id){
		conversation_id = id;
	}
	
	/**
	 * 
	 * @return conversation id
	 */
	public String getConversationId(){
		return conversation_id;
	}
	/**
	 * 
	 * @param reply with
	 */
	public void setReplyWith(String rw){
		reply_with = rw;
	}
	/**
	 * 
	 * @return reply_with
	 */
	public String getReplyWith(){
		return reply_with;
	}
	
	/**
	 * 
	 * @param irt in reply to
	 */
	public void setInReplyTo(String irt){
		in_reply_to = irt;
	}
	
	/**
	 * 
	 * @return in_reply_tp
	 */
	public String getInReplyTo(){
		return in_reply_to;
	}
	
	/**
	 * 
	 * @param date
	 */
	public void setReplyByDate(Date date) {
		reply_byInMillisec = (date==null?0:date.getTime());
	}
	
	/**
	 * 
	 * @return date
	 */
	public String getReplyBy() {
		if(reply_byInMillisec != 0)
			return new Date(reply_byInMillisec).toString();
		else
			return "";
	}
	
	/**
	 * 
	 * @return date
	 */
	public Date getReplyByDate() {
		if(reply_byInMillisec != 0)
			return new Date(reply_byInMillisec);
		else
			return null;
	}

}
