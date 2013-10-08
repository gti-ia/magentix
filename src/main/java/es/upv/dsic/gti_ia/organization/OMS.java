package es.upv.dsic.gti_ia.organization;

/**
 * OMS.java
 * 
 * @version 2.0
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

//import normative_jason.SimpleArchitecture;





import org.apache.log4j.Logger;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.jason.JasonAgent;
import es.upv.dsic.gti_ia.norms.NormsMagentixAgArch;
import es.upv.dsic.gti_ia.organization.exception.ExchangeBindException;
import es.upv.dsic.gti_ia.organization.exception.ExchangeUnbindException;
import es.upv.dsic.gti_ia.organization.exception.InvalidPositionException;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages.MessageID;
/**
 * OMS agent is responsible for managing all the request messages from other
 * entities OMS agent follows a FIPA-Request protocol
 * 
 * @author Jose Alemany Bordera - jalemany1@dsic.upv.es
 */
public class OMS extends CAgent {

	// CAgents
	String                                  msgContent                    = "";

	Configuration                           configuration                 = Configuration.getConfiguration();

	OMSInterface                            omsInterface                  = new OMSInterface(this);

	DataBaseInterface						dbInterface 				  = new DataBaseInterface();

	ResponseParser                          responseParser                = new ResponseParser();

	String                                  separatorToken                = " ";
	private String                          OMSServiceDescriptionLocation = configuration.getOMSServiceDescriptionLocation();
	private static HashMap<String, Integer> omsServicesURLs               = new HashMap<String, Integer>();
	ServiceTools                            st                            = new ServiceTools();
	static Logger                           logger                        = Logger.getLogger(OMS.class);

	/**
	 * Used for retrieve local messages.
	 */

	private THOMASMessages                  l10n;
	
	JasonAgent ag;

//	// URI where the SF service descriptions are located
//
//	/**
//	 * Returns an instance of the agents OMS
//	 * 
//	 * @param agent
//	 *            a new Agent ID
//	 * @return oms
//	 */
//	static public OMS getOMS(AgentID agent) {
//		if (oms == null) {
//			try {
//				oms = new OMS(agent);
//			} catch (Exception e) {
//				logger.error(e);
//			}
//		}
//		return oms;
//	}
//
//	/**
//	 * Returns an instance of the agents OMS
//	 * 
//	 * @return oms
//	 */
//	static public OMS getOMS() {
//		if (oms == null) {
//			try {
//				oms = new OMS(new AgentID("OMS"));
//			} catch (Exception e) {
//				logger.error(e);
//			}
//		}
//		return oms;
//	}

	/**
	 * Constructor which creates and initializes the OMS agent.
	 * 
	 * @param aid	AgentID which will be used to create the agent
	 * 
	 * @throws Exception
	 */
	public OMS(AgentID aid) throws Exception {
		super(aid);
		l10n = new THOMASMessages();
		omsServicesURLs.put("RegisterUnit", 1);
		omsServicesURLs.put("JointUnit", 2);
		omsServicesURLs.put("RegisterRole", 3);
		omsServicesURLs.put("DeregisterUnit", 4);
		omsServicesURLs.put("DeregisterRole", 5);
		omsServicesURLs.put("AcquireRole", 6);
		omsServicesURLs.put("AllocateRole", 7);
		omsServicesURLs.put("DeallocateRole", 8);
		omsServicesURLs.put("LeaveRole", 9);
		omsServicesURLs.put("InformUnit", 10);
		omsServicesURLs.put("InformRole", 11);
		omsServicesURLs.put("InformAgentRole", 12);
		omsServicesURLs.put("InformMembers", 13);
		omsServicesURLs.put("InformUnitRoles", 14);
		omsServicesURLs.put("QuantityMembers", 15);
		omsServicesURLs.put("RegisterNorm", 16);
		omsServicesURLs.put("DeregisterNorm", 17);
		omsServicesURLs.put("InformNorm", 18);
		omsServicesURLs.put("InformTargetNorms", 19);
		
		//Launch Jason Agent
		//SimpleArchitecture archEjemplo = new SimpleArchitecture();
		NormsMagentixAgArch archEjemplo = new NormsMagentixAgArch();
		
		ag = new JasonAgent(new AgentID("JasonNormativeAgent"), "configuration/NormativeAgent.asl", archEjemplo);
		ag.start();
	}
	
	/**
	 * Constructor which creates and initializes the OMS agent. In this case the agent ID is OMS.
	 * 
	 * @throws Exception
	 */
	public OMS() throws Exception {
		super(new AgentID("OMS"));
		l10n = new THOMASMessages();
		omsServicesURLs.put("RegisterUnit", 1);
		omsServicesURLs.put("JointUnit", 2);
		omsServicesURLs.put("RegisterRole", 3);
		omsServicesURLs.put("DeregisterUnit", 4);
		omsServicesURLs.put("DeregisterRole", 5);
		omsServicesURLs.put("AcquireRole", 6);
		omsServicesURLs.put("AllocateRole", 7);
		omsServicesURLs.put("DeallocateRole", 8);
		omsServicesURLs.put("LeaveRole", 9);
		omsServicesURLs.put("InformUnit", 10);
		omsServicesURLs.put("InformRole", 11);
		omsServicesURLs.put("InformAgentRole", 12);
		omsServicesURLs.put("InformMembers", 13);
		omsServicesURLs.put("InformUnitRoles", 14);
		omsServicesURLs.put("QuantityMembers", 15);
		omsServicesURLs.put("RegisterNorm", 16);
		omsServicesURLs.put("DeregisterNorm", 17);
		omsServicesURLs.put("InformNorm", 18);
		omsServicesURLs.put("InformTargetNorms", 19);
	}

	/**
	 * Changes the URL where the owl's document is located.
	 * 
	 * @param OMSUrl
	 */
	public void setOMSServiceDesciptionLocation(String OMSUrl) {

		this.OMSServiceDescriptionLocation = OMSUrl;
	}

	/**
	 * Gets the URL where the owl's document is located.
	 * 
	 * @param OMSUrl
	 */
	public String getOMSServiceDesciptionLocation() {

		return OMSServiceDescriptionLocation;
	}

	@Override
	protected void finalize(CProcessor firstProcessor, ACLMessage finalizeMessage) {
		
	}

	/**
	 * Creates a new binding according to type of playing position into
	 * organization
	 * 
	 * @param The
	 *            Agent ID represents the name of the agent queue
	 * @param The
	 *            Organization ID is part of the binging key
	 * @param The
	 *            Position Type, this position may be supervisor, subordinate,
	 *            participant(member) or creator
	 * @throws InvalidPositionException
	 *             in order to show the invalid value for Position.
	 * @throws ExchangeBindException
	 *             in order to show the Exchange error.
	 */
	private void createBinding(String aid, String OrganizationID, String positionType) throws InvalidPositionException, ExchangeBindException {

		Map<String, Object> arguments = new HashMap<String, Object>();

		if (positionType.equals("member") || positionType.equals("subordinate")) {
			arguments.put("x-match", "all");
			arguments.put("participant", OrganizationID);
		} else if (positionType.equals("supervisor")) {
			arguments.put("x-match", "any");
			arguments.put("supervisor", OrganizationID);
			arguments.put("participant", OrganizationID);

		} else // any other
		{

			String message = l10n.getMessage(MessageID.INVALID_POSITION, positionType);
			throw new InvalidPositionException(message);
		}

		try {
			this.session.exchangeBind(aid, "amq.match", aid + "." + OrganizationID + "." + positionType, arguments);
			this.session.sync();
		} catch (Exception e) {
			String message = l10n.getMessage(MessageID.EXCHANGE_BIND, e);
			throw new ExchangeBindException(message);
		}
	}

	/**
	 * Deletes binding with binding key represented by the aid, organizationID
	 * and positionType
	 * 
	 * @param The
	 *            Agent ID represents the name of the agent queue
	 * @param The
	 *            Organization ID is part of the binging key
	 * @param The
	 *            Position Type, this position may be supervisor, subordinate,
	 *            participant(member) or creator
	 * @throws ExchangeUnbindException
	 *             in order to show the cause of exception uses getContent
	 */
	private void deleteBinding(String aid, String OrganizationID, String positionType) throws ExchangeUnbindException {
		try {
			this.session.exchangeUnbind(aid, "amq.match", aid + "." + OrganizationID + "." + positionType);
			this.session.sync();
		} catch (Exception e) {
			String message = l10n.getMessage(MessageID.EXCHANGE_UNBIND, e);
			throw new ExchangeUnbindException(message);

		}

	}

	@Override
	protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {

		class myFIPA_REQUEST extends FIPA_REQUEST_Participant {

			@Override
			protected String doAction(CProcessor myProcessor) {
				String next = "";
				String organizationID = "";
				String aidName = "";
				String rol = "";
				String positionType = "";

				try {
					// execute the service

					responseParser.parseResponse(myProcessor.getLastReceivedMessage().getContent());
					String serviceName = responseParser.getServiceName();

					HashMap<String, String> inputs = responseParser.getKeyAndValueList();

					// Extract the parameters needed to create and delete binds
					if (serviceName.equals("AcquireRole") || serviceName.equals("LeaveRole")) {

						if (inputs.containsKey("AgentID")) {
							aidName = inputs.get("AgentID");
							rol = inputs.get("RoleID");
							organizationID = inputs.get("UnitID");
						} else {
							rol = inputs.get("RoleID");
							organizationID = inputs.get("UnitID");
							aidName = myProcessor.getLastReceivedMessage().getSender().name;

						}
						// -------------Inform Role-----------------

						String content = omsInterface.informRole(rol, organizationID, myProcessor.getLastReceivedMessage().getSender().name);

						responseParser.parseResponse(content);

						if (responseParser.getStatus().equals("Ok"))
							positionType = responseParser.getElementsList().get(0);
					} else if (serviceName.equals("AllocateRole") || serviceName.equals("DeallocateRole")) {

						aidName = inputs.get("TargetAgentID");
						rol = inputs.get("RoleID");
						organizationID = inputs.get("UnitID");
						// -------------Inform Role-----------------

						String content = omsInterface.informRole(rol, organizationID, myProcessor.getLastReceivedMessage().getSender().name);

						responseParser.parseResponse(content);

						if (responseParser.getStatus().equals("Ok"))
							positionType = responseParser.getElementsList().get(0);

					}

					// Execute the service requested by the agent
					String resultContent = "";
					switch (omsServicesURLs.get(serviceName)) {
					case 1: // register unit service
						if (inputs.get("UnitID").trim().equals("null")) {

							resultContent = omsInterface.registerUnit(null, inputs.get("TypeID"), inputs.get("AgentID"), inputs.get("CreatorID"));
						}
						else if (inputs.get("CreatorID").trim().equals("null"))
						{
							resultContent = omsInterface.registerUnit(inputs.get("UnitID"), inputs.get("TypeID"), inputs.get("ParentUnitID"), inputs.get("AgentID"), null);
						}else {

							resultContent = omsInterface.registerUnit(inputs.get("UnitID"), inputs.get("TypeID"), inputs.get("ParentUnitID"), inputs.get("AgentID"), inputs.get("CreatorID"));

						}

						break;
					case 2: // joint unit service
						if (inputs.get("UnitID").trim().equals("null"))
							resultContent = omsInterface.joinUnit(null, inputs.get("ParentUnitID"), inputs.get("AgentID"));
						else if (inputs.get("ParentUnitID").trim().equals("null"))
							resultContent = omsInterface.joinUnit(inputs.get("UnitID"), null, inputs.get("AgentID"));
						else
							resultContent = omsInterface.joinUnit(inputs.get("UnitID"), inputs.get("ParentUnitID"), inputs.get("AgentID"));

						break;
					case 3: // register role service
						if (inputs.get("RoleID").trim().equals("null"))
							resultContent = omsInterface.registerRole(null, inputs.get("UnitID"), inputs.get("AccessibilityID"), inputs.get("VisibilityID"), inputs.get("PositionID"), inputs.get("AgentID"));
						else if (inputs.get("UnitID").trim().equals("null"))
							resultContent = omsInterface.registerRole(inputs.get("RoleID"), null, inputs.get("AccessibilityID"), inputs.get("VisibilityID"), inputs.get("PositionID"), inputs.get("AgentID"));
						else if (inputs.get("AccessibilityID").trim().equals("null"))
							resultContent = omsInterface.registerRole(inputs.get("RoleID"), inputs.get("UnitID"), null, inputs.get("VisibilityID"), inputs.get("PositionID"), inputs.get("AgentID"));
						else if (inputs.get("VisibilityID").trim().equals("null"))
							resultContent = omsInterface.registerRole(inputs.get("RoleID"), inputs.get("UnitID"), inputs.get("AccessibilityID"), null, inputs.get("PositionID"), inputs.get("AgentID"));
						else if (inputs.get("PositionID").trim().equals("null"))
							resultContent = omsInterface.registerRole(inputs.get("RoleID"), inputs.get("UnitID"), inputs.get("AccessibilityID"), inputs.get("VisibilityID"), null, inputs.get("AgentID"));
						else
							resultContent = omsInterface.registerRole(inputs.get("RoleID"), inputs.get("UnitID"), inputs.get("AccessibilityID"), inputs.get("VisibilityID"), inputs.get("PositionID"), inputs.get("AgentID"));

						break;
					case 4: // deregister unit service
						if (inputs.get("UnitID").trim().equals("null"))
							resultContent = omsInterface.deregisterUnit(null, inputs.get("AgentID"));
						else
							resultContent = omsInterface.deregisterUnit(inputs.get("UnitID"), inputs.get("AgentID"));
						break;
					case 5: // De-register role service
						if (inputs.get("RoleID").trim().equals("null"))
								resultContent = omsInterface.deregisterRole(null, inputs.get("UnitID"), inputs.get("AgentID"));
						else if (inputs.get("UnitID").trim().equals("null"))
							resultContent = omsInterface.deregisterRole(inputs.get("RoleID"), null, inputs.get("AgentID"));
						else
							resultContent = omsInterface.deregisterRole(inputs.get("RoleID"), inputs.get("UnitID"), inputs.get("AgentID"));
						break;
					case 6: // acquire role service
						if (inputs.get("RoleID").trim().equals("null"))
							resultContent = omsInterface.acquireRole(null, inputs.get("UnitID"), inputs.get("AgentID"));
						else if (inputs.get("UnitID").trim().equals("null"))
							resultContent = omsInterface.acquireRole(inputs.get("RoleID"), null, inputs.get("AgentID"));
						else
							resultContent = omsInterface.acquireRole(inputs.get("RoleID"), inputs.get("UnitID"), inputs.get("AgentID"));

						break;
					case 7: // allocate role service
						if (inputs.get("RoleID").trim().equals("null"))
							resultContent = omsInterface.allocateRole(null, inputs.get("UnitID"), inputs.get("TargetAgentID"), inputs.get("AgentID"));
						else if (inputs.get("UnitID").trim().equals("null"))
							resultContent = omsInterface.allocateRole(inputs.get("RoleID"), null, inputs.get("TargetAgentID"), inputs.get("AgentID"));
						else if (inputs.get("TargetAgentID").trim().equals("null"))
							resultContent = omsInterface.allocateRole(inputs.get("RoleID"), inputs.get("UnitID"), null, inputs.get("AgentID"));
						else
							resultContent = omsInterface.allocateRole(inputs.get("RoleID"), inputs.get("UnitID"), inputs.get("TargetAgentID"), inputs.get("AgentID"));

						break;
					case 8: // deallocate role service
						if (inputs.get("RoleID").trim().equals("null"))
							resultContent = omsInterface.deallocateRole(null, inputs.get("UnitID"), inputs.get("TargetAgentID"), inputs.get("AgentID"));
						else if (inputs.get("UnitID").trim().equals("null"))
							resultContent = omsInterface.deallocateRole(inputs.get("RoleID"), null, inputs.get("TargetAgentID"), inputs.get("AgentID"));
						else if (inputs.get("TargetAgentID").trim().equals("null"))
							resultContent = omsInterface.deallocateRole(inputs.get("RoleID"), inputs.get("UnitID"), null, inputs.get("AgentID"));
						else
							resultContent = omsInterface.deallocateRole(inputs.get("RoleID"), inputs.get("UnitID"), inputs.get("TargetAgentID"), inputs.get("AgentID"));

						break;
					case 9: // leave role service
						if (inputs.get("RoleID").trim().equals("null"))
							resultContent = omsInterface.leaveRole(null, inputs.get("UnitID"), inputs.get("AgentID"));
						else if (inputs.get("UnitID").trim().equals("null"))
							resultContent = omsInterface.leaveRole(inputs.get("RoleID"), null, inputs.get("AgentID"));
						else
							resultContent = omsInterface.leaveRole(inputs.get("RoleID"), inputs.get("UnitID"), inputs.get("AgentID"));

						break;
					case 10: // inform unit service
						if (inputs.get("UnitID").trim().equals("null"))
							resultContent = omsInterface.informUnit(null, inputs.get("AgentID"));
						else
							resultContent = omsInterface.informUnit(inputs.get("UnitID"), inputs.get("AgentID"));

						break;
					case 11:// inform role service

						if (inputs.get("RoleID").trim().equals("null"))
							resultContent = omsInterface.informRole(null, inputs.get("UnitID"), inputs.get("AgentID"));
						else if (inputs.get("UnitID").trim().equals("null"))
							resultContent = omsInterface.informRole(inputs.get("RoleID"), null, inputs.get("AgentID"));
						else
							resultContent = omsInterface.informRole(inputs.get("RoleID"), inputs.get("UnitID"), inputs.get("AgentID"));
						break;
					case 12: // inform agent role service
						if (inputs.get("RequestedAgentID").trim().equals("null"))
							resultContent = omsInterface.informAgentRole(null, inputs.get("AgentID"));
						else
							resultContent = omsInterface.informAgentRole(inputs.get("RequestedAgentID"), inputs.get("AgentID"));

						break;
					case 13: // inform members service
						if (inputs.get("UnitID").trim().equals("null"))
							resultContent = omsInterface.informMembers(null, inputs.get("RoleID"), inputs.get("PositionID"), inputs.get("AgentID"));
						else
							resultContent = omsInterface.informMembers(inputs.get("UnitID"), inputs.get("RoleID"), inputs.get("PositionID"), inputs.get("AgentID"));

						break;
					case 14:// inform unit role service

						if (inputs.get("UnitID").trim().equals("null"))
							resultContent = omsInterface.informUnitRoles(null, inputs.get("AgentID"));
						else
							resultContent = omsInterface.informUnitRoles(inputs.get("UnitID"), inputs.get("AgentID"));

						break;
					case 15:// quantity members service

						if (inputs.get("UnitID").trim().equals("null"))
							resultContent = omsInterface.informQuantityMembers(null, inputs.get("RoleID"), inputs.get("PositionID"), inputs.get("AgentID"));
						else
							resultContent = omsInterface.informQuantityMembers(inputs.get("UnitID"), inputs.get("RoleID"), inputs.get("PositionID"), inputs.get("AgentID"));

						break;
					case 16:// register norm service

						resultContent = omsInterface.registerNorm(inputs.get("UnitID"), inputs.get("NormContent"), inputs.get("AgentID"));
						break;
					case 17:// deregister norm service

						resultContent = omsInterface.deregisterNorm(inputs.get("NormName"), inputs.get("UnitID"), inputs.get("AgentID"));
						break;
					case 18:// inform norm service

						resultContent = omsInterface.informNorm(inputs.get("NormName"), inputs.get("UnitID"), inputs.get("AgentID"));
						break;
					case 19:// inform target norm service

						resultContent = omsInterface.informTargetNorms(inputs.get("TargetTypeName"), inputs.get("TargetValueName"), inputs.get("UnitID"), inputs.get("AgentID"));
						break;
						
					}
					
					
					// String serviceWSDLURL=omsServicesURLs.get(serviceName);
					// HashMap<String,Object>
					// result=st.executeWebService(serviceWSDLURL, inputs);

					responseParser.parseResponse(resultContent);

					// If acquire role is ok. If organization is virtual the
					// agent position is considered creator
					if (responseParser.getStatus().equals("Ok") && (responseParser.getServiceName().equals("AcquireRole") || responseParser.getServiceName().equals("AllocateRole")) && !organizationID.equals("virtual")) {
						// Gets position for the unit

						// < Accessibility - Visibility - Position >

						// -------------Inform Role-----------------


						//                        String content = omsInterface.informRole(rol, organizationID, myProcessor.getLastReceivedMessage().getSender().name);

						//                      responseParser.parseResponse(content);

						//                    positionType = responseParser.getElementsList().get(0);

						positionType = dbInterface.getInformRole(rol, organizationID).get(2);

						// positionType =
						// omsProxy.getAgentPosition(aidName,organizationID,
						// rol, unitType);

						// If position type is member then creates binding for
						// participant
						if (positionType.equals("member")) {
							createBinding(aidName, organizationID, "member");
						}// If position type is subordinate then creates binding
						// for subordinate
						else if (positionType.equals("subordinate")) {
							createBinding(aidName, organizationID, "subordinate");

						}// If position type is supervisor then creates binding
						// for supervisor
						else if (positionType.equals("supervisor")) {
							createBinding(aidName, organizationID, "supervisor");
						}// If not this one in any of the previous positions and
						// it is not creator either
						else if (!positionType.equals("creator")) {
							String message = l10n.getMessage(MessageID.INVALID_POSITION, positionType);
							throw new InvalidPositionException(message);
						}

					}

					// If leave role is ok. If organization is virtual the agent
					// position is considered creator
					if (responseParser.getStatus().equals("Ok") && (responseParser.getServiceName().equals("LeaveRole") || responseParser.getServiceName().equals("DeallocateRole")) && !organizationID.equals("virtual")) {

						String content = omsInterface.informAgentRole(aidName, aidName);

						responseParser.parseResponse(content);

						ArrayList<ArrayList<String>> agentsRole = responseParser.getItemsList();

						String unit_aux;
						String role_aux;
						boolean exists_in_unit = false;

						for (ArrayList<String> agentRole : agentsRole) {

							role_aux = agentRole.get(0);
							unit_aux = agentRole.get(1);

							// If agent is inside the organization and the rol
							// played is not creator
							if (unit_aux.equals(organizationID) && !role_aux.equals("creator")) {

								String contentRole = omsInterface.informRole(rol, organizationID, myProcessor.getLastReceivedMessage().getSender().name);
								responseParser.parseResponse(contentRole);

								String pos = responseParser.getElementsList().get(0);

								if (positionType.equals(pos))// ;omsProxy.getAgentPosition(aidName,organizationID,
									// role_aux,
									// unitType)))
									exists_in_unit = true;
							}
						}

						if (!exists_in_unit) {

							deleteBinding(aidName, organizationID, positionType);
						}

					}

					next = "INFORM";

					logger.info("[OMS]Before set message content...");

					myProcessor.getLastReceivedMessage().setContent(resultContent);

				} catch (Exception e) {
					StringTokenizer tokenInputParams = new StringTokenizer(myProcessor.getLastReceivedMessage().getContent(), separatorToken);
					String serviceURL = tokenInputParams.nextToken().trim();

					String resultXML = "<response>\n<serviceName>" + serviceURL + "</serviceName>\n";
					resultXML += "<status>Error</status>\n";
					resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
					resultXML += "</response>";

					myProcessor.getLastReceivedMessage().setContent(resultXML);

					// next = "FAILURE";
				}
				return next;
			}//

			@Override
			protected void doInform(CProcessor myProcessor, ACLMessage response) {
				ACLMessage lastReceivedMessage = myProcessor.getLastReceivedMessage();
				response.setContent(lastReceivedMessage.getContent());
			}

			@Override
			protected String doReceiveRequest(CProcessor myProcessor, ACLMessage request) {
				ACLMessage msg = request;
				String next = "";

				if (msg != null) {

					try {
						HashMap<String, String> inputs = new HashMap<String, String>();
						String serviceName = st.extractServiceContent(msg.getContent(), inputs);

						logger.info("[OMS]Service Name: " + serviceName);

						if (omsServicesURLs.containsKey(serviceName)) // if
							// (sfServicesURLs.containsKey(serviceName))
						{

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

				return next;
			}
		}

		CFactory talk = new myFIPA_REQUEST().newFactory("TALK", null, 1, firstProcessor.getMyAgent());

		// Finally the factory is setup to answer to incoming messages that
		// can start the participation of the agent in a new conversation
		this.addFactoryAsParticipant(talk);
	}
	
	@Override
	public void Shutdown() {
		ag.Shutdown();
		super.Shutdown();
	}
	
	@Override
	public void await() {
		ag.await();
		super.await();
	}
} // end OMS Agent
