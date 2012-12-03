// Agent performance_aut_ag in project magentix2JasonConv

/* Initial beliefs and rules */

/* Initial goals */

!start.


/* Plans */

+!start:myname(Me)
<- +contador(1);
   +mytables([]);
   .setlogfile("logs/agents.log"); //Form performance purposes only
   .print("Requesting staff for accreditation....");
   !accredit(Me,"").

+accredited(User,WMarket):water_market_Arguments_List(WMarket,WMFieldsList,WMID)
<- .print("I'm accredited!");
	//Recovering trading tables involved
	?userInfo_Arguments_List(User,FieldsList,ID);
	.member(name(Name),FieldsList);
	!getTradingTablesInvolved(Name,WMID,ID).

//This belief indicates that the accreditation has finished successfully
+!getTradingTablesInvolved(UsrName,WMID,UserID)
<- //recovering trading tables informationWMarket;
	//.print("Getting trading tables involved!  User ",UsrName);
	TradingHall = WMID;
    //!getOpenNTTList(OpenNTTList,WMID,TradingHall).
    ?staffname(Sn);
    .send(Sn,askOne,tablesinvolved(WMID,UserID,TTInvolved),Reply);
    if (Reply==false){TTInvolved = [];}
	else{Reply = tablesinvolved(WMID,UserID,TTInvolved);};
	?table_structure(Table);
	?table_Arguments_List(Table,TableFieldsList,TableID);
	.member(opening_user(UserID),TableFieldsList);
	if (.member(Table,TTInvolved))
		{?mytables(MyTbls);
		 .concat([Table],MyTbls,MyNewTbls);
		 -+mytables(MyNewTbls);}.


+allmemberjoined(Table,UserID,WR,StartAuction)
<- //what i do as seller
  ?table_Arguments_List(Table,FieldsList,TableID);
   if (StartAuction==true) 
   {
	//.abolish(memberjoined(Table,_,_));
	.member(protocol_type(Prot),FieldsList);
	.print("******** Enough users to start subprotocol. Starting ... ");
	.concat("Starting- Table: ",TableID,Text);
	.ia_save_log(Text);
	if (WR\==[]){ ?waterRight_Arguments_List(WR,WRFieldsList,WRID)  }else{  WRFieldsList=[];  }
   	.concat([rol(seller)],[water_right_fields(WRFieldsList)],[table(Table)],[max_iterations(8)],[increment(5)],[initial_bid(1)],ProtocolParameters);
   	!startSubprotocol(Prot,ProtocolParameters);
   	
   }.


//+auctionsummary(Me,ProtName,TableID,WRID,ThereIsWinner,Winner,WinnerBid,SubProtConvID):.print("ccccc")
//<- if (ThereIsWinner){.print("I'VE BEEN THE WINNER!!!");}.


+?acceptPrice(WRight,TableID,WMarket,Protocol,Bid,Participants,Reply)
<-//.print("@@autom@@@ Recibido accept price!!! ",WRight); 
   .random(R);
	NewR = R * 100;
	if ((NewR > 50)|(NewR<20 ))
	{Reply = true}
	else{
	Reply = false;
	}.


	
	{ include("../mwaterJasonAgents/wu_performance.asl") }