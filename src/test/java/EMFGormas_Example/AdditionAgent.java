/**
 * This class has been generated using Gormas2Magentix tool.
 * 
 * @author Mario Rodrigo - mrodrigo@dsic.upv.es
 * 
 */
package EMFGormas_Example;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import EMFGormas_Example.Utils.LocalData;
import EMFGormas_Example.Utils.UnitRolePair;

import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Initiator;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.Oracle;
import es.upv.dsic.gti_ia.organization.Provider;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.ServiceTools;
import es.upv.dsic.gti_ia.organization.THOMASException;

public class AdditionAgent extends CAgent {

    // ----------------------------------------
    // FIELDS of the class
    // ----------------------------------------
    private OMSProxy          omsProxy;
    private SFProxy           sfProxy;
    private ArrayList<String> results;
    private Oracle            oracle;
    private String            serviceName;
    private LocalData         myLocalData;

    // ----------------------------------------
    // CONSTRUCTOR of the class
    // ----------------------------------------
    public AdditionAgent(AgentID aid) throws Exception {
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
        omsProxy = new OMSProxy(this);
        sfProxy = new SFProxy(this);
        String result;
        ArrayList<String> resultArrayList;
        UnitRolePair desiredRole;
        ACLMessage msg;
        ArrayList<String> searchInputs = new ArrayList<String>();
        ArrayList<String> searchOutputs = new ArrayList<String>();
        ArrayList<String> searchKeywords = new ArrayList<String>();
        ArrayList<ArrayList<String>> foundServices = new ArrayList<ArrayList<String>>();

        // Requesting for the list of roles and units in which the agent is in a
        // specific moment.
        CAgent cAgent = firstProcessor.getMyAgent();

        try {
            // Get the list of roles that agent is playing
            ArrayList<UnitRolePair> agentPlayingRoles = Utils.queryOMSForRolesPlayedByAnAgent(cAgent, omsProxy, logger);
            for (UnitRolePair unitRolePair : agentPlayingRoles) {
                myLocalData.addPlayingRole(unitRolePair);
            }

            desiredRole = new UnitRolePair("operation", "Calculator");

            if (!myLocalData.getPlayingRoles().contains(desiredRole)) {
                // Acquire Role in main Organization
                result = omsProxy.acquireRole("operation", "Calculator");

                if (result.contains("acquired")) {
                    // Add the pair Unit - Role to playingRole ArrayList
                    myLocalData.addPlayingRole(desiredRole);
                }
                logger.debug("[" + getName() + "] Entering in 'Calculator': " + result + "\n");

            } else {
                logger.debug("[" + getName() + "] is inside in 'Calculator'.");
            }

        } catch (Exception e) {
            logger.error("[" + getName() + "]" + e.getMessage());
        }

        // The agent creates the CFactory that manages every message which its
        // performative is set to REQUEST and filter is set to shutdown.

        // The agent creates the CFactory that manages every message which its
        // performative is set to REQUEST and filter is set to shutdown.

        // We create a factory in order to manage ShutdownAgent orders
        class ShutdownAgentFIPA_REQUEST extends FIPA_REQUEST_Participant {

            @Override
            protected String doAction(CProcessor myProcessor) {
                CAgent cAgent = myProcessor.getMyAgent();
                removePublishedServices(cAgent);
                leaveRoles(cAgent);
                deregisterRoles(cAgent);
                deregisterUnits(cAgent);
                return "INFORM";
            }

            @Override
            protected void doInform(CProcessor myProcessor, ACLMessage response) {
                CAgent cAgent = myProcessor.getMyAgent();
                response.setSender(cAgent.getAid());
                response.setReceiver(myProcessor.getLastReceivedMessage().getSender());
                response.setHeader("shutdown", "ShutdownAgent");
                response.setContent("All my published services have been removed and all played roles have been left.");
                logger.debug("[" + myProcessor.getMyAgent().getName() + "] All my published services have been removed and all played roles have been left.");
                myProcessor.ShutdownAgent();
            }

            @Override
            protected String doReceiveRequest(CProcessor myProcessor, ACLMessage request) {
                return "AGREE";
            }

            @Override
            protected void doAgree(CProcessor myProcessor, ACLMessage messageToSend) {
                messageToSend.setPerformative(ACLMessage.AGREE);
                messageToSend.setHeader("shutdown", "ShutdownAgent");
                messageToSend.setSender(myProcessor.getMyAgent().getAid());
                messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
                messageToSend.setHeader("shutdown", "ShutdownAgent");
                messageToSend.setContent("Received \"Shutdown\" order. Removing all my published services and leaving all played roles.");
                logger.debug("[" + myProcessor.getMyAgent().getName() + "] Received \"Shutdown\" order. Removing all my published services and leaving all played roles.");
            }
        }

        ACLMessage template;
        MessageFilter shutdownFilter = new MessageFilter("performative = " + ACLMessage.getPerformative(ACLMessage.REQUEST) + "  AND shutdown = ShutdownAgent");

        CFactory shutdownTalk = new ShutdownAgentFIPA_REQUEST().newFactory("ShutdownTalk", shutdownFilter, 1, firstProcessor.getMyAgent());
        // The template processor is ready. We activate the factory
        // as participant. Every message that arrives to the agent
        // with the performative set to REQUEST will make the factory
        // ShutdownTalk to create a processor in order to manage the
        // conversation.
        this.addFactoryAsParticipant(shutdownTalk);

        // Register services

        registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
        // YOUR CODE FOR REGISTER SERVICES STARTS HERE
        //
        // 
        // YOUR CODE ENDS HERE

        // Each agent's conversation is carried out by a CProcessor.
        // CProcessors are created by the CFactories in response
        // to messages that start the agent's activity in a conversation

        // An easy way to create CFactories is to create them from the
        // predefined factories of package es.upv.dsi.gti_ia.cAgents.protocols
        // Another option, not shown here, is that the agent
        // designs her own factory and, therefore, a new interaction protocol
        class AdditionFIPA_REQUEST extends FIPA_REQUEST_Participant {

            ServiceTools            st          = new ServiceTools();
            HashMap<String, String> inputs      = new HashMap<String, String>();
            String                  serviceName = "";

            @Override
            protected String doAction(CProcessor myProcessor) {

                // YOUR CODE STARTS HERE
                //

                String next = "";
                Double resultContent = 0.0;
                try {

                    String serviceWSDLURL = "http://localhost:8080/testSFservices/services/Addition?wsdl";
                    HashMap<String, Object> result = st.executeWebService(serviceWSDLURL, inputs);

                    next = "INFORM";

                    resultContent = (Double) result.get("Result");

                    String resultXML = "";
                    resultXML += "<serviceOutput>\n";
                    resultXML += "<serviceName>" + serviceName + "</serviceName>\n";
                    resultXML += "<outputs>\n";
                    resultXML += "<Result>" + resultContent + "</Result>\n";
                    resultXML += "</outputs>\n";
                    resultXML += "</serviceOutput>\n";

                    myProcessor.getLastReceivedMessage().setContent("" + resultXML);

                } catch (Exception e) {
                    next = "FAILURE";
                }

                return next;

                //
                // YOUR CODE ENDS HERE
            }

            @Override
            protected void doInform(CProcessor myProcessor, ACLMessage response) {
                // YOUR CODE STARTS HERE
                //

                ACLMessage lastReceivedMessage = myProcessor.getLastReceivedMessage();
                response.setContent(lastReceivedMessage.getContent());

                //
                // YOUR CODE ENDS HERE
            }

            @Override
            protected String doReceiveRequest(CProcessor myProcessor, ACLMessage request) {
                // YOUR CODE STARTS HERE
                //

                String next = "";
                ACLMessage msg = request;

                if (msg != null) {

                    try {

                        inputs.clear();
                        serviceName = st.extractServiceContent(msg.getContent(), inputs);

                        if (serviceName.toLowerCase().contains("addition")) {

                            logger.info("AGREE");
                            next = "AGREE";

                        } else {

                            logger.info("REFUSE");
                            next = "REFUSE";
                        }

                    } catch (Exception e) {

                        logger.info("EXCEPTION");
                        System.out.println(e);
                        e.printStackTrace();
                        throw new RuntimeException(e.getMessage());

                    }

                } else {

                    logger.info("NOTUNDERSTOOD");
                    next = "NOT_UNDERSTOOD";
                }

                logger.info("[Addition]Sending First message:" + next);

                return next;

                //
                // YOUR CODE ENDS HERE
            }

            @Override
            protected void doAgree(CProcessor myProcessor, ACLMessage messageToSend) {
                messageToSend.setPerformative(ACLMessage.AGREE);
                messageToSend.setSender(myProcessor.getMyAgent().getAid());
                messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
            }
        } // End of class AdditionFIPA_REQUEST

        // YOUR CODE FOR CREATE PARTICIPANTS MANAGERS FOR CONVERSATIONS STARTS
        // HERE
        //
        // 
        // YOUR CODE ENDS HERE

        // The agent creates the CFactory that manages every message which its
        // performative is set to REQUEST and protocol set to REQUEST. In this
        // example the CFactory gets the name "TALK", we don't add any
        // additional message acceptance criterion other than the required
        // by the REQUEST protocol (null) and we limit the number of
        // simultaneous
        // processors to 1, i.e. the requests will be attended one after
        // another.
        MessageFilter templateForAddition = new MessageFilter("performative = " + ACLMessage.getPerformative(ACLMessage.REQUEST) + " AND serviceName = Addition");
        CFactory talkFactoryForAddition = new AdditionFIPA_REQUEST().newFactory("AdditionTalk", templateForAddition, 0, firstProcessor.getMyAgent());

        // Finally the factory is setup to answer to incoming messages that
        // can start the participation of the agent in a new conversation
        this.addFactoryAsParticipant(talkFactoryForAddition);

        // YOUR CODE FOR MANAGE CONVERSATIONS STARTS HERE
        //
        // 
        // YOUR CODE ENDS HERE

        // Each agent's conversation is carried out by a CProcessor.
        // CProcessors are created by the CFactories in response
        // to messages that start the agent's activity in a conversation

        // An easy way to create CFactories is to create them from the
        // predefined factories of package es.upv.dsi.gri_ia.cAgents.protocols
        // YOUR CODE FOR CREATE INITIATORS MANAGERS FOR CONVERSATIONS STARTS
        // HERE
        //
        // 
        // YOUR CODE ENDS HERE

        // In order to start a conversation the agent creates a message
        // that can be accepted by one of its initiator factories.

    } // End of method execution()

    // This method is executed just before the agent ends its execution
    @Override
    protected void finalize(CProcessor firstProcessor, ACLMessage finalizeMessage) {
        CAgent cAgent = firstProcessor.getMyAgent();
        logger.info("[" + cAgent.getName() + "] finalize method executed.");

    } // End of method finalize()

    /**
     * Method to remove all the services that this Agent provides and has
     * registered in SF.
     * 
     * @param cAgent
     */
    private void removePublishedServices(CAgent cAgent) {
        for (String service : myLocalData.getRegisteredServices()) {
            deregisterService(service);
        }

    } // End of method removePublishedServices ()

    /**
     * This method makes the Agent leave all the acquired Roles.
     * 
     * @param cAgent
     */
    private void leaveRoles(CAgent cAgent) {
        // Requesting for the list of roles and units in which the agent is in a
        // specific moment to leave them.

        ArrayList<UnitRolePair> agentPlayingRoles = myLocalData.getPlayingRolesInReverseOrder();
        for (UnitRolePair playingRole : agentPlayingRoles) {
            try {
                logger.debug("[" + cAgent.getName() + "] leaving role \"" + playingRole.getRoleID() + "\" at \"" + playingRole.getUnitID() + "\": " + omsProxy.leaveRole(playingRole.getRoleID(), playingRole.getUnitID()));
            } catch (Exception e) {
                logger.error("[" + cAgent.getName() + "] Exception in LeaveRole method: " + e.getMessage());
            }
        }
    }

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
     * Method to register the service on SF.
     * 
     * @param serviceName
     *            String with the name used to call the user's service.
     * @param profile
     *            ProfileDescription of the user's service.
     */
    private void registerService(String serviceName) {
        try {
            ArrayList<String> resultRegister = sfProxy.registerService(serviceName);
            Iterator<String> iterRes = resultRegister.iterator();
            String registerRes = "";
            while (iterRes.hasNext()) {
                registerRes += iterRes.next() + "\n";
            }
            logger.debug("[" + this.getName() + "] Result registerService: " + registerRes);

            String[] parts = resultRegister.get(0).split(": ");
            String serviceProfile = parts[1].trim();

            myLocalData.addRegisteredServices(serviceProfile);

        } catch (THOMASException e) {
            logger.error("[" + getName() + "]" + e.getMessage());
        }
    }

    /**
     * Deregistering the service from SF
     * 
     * @param serviceName
     */
    private void deregisterService(String service) {
        try {
            String serviceToRemove = service.substring(service.lastIndexOf("/") + 1, service.indexOf("."));
            logger.debug("[" + getName() + "] deregistering service \"" + serviceToRemove + "\".");

            sfProxy.deregisterService(service);

        } catch (Exception ex) {
            logger.error("[" + getName() + "] Exception in RemoveProvider method: " + ex.getMessage());
        }

    }// end of method DeregisterService(String service)

} // End of class AdditionAgent
