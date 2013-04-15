/**
 * This class has been generated using Gormas2Magentix tool.
 * 
 * @author Mario Rodrigo - mrodrigo@dsic.upv.es
 * 
 */
package EMFGormas_Example;

import java.util.ArrayList;

import EMFGormas_Example.Constants.AccessibilityType;
import EMFGormas_Example.Constants.PositionType;
import EMFGormas_Example.Constants.UnitType;
import EMFGormas_Example.Constants.VisibilityType;
import EMFGormas_Example.Utils.LocalData;
import EMFGormas_Example.Utils.UnitRolePair;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.exception.InvalidVisibilityException;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;

public class GodAgent extends CAgent {

    // ----------------------------------------
    // FIELDS of the class
    // ----------------------------------------
    private OMSProxy          omsProxy;
    private SFProxy           sfProxy;
    private ArrayList<String> results;
    Monitor                   mon = null;
    private LocalData         myLocalData;

    // ----------------------------------------
    // CONSTRUCTOR of the class
    // ----------------------------------------
    /**
     * Constructor of the class
     * 
     * @param aid
     * @throws Exception
     */
    public GodAgent(AgentID aid) throws Exception {
        super(aid);

        if (results == null) {
            results = new ArrayList<String>();
        }

        if (omsProxy == null) {
            omsProxy = new OMSProxy(this);
        }

        if (sfProxy == null) {
            sfProxy = new SFProxy(this);
        }

        if (myLocalData == null) {
            myLocalData = new LocalData();
        }

    } // End of constructor

    // ----------------------------------------
    // METHODS of the class
    // ----------------------------------------
    // This is the main method of the agent
    @Override
    protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {
        String result;
        ArrayList<String> resultArrayList = null;
        ArrayList<ArrayList<String>> serviceResultArrayList = null;

        // Requesting for the list of roles and units in which the agent is in a
        // specific moment.
        CAgent cAgent = firstProcessor.getMyAgent();

        try {
            logger.info("[" + getName() + "] Initializing scenario.");

            // Get the list of roles that agent is playing
            ArrayList<UnitRolePair> agentPlayingRoles = Utils.queryOMSForRolesPlayedByAnAgent(cAgent, omsProxy, logger);
            for (UnitRolePair unitRolePair : agentPlayingRoles) {
                myLocalData.addPlayingRole(unitRolePair);
            }

            UnitRolePair desiredRole = new UnitRolePair("participant", "virtual");

            if (!myLocalData.getPlayingRoles().contains(desiredRole)) {
                // Acquire Role in main Organization
                result = omsProxy.acquireRole(desiredRole.getRoleID(), desiredRole.getUnitID());

                if (result.contains("acquired")) {
                    // Add the pair Unit - Role to playingRole ArrayList
                    myLocalData.addPlayingRole(desiredRole);
                }

                logger.debug("[" + getName() + "] Entering in THOMAS: " + result + "\n");

            } else {
                logger.debug("[" + getName() + "] is already inside THOMAS.");
            }

        } catch (THOMASException e) {
            // If an error occurs trying to acquire role "participant" in
            // "virtual" organization, the agent will not be able to do any
            // action in future. So the only option is exit from the app with
            // the abnormally exit code (1).
            logger.error("[" + getName() + "] Application ended abnormally. Please review your code, your configuration and your database.\n" + e.getContent());

            // Ends the application abnormally
            System.exit(1);
        }

        try {
            // Not exists, so RegisterUnit "Calculator"
            result = omsProxy.registerUnit("Calculator", UnitType.TEAM.toString(), null, "creator");
            if (result.equalsIgnoreCase("Calculator created")) {
                // Add the Virtual Organization to createdOrganizations
                // ArrayList
                myLocalData.addCreatedUnit("Calculator");

                // Add the pair Unit - Role to playingRole ArrayList
                myLocalData.addPlayingRole(new UnitRolePair("creator", "Calculator"));
            }

            logger.debug("[" + getName() + "] Register Unit \"Calculator\" result: " + result + "\n");

        } catch (THOMASException e) {
            logger.error("[" + getName() + "] Application ended abnormally. Please review your code, your configuration and your database.\n" + e.getContent());
            // Ends the application abnormally
            System.exit(1);
        }

        try {
            // Query for roles registered inside "Calculator"
            serviceResultArrayList = omsProxy.informUnitRoles("Calculator");
            ArrayList<String> containedRoles = new ArrayList<String>();

            for (ArrayList<String> tuple : serviceResultArrayList) {
                // Role's name
                containedRoles.add(tuple.get(0));
                logger.debug("[" + getName() + "] Added the role \"" + tuple.get(0) + "\" to the contained roles array for the Unit \"Calculator\".\n");
            }

            // Register role "operation"
            if (!containedRoles.contains("operation")) {
                result = omsProxy.registerRole("operation", "Calculator", AccessibilityType.EXTERNAL.toString(), "visionlab"/*VisibilityType.PUBLIC.toString()*/, PositionType.MEMBER.toString());

                if (result.equalsIgnoreCase("operation created")) {
                    // Add the pair Unit - Role to createdRole ArrayList
                    myLocalData.addCreatedRole(new UnitRolePair("operation", "Calculator"));
                    logger.debug("[" + getName() + "] Register Role \"operation\" result: " + result + "\n");
                }
            }

        } catch (InvalidVisibilityException iVE) {

            logger.error("[" + getName() + "] Application ended abnormally. Please review your code, your configuration and your database.\n" + iVE.getContent());
            // Ends the application abnormally
            System.exit(1);
        
        } catch (THOMASException e) {
            logger.error("[" + getName() + "] Application ended abnormally. Please review your code, your configuration and your database.\n" + e.getContent());
            // Ends the application abnormally
            System.exit(1);
        }

        try {
            // Not exists, so RegisterUnit "School"
            result = omsProxy.registerUnit("School", UnitType.FLAT.toString(), null, "creator");
            if (result.equalsIgnoreCase("School created")) {
                // Add the Virtual Organization to createdOrganizations
                // ArrayList
                myLocalData.addCreatedUnit("School");

                // Add the pair Unit - Role to playingRole ArrayList
                myLocalData.addPlayingRole(new UnitRolePair("creator", "School"));
            }

            logger.debug("[" + getName() + "] Register Unit \"School\" result: " + result + "\n");

        } catch (THOMASException e) {
            logger.error("[" + getName() + "] Application ended abnormally. Please review your code, your configuration and your database.\n" + e.getContent());
            // Ends the application abnormally
            System.exit(1);
        }

        try {
            // Query for roles registered inside "School"
            serviceResultArrayList = omsProxy.informUnitRoles("School");
            ArrayList<String> containedRoles = new ArrayList<String>();

            for (ArrayList<String> tuple : serviceResultArrayList) {
                // Role's name
                containedRoles.add(tuple.get(0));
                logger.debug("[" + getName() + "] Added the role \"" + tuple.get(0) + "\" to the contained roles array for the Unit \"School\".\n");
            }

            // Register role "student"
            if (!containedRoles.contains("student")) {
                result = omsProxy.registerRole("student", "School", AccessibilityType.EXTERNAL.toString(), VisibilityType.PUBLIC.toString(), PositionType.MEMBER.toString());

                if (result.equalsIgnoreCase("student created")) {
                    // Add the pair Unit - Role to createdRole ArrayList
                    myLocalData.addCreatedRole(new UnitRolePair("student", "School"));
                    logger.debug("[" + getName() + "] Register Role \"student\" result: " + result + "\n");
                }
            }

        } catch (THOMASException e) {
            logger.error("[" + getName() + "] Application ended abnormally. Please review your code, your configuration and your database.\n" + e.getContent());
            // Ends the application abnormally
            System.exit(1);
        }

        logger.info("[" + getName() + "] Scenario initialized.");

        // Time to instantiate the agents

        try {
            // Instantiating ProductAgent agent
            ProductAgent ProductAgentAgent = new ProductAgent(new AgentID("ProductAgent"));

            // Execute the agent
            ProductAgentAgent.start();
            myLocalData.addStartedAgent(ProductAgentAgent);
            logger.debug("[" + getName() + "] ProductAgentAgent started successfully.");

        } catch (Exception e) {
            logger.error("[" + getName() + "] Application ended abnormally during the creation of \"ProductAgentAgent\".\n" + e.getMessage());
            // Ends the application abnormally
            System.exit(1);
        }

        try {
            // Instantiating AdditionAgent agent
            AdditionAgent AdditionAgentAgent = new AdditionAgent(new AgentID("AdditionAgent"));

            // Execute the agent
            AdditionAgentAgent.start();
            myLocalData.addStartedAgent(AdditionAgentAgent);
            logger.debug("[" + getName() + "] AdditionAgentAgent started successfully.");

        } catch (Exception e) {
            logger.error("[" + getName() + "] Application ended abnormally during the creation of \"AdditionAgentAgent\".\n" + e.getMessage());
            // Ends the application abnormally
            System.exit(1);
        }

        try {
            // Instantiating JamesAgent agent
            JamesAgent JamesAgentAgent = new JamesAgent(new AgentID("JamesAgent"));

            // Execute the agent
            JamesAgentAgent.start();
            myLocalData.addStartedAgent(JamesAgentAgent);
            logger.debug("[" + getName() + "] JamesAgentAgent started successfully.");

        } catch (Exception e) {
            logger.error("[" + getName() + "] Application ended abnormally during the creation of \"JamesAgentAgent\".\n" + e.getMessage());
            // Ends the application abnormally
            System.exit(1);
        }

        // We create a factory in order to send a REQUEST and wait for the
        // answer
        class ShutdownAgentsFIPA_REQUEST {

            int timesFirstWait  = 1;
            int timesSecondWait = 1;

            /**
             * Method to execute at the beginning of the conversation
             * 
             * @param myProcessor
             *            the CProcessor managing the conversation
             * @param msg
             *            first message to send
             */
            protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
                myProcessor.getInternalData().put("InitialMessage", msg);
            }

            class BEGIN_Method implements BeginStateMethod {
                public String run(CProcessor myProcessor, ACLMessage msg) {
                    doBegin(myProcessor, msg);
                    if (((GodAgent) myProcessor.getMyAgent()).myLocalData.getStartedAgents().size() > 0) {
                        return "REQUEST_REQUEST_INITIATOR";
                    } else {
                        return "FINAL_REQUEST_INITIATOR";
                    }
                };
            }

            /**
             * Sets the request message
             * 
             * @param myProcessor
             *            the CProcessor managing the conversation
             * @param messageToSend
             *            request message
             */
            protected void doRequest(CProcessor myProcessor, ACLMessage messageToSend) {
                for (CAgent agentToStop : ((GodAgent) myProcessor.getMyAgent()).myLocalData.getStartedAgents()) {
                    messageToSend.addReceiver(agentToStop.getAid());
                }
                messageToSend.setSender(myProcessor.getMyAgent().getAid());
                messageToSend.setContent("Time to shutdown.");
                messageToSend.setHeader("shutdown", "ShutdownAgent");
                messageToSend.setPerformative(ACLMessage.REQUEST);
                for (int index = 0; index < messageToSend.getTotalReceivers(); index++) {
                    logger.debug("[" + myProcessor.getMyAgent().getName() + "] I tell " + messageToSend.getReceiver(index).name + " " + messageToSend.getPerformative() + " " + messageToSend.getContent());
                }
            }

            class REQUEST_Method implements SendStateMethod {
                public String run(CProcessor myProcessor, ACLMessage messageToSend) {
                    doRequest(myProcessor, messageToSend);
                    return "FIRST_WAIT_REQUEST_INITIATOR";
                }
            }

            /**
             * Method to execute when the initiator receives a not-understood
             * message
             * 
             * @param myProcessor
             *            the CProcessor managing the conversation
             * @param msg
             *            not-understood message
             */
            protected void doNotUnderstood(CProcessor myProcessor, ACLMessage msg) {
                logger.debug("[" + myProcessor.getMyAgent().getName() + "] " + msg.getSender().name + " tell me: " + msg.getPerformative() + " " + msg.getContent());
            }

            class NOT_UNDERSTOOD_Method implements ReceiveStateMethod {
                public String run(CProcessor myProcessor, ACLMessage messageReceived) {
                    doNotUnderstood(myProcessor, messageReceived);
                    if (timesFirstWait < ((GodAgent) myProcessor.getMyAgent()).myLocalData.getStartedAgents().size()) {
                        timesFirstWait++;
                        return "FIRST_WAIT_REQUEST_INITIATOR";
                    } else {
                        return "FINAL_REQUEST_INITIATOR";
                    }
                }
            }

            /**
             * Method to execute when the initiator receives a failure message
             * 
             * @param myProcessor
             *            the CProcessor managing the conversation
             * @param msg
             *            failure message
             */
            protected void doRefuse(CProcessor myProcessor, ACLMessage msg) {
                logger.debug("[" + myProcessor.getMyAgent().getName() + "] " + msg.getSender().name + " tell me: " + msg.getPerformative() + " " + msg.getContent());
            }

            class REFUSE_Method implements ReceiveStateMethod {
                public String run(CProcessor myProcessor, ACLMessage messageReceived) {
                    doRefuse(myProcessor, messageReceived);
                    if (timesFirstWait < ((GodAgent) myProcessor.getMyAgent()).myLocalData.getStartedAgents().size()) {
                        timesFirstWait++;
                        return "FIRST_WAIT_REQUEST_INITIATOR";
                    } else {
                        return "FINAL_REQUEST_INITIATOR";
                    }
                }
            }

            /**
             * Method to execute when the initiator receives a agree message
             * 
             * @param myProcessor
             *            the CProcessor managing the conversation
             * @param msg
             *            agree message
             */
            protected void doAgree(CProcessor myProcessor, ACLMessage msg) {
                logger.debug("[" + myProcessor.getMyAgent().getName() + "] " + msg.getSender().name + " tell me: " + msg.getPerformative() + " " + msg.getContent());
            }

            class AGREE_Method implements ReceiveStateMethod {
                public String run(CProcessor myProcessor, ACLMessage messageReceived) {
                    doAgree(myProcessor, messageReceived);
                    // There are so many answers as Agents started
                    if (timesFirstWait < ((GodAgent) myProcessor.getMyAgent()).myLocalData.getStartedAgents().size()) {
                        timesFirstWait++;
                        return "FIRST_WAIT_REQUEST_INITIATOR";
                    } else {
                        return "SECOND_WAIT_REQUEST_INITIATOR";
                    }
                }
            }

            /**
             * Method to execute when the initiator receives a failure message
             * 
             * @param myProcessor
             *            the CProcessor managing the conversation
             * @param msg
             *            failure message
             */
            protected void doFailure(CProcessor myProcessor, ACLMessage msg) {
                logger.debug("[" + myProcessor.getMyAgent().getName() + "] " + msg.getSender().name + " tell me: " + msg.getPerformative() + " " + msg.getContent());
            }

            class FAILURE_Method implements ReceiveStateMethod {
                public String run(CProcessor myProcessor, ACLMessage messageReceived) {
                    doFailure(myProcessor, messageReceived);
                    if (timesSecondWait < ((GodAgent) myProcessor.getMyAgent()).myLocalData.getStartedAgents().size()) {
                        timesSecondWait++;
                        return "SECOND_WAIT_REQUEST_INITIATOR";
                    } else {
                        return "FINAL_REQUEST_INITIATOR";
                    }
                }
            }

            /**
             * Method to execute when the initiator receives a inform message
             * 
             * @param myProcessor
             *            the CProcessor managing the conversation
             * @param msg
             *            inform message
             */
            protected void doInform(CProcessor myProcessor, ACLMessage msg) {
                logger.debug("[" + myProcessor.getMyAgent().getName() + "] " + msg.getSender().name + " tell me: " + msg.getPerformative() + " " + msg.getContent());
            }

            class INFORM_Method implements ReceiveStateMethod {
                public String run(CProcessor myProcessor, ACLMessage messageReceived) {
                    doInform(myProcessor, messageReceived);
                    // There are so many answers as Agents started
                    if (timesSecondWait < ((GodAgent) myProcessor.getMyAgent()).myLocalData.getStartedAgents().size()) {
                        timesSecondWait++;
                        return "SECOND_WAIT_REQUEST_INITIATOR";
                    } else {
                        return "FINAL_REQUEST_INITIATOR";
                    }
                }
            }

            /**
             * Method to execute when the initiator ends the conversation
             * 
             * @param myProcessor
             *            the CProcessor managing the conversation
             * @param messageToSend
             *            final message
             */
            protected void doFinal(CProcessor myProcessor, ACLMessage messageToSend) {
                messageToSend = myProcessor.getLastSentMessage();

                CAgent cAgent = myProcessor.getMyAgent();
                // Time to deregister the roles .
                deregisterRoles(cAgent);

                // After deregistering them, it is time to
                // deregister units.
                deregisterUnits(cAgent);

                // It remains only to leave THOMAS
                leaveTHOMAS(cAgent);

                // The last step is call to his ShutdownAgent method.
                // NO USER CODE BEYOND THIS POINT!!
                myProcessor.ShutdownAgent();
            }

            class FINAL_Method implements FinalStateMethod {
                public void run(CProcessor myProcessor, ACLMessage messageToSend) {
                    doFinal(myProcessor, messageToSend);
                }
            }

            /**
             * Creates a new initiator fipa request cfactory
             * 
             * @param name
             *            factory's name
             * @param filter
             *            message filter
             * @param requestMessage
             *            first message to send
             * @param availableConversations
             *            maximum number of conversation this CFactory can
             *            manage simultaneously
             * @param myAgent
             *            agent owner of this CFactory
             * @param timeout
             *            for waiting after sending the request message
             * @return a new fipa request initiator factory
             */
            public CFactory newFactory(String name, MessageFilter filter, ACLMessage requestMessage, int availableConversations, CAgent myAgent, long timeout) {

                // Create factory
                if (filter == null) {
                    filter = new MessageFilter("performative = REQUEST");
                }
                CFactory theFactory = new CFactory(name, filter, availableConversations, myAgent);

                // Processor template setup
                CProcessor processor = theFactory.cProcessorTemplate();

                // BEGIN State

                BeginState BEGIN = (BeginState) processor.getState("BEGIN");
                BEGIN.setMethod(new BEGIN_Method());

                // REQUEST State

                SendState REQUEST = new SendState("REQUEST_REQUEST_INITIATOR");

                REQUEST.setMethod(new REQUEST_Method());
                REQUEST.setMessageTemplate(requestMessage);
                processor.registerState(REQUEST);
                processor.addTransition("BEGIN", "REQUEST_REQUEST_INITIATOR");
                processor.addTransition("BEGIN", "FINAL_REQUEST_INITIATOR");

                // FIRST_WAIT State

                processor.registerState(new WaitState("FIRST_WAIT_REQUEST_INITIATOR", timeout));
                processor.addTransition("REQUEST_REQUEST_INITIATOR", "FIRST_WAIT_REQUEST_INITIATOR");

                // NOT_UNDERSTOOD State

                ReceiveState NOT_UNDERSTOOD = new ReceiveState("NOT_UNDERSTOOD_REQUEST_INITIATOR");
                NOT_UNDERSTOOD.setMethod(new NOT_UNDERSTOOD_Method());
                filter = new MessageFilter("performative = " + ACLMessage.getPerformative(ACLMessage.NOT_UNDERSTOOD));
                NOT_UNDERSTOOD.setAcceptFilter(filter);
                processor.registerState(NOT_UNDERSTOOD);
                processor.addTransition("FIRST_WAIT_REQUEST_INITIATOR", "NOT_UNDERSTOOD_REQUEST_INITIATOR");

                // REFUSE State

                ReceiveState REFUSE = new ReceiveState("REFUSE_REQUEST_INITIATOR");
                REFUSE.setMethod(new REFUSE_Method());
                filter = new MessageFilter("performative = REFUSE");
                REFUSE.setAcceptFilter(filter);
                processor.registerState(REFUSE);
                processor.addTransition("FIRST_WAIT_REQUEST_INITIATOR", "REFUSE_REQUEST_INITIATOR");

                // AGREE State

                ReceiveState AGREE = new ReceiveState("AGREE_REQUEST_INITIATOR");
                AGREE.setMethod(new AGREE_Method());
                filter = new MessageFilter("performative = AGREE");
                AGREE.setAcceptFilter(filter);
                processor.registerState(AGREE);
                processor.addTransition("FIRST_WAIT_REQUEST_INITIATOR", "AGREE_REQUEST_INITIATOR");
                processor.addTransition("AGREE_REQUEST_INITIATOR", "FIRST_WAIT_REQUEST_INITIATOR");

                // SECOND_WAIT State

                processor.registerState(new WaitState("SECOND_WAIT_REQUEST_INITIATOR", timeout));
                processor.addTransition("AGREE_REQUEST_INITIATOR", "SECOND_WAIT_REQUEST_INITIATOR");

                // FAILURE State

                ReceiveState FAILURE = new ReceiveState("FAILURE_REQUEST_INITIATOR");
                FAILURE.setMethod(new FAILURE_Method());
                filter = new MessageFilter("performative = FAILURE");
                FAILURE.setAcceptFilter(filter);
                processor.registerState(FAILURE);
                processor.addTransition("SECOND_WAIT_REQUEST_INITIATOR", "FAILURE_REQUEST_INITIATOR");
                processor.addTransition("FAILURE_REQUEST_INITIATOR", "SECOND_WAIT_REQUEST_INITIATOR");

                // INFORM State

                ReceiveState INFORM = new ReceiveState("INFORM_REQUEST_INITIATOR");
                INFORM.setMethod(new INFORM_Method());
                filter = new MessageFilter("performative = INFORM");
                INFORM.setAcceptFilter(filter);
                processor.registerState(INFORM);
                processor.addTransition("SECOND_WAIT_REQUEST_INITIATOR", "INFORM_REQUEST_INITIATOR");
                processor.addTransition("INFORM_REQUEST_INITIATOR", "SECOND_WAIT_REQUEST_INITIATOR");

                // FINAL State

                FinalState FINAL = new FinalState("FINAL_REQUEST_INITIATOR");

                FINAL.setMethod(new FINAL_Method());

                processor.registerState(FINAL);
                processor.addTransition("INFORM_REQUEST_INITIATOR", "FINAL_REQUEST_INITIATOR");
                processor.addTransition("FAILURE_REQUEST_INITIATOR", "FINAL_REQUEST_INITIATOR");
                processor.addTransition("NOT_UNDERSTOOD_REQUEST_INITIATOR", "FINAL_REQUEST_INITIATOR");
                processor.addTransition("REFUSE_REQUEST_INITIATOR", "FINAL_REQUEST_INITIATOR");
                return theFactory;
            }

        } // End of class ShutdownAgentsFIPA_REQUEST

        MessageFilter shutdownFilter = new MessageFilter("performative = " + ACLMessage.getPerformative(ACLMessage.REQUEST) + " AND shutdown = ShutdownAgent");

        // Call Shutdown actions for agents contained at startedAgents
        // ArrayList
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        for (CAgent agentToStop : myLocalData.getStartedAgents()) {
            msg.addReceiver(agentToStop.getAid());
        }
        msg.setContent("Time to shutdown.");
        msg.setHeader("shutdown", "ShutdownAgent");
        CFactory shutdownTalk = new ShutdownAgentsFIPA_REQUEST().newFactory("ShutdownTalk", shutdownFilter, msg, 1, firstProcessor.getMyAgent(), 0);

        // /////////////////////////////////////////////////////////////////////////////
        // The template processor is ready. We activate the factory.

        this.addFactoryAsInitiator(shutdownTalk);

        try {
            Thread.sleep(120 * 1000);
        } catch (InterruptedException iE) {
            logger.error("[" + cAgent.getName() + "] Application ended abnormally. Please review your code, your configuration and your database.\n" + iE.getMessage());
            // Ends the application abnormally
            System.exit(1);

        }
        // Finally starts the conversation.
        this.startSyncConversation(shutdownTalk.getName());

    } // End execution()

    // This method is executed just before the agent ends its execution
    @Override
    protected void finalize(CProcessor firstProcessor, ACLMessage finalizeMessage) {
        CAgent cAgent = firstProcessor.getMyAgent();
        logger.info("[" + cAgent.getName() + "] finalize method executed.");
    } // End finalize()

    /**
     * This method makes the Agent deregister all the created Roles.
     * 
     * @param cAgent
     */
    private void deregisterRoles(CAgent cAgent) {
        // Deregister the roles I have created
        ArrayList<UnitRolePair> agentCreatedRoles = myLocalData.getCreatedRolesInReverseOrder();
        for (UnitRolePair createdRole : agentCreatedRoles) {
            try {
                logger.debug("[" + cAgent.getName() + "] deregistering role \"" + createdRole.getRoleID() + "\" at \"" + createdRole.getUnitID() + "\": " + omsProxy.deregisterRole(createdRole.getRoleID(), createdRole.getUnitID()));
            } catch (Exception e) {
                logger.error("[" + cAgent.getName() + "] Exception in DeregisterRole method: " + e.getMessage());
            }
        }
    }

    /**
     * This method makes the Agent deregister all the created Units.
     * 
     * @param cAgent
     */
    private void deregisterUnits(CAgent cAgent) {
        // Requesting for the list of created unit by the agent in order
        // to deregister them.
        for (String unit : myLocalData.getCreatedUnitsInReverseOrder()) {
            try {
                logger.debug("[" + cAgent.getName() + "] deregistering unit \"" + unit + "\": " + omsProxy.deregisterUnit(unit));
            } catch (Exception e) {
                logger.error("[" + cAgent.getName() + "] Exception in DeregisterUnit method: " + e.getMessage());
            }
        }
    }

    /**
     * This method makes the Agent leaves THOMAS.
     * 
     * @param cAgent
     */
    private void leaveTHOMAS(CAgent cAgent) {
        try {
            logger.debug("[" + cAgent.getName() + "] Exiting from THOMAS: " + omsProxy.leaveRole("participant", "virtual"));
        } catch (Exception ex) {
            logger.error("[" + cAgent.getName() + "] Exception in LeaveRole method: " + ex.getMessage());
        }
    } // End of method leaveTHOMAS ()

} // End class GodAgent

