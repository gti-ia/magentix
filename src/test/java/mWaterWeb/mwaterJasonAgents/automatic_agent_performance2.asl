// Agent automatic_agent in project MWaterWeb

/* Initial beliefs and rules */

/* Initial goals */
!start.


/* Plans */

+!start:myname(Me)
<- //+contador(1);
   
   .setlogfile("logs/agents.log");
   .print("Action:ACCREDITATION | Input:[] | Output:Started");
   !accredit(Me,"").

+accredited(User,WMarket)
<- ?water_market_Arguments_List(WMarket,WMFieldsList,WMID);
   .print("Action:ACCREDITATION |Input:[Water-market:",WMID,"] | Output:Success");
   +firstRound(true).
   //!startFirstRound.

//+!startFirstRound
//<- +firstRound(true);
//   !startRound([]).

+!startRound(ExecutionDate)
<-  //.send(Sn,tell,roundStarted(UsrID,WMarketID,ExecutionDate));
    -+currentExecutionDate(ExecutionDate);
	?accredited(User,WMarket);
	?userInfo_Arguments_List(User,UsrFieldsList,UsrID);
	?water_market_Arguments_List(WMarket,WMFieldsList,WMarketID);
	!cleanBeliefsBase; 
    ?decideCreateTable(ExecutionDate, UsrID);
	if (max_iteration_number(MaxIt)  )
    	{RMaxIt = MaxIt;}
   	else 	{RMaxIt = 8}
   	if (participants_per_table(NPart)){RNPart=NPart}else{RNPart=3;}
   	if (protocol_negotiations_type(Ptype)){RPtype=Ptype}else{RPtype=4;}
    
	.concat([num_iter_until_agreem(RMaxIt)],[num_participants(RNPart)],[opening_user(UsrID)],[protocol_type(RPtype)],[role_when_opening_table("Seller")],AttributesList);
	
    !createNewNTT(AttributesList,[],"Seller",RNPart,RPtype,WMarketID,_).
    
-!startRound(ExecutionDate):staffname(Sn)
<-  //.print(" << I don't want to create a new trading table for date ",ExecutionDate," >>");
//    .send(Sn,achieve,finishedRound);
    -+firstRound(false).

@pstartsubprotocol[atomic]
/*+allmemberjoined(TableID,WMarket,ConfID,ProtType,UserID,WR,MissingParticipants,ExecutionDate)
<- //?firstRound(FirstRound);
   !evaluateStartSubProt(TableID,WMarket,ConfID,ProtType,UserID,WR,MissingParticipants,ExecutionDate).*/

+!evaluateStartSubProt(TableID,WMarket,ConfID,ProtType,UserID,WR,ExecutionDate):
staffname(Sn)&myname(Me)
<-  //what i do as seller
    //?table_Arguments_List(Table,FieldsList,TableID);
    !decideStartSubprotocol(TableID,WMarket,ConfID,ProtType,UserID,WR,ExecutionDate,SPReply) ;
    if (SPReply=true)
   {
    if (max_iteration_number(MaxIt) & bid_increment(BidInc ) & initial_bid(IniBid )  )
    	{RMaxIt = MaxIt; RBidInc = BidInc; RIniBid = IniBid;}
    else {RMaxIt = 12; RBidInc = 5; RIniBid = 1;}
	if ( (WR\==[]) & (WR\==[""] ) ){ ?waterRight_Arguments_List(WR,WRFieldsList,WRID) ; }
	else{  WRFieldsList=[];  }
   .concat([rol(seller)],[water_right_fields(WRFieldsList)],[tableid(TableID)],[wmarket(WMarket)],[confid(ConfID)],[max_iterations(RMaxIt)],[increment(RBidInc)],[initial_bid(RIniBid)],ProtocolParameters);
   !!startSubprotocol(ProtType,ProtocolParameters);
   }else  //I don't want to start a subprotocol
   {		//.print("Condition for starting not fulfilled. Closing table...");
		.print("Action:START NEGOTIATION | Input:[ProtocolID:",ProtType,",Table:",TableID,"] | Output:Refused");
		?table_structure(Table);
		?table_Arguments_List(Table,TableArgList,TableID);
		.member(wmarket(WMarket),TableArgList);
		.member(configuration_id(ConfID),TableArgList);
	 	.send(Sn, achieve,closeTable(Table));
	 	.send(Sn,achieve,finishedRound(Me)); 
   }
   .abolish(newmemberjoined(TableID,WMarket,ConfID,ProtType,_,_,_,ExecutionDate)); 
   -+firstRound(false).

-!evaluateStartSubProt(TableID,WMarket,ConfID,ProtType,UserID,WR,MissingParticipants,ExecutionDate):staffname(Sn)
<- -+firstRound(false).

+invitation(Table, RequesterUser, RemainingPart) //an invitation is received
<-	
	!!decideInvitation(Table, RequesterUser, RemainingPart).

+!decideInvitation(Table, RequesterUser, RemainingPart):RemainingPart>0
<- //!decideIfAccept(Table, RequesterUser);

   !joinNTT(Table,""); .

-!decideInvitation(Table, RequesterUser, RemainingPart).//:staffname(Sn)
//<-   ?currentExecutionDate(ExecutionDate); 
//    .send(Sn,tell,invitationDeclined(Table,ExecutionDate)).


//DECISION
+?acceptPrice(WRight,TableID,WMarket,Protocol,Bid,Participants,Reply):myname(UserName)
<- 
   if (buying_probability(BProb)){RAccRate = BProb}
   else {RAccRate = 0.5};
  // if (UserName="aut_agent115"){.print("*/*/*/*/*/**/ Deciding RAccRate ",RAccRate);};
  // .print("Deciding if to accept price. My buying probability: ",RAccRate); 
   .random(R);
   NewR = R * 100;
   PercAccRate = RAccRate * 100;
   if (NewR < PercAccRate )
   {Reply = true}
   else{	Reply = false;	}.
	

//DECISION	
+?decideCreateTable(ExecutionDate, UsrId):staffname(Sn)&myname(Me)
<-	
    .send(Sn,askOne,canCreateTable(UsrId),Result); 
    if (Result = canCreateTable(UsrId)[source(Sn)]){
		.print("Action:TRADING TABLE CREATION | Input:[Sim-date:",ExecutionDate,"] | Output:Accepted")
	}else{
		.print("Action:TRADING TABLE CREATION | Input:[Sim-date:",ExecutionDate,"] | Output:Refused");
   		.send(Sn,achieve,finishedRound(Me));
   		.fail;
	}.
//DECISION	
+!decideIfAccept(Table, RequesterUser):staffname(Sn)&myname(Me)
<-  if (invitation_acceptance_probability(IAProb)){RIAProb=IAProb}
    else {RIAProb = 1};
   //.print("RIAProb ",RIAProb);
   .random(R);
   NewR = R * 100;
   NewIAProb = RIAProb * 100;
   ?table_Arguments_List(Table,FieldsList,TableID);
   if (NewR < NewIAProb ) //I join table
   {	
	.print("Action:INVITATION | Input:[Table:",TableID,"] | Output:Accepted");
   }
   else{
	.print("Action:INVITATION | Input:[Table:",TableID,"] | Output:Refused");
	.fail; }.

+!sendfinishedround:staffname(Sn)
<- .send(Sn,achieve,finishedRound(Me)).

+!waitForParticipantsToJoinTable(Table):staffname(Sn)&myname(Me)
<- 
//    !getSubprotocolTimeOut(initiator,TO);
//.print("waitForParticipantsToJoinTable +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	.send(Sn,askOne,invitationAcceptanceDeadline(TO),ReplyTO);
	if (ReplyTO=invitationAcceptanceDeadline(TO)[source(Sn)]){ NewTO=TO;}
	else {    NewTO = 50000 ;};
     .print("-------- Start waiting for participants: ",NewTO);
	.wait(NewTO);
	
	?table_structure(Table);
	?table_Arguments_List(Table,TableArgList,TableID);
	.member(wmarket(WMarket),TableArgList);
	.member(configuration_id(ConfID),TableArgList);
	if (newmemberjoined(TableID,WMarket,ConfID,ProtType,UserID,WR,MissingParticipants,ExecutionDate))  //there is at least one participant
	{
		!!evaluateStartSubProt(TableID,WMarket,ConfID,ProtType,UserID,WR,ExecutionDate);
	}else
	{	//.print("-------------------  no encontrado ");
	    	.print("No participants have joined. Closing table...");
	 	.send(Sn, achieve,closeTable(Table));
	 	.send(Sn,achieve,finishedRound(Me));
	}.


		
//DECISION
+!decideStartSubprotocol(TableID,WMarketID,ConfID,ProtType,UserID,WR,ExecutionDate,Reply)
<- if ((newmemberjoined(TableID,WMarketID,ConfID,ProtType,UserID,WR,MissingParticipants,ExecutionDate))&(MissingParticipants==0)){
		.print("Action:START NEGOTIATION | Input:[Protocol:japanese_auction,Table:",TableID,"] | Output:Accepted");
		Reply=true;
	}
	else {	Reply=false;}.

+!joined(Result).

+!joined(Me,TableSend,"REFUSE").



  {include("wu_performance2.asl") }
