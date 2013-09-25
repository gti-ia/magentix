// Agent webCommParticipant in project MWaterWeb


/* Initial beliefs and rules */
webConversationID(participantwebconv).
webInterfaceAgName(webInterfaceAgent).

/* Initial goals */

!start.

/* Plans */

+!start:webConversationID(ConvID)&.my_name(Me)
<- .print(Me, " joined to plataform. Waiting for web requests...");
   +webcommunication(true);
   .ia_web_request("joinconversation").
/*--------------------accreditation--------------------------*/

+!startRound(CurrDate)
<- .abolish(tableRoundCreated(_)).

+!sendfinishedround:staffname(Sn)
<- .send(Sn,achieve,finishedRound(Me)).

+!request("accreditation",UsrName,CurrRol, CurrWRID,WMarket,[D,M,Y],ConvID)
<-  		//+request("accreditation",UsrName,CurrRol, CurrWRID,WMarket,[D,M,Y],ConvID);
   /*	if (not (accredited(User,WMarket)&userInfo_Arguments_List(User,UsrFieldsList,UserID)&
   			.member(name(UsrName),UsrFieldsList))) //no esta acreditado ya
   		{  */
   			.print("Requesting staff for accreditation ...");
   			!accredit(UsrName,WMarket).
   		//}.  


//This belief indicates that the accreditation has finished successfully
//accredited(water_user(id(3),name("AGiret"),user_type(4),seller_timeout(0),seller_price(16),seller_percentage(0.2),seller_wt(22),seller_th(0.75),buyer_bid(0.35),buyer_enter(0.4),buyer_cont_enact(0.3)),water_market(id(1021),description("Water market for configuration 563"),version("1.0")))
@pacc[atomic]
+accredited(User,WMarket):
water_market_Arguments_List(WMarket,WMFieldsList,WMID)&
userInfo_Arguments_List(User,UsrFieldsList,UserID)&
.member(name(UsrName),UsrFieldsList)&
request("accreditation",UsrName,CurrRol, CurrWRID,"",[D,M,Y],ConvID)
<- +accredited(User,WMarket,CurrRol);
	.abolish(request("accreditation",UsrName,CurrRol, _,_,_,_));
   //-request("accreditation",UsrName,CurrRol, CurrWRID,"",[D,M,Y],ConvID);
   +request("accreditation",UsrName,CurrRol,CurrWRID,WMID,[D,M,Y],ConvID);
   //+request("accreditation",UsrName,CurrRol, CurrWRID,WMID,[D,M,Y]);
   !manageAccrediteUserWithRol(UsrName,CurrRol,CurrWRID,[D,M,Y]).//,ConvID).

+accredited(A,B)
<- print("ACCREDITED RECIBIDO!!!!").

+!manageAccrediteUserWithRol(UsrName,Rol,WRID,[D,M,Y]):
accredited(User,WMarket)&
userInfo_Arguments_List(User,UsrFieldsList,UserID)&
water_market_Arguments_List(WMarket,WMFieldsList,WMID)&
.member(name(UsrName),UsrFieldsList)
<- //.print("Dentro de manageAccrediteUserWithRol");
	if (.ground(WRID)){NewWRID=WRID;}else{NewWRID=[]};
    if (Rol="seller"){  
   	//The next must be asket to the satff
   	    ?staffname(Sn);
   	    .send(Sn,askOne,japaneseauc_protocolparameters(ProtParam),ResultPP);
   	    if (ResultPP=japaneseauc_protocolparameters(ProtParam))
   	    { .member(max_iterations(MaxIt),ProtParam);
   	      .member(increment(Increm),ProtParam);
   	      .member(initial_bid(IniBid),ProtParam);
   	      .member(protocol_type(ProtType),ProtParam);
   	      .member( num_participants(NPart),ProtParam);
		}else{MaxIt=8; Increm=1; IniBid=1; ProtType=4; NPart=3;}
		.concat([num_iter_until_agreem(MaxIt)],[num_participants(NPart)],[opening_user(UserID)],[protocol_type(ProtType)],[role_when_opening_table("Seller")],AttributesList);
        .print(" - Verifiying the creation of a new trading table for WRID ",NewWRID," in market ",WMID);
       
	if (not tableRoundCreated([D,M,Y])){ !createNewNTT(AttributesList,[NewWRID],"Seller",NPart,ProtType,WMID,_);}
	else {  !!getTradingTables(UsrName,[D,M,Y],WMID); }
	+tableRoundCreated([D,M,Y]);
    }else{.print(" - Searching active trading tables in market ",WMID,".");
   		!getTradingTables(UsrName,[D,M,Y],WMID);}.


+!getTradingTables(UsrName,[D,M,Y],WMID): 
accredited(User,WM)& 
water_market_Arguments_List(WM,WMFieldsList,WMID)& 
userInfo_Arguments_List(User,UFieldsList,UserID)& 
.member(name(UsrName),UFieldsList)
<- 	.print("Accreditation process finished!  User ",UsrName);
	?staffname(Sn);
	.send(Sn,askOne,tablesinvitedto(WMID,UserID,[D,M,Y],InvitationsList),Reply);
	if (Reply==false){Invitations = [];}
	else{Reply = tablesinvitedto(WMID,UserID,[D,M,Y],Invitations);}

	.send(Sn,askOne,tablesselling(WMID,UserID,[D,M,Y],TTSellingList),SReply);
	if (SReply==false){TTSelling = [];}
	else{SReply = tablesselling(WMID,UserID,[D,M,Y],TTSelling);}

	.send(Sn,askOne,tablesbuying(WMID,UserID,[D,M,Y],TTBuyingList),BReply);
	if (BReply==false){TTBuying = [];}
	else{BReply = tablesbuying(WMID,UserID,[D,M,Y],TTBuying);}

	 Result = result(Invitations,TTSelling,TTBuying,WMID);

	?webInterfaceAgName(WIAgName);
	?myname(MyName);
	.send(WIAgName,tell,accreditationresult(Result,MyName));
	
	.abolish(request("accreditation",UsrName,CurrRol, CurrWRID,WMID,[D,M,Y],_)).


//This belief indicates that the accreditation has finished successfully
/*+!getTradingTables(UsrName,[D,M,Y],WMID)://,ConvID):
//request("accreditation",UsrName,CurrRol, CurrWRID,[D,M,Y],WMID)//,ConvID)
accredited(User,WM)&
water_market_Arguments_List(WM,WMFieldsList,WMID)&
userInfo_Arguments_List(User,UFieldsList,UserID)&
.member(name(UsrName),UFieldsList)
<- //recovering trading tables informationWMarket;
	.print("Accreditation process finished!  User ",UsrName);
	TradingHall = WMID;
   !getOpenNTTList(OpenNTTList,[D,M,Y],WMID,TradingHall).

+openNTTqueryended([D,M,Y],WMarketID,THallID):
accredited(User,WaterMarket)&
water_market_Arguments_List(WaterMarket,WMFieldsList,WMarketID)&
userInfo_Arguments_List(User,UsrFieldsList,UserID)&
.member(name(UsrName),UsrFieldsList)
//request("accreditation",UsrName,CurrRol, CurrWRID,WMarketID,[D,M,Y],ConvID)
<- 	.print("Open negociation trading tables process finished!");
	?openNTT(OpenNTTList,[D,M,Y],WMarketID,THallID);


	?staffname(Sn);
	.send(Sn,askOne,tablesinvitedto(WMarketID,UserID,[D,M,Y],InvitationsList),Reply);
	if (Reply==false){Invitations = [];}
	else{Reply = tablesinvitedto(WMarketID,UserID,[D,M,Y],Invitations);}

	.send(Sn,askOne,tablesselling(WMarketID,UserID,[D,M,Y],TTSellingList),SReply);
	if (SReply==false){TTSelling = [];}
	else{SReply = tablesselling(WMarketID,UserID,[D,M,Y],TTSelling);}

	.send(Sn,askOne,tablesbuying(WMarketID,UserID,[D,M,Y],TTBuyingList),BReply);
	if (BReply==false){TTBuying = [];}
	else{BReply = tablesbuying(WMarketID,UserID,[D,M,Y],TTBuying);}

	// Invitations = OpenNTTList;
	
	 Result = result(Invitations,TTSelling,TTBuying,WMarketID);
	 //.print("List of tables received. Result: ",Result);

	?webInterfaceAgName(WIAgName);
	?myname(MyName);
	.send(WIAgName,tell,accreditationresult(Result,MyName));
	
	//.ia_web_request("inform","accreditation",Result,ConvID);
	-openNTTqueryended(_,WMarketID,THallID);
	.abolish(request("accreditation",UsrName,CurrRol, CurrWRID,WMarketID,[D,M,Y],_)).*/

/*-----------------------tradinghall-----------------------*/
//request("tradinghall","BAlfonso",1014,5,"buyer","qpid://interfaceAgent58@localhost:8080")[source(self)]
+request("tradinghall",UserName,WMarketID,TableID,ConvID)://falta agragar en recruited participant si no está
accredited(User,WM)&
water_market_Arguments_List(WM,WMFieldsList,WMarketID)&
userInfo_Arguments_List(User,UFieldsList,UserID)&
.member(name(UserName),UFieldsList)
<- 	
	?table_structure(Table);
	?table_Arguments_List(Table,TFieldsList,TableID);
	.member(wmarket(WMarketID),TFieldsList);
	?staffname(Sn);
	.send(Sn,askOne,get_trading_table(Table),TableReply); 
	if (TableReply\==false){
		TableReply = get_trading_table(Table);
		.print("Table received ",Table);	
		!joinNTT(Table,"");
	}.


+request("tradinghallstate",UserName,WMarketID,TableID,ConvID)://para cuando solo se quiere consultar la mesa
accredited(User,WM,Rol)&
water_market_Arguments_List(WM,WMFieldsList,WMarketID)&
userInfo_Arguments_List(User,UFieldsList,UserID)&
.member(name(UserName),UFieldsList)
<- 	
	?table_structure(Table);
	?table_Arguments_List(Table,TFieldsList,TableID);
	.member(wmarket(WMarketID),TFieldsList);
	?staffname(Sn);
	.send(Sn,askOne,get_trading_table(Table),TableReply); 
	.send(Sn,askOne,tableownerwaterrights(WMarketID,TableID,WRList),Reply);
	if (Reply==false)
		{WRights = [];}else{Reply=tableownerwaterrights(WMarketID,TableID,WRList); WRights = WRList;}
	TableReply = get_trading_table(Table);
	.ia_web_request("inform","tradinghall",result(Table,WRights,Rol,"true"),ConvID).


+!joined(UserName,Table,"REFUSE"): //.print("TABLE REFUSED")&
table_Arguments_List(Table,TFieldsList,TableID)&
request("tradinghall",UserName,WMarketID,TableID,ConvID)& 
accredited(User,WMarket,Rol)&
userInfo_Arguments_List(User,UFieldsList,UserID)&
.member(name(UserName),UFieldsList)
<- .print("User ",UserName," could  not join to table ",TableID," in market ",WMarketID," Rol ",Rol);
	?staffname(Sn);

	if (Rol=="seller")
	{	
		.send(Sn,askOne,waterrightsasseller(UserID,WMarketID,TableID,WRList),Reply);
		if (Reply==false)
		{WRights = []; }
		else
		{ Reply=waterrightsasseller(UserID,WMarketID,TableID,WRList); WRights = WRList;}
	}else{
		if (Rol=="buyer"){
			.send(Sn,askOne,tableownerwaterrights(WMarketID,TableID,WRList),Reply);
			if (Reply==false)
			{WRights = [];}
			else
			{Reply=tableownerwaterrights(WMarketID,TableID,WRList); WRights = WRList;}
		}else{WRights = [];}
	}

	.ia_web_request("inform","tradinghall",result(Table,WRights,Rol,"false"),ConvID);
	//-joined(UserName,TableID,"REFUSE");
	-request("tradinghall",UserName,WMarketID,TableID,ConvID).

	
+!joined(Result): //Result is a literal "participant(RPart,Table,Rol)"
request("tradinghall",UserName,WMarketID,TableID,ConvID)& 
accredited(User,WM)&
water_market_Arguments_List(WM,WMFieldsList,WMarketID)&
userInfo_Arguments_List(User,UFieldsList,UserID)&
.member(name(UserName),UFieldsList)
<-	.print("User ",UserName," has join to table ",TableID," in market ",WMarket);
	?staffname(Sn);
	Result = participant(RPart,Table,Rol);
	if (Rol=="seller")
	{	.send(Sn,askOne,waterrightsasseller(UserID,WMarketID,TableID,WRList),Reply);
		if (Reply==false)
		{WRights = [];}
		else
		{Reply=waterrightsasseller(UserID,WMarketID,TableID,WRList); WRights = WRList;}
	}else{
		if (Rol=="buyer"){
			.send(Sn,askOne,tableownerwaterrights(WMarketID,TableID,WRList),Reply);
			if (Reply==false)
			{WRights = [];}
			else
			{Reply=tableownerwaterrights(WMarketID,TableID,WRList); WRights = WRList;}
		}else{WRights = [];}
	}
	
	.ia_web_request("inform","tradinghall",result(Table,WRights,Rol,"true"),ConvID);
	//-joined(Result);
	-request("tradinghall",UserName,WMarketID,TableID,ConvID).

/*--------------------new table--------------------------*/
//createNewNTT(AttributesList,WaterRightIDs,Rol,Participants,Protocol,MWaterMarket,THall) 	
/*+request("newtable",WrIdsList,Rolwopening,Participants,Protocol,MWaterMarket,THall,ConvID)
<- AttList = [];
   createNewNTT(AttList,WrIdsList,Rolwopening,Participants,Protocol,MWaterMarket,THall).*/

@pwebcommtablecreated[atomic]
+tablecreated(ResultTable):
request("accreditation",UsrName,CurrRol, CurrWRID,WMID,[D,M,Y],ConvID)
<-  ?table_Arguments_List(ResultTable,TFieldsList,TableID);
    .print(" - The table ",TableID," has been created for my water right!");
	.abolish(tablecreated(_));
	!getTradingTables(UsrName,[D,M,Y],WMID).

+tablenotcreated:request("accreditation",UsrName,CurrRol, CurrWRID,WMID,[D,M,Y],ConvID)
<- -tablenotcreated;
	!getTradingTables(UsrName,[D,M,Y],WMID).


/*-------------------auction-------------------------*/   
/*-----------------seller-----------------------------*/



+!waitForParticipantsToJoinTable(Table):staffname(Sn)&myname(Me)
<- 
    //!getSubprotocolTimeOut(initiator,TO);
	.send(Sn,askOne,invitationAcceptanceDeadline(TO),ReplyTO);
	if (ReplyTO==invitationAcceptanceDeadline(TO)){ReplyTO=invitationAcceptanceDeadline(TO); NewTO=TO;}
	else {    NewTO = 50000 ;};
	.wait(NewTO);
	?table_structure(Table);
	?table_Arguments_List(Table,TableArgList,TableID);
	.member(wmarket(WMarket),TableArgList);
	.member(configuration_id(ConfID),TableArgList);

	if (newmemberjoined(TableID,WMarket,ConfID,ProtType,UserID,WR,MissingParticipants,ExecutionDate))  //there is at least one participant
	{ !!evaluateStartSubProt(TableID,WMarket,ConfID,ProtType,UserID,WR,ExecutionDate);	}
	else
	{	//.print("-------------------  no encontrado ");
	    .print("No participants have joined. Closing table...");
	    .send(Sn, achieve,closeTable(Table));
	    .send(Sn,achieve,finishedRound(Me));
	}.
   

+!evaluateStartSubProt(TableID,WMarket,ConfID,ProtType,UserID,WR,ExecutionDate):staffname(Sn)&myname(Me)
<-  //.print(" - All required member joined to my table ",TableID);
   !decideStartSubprotocol(TableID,WMarket,ConfID,ProtType,UserID,WR,ExecutionDate,SPReply) ; //this must be included in the web interface
    if (SPReply=true)
   {
	.send(Sn,askOne,japaneseauc_protocolparameters(ProtParam),ResultPP); //.print("ResultPP ",ResultPP);
   	if (ResultPP=japaneseauc_protocolparameters(ProtParam))
   	    { .member(max_iterations(MaxIt),ProtParam);
   	      .member(increment(Increm),ProtParam);
   	      .member(initial_bid(IniBid),ProtParam);
		}else{MaxIt=8; Increm=1; IniBid=1; }

	if ( (WR\==[]) & (WR\==[""] ) ){ ?waterRight_Arguments_List(WR,WRFieldsList,WRID) ; }
	else{  WRFieldsList=[];  }
   	.concat([rol(seller)],[water_right_fields(WRFieldsList)],[tableid(TableID)],[wmarket(WMarket)],[confid(ConfID)],[max_iterations(MaxIt)],[increment(Increm)],[initial_bid(IniBid)],ProtocolParameters);
   	!startSubprotocol(ProtType,ProtocolParameters)
   }else{ //I don't want to start a subprotocol
	   ?table_structure(Table);
	   ?table_Arguments_List(Table,TableArgList,TableID);
	   .member(wmarket(WMarket),TableArgList);
	   .member(configuration_id(ConfID),TableArgList);
	   .print("Condition for starting not fulfilled. Closing table...");
	   .send(Sn, achieve,closeTable(Table));
	   .send(Sn,achieve,finishedRound(Me));
   };
   .abolish(newmemberjoined(TableID,WMarket,ConfID,ProtType,_,_,_,ExecutionDate)) .


+!decideStartSubprotocol(TableID,WMarket,ConfID,ProtType,UserID,WR,ExecutionDate,Reply)
<- if ((newmemberjoined(TableID,WMarket,ConfID,ProtType,UserID,WR,MissingParticipants,ExecutionDate))&(MissingParticipants==0)){
		.print("Starting subprotocol in table ",TableID);
		Reply=true;
	}
	else {	Reply=false;}.   	
/*-----------------buyer-----------------------------*/

+callforbid(Sender,PRequest,RemainingParticipants,Bid,SubProtConvID):
ProtName = "japanese_auction" &protocol_request(ProtName,Protocol, PRequest, ParamList)&
webcommunication(true)
<-  	//.print("//// PRequest: ",PRequest);
        
    	.member(water_right(WRight),ParamList);
    	.time(H,M,S);
    	.print("Call for bid received! Bid: ",Bid);//," Time: ",H,".",M,".",S);
    	.member(table(Table),ParamList);
		?table_Arguments_List(Table,TableFieldsList,TableID);
    	.member(wmarket(WMarket),TableFieldsList);
    	?waterRight_Arguments_List(WRight,WRFieldsList,WRID);
    	?staffname(Sn);
    	.send(Sn,askOne,subprotocolConvIDLiteral(SubProtConvID,Prot,ConvID),Reply);
    	Reply=subprotocolConvIDLiteral(SubProtConvID,Prot,ConvID);
    	//-+inauction(WRID,TableID,WMarket,Protocol,Bid,RemainingParticipants,ConvID);
    	//BB=inauction(WRID,TableID,WMarket,Protocol,Bid,RemainingParticipants,SubProtConvID);
    	//.print("Agregado in auction ",BB);

    	+timetoacceptPrice(WRID,TableID,WMarket,Protocol,Bid,RemainingParticipants,SubProtConvID,ConvID);
    	-callforbid(Sender,PRequest,RemainingParticipants,Bid,SubProtConvID). //ontology: there must be a plan in the agent for this

@pwebrequest[atomic]
+request("auctionstate",WRID,TableID,WMarket,Protocol,UserName,ConvID,ExternConvID):staffname(Sn)
<- //.print("Recibido request ConvID: ",ConvID);
   if ( (  (winner(Sender,ProtocolRequest,FinalBid,SubProtConvID))|
   		   (auctionsummary(Sender,Protocolid,ProtName,TableID,WRID,ThereWasWinner,Winner,WinnerBid,SubProtConvID) ))& //OJO: cdo no hay WR no se q pasa
	    protocol_request("japanese_auction",Protocolid, ProtocolRequest, PReqParamList)&
	    ProtName == "japanese_auction" &
	    .member(table(Table),PReqParamList)& 
	    .member(water_right(WR),PReqParamList)
	  )
	{	

	   if (winner(Sender,ProtocolRequest,FinalBid,SubProtConvID))
	   {	?table_Arguments_List(Table,TFieldsList,TableID);
	   		Agreement = agreement(WR,UserName,Sender,FinalBid,TableID);
			?water_right_structure(WR);
			?waterRight_Arguments_List(WR,WRFieldsList,WR-ID);
			.member(owner(OwnID),WRFieldsList);
			.send(Sn,askOne,username(OwnID,OwnName),UsrNReply);
			if (UsrNReply\==false){UsrNReply =username(OwnID,OwnName); WROwner=owner(OwnName);}
			else {WROwner=owner(OwnID);};
	  		.print("I'VE BEEN CHOOSEN AS WINNER. AGREEMENT! ",Agreement);
	  		.ia_web_request("inform","auctionstate",result(ConvID,0,"true",[UserName],WRID,Agreement,UserName,FinalBid),ExternConvID);
	  		.abolish(newmemberjoined(TableID,WMarket,ConfID,Protocolid,_,_,WR,_));
	  		.abolish(winner(Sender,_,_,SubProtConvID));
	   }else
	 {
	  	 if (auctionsummary(Sender,Protocolid,ProtName,TableID,WRID,true,Winner,WinnerBid,SubProtConvID))
	   	{  .print("I HAVEN'T BEEN CHOOSEN AS WINNER. WINNER WAS: ",Winner);
	   	   .ia_web_request("inform","auctionstate",result(ConvID,0,"true",[Winner],WRID,"",Winner,WinnerBid),ExternConvID);
	    	  .abolish(newmemberjoined(TableID,WMarket,ConfID,Protocolid,_,_,WR,_));
	    	  .abolish(auctionsummary(Sender,Protocolid,ProtName,TableID,_,true,Winner,WinnerBid,SubProtConvID));
	    	}else{
	  		 if (auctionsummary(Sender,Protocolid,ProtName,TableID,WRID,false,Winner,WinnerBid,SubProtConvID))
	   		{ .print("THERE WAS NOT WINNER!");
	   	  	.ia_web_request("inform","auctionstate",result(ConvID,0,"true",[],WRID,"","",""),ExternConvID);
	   	 	 .abolish(newmemberjoined(TableID,WMarket,ConfID,Protocolid,_,_,WR,_));
	    	 	.abolish(auctionsummary(Sender,Protocolid,ProtName,TableID,_,false,Winner,WinnerBid,SubProtConvID));
	  	 	}
			else{  //probably is the seller
			!manageTableClosedOrTableSeller(WRID,TableID,WMarket,Protocol,UserName,ConvID,ExternConvID);
			}
		}
	  }
	    
	}else{
		if  (timetoacceptPrice(WRID,TableID,WMarket,Protocol,Bid,RemainingParticipants,SubProtConvID,StrSubProtConvID)) //SubProtConvID: conv with seller
			{		
					if (request("bidup",GoOn,WRID,StrSubProtConvID,BidUpConvID) )
					{
						if (GoOn=true)
						{	
							.print("I'm agree with bid.");
    		 					!goonwithbidagree(SubProtConvID);
						}
						else
						{ //withdrawal
							.print("Withdrawal!!!");
    							!goonwithbidwithdrawal(SubProtConvID);
						};
						-request("bidup",GoOn,WRID,StrSubProtConvID,BidUpConvID) ;
						-timetoacceptPrice(WRID,TableID,WMarket,Protocol,_,_,SubProtConvID,StrSubProtConvID);
					}else{
						.print("Pending call for bid but no bidup...");
					}
					.ia_web_request("inform","auctionstate",result(StrSubProtConvID, Bid, "false", RemainingParticipants,WRID,"","",""),ExternConvID);
		}else{   //no winner and not time to accept price
			!manageTableClosedOrTableSeller(WRID,TableID,WMarket,Protocol,UserName,ConvID,ExternConvID);
		};
	};

	-request("auctionstate",WRID,TableID,WMarket,Protocol,UserName,ConvID,ExternConvID).

+!manageTableClosedOrTableSeller(WRID,TableID,WMarket,Protocol,UserName,ConvID,ExternConvID):staffname(Sn)
<- 	.print("REQUESTING AUCTION STATE...");
	//apaño para recuperar el closing date de la mesa y agreement si hubo

	.send(Sn,askOne,closedtableagreement(TableID,WMarket, Agr, Closed),ResultAgr);

	if (ResultAgr\==false)	
	{	ResultAgr = closedtableagreement(TableID,WMarket, Agr, Closed); 
		if (Closed=true){
			//LastParam="closed";
			if (Agr\==false){
				 Agr=agreement(WR,WinnerBuyer,SenderAgreement,FinalAgrBid,TableID); 
				.ia_web_request("inform","auctionstate",result(ConvID,0,"true",[WinnerBuyer],WRID,Agr,WinnerBuyer,"closed"),ExternConvID);
				.abolish(newmemberjoined(TableID,WMarket,ConfID,Protocolid,_,_,WR,_));
				.abolish(auctionsummary(Sender,Protocolid,ProtName,TableID,_,true,Winner,WinnerBid,SubProtConvID));
			}else{
				 .print("THERE WAS NOT WINNER IN THE CLOSED TABLE!");							 
			 .ia_web_request("inform","auctionstate",result(ConvID,0,"true",[],WRID,"","","closed"),ExternConvID);
			 .abolish(newmemberjoined(TableID,WMarket,ConfID,Protocolid,_,_,WR,_));
			 .abolish(auctionsummary(Sender,Protocolid,ProtName,TableID,_,false,Winner,WinnerBid,SubProtConvID));
			};
		}else
		{ .ia_web_request("inform","auctionstate",result(ConvID,0,"false",[],WRID,"","",""),ExternConvID);};
	
	}else
		{ .ia_web_request("inform","auctionstate",result(ConvID,0,"false",[],WRID,"","",""),ExternConvID);}.


		
/*-------------------bidup--------------------------*/
+request("bidup",GoOn,WRID,StrSubProtConvID,BidUpConvID) 
<-
if (timetoacceptPrice(WRID,TableID,WMarket,Protocol,Bid,RemainingParticipants,SubProtConvID,StrSubProtConvID))
	{	
		if (GoOn=true)
		{
		.ia_web_request("inform","bidup",result(StrSubProtConvID,Bid,"false",RemainingParticipants,WRID,"","",""),BidUpConvID);}
		else{
		.ia_web_request("inform","bidup",result(StrSubProtConvID,0,"false",[],WRID,"","",""),BidUpConvID);}
	}else{
		
		.ia_web_request("inform","bidup",result(StrSubProtConvID,0,"false",[],WRID,"","",""),BidUpConvID);
	}.

/*----------------------------------------------*/
/*
+winner(Sender,PRequest,FinalBid,SubProtConvID)
<- .print("AÑADIDO WINNER!!!!") .*/
	
	{ include("../mwaterJasonAgents/wu.asl") }
