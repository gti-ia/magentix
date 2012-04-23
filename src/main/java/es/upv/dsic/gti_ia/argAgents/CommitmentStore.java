package es.upv.dsic.gti_ia.argAgents;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Argument;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Dialogue;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Position;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.CommitmentStore_Protocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 * This agent, named CommitmentStore, stores all the information about the dialogues, including: 
 * positions, arguments and modification times.
 * It responds to the petitions of the agents in the dialogue process.
 * @author Jaume Jordan
 *
 */
public class CommitmentStore extends CAgent {
	
	HashMap<String,Dialogue> dialogues;
	HashMap<String,HashMap<String,ArrayList<Argument>>> arguments;
	HashMap<String,HashMap<String,Position>> positions;
	
	HashMap<String,Long> lastModificationDates;
	
	private final String ADDARGUMENT="ADDARGUMENT";
	private final String REMOVEARGUMENT="REMOVEARGUMENT";
	private final String ADDPOSITION="ADDPOSITION";
	private final String GETPOSITION="GETPOSITION";
	private final String GETALLPOSITIONS="GETALLPOSITIONS";
	private final String NOCOMMIT="NOCOMMIT";
	private final String ASSERT="ASSERT";
	private final String ATTACK="ATTACK";
	private final String ADDDIALOGUE="ADDDIALOGUE";
	private final String GETDIALOGUE="GETDIALOGUE";
	private final String ENTERDIALOGUE="ENTERDIALOGUE";
	private final String WITHDRAWDIALOGUE="WITHDRAWDIALOGUE";

	/**
	 * Constructor of the CommitmentStore. It takes the agent id as a parameter 
	 * and it initializes all the structures.
	 * @param aid Agent identifier
	 * @throws Exception
	 */
	public CommitmentStore(AgentID aid) throws Exception {
		super(aid);
		
		this.arguments = new HashMap<String, HashMap<String,ArrayList<Argument>>>();
		this.positions = new HashMap<String, HashMap<String,Position>>();
		this.dialogues = new HashMap<String, Dialogue>();
		this.lastModificationDates = new HashMap<String, Long>();
	}

	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		logger.info("+++++++++++++++++++++++++++++++++++++++ "+this.getName()+": Finalizing");
	}

	@Override
	protected void execution(CProcessor firstProcessor,
			ACLMessage welcomeMessage) {
		
		/**
		 * This class extends the CommitmentStore protocol to do the necessary 
		 * functionalities of this agent implementing the abstract methods.
		 * @author Jaume Jordan
		 *
		 */
		class myCSProtocol extends CommitmentStore_Protocol{

			@Override
			protected ACLMessage doRespond(CProcessor myProcessor, ACLMessage msg) {
				ACLMessage response=null;
				logger.info(getAid().name+": "+"message received: "+"from: "+msg.getSender().getLocalName()+" dialogueID: "+msg.getConversationId()+" locution: "+msg.getHeaderValue("locution"));
				String locution=msg.getHeaderValue("locution");
				try{
					
				if(locution.equalsIgnoreCase("LASTMODIFICATIONDATE")){
					Long lastDate=lastModificationDates.get(msg.getConversationId());
					Long millisDifference=System.currentTimeMillis()-lastDate;
					response=createMessage(msg.getSender().getLocalName(), "LASTMODIFICATIONDATE", msg.getConversationId(), millisDifference);
				}
				else if(locution.equalsIgnoreCase(ADDARGUMENT) || locution.equalsIgnoreCase(ATTACK) ||
						locution.equalsIgnoreCase(ASSERT)){
					lastModificationDates.put(msg.getConversationId(), System.currentTimeMillis());
					Argument arg=(Argument)msg.getContentObject();
					addArgument(arg, msg.getSender().getLocalName(), msg.getConversationId());
				}
				else if(locution.equalsIgnoreCase(REMOVEARGUMENT)){
					lastModificationDates.put(msg.getConversationId(), System.currentTimeMillis());
					Argument arg=(Argument)msg.getContentObject();
					removeArgument(arg, msg.getSender().getLocalName(),msg.getConversationId());
				}
				else if(locution.equalsIgnoreCase(ADDPOSITION)){
					lastModificationDates.put(msg.getConversationId(), System.currentTimeMillis());
					Position pos=(Position)msg.getContentObject();
					addPosition(pos, msg.getSender().getLocalName(),msg.getConversationId());
				}
				else if(locution.equalsIgnoreCase(GETPOSITION)){
					Position pos=getPosition(msg.getHeaderValue("agentID"), msg.getConversationId());
					response=createMessage(msg.getSender().getLocalName(), GETPOSITION, msg.getConversationId(), pos);
				}
				else if(locution.equalsIgnoreCase(GETALLPOSITIONS)){
					ArrayList<Position> allPositions=getAllPositions(msg.getConversationId(),msg.getSender().getLocalName());
					response=createMessage(msg.getSender().getLocalName(), GETALLPOSITIONS, msg.getConversationId(), allPositions);
				}
				else if(locution.equalsIgnoreCase(NOCOMMIT)){
					lastModificationDates.put(msg.getConversationId(), System.currentTimeMillis());
					removePosition(msg.getSender().getLocalName(), msg.getConversationId());
				}
				else if(locution.equalsIgnoreCase(ADDDIALOGUE)){
					lastModificationDates.put(msg.getConversationId(), System.currentTimeMillis());
					Dialogue dialogue=(Dialogue) msg.getContentObject();
					addDialogue(dialogue);
				}
				else if(locution.equalsIgnoreCase(GETDIALOGUE)){
					String dialogueID=msg.getConversationId();
					Dialogue dialogue=getDialogue(dialogueID);
					
					response=createMessage(msg.getSender().getLocalName(),GETDIALOGUE,dialogueID,dialogue);
				}
				else if(locution.equalsIgnoreCase(ENTERDIALOGUE)){
					lastModificationDates.put(msg.getConversationId(), System.currentTimeMillis());
					Dialogue dialogue=dialogues.get(msg.getConversationId());
					dialogue.addAgentID(msg.getSender().getLocalName());
					dialogues.put(msg.getConversationId(), dialogue);
				}
				else if(locution.equalsIgnoreCase(WITHDRAWDIALOGUE)){
					lastModificationDates.put(msg.getConversationId(), System.currentTimeMillis());
					Dialogue dialogue=dialogues.get(msg.getConversationId());
					dialogue.removeAgentID(msg.getSender().getLocalName());
					dialogues.put(msg.getConversationId(), dialogue);
				}
				else{
					logger.info(getAid().name+": not understood");
				}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				return response;
			}

			@Override
			protected void doDie(CProcessor myProcessor) {
				
				myProcessor.getMyAgent().Shutdown();
				
			}
			
		}
		
		CFactory talk = new myCSProtocol().newFactory("TALK", 1, this);
		
		this.addFactoryAsParticipant(talk);
	}
	
	/**
	 * Creates and returns an {@link ACLMessage} to send with the specified parameters. 
	 * @param agentID Agent identifier of the receiver
	 * @param locution Locution of the message
	 * @param conversationID Conversation identifier
	 * @param contentObject Content object if it is necessary
	 * @return Returns an {@link ACLMessage}
	 */
	private ACLMessage createMessage(String agentID, String locution, String conversationID, Serializable contentObject){
		
		ACLMessage msg = new ACLMessage();
		msg.setSender(getAid());
		msg.setReceiver(new AgentID(agentID));
		msg.setHeader("locution", locution);
		msg.setConversationId(conversationID);
		msg.setPerformative(ACLMessage.INFORM);
		try {
			msg.setContentObject(contentObject);
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info(this.getName()+": "+"message to send to: "+msg.getReceiver().toString()+" dialogueID: "+msg.getConversationId()+" locution: "+msg.getHeaderValue("locution"));
		
		return msg;
	
	}
	
	/**
	 * Adds an {@link Argument} to the corresponding list of the dialogue and the agent
	 * @param arg {@link Argument} to store
	 * @param agentID Agent identifier of the agent that use the {@link Argument}
	 * @param dialogueID Dialogue identifier where the {@link Argument} is used
	 */
	private void addArgument(Argument arg, String agentID, String dialogueID){
		HashMap<String,ArrayList<Argument>> agentArg=arguments.get(agentID);
		if(agentArg==null)
			agentArg=new HashMap<String, ArrayList<Argument>>();
		ArrayList<Argument> aList = agentArg.get(dialogueID);
		if (aList == null)
			aList = new ArrayList<Argument>();
		aList.add(arg);
		agentArg.put(dialogueID, aList);
		
		arguments.put(agentID, agentArg);
	}
	
	/**
	 * Returns the arguments that has used an agent in a dialogue.
	 * @param agentID Agent identifier
	 * @param dialogueID Dialogue identifier
	 * @return arguments that has used an agent in a dialogue
	 */
	private ArrayList<Argument> getArguments(String agentID, String dialogueID){
		HashMap<String,ArrayList<Argument>> agentArg=arguments.get(agentID);
		if(agentArg==null)
			return null;
		ArrayList<Argument> args=agentArg.get(dialogueID);
		return args;
	}
	
	/**
	 * Removes an {@link Argument} that has used an agent in a dialogue.
	 * @param arg {@link Argument} to remove
	 * @param agentID Agent identifier of the agent that use the {@link Argument}
	 * @param dialogueID Dialogue identifier where the Argument is used
	 */
	private void removeArgument(Argument arg, String agentID, String dialogueID){
		ArrayList<Argument> argumentsList=getArguments(agentID, dialogueID);
		if(argumentsList!=null){
			for(int i=0;i<argumentsList.size();i++){
				if(argumentsList.get(i).getID()==arg.getID()){
					argumentsList.remove(i);
					break;
				}
			}
		}
		
		HashMap<String,ArrayList<Argument>> agentArg=arguments.get(agentID);
		agentArg.put(dialogueID, argumentsList);
		arguments.put(agentID, agentArg);
	}
	
	/**
	 * Adds a {@link Position} to the corresponding list of the dialogue and the agent
	 * @param pos {@link Position} to store
	 * @param agentID Agent identifier of the agent that has the {@link Position}
	 * @param dialogueID Dialogue identifier where the {@link Position} is used
	 */
	private void addPosition(Position pos, String agentID, String dialogueID){
		//check if there is a previous position
		HashMap<String,Position> agentPos=positions.get(agentID);
		if(agentPos==null)
			agentPos=new HashMap<String, Position>();
		agentPos.put(dialogueID, pos);
		positions.put(agentID, agentPos);
	}
	
	/**
	 * Returns the {@link Position} of the given agent in the given dialogue
	 * @param agentID agent identifier
	 * @param dialogueID dialogue identifier
	 * @return the {@link Position} of the given agent in the given dialogue
	 */
	private Position getPosition(String agentID, String dialogueID){
		HashMap<String,Position> agentPos=positions.get(agentID);
		if(agentPos==null)
			return null;
		Position pos=agentPos.get(dialogueID);
		return pos;
	}
	
	/**
	 * Returns all the positions of a dialogue 
	 * (it does not includes the position of the agent specified in the second parameter, normally the requester)
	 * @param dialogueID dialogue identifier
	 * @param myAgentID agent identifier
	 * @return all the positions of the given dialogue
	 */
	private ArrayList<Position> getAllPositions(String dialogueID, String myAgentID){
		ArrayList<Position> positionsList=new ArrayList<Position>();
		Dialogue dialogue=dialogues.get(dialogueID);
		ArrayList<String> agentIDs=dialogue.getAgentIDs();
		Iterator<String> iterAgentIDs=agentIDs.iterator();
		while(iterAgentIDs.hasNext()){
			String agentID=iterAgentIDs.next();
			//not include the position of the agent that request
			if(agentID.equalsIgnoreCase(myAgentID))
				continue;
			HashMap<String,Position> agentPositions=positions.get(agentID);
			if(agentPositions!=null){
				Position pos=agentPositions.get(dialogueID);
				if(pos!=null)
					positionsList.add(pos);
			}
		}
		return positionsList;
	}
	
	/**
	 * Removes the positions of the given agent in the given dialogue
	 * @param agentID agent identifier
	 * @param dialogueID dialogue identifier
	 */
	private void removePosition(String agentID, String dialogueID){
		HashMap<String,Position> agentPositions=positions.get(agentID);
		
		agentPositions.remove(dialogueID);
		positions.put(agentID, agentPositions);
	}
	
	/**
	 * Adds a dialogue to the dialogue {@link HashMap}
	 * @param dialogue
	 */
	private void addDialogue(Dialogue dialogue){
		dialogues.put(dialogue.getDialogueID(), dialogue);
	}
	
	/**
	 * Returns the {@link Dialogue} specified by the identifier
	 * @param dialogueID dialogue identifier
	 * @return the {@link Dialogue} specified by the identifier
	 */
	private Dialogue getDialogue(String dialogueID){
		Dialogue dialogue = dialogues.get(dialogueID);
		return dialogue;
	}
	
	

}
