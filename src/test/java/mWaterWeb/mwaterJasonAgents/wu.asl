// Agent wu in project mWater-Magentix-Jason

/* Initial beliefs and rules */
staffname(staff).  //This must be obtained in other way

//ontology: invitation(Sender,TableID,WMarket,ConfID)
//pendingInvitations(TableID, WMarketID, PI):- .count(invitation(Table,SenderUser),PI). 


/* Initial goals */



/* Common Plans */

+!getAccTimeOut(TO):staffname(Sn)
<- .send(Sn,askOne,accreditationTimeOut(TO),accreditationTimeOut(TO)).
-!getAccTimeOut(TO)
<- TO = -1.

+!getOpenNTTQueryTimeOut(TO):staffname(Sn)
<- .send(Sn,askOne,openNTTQueryTimeOut(TO),openNTTQueryTimeOut(TO)).
-!getAccTimeOut(TO)
<- TO = -1.

+!getnewNTTTimeOut(TO):staffname(Sn)
<- .send(Sn,askOne,newNTTTimeOut(TO),newNTTTimeOut(TO)).
-!getnewNTTTimeOut(TO)
<- TO = -1.

+!getInvitationTimeOut(TO):staffname(Sn)
<- .send(Sn,askOne,invitationTimeOut(TO),invitationTimeOut(TO)).
-!getInvitationTimeOut(TO)
<- TO = -1.

+!getjoinNTTTimeOut(TO):staffname(Sn)
<- .send(Sn,askOne,joinNTTTimeOut(TO),joinNTTTimeOut(TO)).
-!getjoinNTTTimeOut(TO)
<- TO = -1.

+!getSubprotocolTimeOut(Rol,TO):staffname(Sn)
<- .send(Sn,askOne,subprotocolTimeOut(Rol,TO),subprotocolTimeOut(Rol,TO)).
-!getSubprotocolTimeOut(Rol,TO)
<- TO = -1.

+!getSubprotocoljoinTimeOut(Rol,TO):staffname(Sn)
<- .send(Sn,askOne,subprotocoljoinTimeOut(Rol,TO),subprotocoljoinTimeOut(Rol,TO)).
-!getSubprotocoljoinTimeOut(Rol,TO)
<- TO = -1.

+!getProposalsDeadLine(DL):staffname(Sn)
<- .send(Sn,askOne,proposalsDeadLine(DL),proposalsDeadLine(DL)).
-!getProposalsDeadLine(DL)
<- TO = -1.

+!getAccreditationConvID(ID):staffname(Sn)&myname(Me)
<- .send(Sn,askOne,accreditationConvID(ID,Me),accreditationConvID(ID,Me)).
-!getAccreditationConvID(ID)
<- ID = -1.

+!getOpenNTTConvID(ID):staffname(Sn)&myname(Me)
<- .send(Sn,askOne,openNTTConvID(ID,Me),openNTTConvID(ID,Me)).
-!getOpenNTTConvID(ID)
<- ID = -1.

+!getNewNTTConvID(WMarket,ID):staffname(Sn)&myname(Me)  //To get the ID for starting the converstation
<- .send(Sn,askOne,newNTTConvID(WMarket,ID,Me),newNTTConvID(WMarket,ID,Me)).
-!getNewNTTConvID(WMarket,ID)
<- ID = -1.

+!getInvitationConvID(ID):staffname(Sn)&myname(Me)  //To get the ID for starting the converstation
<- .send(Sn,askOne,invitationConvID(ID,Me),invitationConvID(ID,Me)).
-!getInvitationConvID(ID)
<- ID = -1.

+!getJoinNTTConvID(ID,Rol):staffname(Sn)&myname(Me)
<- .send(Sn,askOne,joinNTTConvID(ID,Rol,Me),joinNTTConvID(ID,Rol,Me)).
-!getJoinNTTConvID(ID,Rol)
<- ID = -1.

+!getSubprotocolID(ID,Protocol):staffname(Sn)&myname(Me)
<- .send(Sn,askOne,subprotocolID(ID,Protocol,Me),subprotocolID(ID,Protocol,Me)).
-!getSubprotocolID(ID,Protocol)
<- ID = -1.

+!notifyStaff(Sn,ConvID,Protocol,Action)
<- 
   .send(Sn,askOne,join(ConvID,Protocol,Action),Reply);
   if (Reply == false)
   {//.print("- Staff communication stablished. Conversation: ",ConvID)}
 //  else
 //  {.print("- Staff communication coul'd be stablished. Conversation: ",ConvID);
    .fail;}.
    
+!buyersoftable(TableID,WMarket,Members):staffname(Sn)
<- .send(Sn,askOne,buyersoftable(TableID,WMarket,Members),buyersoftable(TableID,WMarket,Members)).

+!sellersoftable(TableID,Members):staffname(Sn)
<- .send(Sn,askOne,sellersoftable(TableID,WMarket,Members),sellersoftable(TableID,WMarket,Members)).

+!pendingInvitations(TableID, WMarketID, PI)
<- 	?table_structure(Table);
	?table_Arguments_List(Table,TableFieldsList,TableID);
   	.count(invitation(Table,SenderUser),PI1);
   	?accredited(MyUserInformation,WaterMarket);
	?water_market_Arguments_List(WaterMarket,WMFieldsList,WMarketID);
   	?userInfo_Arguments_List(MyUserInformation,UserFIeldsList,UserID);
   	?staffname(Sn);
  	.send(Sn,askOne,pendinginvitations(TableID, WMarketID,UserID,PIList),Reply);
   	if (Reply==false)
   	{
   		PI = PI1;
   	}else{
   		Reply = pendinginvitations(TableID, WMarketID,UserID,PIList); 
   		.length(PIList,PI2);
   		PI = PI1+PI2;
   	}.

//-!notifyStaff(Sn,ConvID,Protocol)
//<- .print("- ",Sn," didn't join to the conversation ",ConvID).

/* ----------------------------------------------------------------*/

//Literal is the corresponding literal identifying the conversation.
//If it doesn't exists it means that the conversation is not taking place
/*{begin bdg( getOpenNTTList(OpenNTTList))} 

+!checkConvEnding(Literal,OpenNTTList): not Literal//&Literal = openNTTQueryConv(oNTTconv,Me,fqrp,Query)//&openNTTResult(OpenNTTList)
<- 
   .print("Open negociation tables query from ",Me," has finished. Result: ", OpenNTTList," Literal: ", Literal).

{end}*/


/* ->  ACREDITATION  - */

+!accredit(UserName,WM) : staffname(Sn)&myname(Me)
<- //!userlInfoFiledsToStructure(PersonalInfoList,UserInfo,UserID);
   if (WM==""){ .send(Sn,askOne,currentWaterMarket_id(WMarket),Reply);}
   else {Reply=currentWaterMarket_id(WM); }

   if (Reply==false){.fail;}
   else
	{  ?Reply=currentWaterMarket_id(WMarket);
	   ?request(accreditation, Request,[wmarket(WMarket),user_name(UserName)] );
	   !getAccreditationConvID(ID);
	   if (not ID==-1)
	   {+accreditationConv(Me,frp,ID);
	   !notifyStaff(Sn,ID,frp,Request);
	   !startAccreditation(Sn,ID,frp,Request,WMarket)}
	   else
	   {.print("Accreditation Id is not defined.");} //This must dissapear if the staff is allways listening this kind of requests
	}. 

-!accredit(PersonalInfoList,WMarket)
<- .print("Acreditation couldn't be started.").

+!startAccreditation(Sn,ConvID,Protocol,AccReq,WMarket):accreditationConv(Me,Protocol,ConvID)&
request(accreditation, AccReq,ParamList )
<- !getAccTimeOut(TO);
   if (not TO==-1)
   {//.print("-Starting accreditation conversation.");
   .ia_fipa_request_Initiator("start", Sn , TO, "Accreditation started",ConvID);
   .wait(500);
   
   .ia_fipa_request_Initiator("request",accreditation,Sn, AccReq,ConvID)}.

+taskdonesuccessfully(Sn,Result,ConvID):accreditationConv(Me,Protocol,ConvID)
<- -Result;
	+Result; //Result=accredited(User,WMarket)
   .ia_fipa_request_Initiator("taskdone",ConvID). //The second term is the proposal.

+conversationended(ConvID, Result):myname(Me)&accreditationConv(Me,frp,ConvID)
<- -accreditationConv(Me,Protocol,ConvID).
  // if (Result=="INFORM") {  .print("I'm accredited.")}.
  
/* -  ACREDITATION  <- */
  
/* ----------------------------------------------------------------*/
  
/* ->  QUERY OPEN NEGOCIATION TRADING TABLES  - */
  
+!getOpenNTTList(OpenNTTList,WM,TH) : staffname(Sn)&myname(Me)
<- 
   if (WM==""){.send(Sn,askOne,currentWaterMarket_id(WMarket),ReplyWM);}
   else {ReplyWM=currentWaterMarket_id(WM); }
   if (TH==""){.send(Sn,askOne,currentTradinghall_id(TradingHall),ReplyTH);}
   else {ReplyTH=currentTradinghall_id(TH); }
   
   if ((ReplyWM==false)|(ReplyTH==false)){.fail;}
   else
	{  ?ReplyWM=currentWaterMarket_id(WMarketID);
	   ?ReplyTH=currentTradinghall_id(TradingHallID);
	   !getOpenNTTConvID(ONTTconvID);
	   ?accredited(MyUserInformation,WaterMarket);
	   ?water_market_structure(WaterMarket);
   	   ?water_market_Arguments_List(WaterMarket,WMFieldsList,WMarketID);
	   
	   ?query(openNTT,Query,[openNTTList(OpenNTTList),wmarket(WMarketID),th_id(TradingHallID),sender(MyUserInformation)]);
	   //.print("@@@@ ---------- Query ",Query);
	   if (not ONTTconvID==-1)
	   {	
	        if (openNTTQueryConv(ONTTconvID,Me,fqrp,Query)){.fail;} //There is already a similar conversation
	        +openNTTQueryConv(ONTTconvID,Me,fqrp,Query);
	   		!notifyStaff(Sn,ONTTconvID,fqrp,Query);
	   		!getOpenNTTQueryTimeOut(TO);
	   		if (not TO==-1)
	   		{!startOpenNTTQuery(Sn,ONTTconvID,fqrp,TO);
			    //!checkConvEnding(openNTTQueryConv(oNTTconv,Me,fqrp,Query),OpenNTTList)
			 }
	   		else
	   		{.fail;}
	   	}else{
	   	.print("ID for Open Negociation Trading Tables conversation is not defined.");
	   	}
   	}.
   
-!getOpenNTTList(OpenNTTList) 
<- .print("The conversation to query the open negociation tables list couldn't be established.").

+!startOpenNTTQuery(Sn,ConvID,Protocol,TO):myname(Me)&openNTTQueryConv(ConvID,Me,Protocol,Query) 
<- .ia_fipa_query_Initiator("start", Sn , TO, "Fipa query conversation started",fqrp,ConvID);
   .ia_fipa_query_Initiator("ref-query",Query,Sn, ConvID).

+queryResult(Sn,Result,ConvID):myname(Me)&openNTTQueryConv(ConvID,Me,fqrp,Query)
<-  
	?query(openNTT,Query,ParamList);
	.member(wmarket(WMarket),ParamList);
	.member(th_id(THall),ParamList);
	?query(openNTT,Result,ResultQueryParamList);//ontology
	.member(openNTTList(OpenNTTList),ResultQueryParamList);
    -openNTT(_,WMarket,THall);
    +openNTT(OpenNTTList,WMarket,THall);
    //.print("@@@@---------- En el inform y agregando : openNTT(",OpenNTTList,",",WMarket,",",THall,")");
    .ia_fipa_query_Initiator("inform",ConvID). 

+conversationended(ConvID,Result):myname(Me)&openNTTQueryConv(ConvID,Me,Protocol,Query)
<-   //.print("Result received!!!! ---- ",Result);
	-openNTTQueryConv(ConvID,Me,Protocol,Query);
	//if (openNTT(OpenNTTList,WMarket))
	+openNTTqueryended(WMarket,THall).
	//.print("- Conversation ",ConvID," for query: ",Query," ended! Result: ",Result," ++++++++++++++").

  
/* -  QUERY OPEN NEGOCIATION TRADING TABLES  <- */

/* ----------------------------------------------------------------*/    

/* ->  REQUEST NEW NEGOCIATION TRADING TABLE  - */

+!createNewNTT(AttributesList,WaterRightIDs,Rol,Participants,Protocol,MWaterMarket,THall) 
<- !manageNewNTTCreation(AttributesList,Rol,Participants,Protocol,MWaterMarket,THall,Result).

+!manageNewNTTCreation(AttributesList,WaterRightIDsList,Rol,Participants,Protocol,WMarketID,THall,Result): staffname(Sn)&myname(Me)
<- ?accredited(MyUserInformation,WaterMarket);
   ?water_market_Arguments_List(WaterMarket,WMFieldsList,WMarketID);
   ?request(newNTT, Request,[table(Table),user(MyUserInformation),water_right_ids(WaterRightIDsList)]);
   !tableFiledsToTable(AttributesList,Table,TableID);
   ?table_Arguments_List(Table,ArgumentsList,TableID);//Maybe this can be removed
  // .member(wmarket(MWaterMarket),ParamList);
   .member(wmarket(WMarketID),ArgumentsList);
   //.member(th_id(THall),ParamList);
   .member(th_id(THall),ArgumentsList);
   //.member(configuration_id(Config),ParamList);  the staff is the who knows the configuration_id
   //.member(configuration_id(Config),ArgumentsList);
   .member(role_when_opening_table(Rol),ArgumentsList);
   .member(protocol_type(Protocol2),ArgumentsList);
   if (not .ground(Protocol2)) {Protocol2 = Protocol; }
   .date(Y,M,D);
   .member(opening_date([D,M,Y]),ArgumentsList);
   .member(num_iter_until_agreem(0),ArgumentsList);
   .member(time_until_agreem(0),ArgumentsList);
   .member(num_participants(Participants),ArgumentsList); //
   .member(number_of_opener_participations(0),ArgumentsList);
   .member(protocol_parameters("timeout:1,price:67,percentage:0.05"),ArgumentsList); //this must not be fixed
   
   //At this point 'TableID' has no value and 'Table' has three fields instantiated

   !getNewNTTConvID(WMarketID,ConvID);

   if (not ConvID==-1)
   {	
    	+newNTTConv(Me,frp,ConvID);
   		!notifyStaff(Sn,ConvID,frp,Request);
   		NewTable = Table;
   		!startnewNTTrequest(Sn,ConvID,frp,Request,NewTable);
   		
   }else{ 
   	.print("ID for New Negociation Trading Tables conversation is not defined.");
   }.  //This must dissapear if the staff is allways listening this kind of requests

-!manageNewNTTCreation(Table,Result)
<- .print("New negociation table creation request couldn't be finished.").

+!startnewNTTrequest(Sn,ConvID,Protocol,Req,NewTable):newNTTConv(Me,Protocol,ConvID)
<-
   !getnewNTTTimeOut(TO);
   if (not TO==-1)
   {//   .print("-Starting New negociation table conversation.");
   .ia_fipa_request_Initiator("start", Sn , TO, "New NTT request started",ConvID);
   .wait(500);
   ?request(newNTT, Req,ParamList);

   .ia_fipa_request_Initiator("request",newNTT,Sn,Req,ConvID);
         }.

+taskdonesuccessfully(Sn,Result,ConvID):newNTTConv(Me,Protocol,ConvID)
<- //.print("********** taskdonesuccessfully result: ",Result);
   +tablecreated(Result); //Result is the table created
   //!inviteParticipants(Result);
   .ia_fipa_request_Initiator("taskdone",ConvID). 

+conversationended(ConvID, Result):myname(Me)&newNTTConv(Me,frp,ConvID)
<-// .print("New negociation table ",ConvID," creation ended!!! Result: ",Result);
   if (Result\=="INFORM"){+tablenotcreated;}
   -newNTTConv(Me,frp,ConvID).

+!inviteParticipants(Table)
<- 
   //.broadcast(tell,invitation(Result,User)).
   !manageNewInvitation(Table,[]). //In the future 'Participants' (the second argument) must be filled

-!inviteParticipants(Table)
<- .print("The invitation to participants has failed. Probably I have no information about my accreditation yet.").
  
/* -  REQUEST NEW NEGOCIATION TRADING TABLE  <- */

/* ----------------------------------------------------------------*/

/* ->  INVITATION  - */
//ontology: !manageNewInvitation(Table,Participants)
+!manageNewInvitation(Table,Participants): staffname(Sn)
<- ?accredited(MyUserInformation,WaterMarket);
   ?water_market_Arguments_List(WaterMarket,WMFieldsList,WMarketID);
   ?request(invitation, Request, [table(Table),sender(MyUserInformation),receivers(Participants)]);
   !getInvitationConvID(ID);
   if (ID\==-1)
   {
   +invitationConv(Me,frp,ID);
    !notifyStaff(Sn,ID,frp,Request);
    !startInvitation(Sn,ID,frp,Request)}
   else
   {.print("Invitation Id is not defined.");}.  //This must dissapear if the staff is allways listening this kind of requests

-!manageNewInvitation(Table,Participants)
<- .print("Invitation to table: ",Table," couldn't be started.").

+!startInvitation(Sn,ConvID,frp,Request):invitationConv(Me,Protocol,ConvID)
<- 
   ?request(invitation,Request,ParamList);
   !getInvitationTimeOut(TO);
   if (not TO==-1)
   {//.print("-Starting invitation conversation.");

   .ia_fipa_request_Initiator("start", Sn , TO, "Invitation started",ConvID);
   .wait(500);
   .ia_fipa_request_Initiator("request",invitation,Sn, Request,ConvID)}.

+taskdonesuccessfully(Sn,Result,ConvID):invitationConv(Me,Protocol,ConvID)
<- 
   .ia_fipa_request_Initiator("taskdone",ConvID). 

+conversationended(ConvID, Result):myname(Me)&invitationConv(Me,frp,ConvID)
<- -invitationConv(Me,Protocol,ConvID);
   if (Result=="INFORM") {  .print("Participants have been invited.")}.
  
/* -  INVITATION  <- */
  

  
/* ----------------------------------------------------------------*/  

/* ->  REQUEST JOIN NEGOCIATION TRADING TABLE  - */

+!joinNTT(Table, Rol) : staffname(Sn)&myname(Me)&
request(joinNTT, Request,[table(Table),user(MyUserInfo),rol(MyRol)] )
<-  ?accredited(MyUserInfo,WMarket);
	if ((Rol\=="seller")&((Rol\=="buyer"))){
		?table_Arguments_List(Table,TFieldsList,TableID);
		?userInfo_Arguments_List(MyUserInfo,UFieldsList,UserID);
		.member(opening_user(OpUsr),TFieldsList);
		if (OpUsr==UserID) //seller
		{MyRol="seller";}else{MyRol="buyer";}
	}
	!getJoinNTTConvID(ConvID,MyRol);
   if (not ConvID==-1)
   {	if (joinNTTConv(Me,TableID,frp,ConvID)){.fail;};
  		+joinNTTConv(Me,TableID,frp,ConvID);
   		!notifyStaff(Sn,ConvID,frp,Request);
   		!startjoinNTTrequest(Sn,ConvID,frp,Request);
   }else{
   	.print("ID for join Negociation Trading Tables conversation is not defined.");
   }.  //This must dissapear if the staff is allways listening this kind of requests

-!joinNTT(Table,Rol)
<- .print("Request for join to table ",TableID," couldn't be finished.").

+!startjoinNTTrequest(Sn,ConvID,Protocol,Req):joinNTTConv(Me,TableID,Protocol,ConvID)
<- !getjoinNTTTimeOut(TO);
   if (not TO==-1)
   {
   .ia_fipa_request_Initiator("start", Sn , TO, "Join NTT request started",ConvID);
   .wait(500);
   ?request(joinNTT, Req,ParamList);
   .ia_fipa_request_Initiator("request",joinNTT,Sn,Req,ConvID)}.

+taskdonesuccessfully(Sn,Result,ConvID):joinNTTConv(Me,TableID,Protocol,ConvID)
<-
   +joined(Result); //Result is a literal "participant(RPart,Table,Rol)"
   .ia_fipa_request_Initiator("taskdone",ConvID). //The second term is the proposal.

+conversationended(ConvID, Result):myname(Me)&joinNTTConv(Me,TableID,frp,ConvID)
<- //.print("Conversation ",ConvID," for join to table ended!!!");
   -joinNTTConv(Me,TableID,frp,ConvID).

  
/* -  REQUEST JOIN NEGOCIATION TRADING TABLE  <- */
  
/* ----------------------------------------------------------------*/  
    
/*  ->  START SUBPROTOCOL  -   */
 
+!startSubprotocol(Protocol, ProtocolParameters)//,Rol,WaterRightFieldsList,Table)
<- 
   !getSubprotocolTimeOut(initiator,TO);
   !getSubprotocoljoinTimeOut(initiator,JTO);
   !getProposalsDeadLine(DL);
   !getSubprotocolID(ID,Protocol);
   !protocoltype(Protocol,ProtName);
   .member(rol(Rol),ProtocolParameters);
   .member(tableid(TableID),ProtocolParameters);
   .member(wmarket(WMarket),ProtocolParameters);
   .member(confid(ConfID),ProtocolParameters);
   ?table_structure(Table);
   ?table_Arguments_List(Table,TFieldsList,TableID);
   .member(configuration_id(ConfID),TFieldsList);
   .member(wmarket(WMarket),TFieldsList);

   //.member(table(Table),ProtocolParameters);
   if ((TO \== -1)&(DL \== -1 )&(ID \== -1))
     {  .print("Starting subprotocol ",Protocol," as ",Rol," in table ",Table);
        if (ProtName == "contract_net")
        	{
   			.member(water_right_fields(WaterRightFieldsList),ProtocolParameters);
   			!waterRightFiledsToWRight(WaterRightFieldsList,WaterRight,WRID);
        	!manageStartSubProtocol(Protocol,ProtName,Rol,WaterRight,Table,JTO,TO,DL,ID);
        	}
        if (ProtName == "japanese_auction")
        	{
   			.member(water_right_fields(WaterRightFieldsList),ProtocolParameters);
   			.member(max_iterations(MaxIter),ProtocolParameters);
   			.member(increment(Inc),ProtocolParameters);
   			.member(initial_bid(IniBid),ProtocolParameters);
   			!waterRightFiledsToWRight(WaterRightFieldsList,WaterRight,WRID);
        	!manageStartSubProtocol(Protocol,ProtName,Rol,WaterRight,Table,MaxIter,Inc,IniBid,JTO,TO,ID);
        	}
        
      }
   else 
     {.fail;}.
   
-!startSubprotocol(Protocol,ProtocolParameters)
<- .print("Failing when trying to start suprotocol ",Protocol).

+!notifyParticipants([],ConvID,Protocol).

+!notifyParticipants([P|R],ConvID,Protocol): not notified(P,ConvID,Protocol)
<-
  .send(P,askOne,joinprotocol(Protocol,ConvID),Reply);
  if (Reply \== false)
  {
   	+notified(P,ConvID,Protocol);
   	!notifyParticipants(R,ConvID,Protocol)
   	}
   	else{
   		.print("One or more participants couldn't be notified to join to subprotocol.");
   		.fail;
   	}.

+!protocoltype(Prot,TypeName):staffname(Sn)
<- ?protocol_type_structure(PType);
   ?protocol_type_Arguments_List(PType,PTFieldsList,PTypeID);
   .member(id(Prot),PTFieldsList);
   .member(type_name(TypeName),PTFieldsList);
   .send(Sn,askOne,PType,Reply);
   
   if (Reply \== false)
   		{PType=Reply;	}
   else
   		{.fail;}.   

/*  -   START SUBPROTOCOL  <-   */

/*  ---------------------------------------------------------------  */

/*  ->  FIPA CONTRACT NET SUBPROTOCOL  -  */

/* <INITIATOR> */


+!manageStartSubProtocol(ProtID,ProtName,seller,WaterRight,Table,JTO,TO,DL,SubProtConvID):myname(Me)&
ProtName == "contract_net" //ontology
<- 
   ?table_Arguments_List(Table,TableArgList,TableID);
   .member(wmarket(WMarket),TableArgList);
   !buyersoftable(TableID,WMarket,Buyers);
   ?protocol_request(ProtName,ProtID, PRequest, [water_right(WaterRight),table(Table),sender(Me)]);
   +subprotocolConv(Me,ProtName,PRequest,seller,SubProtConvID);
   !notifyParticipants(Buyers,SubProtConvID,ProtName);
   .ia_FCN_Initiator("start", TO, DL, Buyers, PRequest,SubProtConvID).



+proposalsevaluationtime(SubProtConvID):myname(Me)&
ProtName = "contract_net"&subprotocolConv(Me,ProtName,PRequest,seller,SubProtConvID)
<- .findall(proposal(P,Source),proposal(P,Source,SubProtConvID),Proposals);
   ?protocol_request(ProtName,ProtID, PRequest, ProtReqParamList);
   !protocoltype(ProtID,ProtName);
   !evaluateProposals(ProtName,Proposals,Accepted,Rejected);
   +accepted_proposals(Accepted,SubProtConvID);
   +rejected_proposals(Rejected,SubProtConvID);
   .print("Best proposal: ",Accepted);
   .abolish(proposal(_,_,SubProtConvID));
   .ia_FCN_Initiator("proposalsevaluated",Accepted,Rejected,SubProtConvID).


/*This is going to be executed only if there is no other definition.
Another way to make is establishing at the begining of the protocol 
the way of selecting the best (Higest, Smallest, etc...)*/
-!evaluateProposals(ProtName,Proposals,Accepted,Rejected):ProtName=="contract_net"
<- .print("Evaluation of proposals has failed. Taking the higest as the best.");
   .max(Proposals,Best);
   Best=proposal(P,Source);
   Accepted=[P,Source];

   .difference(Proposals,[Best],RejectedProposals);
   !buildProposalsList(RejectedProposals,[],Rejected).
   //.print("2222 Accepted: ",Accepted," Rejected ",Rejected).

+!buildProposalsList([],AuxList,AuxList).
+!buildProposalsList([HeadProp|TailProp],AuxList,ResultList)
<- HeadProp = proposal(P,Source);
   .concat(AuxList,[P],[Source],NewAuxList);
   !buildProposalsList(TailProp,NewAuxList,ResultList).
-!buildProposalsList(Proposals,AuxList,[]).


+resultsreceived(SubProtConvID):myname(Me)&subprotocolConv(Me,ProtName,PRequest,seller,SubProtConvID)&
ProtName = "contract_net"
 <- //.print("Processing results... CID: ",SubProtConvID);
    !processResults(SubProtConvID); 
    .ia_FCN_Initiator("resultsprocessed",SubProtConvID).

+!processResults(SubProtConvID):taskDone(Sender,SubProtConvID)&
subprotocolConv(Me,ProtName,PRequest,seller,SubProtConvID)&ProtName="contract_net"
<- 
    .print("Agent ",Sender," has confirmed the trade.");
    ?protocol_request(ProtName,Prot,PRequest, ProtReqParamList);
    .member(water_right(WaterRight),ProtReqParamList);
    .member(table(Table),ProtReqParamList);
    ?accepted_proposals(Accepted,SubProtConvID);
    if (Accepted = [Price,Sender])
    {
	    ?transfer_agreement_structure(TAgr);
	    ?transfer_agreement_Arguments_List(TAgr,TAFieldsList,TAID);
	    .member(agreed_price(Price),TAFieldsList);
	    ?waterRight_Arguments_List(WaterRight,WRFieldsList,WRID);
	    .member(water_right_id(WRID),TAFieldsList);
	   
	    ?table_Arguments_List(Table,TableFieldsList,TableID);
	    .member(trading_table_id(TableID),TAFieldsList);
	    .member(wmarket(WMarket),TableFieldsList);
	    .member(wmarket(WMarket),TAFieldsList);
	    .member(configuration_id(ConfID),TableFieldsList);
	    .member(configuration_id(ConfID),TAFieldsList);
	    .date(Y,M,D);
	    .member(signature_date([D,M,Y]),TAFieldsList);
	    ?staffname(Sn);
	    .send(Sn,askOne,addTransferAgreement(TAgr,Sender),Reply); //ontology
	    if (Reply\==false){.print("Transfer agreements successfully registered! ");}
	    else{.print("Transfer agreements was not registered!");}
	}
    -taskDone(Sender,SubProtConvID); //this belief is added by the system when receiving answers from participants
    //+trade(WaterRight,seller,S,confirmed);  //ontology: possible states for a trade
    !processResults(SubProtConvID).
+!processResults(SubProtConvID):taskNotDone(S,SubProtConvID)
<- 
    //+rejected_proposals(Rejected,SubProtConvID);
    .print("Agent ",S," doesn't want to make the trade.",SubProtConvID);
    -taskNotDone(S,SubProtConvID);
    //+trade(WaterRight,seller,S,rejected);
    !processResults(SubProtConvID).
+!processResults(SubProtConvID)
<- .print("Processing of results finished.").

+conversationended(SubProtConvID):myname(Me)&
subprotocolConv(Me,ProtName,PRequest,seller,SubProtConvID)&ProtName="contract_net"
<-  -accepted_proposals(Accepted,SubProtConvID);
    -rejected_proposals(Rejected,SubProtConvID);
    -subprotocolConv(Me,PRequest,seller,SubProtConvID).
  //.print("Conversation ",SubProtConvID," ENDED!  ++++++++++++++").

/* <PARTICIPANT> */

// By default the agents are agree to start a conversation of a cnp subprotocol
@pjoincnp[atomic]
+?joinprotocol(ProtName,SubProtConvID):ProtName=="contract_net"
<- .print("I'm going to join protocol ",ProtName); 
   .ia_FCN_Participant("joinconversation",SubProtConvID).

//When receiving a call, to agree or refuse is random
+callforproposal(Sender,PRequest,SubProtConvID):
ProtName = "contract_net"&protocol_request(ProtName,Protocol, PRequest, ParamList)
<-  
    	!evaluatePriceOfWaterRight(PRequest,Price);
   		if (Price > 0){//just a random condition to agree
   			+callState(PRequest,SubProtConvID,sent); //ontology
   			.ia_FCN_Participant("makeproposal",Price,SubProtConvID);
   			}
    	else {
    		.print("I've recived a call for proposal: ",PRequest," and i've rejected it.");
   			.ia_FCN_Participant("refuse",SubProtConvID);
    		}.
+callforproposal(Sender,PRequest,SubProtConvID):protocol_request(ProtName,Protocol, PRequest, ParamList)
<-  
   	.print("I've recived a call for proposal: ",PRequest," and I didn't understand it. ");
	.ia_FCN_Participant("notunderstood",SubProtConvID).


   
/*This is going to be executed only if there is no other definition.
Another way to make is establishing at the begining of the protocol 
the way of selecting the best (Higest, Smallest, etc...) */
-!evaluatePriceOfWaterRight(PRequest,Price):myname(Me)&
ProtName = "contract_net"& protocol_request(ProtName, ProtID, PRequest, ParamList)
<-
   	.member(water_right(WRight),ParamList);
   	.member(table(Table),ParamList);
   	.member(sender(Sender),ParamList);
   	
	.random(P); 
	Price = P * 20 ;
	.print("Evaluating the price for call has failed. Generating a random value: ",Price).


+timetodotask(Sender,SubProtConvID):callState(PRequest,SubProtConvID,sent)&
ProtName = "contract_net"&
protocol_request(ProtName, ProtID, PRequest, ParamList)
<- -callState(_,SubProtConvID,sent);
	+callState(PRequest,SubProtConvID,confirmed);
   //.print("PRequest ",PRequest," has been confirmed.");
   .ia_FCN_Participant("taskdone","OK, i confirm the trade.",SubProtConvID).

+timetodotask(Sender,SubProtConvID)
<- .print("Task of conversation ",SubProtConvID," failed!");
   .ia_FCN_Participant("tasknotdone","Task failed!",SubProtConvID).    			   


/*  -  FIPA CONTRACT NET SUBPROTOCOL  <-  */

/*  ---------------------------------------------------------------  */

/*  ->  JAPANESE AUCTION PROTOCOL  -  */

/* <INITIATOR> */

+!manageStartSubProtocol(ProtID,ProtName,seller,WaterRight,Table,MaxIter,Inc,IniBid,JTO,TO,SubProtConvID):myname(Me)&
ProtName == "japanese_auction" //ontology
<- //.print("///// manageStartSubProtocol");
   ?table_Arguments_List(Table,TableArgList,TableID);
   .member(wmarket(WMarket),TableArgList);
   !buyersoftable(TableID,WMarket,Buyers);
   ?protocol_request(ProtName,ProtID, PRequest, [water_right(WaterRight),table(Table),sender(Me),max_iterations(MaxIter),increment(Inc),initial_bid(IniBid)]);
   +subprotocolConv(Me,ProtName,PRequest,seller,SubProtConvID);
   !notifyParticipants(Buyers,SubProtConvID,ProtName);
   .ia_JAuc_Initiator("start",JTO,TO, IniBid,Inc,MaxIter,"Starting japanese auction", Buyers,PRequest,SubProtConvID).

+conversationended(Participations,Winner,WinnerBid,SubProtConvID):myname(Me)&ProtName="japanese_auction"&
subprotocolConv(Me,ProtName,PRequest,seller,SubProtConvID)
<- .time(H,Mi,S);
   .print("---- WINNER: ",Winner," Bid: ",WinnerBid); 
   //.print("/// Participantions: ",Participations);
	?staffname(Sn);
	?protocol_request(ProtName, ProtID, PRequest, ReqParamList);
	.member(table(Table),ReqParamList);
	?table_Arguments_List(Table,TableFieldsList,TableID);
	.member(water_right(WaterRight),ReqParamList);
    ?waterRight_Arguments_List(WaterRight,WRFieldsList,WRID);
	.member(wmarket(WMarket),TableFieldsList);	
	for (.member(P,Participations))
		{
			if ((P = [Participant,ParticipationsNum])&(ParticipationsNum>0))
			{
	    		.send(Sn,askOne,addParticipation(Table,Participant,ParticipationsNum),Reply); //ontology
	    		if (Reply\==false){.print("Participations of ",Participant," successfully registered! ");}
	    		else{.print("Participations of ",Participant," were not registered! ");}
			}
		}
	
	if (.ground(Winner))
	{

	    ?transfer_agreement_structure(TAgr);
	    ?transfer_agreement_Arguments_List(TAgr,TAFieldsList,TAID);
	    .member(agreed_price(WinnerBid),TAFieldsList);
	    .member(water_right_id(WRID),TAFieldsList);
	    .member(trading_table_id(TableID),TAFieldsList);
	    .member(wmarket(WMarket),TAFieldsList);
	    .member(configuration_id(ConfID),TableFieldsList);
	    .member(configuration_id(ConfID),TAFieldsList);
	    .date(Y,M,D);
	    .member(signature_date([D,M,Y]),TAFieldsList);
	    .send(Sn,askOne,addTransferAgreement(TAgr,Winner),Reply); //ontology
	    if (Reply\==false){.print("Transfer agreements of japanese auction successfully registered! ");}
	    else{.print("Transfer agreements of japanese auction was not right registered!");}

	}else{.print("No winner in japanese auction for table ",TableID," in water market ",WMarket);};
	for (.member(P,Participations))
		{
			if ( P = [Participant,ParticipationsNum] )
			{
				if (not .ground(WRID)){WRID=""};
				if (.ground(Winner)){
					//fifth param of auctionsummary is if there was winner
					if (Participant\==Winner)
					 { XX=auctionsummary(Me,ProtID,ProtName,TableID,WRID,true,Winner,WinnerBid,SubProtConvID);
					   .print("Sending auction summary to participant: ",Participant," the winner: ",Winner," : ",XX);
					   .send(Participant,tell,XX);}
				}else{
				     XX=auctionsummary(Me,ProtID,ProtName,TableID,WRID,false,Winner,WinnerBid,SubProtConvID);
				    .print("NO WINNER!!! SENDING INFOMRATION TO : ",Participant," the winner: ",Winner," : ",XX);
					.send(Participant,tell,XX);
				}
			}
		}
		.print("Japanesea auction finished!!!").
	
  //.print("Conversation ",SubProtConvID," ENDED!  ++++++++++++++").

/* <PARTICIPANT> */

// By default the agents are agree to start a conversation of a cnp subprotocol

@pjoinja[atomic]
+?joinprotocol(ProtName,SubProtConvID):ProtName == "japanese_auction"
<- .print("I'm going to join protocol ",ProtName," SubProtConvID ",SubProtConvID); 
	!getSubprotocolTimeOut(participant,TO);
   .ia_JAuc_Participant("joinconversation",TO,SubProtConvID).

//When receiving a call, to agree or refuse is random
+callforbid(Sender,PRequest,RemainingParticipants,Bid,SubProtConvID):
ProtName = "japanese_auction" & protocol_request(ProtName,Protocol, PRequest, ParamList)
<-  	//.print("////callforbid  antes de PRequest: ",PRequest);
        //.print("////callforbid  despues de de PRequest: ",PRequest);
    	.member(water_right(WRight),ParamList);
    	.time(H,M,S);
    	.print("Call for bid received! Bid: ",Bid," WRight: ",WRight," Time: ",H,".",M,".",S);
		.member(table(Table),ParamList);
		?table_Arguments_List(Table,TableFieldsList,TableID);
    	.member(wmarket(WMarket),TableFieldsList);
    	
    	?acceptPrice(WRight,TableID,WMarket,Protocol,Bid,RemainingParticipants,Reply); //ontology: there must be a plan in the agent for this
    	if ((.ground(Reply))&(Reply = true))
    		{.print("I'm agree with bid.");
    		 .ia_JAuc_Participant("agree",SubProtConvID);}
    	else
    		{.print("Withdrawal!!! ");
    		 .ia_JAuc_Participant("withdrawal",SubProtConvID);}.
    		   		
+!goonwithbidagree(SubProtConvID)
<- .ia_JAuc_Participant("agree",SubProtConvID).  
+!goonwithbidwithdrawal(SubProtConvID)
<- .ia_JAuc_Participant("withdrawal",SubProtConvID).   			   
/*This is going to be executed only if there is no other definition.
Another way to make is establishing at the begining of the protocol 
the way of selecting the best (Higest, Smallest, etc...) */

+?acceptPrice(WRight,TableID,WMarket,Protocol,Bid,Participants,Reply)
<-  .random(R);
	NewR = R * 100;
	if ((NewR > 50)|(NewR<20 ))
	{Reply = true}
	else{
	Reply = false;
	}.

+winner(Sender,PRequest,FinalBid,SubProtConvID):
ProtName = "japanese_auction" & protocol_request(ProtName,Protocol, PRequest, ParamList)
<- .print("I've been choosen as WINNER!");
   .member(water_right(WRight),ParamList);
   .member(table(Table),ParamList).

//japanese_auction
//auctionsummary("VBotti",protocolrequest(...),false,Winner,25,subprotocol(4,"4.VBotti.2012.2.12.0.44.43"))
//auctionsummary("VBotti",protocolrequest(4,water_right(owner(2),id(67),authorized_extraction_flow(23),authorization_date([21,4,2012]),authorized(true),type_of_water("energy"),initial_date_for_extraction([1,1,2010]),final_date_for_extraction([31,12,2014]),aggregation_right(0),season_unit(1),season(1),general_water_right(1)),trading_table(configuration_id(556),wmarket(1014),id(2),opening_date([28,2,2012]),closing_date([]),conditions(""),access_type("Public"),deal(""),protocol_parameters("timeout:1,price:13,percentage:0.05"),num_iter_until_agreem(0),time_until_agreem(0),num_participants(4),opening_user(2),protocol_type(4),role_when_opening_table("Seller"),number_of_opener_participations(0),th_id(1014)),"VBotti",12,5,1),false,Winner,6,subprotocol(4,"4.VBotti.2012.2.12.3.9.0")),mid194)

+auctionsummary(Sender,ProtID,ProtName,TableID,WRID,ThereWasWinner,Winn,WinnerBid,SubProtConvID):
ProtName == "japanese_auction"  //&protocol_request(ProtName,Protocol, PRequest, ParamList)
<- 
   if (ThereWasWinner==true){
		.print("I haven't won the auction in protocol: ",ProtName,". The winner was ",Winner," win bid ",WinnerBid);
	}else{.print("There was not winner in protocol: ",ProtName);}.
   
/*  -  JAPANESE AUCTION PROTOCOL  <-  */

/* ----------------------------------------------------------------- */

/* <ALL PROTOCOLS> */


-!evaluatePriceOfWaterRight(PRequest,Price)
<- Price = 0. 




 { include("belief_builder.asl") }
