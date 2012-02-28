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
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.AcceptabilityStatus;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgNode;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgNode.NodeType;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Argument;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentJustification;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentProblem;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentSolution;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentSolution.ArgumentType;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentationScheme;
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

/**
 * This class implements the argumentative agent as a CAgent. It can join in an
 * argumentation dialogue to solve a problem.
 * 
 * @author Jaume Jordan
 * 
 */
public class ArgCAgent extends CAgent {

	private final int multTimeFactor = 10; // 1000 will be seconds
	private final String ENTERDIALOGUE = "ENTERDIALOGUE";
	private final String WITHDRAWDIALOGUE = "WITHDRAWDIALOGUE";
	private final String WHY = "WHY";
	private final String NOCOMMIT = "NOCOMMIT";
	private final String ASSERT = "ASSERT";
	private final String ACCEPT = "ACCEPT";
	private final String ATTACK = "ATTACK";
	private final String LOCUTION = "locution";

	private final String ADDPOSITION = "ADDPOSITION";
	private final String GETALLPOSITIONS = "GETALLPOSITIONS";

	private String myID;
	private ArrayList<String> preferedValues;
	private SocialEntity mySocialEntity;
	private ArrayList<SocialEntity> myFriends;
	private ArrayList<DependencyRelation> depenRelations;
	private Group myGroup;
	private String commitmentStoreID;

	private DomainCBR domainCBR;
	private ArgCBR argCBR;
	private float domCBRthreshold;
	ArrayList<SimilarDomainCase> similarDomainCases;

	// weights
	private float wSimilarity;
	private float wArgSuitFactor;

	// SF =( (wPD * PD + wSD * SD + wRD * (1 - RD) + wAD * (1 - AD) + wED * ED +
	// wEP * EP) )/6
	private float wPD;
	private float wSD;
	private float wRD;
	private float wAD;
	private float wED;
	private float wEP;

	private String currentDialogueID;
	private Problem currentProblem;
	private DomainCase currentDomCase2Solve;
	private Position currentPosition;
	private Position lastPositionBeforeNull;
	private HashMap<String, ArrayList<DialogueGraph>> dialogueGraphs;
	private DialogueGraph currentDialogueGraph;
	private String subDialogueAgentID = "";
	private ArrayList<Position> differentPositions;

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
	private HashMap<String, ArrayList<Position>> attendedWhyPetitions;
	private HashMap<String, ArrayList<Argument>> mySupportArguments;
	private int myUsedLocutions = 0;

	// we do not use a list of attack arguments because we only generate a
	// counter example or distinguishing premises arguments
	// and that don't cause efficiency problems
	// private HashMap<String,ArrayList<Argument>> myAttackArguments;
	private HashMap<String, ArrayList<Argument>> myUsedSupportArguments;
	private HashMap<String, ArrayList<Argument>> myUsedAttackArguments;

	private HashMap<String, ArrayList<Argument>> storeArguments;

	/**
	 * Main method to build Argumentative Agents
	 * 
	 * @param aid
	 *            Agent {@link AID}
	 * @param isInitiator
	 *            Flag to set the Initiator role
	 * @param mySocialEntity
	 *            {@link SocialEntity} of the Agent
	 * @param myFriends
	 *            {@link ArrayList} with the SocialEntities that represent the
	 *            Agent's friends
	 * @param depenRelations
	 *            {@link ArrayList} with the Agent's dependency relations with
	 *            its friends
	 * @param group
	 *            {@link Group} that the Agent belongs to
	 * @param commitmentStoreID
	 *            ID of the {@link CommitmentStore}
	 * @param testerAgentID
	 *            ID of the TesterAgent to run tests in the system
	 * @param iniDomCasesFilePath
	 *            File with the original DomainCases case-base
	 * @param finDomCasesFilePath
	 *            File to write the updated DomainCases case-base
	 * @param iniArgCasesFilePath
	 *            File with the original ArgumentCases case-base
	 * @param finArgCasesFilePath
	 *            File with the updated ArgumentCases case-base
	 * @param threshold
	 *            Similarity threshold over which a DomainCase is retrieved from
	 *            the DomainCases case-base
	 * @param wPD
	 *            Weight of the Persuasion Degree
	 * @param wSD
	 *            Weight of the Support Degree
	 * @param wRD
	 *            Weight of the Risk Degree
	 * @param wAD
	 *            Weight of the Attack Degree
	 * @param wED
	 *            Weight of the Efficiency Degree
	 * @param wEP
	 *            Weight of the Explanatory Power
	 * @throws Exception
	 */
	public ArgCAgent(AgentID aid, boolean isInitiator, SocialEntity mySocialEntity, ArrayList<SocialEntity> myFriends,
			ArrayList<DependencyRelation> depenRelations, Group group, String commitmentStoreID, String testerAgentID,
			String iniDomCasesFilePath, String finDomCasesFilePath, int domCBRindex, float domCBRthreshold,
			String iniArgCasesFilePath, String finArgCasesFilePath, float wPD, float wSD, float wRD, float wAD,
			float wED, float wEP) throws Exception {

		super(aid);

		this.myID = aid.getLocalName();
		this.mySocialEntity = mySocialEntity;
		this.preferedValues = mySocialEntity.getValPref().getValues();
		this.myFriends = myFriends;
		this.depenRelations = depenRelations;
		this.myGroup = group;
		this.commitmentStoreID = commitmentStoreID;
		this.domCBRthreshold = domCBRthreshold;
		this.wPD = wPD;
		this.wSD = wSD;
		this.wRD = wRD;
		this.wAD = wAD;
		this.wED = wED;
		this.wEP = wEP;

		domainCBR = new DomainCBR(iniDomCasesFilePath, finDomCasesFilePath, domCBRindex);
		argCBR = new ArgCBR(iniArgCasesFilePath, finArgCasesFilePath);

		currentDialogueID = null;
		currentProblem = null;
		currentDomCase2Solve = null;
		lastPositionBeforeNull = null;
		currentPosition = null;
		dialogueGraphs = new HashMap<String, ArrayList<DialogueGraph>>();
		currentDialogueGraph = null;

		myPositions = null;
		positionsGenerated = false;
		askedPositions = new ArrayList<Position>();
		attendedWhyPetitions = new HashMap<String, ArrayList<Position>>();
		mySupportArguments = new HashMap<String, ArrayList<Argument>>();
		myUsedSupportArguments = new HashMap<String, ArrayList<Argument>>();
		myUsedAttackArguments = new HashMap<String, ArrayList<Argument>>();
		storeArguments = new HashMap<String, ArrayList<Argument>>();
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		try {
			// Clean end file
			FileWriter fstream = new FileWriter(this.getName(), false);
			BufferedWriter outFile = new BufferedWriter(fstream);
			// Close the output stream
			outFile.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		/**
		 * This class extends the Argumentation Participant protocol
		 * implementing all the needed functions to perform the dialogue.
		 * 
		 * @author Jaume Jordan
		 * 
		 */
		class myArgumentation extends Argumentation_Participant {

			@Override
			protected void doOpenDialogue(CProcessor myProcessor, ACLMessage msg) {
				currentDomCase2Solve = (DomainCase) msg.getContentObject();
				currentDialogueID = msg.getConversationId();
			}

			@Override
			protected boolean doEnterDialogue(CProcessor myProcessor, ACLMessage msg) {

				agreementReached = 0;
				acceptanceFrequency = 0;
				usedArgCases = 0;

				ACLMessage msg2 = enter_dialogue(currentDomCase2Solve, currentDialogueID);
				copyMessages(msg, msg2);

				logger.info(myID + ": message " + msg.getHeaderValue(LOCUTION) + " receiver: " + msg.getReceiver().name);
				if (msg.getHeaderValue(LOCUTION).equalsIgnoreCase(ENTERDIALOGUE))
					return true;
				else
					return false;
			}

			@Override
			protected boolean doPropose(CProcessor myProcessor, ACLMessage msg) {
				if (!positionsGenerated)
					myPositions = generatePositions(currentProblem);
				ACLMessage msg2;
				currentPosition = null;
				if (myPositions != null && myPositions.size() > 0) {
					currentPosition = myPositions.remove(0);// extract the first
															// position and
															// remove it from
															// list
					if (currentPosition != null)
						lastPositionBeforeNull = new Position(currentPosition.getAgentID(),
								currentPosition.getDialogueID(), currentPosition.getSolution(),
								currentPosition.getPremises(), currentPosition.getDomainCases(),
								currentPosition.getDomainCaseSimilarity());
					;
					currentPosAccepted = 0;
					mySupportArguments = new HashMap<String, ArrayList<Argument>>();
					myUsedSupportArguments = new HashMap<String, ArrayList<Argument>>();
					myUsedAttackArguments = new HashMap<String, ArrayList<Argument>>();
					// dialogueGraphs= new HashMap<String,
					// ArrayList<DialogueGraph>>();
					currentDialogueGraph = null;
				}
				if (currentPosition != null) {

					msg2 = propose(currentPosition, currentDialogueID);
					copyMessages(msg, msg2);
					return true;
				} else {// no position generated, withdraw
					msg2 = withdraw_dialogue();
					copyMessages(msg, msg2);
					return false;
				}

			}

			@Override
			protected String doAssert(CProcessor myProcessor, ACLMessage msg, String whyAgentID) {

				ACLMessage msg2;

				// clean other possible WHY sent by the same agent and attend
				// only one
				ArrayList<String> locutions = new ArrayList<String>();
				locutions.add(WHY);
				int removed = myProcessor.cleanMessagesQueue(whyAgentID, LOCUTION, locutions);
				logger.info(myID + ": " + removed + " messages removed");

				ArrayList<Position> myPositionsAsked = attendedWhyPetitions.get(whyAgentID);
				if (myPositionsAsked != null && myPositionsAsked.contains(currentPosition)) {
					// I have already replied this agent with my current
					// position, do not reply him
					msg2 = nothingMsg();
					copyMessages(msg, msg2);
					return "WAIT_CENTRAL"; // send message with locution NOTHING
											// to noOne (non existing agent)
				} else {
					// try to generate a support argument
					// 1) Argument-cases 2) domain-cases 3) premises

					ArrayList<Argument> supportArgs = generateSupportArguments(currentPosition, whyAgentID);
					Argument arg = null;
					if (!supportArgs.isEmpty())
						arg = supportArgs.remove(0);
					mySupportArguments.put(whyAgentID, supportArgs);

					if (arg != null) { // assert the argument
						logger.info("*********************" + myID + ": "
								+ " received WHY, generating suport arg. ASSERTING");

						msg2 = asserts(whyAgentID, arg);
						copyMessages(msg, msg2);

						// I am now talking only with this agent
						subDialogueAgentID = whyAgentID;

						// add argument to myUsedSupportArguments
						ArrayList<Argument> supportArgsUsed = myUsedSupportArguments.get(whyAgentID);
						if (supportArgsUsed == null)
							supportArgsUsed = new ArrayList<Argument>();
						supportArgsUsed.add(arg);
						myUsedSupportArguments.put(whyAgentID, supportArgsUsed);

						// add argument to dialogue graph, it is the first
						ArgNode argNode = new ArgNode(arg.getID(), new ArrayList<Long>(), -1, ArgNode.NodeType.FIRST);
						currentDialogueGraph = new DialogueGraph();
						currentDialogueGraph.addNode(argNode);

						ArrayList<String> locutions2 = new ArrayList<String>();
						locutions2.add(ACCEPT);
						locutions2.add(ATTACK);

						// clean message queue from old messages
						int removed2 = myProcessor.cleanMessagesQueue(subDialogueAgentID, LOCUTION, locutions2);
						logger.info(myID + ": " + removed2 + " messages removed");

						return ASSERT;
					} else { // can not generate support arguments, noCommit
						logger.info("*********************" + myID + ": "
								+ " received WHY, generating suport arg. NO COMMITTT");
						msg2 = noCommit(whyAgentID, currentPosition);
						copyMessages(msg, msg2);

						return NOCOMMIT;
					}
				}

			}

			@Override
			protected boolean doAttack(CProcessor myProcessor, ACLMessage msgToSend, ACLMessage msgReceived,
					boolean defending) {

				Argument againstArgument = (Argument) msgReceived.getContentObject();
				subDialogueAgentID = msgReceived.getSender().getLocalName();

				// store this attack into the corresponding argument
				Argument myLastUsedArg = getMyLastUsedArg(subDialogueAgentID, againstArgument.getAttackingToArgID());
				if (myLastUsedArg != null) {
					// if attack was a counter-example
					if (!againstArgument.getSupportSet().getCounterExamplesDomCases().isEmpty()
							|| !againstArgument.getSupportSet().getCounterExamplesArgCases().isEmpty())
						myLastUsedArg.addReceivedAttacksCounterExample(againstArgument);
					// it is a distinguishing premises attack
					else
						myLastUsedArg.addReceivedAttacksDistPremises(againstArgument);
				}

				ArgNode argNode = null;
				if (msgReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(ASSERT)) {
					// add his position to my asked positions
					Solution sol = new Solution(againstArgument.getHasConclusion(), againstArgument.getPromotesValue(),
							againstArgument.getTimesUsedConclusion());
					Position hisPosition = new Position(subDialogueAgentID, currentDialogueID, sol, null, null, 0f);
					askedPositions.add(hisPosition);
					argNode = new ArgNode(againstArgument.getID(), new ArrayList<Long>(), -1, ArgNode.NodeType.FIRST);
					currentDialogueGraph = new DialogueGraph();
				} else {
					if (defending) {
						ArrayList<Position> myPositionsAsked = attendedWhyPetitions.get(subDialogueAgentID);
						if (myPositionsAsked == null) {
							myPositionsAsked = new ArrayList<Position>();
							myPositionsAsked.add(currentPosition);
						} else {
							if (!myPositionsAsked.contains(currentPosition))
								myPositionsAsked.add(currentPosition);
						}
						attendedWhyPetitions.put(subDialogueAgentID, myPositionsAsked);
					}
					// add the incoming attack argument in dialogue graph and
					// child list of the last node
					argNode = new ArgNode(againstArgument.getID(), new ArrayList<Long>(),
							againstArgument.getAttackingToArgID(), ArgNode.NodeType.NODE);
					ArgNode attackedNode = currentDialogueGraph.getNode(againstArgument.getAttackingToArgID());
					if (attackedNode == null) {
						logger.error(myID + " subDialogueAgentID " + subDialogueAgentID + " GETTING NODE "
								+ againstArgument.getAttackingToArgID());
						for (ArgNode node : currentDialogueGraph.getNodes()) {
							logger.error(myID + " " + node.getNodeType() + " " + node.getArgCaseID() + " PARENT "
									+ node.getParentArgCaseID() + "\n");
						}
						Iterator<String> sup = myUsedSupportArguments.keySet().iterator();
						while (sup.hasNext()) {
							String s = sup.next();
							ArrayList<Argument> mySupports = myUsedSupportArguments.get(s);
							if (mySupports != null)
								for (Argument supp : mySupports) {
									logger.error(myID + " subDialogueAgentID " + s + " Support Argument "
											+ supp.getID() + "\n");
								}
						}
						Iterator<String> att = myUsedAttackArguments.keySet().iterator();
						while (att.hasNext()) {
							String at = att.next();
							ArrayList<Argument> myAttacks = myUsedAttackArguments.get(at);
							if (myAttacks != null)
								for (Argument a : myAttacks) {
									logger.error(myID + " subDialogueAgentID " + at + " Attack Argument " + a.getID()
											+ "\n");
								}
						}

					} else
						attackedNode.addChildArgCaseID(againstArgument.getID());// aquí
																				// petaba!!!
				}
				currentDialogueGraph.addNode(argNode);

				// try to generate an attack argument: Distinguishing premise or
				// Counter Example, depending on the attack received

				logger.info("+++++++++ " + myID + " locution= " + msgReceived.getHeaderValue(LOCUTION));
				logger.info("+++++++++ " + myID + ": doAttack from " + subDialogueAgentID);
				logger.info("+++++++++ " + myID + " receiver: " + msgReceived.getReceiver().getLocalName());

				Argument attackArg = generateAttackArgument(againstArgument, subDialogueAgentID);
				ACLMessage msg2;

				if (attackArg != null) {
					ArrayList<String> locutions = new ArrayList<String>();
					locutions.add(ACCEPT);
					locutions.add(ATTACK);
					locutions.add(NOCOMMIT);
					// clean message queue from old messages
					int removed = myProcessor.cleanMessagesQueue(subDialogueAgentID, LOCUTION, locutions);
					logger.info(myID + ": " + removed + " messages removed");

					msg2 = attack(subDialogueAgentID, attackArg);
					copyMessages(msgToSend, msg2);

					// add argument to myUsedAttackArguments
					ArrayList<Argument> attackArgs = myUsedAttackArguments.get(subDialogueAgentID);
					if (attackArgs == null)
						attackArgs = new ArrayList<Argument>();
					attackArgs.add(attackArg);
					myUsedAttackArguments.put(subDialogueAgentID, attackArgs);

					logger.info("\n" + myID + ": myUsedAttackArgs with " + subDialogueAgentID + " " + attackArgs.size()
							+ " " + myUsedAttackArguments.get(subDialogueAgentID).size() + "\n");

					// add the attack argument to dialogue graph
					ArgNode attNode = currentDialogueGraph.getNode(againstArgument.getID());
					if (attNode == null) {
						logger.error(myID + " GETTING NODE " + againstArgument.getID());

						for (ArgNode node : currentDialogueGraph.getNodes()) {
							logger.error(myID + " " + node.getNodeType() + " " + node.getArgCaseID() + " PARENT "
									+ node.getParentArgCaseID() + "\n");
						}
					}
					attNode.addChildArgCaseID(attackArg.getID());
					ArgNode attackNode = new ArgNode(attackArg.getID(), new ArrayList<Long>(), againstArgument.getID(),
							ArgNode.NodeType.NODE);
					currentDialogueGraph.addNode(attackNode);

					return true;
				} else {

					/*
					 * If the agent cannot generate another attack, it retracts
					 * its attack argument. If it has no more attacks, it has to
					 * retract the support argument, if it has no more support
					 * arguments, it has to noCommit position
					 */

					// search my last attack argument, the one I told this agent
					ArrayList<Argument> attackArgs = myUsedAttackArguments.get(subDialogueAgentID);
					if (attackArgs != null && !attackArgs.isEmpty()) {
						Argument myLastAttackArg = attackArgs.get(attackArgs.size() - 1);
						// put acceptability state to Unacceptable
						myLastAttackArg.setAcceptabilityState(AcceptabilityStatus.UNACCEPTABLE);
						// retract my last attack argument
						ArrayList<Argument> storeList = storeArguments.get(subDialogueAgentID);
						if (storeList == null)
							storeList = new ArrayList<Argument>();
						storeList.add(myLastAttackArg);
						storeArguments.put(subDialogueAgentID, storeList);

						// set the last node of this branch of the dialogue
						// graph
						ArgNode thisNode = currentDialogueGraph.getNode(myLastAttackArg.getID());
						if (thisNode == null) {
							logger.error(myID + " GETTING NODE " + myLastAttackArg.getID());

							for (ArgNode node : currentDialogueGraph.getNodes()) {
								logger.error(myID + " " + node.getNodeType() + " " + node.getArgCaseID() + " PARENT "
										+ node.getParentArgCaseID() + "\n");
							}
						} else
							thisNode.setNodeType(NodeType.LAST);
					}

					return false;

				}

			}

			@Override
			protected void doQueryPositions(CProcessor myProcessor, ACLMessage msg) {

				ACLMessage msg2 = createMessage(commitmentStoreID, GETALLPOSITIONS, currentDialogueID, null);
				copyMessages(msg, msg2);

			}

			@SuppressWarnings("unchecked")
			@Override
			protected void doGetPositions(CProcessor myProcessor, ACLMessage msg) {
				differentPositions = getDifferentPositions((ArrayList<Position>) msg.getContentObject());
			}

			@Override
			protected boolean doWhy(CProcessor myProcessor, ACLMessage msg) {
				ACLMessage msg2;

				if (differentPositions != null && differentPositions.size() > 0) {// some
																					// position
																					// to
																					// ask
					int randPos = (int) Math.random() * differentPositions.size();
					Position pos = differentPositions.get(randPos); // position
																	// chosen
																	// randomly
					// askedPositions.add(pos); we only add the position of the
					// other agent when that agent responds

					ArrayList<String> locutions = new ArrayList<String>();
					locutions.add(ASSERT);
					locutions.add(NOCOMMIT);
					// clean message queue from old messages
					int removed = myProcessor.cleanMessagesQueue(pos.getAgentID(), LOCUTION, locutions);
					logger.info(myID + ": " + removed + " messages removed");

					msg2 = why(pos.getAgentID(), pos);
					copyMessages(msg, msg2);
					logger.info("------------ ------ " + myID + ": WHY to " + pos.getAgentID());
					return true;
				} else {// nothing to challenge, remain in this state
						// send NOTHING message
					msg2 = nothingMsg();
					copyMessages(msg, msg2);
					logger.info(myID + ": NOT WHY nothing to challenge");
					return false;
				}

			}

			@Override
			protected void doFinishDialogue(CProcessor myProcessor, ACLMessage msg) {

			}

			@Override
			protected void doSendPosition(CProcessor myProcessor, ACLMessage msg) {
				ACLMessage msg2;
				if (currentPosition != null) {
					msg2 = createMessage(commitmentStoreID, ADDPOSITION, currentDialogueID, currentPosition);
					copyMessages(msg, msg2);
				}

			}

			@Override
			protected void doSolution(CProcessor myProcessor, ACLMessage msg) {
				Solution solution = (Solution) msg.getContentObject();
				if (solution.getConclusion().getID() != -1)
					updateCBs(solution);
				logger.info(myID + ": " + "SOLUTION received" + " from: " + msg.getSender().getLocalName()
						+ "\n domCases=" + domainCBR.getAllCasesList().size() + "\n argCases="
						+ argCBR.getAllCasesVector().size());

			}

			@Override
			protected void doDie(CProcessor myProcessor) {
				try {
					// Create the end file
					FileWriter fstream = new FileWriter(myID, false);
					BufferedWriter outFile = new BufferedWriter(fstream);
					outFile.write("test finished");
					outFile.newLine();
					// Close the output stream
					outFile.close();
				} catch (Exception e) {// Catch exception if any
					System.err.println("Error: " + e.getMessage());
				}

				myProcessor.getMyAgent().Shutdown();

			}

			@Override
			protected void doMyPositionAccepted(CProcessor myProcessor, ACLMessage messageReceived) {
				// my position is accepted, increase timesAccepted of my
				// Position
				currentPosition.increaseTimesAccepted();
				logger.info(myID + ": " + "increasing vote for my position. SolID="
						+ currentPosition.getSolution().getConclusion().getID() + " currentVotes="
						+ currentPosition.getTimesAccepted() + "\n");

				// change my support argument acceptability state
				// search my support argument, the one I told this agent.
				ArrayList<Argument> supportArgs = myUsedSupportArguments
						.get(messageReceived.getSender().getLocalName());
				Argument myLastSupportArg = supportArgs.get(supportArgs.size() - 1);
				myLastSupportArg.setAcceptabilityState(AcceptabilityStatus.ACCEPTABLE);
				supportArgs.set(supportArgs.size() - 1, myLastSupportArg);
				myUsedSupportArguments.put(messageReceived.getSender().getLocalName(), supportArgs);
				ArrayList<Argument> storeList = storeArguments.get(messageReceived.getSender().getLocalName());
				if (storeList == null)
					storeList = new ArrayList<Argument>();
				storeList.add(myLastSupportArg);
				storeArguments.put(messageReceived.getSender().getLocalName(), storeList);

				// change type of the last node in dialogue graph that
				// corresponds to the last argument that I gave
				ArgNode thisNode = currentDialogueGraph.getNodes().get(currentDialogueGraph.size() - 1);
				if (thisNode == null) {
					logger.error(myID + " GETTING NODE "
							+ currentDialogueGraph.getNodes().get(currentDialogueGraph.size() - 1).getArgCaseID());

					for (ArgNode node : currentDialogueGraph.getNodes()) {
						logger.error(myID + " " + node.getNodeType() + " " + node.getArgCaseID() + " PARENT "
								+ node.getParentArgCaseID() + "\n");
					}
				}
				thisNode.setNodeType(NodeType.AGREE);

				// add finished dialogue to the hashmap
				ArrayList<DialogueGraph> theseGraphs = dialogueGraphs.get(messageReceived.getSender().getLocalName());
				if (theseGraphs == null)
					theseGraphs = new ArrayList<DialogueGraph>();
				theseGraphs.add(currentDialogueGraph);
				dialogueGraphs.put(messageReceived.getSender().getLocalName(), theseGraphs);
			}

			@Override
			protected void doNoCommit(CProcessor myProcessor, ACLMessage msg) {
				ACLMessage msg2 = noCommit(subDialogueAgentID, currentPosition);
				copyMessages(msg, msg2);

			}

			@Override
			protected void doAccept(CProcessor myProcessor, ACLMessage msg) {
				ACLMessage msg2 = accept(subDialogueAgentID);
				copyMessages(msg, msg2);
			}

		}

		// The agent creates the CFactory that creates processors that initiate
		// conversations. In this example the CFactory gets the name "TALK".
		// We limit the number of simultaneous processors to 1.

		long rand = (long) (1200 * Math.random());
		CFactory talk = new myArgumentation().newFactory("TALK", 1, this, 300, rand);

		logger.info(this.getName() + ": My rand wait time is " + rand);

		this.addFactoryAsParticipant(talk);

	}

	protected void finalize(CProcessor firstProcessor, ACLMessage finalizeMessage) {
		logger.info("+++++++++++++++++++++++++++++++++++++++ " + this.getName() + ": Finalizing");
	}

	/**
	 * Copies all contents of the msg2 to the msg
	 * 
	 * @param msg
	 * @param msg2
	 */
	private void copyMessages(ACLMessage msg, ACLMessage msg2) {
		msg.setSender(msg2.getSender());
		Iterator<AgentID> iterReceivers = msg2.getReceiverList().iterator();
		while (iterReceivers.hasNext()) {
			msg.addReceiver(iterReceivers.next());
		}
		msg.setConversationId(msg2.getConversationId());
		msg.setHeader(LOCUTION, msg2.getHeaderValue(LOCUTION));
		msg.setPerformative(msg2.getPerformative());
		if (msg2.getContentObject() != null)
			try {
				msg.setContentObject((Serializable) msg2.getContentObject());
			} catch (IOException e) {

				e.printStackTrace();
			}
	}

	/**
	 * Evaluates if the agent can enter in the dialogue offering a solution. It
	 * it can, it returns an {@link ACLMessage} with the locution ENTERDIALOGUE.
	 * If not, it returns the {@link ACLMessage} with the locution
	 * WITHDRAWDIALOGUE.
	 * 
	 * @param domCase
	 *            {@link DomainCase} that represents the problem to solve
	 * @param dialogueID
	 *            dialogue identifier
	 * @return {@link ACLMessage} with the corresponding locution depending if
	 *         it can enter or not to the dialogue
	 */
	private ACLMessage enter_dialogue(DomainCase domCase, String dialogueID) {

		currentDomCase2Solve = domCase;
		/*
		 * each agent adds domain-cases with the agreed solution ONLY!!! at the
		 * end of the dialogue
		 */

		// a test agent will give the initiator agent the problem to solve
		similarDomainCases = domainCBR.retrieve(domCase.getProblem().getDomainContext().getPremises(), domCBRthreshold);

		currentDialogueID = dialogueID;

		// has cases and solutions to enter in the dialogue
		if (similarDomainCases != null && similarDomainCases.size() > 0) {

			currentProblem = new Problem(new DomainContext(domCase.getProblem().getDomainContext().getPremises()));
			if (currentPosition != null)
				lastPositionBeforeNull = new Position(currentPosition.getAgentID(), currentPosition.getDialogueID(),
						currentPosition.getSolution(), currentPosition.getPremises(), currentPosition.getDomainCases(),
						currentPosition.getDomainCaseSimilarity());
			;
			currentPosition = null;
			positionsGenerated = false;

			askedPositions = new ArrayList<Position>();
			attendedWhyPetitions = new HashMap<String, ArrayList<Position>>();
			dialogueGraphs = new HashMap<String, ArrayList<DialogueGraph>>();
			storeArguments = new HashMap<String, ArrayList<Argument>>();

			// enter dialogue, add in commitment store

			return enterDialogue(dialogueID);

		}
		// Not enter in the dialogue
		else {
			return withdraw_dialogue();
		}
	}

	/**
	 * Returns an {@link ACLMessage} with the locution WITHDRAWDIALOGUE
	 * 
	 * @return an {@link ACLMessage} with the locution WITHDRAWDIALOGUE
	 */
	private ACLMessage withdraw_dialogue() {

		if (currentPosition != null)
			lastPositionBeforeNull = new Position(currentPosition.getAgentID(), currentPosition.getDialogueID(),
					currentPosition.getSolution(), currentPosition.getPremises(), currentPosition.getDomainCases(),
					currentPosition.getDomainCaseSimilarity());
		;
		currentPosition = null;
		currentProblem = null;
		positionsGenerated = false;

		myUsedLocutions++;
		return createMessage(commitmentStoreID, WITHDRAWDIALOGUE, currentDialogueID, null);
	}

	/**
	 * Returns an {@link ACLMessage} with the locution ADDPOSITION and the
	 * position proposed
	 * 
	 * @param pos
	 *            {@link Position} to propose
	 * @param dialogueID
	 *            dialogue identifier
	 * @return an {@link ACLMessage} with the locution ADDPOSITION
	 */
	private ACLMessage propose(Position pos, String dialogueID) {

		return addPosition(pos, dialogueID);

	}

	/**
	 * Returns an {@link ACLMessage} with locution WHY to the given position
	 * 
	 * @param agentIDr
	 *            agent identifier to ask WHY is defending a {@link Position}
	 * @param pos
	 *            {@link Position} of the agent to ask WHY
	 * @return an {@link ACLMessage} with locution WHY to the given position
	 */
	private ACLMessage why(String agentIDr, Position pos) {

		myUsedLocutions++;
		return createMessage(agentIDr, WHY, currentDialogueID, pos);
	}

	/**
	 * Returns an {@link ACLMessage} with locution NOCOMMIT to the given
	 * position
	 * 
	 * @param agentIDr
	 *            agent identifier to tell NOCOMMIT
	 * @param pos
	 *            {@link Position} that the agent does the NOCOMMIT
	 * @return an {@link ACLMessage} with locution NOCOMMIT to the given
	 *         position
	 */
	private ACLMessage noCommit(String agentIDr, Position pos) {

		myUsedLocutions++;
		if (currentPosition != null)
			lastPositionBeforeNull = new Position(currentPosition.getAgentID(), currentPosition.getDialogueID(),
					currentPosition.getSolution(), currentPosition.getPremises(), currentPosition.getDomainCases(),
					currentPosition.getDomainCaseSimilarity());
		;
		currentPosition = null;
		currentPosAccepted = 0;

		// (the no commit is received also by the Commitment Store)
		return createMessage(agentIDr, NOCOMMIT, currentDialogueID, null);

	}

	/**
	 * Returns an {@link ACLMessage} with locution ASSERT and the corresponding
	 * assert argument to respond another agent about a WHY
	 * 
	 * @param agentIDr
	 *            agent identifier to tell that this agent makes an ASSERT to
	 *            respond its WHY
	 * @param arg
	 *            assert argument that use the agent
	 * @return an {@link ACLMessage} with locution ASSERT and the corresponding
	 *         assert argument
	 */
	private ACLMessage asserts(String agentIDr, Argument arg) {
		myUsedLocutions++;
		// send it to the other agent and add the argument to commitment store
		// (the assert is received also by the Commitment Store)
		return createMessage(agentIDr, ASSERT, currentDialogueID, arg);

	}

	/**
	 * Returns an {@link ACLMessage} with locution ACCEPT
	 * 
	 * @param agentIDr
	 *            agent identifier to tell that this agent makes an ACCEPT of
	 *            its position or argument
	 * @return an {@link ACLMessage} with locution ACCEPT
	 */
	private ACLMessage accept(String agentIDr) {

		// send it to the other agent
		myUsedLocutions++;
		return createMessage(agentIDr, ACCEPT, currentDialogueID, null);
	}

	/**
	 * Returns an {@link ACLMessage} with locution ATTACK and an attack argument
	 * 
	 * @param agentIDr
	 *            agent identifier to tell that this agent makes an ATTACK to
	 *            its position or argument
	 * @param arg
	 *            attack argument
	 * @return an {@link ACLMessage} with locution ATTACK and an attack argument
	 */
	private ACLMessage attack(String agentIDr, Argument arg) {
		myUsedLocutions++;

		// send it to the other agent AND add the argument to commitment store
		return createMessage(agentIDr, ATTACK, currentDialogueID, arg);
	}

	/**
	 * Returns an {@link ACLMessage} with locution NOTHING to send it to no one
	 * 
	 * @return an {@link ACLMessage} with locution NOTHING to send it to no one
	 */
	private ACLMessage nothingMsg() {
		ACLMessage msg = new ACLMessage();
		msg.setReceiver(new AgentID("noOne"));
		msg.setHeader(LOCUTION, "NOTHING");
		msg.setSender(getAid());
		msg.setConversationId(currentDialogueID);
		msg.setPerformative(ACLMessage.INFORM);

		return msg;
	}

	/**
	 * Returns the last used argument with another specified agent and with the
	 * given id
	 * 
	 * @param agentID
	 *            agent identifier that received the argument
	 * @param argID
	 *            argument identifier
	 * @return Returns the last used argument
	 */
	private Argument getMyLastUsedArg(String agentID, long argID) {
		try {
			ArrayList<Argument> attackArgs = myUsedAttackArguments.get(agentID);
			if (attackArgs != null) {
				for (int i = attackArgs.size() - 1; i >= 0; i--) {
					if (argID == attackArgs.get(i).getID())
						return attackArgs.get(i);
				}
			}

			ArrayList<Argument> supportArgs = myUsedSupportArguments.get(agentID);
			if (supportArgs != null) {
				for (int i = supportArgs.size() - 1; i >= 0; i--) {
					if (argID == supportArgs.get(i).getID())
						return supportArgs.get(i);
				}
			}
		} catch (Exception e) {
			logger.error(this.getName() + ": Exception in getMyLastUsedArg\n" + e.toString());
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Returns an {@link ArrayList} of {@link Position} with all generated
	 * positions to solve the specified problem, ordered from more to less
	 * suitable to the problem.
	 * 
	 * @param prob
	 *            {@link Problem} to solve.
	 * @return an {@link ArrayList} of {@link Position} with all generated
	 *         positions
	 */
	private ArrayList<Position> generatePositions(Problem prob) {
		// the agent will get the first position always, removing it. when it is
		// out of positions, has to withdraw

		// generate all positions and store them in a list, ordered by Promoted
		// Value, and then Suitability = w*SimDegree + w2*SuitFactor arg

		// so, at first we need make a query to DomainCBR to the possible
		// solutions
		// then, with each solution, create a Position
		// with each position, query the argCBR calculating the SuitFactor Arg
		// (PD + ....)

		// First, assign weights in accordance with the quantity of knowledge
		// int domCases = domainCBR.getAllCasesVector().size();
		// int arguCases = argCBR.getAllCasesVector().size();
		// int totalCases = domCases + arguCases;
		//
		// if (totalCases != 0){
		// wSimilarity = (float) domCases / (float) totalCases;
		// wArgSuitFactor = (float) arguCases / (float) totalCases;
		// }else{
		// wSimilarity = 0.5f;
		// wArgSuitFactor = 0.5f;
		// }

		ArrayList<Position> finalPositions = new ArrayList<Position>();

		if (similarDomainCases == null || similarDomainCases.size() == 0)
			logger.info("\n" + this.getName() + ": " + " NO similar domain cases" + "\n");

		// similarDomainCases has been initialized in enter_dialogue, with the
		// similar domain cases to the problem
		if (similarDomainCases != null && similarDomainCases.size() > 0) {

			// create a list of position list
			// in each list of positions will be stored the positions with same
			// Promoted Values
			ArrayList<ArrayList<Position>> positionsLists = new ArrayList<ArrayList<Position>>();
			for (int i = 0; i < preferedValues.size(); i++) {
				ArrayList<Position> positions = new ArrayList<Position>();
				positionsLists.add(positions);
			}

			for (int sdc = 0; sdc < similarDomainCases.size(); sdc++) {
				SimilarDomainCase simDomCase = similarDomainCases.get(sdc);
				ArrayList<Solution> caseSolutions = simDomCase.getCaseb().getSolutions();
				for (int sc = 0; sc < caseSolutions.size(); sc++) {
					Solution sol = caseSolutions.get(sc);
					int index = getPreferredValueIndex(sol.getPromotesValue());
					// if the Promoted Value is one of the preferred values of
					// the agent, the position is added. Otherwise it is not
					// added.
					if (index != -1) {
						ArrayList<DomainCase> supportDomCases = new ArrayList<DomainCase>();
						supportDomCases.add(simDomCase.getCaseb());

						for (int i = sdc + 1; i < similarDomainCases.size(); i++) {
							SimilarDomainCase dc = similarDomainCases.get(i);
							for (int k = 0; k < dc.getCaseb().getSolutions().size(); k++) {
								Solution s = dc.getCaseb().getSolutions().get(k);
								if (s.getConclusion().getID() == sol.getConclusion().getID()
										&& s.getPromotesValue().equalsIgnoreCase(sol.getPromotesValue())) {
									supportDomCases.add(dc.getCaseb());
									dc.getCaseb().removeSolution(s);
									k--;

									if (dc.getCaseb().getSolutions().isEmpty()) {
										similarDomainCases.remove(dc);
										i--;
									}
									break;
								}
							}

						}

						Position pos = new Position(myID, currentDialogueID, sol, simDomCase.getCaseb().getProblem()
								.getDomainContext().getPremises(), supportDomCases, simDomCase.getSimilarity());
						positionsLists.get(index).add(pos);
					}
				}
			}

			// store all positions in a list, to calculate attack degree,
			// efficiency degree and explanatory power
			ArrayList<Position> allPositions = new ArrayList<Position>();
			Iterator<ArrayList<Position>> iterPositionsLists2 = positionsLists.iterator();
			while (iterPositionsLists2.hasNext()) {
				ArrayList<Position> positions = iterPositionsLists2.next();
				allPositions.addAll(positions);
			}

			// logger.info("\n"+this.getName()+": "+allPositions.size()+" initial positions"+"\n");

			Iterator<ArrayList<Position>> iterPositionsLists = positionsLists.iterator();
			while (iterPositionsLists.hasNext()) {
				ArrayList<Position> positions = iterPositionsLists.next();
				for (int i = 0; i < positions.size(); i++) {
					Position position = positions.get(i);

					SocialContext socCont = new SocialContext(mySocialEntity, null, myGroup, null);
					ArgumentProblem argProblem = new ArgumentProblem(new DomainContext(position.getPremises()), socCont);

					ArrayList<Float> degrees = argCBR.getDegrees(argProblem, position.getSolution(), allPositions,
							allPositions.indexOf(position));
					float persuasivenessDegree = degrees.get(0);
					float supportDegree = degrees.get(1);
					float riskDegree = degrees.get(2);
					float attackDegree = degrees.get(3);
					float efficiencyDegree = degrees.get(4);
					float explanatoryPower = degrees.get(5);

					// SF =( (wPD * PD + wSD * SD + wRD * (1 - RD) + wAD * (1 -
					// AD) + wED * ED + wEP * EP) )/6
					// float argSuitabilityFactor =
					// (wPD * persuasivenessDegree + wSD * supportDegree + wRD *
					// (1 - riskDegree)
					// + wAD * (1 - attackDegree) + wED * efficiencyDegree + wEP
					// * explanatoryPower) /6;
					float argSuitabilityFactor = (wPD * persuasivenessDegree + wSD * supportDegree + wRD
							* (1 - riskDegree) + wAD * (1 - attackDegree) + wED * efficiencyDegree + wEP
							* explanatoryPower);
					position.setArgSuitabilityFactor(argSuitabilityFactor);

					// Assign weights in accordance with the quantity of
					// knowledge
					int domCases = similarDomainCases.size();
					int arguCases;
					try {
						arguCases = argCBR.getSameDomainAndSocialContextAccepted(position.getPremises(),
								position.getSolution(), socCont).size();
						usedArgCases += arguCases;
					} catch (Exception e) {

						arguCases = 0;
					}
					int totalCases = domCases + arguCases;

					if (totalCases != 0) {
						wSimilarity = (float) domCases / (float) totalCases;
						wArgSuitFactor = (float) arguCases / (float) totalCases;
					} else {
						wSimilarity = 0.5f;
						wArgSuitFactor = 0.5f;
					}

					// float
					// finalSuitability=(position.getDomainCaseSimilarity()*wSimilarity
					// + argSuitabilityFactor*wArgSuitFactor)/2;
					float finalSuitability = (position.getDomainCaseSimilarity() * wSimilarity + argSuitabilityFactor
							* wArgSuitFactor);

					position.setFinalSuitability(finalSuitability);

					positions.set(i, position);
				}

				Collections.sort(positions);// sort positions by its
											// finalSuitability= w*SimDegree +
											// w2*SuitFactorArg
				finalPositions.addAll(positions);

			}

		}

		String solString = "";
		for (int i = 0; i < finalPositions.size(); i++) {
			solString += finalPositions.get(i).getSolution().getConclusion().getID() + " ";
		}
		logger.info("\n" + this.getName() + ": " + finalPositions.size() + " initial positions" + " ** Solutions: "
				+ solString + "\n");
		positionsGenerated = true;
		return finalPositions;
	}

	/**
	 * Returns an {@link ArrayList} of support {@link Argument} for the given
	 * {@link Position} against the given agent identifier
	 * 
	 * @param myPos
	 *            {@link Position} to generate support arguments for it
	 * @param agentID
	 *            agent identifier to give support arguments
	 * @return an {@link ArrayList} of support {@link Argument}
	 */
	private ArrayList<Argument> generateSupportArguments(Position myPos, String agentID) {
		// Fist, assign weights in accordance with the quantity of knowledge
		// int domCases = domainCBR.getAllCasesVector().size();
		// int arguCases = argCBR.getAllCasesVector().size();
		// int totalCases = domCases + arguCases;
		//
		// if (totalCases != 0){
		// wSimilarity = (float) domCases / (float) totalCases;
		// wArgSuitFactor = (float) arguCases / (float) totalCases;
		// }else{
		// wSimilarity = 0.5f;
		// wArgSuitFactor = 0.5f;
		// }

		ArrayList<Argument> finalSupportArguments = new ArrayList<Argument>();

		int friendIndex = getFriendIndex(agentID);
		SocialEntity opponent = myFriends.get(friendIndex);
		DependencyRelation relation = depenRelations.get(friendIndex);

		// try to generate a support argument
		// 1) Argument-cases 2) domain-cases 3) premises

		SocialContext socialContext = new SocialContext(mySocialEntity, opponent, myGroup, relation);

		// create arg case with the domain case
		ArgumentProblem argProblem = new ArgumentProblem(new DomainContext(currentPosition.getPremises()),
				socialContext);
		ArgumentSolution argSolution = new ArgumentSolution();
		argSolution.setConclusion(currentPosition.getSolution().getConclusion());
		argSolution.setPromotesValue(currentPosition.getSolution().getPromotesValue());
		argSolution.setTimesUsed(currentPosition.getSolution().getTimesUsed());
		ArgumentJustification argJustification = new ArgumentJustification();
		for (int i = 0; i < currentPosition.getDomainCases().size(); i++) {
			argJustification.addDomainCase(currentPosition.getDomainCases().get(i).getID());
		}

		ArgumentCase argCasefromDomainCase = new ArgumentCase(System.nanoTime(),
				new Date(System.currentTimeMillis()).toString(), argProblem, argSolution, argJustification, 0);

		// create arg case with just the premises
		ArgumentJustification argJustificationPremises = new ArgumentJustification();
		ArgumentCase argCasePremises = new ArgumentCase(System.nanoTime(),
				new Date(System.currentTimeMillis()).toString(), argProblem, argSolution, argJustificationPremises, 0);

		// extract argument cases
		ArrayList<SimilarArgumentCase> argCases = argCBR.getSameDomainAndSocialContextAccepted(myPos.getPremises(),
				myPos.getSolution(), socialContext);
		usedArgCases += argCases.size();

		// add arg case with the domain case and arg case with just the premises

		argCases.add(new SimilarArgumentCase(argCasefromDomainCase, 1));
		argCases.add(new SimilarArgumentCase(argCasePremises, 1));

		Iterator<SimilarArgumentCase> iterArgCases = argCases.iterator();
		// this list contains positions that represent the different arg cases
		// extracted
		// just to calculate the degrees with the same function getDegrees()
		ArrayList<Position> allPositions = new ArrayList<Position>();
		while (iterArgCases.hasNext()) {
			SimilarArgumentCase simArgCase = iterArgCases.next();
			Solution solution = new Solution(simArgCase.getArgumentCase().getArgumentSolution().getConclusion(),
					simArgCase.getArgumentCase().getArgumentSolution().getPromotesValue(), simArgCase.getArgumentCase()
							.getArgumentSolution().getTimesUsed());
			allPositions.add(new Position("", "", solution, simArgCase.getArgumentCase().getArgumentProblem()
					.getDomainContext().getPremises(), null, 1f));
		}

		iterArgCases = argCases.iterator();
		while (iterArgCases.hasNext()) {
			SimilarArgumentCase simArgCase = iterArgCases.next();
			Solution solution = new Solution(simArgCase.getArgumentCase().getArgumentSolution().getConclusion(),
					simArgCase.getArgumentCase().getArgumentSolution().getPromotesValue(), simArgCase.getArgumentCase()
							.getArgumentSolution().getTimesUsed());

			ArrayList<Float> degreesList = argCBR.getDegrees(simArgCase.getArgumentCase().getArgumentProblem(),
					solution, allPositions, argCases.indexOf(simArgCase));
			float persuasivenessDegree = degreesList.get(0);
			float supportDegree = degreesList.get(1);
			float riskDegree = degreesList.get(2);
			float attackDegree = degreesList.get(3);
			float efficiencyDegree = degreesList.get(4);
			float explanatoryPower = degreesList.get(5);

			float argSuitabilityFactor = (wPD * persuasivenessDegree + wSD * supportDegree + wRD * (1 - riskDegree)
					+ wAD * (1 - attackDegree) + wED * efficiencyDegree + wEP * explanatoryPower);
			// simArgCase.setSuitability((argSuitabilityFactor * wArgSuitFactor
			// + simArgCase.getSuitability() * wSimilarity)/2);

			// Assign weights in accordance with the quantity of knowledge
			int domCases = similarDomainCases.size();
			int arguCases;
			try {
				arguCases = argCBR.getSameDomainAndSocialContextAccepted(myPos.getPremises(), myPos.getSolution(),
						socialContext).size();
			} catch (Exception e) {

				arguCases = 0;
			}
			int totalCases = domCases + arguCases;

			if (totalCases != 0) {
				wSimilarity = (float) domCases / (float) totalCases;
				wArgSuitFactor = (float) arguCases / (float) totalCases;
			} else {
				wSimilarity = 0.5f;
				wArgSuitFactor = 0.5f;
			}

			simArgCase.setSuitability((argSuitabilityFactor * wArgSuitFactor + simArgCase.getSuitability()
					* wSimilarity));

			//
			// calculate suitability: with the current similarity in
			// SimilarArgCase, and the suitability obtained of argumentation

			// simArgCase.setSuitability(simArgCase.getSuitability());//TODO
			// this avoids argumentation knowledge to the support arg...
		}

		Collections.sort(argCases);

		ArrayList<Premise> premises = new ArrayList<Premise>();
		Iterator<Premise> iterPremises = myPos.getPremises().values().iterator();
		while (iterPremises.hasNext()) {
			premises.add(iterPremises.next());
		}
		ArrayList<DomainCase> domainCases = new ArrayList<DomainCase>();
		ArrayList<ArgumentCase> argumentCases = new ArrayList<ArgumentCase>();
		ArrayList<ArgumentationScheme> schemes = new ArrayList<ArgumentationScheme>();
		ArrayList<Premise> distPremises = new ArrayList<Premise>();
		ArrayList<Premise> presumptions = new ArrayList<Premise>();
		ArrayList<Premise> exceptions = new ArrayList<Premise>();
		ArrayList<DomainCase> counterExamplesdomainCases = new ArrayList<DomainCase>();
		ArrayList<ArgumentCase> counterExamplesargumentCases = new ArrayList<ArgumentCase>();

		// create support argument with premises or domain-cases, not directly
		// with argument-cases

		for (int i = 0; i < argCases.size(); i++) {
			ArgumentCase bestArgCase = argCases.get(i).getArgumentCase();
			if (bestArgCase != null) {
				argumentCases = new ArrayList<ArgumentCase>();
				domainCases = new ArrayList<DomainCase>();

				ArgumentJustification argJustification2 = bestArgCase.getArgumentJustification();
				ArrayList<Long> domCasesJustification = argJustification2.getDomainCasesIDs();
				ArrayList<Long> argCasesJustification = argJustification2.getArgumentCasesIDs();
				ArrayList<ArgumentationScheme> argSchemes = argJustification2.getArgumentationSchemes();
				ArrayList<DialogueGraph> dialogueGraphs = argJustification2.getDialogueGraphs();

				// detect if it is the domain Case justification argument
				if (domCasesJustification != null && domCasesJustification.size() > 0) {
					// add the domain case that justifies the position, because
					// it is this argument: argCasefromDomainCase
					domainCases = currentPosition.getDomainCases();
				}
				// detect if it is the only premises justification argument
				else if ((argSchemes == null || argSchemes.size() == 0)
						&& (dialogueGraphs == null || dialogueGraphs.size() == 0)
						&& (domCasesJustification == null || domCasesJustification.size() == 0)
						&& (argCasesJustification == null || argCasesJustification.size() == 0)) {
					// premises already in the premises list
					// do not add as an argument case
				} else {
					argumentCases.add(bestArgCase);
				}
			}

			SupportSet supportSet = new SupportSet(premises, domainCases, argumentCases, schemes, distPremises,
					presumptions, exceptions, counterExamplesdomainCases, counterExamplesargumentCases);
			Argument argument = new Argument(System.nanoTime(), myPos.getSolution().getConclusion(), myPos
					.getSolution().getTimesUsed(), myPos.getSolution().getPromotesValue(), supportSet, relation);

			finalSupportArguments.add(argument);

		}

		return finalSupportArguments;

	}

	/**
	 * Returns an attack {@link Argument} against the given argument of the
	 * given agent identifier
	 * 
	 * @param incArgument
	 *            previous {@link Argument} of the agent to give an attack
	 *            {@link Argument}
	 * @param agentID
	 *            agent identifier of the agent to attack
	 * @return an attack {@link Argument} against the given argument, or
	 *         <code>null</code> if it is not possible
	 */
	private Argument generateAttackArgument(Argument incArgument, String agentID) {
		// Fist, assign weights in accordance with the quantity of knowledge
		// int domCases = domainCBR.getAllCasesVector().size();
		// int arguCases = argCBR.getAllCasesVector().size();
		// int totalCases = domCases + arguCases;
		//
		// if (totalCases != 0){
		// wSimilarity = (float) domCases / (float) totalCases;
		// wArgSuitFactor = (float) arguCases / (float) totalCases;
		// }else{
		// wSimilarity = 0.5f;
		// wArgSuitFactor = 0.5f;
		// }

		// try to generate an attack argument: Distinguishing premise or Counter
		// Example, depending on the attack received

		// againstPos.getSolution().getConclusion().getID();
		//
		// SocialContext socialContext= new SocialContext(mySocialEntity,
		// opponent, myGroup, relation);
		// //create arg case with the domain case
		// ArgumentProblem argProblem=new ArgumentProblem(new
		// DomainContext(currentPosition.getPremises()), socialContext);
		// ArgumentSolution argSolution=new ArgumentSolution();
		// argSolution.setConclusion(currentPosition.getSolution().getConclusion());
		// argSolution.setPromotesValue(currentPosition.getSolution().getPromotesValue());
		// argSolution.setTimesUsed(currentPosition.getSolution().getTimesUsed());
		// ArgumentJustification argJustification=new ArgumentJustification();
		// argJustification.addCase(currentPosition.getDomainCase());
		//
		// ArgumentCase argCasefromDomainCase=
		// new ArgumentCase(System.nanoTime(), new
		// Date(System.currentTimeMillis()), argProblem, argSolution,
		// argJustification, 0);
		//
		//
		try {

			int friendInd = getFriendIndex(agentID);
			SocialEntity opponent = myFriends.get(friendInd);
			DependencyRelation relation = depenRelations.get(friendInd);

			// if the opponent is more powerful than me, do not attack
			if (incArgument.getProponentDepenRelation().compareTo(relation) < 0)
				return null;

			SocialContext socialContext = new SocialContext(mySocialEntity, opponent, myGroup, relation);

			// extract argument-cases that match my position
			HashMap<Integer, Premise> myPosPremises = currentPosition.getPremises();
			Solution sol = currentPosition.getSolution();
			ArrayList<SimilarArgumentCase> argCases = argCBR.getSameDomainAndSocialContextAccepted(myPosPremises, sol,
					socialContext);
			usedArgCases += argCases.size();

			// create argument-cases with the domain-cases
			for (int i = 0; i < currentPosition.getDomainCases().size(); i++) {
				ArgumentProblem argProblem = new ArgumentProblem(new DomainContext(currentPosition.getDomainCases()
						.get(i).getProblem().getDomainContext().getPremises()), socialContext);
				ArgumentSolution argSolution = new ArgumentSolution();
				argSolution.setConclusion(currentPosition.getSolution().getConclusion());
				argSolution.setPromotesValue(currentPosition.getSolution().getPromotesValue());
				argSolution.setTimesUsed(currentPosition.getSolution().getTimesUsed());
				ArgumentJustification argJustification = new ArgumentJustification();

				argJustification.addDomainCase(currentPosition.getDomainCases().get(i).getID());

				ArgumentCase argCasefromDomainCase = new ArgumentCase(System.nanoTime(), new Date(
						System.currentTimeMillis()).toString(), argProblem, argSolution, argJustification, 0);

				// add argument-case with the domain-cases to the list of
				// potential attacks

				argCases.add(new SimilarArgumentCase(argCasefromDomainCase, domainCBR.getPremisesSimilarity(
						myPosPremises, currentPosition.getDomainCases().get(i).getProblem().getDomainContext()
								.getPremises())));
			}

			Iterator<SimilarArgumentCase> iterArgCases = argCases.iterator();
			// this list contains positions that represent the different arg
			// cases extracted
			// just to calculate the degrees with the same function getDegrees()
			ArrayList<Position> allPositions = new ArrayList<Position>();
			while (iterArgCases.hasNext()) {
				SimilarArgumentCase simArgCase = iterArgCases.next();
				Solution solution = new Solution(simArgCase.getArgumentCase().getArgumentSolution().getConclusion(),
						simArgCase.getArgumentCase().getArgumentSolution().getPromotesValue(), simArgCase
								.getArgumentCase().getArgumentSolution().getTimesUsed());
				allPositions.add(new Position("", "", solution, simArgCase.getArgumentCase().getArgumentProblem()
						.getDomainContext().getPremises(), null, 1f));
			}

			iterArgCases = argCases.iterator();
			while (iterArgCases.hasNext()) {
				SimilarArgumentCase simArgCase = iterArgCases.next();
				Solution solution = new Solution(simArgCase.getArgumentCase().getArgumentSolution().getConclusion(),
						simArgCase.getArgumentCase().getArgumentSolution().getPromotesValue(), simArgCase
								.getArgumentCase().getArgumentSolution().getTimesUsed());

				ArrayList<Float> degreesList = argCBR.getDegrees(simArgCase.getArgumentCase().getArgumentProblem(),
						solution, allPositions, argCases.indexOf(simArgCase));
				float persuasivenessDegree = degreesList.get(0);
				float supportDegree = degreesList.get(1);
				float riskDegree = degreesList.get(2);
				float attackDegree = degreesList.get(3);
				float efficiencyDegree = degreesList.get(4);
				float explanatoryPower = degreesList.get(5);

				float argSuitabilityFactor = (wPD * persuasivenessDegree + wSD * supportDegree + wRD * (1 - riskDegree)
						+ wAD * (1 - attackDegree) + wED * efficiencyDegree + wEP * explanatoryPower);
				// simArgCase.setSuitability((argSuitabilityFactor *
				// wArgSuitFactor + simArgCase.getSuitability() *
				// wSimilarity)/2);

				// Assign weights in accordance with the quantity of knowledge
				int domCases = similarDomainCases.size();
				int arguCases;
				try {
					arguCases = argCBR.getSameDomainAndSocialContextAccepted(myPosPremises, sol, socialContext).size();
				} catch (Exception e) {

					arguCases = 0;
				}
				int totalCases = domCases + arguCases;

				if (totalCases != 0) {
					wSimilarity = (float) domCases / (float) totalCases;
					wArgSuitFactor = (float) arguCases / (float) totalCases;
				} else {
					wSimilarity = 0.5f;
					wArgSuitFactor = 0.5f;
				}
				simArgCase.setSuitability((argSuitabilityFactor * wArgSuitFactor + simArgCase.getSuitability()
						* wSimilarity));

				// calculate suitability: with the current similarity in
				// SimilarArgCase, and the suitability obtained of argumentation

			}

			Collections.sort(argCases);

			SupportSet incSS = incArgument.getSupportSet();
			Argument attack = null;
			boolean support = false;
			if (incSS.getDistinguishingPremises().isEmpty() && incSS.getPresumptions().isEmpty()
					&& incSS.getExceptions().isEmpty() && incSS.getCounterExamplesDomCases().isEmpty()
					&& incSS.getCounterExamplesArgCases().isEmpty()) {
				support = true;
			}

			if (support) {
				// incoming argument is a support argument
				// TODO attack argumentation scheme
				if (!incSS.getDomainCases().isEmpty()) {
					attack = generateCEAttack(argCases, incSS.getDomainCases().get(0).getProblem().getDomainContext()
							.getPremises(), relation, agentID);
					if (attack == null) {
						attack = generateDPAttack(argCases, incSS.getDomainCases().get(0).getProblem()
								.getDomainContext().getPremises(), relation, agentID);
					}
				} else if (!incSS.getArgumentCases().isEmpty()) {
					attack = generateCEAttack(argCases, incSS.getArgumentCases().get(0).getArgumentProblem()
							.getDomainContext().getPremises(), relation, agentID);
					if (attack == null) {
						attack = generateDPAttack(argCases, incSS.getArgumentCases().get(0).getArgumentProblem()
								.getDomainContext().getPremises(), relation, agentID);
					}
				} else {
					HashMap<Integer, Premise> premHash = new HashMap<Integer, Premise>();
					for (Premise p : incSS.getPremises()) {
						premHash.put(p.getID(), p);
					}
					attack = generateDPAttack(argCases, premHash, relation, agentID);
					if (attack == null) {
						attack = generateCEAttack(argCases, premHash, relation, agentID);
					}
				}
			} else {
				// incoming argument is an attack argument
				// TODO attack presumptions and exceptions
				if (!incSS.getCounterExamplesDomCases().isEmpty()) {
					attack = generateCEAttack(argCases, incSS.getCounterExamplesDomCases().get(0).getProblem()
							.getDomainContext().getPremises(), relation, agentID);
					if (attack == null) {
						attack = generateDPAttack(argCases, incSS.getCounterExamplesDomCases().get(0).getProblem()
								.getDomainContext().getPremises(), relation, agentID);
					}
				} else if (!incSS.getCounterExamplesArgCases().isEmpty()) {
					attack = generateCEAttack(argCases, incSS.getCounterExamplesArgCases().get(0).getArgumentProblem()
							.getDomainContext().getPremises(), relation, agentID);
					if (attack == null) {
						attack = generateDPAttack(argCases, incSS.getCounterExamplesArgCases().get(0)
								.getArgumentProblem().getDomainContext().getPremises(), relation, agentID);
					}
				} else {
					HashMap<Integer, Premise> distPremHash = new HashMap<Integer, Premise>();
					for (Premise p : incSS.getDistinguishingPremises()) {
						distPremHash.put(p.getID(), p);
					}
					attack = generateDPAttack(argCases, distPremHash, relation, agentID);
					if (attack == null) {
						attack = generateCEAttack(argCases, distPremHash, relation, agentID);
					}
				}
			}

			if (attack != null)
				attack.setAttackingToArgID(incArgument.getID());

			return attack;

		} catch (Exception e) {
			logger.error(this.getName() + ": Exception in generateAttackArgument\n" + e.toString());
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Return a distinguishing premises attack {@link Argument} against the
	 * agent of the given agent identifier, and its given premises
	 * 
	 * @param argCases
	 *            {@link ArrayList} of {@link SimilarArgumentCase} to the
	 *            current {@link Position} to defend to generate distinguishing
	 *            premises
	 * @param itsPremises
	 *            {@link HashMap} of the premises of the other agent to attack
	 * @param relation
	 *            {@link DependencyRelation} with the other agent to attack
	 * @param agentID
	 *            agent identifier of the agent to attack
	 * @return a distinguishing premises attack {@link Argument}, or
	 *         <code>null</code> if it is not possible
	 */
	private Argument generateDPAttack(ArrayList<SimilarArgumentCase> argCases, HashMap<Integer, Premise> itsPremises,
			DependencyRelation relation, String agentID) {
		HashMap<Integer, Premise> hisUsefulPremises = getUsefulPremises(
				currentProblem.getDomainContext().getPremises(), itsPremises);
		Iterator<SimilarArgumentCase> iterArgCases = argCases.iterator();
		while (iterArgCases.hasNext()) {
			SimilarArgumentCase simArgCase = iterArgCases.next();
			HashMap<Integer, Premise> myPremises = simArgCase.getArgumentCase().getArgumentProblem().getDomainContext()
					.getPremises();
			HashMap<Integer, Premise> myUsefulPremises = getUsefulPremises(currentProblem.getDomainContext()
					.getPremises(), myPremises);
			ArrayList<Premise> distPremises = getDistinguishingPremises(myUsefulPremises, hisUsefulPremises);
			ArrayList<Premise> itsdistPremises = getDistinguishingPremises(hisUsefulPremises, myUsefulPremises);

			if (distPremises.size() > 0 && distPremises.size() >= itsdistPremises.size()) {// generate
																							// attack
				ArrayList<Premise> premises = new ArrayList<Premise>();
				Iterator<Premise> iterPremises = currentPosition.getPremises().values().iterator();
				while (iterPremises.hasNext()) {
					premises.add(iterPremises.next());
				}
				ArrayList<DomainCase> domainCases = new ArrayList<DomainCase>();
				ArrayList<ArgumentCase> argumentCases = new ArrayList<ArgumentCase>();
				ArrayList<ArgumentationScheme> schemes = new ArrayList<ArgumentationScheme>();
				// ArrayList<Premise> distPremises=new ArrayList<Premise>();
				ArrayList<Premise> presumptions = new ArrayList<Premise>();
				ArrayList<Premise> exceptions = new ArrayList<Premise>();
				ArrayList<DomainCase> counterExamplesdomainCases = new ArrayList<DomainCase>();
				ArrayList<ArgumentCase> counterExamplesargumentCases = new ArrayList<ArgumentCase>();
				SupportSet supportSet = new SupportSet(premises, domainCases, argumentCases, schemes, distPremises,
						presumptions, exceptions, counterExamplesdomainCases, counterExamplesargumentCases);
				Argument argument = new Argument(System.nanoTime(), currentPosition.getSolution().getConclusion(),
						currentPosition.getSolution().getTimesUsed(), currentPosition.getSolution().getPromotesValue(),
						supportSet, relation);
				if (!argumentPreviouslyUsed(argument, myUsedAttackArguments.get(agentID))) {

					Iterator<Premise> iterPrem = distPremises.iterator();
					String str = "";
					while (iterPrem.hasNext()) {
						Premise p = iterPrem.next();
						str += p.getID() + "=" + p.getContent() + " ";
					}
					Iterator<Premise> iterPremises2 = itsdistPremises.iterator();
					String str2 = "";
					while (iterPremises2.hasNext()) {
						Premise p = iterPremises2.next();
						str2 += p.getID() + "=" + p.getContent() + " ";
					}
					logger.info(this.getName() + ": " + " distinguishing premises attack argument against: " + agentID
							+ "\n" + "mydistPremises (" + distPremises.size() + "): " + str + "\n itsdistPremises ("
							+ itsdistPremises.size() + "): " + str2 + "\n");
					return argument;
				}

			}

		}
		return null;
	}

	/**
	 * Return a counter-example attack {@link Argument} against the agent of the
	 * given agent identifier, and its given premises
	 * 
	 * @param argCases
	 *            {@link ArrayList} of {@link SimilarArgumentCase} to the
	 *            current {@link Position} to defend to generate
	 *            counter-examples
	 * @param itsCasePremises
	 *            {@link HashMap} of the premises of the other agent to attack
	 * @param relation
	 *            {@link DependencyRelation} with the other agent to attack
	 * @param agentID
	 *            agent identifier of the agent to attack
	 * @return a counter-example attack {@link Argument}, or <code>null</code>
	 *         if it is not possible
	 */
	private Argument generateCEAttack(ArrayList<SimilarArgumentCase> argCases,
			HashMap<Integer, Premise> itsCasePremises, DependencyRelation relation, String agentID) {

		HashMap<Integer, Premise> itsUsefulPremises = getUsefulPremises(
				currentProblem.getDomainContext().getPremises(), itsCasePremises);
		Iterator<SimilarArgumentCase> iterArgCases = argCases.iterator();
		while (iterArgCases.hasNext()) {
			SimilarArgumentCase simArgCase = iterArgCases.next();
			HashMap<Integer, Premise> myPremises = simArgCase.getArgumentCase().getArgumentProblem().getDomainContext()
					.getPremises();
			HashMap<Integer, Premise> myUsefulPremises = getUsefulPremises(currentProblem.getDomainContext()
					.getPremises(), myPremises);

			boolean find = false;
			Iterator<Premise> hisPrem = itsUsefulPremises.values().iterator();
			while (hisPrem.hasNext() && !find) {
				Premise hp = hisPrem.next();
				if (myUsefulPremises.get(hp.getID()) == null)
					find = true;
			}

			if (find == false) {// generate attack
				ArrayList<Premise> premises = new ArrayList<Premise>();
				Iterator<Premise> iterPremises = currentPosition.getPremises().values().iterator();
				while (iterPremises.hasNext()) {
					premises.add(iterPremises.next());
				}
				ArrayList<DomainCase> domainCases = new ArrayList<DomainCase>();
				ArrayList<ArgumentCase> argumentCases = new ArrayList<ArgumentCase>();
				ArrayList<ArgumentationScheme> schemes = new ArrayList<ArgumentationScheme>();
				ArrayList<Premise> distPremises = new ArrayList<Premise>();
				ArrayList<Premise> presumptions = new ArrayList<Premise>();
				ArrayList<Premise> exceptions = new ArrayList<Premise>();
				ArrayList<DomainCase> counterExamplesdomainCases = new ArrayList<DomainCase>();
				ArrayList<ArgumentCase> counterExamplesargumentCases = new ArrayList<ArgumentCase>();
				counterExamplesargumentCases.add(simArgCase.getArgumentCase());
				SupportSet supportSet = new SupportSet(premises, domainCases, argumentCases, schemes, distPremises,
						presumptions, exceptions, counterExamplesdomainCases, counterExamplesargumentCases);
				Argument argument = new Argument(System.nanoTime(), currentPosition.getSolution().getConclusion(),
						currentPosition.getSolution().getTimesUsed(), currentPosition.getSolution().getPromotesValue(),
						supportSet, relation);
				if (!argumentPreviouslyUsed(argument, myUsedAttackArguments.get(agentID))) {
					logger.info(this.getName() + ": " + " counter-example attack argument against: " + agentID + "\n");
					return argument;
				}

			}

		}
		return null;
	}

	/**
	 * Returns a {@link HashMap} of the useful premises of the agent of the
	 * current problem to solve (the premises of the {@link Position} that are
	 * in the problem).
	 * 
	 * @param problemPremises
	 *            {@link HashMap} with the premises of the problem to solve
	 * @param myPremises
	 *            {@link HashMap} with the premises of the {@link Position} that
	 *            defends the agent
	 * @return a {@link HashMap} of the useful premises of the agent of the
	 *         current problem to solve
	 */
	private HashMap<Integer, Premise> getUsefulPremises(HashMap<Integer, Premise> problemPremises,
			HashMap<Integer, Premise> myPremises) {

		Iterator<Premise> iterMyPremises = myPremises.values().iterator();
		HashMap<Integer, Premise> usefulPremises = new HashMap<Integer, Premise>();
		while (iterMyPremises.hasNext()) {
			Premise premise = iterMyPremises.next();
			Premise problemPremise = problemPremises.get(premise.getID());
			if (problemPremise != null && problemPremise.getContent().equalsIgnoreCase(premise.getContent())) {// this
																												// premise
																												// is
																												// in
																												// the
																												// problem
				usefulPremises.put(premise.getID(), premise);
			}
		}
		return usefulPremises;
	}

	/**
	 * Returns an {@link ArrayList} with distinguishing premises between the
	 * HashMaps given as arguments
	 * 
	 * @param myPremises
	 *            {@link HashMap} of the premises candidates to be
	 *            distinguishing premises
	 * @param itsPremises
	 *            {@link HashMap} of the premises to generate distinguishing
	 *            premises against
	 * @return an {@link ArrayList} with distinguishing premises
	 */
	private ArrayList<Premise> getDistinguishingPremises(HashMap<Integer, Premise> myPremises,
			HashMap<Integer, Premise> itsPremises) {
		ArrayList<Premise> distPremises = new ArrayList<Premise>();
		Iterator<Premise> iterUsefulPremises = myPremises.values().iterator();
		while (iterUsefulPremises.hasNext()) {
			Premise premise = iterUsefulPremises.next();
			Premise premise2 = itsPremises.get(premise.getID());
			if (premise2 != null && premise2.getContent().equalsIgnoreCase(premise.getContent())) {

			} else
				distPremises.add(premise);
		}
		return distPremises;
	}

	/**
	 * Returns true if the given {@link Argument} is on the given
	 * {@link ArrayList} of arguments, otherwise returns false
	 * 
	 * @param arg
	 *            {@link Argument} to be test if its knowledge resources have
	 *            been used previously
	 * @param myArguments
	 *            {@link ArrayList} of {@link Argument} used previously
	 * @return true if the given {@link Argument} is on the given
	 *         {@link ArrayList} of arguments, otherwise returns false
	 */
	private boolean argumentPreviouslyUsed(Argument arg, ArrayList<Argument> myArguments) {

		if (myArguments == null)
			return false;

		for (int i = myArguments.size() - 1; i >= 0; i--) {
			Argument currentArg = myArguments.get(i);
			if (arg.getHasConclusion().getID() == currentArg.getHasConclusion().getID()
					&& arg.getPromotesValue().equalsIgnoreCase(currentArg.getPromotesValue())) {

				SupportSet argSupp = arg.getSupportSet();
				SupportSet currentArgSupp = currentArg.getSupportSet();

				if (argSupp.getArgumentCases().size() > 0
						&& argSupp.getArgumentCases().size() == currentArgSupp.getArgumentCases().size()) {
					ArrayList<ArgumentCase> cases = argSupp.getArgumentCases();
					ArrayList<ArgumentCase> cases2 = currentArgSupp.getArgumentCases();
					if (cases != null && cases2 != null && cases.size() > 0 && cases2.size() > 0) {
						// logger.info(this.getName()+": "+" argument cases\n");
						if (cases.get(0).equals(cases2.get(0))) {
							logger.info(this.getName() + ": " + " SAME arg cases\n");
							return true;
						}
					}
				}
				if (argSupp.getDomainCases().size() > 0
						&& argSupp.getDomainCases().size() == currentArgSupp.getDomainCases().size()) {
					ArrayList<DomainCase> cases = argSupp.getDomainCases();
					ArrayList<DomainCase> cases2 = currentArgSupp.getDomainCases();
					if (cases != null && cases2 != null && cases.size() > 0 && cases2.size() > 0) {
						// logger.info(this.getName()+": "+" domain cases\n");
						if (cases.get(0).equals(cases2.get(0))) {
							logger.info(this.getName() + ": " + " SAME domain cases\n");
							return true;
						}
					}
				}
				if (argSupp.getCounterExamplesDomCases().size() > 0
						&& argSupp.getCounterExamplesDomCases().size() == currentArgSupp.getCounterExamplesDomCases()
								.size()) {
					ArrayList<DomainCase> cases = argSupp.getCounterExamplesDomCases();
					ArrayList<DomainCase> cases2 = currentArgSupp.getCounterExamplesDomCases();
					if (cases != null && cases2 != null && cases.size() > 0 && cases2.size() > 0) {
						// logger.info(this.getName()+": "+" counter example cases\n");
						if (cases.get(0).equals(cases2.get(0))) {
							logger.info(this.getName() + ": " + " SAME counter example domain cases\n");
							return true;
						}
					}
				}
				if (argSupp.getCounterExamplesArgCases().size() > 0
						&& argSupp.getCounterExamplesArgCases().size() == currentArgSupp.getCounterExamplesArgCases()
								.size()) {
					ArrayList<ArgumentCase> cases = argSupp.getCounterExamplesArgCases();
					ArrayList<ArgumentCase> cases2 = currentArgSupp.getCounterExamplesArgCases();
					if (cases != null && cases2 != null && cases.size() > 0 && cases2.size() > 0) {
						// logger.info(this.getName()+": "+" counter example cases:\n"+
						// cases.get(0).toString()+"\n"+cases2.get(0).toString());

						ArgumentCase case1 = cases.get(0);
						ArgumentCase case2 = cases2.get(0);

						if (case1.getID() == case2.getID()) {
							logger.info(this.getName() + ": " + " SAME counter example argument cases ID");
							return true;
						} else if (case1.equals(case2)) {
							logger.info(this.getName() + ": " + " SAME counter example argument cases");
							return true;
						} else if (areSamePremises(case1.getArgumentProblem().getDomainContext().getPremises(), case2
								.getArgumentProblem().getDomainContext().getPremises())
								&& case1.getArgumentSolution().getConclusion()
										.equals(case2.getArgumentSolution().getConclusion())
								&& case1.getArgumentSolution().getPromotesValue()
										.equals(case2.getArgumentSolution().getPromotesValue())
								&& case1.getArgumentSolution().getTimesUsed() == case2.getArgumentSolution()
										.getTimesUsed()) {
							logger.info(this.getName() + ": "
									+ " SAME counter example argument cases premises and conclusions");
							return true;
						}
					}
				}
				if (argSupp.getDistinguishingPremises().size() > 0) {
					ArrayList<Premise> premises = argSupp.getDistinguishingPremises();
					ArrayList<Premise> premises2 = currentArgSupp.getDistinguishingPremises();

					Iterator<Premise> iterPremises = premises.iterator();
					String str = "";
					while (iterPremises.hasNext()) {
						Premise p = iterPremises.next();
						str += p.getID() + "=" + p.getContent() + " ";
					}
					Iterator<Premise> iterPremises2 = premises2.iterator();
					String str2 = "";
					while (iterPremises2.hasNext()) {
						Premise p = iterPremises2.next();
						str2 += p.getID() + "=" + p.getContent() + " ";
					}
					logger.info(this.getName() + ": dist prems\n" + str + "\n" + str2);
					if (premises != null && premises2 != null && premises.size() > 0 && premises2.size() > 0
							&& premises.size() == premises2.size()) {
						logger.info(this.getName() + ": " + " distinguishing premises\n");

						if (areSamePremises(premises, premises2)) {
							logger.info(this.getName() + ": " + " SAME distinguishing premises\n");
							return true;
						}
					}
				}

			}
		}
		logger.info(this.getName() + ": " + " argument not previously used");
		return false;
	}

	/**
	 * Returns <code>true</code> if the premises of the given HashMaps are the
	 * same
	 * 
	 * @param prem1
	 *            {@link HashMap} of {@link Premise}
	 * @param prem2
	 *            {@link HashMap} of {@link Premise}
	 * @return <code>true</code> if the premises of the given HashMaps are the
	 *         same
	 */
	private boolean areSamePremises(HashMap<Integer, Premise> prem1, HashMap<Integer, Premise> prem2) {

		if (prem1.values().size() != prem2.values().size())
			return false;
		Iterator<Premise> iterPrem = prem1.values().iterator();
		while (iterPrem.hasNext()) {
			Premise p = iterPrem.next();
			Premise p2 = prem2.get(p.getID());
			if (p2 == null || !p2.getContent().equalsIgnoreCase(p.getContent()))
				return false;
		}

		return true;
	}

	/**
	 * Returns <code>true</code> if the premises of the given ArrayLists are the
	 * same
	 * 
	 * @param prem1
	 *            {@link ArrayList} of {@link Premise}
	 * @param prem2
	 *            {@link ArrayList} of {@link Premise}
	 * @return <code>true</code> if the premises of the given ArrayLists are the
	 *         same
	 */
	private boolean areSamePremises(ArrayList<Premise> prem1, ArrayList<Premise> prem2) {

		if (prem1.size() != prem2.size())
			return false;
		Iterator<Premise> iterPrem = prem1.iterator();
		while (iterPrem.hasNext()) {
			Premise p = iterPrem.next();
			Iterator<Premise> iterPrem2 = prem2.iterator();
			while (iterPrem2.hasNext()) {
				Premise p2 = iterPrem2.next();
				if (p.getID() == p2.getID()) {
					if (!p.getContent().equals(p2.getContent()))
						return false;
					break;
				}
			}
		}

		return true;
	}

	/**
	 * Returns the index of the given preference value
	 * 
	 * @param value
	 *            String representing a preference value
	 * @return the index of the given preference value
	 */
	private int getPreferredValueIndex(String value) {
		for (int i = 0; i < preferedValues.size(); i++) {
			if (preferedValues.get(i).equalsIgnoreCase(value))
				return i;
		}
		return -1;
	}

	/**
	 * Returns the index of the given agent identifier
	 * 
	 * @param agentID
	 *            agent identifier
	 * @return the index of the given agent identifier
	 */
	private int getFriendIndex(String agentID) {
		Iterator<SocialEntity> iterFriends = myFriends.iterator();
		while (iterFriends.hasNext()) {
			SocialEntity friend = iterFriends.next();
			if (friend.getName().equalsIgnoreCase(agentID)) {
				return myFriends.indexOf(friend);
			}
		}
		System.err.println(myID + ": getFriendIndex not found " + agentID);
		return -1;
	}

	/**
	 * Returns an {@link ACLMessage} with the locution ADDPOSITION and a
	 * {@link Position} to send to Commitment Store
	 * 
	 * @param pos
	 *            {@link Position} to add in the Commitment Store
	 * @param dialogueID
	 *            current dialogue identifier
	 * @return an {@link ACLMessage} with the locution ADDPOSITION
	 */
	private ACLMessage addPosition(Position pos, String dialogueID) {
		myUsedLocutions++;
		return createMessage(commitmentStoreID, ADDPOSITION, dialogueID, pos);
	}

	/**
	 * Returns an {@link ArrayList} of positions that are different from the
	 * defended position and also are not asked yet.
	 * 
	 * @param positions
	 *            {@link ArrayList} with all the positions in the dialogue
	 * @return an {@link ArrayList} of positions that are different from the
	 *         defended position
	 */
	private ArrayList<Position> getDifferentPositions(ArrayList<Position> positions) {
		ArrayList<Position> differentPositions = new ArrayList<Position>();
		if (positions == null || positions.size() == 0)
			return new ArrayList<Position>();
		// if it has not position, all positions are considered different
		if (currentPosition == null)
			return positions;
		try {

			Iterator<Position> iterPositions = positions.iterator();

			while (iterPositions.hasNext()) {
				Position pos = iterPositions.next();
				if (currentPosition.getSolution().getConclusion().getID() != pos.getSolution().getConclusion().getID()) {
					Iterator<Position> iterAskedPositions = askedPositions.iterator();
					boolean asked = false;
					while (iterAskedPositions.hasNext()) {
						Position askedPos = iterAskedPositions.next();
						if (askedPos != null) {
							String askedPosAgentID = askedPos.getAgentID();
							String posAgentID = pos.getAgentID();
							if (askedPosAgentID.equalsIgnoreCase(posAgentID)
									&& askedPos.getSolution().getConclusion().getID() == pos.getSolution()
											.getConclusion().getID()
									&& askedPos.getSolution().getPromotesValue()
											.equalsIgnoreCase(pos.getSolution().getPromotesValue())) {
								logger.info(this.getName() + ": position already asked");
								asked = true;
								break;
							}
						}

					}
					if (!asked)
						differentPositions.add(pos);

				}
			}
		} catch (Exception e) {
			logger.error(this.getName() + ": Exception in getDifferentPositions\n" + e.toString());
			e.printStackTrace();
		}

		return differentPositions;
	}

	/**
	 * Returns an {@link ACLMessage} with the locution ENTERDIALOGUE to send to
	 * Commitment Store
	 * 
	 * @param dialogueID
	 *            id of the dialogue to join
	 * @return an {@link ACLMessage} with the locution ENTERDIALOGUE
	 */
	private ACLMessage enterDialogue(String dialogueID) {
		myUsedLocutions++;
		return createMessage(commitmentStoreID, ENTERDIALOGUE, dialogueID, null);
	}

	/**
	 * Returns the list of IDs of the given domain cases
	 * 
	 * @param domainCases
	 * @return the list of IDs of the given domain cases
	 */
	private ArrayList<Long> domCasestoLongIDs(ArrayList<DomainCase> domainCases) {
		ArrayList<Long> longListIDs = new ArrayList<Long>();
		Iterator<DomainCase> iterDomCases = domainCases.iterator();
		while (iterDomCases.hasNext()) {
			longListIDs.add(iterDomCases.next().getID());
		}

		return longListIDs;
	}

	/**
	 * Returns the list of IDs of the given argument cases
	 * 
	 * @param argCases
	 * @return the list of IDs of the given argument cases
	 */
	private ArrayList<Long> argCasestoLongIDs(ArrayList<ArgumentCase> argCases) {
		ArrayList<Long> longListIDs = new ArrayList<Long>();
		Iterator<ArgumentCase> iterArgCases = argCases.iterator();
		while (iterArgCases.hasNext()) {
			longListIDs.add(iterArgCases.next().getID());
		}
		return longListIDs;
	}

	/**
	 * Adds the final solution to the current ticket and adds it in the domain
	 * case-base. Also, stores all the generated argumentation data in the
	 * argumentation case-base. Finally, makes a cache of the domain CBR and the
	 * argumentation CBR.
	 * 
	 * @param solution
	 */
	private void updateCBs(Solution solution) {
		// add the solution to the ticket and add the ticket to domainCBR

		ArrayList<Solution> solutions = new ArrayList<Solution>();
		solutions.add(solution);
		currentDomCase2Solve.setSolutions(solutions);
		synchronized (domainCBR) {
			boolean caseAdded = domainCBR.addCase(currentDomCase2Solve);
			if (caseAdded) {
				logger.info(this.getName() + ": " + "Domain-case Introduced");
			} else {
				logger.info(this.getName() + ": " + "Domain-case Updated");
			}
		}
		// add argument cases generated during the dialogue

		DomainContext domainContext = new DomainContext(currentDomCase2Solve.getProblem().getDomainContext()
				.getPremises());

		for (int i = 0; i < myFriends.size(); i++) {
			// logger.info(this.getName()+": "+"friend="+i);
			SocialEntity friend = myFriends.get(i);
			DependencyRelation relation = depenRelations.get(i);
			SocialContext socialContext = new SocialContext(mySocialEntity, friend, myGroup, relation);

			ArrayList<DialogueGraph> dialogues = dialogueGraphs.get(friend.getName());

			// support arguments
			ArrayList<Argument> listArgs = storeArguments.get(friend.getName());
			if (listArgs != null) {// if there are used support arguments with
									// this friend
				// logger.info(this.getName()+": "+"friend="+i+" -> "+listArgs.size()+" support args to add.");
				Iterator<Argument> iterArgs = listArgs.iterator();
				while (iterArgs.hasNext()) {
					Argument arg = iterArgs.next();
					ArgumentProblem argProb = new ArgumentProblem(domainContext, socialContext);
					ArrayList<Premise> distP = new ArrayList<Premise>();
					for (Argument a : arg.getReceivedAttacksDistPremises()) {
						distP.addAll(a.getSupportSet().getDistinguishingPremises());
					}
					ArrayList<Long> countExDom = new ArrayList<Long>();
					ArrayList<Long> countExArg = new ArrayList<Long>();
					for (Argument a : arg.getReceivedAttacksCounterExamples()) {
						countExDom.addAll(domCasestoLongIDs(a.getSupportSet().getCounterExamplesDomCases()));
						countExArg.addAll(argCasestoLongIDs(a.getSupportSet().getCounterExamplesArgCases()));
					}
					// TODO put presumptions and exceptions
					ArgumentSolution argSol = new ArgumentSolution(ArgumentType.INDUCTIVE, arg.getAcceptabilityState(),
							distP, new ArrayList<Premise>(), new ArrayList<Premise>(), countExDom, countExArg);
					argSol.setPromotesValue(arg.getPromotesValue());
					argSol.setTimesUsed(arg.getTimesUsedConclusion());
					argSol.setConclusion(arg.getHasConclusion());

					ArrayList<Long> domainCasesIDs = new ArrayList<Long>();
					Iterator<DomainCase> iterDomCases = arg.getSupportSet().getDomainCases().iterator();
					while (iterDomCases.hasNext()) {
						domainCasesIDs.add(iterDomCases.next().getID());
					}
					ArrayList<Long> argCasesIDs = new ArrayList<Long>();
					Iterator<ArgumentCase> iterArgCases = arg.getSupportSet().getArgumentCases().iterator();
					while (iterArgCases.hasNext()) {
						argCasesIDs.add(iterArgCases.next().getID());
					}

					// take the dialogues where this argument is implied
					ArrayList<DialogueGraph> diags = new ArrayList<DialogueGraph>();
					if (dialogues != null) {
						Iterator<DialogueGraph> iterDialogues = dialogues.iterator();
						while (iterDialogues.hasNext()) {
							DialogueGraph diag = iterDialogues.next();
							if (diag.contains(arg.getID()))
								diags.add(diag);
						}
					}

					ArgumentJustification argJust = new ArgumentJustification(domainCasesIDs, argCasesIDs, arg
							.getSupportSet().getArgumentationSchemes(), diags);

					ArgumentCase newArgCase = new ArgumentCase(arg.getID(), new Date(arg.getID()).toString(), argProb,
							argSol, argJust, 1);
					boolean argCaseAdded = argCBR.addCase(newArgCase);
					if (argCaseAdded) {
						logger.info(this.getName() + ": " + "friend=" + i + " -> " + "Argument-case Introduced");
					} else {
						logger.info(this.getName() + ": " + "friend=" + i + " -> " + "Argument-case Updated");
					}
				}
			}

		}

	}

	/**
	 * Creates and returns an {@link ACLMessage} with the given arguments.
	 * 
	 * @param agentID
	 *            {@link String} with the agent ID to send the message
	 * @param locution
	 *            {@link String} with the locution of the message to send
	 * @param dialogueID
	 *            {@link String} with dialogueID of the message
	 * @param contentObject
	 *            {@link Serializable} with an object to attach to the message
	 * @return an {@link ACLMessage} with the given arguments
	 */
	private ACLMessage createMessage(String agentID, String locution, String dialogueID, Serializable contentObject) {

		ACLMessage msg = new ACLMessage();
		msg.setSender(getAid());
		msg.setReceiver(new AgentID(agentID));
		if (locution.equalsIgnoreCase(NOCOMMIT) || locution.equalsIgnoreCase(ASSERT)
				|| locution.equalsIgnoreCase(ATTACK)) {
			msg.addReceiver(new AgentID(commitmentStoreID));
		}
		msg.setConversationId(dialogueID);
		msg.setPerformative(ACLMessage.INFORM);

		if (locution.contains("=")) {
			StringTokenizer tokenizer = new StringTokenizer(locution, "=");
			String perf = tokenizer.nextToken();
			String contentAgentID = tokenizer.nextToken();
			locution = perf;
			msg.setHeader("agentID", contentAgentID);
		}
		msg.setHeader(LOCUTION, locution);

		if (contentObject != null) {
			try {
				msg.setContentObject(contentObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ArrayList<AgentID> receivers = msg.getReceiverList();
		String receiversStr = "";
		Iterator<AgentID> iterReceivers = receivers.iterator();
		while (iterReceivers.hasNext()) {
			receiversStr += iterReceivers.next().name + " ";
		}

		logger.info(this.getName() + ": " + "message to send to: " + receiversStr + " dialogueID: "
				+ msg.getConversationId() + " locution: " + msg.getHeaderValue(LOCUTION));

		return msg;
	}

	/**
	 * 
	 * @return used locutions in the current dialogue
	 */
	public int getMyUsedLocutions() {
		return myUsedLocutions;
	}

	/**
	 * 
	 * @return number of domain-cases in the domain CBR
	 */
	public int getNumberDomainCases() {
		return domainCBR.getAllCasesList().size();
	}

	/**
	 * 
	 * @return number of argument-cases in the argumentation CBR
	 */
	public int getNumberArgumentCases() {
		return argCBR.getAllCasesVector().size();
	}

	/**
	 * 
	 * @return dialogue time
	 */
	public float getDialogueTime() { // TODO not working...
		return dialogueTime;
	}

	/**
	 * 
	 * @return last {@link Position} before being <code>null</code>
	 */
	public Position getLastPositionBeforeNull() {
		return lastPositionBeforeNull;
	}

	/**
	 * 
	 * @return current {@link Position} defended by the agent in the current
	 *         dialogue
	 */
	public Position getCurrentPosition() {
		return currentPosition;
	}

	/**
	 * 
	 * @return current dialogue identifier
	 */
	public String getCurrentDialogueID() {
		return currentDialogueID;
	}

	/**
	 * 
	 * @return number of votes to the current position
	 */
	public int getAccepted() {
		return currentPosAccepted;
	}

	/**
	 * 
	 * @return number of agreements of the current position
	 */
	public int getAgreement() {
		return agreementReached;
	}

	/**
	 * 
	 * @return acceptance frequency
	 */
	public int getAcceptanceFrequency() {
		return acceptanceFrequency;
	}

	/**
	 * 
	 * @return <code>true</code> if the agent is alive, otherwise it returns
	 *         <code>false</code>
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * 
	 * @return votes received to the defended position
	 */
	public int getVotes() {
		return votesReceived;
	}

	/**
	 * 
	 * @return number of used argument-cases
	 */
	public int getUsedArgCases() {
		return usedArgCases;
	}

}
