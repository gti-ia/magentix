package Argumentation_Example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import es.upv.dsic.gti_ia.argAgents.CommitmentStore;
import es.upv.dsic.gti_ia.argAgents.argCBR.ArgCBR;
import es.upv.dsic.gti_ia.argAgents.domainCBR.DomainCBR;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.AcceptabilityState;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgNode;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgNode.NodeType;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Argument;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentJustification;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentProblem;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentSolution;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentSolution.ArgumentType;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentationScheme;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Dialogue;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DialogueGraph;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainContext;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Group;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Position;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Premise;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Problem;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SimilarArgumentCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SimilarDomainCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialContext;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialContext.DependencyRelation;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialEntity;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Solution;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SupportSet;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.Argumentation_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

public class ArgCAgent extends CAgent{
	
	private final int multTimeFactor=10; //1000 will be seconds
	private boolean die=false;
	private boolean lastLocutionOpenDialogue=false; //just to avoid excessive log messages of OPENDIALOGUE waiting
	private final String OPENDIALOGUE="OPENDIALOGUE";
	private final String ENTERDIALOGUE="ENTERDIALOGUE";
	private final String LEAVEDIALOGUE="LEAVEDIALOGUE";
//	private final String WITHDRAWDIALOGUE="WITHDRAWDIALOGUE";
//	private final String PROPOSE="PROPOSE";
	private final String WHY="WHY";
	private final String NOCOMMIT="NOCOMMIT";
	private final String ASSERT="ASSERT";
	private final String ACCEPT="ACCEPT";
	private final String ATTACK="ATTACK";
	private final String RETRACT="RETRACT";
	
	private final String ADDARGUMENT="ADDARGUMENT";
//	private final String GETARGUMENT="GETARGUMENT";
	private final String REMOVEARGUMENT="REMOVEARGUMENT";
	private final String ADDPOSITION="ADDPOSITION";
	private final String GETPOSITION="GETPOSITION";
	private final String GETALLPOSITIONS="GETALLPOSITIONS";
	private final String REMOVEPOSITION="REMOVEPOSITION";
	private final String ADDDIALOGUE="ADDDIALOGUE";
//	private final String GETDIALOGUE="GETDIALOGUE";
	
	private final String FINISHDIALOGUE="FINISHDIALOGUE";
	
	private String myID;
	private boolean initiator=false;
	private ArrayList<String> preferedValues;
	private SocialEntity mySocialEntity;
	private ArrayList<SocialEntity> myFriends;
	private ArrayList<DependencyRelation> depenRelations;
	private Group myGroup;
	private String commitmentStoreID;
	private String testerAgentID;
	
	private DomainCBR domainCBR;
	private ArgCBR argCBR;
	
	//private Ticket ticket;
	private float threshold;
	ArrayList<SimilarDomainCase> similarDomainCases;
	
	//weights
	private float wSimilarity;
	private float wArgSuitFactor;
	
	//SF =( (wPD * PD + wSD * SD + wRD * (1 - RD) + wAD * (1 - AD) + wED * ED + wEP * EP) )/6
	private float wPD;
	private float wSD;
	private float wRD;
	private float wAD;
	private float wED;
	private float wEP;
		
	
	private String currentDialogueID;
	private String currentDialogueIDWithdraw;//only for initiator agents
	private Problem currentProblem;
	private DomainCase currentDomCase2Solve;
	private DomainCase originalTDomCase2Solve;
	private Position currentPosition;
	private Position lastPositionBeforeNull;
	private HashMap<String,ArrayList<DialogueGraph>> dialogueGraphs;
	private DialogueGraph currentDialogueGraph;
	private String subDialogueAgentID="";
	private ArrayList<Position> differentPositions;
	
	private boolean dialogueFinished;
//	private boolean waitingFinalSolution;
	private long myLastCheckMillis=0l;
	private long dialogueInitTime;
	private int timesInState4=0;
	private int timesInState5=0;
	private int timesInState6=0;
	private int timesInState7=0;
	private float dialogueTime = 0f;
	private int currentPosAccepted = 0;
	private int agreementReached = 0;
	private int acceptanceFrequency = 0;
	private int votesReceived = 0;
	private int usedArgCases = 0;
	private boolean alive = true;
	
	private ArrayList<Position> myPositions;
	private boolean positionsGenerated;
	private ArrayList<Position> askedPositions;
	private HashMap<String,ArrayList<Position>> attendedWhyPetitions;
	private HashMap<String,ArrayList<Argument>> mySupportArguments;
	private int myUsedLocutions = 0;

	//we don't use a list of attack arguments because we only generate a counter example or distinguishing premises arguments
	//and that don't cause efficiency problems
	//private HashMap<String,ArrayList<Argument>> myAttackArguments;
	private HashMap<String,ArrayList<Argument>> myUsedSupportArguments;
	private HashMap<String,ArrayList<Argument>> myUsedAttackArguments;
	
	private HashMap<String,ArrayList<Argument>> storeArguments;
	
	
	private ArrayList<ACLMessage> messagesQueue;
	
	private String iniArgCasesFilePath;
	private String finArgCasesFilePath;
	private String iniDomCasesFilePath;
	private String finDomCasesFilePath;

	/**
	 * Main method to build Argumentative Agents
	 * @param aid Agent {@link AID}
	 * @param isInitiator Flag to set the Initiator role
	 * @param mySocialEntity {@link SocialEntity} of the Agent
	 * @param myFriends {@link ArrayList} with the SocialEntities that represent the Agent's friends
	 * @param depenRelations {@link ArrayList} with the Agent's dependency relations with its friends
	 * @param group {@link Group} that the Agent belongs to
	 * @param commitmentStoreID ID of the {@link CommitmentStore}
	 * @param testerAgentID ID of the TesterAgent to run tests in the system
	 * @param iniDomCasesFilePath File with the original DomainCases case-base
	 * @param finDomCasesFilePath File to write the updated DomainCases case-base
	 * @param iniArgCasesFilePath File with the original ArgumentCases case-base
	 * @param finArgCasesFilePath File with the updated ArgumentCases case-base
	 * @param threshold Similarity threshold over which a DomainCase is retrieved from the DomainCases case-base
	 * @param wPD Weight of the Persuasion Degree
	 * @param wSD Weight of the Support Degree
	 * @param wRD Weight of the Risk Degree
	 * @param wAD Weight of the Attack Degree
	 * @param wED Weight of the Efficiency Degree
	 * @param wEP Weight of the Explanatory Power
	 * @throws Exception
	 */
	public ArgCAgent(AgentID aid, boolean isInitiator, SocialEntity mySocialEntity, 
			ArrayList<SocialEntity> myFriends, ArrayList<DependencyRelation> depenRelations, Group group, 
			String commitmentStoreID, String testerAgentID, String iniDomCasesFilePath, String finDomCasesFilePath, 
			String iniArgCasesFilePath, String finArgCasesFilePath, float threshold,
			float wPD, float wSD, float wRD, float wAD, float wED, float wEP) throws Exception {
		
		super(aid);
		
		this.myID=aid.getLocalName();
		this.initiator=isInitiator;
		this.mySocialEntity=mySocialEntity;
		this.preferedValues=mySocialEntity.getValPref().getValues();
		this.myFriends=myFriends;
		this.depenRelations=depenRelations;
		this.myGroup=group;
		this.commitmentStoreID=commitmentStoreID;
		this.testerAgentID=testerAgentID;
		this.threshold=threshold;
		this.wPD = wPD;
		this.wSD = wSD;
		this.wRD = wRD;
		this.wAD = wAD;
		this.wED = wED;
		this.wEP = wEP;
		
		this.iniArgCasesFilePath = iniArgCasesFilePath;
		this.finArgCasesFilePath = finArgCasesFilePath;
		this.iniDomCasesFilePath = iniDomCasesFilePath;
		this.finDomCasesFilePath = finDomCasesFilePath;

		
		domainCBR= new DomainCBR(iniDomCasesFilePath, finDomCasesFilePath);
		argCBR= new ArgCBR(iniArgCasesFilePath, finArgCasesFilePath);
		
		currentDialogueID=null;
		currentDialogueIDWithdraw=null;//only for initiator agents
		currentProblem=null;
		currentDomCase2Solve=null;
		originalTDomCase2Solve=null;
		lastPositionBeforeNull = null;
		currentPosition=null;
		dialogueFinished=false;
//		waitingFinalSolution=false;
		dialogueInitTime=0;
		dialogueGraphs=new HashMap<String, ArrayList<DialogueGraph>>();
		currentDialogueGraph=null;
		
		myPositions=null;
		positionsGenerated=false;
		askedPositions=new ArrayList<Position>();
		attendedWhyPetitions=new HashMap<String, ArrayList<Position>>();
		mySupportArguments= new HashMap<String, ArrayList<Argument>>();
//		myAttackArguments= new HashMap<String, ArrayList<Argument>>();
		myUsedSupportArguments= new HashMap<String, ArrayList<Argument>>();
		myUsedAttackArguments= new HashMap<String, ArrayList<Argument>>();
		storeArguments= new HashMap<String, ArrayList<Argument>>();
		messagesQueue= new ArrayList<ACLMessage>();
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		
		try{
			// Clean end file 
			FileWriter fstream = new FileWriter(this.getName(),false);
			BufferedWriter outFile = new BufferedWriter(fstream);
			//Close the output stream
			outFile.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
		
		class myArgumentation extends Argumentation_Participant {

			
			@Override
			protected void doOpenDialogue(CProcessor myProcessor,
					ACLMessage msg) {
				currentDomCase2Solve=(DomainCase)msg.getContentObject();
				currentDialogueID=msg.getConversationId();
			}
			
			@Override
			protected boolean doEnterDialogue(CProcessor myProcessor,
					ACLMessage msg) {
				
				agreementReached = 0;
				acceptanceFrequency = 0;
				usedArgCases=0;
				
				ACLMessage msg2=enter_dialogue(currentDomCase2Solve, currentDialogueID);
				copyMessages(msg, msg2);
				
				logger.info(myID+": message "+msg.getHeaderValue("locution")+ " receiver: "+msg.getReceiver().name);
				if(msg.getHeaderValue("locution").equalsIgnoreCase(ENTERDIALOGUE)) return true;
				else return false;
			}

			@Override
			protected boolean doPropose(CProcessor myProcessor, ACLMessage msg) {
				if(!positionsGenerated)
					myPositions=generatePositions(currentProblem);
				ACLMessage msg2;
				currentPosition=null;
				if(myPositions!=null && myPositions.size()>0){
					currentPosition=myPositions.remove(0);//extract the first position and remove it from list
					if (currentPosition != null)
						lastPositionBeforeNull = new Position(currentPosition.getAgentID(), currentPosition.getDialogueID(), currentPosition.getSolution(),
							currentPosition.getPremises(), currentPosition.getDomainCases(), currentPosition.getDomainCaseSimilarity());;
					currentPosAccepted = 0;
					mySupportArguments= new HashMap<String, ArrayList<Argument>>();
					myUsedSupportArguments= new HashMap<String, ArrayList<Argument>>();
					myUsedAttackArguments= new HashMap<String, ArrayList<Argument>>();
//					dialogueGraphs= new HashMap<String, ArrayList<DialogueGraph>>();
					currentDialogueGraph=null;
				}
				if(currentPosition!=null){
					
					msg2=propose(currentPosition,currentDialogueID);
					copyMessages(msg, msg2);
					return true;
				}
				else{//no position generated, withdraw
					msg2=withdraw_dialogue(currentProblem);
					copyMessages(msg, msg2);
					return false;
				}
				
			}


			@Override
			protected String doAssert(CProcessor myProcessor, ACLMessage msg, String whyAgentID) {
				ArrayList<Position> myPositionsAsked=attendedWhyPetitions.get(whyAgentID);
				ACLMessage msg2;
				
				if(myPositionsAsked!=null && myPositionsAsked.contains(currentPosition)){
					//I have already replied this agent with my current position, do not reply him
					return "CENTRAL"; //TODO send something...?? what?
				}
				else{	
					//try to generate a support argument 
					//1) Argument-cases 2) domain-cases 3) premises
					
					ArrayList<Argument> supportArgs=generateSupportArguments(currentPosition, whyAgentID);
					Argument arg=null;
					if (!supportArgs.isEmpty())
						arg=supportArgs.remove(0);
					mySupportArguments.put(whyAgentID, supportArgs);
					
					if(arg!=null){ //assert the argument
						logger.info("*********************"+myID+": "+" received WHY, generating suport arg. ASSERTING");
						
						
						msg2=asserts(whyAgentID, arg);
						copyMessages(msg, msg2);
						
						//I am now talking only with this agent
						subDialogueAgentID=whyAgentID;
						
						//add argument to myUsedSupportArguments
						ArrayList<Argument> supportArgsUsed=myUsedSupportArguments.get(whyAgentID);
						if(supportArgsUsed==null)
							supportArgsUsed=new ArrayList<Argument>();
						supportArgsUsed.add(arg);
						myUsedSupportArguments.put(whyAgentID, supportArgsUsed);
						
						// add argument to dialogue graph, it is the first
						ArgNode argNode=new ArgNode(arg.getID(), new ArrayList<Long>(), -1, ArgNode.NodeType.FIRST);
						currentDialogueGraph=new DialogueGraph();
						currentDialogueGraph.addNode(argNode);
						
						return ASSERT;
					}
					else{ //can not generate support arguments, noCommit
						logger.info("*********************"+myID+": "+" received WHY, generating suport arg. NO COMMITTT");
						msg2=noCommit(whyAgentID,currentPosition);
						copyMessages(msg, msg2);
						
						return NOCOMMIT;
					}
				}
				
			}

			@Override
			protected boolean doAttack(CProcessor myProcessor, ACLMessage msg) {
				// try to generate an attack argument: Distinguishing premise or Counter Example, depending on the attack received
				Argument againstArgument = (Argument)msg.getContentObject();
				Argument attackArg=generateAttackArgument(againstArgument, msg.getSender().getLocalName());
				ACLMessage msg2;
				
				if(attackArg!=null){
					msg2=attack(msg.getSender().getLocalName(), attackArg);
					copyMessages(msg, msg2);
					
					//add argument to myUsedAttackArguments
					ArrayList<Argument> attackArgs=myUsedAttackArguments.get(msg.getSender().getLocalName());
					if(attackArgs==null)
						attackArgs=new ArrayList<Argument>();
					attackArgs.add(attackArg);
					myUsedAttackArguments.put(msg.getSender().getLocalName(), attackArgs);
					
					// add the attack argument to dialogue graph
					ArgNode attNode = currentDialogueGraph.getNode(againstArgument.getID());
					if (attNode == null){
						logger.error(myID + " GETTING NODE " + againstArgument.getID());

						for (ArgNode node : currentDialogueGraph.getNodes()){
							logger.error(myID + " " + node.getNodeType() + " " + node.getArgCaseID() + " PARENT " + node.getParentArgCaseID()+ "\n");
						}
					}
					attNode.addChildArgCaseID(attackArg.getID());
					ArgNode attackNode=new ArgNode(attackArg.getID(), new ArrayList<Long>(), againstArgument.getID(), ArgNode.NodeType.NODE);
					currentDialogueGraph.addNode(attackNode);

					return true;
				}
				else{
					
					/*
					 * If the agent cannot generate another attack, it retracts its attack argument. If it has no more attacks, 
					 * it has to retract the support argument, if it has no more support arguments, it has to noCommit position
					 */
					
					//search my last attack argument, the one I told this agent
					ArrayList<Argument> attackArgs=myUsedAttackArguments.get(msg.getSender().getLocalName());
					Argument myLastAttackArg=attackArgs.get(attackArgs.size()-1);
					//put acceptability state to Unacceptable
					myLastAttackArg.setAcceptabilityState(AcceptabilityState.UNACCEPTABLE);
					//retract my last attack argument
					ArrayList<Argument> storeList = storeArguments.get(msg.getSender().getLocalName());
					if (storeList == null)
						storeList = new ArrayList<Argument>();
					storeList.add(myLastAttackArg);
					storeArguments.put(msg.getSender().getLocalName(), storeList);
					
					msg2=retract(msg.getSender().getLocalName(), myLastAttackArg);
					copyMessages(msg, msg2);
					
					// set the last node of this branch of the dialogue graph
					ArgNode thisNode = currentDialogueGraph.getNode(myLastAttackArg.getID());
					if (thisNode == null){
						logger.error(myID + " GETTING NODE " + myLastAttackArg.getID());

						for (ArgNode node : currentDialogueGraph.getNodes()){
							logger.error(myID + " " + node.getNodeType() + " " + node.getArgCaseID() + " PARENT " + node.getParentArgCaseID()+ "\n");
						}
					}
					thisNode.setNodeType(NodeType.LAST);

					return false;
						
				}
				
			}

			@Override
			protected void doQueryPositions(CProcessor myProcessor,
					ACLMessage msg) {
				
				ACLMessage msg2=sendMessage(commitmentStoreID, GETALLPOSITIONS, currentDialogueID, null);
				copyMessages(msg, msg2);
				
			}

			@SuppressWarnings("unchecked")
			@Override
			protected boolean doGetPositions(CProcessor myProcessor,
					ACLMessage msg) {
				// TODO Auto-generated method stub
				differentPositions=getDifferentPositions((ArrayList<Position>)msg.getContentObject());
				return true;
			}
			
			@Override
			protected boolean doWhy(CProcessor myProcessor, ACLMessage msg) {
				ACLMessage msg2;
				
				
				if(differentPositions!=null && differentPositions.size()>0){//some position to ask
					int randPos=(int)Math.random()*differentPositions.size();
					Position pos=differentPositions.get(randPos); //position chosen randomly
					//askedPositions.add(pos);  we only add the position of the other agent when that agent responds
					msg2=why(pos.getAgentID(),pos);
					copyMessages(msg, msg2);
					logger.info("------------ ------ "+myID + ": WHY to "+pos.getAgentID());
					return true;
				}
				else{//nothing to challenge, remain in this state
					//TODO return some message????
					logger.info("------------ ------ "+myID + ": NOT WHY nothing to challenge");
					 return false; 
				}
				
			}

			@Override
			protected boolean doFinishDialogue(CProcessor myProcessor,
					ACLMessage msg) {
				
				return true;
			}
			
			
			@Override
			protected void doSendPosition(CProcessor myProcessor,
					ACLMessage msg) {
				ACLMessage msg2;
				if(currentPosition!=null){
					msg2=sendMessage(commitmentStoreID, ADDPOSITION, currentDialogueID, currentPosition);
					copyMessages(msg, msg2);
				}
				
				
			}
			
			@Override
			protected void doSolution(CProcessor myProcessor, ACLMessage msg) {
				Solution solution=(Solution) msg.getContentObject();
				if(solution.getConclusion().getID()!=-1)
					updateCBs(solution);
				logger.info(myID+": "+"SOLUTION received"+" from: "+msg.getSender().getLocalName()+"\n domCases="+domainCBR.getAllCasesList().size()+"\n argCases="+argCBR.getAllCasesVector().size());
				
			}

			@Override
			protected void doDie(CProcessor myProcessor) {
				try{
					// Create the end file 
					FileWriter fstream = new FileWriter(myID,false);
					BufferedWriter outFile = new BufferedWriter(fstream);
					outFile.write("test finished");
					outFile.newLine();
					//Close the output stream
					outFile.close();
				}catch (Exception e){//Catch exception if any
					System.err.println("Error: " + e.getMessage());
				}
				
				myProcessor.getMyAgent().ShutdownNoLock();
				
			}

			

			

			

			
		}
		
		// In order to start a conversation the agent creates a message
		// that can be accepted by one of its initiator factories.

//		msg = new ACLMessage(ACLMessage.CFP);
//		msg.addReceiver(new AgentID("Sally"));
//		msg.addReceiver(new AgentID("Mary"));
//		msg.setContent("How much do you want to spend tomorrow in the dinner?");

		// The agent creates the CFactory that creates processors that initiate
		// CONTRACT_NET protocol conversations. In this
		// example the CFactory gets the name "TALK", we don't add any
		// additional message acceptance criterion other than the required
		// by the CONTRACT_NET protocol (null) and we do not limit the number of simultaneous
		// processors (value 0)
		
		long rand=(long) (1200*Math.random());
		CFactory talk = new myArgumentation().newFactory("TALK", null, null,
				1, this, 300, rand);
		
		logger.info(this.getName()+": My rand wait time is "+rand);

		// The factory is setup to answer start conversation requests from the agent
		// using the CONTRACT_NET protocol.

		this.addFactoryAsParticipant(talk);

		// finally the new conversation starts. Because it is synchronous, 
		// the current interaction halts until the new conversation ends.

//		System.out.println("I ask for proposals to Mary and Sally");
//		myProcessor.createSyncConversation(msg);

		
	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {

		logger.info("+++++++++++++++++++++++++++++++++++++++ "+this.getName()+": Finalizing");
		
		System.out.println(finalizeMessage.getContent());
	}
	
	
	
	
	
	
	
	/**
	 * Copies all contents of the msg2 to the msg1
	 * @param msg
	 * @param msg2
	 */
	private void copyMessages(ACLMessage msg, ACLMessage msg2){
		msg.setSender(msg2.getSender());
		msg.setReceiver(msg2.getReceiver());
		msg.setConversationId(msg2.getConversationId());
		msg.setHeader("locution", msg2.getHeaderValue("locution"));
		msg.setPerformative(msg2.getPerformative());
		if(msg2.getContentObject()!=null)
			try {
				msg.setContentObject((Serializable) msg2.getContentObject());
			} catch (IOException e) {
				
				e.printStackTrace();
			}
	}
	
	
	public ACLMessage enter_dialogue(DomainCase domCase, String dialogueID){
		
		currentDomCase2Solve=domCase;
		/*
		 * each agent adds domain-cases with the agreed solution ONLY!!! at the end of the dialogue
		 */
		
		
		//a test agent will give the initiator agent the Ticket to solve
		similarDomainCases=domainCBR.retrieve(domCase.getProblem().getDomainContext().getPremises(), threshold);
		
//		if(similarDomainCases==null || similarDomainCases.size()==0)
//			logger.info("\n"+this.getName()+": "+" NO similar domain cases"+"\n");
		
		currentDialogueID=dialogueID;
		
		//has cases and solutions to enter in the dialogue
		if(similarDomainCases!=null && similarDomainCases.size()>0){
			
			currentProblem=new Problem(new DomainContext(domCase.getProblem().getDomainContext().getPremises()));
			if (currentPosition != null)
				lastPositionBeforeNull = new Position(currentPosition.getAgentID(), currentPosition.getDialogueID(), currentPosition.getSolution(),
					currentPosition.getPremises(), currentPosition.getDomainCases(), currentPosition.getDomainCaseSimilarity());;
			currentPosition=null;
			positionsGenerated=false;
			dialogueFinished=false;
//			waitingFinalSolution=false;
			
			askedPositions=new ArrayList<Position>();
			attendedWhyPetitions=new HashMap<String, ArrayList<Position>>();
			dialogueGraphs= new HashMap<String, ArrayList<DialogueGraph>>();
			storeArguments = new HashMap<String,ArrayList<Argument>>();
			
			//enter dialogue, add in commitment store
			
			return enterDialogue(dialogueID);
			
		}
		//Not enter in the dialogue
		else{ 
			return withdraw_dialogue(currentProblem);
		}
	}
	
	public ACLMessage withdraw_dialogue(Problem prob){
		
//		//check if there is not any active position
//		Position pos= getPosition(myID, currentDialogueID);
//		if(pos==null){
			//then, get out of dialogue
			
			if (currentPosition != null)
				lastPositionBeforeNull= new Position(currentPosition.getAgentID(), currentPosition.getDialogueID(), currentPosition.getSolution(),
					currentPosition.getPremises(), currentPosition.getDomainCases(), currentPosition.getDomainCaseSimilarity());;
			currentPosition=null;
			currentProblem=null;
			positionsGenerated=false;
//			return true;
//		}
//		else
//			return false;
			return leaveDialogue(currentDialogueID);
	}
	
	public ACLMessage propose(Position pos, String dialogueID){
		
		ArrayList<String> locutions = new ArrayList<String>();
		locutions.add(ACCEPT);
		locutions.add(WHY);
		
		// clean message queue from old messages
//		cleanMessagesQueue(locutions);
		
		return addPosition(pos, dialogueID);
		
	}
	
	public ACLMessage why(String agentIDr, Position pos){
		
		ArrayList<String> locutions = new ArrayList<String>();
		locutions.add(ASSERT);
		locutions.add(NOCOMMIT);
		
		// clean message queue from old messages
//		cleanMessagesQueue(locutions, agentIDr);
		
		
		myUsedLocutions++;
		return sendMessage(agentIDr, WHY, currentDialogueID, pos);
	}
	
//	public void why(String agentIDr, Argument arg){
//		
//		sendMessage(agentIDr, WHY, currentDialogueID, arg);
//		
//	}
	
	public ACLMessage noCommit(String agentIDr, Position pos){
		
		removePosition(currentDialogueID);
		
		myUsedLocutions++;
		if (currentPosition != null)
			lastPositionBeforeNull=new Position(currentPosition.getAgentID(), currentPosition.getDialogueID(), currentPosition.getSolution(),
				currentPosition.getPremises(), currentPosition.getDomainCases(), currentPosition.getDomainCaseSimilarity());;
		currentPosition=null;
		currentPosAccepted = 0;
		
		return sendMessage(agentIDr, NOCOMMIT, currentDialogueID, null);
		
	}
	
	public ACLMessage asserts(String agentIDr, Argument arg){
		
		ArrayList<String> locutions = new ArrayList<String>();
		locutions.add(ACCEPT);
		locutions.add(ATTACK);
		
		// clean message queue from old messages
//		cleanMessagesQueue(locutions, agentIDr);
		
		//add the argument to commitment store
		addArgument(arg, currentDialogueID);
		
		//send it to the other agent
		
		myUsedLocutions++;
		return sendMessage(agentIDr, ASSERT,currentDialogueID, arg);
		
	}
	
	public void accept(String agentIDr, Argument arg){
		
		//send it to the other agent
		sendMessage(agentIDr, ACCEPT, currentDialogueID, arg);
		myUsedLocutions++;
		
	}
	
//	public void accept(String agentIDr, Position pos){
//
//		//send it to the other agent
//		sendMessage(agentIDr, ACCEPT, currentDialogueID, pos);
//		
//	}
	
	public ACLMessage attack(String agentIDr, Argument arg){
		
		ArrayList<String> locutions = new ArrayList<String>();
		locutions.add(ACCEPT);
		locutions.add(ATTACK);
		locutions.add(RETRACT);
		
		// clean message queue from old messages
//		cleanMessagesQueue(locutions, agentIDr);
		
		//add the argument to commitment store
		addArgument(arg, currentDialogueID);
		
		//send it to the other agent
		
		myUsedLocutions++;
		return sendMessage(agentIDr, ATTACK, currentDialogueID, arg);
	}
	
	public ACLMessage retract(String agentIDr, Argument arg){
		
		//remove the argument from commitment store
		removeArgument(arg, currentDialogueID);
		
		//The argument is not removed from the list of used arguments. I need to know those arguments
	
		//inform to the other agent
		
		myUsedLocutions++;
		return sendMessage(agentIDr, RETRACT, currentDialogueID, arg);
	}
	

	private Argument getMyLastUsedArg(String agentID, long argID){
		try{
			ArrayList<Argument> attackArgs=myUsedAttackArguments.get(agentID);
			if(attackArgs!=null){
				for(int i=attackArgs.size()-1;i>=0;i--){
					if(argID==attackArgs.get(i).getID())
						return attackArgs.get(i);
				}
			}
			
			ArrayList<Argument> supportArgs=myUsedSupportArguments.get(agentID);
			if(supportArgs!=null){
				for(int i=supportArgs.size()-1;i>=0;i--){
					if(argID==supportArgs.get(i).getID())
						return supportArgs.get(i);
				}
			}
		}catch(Exception e){
			logger.error(this.getName()+": Exception in getMyLastUsedArg\n"+e.toString());
			e.printStackTrace();
		}
		
		return null;
	}
	
	//the agent will get the first position always, removing it. when he is out of positions, has to withdraw
	private ArrayList<Position> generatePositions(Problem prob){
		//generate all positions and store them in a list, ordered by Promoted Value, and then Suitability = w*SimDegree + w2*SuitFactor arg
		
		//so, at first we need make a query to DomainCBR to the possible solutions
		//then, with each solution, create a Position
		//with each position, query the argCBR calculating the SuitFactor Arg (PD + ....)
		
		// Fist, assign weights in accordance with the quantity of knowledge
//		int domCases = domainCBR.getAllCasesVector().size();
//		int arguCases = argCBR.getAllCasesVector().size();
//		int totalCases = domCases + arguCases;
//		
//		if (totalCases != 0){
//			wSimilarity = (float) domCases / (float) totalCases;
//			wArgSuitFactor = (float) arguCases / (float) totalCases;
//		}else{
//			wSimilarity = 0.5f;
//			wArgSuitFactor = 0.5f;
//		}

		
		ArrayList<Position> finalPositions=new ArrayList<Position>();
		
		if(similarDomainCases==null || similarDomainCases.size()==0)
			logger.info("\n"+this.getName()+": "+" NO similar domain cases"+"\n");
		
		//similarDomainCases has been initialized in enter_dialogue, with the similar domain cases to the problem
		if(similarDomainCases!=null && similarDomainCases.size()>0){
			
			//create a list of position list
			//in each list of positions will be stored the positions with same Promoted Values
			ArrayList<ArrayList<Position>> positionsLists=new ArrayList<ArrayList<Position>>();
			for(int i=0;i<preferedValues.size();i++){
				ArrayList<Position> positions= new ArrayList<Position>();
				positionsLists.add(positions);
			}
			
			for(int sdc=0; sdc<similarDomainCases.size();sdc++){
				SimilarDomainCase simDomCase=similarDomainCases.get(sdc);
				ArrayList<Solution> caseSolutions=simDomCase.getCaseb().getSolutions();
				for(int sc=0; sc<caseSolutions.size(); sc++){
					Solution sol= caseSolutions.get(sc);
					int index=getPreferredValueIndex(sol.getPromotesValue());
					//if the Promoted Value is one of the preferred values of the agent, the position is added. Otherwise it is not added.
					if(index!=-1){
						ArrayList<DomainCase> supportDomCases = new ArrayList<DomainCase>();
						supportDomCases.add(simDomCase.getCaseb());
						
						for (int i = sdc+1; i< similarDomainCases.size(); i++){
							SimilarDomainCase dc = similarDomainCases.get(i);
							for (int k=0; k< dc.getCaseb().getSolutions().size(); k++){
								Solution s = dc.getCaseb().getSolutions().get(k);
								if (s.getConclusion().getID() == sol.getConclusion().getID() 
										&& s.getPromotesValue().equalsIgnoreCase(sol.getPromotesValue())){
									supportDomCases.add(dc.getCaseb());
									dc.getCaseb().removeSolution(s);
									k--;
									
									if (dc.getCaseb().getSolutions().isEmpty()){
										similarDomainCases.remove(dc);
										i--;
									}
									break;
								}
							}
							
						}
						
						Position pos=new Position(myID, currentDialogueID, sol, 
								simDomCase.getCaseb().getProblem().getDomainContext().getPremises(), 
								supportDomCases,simDomCase.getSimilarity());
						positionsLists.get(index).add(pos);
					}
				}
			}
			
			//store all positions in a list, to calculate attack degree, efficiency degree and explanatory power
			ArrayList<Position> allPositions=new ArrayList<Position>();
			Iterator<ArrayList<Position>> iterPositionsLists2=positionsLists.iterator();
			while(iterPositionsLists2.hasNext()){
				ArrayList<Position> positions=iterPositionsLists2.next();
				allPositions.addAll(positions);
			}
			
			//logger.info("\n"+this.getName()+": "+allPositions.size()+" initial positions"+"\n");
			
			Iterator<ArrayList<Position>> iterPositionsLists=positionsLists.iterator();
			while(iterPositionsLists.hasNext()){
				ArrayList<Position> positions=iterPositionsLists.next();
				for(int i=0;i<positions.size();i++){
					Position position=positions.get(i);
										
					SocialContext socCont = new SocialContext(mySocialEntity, null, myGroup, null);
					ArgumentProblem argProblem=new ArgumentProblem(new DomainContext(position.getPremises()), socCont);
					
					ArrayList<Float> degrees=argCBR.getDegrees(argProblem, position.getSolution(),allPositions,allPositions.indexOf(position));
					float persuasivenessDegree=degrees.get(0);
					float supportDegree=degrees.get(1);
					float riskDegree=degrees.get(2);
					float attackDegree=degrees.get(3);
					float efficiencyDegree=degrees.get(4);
					float explanatoryPower=degrees.get(5);
				
					
					//SF =( (wPD * PD + wSD * SD + wRD * (1 - RD) + wAD * (1 - AD) + wED * ED + wEP * EP) )/6
//					float argSuitabilityFactor =
//						(wPD * persuasivenessDegree + wSD * supportDegree + wRD * (1 - riskDegree) 
//								+ wAD * (1 - attackDegree) + wED * efficiencyDegree + wEP * explanatoryPower) /6;
					float argSuitabilityFactor =
						(wPD * persuasivenessDegree + wSD * supportDegree + wRD * (1 - riskDegree) 
								+ wAD * (1 - attackDegree) + wED * efficiencyDegree + wEP * explanatoryPower);
					position.setArgSuitabilityFactor(argSuitabilityFactor);
					
					// Assign weights in accordance with the quantity of knowledge
					int domCases = similarDomainCases.size();
					int arguCases;
					try {
						arguCases = argCBR.getSameDomainAndSocialContextAccepted(position.getPremises(), 
								position.getSolution(), socCont).size();
						usedArgCases += arguCases;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						arguCases = 0;
					}
					int totalCases = domCases + arguCases;
					
					if (totalCases != 0){
						wSimilarity = (float) domCases / (float) totalCases;
						wArgSuitFactor = (float) arguCases / (float) totalCases;
					}else{
						wSimilarity = 0.5f;
						wArgSuitFactor = 0.5f;
					}
					
//					float finalSuitability=(position.getDomainCaseSimilarity()*wSimilarity + argSuitabilityFactor*wArgSuitFactor)/2;
					float finalSuitability=(position.getDomainCaseSimilarity()*wSimilarity + argSuitabilityFactor*wArgSuitFactor);

					position.setFinalSuitability(finalSuitability);
					
					positions.set(i, position);
				}
				
				Collections.sort(positions);//sort positions by its finalSuitability= w*SimDegree + w2*SuitFactorArg
				finalPositions.addAll(positions);
				
			}
			

			
			
		}
		
		String solString="";
		for(int i=0;i<finalPositions.size();i++){
			solString+=finalPositions.get(i).getSolution().getConclusion().getID()+" ";
		}
		logger.info("\n"+this.getName()+": "+finalPositions.size()+" initial positions"+" ** Solutions: "+solString+"\n");
		positionsGenerated=true;
		return finalPositions;
	}
	
	
	private ArrayList<Argument> generateSupportArguments(Position myPos, String agentID){
		// Fist, assign weights in accordance with the quantity of knowledge
//		int domCases = domainCBR.getAllCasesVector().size();
//		int arguCases = argCBR.getAllCasesVector().size();
//		int totalCases = domCases + arguCases;
//		
//		if (totalCases != 0){
//			wSimilarity = (float) domCases / (float) totalCases;
//			wArgSuitFactor = (float) arguCases / (float) totalCases;
//		}else{
//			wSimilarity = 0.5f;
//			wArgSuitFactor = 0.5f;
//		}
		
		ArrayList<Argument> finalSupportArguments=new ArrayList<Argument>();
		
		int friendIndex=getFriendIndex(agentID);
		SocialEntity opponent=myFriends.get(friendIndex);
		DependencyRelation relation=depenRelations.get(friendIndex);
		
		//try to generate a support argument 
		//1) Argument-cases 2) domain-cases 3) premises
		
		SocialContext socialContext= new SocialContext(mySocialEntity, opponent, myGroup, relation);
		
		//create arg case with the domain case
		ArgumentProblem argProblem=new ArgumentProblem(new DomainContext(currentPosition.getPremises()), socialContext);
		ArgumentSolution argSolution=new ArgumentSolution();
		argSolution.setConclusion(currentPosition.getSolution().getConclusion());
		argSolution.setPromotesValue(currentPosition.getSolution().getPromotesValue());
		argSolution.setTimesUsed(currentPosition.getSolution().getTimesUsed());
		ArgumentJustification argJustification=new ArgumentJustification();
		for(int i=0; i<currentPosition.getDomainCases().size(); i++){
			argJustification.addDomainCase(currentPosition.getDomainCases().get(i).getID());
		}
		
		ArgumentCase argCasefromDomainCase= 
			new ArgumentCase(System.nanoTime(), new Date(System.currentTimeMillis()).toString(), argProblem, argSolution, argJustification, 0);
		
		
		//create arg case with just the premises
		ArgumentJustification argJustificationPremises=new ArgumentJustification();
		ArgumentCase argCasePremises= 
			new ArgumentCase(System.nanoTime(), new Date(System.currentTimeMillis()).toString(), argProblem, argSolution, argJustificationPremises, 0);
		
		//extract argument cases
		ArrayList<SimilarArgumentCase> argCases=argCBR.getSameDomainAndSocialContextAccepted(myPos.getPremises(),myPos.getSolution(),socialContext);
		usedArgCases+=argCases.size();
		
		//add arg case with the domain case and arg case with just the premises
		
		argCases.add(new SimilarArgumentCase(argCasefromDomainCase, 1));
		argCases.add(new SimilarArgumentCase(argCasePremises, 1));
		
		Iterator<SimilarArgumentCase> iterArgCases=argCases.iterator();
		//this list contains positions that represent the different arg cases extracted
		//just to calculate the degrees with the same function getDegrees()
		ArrayList<Position> allPositions= new ArrayList<Position>();  
		while(iterArgCases.hasNext()){
			SimilarArgumentCase simArgCase = iterArgCases.next();
			Solution solution=new Solution(simArgCase.getArgumentCase().getArgumentSolution().getConclusion(), simArgCase.getArgumentCase().getArgumentSolution().getPromotesValue(), simArgCase.getArgumentCase().getArgumentSolution().getTimesUsed());
			allPositions.add(new Position("", "", solution, simArgCase.getArgumentCase().getArgumentProblem().getDomainContext().getPremises(),null, 1f));
		}
		
		iterArgCases=argCases.iterator();
		while(iterArgCases.hasNext()){
			SimilarArgumentCase simArgCase = iterArgCases.next();
			Solution solution=new Solution(simArgCase.getArgumentCase().getArgumentSolution().getConclusion(), simArgCase.getArgumentCase().getArgumentSolution().getPromotesValue(), simArgCase.getArgumentCase().getArgumentSolution().getTimesUsed());
			
			ArrayList<Float> degreesList=argCBR.getDegrees(simArgCase.getArgumentCase().getArgumentProblem(), solution, allPositions,argCases.indexOf(simArgCase));
			float persuasivenessDegree=degreesList.get(0);
			float supportDegree=degreesList.get(1);
			float riskDegree=degreesList.get(2);
			float attackDegree=degreesList.get(3);
			float efficiencyDegree=degreesList.get(4);
			float explanatoryPower=degreesList.get(5);
			
			float argSuitabilityFactor =
//				(wPD * persuasivenessDegree + wSD * supportDegree + wRD * (1 - riskDegree) 
//						+ wAD * (1 - attackDegree) + wED * efficiencyDegree + wEP * explanatoryPower) /6;
				(wPD * persuasivenessDegree + wSD * supportDegree + wRD * (1 - riskDegree) 
						+ wAD * (1 - attackDegree) + wED * efficiencyDegree + wEP * explanatoryPower);
//			simArgCase.setSuitability((argSuitabilityFactor * wArgSuitFactor + simArgCase.getSuitability() * wSimilarity)/2);
			
			// Assign weights in accordance with the quantity of knowledge
			int domCases = similarDomainCases.size();
			int arguCases;
			try {
				arguCases = argCBR.getSameDomainAndSocialContextAccepted(myPos.getPremises(), 
						myPos.getSolution(), socialContext).size();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				arguCases = 0;
			}
			int totalCases = domCases + arguCases;
			
			if (totalCases != 0){
				wSimilarity = (float) domCases / (float) totalCases;
				wArgSuitFactor = (float) arguCases / (float) totalCases;
			}else{
				wSimilarity = 0.5f;
				wArgSuitFactor = 0.5f;
			}
			
			simArgCase.setSuitability((argSuitabilityFactor * wArgSuitFactor + simArgCase.getSuitability() * wSimilarity));

//			
			//calculate suitability: with the current similarity in SimilarArgCase, and the suitability obtained of argumentation
			
//			simArgCase.setSuitability(simArgCase.getSuitability());//TODO this avoids argumentation knowledge to the support arg...
		}
		
		Collections.sort(argCases);
		
		
		ArrayList<Premise> premises=new ArrayList<Premise>();
		Iterator<Premise> iterPremises=myPos.getPremises().values().iterator();
		while(iterPremises.hasNext()){
			premises.add(iterPremises.next());
		}
		ArrayList<DomainCase> domainCases=new ArrayList<DomainCase>();
		ArrayList<ArgumentCase> argumentCases=new ArrayList<ArgumentCase>();
		ArrayList<ArgumentationScheme> schemes=new ArrayList<ArgumentationScheme>();
		ArrayList<Premise> distPremises=new ArrayList<Premise>();
		ArrayList<Premise> presumptions=new ArrayList<Premise>();
		ArrayList<Premise> exceptions=new ArrayList<Premise>();
		ArrayList<DomainCase> counterExamplesdomainCases=new ArrayList<DomainCase>();
		ArrayList<ArgumentCase> counterExamplesargumentCases=new ArrayList<ArgumentCase>();
		
		// create support argument with premises or domain-cases, not directly with argument-cases
		
		for(int i=0;i<argCases.size();i++){
			ArgumentCase bestArgCase=argCases.get(i).getArgumentCase();
			if(bestArgCase!=null){
				argumentCases=new ArrayList<ArgumentCase>();
				domainCases=new ArrayList<DomainCase>();
				
				ArgumentJustification argJustification2=bestArgCase.getArgumentJustification();
				ArrayList<Long> domCasesJustification=argJustification2.getDomainCasesIDs();
				ArrayList<Long> argCasesJustification=argJustification2.getArgumentCasesIDs();
				ArrayList<ArgumentationScheme> argSchemes=argJustification2.getArgumentationSchemes();
				ArrayList<DialogueGraph> dialogueGraphs=argJustification2.getDialogueGraphs();
				
				//detect if it is the domain Case justification argument
				if(domCasesJustification!=null && domCasesJustification.size()>0){
					//add the domain case that justifies the position, because it is this argument: argCasefromDomainCase
					domainCases = currentPosition.getDomainCases();
				}
				// detect if it is the only premises justification argument
				else if( (argSchemes==null || argSchemes.size()==0) && 
						(dialogueGraphs==null || dialogueGraphs.size()==0) && 
						(domCasesJustification==null || domCasesJustification.size()==0) && 
						(argCasesJustification==null || argCasesJustification.size()==0) ){ 
					//premises already in the premises list
					//do not add as an argument case
				}
				else{
					argumentCases.add(bestArgCase);
				}
			}
			
			SupportSet supportSet= new SupportSet(premises, domainCases, argumentCases, schemes, distPremises, presumptions, exceptions, counterExamplesdomainCases, counterExamplesargumentCases);
			Argument argument= new Argument(System.nanoTime(), myPos.getSolution().getConclusion(), myPos.getSolution().getTimesUsed(), myPos.getSolution().getPromotesValue(), supportSet,relation);
			
			finalSupportArguments.add(argument);
			
			
		}
		
		return finalSupportArguments;
		
	}
	
	
	private Argument generateAttackArgument(Argument incArgument, String agentID){
		// Fist, assign weights in accordance with the quantity of knowledge
//		int domCases = domainCBR.getAllCasesVector().size();
//		int arguCases = argCBR.getAllCasesVector().size();
//		int totalCases = domCases + arguCases;
//		
//		if (totalCases != 0){
//			wSimilarity = (float) domCases / (float) totalCases;
//			wArgSuitFactor = (float) arguCases / (float) totalCases;
//		}else{
//			wSimilarity = 0.5f;
//			wArgSuitFactor = 0.5f;
//		}
		
		// try to generate an attack argument: Distinguishing premise or Counter Example, depending on the attack received
		
//		againstPos.getSolution().getConclusion().getID();
//		
//		SocialContext socialContext= new SocialContext(mySocialEntity, opponent, myGroup, relation);
//		//create arg case with the domain case
//		ArgumentProblem argProblem=new ArgumentProblem(new DomainContext(currentPosition.getPremises()), socialContext);
//		ArgumentSolution argSolution=new ArgumentSolution();
//		argSolution.setConclusion(currentPosition.getSolution().getConclusion());
//		argSolution.setPromotesValue(currentPosition.getSolution().getPromotesValue());
//		argSolution.setTimesUsed(currentPosition.getSolution().getTimesUsed());
//		ArgumentJustification argJustification=new ArgumentJustification();
//		argJustification.addCase(currentPosition.getDomainCase());
//		
//		ArgumentCase argCasefromDomainCase= 
//			new ArgumentCase(System.nanoTime(), new Date(System.currentTimeMillis()), argProblem, argSolution, argJustification, 0);
//		
//		
		try{
			
			//the opponent is more powerful than me
			int friendInd = getFriendIndex(agentID);
			SocialEntity opponent = myFriends.get(friendInd);
			DependencyRelation relation = depenRelations.get(friendInd);
			
			if(incArgument.getProponentDepenRelation().compareTo(relation)<0)
				return null;
			
			SocialContext socialContext= new SocialContext(mySocialEntity, opponent, myGroup, relation);
			
			//extract argument-cases that match my position
			HashMap<Integer, Premise> myPosPremises=currentPosition.getPremises();
			Solution sol=currentPosition.getSolution();
			ArrayList<SimilarArgumentCase> argCases=argCBR.getSameDomainAndSocialContextAccepted(myPosPremises,sol,socialContext);
			usedArgCases+=argCases.size();
			
			//create argument-cases with the domain-cases
			for (int i=0; i< currentPosition.getDomainCases().size(); i++){
				ArgumentProblem argProblem=new ArgumentProblem(new DomainContext(currentPosition.getDomainCases().get(i).
						getProblem().getDomainContext().getPremises()), socialContext);
				ArgumentSolution argSolution=new ArgumentSolution();
				argSolution.setConclusion(currentPosition.getSolution().getConclusion());
				argSolution.setPromotesValue(currentPosition.getSolution().getPromotesValue());
				argSolution.setTimesUsed(currentPosition.getSolution().getTimesUsed());
				ArgumentJustification argJustification=new ArgumentJustification();
			
				argJustification.addDomainCase(currentPosition.getDomainCases().get(i).getID());
			
					
				ArgumentCase argCasefromDomainCase= 
				new ArgumentCase(System.nanoTime(), new Date(System.currentTimeMillis()).toString(), argProblem, argSolution, 
						argJustification, 0);
				
				//add argument-case with the domain-cases to the list of potential attacks
				
				argCases.add(new SimilarArgumentCase(argCasefromDomainCase, domainCBR.getPremisesSimilarity(myPosPremises, 
						currentPosition.getDomainCases().get(i).getProblem().getDomainContext().getPremises())));
			}
			

			Iterator<SimilarArgumentCase> iterArgCases=argCases.iterator();
			//this list contains positions that represent the different arg cases extracted
			//just to calculate the degrees with the same function getDegrees()
			ArrayList<Position> allPositions= new ArrayList<Position>();  
			while(iterArgCases.hasNext()){
				SimilarArgumentCase simArgCase = iterArgCases.next();
				Solution solution=new Solution(simArgCase.getArgumentCase().getArgumentSolution().getConclusion(), simArgCase.getArgumentCase().getArgumentSolution().getPromotesValue(), simArgCase.getArgumentCase().getArgumentSolution().getTimesUsed());
				allPositions.add(new Position("", "", solution, simArgCase.getArgumentCase().getArgumentProblem().getDomainContext().getPremises(),null, 1f));
			}
			
			iterArgCases=argCases.iterator();
			while(iterArgCases.hasNext()){
				SimilarArgumentCase simArgCase = iterArgCases.next();
				Solution solution=new Solution(simArgCase.getArgumentCase().getArgumentSolution().getConclusion(), simArgCase.getArgumentCase().getArgumentSolution().getPromotesValue(), simArgCase.getArgumentCase().getArgumentSolution().getTimesUsed());
				
				ArrayList<Float> degreesList=argCBR.getDegrees(simArgCase.getArgumentCase().getArgumentProblem(), solution, allPositions,argCases.indexOf(simArgCase));
				float persuasivenessDegree=degreesList.get(0);
				float supportDegree=degreesList.get(1);
				float riskDegree=degreesList.get(2);
				float attackDegree=degreesList.get(3);
				float efficiencyDegree=degreesList.get(4);
				float explanatoryPower=degreesList.get(5);
								
				float argSuitabilityFactor =
//					(wPD * persuasivenessDegree + wSD * supportDegree + wRD * (1 - riskDegree) 
//							+ wAD * (1 - attackDegree) + wED * efficiencyDegree + wEP * explanatoryPower) /6;
					(wPD * persuasivenessDegree + wSD * supportDegree + wRD * (1 - riskDegree) 
							+ wAD * (1 - attackDegree) + wED * efficiencyDegree + wEP * explanatoryPower);
//				simArgCase.setSuitability((argSuitabilityFactor * wArgSuitFactor + simArgCase.getSuitability() * wSimilarity)/2);
				
				// Assign weights in accordance with the quantity of knowledge
				int domCases = similarDomainCases.size();
				int arguCases;
				try {
					arguCases = argCBR.getSameDomainAndSocialContextAccepted(myPosPremises, 
							sol, socialContext).size();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					arguCases = 0;
				}				int totalCases = domCases + arguCases;
				
				if (totalCases != 0){
					wSimilarity = (float) domCases / (float) totalCases;
					wArgSuitFactor = (float) arguCases / (float) totalCases;
				}else{
					wSimilarity = 0.5f;
					wArgSuitFactor = 0.5f;
				}
				simArgCase.setSuitability((argSuitabilityFactor * wArgSuitFactor + simArgCase.getSuitability() * wSimilarity));

				
				//calculate suitability: with the current similarity in SimilarArgCase, and the suitability obtained of argumentation
				
				
			}
			
			Collections.sort(argCases);
			
//			HashMap<Integer,Premise> hisUsefulPremises=getUsefulPremises(currentProblem.getDomainContext().getPremises(), incArgument.getSupportSet().getPremises());
			
			// SupportSet(premises, domainCases, argumentCases, schemes, distPremises, presumptions, 
			// exceptions, counterExamplesdomainCases, counterExamplesargumentCases)
			SupportSet incSS = incArgument.getSupportSet();
			Argument attack = null;
			boolean support = false;
			if (incSS.getDistinguishingPremises().isEmpty() && incSS.getPresumptions().isEmpty() && incSS.getExceptions().isEmpty()
					&& incSS.getCounterExamplesDomCases().isEmpty() && incSS.getCounterExamplesArgCases().isEmpty()){
				support = true;
			}
			
			if (support){
				// incoming argument is a support argument
				// TODO attack argumentation scheme
				if (!incSS.getDomainCases().isEmpty()){
					attack = generateCEAttack(argCases, incSS.getDomainCases().get(0).getProblem().getDomainContext().getPremises(),
							relation, agentID);
					if (attack == null){
						attack = generateDPAttack(argCases, incSS.getDomainCases().get(0).getProblem().getDomainContext().getPremises(), 
								relation, agentID);
					}
				}else if (!incSS.getArgumentCases().isEmpty()){
					attack = generateCEAttack(argCases, incSS.getArgumentCases().get(0).getArgumentProblem().getDomainContext().getPremises(),
							relation, agentID);
					if (attack == null){
						attack = generateDPAttack(argCases, incSS.getArgumentCases().get(0).getArgumentProblem().getDomainContext().getPremises(), 
								relation, agentID);
					}
				}else{
					HashMap<Integer, Premise> premHash = new HashMap<Integer, Premise>();
					for (Premise p : incSS.getPremises()){
						premHash.put(p.getID(), p);
					}
					attack = generateDPAttack(argCases, premHash, relation, agentID);
					if (attack == null){
						attack = generateCEAttack(argCases, premHash, relation, agentID);
					}
				}
			}else{
				// incoming argument is an attack argument
				// TODO attack presumptions and exceptions
				if (!incSS.getCounterExamplesDomCases().isEmpty()){
					attack = generateCEAttack(argCases, incSS.getCounterExamplesDomCases().get(0).getProblem().getDomainContext().getPremises(),
							relation, agentID);
					if (attack == null){
						attack = generateDPAttack(argCases, incSS.getCounterExamplesDomCases().get(0).getProblem().getDomainContext().getPremises(), 
								relation, agentID);
					}
				}else if (!incSS.getCounterExamplesArgCases().isEmpty()){
					attack = generateCEAttack(argCases, incSS.getCounterExamplesArgCases().get(0).getArgumentProblem().getDomainContext().getPremises(),
							relation, agentID);
					if (attack == null){
						attack = generateDPAttack(argCases, incSS.getCounterExamplesArgCases().get(0).getArgumentProblem().getDomainContext().getPremises(), 
								relation, agentID);
					}
				}else{
					HashMap<Integer, Premise> distPremHash = new HashMap<Integer, Premise>();
					for (Premise p : incSS.getDistinguishingPremises()){
						distPremHash.put(p.getID(), p);
					}
					attack = generateDPAttack(argCases, distPremHash, relation, agentID);
					if (attack == null){
						attack = generateCEAttack(argCases, distPremHash, relation, agentID);
					}
				}
			}
			
			if (attack != null)
				attack.setAttackingToArgID(incArgument.getID());
			
			return attack;
		
		}catch(Exception e){
			logger.error(this.getName()+": Exception in generateAttackArgument\n"+e.toString());
			e.printStackTrace();
		}
		return null;
			
	}
	
	public Argument generateDPAttack(ArrayList<SimilarArgumentCase> argCases, HashMap<Integer, Premise> hisPremises, 
			DependencyRelation relation, String agentID){
		HashMap<Integer, Premise> hisUsefulPremises = getUsefulPremises(currentProblem.getDomainContext().getPremises(), hisPremises);
		Iterator<SimilarArgumentCase> iterArgCases=argCases.iterator();
		while(iterArgCases.hasNext()){
			SimilarArgumentCase simArgCase = iterArgCases.next();
			HashMap<Integer,Premise> myPremises=simArgCase.getArgumentCase().getArgumentProblem().getDomainContext().getPremises();
			HashMap<Integer,Premise> myUsefulPremises=getUsefulPremises(currentProblem.getDomainContext().getPremises(), myPremises);
			ArrayList<Premise> distPremises=getDistinguishingPremises(myUsefulPremises, hisUsefulPremises);
			ArrayList<Premise> hisdistPremises=getDistinguishingPremises(hisUsefulPremises, myUsefulPremises);
			
			if(distPremises.size()>=hisdistPremises.size()){//generate attack
				ArrayList<Premise> premises=new ArrayList<Premise>();
				Iterator<Premise> iterPremises=currentPosition.getPremises().values().iterator();
				while(iterPremises.hasNext()){
					premises.add(iterPremises.next());
				}
				ArrayList<DomainCase> domainCases=new ArrayList<DomainCase>();
				ArrayList<ArgumentCase> argumentCases=new ArrayList<ArgumentCase>();
				ArrayList<ArgumentationScheme> schemes=new ArrayList<ArgumentationScheme>();
				//ArrayList<Premise> distPremises=new ArrayList<Premise>();
				ArrayList<Premise> presumptions=new ArrayList<Premise>();
				ArrayList<Premise> exceptions=new ArrayList<Premise>();
				ArrayList<DomainCase> counterExamplesdomainCases=new ArrayList<DomainCase>();
				ArrayList<ArgumentCase> counterExamplesargumentCases=new ArrayList<ArgumentCase>();
				SupportSet supportSet= new SupportSet(premises, domainCases, argumentCases, schemes, distPremises, presumptions, 
						exceptions, counterExamplesdomainCases, counterExamplesargumentCases);
				Argument argument= new Argument(System.nanoTime(), currentPosition.getSolution().getConclusion(), 
						currentPosition.getSolution().getTimesUsed(), currentPosition.getSolution().getPromotesValue(), supportSet,relation);
				if(!argumentPreviouslyUsed(argument,myUsedAttackArguments.get(agentID))){
					logger.info(this.getName()+": "+" distinguishing premises attack argument against: "+agentID+"\n");
					return argument;
				}

			}
			
		}
		return null;
	}
	
	public Argument generateCEAttack(ArrayList<SimilarArgumentCase> argCases, HashMap<Integer, Premise> hisCasePremises, 
			DependencyRelation relation, String agentID){
		
		HashMap<Integer,Premise> hisUsefulPremises=getUsefulPremises(currentProblem.getDomainContext().getPremises(), hisCasePremises);
		Iterator<SimilarArgumentCase> iterArgCases=argCases.iterator();
		while(iterArgCases.hasNext()){
			SimilarArgumentCase simArgCase = iterArgCases.next();
			HashMap<Integer,Premise> myPremises=simArgCase.getArgumentCase().getArgumentProblem().getDomainContext().getPremises();
			HashMap<Integer,Premise> myUsefulPremises=getUsefulPremises(currentProblem.getDomainContext().getPremises(), myPremises);
			
			boolean find = false;
			Iterator<Premise> hisPrem = hisUsefulPremises.values().iterator();
			while(hisPrem.hasNext() && !find){
				Premise hp = hisPrem.next();
				if (myUsefulPremises.get(hp.getID()) == null)
					find = true;
			}
			
			if(find == false){//generate attack
				ArrayList<Premise> premises=new ArrayList<Premise>();
				Iterator<Premise> iterPremises=currentPosition.getPremises().values().iterator();
				while(iterPremises.hasNext()){
					premises.add(iterPremises.next());
				}
				ArrayList<DomainCase> domainCases=new ArrayList<DomainCase>();
				ArrayList<ArgumentCase> argumentCases=new ArrayList<ArgumentCase>();
				ArrayList<ArgumentationScheme> schemes=new ArrayList<ArgumentationScheme>();
				ArrayList<Premise> distPremises=new ArrayList<Premise>();
				ArrayList<Premise> presumptions=new ArrayList<Premise>();
				ArrayList<Premise> exceptions=new ArrayList<Premise>();
				ArrayList<DomainCase> counterExamplesdomainCases=new ArrayList<DomainCase>();
				ArrayList<ArgumentCase> counterExamplesargumentCases=new ArrayList<ArgumentCase>();
				counterExamplesargumentCases.add(simArgCase.getArgumentCase());
				SupportSet supportSet= new SupportSet(premises, domainCases, argumentCases, schemes, distPremises, presumptions, exceptions, counterExamplesdomainCases, counterExamplesargumentCases);
				Argument argument= new Argument(System.nanoTime(), currentPosition.getSolution().getConclusion(), 
						currentPosition.getSolution().getTimesUsed(), currentPosition.getSolution().getPromotesValue(), supportSet,relation);
				if(!argumentPreviouslyUsed(argument,myUsedAttackArguments.get(agentID))){
					logger.info(this.getName()+": "+" counter-example attack argument against: "+agentID+"\n");
					return argument;
				}

			}
			
		}
		return null;
	}
	
	private HashMap<Integer,Premise> getUsefulPremises(HashMap<Integer,Premise> problemPremises, HashMap<Integer,Premise> myPremises){
		
		Iterator<Premise> iterMyPremises=myPremises.values().iterator();
		HashMap<Integer,Premise> usefulPremises=new HashMap<Integer, Premise>();
		while(iterMyPremises.hasNext()){
			Premise premise=iterMyPremises.next();
			Premise problemPremise=problemPremises.get(premise.getID());
			if(problemPremise!=null && problemPremise.getContent().equalsIgnoreCase(premise.getContent())){//this premise is in the problem
				usefulPremises.put(premise.getID(), premise);
			}
		}
		return usefulPremises;
	}
	
	
//	private HashMap<Integer,Premise> getUsefulPremises(HashMap<Integer,Premise> problemPremises, ArrayList<Premise> myPremises){
//		
//		Iterator<Premise> iterHisPremises=myPremises.iterator();
//		HashMap<Integer,Premise> usefulPremises=new HashMap<Integer, Premise>();
//		while(iterHisPremises.hasNext()){
//			Premise premise=iterHisPremises.next();
//			Premise problemPremise=problemPremises.get(premise.getID());
//			if(problemPremise!=null && problemPremise.getContent().equalsIgnoreCase(premise.getContent())){//this premise is in the problem
//				usefulPremises.put(premise.getID(), premise);
//			}
//		}
//		return usefulPremises;
//	}
	
	private ArrayList<Premise> getDistinguishingPremises(HashMap<Integer,Premise> myPremises, HashMap<Integer,Premise> hisPremises){
		ArrayList<Premise> distPremises=new ArrayList<Premise>();
		Iterator<Premise> iterUsefulPremises=myPremises.values().iterator();
		while(iterUsefulPremises.hasNext()){
			Premise premise=iterUsefulPremises.next();
			Premise premise2=hisPremises.get(premise.getID());
			if(premise2!=null && premise2.getContent().equalsIgnoreCase(premise.getContent())){
				
			}
			else
				distPremises.add(premise);
		}
		return distPremises;
	}
	
	/**
	 * Returns true if the given argument is on the given ArrayList of arguments, otherwise returns false 
	 * @param arg 
	 * @param myArguments
	 * @return true if the given argument is on the given ArrayList of arguments, otherwise returns false
	 */
	private boolean argumentPreviouslyUsed(Argument arg, ArrayList<Argument> myArguments){
		
		if(myArguments==null)
			return false;
		
		for(int i=myArguments.size()-1;i>=0;i--){
			Argument currentArg=myArguments.get(i);
			if(arg.getHasConclusion().getID()==currentArg.getHasConclusion().getID() &&
					arg.getPromotesValue().equalsIgnoreCase(currentArg.getPromotesValue())){
				
				SupportSet argSupp=arg.getSupportSet();
				SupportSet currentArgSupp=currentArg.getSupportSet();
				
				if(argSupp.getArgumentCases().size()>0 && argSupp.getArgumentCases().size()==currentArgSupp.getArgumentCases().size()){
					ArrayList<ArgumentCase> cases=argSupp.getArgumentCases();
					ArrayList<ArgumentCase> cases2=currentArgSupp.getArgumentCases();
					if(cases!=null && cases2!=null && cases.size()>0 && cases2.size()>0){
						//logger.info(this.getName()+": "+" argument cases\n");
						if(cases.get(0).equals(cases2.get(0))){
							logger.info(this.getName()+": "+" SAME arg cases\n");
							return true;
						}
					}
				}
				if(argSupp.getDomainCases().size()>0 && argSupp.getDomainCases().size()==currentArgSupp.getDomainCases().size()){
					ArrayList<DomainCase> cases=argSupp.getDomainCases();
					ArrayList<DomainCase> cases2=currentArgSupp.getDomainCases();
					if(cases!=null && cases2!=null && cases.size()>0 && cases2.size()>0){
						//logger.info(this.getName()+": "+" domain cases\n");
						if(cases.get(0).equals(cases2.get(0))){
							logger.info(this.getName()+": "+" SAME domain cases\n");
							return true;
						}
					}
				}
				if(argSupp.getCounterExamplesDomCases().size()>0 && argSupp.getCounterExamplesDomCases().size()==currentArgSupp.getCounterExamplesDomCases().size()){
					ArrayList<DomainCase> cases=argSupp.getCounterExamplesDomCases();
					ArrayList<DomainCase> cases2=currentArgSupp.getCounterExamplesDomCases();
					if(cases!=null && cases2!=null && cases.size()>0 && cases2.size()>0){
						//logger.info(this.getName()+": "+" counter example cases\n");
						if(cases.get(0).equals(cases2.get(0))){
							logger.info(this.getName()+": "+" SAME counter example domain cases\n");
							return true;
						}
					}
				}
				if(argSupp.getCounterExamplesArgCases().size()>0 && argSupp.getCounterExamplesArgCases().size()==currentArgSupp.getCounterExamplesArgCases().size()){
					ArrayList<ArgumentCase> cases=argSupp.getCounterExamplesArgCases();
					ArrayList<ArgumentCase> cases2=currentArgSupp.getCounterExamplesArgCases();
					if(cases!=null && cases2!=null && cases.size()>0 && cases2.size()>0){
						//logger.info(this.getName()+": "+" counter example cases\n");
						if(cases.get(0).equals(cases2.get(0))){
							logger.info(this.getName()+": "+" SAME counter example argument cases\n");
							return true;
						}
					}
				}
				if(argSupp.getDistinguishingPremises().size()>0 && argSupp.getDistinguishingPremises().size()==currentArgSupp.getDistinguishingPremises().size()){
					ArrayList<Premise> premises=argSupp.getDistinguishingPremises();
					ArrayList<Premise> premises2=argSupp.getDistinguishingPremises();
					if(premises!=null && premises2!=null && premises.size()>0 && premises2.size()>0 && premises.size()==premises2.size()){
						//logger.info(this.getName()+": "+" distinguishing premises\n");
						boolean samePremises=true;
						for(int prem=0;prem<premises.size();prem++){
							if(premises.get(prem).getID()!=premises2.get(prem).getID()){
								samePremises=false;
							}
						}
						if(samePremises){
							logger.info(this.getName()+": "+" SAME distinguishing premises\n");
							return true;
						}
					}
				}
				
				
			}
		}
		logger.info("**********************"+this.getName()+": "+" argument not previously used");
		return false;
	}
	
	private int getPreferredValueIndex(String value){
		for(int i=0;i<preferedValues.size();i++){
			if(preferedValues.get(i).equalsIgnoreCase(value))
				return i;
		}
		return -1;
	}
	
//	private SocialEntity getSocialEntityFriend(AgentID agentID){
//		Iterator<SocialEntity> iterFriends=myFriends.iterator();
//		while(iterFriends.hasNext()){
//			SocialEntity friend=iterFriends.next();
//			if(friend.getName().equalsIgnoreCase(agentID.toString())){
//				return friend;
//			}
//		}
//		return null;
//	}
	
	private int getFriendIndex(String agentID){
		Iterator<SocialEntity> iterFriends=myFriends.iterator();
		while(iterFriends.hasNext()){
			SocialEntity friend=iterFriends.next();
			if(friend.getName().equalsIgnoreCase(agentID)){
				return myFriends.indexOf(friend);
			}
		}
		System.err.println(myID+": getFriendIndex not found "+agentID);
		return -1;
	}
	
//	private DependencyRelation getDependencyRelationFriend(AgentID agentID){
//		Iterator<DependencyRelation> iterRels=depenRelations.iterator();
//		while(iterRels.hasNext()){
//			DependencyRelation rel=iterRels.next();
//			if(friend.getName().equalsIgnoreCase(agentID.toString())){
//				return friend;
//			}
//		}
//		return null;
//	}
	
	/*
	 * Methods to access to Commitment Store
	 */
	
	
	private void addArgument(Argument arg, String dialogueID){
		sendMessage(commitmentStoreID, ADDARGUMENT, dialogueID, arg);
	}
	
//	private Argument getArgument(String agentID, String dialogueID){
//		sendMessage(commitmentStoreID, GETARGUMENT, dialogueID, null);
//		ArrayList<String> locutions=new ArrayList<String>();
//		locutions.add(GETARGUMENT);
//		ACLMessage msg=listenAndReviseQueue(locutions,2);// careful, only look the queue once
//		Argument arg=null;
//		if(msg!=null)
//			arg= (Argument) msg.getContentObject();
//		return arg;
//	}
	
	private void removeArgument(Argument arg, String dialogueID){
		sendMessage(commitmentStoreID, REMOVEARGUMENT, dialogueID, arg);
	}
	
	
	
	private ACLMessage addPosition(Position pos, String dialogueID){
		myUsedLocutions++;
		return sendMessage(commitmentStoreID, ADDPOSITION, dialogueID, pos);
	}
	
	
	
	/**
	 * Returns a list of positions that are different from the defended position and also are not asked yet.
	 * @param positions {@link ArrayList} with all the positions in the dialogue
	 * @return
	 */
	private ArrayList<Position> getDifferentPositions(ArrayList<Position> positions){
		ArrayList<Position> differentPositions= new ArrayList<Position>();
		if(positions==null || positions.size()==0)
			return new ArrayList<Position>();
		//if it has not position, all positions are considered different
		if(currentPosition==null)
			return positions;
		try{
		
		Iterator<Position> iterPositions=positions.iterator();
		
		while(iterPositions.hasNext()){
			Position pos=iterPositions.next();
			if(currentPosition.getSolution().getConclusion().getID()!=pos.getSolution().getConclusion().getID()){
				Iterator<Position> iterAskedPositions=askedPositions.iterator();
				boolean asked=false;
				while(iterAskedPositions.hasNext()){
					Position askedPos=iterAskedPositions.next();
					if(askedPos!=null){
						String askedPosAgentID=askedPos.getAgentID();
						String posAgentID=pos.getAgentID();
						if(askedPosAgentID.equalsIgnoreCase(posAgentID) &&
								askedPos.getSolution().getConclusion().getID()==pos.getSolution().getConclusion().getID() 
								&& askedPos.getSolution().getPromotesValue().equalsIgnoreCase(pos.getSolution().getPromotesValue())){
							logger.info(this.getName()+": position already asked");
							asked=true;
							break;
						}
					}
						
				}
				if(!asked)
					differentPositions.add(pos);
					
			}
		}
		}catch(Exception e){
			logger.error(this.getName()+": Exception in getDifferentPositions\n"+e.toString());
			e.printStackTrace();
		}
		
		return differentPositions;
	}
	
	private void removePosition(String dialogueID){
		sendMessage(commitmentStoreID, REMOVEPOSITION, dialogueID, currentPosition);
	}
	
	/**
	 * Adds a dialogue to commitment store. If it exist a dialogue with the same id, it is overwritten
	 * @param dialogue to be added
	 */
	private void addDialogue(Dialogue dialogue){
		sendMessage(commitmentStoreID, ADDDIALOGUE, dialogue.getDialogueID(), dialogue);
	}

	/**
	 * Adds this agent to the dialogue specified in commitment store
	 * @param dialogueID id of the dialogue to join
	 */
	private ACLMessage enterDialogue(String dialogueID){
		
		myUsedLocutions++;
		return sendMessage(commitmentStoreID, ENTERDIALOGUE, dialogueID, null);
	}
	
//	/**
//	 * Returns the dialogue with the given dialogue ID
//	 * @param dialogueID
//	 * @return the dialogue with the given dialogue ID
//	 */
//	private Dialogue getDialogue(String dialogueID){
//		sendMessage(commitmentStoreID, GETDIALOGUE, dialogueID, null);
//		ArrayList<String> locutions=new ArrayList<String>();
//		locutions.add(GETDIALOGUE);
//		ACLMessage msg=null;
//		while(msg==null){//careful waiting forever if CS does not respond
//			msg=listenAndReviseQueue(locutions,2);
//		}
//		Dialogue dia = (Dialogue) msg.getContentObject();
//		return dia;
//	}
	
	/**
	 * Removes the agentID from the list of the dialogue.
	 * @param dialogueID
	 */
	private ACLMessage leaveDialogue(String dialogueID){
		myUsedLocutions++;
		return sendMessage(commitmentStoreID, LEAVEDIALOGUE, dialogueID, null);
	}
	
	
	
	
	
	
	/**
	 * Returns the list of IDs of the given domain cases
	 * @param domainCases
	 * @return the list of IDs of the given domain cases
	 */
	private ArrayList<Long> domCasestoLongIDs(ArrayList<DomainCase> domainCases){
		ArrayList<Long> longListIDs=new ArrayList<Long>();
		Iterator<DomainCase> iterDomCases=domainCases.iterator();
		while(iterDomCases.hasNext()){
			longListIDs.add(iterDomCases.next().getID());
		}
		
		return longListIDs;
	}
	
	/**
	 * Returns the list of IDs of the given argument cases
	 * @param argCases
	 * @return the list of IDs of the given argument cases
	 */
	private ArrayList<Long> argCasestoLongIDs(ArrayList<ArgumentCase> argCases){
		ArrayList<Long> longListIDs=new ArrayList<Long>();
		Iterator<ArgumentCase> iterArgCases=argCases.iterator();
		while(iterArgCases.hasNext()){
			longListIDs.add(iterArgCases.next().getID());
		}
		return longListIDs;
	}
	
	
	
	/**
	 * Adds the final solution to the current ticket and adds it in the domain case-base.
	 * Also, stores all the generated argumentation data in the argumentation case-base. 
	 * Finally, makes a cache of the domain CBR and the argumentation CBR.
	 * @param solution
	 */
	private void updateCBs(Solution solution){
		//add the solution to the ticket and add the ticket to domainCBR
		
		ArrayList<Solution> solutions=new ArrayList<Solution>();
		solutions.add(solution);
		currentDomCase2Solve.setSolutions(solutions);
		synchronized(domainCBR){
			boolean caseAdded=domainCBR.addCase(currentDomCase2Solve);
			if(caseAdded){
				logger.info(this.getName()+": "+"Domain-case Introduced");
			}
			else{
				logger.info(this.getName()+": "+"Domain-case Updated");
			}
		}
		//add argument cases generated during the dialogue
		
		DomainContext domainContext=new DomainContext(currentDomCase2Solve.getProblem().getDomainContext().getPremises());
		
		for(int i=0;i<myFriends.size();i++){
			//logger.info(this.getName()+": "+"friend="+i);
			SocialEntity friend=myFriends.get(i);
			DependencyRelation relation=depenRelations.get(i);
			SocialContext socialContext=new SocialContext(mySocialEntity, friend, myGroup, relation);
			
			ArrayList<DialogueGraph> dialogues=dialogueGraphs.get(friend.getName());
			
			//support arguments
			ArrayList<Argument> listArgs=storeArguments.get(friend.getName());
			if(listArgs!=null){//if there are used support arguments with this friend
				//logger.info(this.getName()+": "+"friend="+i+" -> "+listArgs.size()+" support args to add.");
				Iterator<Argument> iterArgs=listArgs.iterator();
				while(iterArgs.hasNext()){
					Argument arg=iterArgs.next();
					ArgumentProblem argProb=new ArgumentProblem(domainContext, socialContext);
					ArrayList<Premise> distP = new ArrayList<Premise>();
					for (Argument a : arg.getReceivedAttacksDistPremises()){
						distP.addAll(a.getSupportSet().getDistinguishingPremises());
					}
					ArrayList<Long> countExDom = new ArrayList<Long>();
					ArrayList<Long> countExArg = new ArrayList<Long>();
					for (Argument a : arg.getReceivedAttacksCounterExamples()){
						countExDom.addAll(domCasestoLongIDs(a.getSupportSet().getCounterExamplesDomCases()));
						countExArg.addAll(argCasestoLongIDs(a.getSupportSet().getCounterExamplesArgCases()));
					}
					// TODO put presumptions and exceptions
					ArgumentSolution argSol=new ArgumentSolution(ArgumentType.INDUCTIVE, arg.getAcceptabilityState(), 
							distP, new ArrayList<Premise>(), new ArrayList<Premise>(), countExDom, countExArg);
					argSol.setPromotesValue(arg.getPromotesValue());
					argSol.setTimesUsed(arg.getTimesUsedConclusion());
					argSol.setConclusion(arg.getHasConclusion());
					
					ArrayList<Long> domainCasesIDs=new ArrayList<Long>();
					Iterator<DomainCase> iterDomCases=arg.getSupportSet().getDomainCases().iterator();
					while(iterDomCases.hasNext()){
						domainCasesIDs.add(iterDomCases.next().getID());
					}
					ArrayList<Long> argCasesIDs=new ArrayList<Long>();
					Iterator<ArgumentCase> iterArgCases=arg.getSupportSet().getArgumentCases().iterator();
					while(iterArgCases.hasNext()){
						argCasesIDs.add(iterArgCases.next().getID());
					}
					
					// take the dialogues where this argument is implied
					ArrayList<DialogueGraph> diags=new ArrayList<DialogueGraph>();
					if(dialogues!=null){
						Iterator<DialogueGraph> iterDialogues=dialogues.iterator();
						while(iterDialogues.hasNext()){
							DialogueGraph diag=iterDialogues.next();
							if(diag.contains(arg.getID()))
								diags.add(diag);
						}
					}
					
					ArgumentJustification argJust=new ArgumentJustification(domainCasesIDs, argCasesIDs , arg.getSupportSet().getArgumentationSchemes(), diags);
					
					ArgumentCase newArgCase= new ArgumentCase(arg.getID(), new Date(arg.getID()).toString(), argProb, argSol, argJust, 1);
					boolean argCaseAdded=argCBR.addCase(newArgCase);
					if(argCaseAdded){
						logger.info(this.getName()+": "+"friend="+i+" -> "+"Argument-case Introduced");
					}
					else{
						logger.info(this.getName()+": "+"friend="+i+" -> "+"Argument-case Updated");
					}
				}
			}
			
		}

	}
	
	
	/**
	 * Sends a message to the given agent.
	 * @param agentID String with the agent ID to send the message
	 * @param locution String with the locution of the message to send
	 * @param dialogueID String with dialogueID of the message
	 * @param contentObject Serializable with an object to attach to the message
	 */
	private ACLMessage sendMessage(String agentID, String locution, String dialogueID, Serializable contentObject){
		
		ACLMessage msg = new ACLMessage();
		msg.setSender(getAid());
		msg.setReceiver(new AgentID(agentID));
		msg.setConversationId(dialogueID);
		msg.setPerformative(ACLMessage.INFORM);
		
		if(locution.contains("=")){
			StringTokenizer tokenizer=new StringTokenizer(locution,"=");
			String perf=tokenizer.nextToken();
			String contentAgentID=tokenizer.nextToken();
			locution=perf;
			msg.setHeader("agentID", contentAgentID);
		}
		msg.setHeader("locution", locution);
		
		if(contentObject!=null){
			try {
				msg.setContentObject(contentObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//System.out.println(this.getName()+": "+"message to send: "+"to: "+msg.getReceiver().toString()+" dialogueID: "+msg.getConversationId()+" locution: "+msg.getHeaderValue("locution"));
		logger.info(this.getName()+": "+"message to send: "+"to: "+msg.getReceiver().getLocalName()+" dialogueID: "+msg.getConversationId()+" locution: "+msg.getHeaderValue("locution"));
		//send(msg);
		return msg;
	}
	
	
	public int getMyUsedLocutions(){
		return myUsedLocutions;
	}
	
	public int getNumberDomainCases(){
		return domainCBR.getAllCasesList().size();
	}
	
	public int getNumberArgumentCases(){
		return argCBR.getAllCasesVector().size();
	}
	
	
	public float getDialogueTime(){
		return dialogueTime;
	}
	
	public Position getLastPositionBeforeNull(){
		return lastPositionBeforeNull;
	}
	
	public Position getCurrentPosition(){
		return currentPosition;
	}
	
	public String getCurrentDialogueID(){
		return currentDialogueID;
	}
	
	public int getAccepted(){
		return currentPosAccepted;
	}
	
	public int getAgreement(){
		return agreementReached;
	}
	
	public int getAcceptanceFrequency(){
		return acceptanceFrequency;
	}
	
	public boolean isAlive(){
		return alive;
	}
	
	public int getVotes(){
		return votesReceived;
	}
	
	public int getUsedArgCases(){
		return usedArgCases;
	}
	
}
