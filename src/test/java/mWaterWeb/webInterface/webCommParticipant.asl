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

+request("accreditation",UsrName,WMarket,ConvID)
<- .print("Requesting staff for accreditation ...");
   !accredit(UsrName,WMarket).  


//This belief indicates that the accreditation has finished successfully
+accredited(User,WMarket):
water_market_Arguments_List(WMarket,WMFieldsList,WMID)&
userInfo_Arguments_List(User,UsrFieldsList,UserID)&
.member(name(UsrName),UsrFieldsList)&
request("accreditation",UsrName,"",ConvID)
<- -request("accreditation",UsrName,_,_);//,ConvID);
   +request("accreditation",UsrName,WMID);
   !getTradingTables(UsrName,WMID).//,ConvID).

//This belief indicates that the accreditation has finished successfully
+!getTradingTables(UsrName,WMID)://,ConvID):
request("accreditation",UsrName,WMID)//,ConvID)
<- //recovering trading tables informationWMarket;
	.print("Accreditation process finished!  User ",UsrName);
	TradingHall = WMID;
   !getOpenNTTList(OpenNTTList,WMID,TradingHall).

+openNTTqueryended(WMarketID,THallID):
accredited(User,WaterMarket)&
water_market_Arguments_List(WaterMarket,WMFieldsList,WMarketID)&
userInfo_Arguments_List(User,UsrFieldsList,UserID)&
.member(name(UsrName),UsrFieldsList)&
request("accreditation",UsrName,WMarketID)//,ConvID)
<- 	.print("Open negociation trading tables process finished!");
	?openNTT(OpenNTTList,WMarketID,THallID);
	.print("List of open trading tables received: ",OpenNTTList);
	?staffname(Sn);
	.send(Sn,askOne,tablesinvolved(WMarketID,UserID,TTInvolved),Reply);

	if (Reply==false){TTInvolved = [];}
	else{Reply = tablesinvolved(WMarketID,UserID,TTInvolved);}

	 Invitations = OpenNTTList;
	.print("List of invitations received: ",Invitations);
	 Result = result(Invitations,TTInvolved,WMarketID);
	.print("Result of accreditation for user ", UsrName,": ",Result);
	?webInterfaceAgName(WIAgName);
	?myname(MyName);
	.send(WIAgName,tell,accreditationresult(MyName,Result));
	
	//.ia_web_request("inform","accreditation",Result,ConvID);
	-openNTTqueryended(WMarketID,THallID);
	-request("accreditation",UsrName,WMarketID).

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
	
+joined(Result): //Result is a literal "participant(RPart,Table,Rol)"
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
	
	.ia_web_request("inform","tradinghall",result(Table,WRights,Rol),ConvID);
	-joined(Result);
	-request("tradinghall",UserName,WMarketID,TableID,ConvID).

/*--------------------new table--------------------------*/
//createNewNTT(AttributesList,WaterRightIDs,Rol,Participants,Protocol,MWaterMarket,THall) 	
+request("newtable",WrIdsList,Rolwopening,Participants,Protocol,MWaterMarket,THall,ConvID)
<- AttList = [];
   createNewNTT(AttList,WrIdsList,Rolwopening,Participants,Protocol,MWaterMarket,THall).
   
+tablecreated(ResultTable):request("newtable",WrIdsList,Rolwopening,Participants,Protocol,MWaterMarket,THall,ConvID)
<- -tablecreated(ResultTable);
  .ia_web_request("inform","newtable",result("true"),ConvID);
  -request("newtable",WrIdsList,Rolwopening,Participants,Protocol,MWaterMarket,THall,ConvID).

+tablenotcreated:request("newtable",WrIdsList,Rolwopening,Participants,Protocol,MWaterMarket,THall,ConvID)
<- -tablenotcreated;
   .ia_web_request("inform","newtable",result("false"),ConvID);
   -request("newtable",WrIdsList,Rolwopening,Participants,Protocol,MWaterMarket,THall,ConvID).

/*-------------------auction-------------------------*/   
/*-----------------seller-----------------------------*/

+memberjoined(Table,UserID,WR,StartAuction)
<- //what i do as seller
   //.print("//////////////////////////// MEMBER JOINED!!! ",UserID);
  ?table_Arguments_List(Table,FieldsList,TableID);
   if (StartAuction==true) 
   {
	.abolish(memberjoined(Table,_,_));
	.member(protocol_type(Prot),FieldsList);
	.print("******** Enough users to start subprotocol. Starting ... ");
	if (WR\==[]){ ?waterRight_Arguments_List(WR,WRFieldsList,WRID)  }else{  WRFieldsList=[];  }
	//.print("**** WR: ",WR);
   	.concat([rol(seller)],[water_right_fields(WRFieldsList)],[table(Table)],[max_iterations(12)],[increment(5)],[initial_bid(1)],ProtocolParameters);
   	!startSubprotocol(Prot,ProtocolParameters);
   	
   }.
/*-----------------buyer-----------------------------*/

+callforbid(Sender,PRequest,RemainingParticipants,Bid,SubProtConvID):
ProtName = "japanese_auction" &protocol_request(ProtName,Protocol, PRequest, ParamList)&
webcommunication(true)
<-  	//.print("//// PRequest: ",PRequest);
        
    	.member(water_right(WRight),ParamList);
    	.time(H,M,S);
    	.print("Call for bid received! Bid: ",Bid," WRight: ",WRight);//," Time: ",H,".",M,".",S);
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
+request("auctionstate",WRID,TableID,WMarket,Protocol,UserName,ConvID,ExternConvID)
<- //.print("Recibido request ConvID: ",ConvID);
   if ( (  (winner(Sender,ProtocolRequest,FinalBid,SubProtConvID))|
   		   (auctionsummary(Sender,ProtName,TableID,WRID,ThereWasWinner,Winner,WinnerBid,SubProtConvID) ))& //OJO: cdo no hay WR no se q pasa
	    protocol_request("japanese_auction",Protocolid, ProtocolRequest, PReqParamList)&
	    ProtName == "japanese_auction" &
	    .member(table(Table),PReqParamList)&
	    .member(water_right(WR),PReqParamList)&
	    table_Arguments_List(Table,TFieldsList,TableID)
	  )
	{	
	  //AA = winner(Sender,ProtocolRequest,FinalBid,SubProtConvID);
 	 // .abolish(inauction(WRID,TableID,WMarket,Protocol,_,_,SubProtConvID));
	   if (winner(Sender,ProtocolRequest,FinalBid,SubProtConvID))
	   {	Agreement = agreement(WR,UserName,Sender,FinalBid,TableID);
	  		.print("I'VE BEEN CHOOSEN AS WINNER. AGREEMENT! ",Agreement);
	  		.ia_web_request("inform","auctionstate",result(ConvID,0,"true",[UserName],WRID,Agreement,UserName,FinalBid),ExternConvID);
	  		.abolish(memberjoined(Table,_,WR,_));
	  		.abolish(winner(Sender,_,_,SubProtConvID));
	   };
	   if (auctionsummary(Sender,ProtName,TableID,WRID,true,Winner,WinnerBid,SubProtConvID))
	   {  .print("I HAVEN'T BEEN CHOOSEN AS WINNER. WINNER WAS: ",Winner);
	      .ia_web_request("inform","auctionstate",result(ConvID,0,"true",[Winner],WRID,"",Winner,WinnerBid),ExternConvID);
	      .abolish(memberjoined(Table,_,WR,_));
	      .abolish(auctionsummary(Sender,ProtName,TableID,_,true,Winner,WinnerBid,SubProtConvID));
	    };
	   if (auctionsummary(Sender,ProtName,TableID,WRID,false,Winner,WinnerBid,SubProtConvID))
	   { .print("THERE WAS NOT WINNER!");
	     .ia_web_request("inform","auctionstate",result(ConvID,0,"true",[],WRID,"","",""),ExternConvID);
	     .abolish(memberjoined(Table,_,WR,_));
	     .abolish(auctionsummary(Sender,ProtName,TableID,_,false,Winner,WinnerBid,SubProtConvID));
	   };
	    //.print("@@@ No winner y No auction summary...");
	    
	}else{
		if  (timetoacceptPrice(WRID,TableID,WMarket,Protocol,Bid,RemainingParticipants,SubProtConvID,StrSubProtConvID)) //SubProtConvID: conv with seller
			{		//.print("ANTES Time to accept price : StrSubProtConvID: ",StrSubProtConvID);
					
					if (request("bidup",GoOn,WRID,StrSubProtConvID,BidUpConvID) )
					{
					//if (GoOn=true){.print("GoOn=true");}else{.print("GoOn=false");}
					//.print("DESPUES Time to accept price : StrSubProtConvID: ",StrSubProtConvID);
					
						if (GoOn=true)
						{	//.ia_web_request("inform","bidup",result(StrSubProtConvID,Bid,"false",RemainingParticipants,WRID,""),BidUpConvID);
							//.abolish(inauction(WRID,TableID,WMarket,Protocol,_,_,SubProtConvID));
							.print("I'm agree with bid.");
							
    		 				!goonwithbidagree(SubProtConvID);
    		 				//.ia_web_request("inform","auctionstate",result(StrSubProtConvID,Bid,"false",RemainingParticipants,WRID,"","",""),ExternConvID);				
						}
						else
						{ //withdrawal
							//.ia_web_request("inform","bidup",result(StrSubProtConvID,Bid,"true",RemainingParticipants,WRID,""),BidUpConvID);
							//.abolish(inauction(WRID,TableID,WMarket,Protocol,_,_,SubProtConvID));
							.print("Withdrawal!!!");
    						!goonwithbidwithdrawal(SubProtConvID);
    						//.ia_web_request("inform","auctionstate",result(StrSubProtConvID,Bid,"false",RemainingParticipants,WRID,"","",""),ExternConvID);
						};
						-request("bidup",GoOn,WRID,StrSubProtConvID,BidUpConvID) ;
						-timetoacceptPrice(WRID,TableID,WMarket,Protocol,_,_,SubProtConvID,StrSubProtConvID);
					}else{
						.print("Pending call for bid but no bidup...");
						//.ia_web_request("inform","auctionstate",result(StrSubProtConvID,Bid,"false",RemainingParticipants,WRID,"","",""),ExternConvID);
					}
					.ia_web_request("inform","auctionstate",result(StrSubProtConvID,Bid,"false",RemainingParticipants,WRID,"","",""),ExternConvID);
			}else{   //no winner and not time to accept price
			/*	
				if (inauction(WRID,TableID,WMarket,Protocol,Bid,RemainingParticipants,StrSubProtConvID))
				{	.print("@@@  No bids but in auction ...");
					.ia_web_request("inform","auctionstate",result(StrSubProtConvID,Bid,"false",RemainingParticipants,WRID,"","",""),ExternConvID);
				}else{*/
					.print("NOT AUCTION...");
					.ia_web_request("inform","auctionstate",result(ConvID,0,"false",[],WRID,"","",""),ExternConvID);
				//};
			};
	};

	-request("auctionstate",WRID,TableID,WMarket,Protocol,UserName,ConvID,ExternConvID).
		
/*-------------------bidup--------------------------*/
+request("bidup",GoOn,WRID,StrSubProtConvID,BidUpConvID) 
<-// if (inauction(WRID,TableID,WMarket,Protocol,Bid,RemainingParticipants,StrSubProtConvID)))
if (timetoacceptPrice(WRID,TableID,WMarket,Protocol,Bid,RemainingParticipants,SubProtConvID,StrSubProtConvID))
	{	//.print("@@@ going on with the bid up conversation inside the auction ... GoOn ",GoOn);
		if (GoOn=true)
		{//.print("ok"); 
		.ia_web_request("inform","bidup",result(StrSubProtConvID,Bid,"false",RemainingParticipants,WRID,"","",""),BidUpConvID);}
		else{//.print("not ok"); 
		.ia_web_request("inform","bidup",result(StrSubProtConvID,0,"false",[],WRID,"","",""),BidUpConvID);}
	}else{
		//.print("@@@  going on with the bid up conversation outside the auction...");
		.ia_web_request("inform","bidup",result(StrSubProtConvID,0,"false",[],WRID,"","",""),BidUpConvID);
	}.

/*----------------------------------------------*/
/*
+winner(Sender,PRequest,FinalBid,SubProtConvID)
<- .print("AÑADIDO WINNER!!!!") .*/
	
	{ include("../mwaterJasonAgents/wu.asl") }