package LoadLauncher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Level;
import org.apache.qpid.transport.DeliveryProperties;
import org.apache.qpid.transport.Header;
import org.apache.qpid.transport.MessageAcceptMode;
import org.apache.qpid.transport.MessageAcquireMode;
import org.apache.qpid.transport.MessageProperties;
import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
//import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.core.TracingService;

import es.upv.dsic.gti_ia.trace.TraceError;
import es.upv.dsic.gti_ia.trace.TracingEntity;
import es.upv.dsic.gti_ia.trace.TracingEntityList;
import es.upv.dsic.gti_ia.trace.TracingServiceList;
import es.upv.dsic.gti_ia.trace.TracingServiceSubscription;

/**
 * Trace Manager entity definition.
 * 
 * The trace manager entity is an agent in charge of coordinating and
 * managing the event trace process. Tracing entities have to interact with
 * the trace manager through ACL messages in order to publish/unpublish their
 * tracing service and in order to subscribe/unsubscribe from tracing services.
 * 
 * @author L Burdalo (lburdalo@dsic.upv.es)
 * 
 */
public class TraceManager extends BaseAgent{
	private String LOG_FILE_NAME;
	/**
	 * List of tracing entities present in the system.
	 * 
	 * @see es.upv.dsic.gti-ia.trace.TracingEntityList
	 */
	private TracingEntityList TracingEntities;
	/**
	 * List of tracing entities which provide a tracing service.
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingEntityList
	 */
	private TracingEntityList TSProviderEntities;
	/**
	 * List of tracing entities which are subscribed to some tracing service
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingEntityList
	 */
	private TracingEntityList TSSubscriberEntities;
	/**
	 * List of tracing services available in the system.
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceList
	 */
	private TracingServiceList TracingServices;
	
	/**
	 * Flag which determines if the trace manager has been launched in
	 * monitorization mode, which is necessary in order to subscribe to
	 * all available tracing services using 
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#requestAllTracingServices(BaseAgent requesterAgent)}
	 */
	private boolean monitorizable;
	
//	private ArrayList<Transmission> transmissions;
//	
//	private boolean finish = false;
	
	private Semaphore write_semaphore;
	
	/**
	 * Constructor which creates and initializes a TraceManager with the monitorization flag
	 * set to 'false'.<p>
	 * 
	 * Initialization tasks are internally performed by invoking the private
	 * method {@link es.upv.dsic.gti_ia.trace.TraceManager#initialize()}. These
	 * tasks are the following:<p>
	 * 
	 * 1) Creation of empty Tracing Entities, Tracing Service Providers,
	 * 		Tracing Service Subscribers and Tracing Services lists.<p>
	 * 2) Add the trace manager to the Tracing Entities List.<p>
	 * 3) Initialize the Tracing Services list with DI tracing services and add the trace
	 * 		manager as provider of those tracing services which are mandatory and requestable.<p>
	 * 4) Subscribe to NEW_AGENT and AGENT_DESTROYED tracing services in order to
	 * 		be able to register tracing entities in the system
	 * 
	 * @see es.upv.dsic.gti_ia.core.TracingService
	 * 
	 * @param aid	AgentID which will be used to create the agent
	 * 
	 * @throws Exception
	 */
	public TraceManager(AgentID aid, Load load_spec) throws Exception{
		super(aid);
		
		this.LOG_FILE_NAME = load_spec.getOutPath() + "/" +
			Load.prefixes[load_spec.getStrategy()] + "_" + this.getName() + "_result_log.txt";
		File LOG_FILE = new File(LOG_FILE_NAME);
		LOG_FILE.delete();
		LOG_FILE.createNewFile();
		this.monitorizable=false;
		
//		this.transmissions = new ArrayList<Transmission>();
		this.write_semaphore = new Semaphore(1, true);
		
        logger.info("[TRACE MANAGER (modified)]: " + this.getAid().toString() + " launched...");
        
        logger.setLevel(Level.OFF);
        
        initialize();
	}
	
	/**
	 * Constructor which creates and initializes a TraceManager.<p>
	 * 
	 * Initialization tasks are internally performed by invoking the private
	 * method {@link es.upv.dsic.gti_ia.trace.TraceManager#initialize()}. These
	 * tasks are the following:<p>
	 * 
	 * 1) Creation of empty Tracing Entities, Tracing Service Providers,
	 * 		Tracing Service Subscribers and Tracing Services lists.<p>
	 * 2) Add the trace manager to the Tracing Entities List.<p>
	 * 3) Initialize the Tracing Services list with DI tracing services and add the trace
	 * 		manager as provider of those tracing services which are mandatory and requestable.<p>
	 * 4) Subscribe to NEW_AGENT and AGENT_DESTROYED tracing services in order to
	 * 		be able to register tracing entities in the system
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TraceManager#initialize()
	 * 
	 * @param aid	AgentID which will be used to create the agent
	 * @param monitorizable	Value to which the monitorizable attribute of
	 * 		the class will be set
	 * 
	 * @throws Exception
	 */
	public TraceManager(AgentID aid, boolean monitorizable, Load load_spec) throws Exception{
		super(aid);
        
		this.LOG_FILE_NAME = load_spec.getOutPath() + "/" +
			Load.prefixes[load_spec.getStrategy()] + "_" + this.getName() + "_result_log.txt";
		File LOG_FILE = new File(LOG_FILE_NAME);
		LOG_FILE.delete();
		LOG_FILE.createNewFile();
		this.monitorizable=monitorizable;
		
//		this.transmissions = new ArrayList<Transmission>();
		this.write_semaphore = new Semaphore(1, true);
		
        if (monitorizable) {
        	logger.info("[TRACE MANAGER]: " + this.getAid().toString() + " launched with monitorization...");
        }
        else{
        	logger.info("[TRACE MANAGER]: " + this.getAid().toString() + " launched...");
        }
        
        logger.setLevel(Level.OFF);
        
        initialize();
	}
	
	/**
	 * Initializes the TraceManager.<p>
	 * 
	 * Initialization tasks are the following:<p>
	 * 
	 * 1) Creation of empty Tracing Entities, Tracing Service Providers,
	 * 		Tracing Service Subscribers and Tracing Services lists.<p>
	 * 2) Add the trace manager to the Tracing Entities List.<p>
	 * 3) Initialize the Tracing Services list with DI tracing services and add the trace
	 * 		manager as provider of those tracing services which are mandatory and requestable.<p>
	 * 4) Subscribe to NEW_AGENT and AGENT_DESTROYED tracing services in order to
	 * 		be able to register tracing entities in the system
	 */
	private void initialize (){
		Map<String, Object> arguments = new HashMap<String, Object>();
		TracingEntities = new TracingEntityList();
		TSProviderEntities = new TracingEntityList();
		TSSubscriberEntities = new TracingEntityList();
		TracingServices = new TracingServiceList();
		TracingEntity tEntity;
		TracingService tService;
		
		// Add Trace Manager to the tracing entities list
		tEntity=new TracingEntity(TracingEntity.AGENT, this.getAid());
		TracingEntities.add(tEntity);

		if (!TracingServices.initializeWithDITracingServices()){
			logger.error("[TRACE MANAGER]: Error while initializing the tracing service list");
		}
		
		// Add as provider of those tracing services which are mandatory and requestable
		for (int i=0; i < TracingService.MAX_DI_TS; i++){
			if (TracingService.DI_TracingServices[i].getRequestable()){
				tService=TracingServices.getTS(TracingService.DI_TracingServices[i].getName());
				synchronized(tEntity.getPublishedTS()){
					tEntity.getPublishedTS().add(tService);
				}
				synchronized(tService.getProviders()){
					tService.getProviders().add(tEntity);
				}
			}
		}
		
		// In order to register tracing entities, the trace manager has to subscribe
		// to certain tracing services: NEW_AGENT, AGENT_DESTROYED, NEW_ARTIFACT and NEW_AGGREGATION
		// Subscriptions to these tracing services cannot be removed; so, it is not necessary to
		// store them.
		// TODO: ARTIFACTS and AGGREGATIONS are not supported yet
		arguments.put("x-match", "all");
    	arguments.put("tracing_service", TracingService.DI_TracingServices[TracingService.NEW_AGENT].getName());
    	this.traceSession.exchangeBind(this.getAid().name+".trace", "amq.match", TracingService.DI_TracingServices[TracingService.NEW_AGENT].getName() + "#any", arguments);
    	arguments.clear();
    	
    	arguments.put("x-match", "all");
    	arguments.put("tracing_service", TracingService.DI_TracingServices[TracingService.AGENT_DESTROYED].getName());
    	this.traceSession.exchangeBind(this.getAid().name+".trace", "amq.match", TracingService.DI_TracingServices[TracingService.AGENT_DESTROYED].getName() + "#any", arguments);
    	arguments.clear();
    	
//    	arguments.put("x-match", "all");
//    	arguments.put("tracing_service", TracingService.DI_TracingServices[TracingService.NEW_ARTIFACT].getName());
//    	this.traceSession.exchangeBind(this.getAid().name+".trace", "amq.match", TracingService.DI_TracingServices[TracingService.NEW_ARTIFACT].getName() + "#any", arguments);
//    	arguments.clear();
//    	
//    	arguments.put("x-match", "all");
//    	arguments.put("tracing_service", TracingService.DI_TracingServices[TracingService.NEW_AGGREGATION].getName());
//    	this.traceSession.exchangeBind(this.getAid().name+".trace", "amq.match", TracingService.DI_TracingServices[TracingService.NEW_AGGREGATION].getName() + "#any", arguments);
//    	arguments.clear();
	}
	
	/**
	 * Sends a system trace event to the amq.match exchange
	 * 
	 * @param tEvent	Trace event to be sent
	 * 
	 * @param destination	Tracing entity to which the trace event is directed to.
	 * 		If set to null, the system trace event is understood to be directed to all tracing
	 * 		entities.        
	 */
	private void sendSystemTraceEvent(TraceEvent tEvent, TracingEntity destination) {
		MessageTransfer xfr = new MessageTransfer();

		xfr.destination("amq.match");
		xfr.acceptMode(MessageAcceptMode.EXPLICIT);
		xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);
		
		DeliveryProperties deliveryProps = new DeliveryProperties();
		
		// Serialize message content
		String body;
		// Timestamp
		body = String.valueOf(tEvent.getTimestamp()) + "#";
		// EventType
		body = body + tEvent.getTracingService().length() + "#"
				+ tEvent.getTracingService();
		// OriginEntiy
		body = body + tEvent.getOriginEntity().getType() + "#";
		body = body + tEvent.getOriginEntity().getAid().toString().length() + "#" + tEvent.getOriginEntity().getAid().toString();
		// Content
		body = body + tEvent.getContent().length() + "#" + tEvent.getContent();
		
		xfr.setBody(body);

		// set message headers
    	MessageProperties messageProperties = new MessageProperties();
    	Map<String, Object> messageHeaders = new HashMap<String, Object>();
    	// set the message property
    	messageHeaders.put("tracing_service", tEvent.getTracingService());
    	messageHeaders.put("origin_entity", "system");
    	if (destination == null){
    		messageHeaders.put("receiver", "all");
    	}
    	else if (destination.getType() == TracingEntity.AGENT){
    		messageHeaders.put("receiver", destination.getAid().name);
    	}
		
    	messageProperties.setApplicationHeaders(messageHeaders);
    	
    	Header header = new Header(deliveryProps, messageProperties);
    	
    	this.traceSession.messageTransfer("amq.match", MessageAcceptMode.EXPLICIT, MessageAcquireMode.PRE_ACQUIRED,
                header, xfr.getBodyString());
	}
	
//	public void execute() {
//		while(true){}
//	}
	
	/**
	 * Requests to the trace manager are sent via ACL messages which are processed
	 * in this method 
	 *
	 * @param msg Message received
	 */
	protected void onMessage(ACLMessage msg) {
		String content, serviceName, serviceDescription, originEntity;
		Map<String, Object> arguments;
		int index, index2, length;
		TraceEvent tEvent; // = new TraceEvent();
		ACLMessage response_msg = null;
		String command;
		
		TracingService tService=null;
		TracingEntity tEntity=null, originTEntity=null;
		TracingServiceSubscription tServiceSubscription=null;
		
		AgentID originAid;//, requestedAid;
		int aidindice1 = 0;
		int aidindice2 = 0;
		
		String tEventContent;
		boolean agree_response=true;
		boolean added_TS=false;
		boolean added_TSP=false;
		boolean linked_TE_TS=false;
		boolean added_TSS=false;
		boolean error;
		
		logger.info("[TRACE MANAGER]: Received [" + msg.getPerformativeInt() + "] -> " + msg.getContent());
		String auxString = Long.toString(System.currentTimeMillis()) + "\t";
		int index1=msg.getContent().indexOf('#');
		
		auxString = auxString + msg.getContent().substring(0, index1) + "\t" +
			msg.getSender().toString() + "\t" +
			this.getAid().toString()  + "\t" +
			String.valueOf(Transmission.SYSTEM) + "\t" + msg.getContent().substring(index1+1) + "\n";
				
		try {
			write_semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
				
		try {
			BufferedWriter log_file = new BufferedWriter(new FileWriter(LOG_FILE_NAME, true));
			log_file.write(auxString);
			log_file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		write_semaphore.release();
		
//		System.out.println(this.getAid() + " Message " + msg.toString());
		
		msg.setContent(msg.getContent().substring(index1+1));
		
		switch (msg.getPerformativeInt()){
				
			case ACLMessage.REQUEST:
					
				content = msg.getContent();
					
				index = content.indexOf('#', 0);
				command = content.substring(0, index);
				
				if (command.equals("publish")) {
					// Publication of a tracing service
					index2 = content.indexOf('#', index+1);
					length = Integer.parseInt(content.substring(index + 1, index2));
					serviceName=content.substring(index2 + 1, index2 + 1 + length);
					index = index2 + length + 1;
					serviceDescription=content.substring(index);

					if ((tEntity=TracingEntities.getTEByAid(msg.getSender())) == null){
						// Error getting the tracing entity
						agree_response=false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent(System.currentTimeMillis() + "#" + "publish#" + serviceName.length() + "#" + serviceName + TraceError.ENTITY_NOT_FOUND);
						logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
					}
					else if (!TSProviderEntities.contains(tEntity)){
						// Register tracing entity as service provider
						synchronized(TSProviderEntities){
							error=TSProviderEntities.add(tEntity);
						}
						if (error){
							added_TSP=true;
						}
						else{
							// Error adding the tracing entity
							agree_response=false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("publish#" + serviceName.length() + "#" + serviceName + TraceError.BAD_ENTITY);
							logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
						}
					}
						
					// Add tracing service
					if (agree_response && ((tService=TracingServices.getTS(serviceName)) == null)){
						// The tracing service does not exist
						tService = new TracingService(serviceName, serviceDescription);
						synchronized(TracingServices){
							error=TracingServices.add(tService);
						}
						if (error){
							added_TS=true;
						}
						else{
							// Impossible to add tracing service
							agree_response=false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("publish#" + serviceName.length() + "#" + serviceName + TraceError.BAD_SERVICE);
							logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
						}
					}
						
					// Link service provider and tracing service
					if (agree_response){
						if (tService.getProviders().contains(tEntity) ||
							tEntity.getPublishedTS().contains(tService)){
							// Tracing service already published by the tracing entity
							agree_response=false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent(System.currentTimeMillis() + "#" + "publish#" + serviceName.length() + "#" + serviceName + TraceError.SERVICE_DUPLICATE);
							logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
						}
						else{
							synchronized(tService.getProviders()){
								error=tService.getProviders().add(tEntity);
							}
							if (error){
								synchronized(tEntity.getPublishedTS()){
									error=tEntity.getPublishedTS().add(tService);
								}
							}
							if (!error){
								// Impossible to link properly tracing service and provider
								agree_response=false;
								response_msg = new ACLMessage(ACLMessage.REFUSE);
								response_msg.setSender(this.getAid());
								response_msg.setReceiver(msg.getSender());
								response_msg.setContent("publish#" + serviceName.length() + "#" + serviceName + TraceError.SUBSCRIPTION_ERROR);
								logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
							}
							else{
								linked_TE_TS=true;
							}
						}
					}
						
					if (agree_response){
						tEvent=new TraceEvent(TracingService.DI_TracingServices[TracingService.PUBLISHED_TRACING_SERVICE].getName(),
								tEntity, serviceName);
						sendTraceEvent(tEvent);
						//sendSystemTraceEvent(tEvent, tEntity);
					
						response_msg = new ACLMessage(ACLMessage.AGREE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent(System.currentTimeMillis() + "#" + "publish#" + serviceName);
						logger.info("[TRACE MANAGER]: Sending AGREE message to " + msg.getReceiver().toString());
					}
					else{
						if (linked_TE_TS){
							synchronized(tService.getProviders()){
								tService.getProviders().remove(tEntity);
							}
							synchronized(tEntity.getPublishedTS()){
								tEntity.getPublishedTS().remove(tService);
							}
						}
						if (added_TS){
							synchronized(TracingServices){
								TracingServices.remove(tService);
							}
						}
						if (added_TSP) {
							synchronized(TSProviderEntities){
								TSProviderEntities.remove(tEntity);
							}
						}
					}
				}
				else {
					/**
					 * Building a ACLMessage
					 */
					response_msg = new ACLMessage(ACLMessage.UNKNOWN);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent(content);
					logger.info("[TRACE MANAGER]: Returning UNKNOWN message to " + msg.getReceiver().toString());
				}
			
				send(response_msg);
				
				break;
				
			case ACLMessage.SUBSCRIBE:
				// Subscription to tracing services
				arguments = new HashMap<String, Object>();
							
				content = msg.getContent();
				
				index = content.indexOf('#', 0);
				serviceName = content.substring(0, index);
				originEntity = content.substring(index + 1);
				
				if (!originEntity.contentEquals("any")){
					originAid = new AgentID();
					aidindice1 = 0;
					aidindice2 = originEntity.indexOf(':');
					if (aidindice2 - aidindice1 <= 0)
						originAid.protocol = "";
					else
						originAid.protocol = originEntity.substring(aidindice1, aidindice2);
					aidindice1 = aidindice2 + 3;
					aidindice2 = originEntity.indexOf('@', aidindice1);
					if (aidindice2 - aidindice1 <= 0)
						originAid.name = "";
					else
						originAid.name = originEntity.substring(aidindice1, aidindice2);
					aidindice1 = aidindice2 + 1;
					aidindice2 = originEntity.indexOf(':', aidindice1);
					if (aidindice2 - aidindice1 <= 0)
						originAid.host = "";
					else
						originAid.host = originEntity.substring(aidindice1, aidindice2);
					originAid.port = originEntity.substring(aidindice2 + 1);
				}
				else{
					originAid=null;
					originTEntity=null;
				}
			
				if ((tEntity=TracingEntities.getTEByAid(msg.getSender())) == null){
					// Tracing entity not found
//							System.out.println("NOT FOUND " + msg.getSender().toString());
					agree_response=false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent(System.currentTimeMillis() + "#" + "subscribe#" + serviceName.length() + "#" +
						serviceName + originEntity.length() + "#" + originEntity + TraceError.ENTITY_NOT_FOUND);
					logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
				}
				else if ((tService=TracingServices.getTS(serviceName)) == null){
					// The tracing service does not exist
					agree_response=false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent(System.currentTimeMillis() + "#" + "subscribe#" + serviceName.length() + "#" +
						serviceName + originEntity.length() + "#" + originEntity + TraceError.SERVICE_NOT_FOUND);
					logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
				}
				else if (!tService.getRequestable()){
					// The tracing service is not requestable
					agree_response=false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent(System.currentTimeMillis() + "#" + "subscribe#" + serviceName.length() + "#" +
						serviceName + originEntity.length() + "#" + originEntity + TraceError.BAD_SERVICE);
					logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
				}
				else if ((originAid != null) &&
						((originTEntity=tService.getProviders().getTEByAid(originAid)) == null)){
					// Tracing service not published by the origin tracing entity
					agree_response=false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent(System.currentTimeMillis() + "#" + "subscribe#" + serviceName.length() + "#" +
						serviceName + originEntity.length() + "#" + originEntity + TraceError.SERVICE_NOT_FOUND);
					logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
				}
				// Check if the subscription already exists
				else if (TSSubscriberEntities.contains(tEntity) &&
					(tEntity.getSubscribedToTS().getTSS(tEntity, originTEntity, tService) != null)){
					// The subscription already exists
					agree_response=false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent(System.currentTimeMillis() + "#" + "subscribe#" + serviceName.length() + "#" +
							serviceName + originEntity.length() + "#" + originEntity + TraceError.SUBSCRIPTION_DUPLICATE);
					logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
				}
				else if (!TSSubscriberEntities.contains(tEntity)){
					// Register tracing entity as subscriber
					synchronized(TSSubscriberEntities){
						error=TSSubscriberEntities.add(tEntity);
					}
					if (error){
						added_TSS=true;
					}
					else{
						// Error adding the tracing entity
						agree_response=false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("subscribe#" + serviceName.length() + "#" +
								serviceName + originEntity.length() + "#" + originEntity + TraceError.SUBSCRIPTION_ERROR);
						logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
					}
				}
				if (agree_response){
					// Add subscription
					tServiceSubscription=new TracingServiceSubscription(tEntity, originTEntity, tService);
					synchronized(tService.getSubscriptions()){
						tService.addSubscription(tServiceSubscription);
					}
					synchronized(tEntity.getSubscribedToTS()){
						tEntity.addSubscription(tServiceSubscription);
					}
					arguments.put("x-match", "all");
					arguments.put("tracing_service", serviceName);
				    	
					if (!originEntity.contentEquals("any")) {
						arguments.put("origin_entity", originEntity);
					}
				    	
					this.traceSession.exchangeBind(msg.getSender().name+".trace", "amq.match", serviceName + "#" + originEntity, arguments);
				    	
					// Send system trace event
					//tEntity=new TracingEntity(TracingEntity.AGENT,
					//		new AgentID("system", this.getAid().protocol, this.getAid().host, this.getAid().port));
						
					//tEventContent=tService.getName() + "#" + originEntity;
					tEventContent=serviceName + "#" + tService.getDescription().length() + "#" +
						tService.getDescription() + "#" + originEntity;
						
					tEvent=new TraceEvent(TracingService.DI_TracingServices[TracingService.SUBSCRIBE].getName(),
						tEntity, tEventContent);
//							sendSystemTraceEvent(tEvent, tServiceSubscription.getSubscriptorEntity());
				    	
					/**
					 * Building a ACLMessage
					 */
					response_msg = new ACLMessage(ACLMessage.AGREE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent(System.currentTimeMillis() + "#" + "subscribe#" + serviceName.length() + "#" + serviceName + "#" + originEntity);
					//logger.info("[TRACE MANAGER]: sending message to " + msg.getReceiver().toString());
				}else{
					if (added_TSS){
						synchronized(TSSubscriberEntities){
							TSSubscriberEntities.remove(tEntity);
						}
					}
				}
				
				/**
				 * Sending a ACLMessage
				 */
				send(response_msg);
				
				break;
				
			default:
				/**
				 * Building a ACLMessage
				 */
				response_msg = new ACLMessage(ACLMessage.UNKNOWN);
				response_msg.setSender(this.getAid());
				response_msg.setReceiver(msg.getSender());
				response_msg.setContent(msg.getContent());
				logger.info("Mensaje received in " + this.getName()
						+ " agent, by onMessage: " + msg.getContent());
				logger.info("[TRACE MANAGER]: returning UNKNOWN message to " + msg.getReceiver().toString());
				send(response_msg);
		}
	}
	
	public void onTraceEvent(TraceEvent tEvent)
	{
		TracingEntity tEntity;
		TracingService tService;
		boolean error;

		if (tEvent.getTracingService().contentEquals(TracingService.DI_TracingServices[TracingService.NEW_AGENT].getName()) == true){
			// Register tracing entity
			tEntity=new TracingEntity(TracingEntity.AGENT, new AgentID(tEvent.getContent()));
			synchronized(TracingEntities){
				error=TracingEntities.add(tEntity);
			}
			if (error){
				for (int i=0; i < TracingService.MAX_DI_TS; i++){
					if (TracingService.DI_TracingServices[i].getRequestable()){
						tService=TracingServices.getTS(TracingService.DI_TracingServices[i].getName());
						synchronized(tEntity.getPublishedTS()){
							tEntity.getPublishedTS().add(tService);
						}
						synchronized(tService.getProviders()){
							tService.getProviders().add(tEntity);
						}
					}
				}
			}
		}
	}
}
