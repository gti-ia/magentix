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

public class JamesAgent extends CAgent {

    // ----------------------------------------
    // FIELDS of the class
    // ----------------------------------------
    private OMSProxy          omsProxy;
    private SFProxy           sfProxy;
    private ArrayList<String> results;
    private Oracle            oracle;
    private String            serviceName;
    private LocalData         myLocalData;
    private ServiceTools      st;
    private String            requestResult;
    private String            resultEquation;

    // ----------------------------------------
    // CONSTRUCTOR of the class
    // ----------------------------------------
    public JamesAgent(AgentID aid) throws Exception {
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

        if (st == null) {
            st = new ServiceTools();
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
        ArrayList<String> serviceInputs;
        HashMap<String, String> agentInputs;


        // Requesting for the list of roles and units in which the agent is in a
        // specific moment.
        CAgent cAgent = firstProcessor.getMyAgent();

        try {
            // Get the list of roles that agent is playing
            ArrayList<UnitRolePair> agentPlayingRoles = Utils.queryOMSForRolesPlayedByAnAgent(cAgent, omsProxy, logger);
            for (UnitRolePair unitRolePair : agentPlayingRoles) {
                myLocalData.addPlayingRole(unitRolePair);
            }

            desiredRole = new UnitRolePair("student", "School");

            if (!myLocalData.getPlayingRoles().contains(desiredRole)) {
                // Acquire Role in main Organization
                result = omsProxy.acquireRole("student", "School");

                if (result.contains("acquired")) {
                    // Add the pair Unit - Role to playingRole ArrayList
                    myLocalData.addPlayingRole(desiredRole);
                }
                logger.debug("[" + getName() + "] Entering in 'School': " + result + "\n");

            } else {
                logger.debug("[" + getName() + "] is inside in 'School'.");
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
        // YOUR CODE FOR MANAGE CONVERSATIONS STARTS HERE
        //
        // 
        // YOUR CODE ENDS HERE

        // Each agent's conversation is carried out by a CProcessor.
        // CProcessors are created by the CFactories in response
        // to messages that start the agent's activity in a conversation

        // An easy way to create CFactories is to create them from the
        // predefined factories of package es.upv.dsi.gri_ia.cAgents.protocols
        class SquareFIPA_REQUEST extends FIPA_REQUEST_Initiator {
            protected void doInform(CProcessor myProcessor, ACLMessage msg) {
                // YOUR CODE STARTS HERE
                //
                logger.debug("[" + myProcessor.getMyAgent().getName() + "] " + msg.getSender().name + " informs me " + msg.getContent());
                requestResult = msg.getContent();
                HashMap<String, String> outputs = new HashMap<String, String>();
                st.extractServiceContent(requestResult, outputs);
                resultEquation = outputs.get("Result");
                //
                // YOUR CODE ENDS HERE
            }

            protected void doRequest(CProcessor myProcessor, ACLMessage messageToSend) {
            }
        } // End of class SquareFIPA_REQUEST
        class ProductFIPA_REQUEST extends FIPA_REQUEST_Initiator {
            protected void doInform(CProcessor myProcessor, ACLMessage msg) {
                // YOUR CODE STARTS HERE
                //
                logger.debug("[" + myProcessor.getMyAgent().getName() + "] " + msg.getSender().name + " informs me " + msg.getContent());
                requestResult = msg.getContent();
                HashMap<String, String> outputs = new HashMap<String, String>();
                st.extractServiceContent(requestResult, outputs);
                resultEquation = outputs.get("Result");
                //
                // YOUR CODE ENDS HERE
            }

            protected void doRequest(CProcessor myProcessor, ACLMessage messageToSend) {
            }
        } // End of class ProductFIPA_REQUEST
        class AdditionFIPA_REQUEST extends FIPA_REQUEST_Initiator {
            protected void doInform(CProcessor myProcessor, ACLMessage msg) {
                // YOUR CODE STARTS HERE
                //
                logger.debug("[" + myProcessor.getMyAgent().getName() + "] " + msg.getSender().name + " informs me " + msg.getContent());
                requestResult = msg.getContent();
                HashMap<String, String> outputs = new HashMap<String, String>();
                st.extractServiceContent(requestResult, outputs);
                resultEquation = outputs.get("Result");

                //
                // YOUR CODE ENDS HERE
            }

            protected void doRequest(CProcessor myProcessor, ACLMessage messageToSend) {

            }
        } // End of class AdditionFIPA_REQUEST
        // YOUR CODE FOR CREATE INITIATORS MANAGERS FOR CONVERSATIONS STARTS
        // HERE
        //
        // 
        // YOUR CODE ENDS HERE

        // In order to start a conversation the agent creates a message
        // that can be accepted by one of its initiator factories.
 
        // Search for the service "Addition"

        searchKeywords.clear();
        searchKeywords.add("addition");
        searchInputs.add(ServiceTools.OntologicalTypesConstants.DOUBLE);
        searchInputs.add(ServiceTools.OntologicalTypesConstants.DOUBLE);

        searchOutputs.add(ServiceTools.OntologicalTypesConstants.DOUBLE);
        foundServices.clear();

        do {
            // Waiting for services
            try {
                Thread.sleep(2 * 1000);

                foundServices = sfProxy.searchService(searchInputs, searchOutputs, searchKeywords);

            } catch (InterruptedException e) {
                logger.error("[" + getName() + "] " + e.getMessage());

            } catch (THOMASException e) {
                logger.error("[" + getName() + "] " + e.getMessage());
            }

        } while (foundServices.isEmpty());

        // Request the execution of the service "Addition"
        try {

            String serviceOWLS = sfProxy.getService(foundServices.get(0).get(0));
            Oracle oracle = new Oracle(serviceOWLS);

            // get service inputs
            serviceInputs = oracle.getOwlsProfileInputs();

            // put the service inputs values
            agentInputs = new HashMap<String, String>();

            for (String input : serviceInputs) {
                if (input.equalsIgnoreCase("x"))
                    agentInputs.put(input, "3");
                else if (input.equalsIgnoreCase("y"))
                    agentInputs.put(input, "4");
                else
                    agentInputs.put(input, "0");
            }

            // Agents or Organizations that can execute the service for me.
            ArrayList<Provider> providers = oracle.getProviders();
            // Groundings to execute directly the service by myself.
            ArrayList<String> providersGroundingWSDL = oracle.getProvidersGroundingWSDL();

            if (!providers.isEmpty()) {
                // Agents or Organizations that can execute the service for me.
                // YOUR CODE STARTS HERE
                //                

                logger.debug("[" + this.getName() + "]" + " Requesting Addition Service.");

                msg = null;
                msg = new ACLMessage(ACLMessage.REQUEST);
                msg.setReceiver(new AgentID(providers.get(0).getEntityID()));
                msg.setProtocol("fipa-request");
                msg.setSender(getAid());

                String content = "";
                content = st.buildServiceContent(oracle.getServiceName(), agentInputs);
                msg.setContent(content);

                // The agent creates the CFactory that creates processors
                // that
                // // initiate REQUEST protocol conversations. In this example
                // // the CFactory gets the name "TALK", we don't add any
                // // additional message acceptance criterion other than the
                // required
                // // by the REQUEST protocol (null) and we do not limit the
                // number of
                // // simultaneous processors (value 0)
                CFactory talkFactoryForAddition = new AdditionFIPA_REQUEST().newFactory("AdditionTalk", null, msg, 1, firstProcessor.getMyAgent(), 0);

                // // The factory is setup to answer start conversation requests
                // from
                // // the agent using the REQUEST protocol.

                this.addFactoryAsInitiator(talkFactoryForAddition);

                // // finally the new conversation starts. Because it is
                // synchronous,
                // // the current interaction halts until the new conversation
                // ends.
                this.startSyncConversation("AdditionTalk");

                // YOUR CODE ENDS HERE

            } else if (!providersGroundingWSDL.isEmpty()) {
                // Groundings to execute the service directly by myself.
                // YOUR CODE STARTS HERE
                //
                logger.debug("[" + this.getName() + "]" + " Executing Addition Service.");

                HashMap<String, Object> resultExecution = st.executeWebService(providersGroundingWSDL.get(0), agentInputs);

                Double resultDouble = (Double) resultExecution.get("Result");
                resultEquation = resultDouble.toString();

                logger.debug("[" + this.getName() + "] Final result: " + resultEquation + ".");

                // YOUR CODE ENDS HERE
            } else {
                // no providers for this service
                logger.warn("[" + this.getName() + "]" + " No providers found for Addition Service.");
            }
        } catch (THOMASException e) {
            logger.error("[" + getName() + "] " + e.getMessage());
        }

        // Search for the service "Product"
        searchKeywords.clear();
        searchKeywords.add("multiplies");

        do {
            // Waiting for services
            try {
                Thread.sleep(2 * 1000);

                foundServices = sfProxy.searchService(searchInputs, searchOutputs, searchKeywords);

            } catch (InterruptedException e) {
                logger.error("[" + getName() + "] " + e.getMessage());

            } catch (THOMASException e) {
                logger.error("[" + getName() + "] " + e.getMessage());
            }

        } while (foundServices.isEmpty());

        // Request the execution of the service "Product"
        try {
            String serviceOWLS = sfProxy.getService(foundServices.get(0).get(0));
            Oracle oracle = new Oracle(serviceOWLS);

            // get service inputs
            serviceInputs = oracle.getOwlsProfileInputs();

            agentInputs = new HashMap<String, String>();
            for (String input : serviceInputs) {
                if (input.equalsIgnoreCase("x"))
                    agentInputs.put(input, "5");
                else if (input.equalsIgnoreCase("y"))
                    agentInputs.put(input, resultEquation);
                else
                    agentInputs.put(input, "0");
            }
            // Agents or Organizations that can execute the service for me.
            ArrayList<Provider> providers = oracle.getProviders();
            // Groundings to execute directly the service by myself.
            ArrayList<String> providersGroundingWSDL = oracle.getProvidersGroundingWSDL();

            if (!providers.isEmpty()) {
                // Agents or Organizations that can execute the service for me.
                // YOUR CODE STARTS HERE
                //
                logger.debug("[" + this.getName() + "]" + " Requesting Product Service.");

                msg = new ACLMessage(ACLMessage.REQUEST);
                msg.setReceiver(new AgentID(providers.get(0).getEntityID()));
                msg.setProtocol("fipa-request");
                msg.setSender(getAid());

                String content = st.buildServiceContent(oracle.getServiceName(), agentInputs);
                msg.setContent(content);

                // The agent creates the CFactory that creates processors
                // that
                // initiate REQUEST protocol conversations. In this example
                // the CFactory gets the name "TALK", we don't add any
                // additional message acceptance criterion other than the
                // required
                // by the REQUEST protocol (null) and we do not limit the
                // number of
                // simultaneous processors (value 0)
                CFactory talkFactoryForProduct = new ProductFIPA_REQUEST().newFactory("ProductTalk", null, msg, 1, firstProcessor.getMyAgent(), 0);

                // The factory is setup to answer start conversation requests
                // from
                // the agent using the REQUEST protocol.

                this.addFactoryAsInitiator(talkFactoryForProduct);

                // finally the new conversation starts. Because it is
                // synchronous,
                // the current interaction halts until the new conversation
                // ends.
                this.startSyncConversation("ProductTalk");

                // YOUR CODE ENDS HERE

            } else if (!providersGroundingWSDL.isEmpty()) {
                // Groundings to execute the service directly by myself.
                // YOUR CODE STARTS HERE
                //

                logger.debug("[" + this.getName() + "]" + " Executing Product Service.");

                HashMap<String, Object> resultExecution = st.executeWebService(providersGroundingWSDL.get(0), agentInputs);

                Double resultDouble = (Double) resultExecution.get("Result");
                resultEquation = resultDouble.toString();

                logger.debug("[" + this.getName() + "] Final result: " + resultEquation + ".");

                //
                // YOUR CODE ENDS HERE
            } else {
                // no providers for this service
                logger.info("[" + this.getName() + "]" + " No providers found for Product Service.");
            }
        } catch (THOMASException e) {
            logger.error("[" + getName() + "] " + e.getMessage());
        }

        // Search for the service "Square"
        searchInputs.clear();
        searchInputs.add(ServiceTools.OntologicalTypesConstants.DOUBLE);
        searchKeywords.clear();
        searchKeywords.add("squares");

        foundServices.clear();

        do {
            // Waiting for services
            try {
                Thread.sleep(2 * 1000);

                foundServices = sfProxy.searchService(searchInputs, searchOutputs, searchKeywords);

            } catch (InterruptedException e) {
                logger.error("[" + getName() + "] " + e.getMessage());

            } catch (THOMASException e) {
                logger.error("[" + getName() + "] " + e.getMessage());
            }

        } while (foundServices.isEmpty());

        // Request the execution of the service "Square"
        try {
            String serviceOWLS = sfProxy.getService(foundServices.get(0).get(0));
            Oracle oracle = new Oracle(serviceOWLS);

            // get service inputs
            serviceInputs = oracle.getOwlsProfileInputs();

            agentInputs = new HashMap<String, String>();
            for (String input : serviceInputs) {
                agentInputs.put(input, resultEquation);
            }

            // Agents or Organizations that can execute the service for me.
            ArrayList<Provider> providers = oracle.getProviders();
            // Groundings to execute directly the service by myself.
            ArrayList<String> providersGroundingWSDL = oracle.getProvidersGroundingWSDL();

            if (!providers.isEmpty()) {
                // Agents or Organizations that can execute the service for me.
                // YOUR CODE STARTS HERE
                //
                logger.debug("[" + this.getName() + "]" + " Requesting Square Service.");

                msg = new ACLMessage(ACLMessage.REQUEST);
                msg.setReceiver(new AgentID(providers.get(0).getEntityID()));
                msg.setProtocol("fipa-request");
                msg.setSender(getAid());

                String content = st.buildServiceContent(oracle.getServiceName(), agentInputs);
                msg.setContent(content);

                // The agent creates the CFactory that creates processors
                // that
                // initiate REQUEST protocol conversations. In this example
                // the CFactory gets the name "TALK", we don't add any
                // additional message acceptance criterion other than the
                // required
                // by the REQUEST protocol (null) and we do not limit the
                // number of
                // simultaneous processors (value 0)
                CFactory talkFactoryForSquare = new SquareFIPA_REQUEST().newFactory("SquareTalk", null, msg, 1, firstProcessor.getMyAgent(), 0);

                // The factory is setup to answer start conversation requests
                // from
                // the agent using the REQUEST protocol.

                this.addFactoryAsInitiator(talkFactoryForSquare);

                // finally the new conversation starts. Because it is
                // synchronous,
                // the current interaction halts until the new conversation
                // ends.
                this.startSyncConversation("SquareTalk");

                //
                // YOUR CODE ENDS HERE

            } else if (!providersGroundingWSDL.isEmpty()) {
                // Groundings to execute the service directly by myself.
                // YOUR CODE STARTS HERE
                //

                logger.debug("[" + this.getName() + "]" + " Executing Square Service.");

                HashMap<String, Object> resultExecution = st.executeWebService(providersGroundingWSDL.get(0), agentInputs);

                Double resultDouble = (Double) resultExecution.get("Result");
                resultEquation = resultDouble.toString();

                //
                // YOUR CODE ENDS HERE
            } else {
                // no providers for this service
                logger.info("[" + this.getName() + "]" + " No providers found for Square Service.");
            }

            String finalResult = resultEquation;

            logger.info("\n\n[" + this.getName() + "] Final result: " + finalResult + "\n\n");

        } catch (THOMASException e) {
            logger.error("[" + getName() + "] " + e.getMessage());
        }

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

} // End of class JamesAgent
