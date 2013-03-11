package mWaterWeb.webInterface;

import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


import mWaterWeb.webInterface.WebComm.AccreditationInJSONObject;
import mWaterWeb.webInterface.WebComm.AccreditationOutJSONObject;
import mWaterWeb.webInterface.WebComm.AuctionInJSONObject;
import mWaterWeb.webInterface.WebComm.BidUpInJSONObject;
import mWaterWeb.webInterface.WebComm.FinishedRoundInJSONObject;
import mWaterWeb.webInterface.WebComm.GetWRInJSONObject;
import mWaterWeb.webInterface.WebComm.InJsonObject;
import mWaterWeb.webInterface.WebComm.JoinTableInJSONObject;
import mWaterWeb.webInterface.WebComm.NewTableInJSONObject;
import mWaterWeb.webInterface.WebComm.RoundStartedInJSONObject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;

import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;


//
// Participant to Initiator Web Request (simplified FIPA_REQUEST)
//
public class webCommParticipantTemplate {
	protected TransitionSystem Ts; 
	private static String myProtocol = "web";

	public webCommParticipantTemplate(TransitionSystem ts) {
		//this.maxAgents = maxAgents;
		Ts = ts;
	}

	/**
	 * Method executed at the beginning of the conversation
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg first message assigned to this conversation
	 */
	protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
	}

	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin(myProcessor, msg);
			return "WAIT";
		};
	}

	/**
	 * Method executed when the initiator receives the request
	 * @param myProcessor the CProcessor managing the conversation
	 * @param request request message
	 * @return
	 */
	protected String doReceiveRequest(ConvCProcessor myProcessor,
			ACLMessage request) { 
		
		Conversation conv = myProcessor.getConversation();
		String jasonConvID = conv.internalConvID;
		//At this point the conversation associated to the CProcessor has no value
		//for the internal conversation ID so it is updated
		conv.jasonConvID = jasonConvID;
		conv.initiator = request.getSender();
		String factName = conv.factoryName;
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
		
		//xstream.alias("jsonObject", InJsonObject.class);

		//Ts.getLogger().info("Antes de leer objeto.... "+request.getContent());
		String purpose = ""; 
		StringTokenizer str = new StringTokenizer(request.getContent(),",");
		StringTokenizer auxstr1; boolean found = false;
		while (str.hasMoreElements()&&(!found))
		{
			auxstr1 = new StringTokenizer(str.nextToken(), ":") ;//example: ["purpose","tradinghall"]
			if (auxstr1.nextToken().compareTo('"'+"purpose"+'"')==0)
				{
					purpose = auxstr1.nextToken();
					purpose = purpose.replace("\"","") ;
					found = true;
				}
		}
		
	
		//{"jsonObject":{"agent_name":"AGarrido","purpose":"tradinghall","content":{"userName":"AGarrido","wmarket":"1014","rol":"buyer","table_id":"2"}}}
		//InJsonObject inJsonObj = (InJsonObject) xstream.fromXML(request.getContent());
		//System.out.println("Antes de leer objeto.... "+purpose);


		WebRequestConversation newConv = new WebRequestConversation(conv.jasonConvID, conv.internalConvID, conv.initiator,purpose,factName);
		List<Literal> allperc = new ArrayList<Literal>();
		String percept="";
		
		if (purpose.compareTo("finishedround")==0){
			xstream.alias("jsonObject", FinishedRoundInJSONObject.class);
			newConv.conversationRequest = (FinishedRoundInJSONObject) xstream.fromXML(request.getContent());
			String content = ((FinishedRoundInJSONObject)newConv.conversationRequest).content;;

			//Add perception in order to start the right conversation
			percept = "finishedround("+'"'+newConv.jasonConvID+'"'+")[source(self)]";
	  
		}
		
		if (purpose.compareTo("roundstarted")==0){
			xstream.alias("jsonObject", RoundStartedInJSONObject.class);
			newConv.conversationRequest = (RoundStartedInJSONObject) xstream.fromXML(request.getContent());
			String userName = ((RoundStartedInJSONObject)newConv.conversationRequest).userName;

			//Add perception in order to start the right conversation
			percept = "roundstarted("+'"'+userName+'"'+","+'"'+newConv.jasonConvID+'"'+")[source(self)]";
	  
		}
		
		if (purpose.compareTo("WRList")==0){
			xstream.alias("jsonObject", GetWRInJSONObject.class);
			newConv.conversationRequest = (GetWRInJSONObject) xstream.fromXML(request.getContent());
			String usrName = ((GetWRInJSONObject)newConv.conversationRequest).content.userName;
			String wmarket = ((GetWRInJSONObject)newConv.conversationRequest).content.wmarket;
			String rol = ((GetWRInJSONObject)newConv.conversationRequest).content.rol;
			String w_right_id = ((GetWRInJSONObject)newConv.conversationRequest).content.wrightid;
			
			//Add perception in order to start the right conversation
			
			if ((wmarket.compareTo("")!=0)&&(w_right_id.compareTo("")!=0))
				{	percept = "request("+'"'+purpose+'"'+","+'"'+usrName+'"'+","+wmarket+","+'"'+rol+'"'+","+w_right_id+","+'"'+newConv.jasonConvID+'"'+")[source(self)]";
				}
			else {
				if ((wmarket.compareTo("")==0)&&(w_right_id.compareTo("")!=0))
					percept = "request("+'"'+purpose+'"'+","+'"'+usrName+'"'+","+'"'+'"'+","+'"'+rol+'"'+","+w_right_id+","+'"'+newConv.jasonConvID+'"'+")[source(self)]";
				if ((wmarket.compareTo("")!=0)&&(w_right_id.compareTo("")==0))
					percept = "request("+'"'+purpose+'"'+","+'"'+usrName+'"'+","+wmarket+","+'"'+rol+'"'+","+'"'+'"'+","+'"'+newConv.jasonConvID+'"'+")[source(self)]";
				if ((wmarket.compareTo("")==0)&&(w_right_id.compareTo("")==0))
					percept = "request("+'"'+purpose+'"'+","+'"'+usrName+'"'+","+'"'+'"'+","+'"'+rol+'"'+","+'"'+'"'+","+'"'+newConv.jasonConvID+'"'+")[source(self)]";
			}   
		}
		if (purpose.compareTo("accreditation")==0){
			xstream.alias("jsonObject", AccreditationInJSONObject.class);
			newConv.conversationRequest = (AccreditationInJSONObject) xstream.fromXML(request.getContent());
			String usrName = ((AccreditationInJSONObject)newConv.conversationRequest).content.userName;
			String wmarket = ((AccreditationInJSONObject)newConv.conversationRequest).content.wmarket;
			String rol = ((AccreditationInJSONObject)newConv.conversationRequest).content.rol;
			String w_right_id = ((AccreditationInJSONObject)newConv.conversationRequest).content.wrightid;
			String date = ((AccreditationInJSONObject)newConv.conversationRequest).content.date;
			//Add perception in order to start the right conversation
			/*if (wmarket.compareTo("")!=0)
				percept = "request("+'"'+purpose+'"'+","+'"'+usrName+'"'+","+wmarket+","+'"'+rol+'"'+","+w_right_id+","+'"'+newConv.jasonConvID+'"'+")[source(self)]";
			else percept = "request("+'"'+purpose+'"'+","+'"'+usrName+'"'+","+'"'+'"'+","+'"'+rol+'"'+","+w_right_id+","+'"'+newConv.jasonConvID+'"'+")[source(self)]";*/

			if ((wmarket.compareTo("")!=0)&&(w_right_id.compareTo("")!=0))
			{	percept = "request("+'"'+purpose+'"'+","+'"'+usrName+'"'+","+wmarket+","+'"'+rol+'"'+","+w_right_id+","+date+","+'"'+newConv.jasonConvID+'"'+")[source(self)]";
			}
		else {
			if ((wmarket.compareTo("")==0)&&(w_right_id.compareTo("")!=0))
				percept = "request("+'"'+purpose+'"'+","+'"'+usrName+'"'+","+'"'+'"'+","+'"'+rol+'"'+","+w_right_id+","+date+","+'"'+newConv.jasonConvID+'"'+")[source(self)]";
			if ((wmarket.compareTo("")!=0)&&(w_right_id.compareTo("")==0))
				percept = "request("+'"'+purpose+'"'+","+'"'+usrName+'"'+","+wmarket+","+'"'+rol+'"'+","+'"'+'"'+","+date+","+'"'+newConv.jasonConvID+'"'+")[source(self)]";
			if ((wmarket.compareTo("")==0)&&(w_right_id.compareTo("")==0))
				percept = "request("+'"'+purpose+'"'+","+'"'+usrName+'"'+","+'"'+'"'+","+'"'+rol+'"'+","+'"'+'"'+","+date+","+'"'+newConv.jasonConvID+'"'+")[source(self)]";
		}  
		
		}
		if ((purpose.compareTo("tradinghall")==0)||(purpose.compareTo("tradinghallstate")==0)){
			xstream.alias("jsonObject", JoinTableInJSONObject.class);
			newConv.conversationRequest = (JoinTableInJSONObject) xstream.fromXML(request.getContent());
			String usrName = ((JoinTableInJSONObject)newConv.conversationRequest).content.userName;
			String wmarket = ((JoinTableInJSONObject)newConv.conversationRequest).content.wmarket;
			String tableid = ((JoinTableInJSONObject)newConv.conversationRequest).content.table_id;
			//String rol = ((JoinTableInJSONObject)newConv.conversationRequest).content.rol;
			//Add perception in order to start the right conversation
			
			percept = "request("+'"'+purpose+'"'+","+'"'+usrName+'"'+","+wmarket+","+tableid+","+'"'+newConv.jasonConvID+'"'+")[source(self)]";
			//System.out.println(percept);
		}
		if (purpose.compareTo("newtable")==0){
			xstream.alias("jsonObject", NewTableInJSONObject.class);
			newConv.conversationRequest = (NewTableInJSONObject) xstream.fromXML(request.getContent());
			String wmarket = ((NewTableInJSONObject)newConv.conversationRequest).content.wmarket;
			String th_id = ((NewTableInJSONObject)newConv.conversationRequest).content.th_id;
			String rolwopening = ((NewTableInJSONObject)newConv.conversationRequest).content.rol_when_opening;
			String prottype = ((NewTableInJSONObject)newConv.conversationRequest).content.protocol_type_id;
			int partic = ((NewTableInJSONObject)newConv.conversationRequest).content.participants;
			String[] wrIds = ((NewTableInJSONObject)newConv.conversationRequest).content.water_rights_ids;
			String wrIdsList = "" ; String sep = "[";
			for (int i=0; i<wrIds.length;i++)
				{wrIdsList = wrIdsList+sep+wrIds[i]; sep = ",";}
			wrIdsList = wrIdsList+"]";
			//+request("newtable",AttributesList,WaterRightIDs,Rol,Participants,Protocol,MWaterMarket,THall)
			percept = "request("+'"'+purpose+'"'+","+wrIdsList+","+	'"'+rolwopening+'"'+","+
				partic+","+	prottype+","+	wmarket+","+th_id+","+'"'+newConv.jasonConvID+'"'+")[source(self)]";
		}
		if (purpose.compareTo("auctionstate")==0){
			xstream.alias("jsonObject", AuctionInJSONObject.class);
			newConv.conversationRequest = (AuctionInJSONObject) xstream.fromXML(request.getContent());
			String protocol_id = ((AuctionInJSONObject)newConv.conversationRequest).content.protocol_id;
			String conv_id = ((AuctionInJSONObject)newConv.conversationRequest).content.conversation_id;
			String username = ((AuctionInJSONObject)newConv.conversationRequest).content.userName;
			String table_id = ((AuctionInJSONObject)newConv.conversationRequest).content.table_id;
			String wmarket = ((AuctionInJSONObject)newConv.conversationRequest).content.wmarket;
			String water_right = ((AuctionInJSONObject)newConv.conversationRequest).content.water_right;
			

			percept = "request("+'"'+purpose+'"'+","+water_right+","+table_id+","+wmarket+","+
			protocol_id+","+'"'+username+'"'+","+'"'+conv_id+'"'+","+'"'+newConv.jasonConvID+'"'+")[source(self)]";
		}
		//request("bidup",GoOn,WRID,ConvID)  
		if (purpose.compareTo("bidup")==0){
			xstream.alias("jsonObject", BidUpInJSONObject.class);
			newConv.conversationRequest = (BidUpInJSONObject) xstream.fromXML(request.getContent());
			String conv_id = ((BidUpInJSONObject)newConv.conversationRequest).content.conversation_id;
			boolean GoOn = ((BidUpInJSONObject)newConv.conversationRequest).content.accepted;
			String water_right = ((BidUpInJSONObject)newConv.conversationRequest).content.water_right_id;
			
			percept = "request("+'"'+purpose+'"'+","+GoOn+","+water_right+","+"\""+conv_id+"\""+","+'"'+newConv.jasonConvID+'"'+")[source(self)]";
			//System.out.println("Percepcion añadida... "+percept);
		}
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);

		((ConvCFactory)myProcessor.getMyFactory()).UpdateConv(newConv, myProcessor);

		newConv.aquire_semaphore();//it will be released when the corresponding conversation finishes

		return "INFORM";
		
	}

	class RECEIVE_REQUEST_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			return doReceiveRequest((ConvCProcessor) myProcessor, messageReceived);
		}
	}

	
	/**
	 * Perform the requested action
	 * @param myProcessor the CProcessor managing the conversation
	 * @return next conversation state
	 */
/*	protected String doAction(ConvCProcessor myProcessor) {

		ACLMessage msg = new ACLMessage();
		
		System.out.println(DataAndStruct.TAB + "WEB - Procesando");
		
		// Get the suitable RQ template
		if (inJsonObject.content.oneWayRound.equals((String) "oneWay")) {
				
			trip.tType = TripType.Oneway;
			
			// Unmarshal template RQ OneWay trip file
			objRqMsgContent = marUnMarDpkg.doUnMarshalDynPkgRqXML(otaFilePath[0]);
		}
		else {
			
			trip.tType = TripType.Roundtrip;
			
			// Unmarshal template RQ OneWay trip file
			objRqMsgContent = marUnMarDpkg.doUnMarshalDynPkgRqXML(otaFilePath[1]);
		}
				
		// Get preferences and data from resource file
		resourceBundle = ResourceBundle.getBundle(resourceFile);
		trip.getDataFromResourceAndSet(myProcessor, resourceBundle);
		// Generate weights and sort them
		trip.randomWeightsAndSort(trip);
		
		trip.departAirport = inJsonObject.content.cityFrom;
		trip.arrivAirport = inJsonObject.content.cityTo;
		trip.startDay = new DateTime(inJsonObject.content.dateFrom);
		trip.endDay = new DateTime(inJsonObject.content.dateTo);
		trip.stayDays = Days.daysBetween(trip.startDay, trip.endDay).getDays();
		trip.personNumber = inJsonObject.content.personNumber;
		trip.webMinPrice = inJsonObject.content.minPrice;
		trip.webMaxPrice = inJsonObject.content.maxPrice;
		
		// Elementary logic
		if (inJsonObject.content.catering && inJsonObject.content.longSeat &&
				inJsonObject.content.wifiFlight) {
			
			trip.flightClass = TripClass.First;
		}
		
		else {
			
			if (inJsonObject.content.catering || inJsonObject.content.longSeat ||
					inJsonObject.content.wifiFlight) {
				
				trip.flightClass = TripClass.Business;
			}
			
			else {
				
				trip.flightClass = TripClass.Cabin;
			}
		}
		
		if (inJsonObject.content.fullBoard && inJsonObject.content.wifiRooms &&
		inJsonObject.content.spa && inJsonObject.content.payTV) {
			
			trip.hotelClass = HotelClass.Luxury;			
		}
		
		else {
			
			if (inJsonObject.content.fullBoard || inJsonObject.content.wifiRooms ||
					inJsonObject.content.spa || inJsonObject.content.payTV) {
				
				trip.hotelClass = HotelClass.FirstClass;
			}
			
			else {
				
				trip.hotelClass = HotelClass.Standard;
			}
		}
		
		if (inJsonObject.content.bid)
			
			trip.conConcesion = 1;
		
		else 
			
			trip.conConcesion = 0;
		
		
		if (inJsonObject.content.strategy.equals((String) "None")) {
			
			trip.betaParam = 0.5; // Relaxed strategy
		}
		
		else {
			
			
			if (inJsonObject.content.strategy.equals((String) "Medium")) {
				
				trip.betaParam = 1; // Medium strategy
			}
			
			else {
				
				trip.betaParam = 1.5; // Aggressive strategy
			}
		}
		
		// Put preferences of the travel in the trip object	
		trip.setTrip2ObjRQ(trip, objRqMsgContent);
		
		trip.xmlConcesContent = marUnMarDpkg.doMarshalDynPkgRqXML(objRqMsgContent).toString();
		
		// Load preferences of the trip
		for (Map.Entry<String, TripAndPrefs> entry : mapTrip.entrySet()) {
			
			mapTrip.put(entry.getKey(), trip.clone(trip));

		}
		// Choose participant agents
		/*for (int i = 0; i <= 4; i++) {
			
			if (i <= 2) {
				
				// Airlines chosen
				participantAgents[i] = new AgentID(sIndustryAgents[i]);
			}
			
			else {
			
				// Hotels chosen
				participantAgents[i] = new AgentID(sIndustryAgents[i+5]);
			}
			
		}*/
		
		// Comment below and uncomment in CustomerDynPackageSearch.execution 
		// for working without web (and vice versa)
		/*CFactory cust2IndTalk0 = new OfferCounterOffer_Initiator(
				industryAgents[0],
				DataAndStruct.ETM_OFFER_COUNTEROFFER_PROTOCOL).
				newFactory("CustInd_TALK0", null, msg, conversationsLimit, myProcessor.getMyAgent(), 
				2, deadline, DataAndStruct.GLOBALTIMEOUT);

		///////////////////////////////////////////////////////////////////////////////
		// The template processor is ready. We add the factory, in this case as 
		// a initiator one
		///////////////////////////////////////////////////////////////////////////////
		//this.addFactoryAsInitiator(cust2IndTalk);
		myProcessor.getMyAgent().addFactoryAsInitiator(cust2IndTalk0);

		// Finally Customer starts the conversation.
		//this.startSyncConversation("CustInd_TALK");
		myProcessor.getMyAgent().startSyncConversation("CustInd_TALK0");

		
		// Comment below and uncomment in CustomerDynPackageSearch.execution 
		// for working without web (and vice versa)
		CFactory cust2IndTalk1 = new OfferCounterOffer_Initiator(
				industryAgents[1],
				DataAndStruct.ETM_OFFER_COUNTEROFFER_PROTOCOL).
				newFactory("CustInd_TALK1", null, msg, conversationsLimit, myProcessor.getMyAgent(), 
				2, deadline, DataAndStruct.GLOBALTIMEOUT);

		///////////////////////////////////////////////////////////////////////////////
		// The template processor is ready. We add the factory, in this case as 
		// a initiator one
		///////////////////////////////////////////////////////////////////////////////
		//this.addFactoryAsInitiator(cust2IndTalk);
		myProcessor.getMyAgent().addFactoryAsInitiator(cust2IndTalk1);

		// Finally Customer starts the conversation.
		//this.startSyncConversation("CustInd_TALK");
		myProcessor.getMyAgent().startSyncConversation("CustInd_TALK1");

		
		// Comment below and uncomment in CustomerDynPackageSearch.execution 
		// for working without web (and vice versa)
		CFactory cust2IndTalk2 = new OfferCounterOffer_Initiator(
				industryAgents[2],
				DataAndStruct.ETM_OFFER_COUNTEROFFER_PROTOCOL).
				newFactory("CustInd_TALK2", null, msg, conversationsLimit, myProcessor.getMyAgent(), 
				2, deadline, DataAndStruct.GLOBALTIMEOUT);

		///////////////////////////////////////////////////////////////////////////////
		// The template processor is ready. We add the factory, in this case as 
		// a initiator one
		///////////////////////////////////////////////////////////////////////////////
		//this.addFactoryAsInitiator(cust2IndTalk);
		myProcessor.getMyAgent().addFactoryAsInitiator(cust2IndTalk2);

		// Finally Customer starts the conversation.
		//this.startSyncConversation("CustInd_TALK");
		myProcessor.getMyAgent().startSyncConversation("CustInd_TALK2");
		
		// Comment below and uncomment in CustomerDynPackageSearch.execution 
		// for working without web (and vice versa)
		CFactory cust2IndTalk8 = new OfferCounterOffer_Initiator(
				industryAgents[8],
				DataAndStruct.ETM_OFFER_COUNTEROFFER_PROTOCOL).
				newFactory("CustInd_TALK8", null, msg, conversationsLimit, myProcessor.getMyAgent(), 
				2, deadline, DataAndStruct.GLOBALTIMEOUT);

		///////////////////////////////////////////////////////////////////////////////
		// The template processor is ready. We add the factory, in this case as 
		// a initiator one
		///////////////////////////////////////////////////////////////////////////////
		//this.addFactoryAsInitiator(cust2IndTalk);
		myProcessor.getMyAgent().addFactoryAsInitiator(cust2IndTalk8);

		// Finally Customer starts the conversation.
		//this.startSyncConversation("CustInd_TALK");
		myProcessor.getMyAgent().startSyncConversation("CustInd_TALK8");


		// Comment below and uncomment in CustomerDynPackageSearch.execution 
		// for working without web (and vice versa)
		CFactory cust2IndTalk9 = new OfferCounterOffer_Initiator(
				industryAgents[9],
				DataAndStruct.ETM_OFFER_COUNTEROFFER_PROTOCOL).
				newFactory("CustInd_TALK9", null, msg, conversationsLimit, myProcessor.getMyAgent(), 
				2, deadline, DataAndStruct.GLOBALTIMEOUT);

		///////////////////////////////////////////////////////////////////////////////
		// The template processor is ready. We add the factory, in this case as 
		// a initiator one
		///////////////////////////////////////////////////////////////////////////////
		//this.addFactoryAsInitiator(cust2IndTalk);
		myProcessor.getMyAgent().addFactoryAsInitiator(cust2IndTalk9);

		// Finally Customer starts the conversation.
		//this.startSyncConversation("CustInd_TALK");
		myProcessor.getMyAgent().startSyncConversation("CustInd_TALK9");

		
		return "INFORM";
	
	}
	
	class ACTION_Method implements ActionStateMethod {
		
		public String run(CProcessor myProcessor) {
			
			return doAction(myProcessor);
		}
		
	}
	*/
	
	/**
	 * Set the inform message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param response inform message
	 */
	protected void doInform(ConvCProcessor myProcessor, ACLMessage response) {

		//System.out.println(DataAndStruct.TAB + "WEB - Informando");
		
		WebRequestConversation conv = (WebRequestConversation) myProcessor.getConversation();
		
		XStream xstream = new XStream(new JsonHierarchicalStreamDriver() {
            @Override
            public HierarchicalStreamWriter createWriter(Writer writer) {
                return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
            }
        });
		
		// Fill json object

		response.setContent(xstream.toXML(conv.conversationResult));
		//System.out.println("Response: "+response.getContent());
        response.setProtocol(myProtocol);
        response.setPerformative(ACLMessage.INFORM);
        response.setSender(myProcessor.getMyAgent().getAid());
        response.setReceiver(conv.initiator);		
        
	}

	class INFORM_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doInform((ConvCProcessor) myProcessor, messageToSend);
			return "FINAL";
		}
	}
	
	/**
	 * Method executed when the conversation ends
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend final message
	 */
	protected void doFinal(CProcessor myProcessor, ACLMessage messageToSend){

	}
	
	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			 doFinal(myProcessor, messageToSend);
		}
	}

	/**
	 * Create a new participant offer counteroffer web factory
	 * @param name factory's name
	 * @param filter message filter
	 * @param availableConversations maximum number of conversation this CFactory can manage simultaneously
	 * @param myAgent agent owner of this CFactory
	 * @return a new fipa request participant factory
	 */
	public ConvCFactory newFactory(String name, MessageFilter filter,
			int availableConversations, ConvJasonAgent myAgent) {

		ACLMessage template;

		if (filter == null) {
			
			filter = new MessageFilter("performative = REQUEST");
		}
		else {
			
			String[] data = filter.getExpression().split("=");
			//myPerformative = data[1].split(" ")[1];
			myProtocol = data[2];

		}
		
		ConvCFactory theFactory = new ConvCFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup

		CProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());
		
		// WAIT State
		processor.registerState(new WaitState("WAIT", 0));
		processor.addTransition("BEGIN", "WAIT");
		
		// RECEIVE_REQUEST State
		ReceiveState RECEIVE_REQUEST = new ReceiveState("RECEIVE_REQUEST");
		RECEIVE_REQUEST.setMethod(new RECEIVE_REQUEST_Method());
		filter = new MessageFilter("performative = REQUEST");
		RECEIVE_REQUEST.setAcceptFilter(filter);
		processor.registerState(RECEIVE_REQUEST);
		processor.addTransition("WAIT", "RECEIVE_REQUEST");
			
		// INFORM State
		SendState INFORM = new SendState("INFORM");
		INFORM.setMethod(new INFORM_Method());
		template = new ACLMessage(ACLMessage.INFORM);
		template.setProtocol(myProtocol);
		INFORM.setMessageTemplate(template);
		processor.registerState(INFORM);
		processor.addTransition("RECEIVE_REQUEST", "INFORM");
		
		// FINAL State

		FinalState FINAL = new FinalState("FINAL");
		
		FINAL.setMethod(new FINAL_Method());
		processor.registerState(FINAL);

		processor.addTransition("INFORM", "FINAL");

		// Thath's all
		return theFactory;
	}	

}
