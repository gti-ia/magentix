// Agent staff_performance in project magentix2JasonConv


 { include("belief_builder.asl") }

/* Initial beliefs and rules */
//possibleactions([accreditation,newNTTable,joinNTT]).
possibleactions(ActionsList):- 
		.findall(ReqAction,request(ReqAction,_,_),Requests)&
		.findall(QueryAction,query(QueryAction,_,_),Querys)&
		.concat(Requests,Querys,ActionsList).


/*request(accreditation,Request,ParamList):- 
		Request = accreditate(UserID,WMarket)*/

/* Conversations IDs */
accreditConvIDLiteral(accreditation(ConvID),ConvID).
openNTTConvIDLiteral(oNTTconv(ConvID),ConvID).
newNTTConvIDLiteral(newNTTconv(ConvID),ConvID).
joinNTTConvIDLiteral(joinNTTconv(Rol,ConvID),Rol,ConvID).
invitationConvIDLiteral(invitation(Requester,ConvID),Requester,ConvID).
subprotocolConvIDLiteral(subprotocol(Prot,ConvID),Prot,ConvID).

		
/* Rules */
allowedusers(L):-  user_structure(UserSt)&.findall(Name,username(UserSt,Name),L ).
accrediteduser(UserID,WMarketID):-  
		accredited_user_structure(AccUser)&
		accredited_user_Arguments_List(AccUser,FieldList,UserID,WMarketID)&
		AccUser.
username(User,Name):- 
		user_structure(User)&userInfo_Arguments_List(User,FieldsList,ID)& 
		.member(name(Name),FieldsList)&User.
currentConfigurationID(ID):-
		currentConfiguration(Conf)&
		configuration_Arguments_List(Conf,FieldsList,ID).


/* Fixes beliefs */

accreditationTimeOut(5000).
openNTTQueryTimeOut(5000).
newNTTTimeOut(7000).
joinNTTTimeOut(5000).
subprotocoljoinTimeOut(initiator,4000).
subprotocolTimeOut(initiator,15000).
subprotocolTimeOut(participant,50000).
invitationTimeOut(5000).
proposalsDeadLine(5000).



/* Plans */
//!createConfiguration("Web simulation","japanese_auction",10,[27,2,2012],[27,2,2012]). //the second argument is 
!setConfiguration(556,1014,1014).

/* ->  GENERAL PLANS  - */
//Plan for executing a previous configuration
+!setConfiguration(ConfID,WMarketID,THallID):configuration_structure(Conf)&configuration_Arguments_List(Conf,FieldsList,ConfID)
<- .setlogfile("logs/staff.log");//Form performance purposes only
   .member(id(ConfID),FieldsList);
   ?configuration_Arguments_List(Conf,FieldsList,ConfID);
   ?Conf;
   .print("EXECUTTING CONFIGURATION: ",ConfID);
   //ontology
   +currentConfiguration(Conf);
   -+currentWaterMarket_id(WMarketID);
   -+currentTradinghall_id(THallID).

//currentConfigurationID(ID)
+!createConfiguration(Desc,ProtocolDescription,Participants,IniDate,EndDate)
<- .date(Y,M,D);
/*Uncomment next lines just if the protocol id is going to be used instead the protocol description*/
  // ?protocol_type_structure(PType);
  // ?protocol_type_Arguments_List(PType,ProtFieldsList,ProtID);
  // .member(type_name(ProtocolDescription),ProtFieldsList);
   //?PType;

   +configuration(id(ID),description(Desc),simulation_date([D,M,Y]),
		negotiation_protocol(ProtocolDescription),group_selected(0),initial_date(IniDate),
		final_date(EndDate),seller_timeout(0),seller_price(0),seller_percentage(0.0),
		seller_wt(0),buyer_bid(0.0),buyer_enter(0.0),buyer_cont_enact(0.0),ba_agr_val(0),
		ba_entitlement(0),mf_cont_enact(0.0),mf_accred(0),seller_th(0.0));
		
	+currentConfiguration(configuration(id(ID),description(Desc),simulation_date([D,M,Y]),
		negotiation_protocol(Protocol),group_selected(0),initial_date(IniDate),
		final_date(EndDate),seller_timeout(0),seller_price(0),seller_percentage(0.0),
		seller_wt(0),buyer_bid(0.0),buyer_enter(0.0),buyer_cont_enact(0.0),ba_agr_val(0),
		ba_entitlement(0),mf_cont_enact(0.0),mf_accred(0),seller_th(0.0)));

	?water_market_structure(WMarket);
	?water_market_Arguments_List(WMarket,WMFieldsList,WMID);
	.concat("Water market for configuration ",ID,WMDesc);
	.member(description(WMDesc),WMFieldsList);
	.member(version("1.0"),WMFieldsList);
	+WMarket;
	-+currentWaterMarket_id(WMID);
	-+currentTradinghall_id(WMID);
	.print("EXECUTTING CONFIGURATION: ",ID," IN WATER MARKET ",WMID).

+?sellersoftable(TableID,WMarketID,Sellers)
<- ?table_structure(Table);
   ?table_Arguments_List(Table, TableFieldsList,TableID);
   .member(wmarket(WMarketID),TableFieldsList);
   ?currentConfigurationID(ConfID);
   .member(configuration_id(ConfID),TableFieldsList);
   ?Table;
   .member(opening_user(Seller),TableFieldsList);
   ?user_structure(S);
   ?userInfo_Arguments_List(S,SFields,Seller);
   ?S;
   .member(name(SName),SFields);
   Sellers = [SName].

   
+?buyersoftable(TableID,WMarketID,BuyersNames)
<- !membersNamesofNTTable(TableID,WMarketID,MembersList);
   ?sellersoftable(TableID,WMarketID,Sellers);
   .difference(MembersList,Sellers,BuyersNames).
   

+!membersNamesofNTTable(TableID,WMarketID,ResultList)
<- ?currentConfigurationID(ConfID);
   ?recruited_participant_structure(RPart);
   ?recruited_participant_Arguments_List(RPart,RPFieldsList,UsrID);
   .member(configuration_id(ConfID),RPFieldsList);
   .member(trading_table_id(TableID),RPFieldsList);
   .member(wmarket(WMarketID),RPFieldsList);
   .member(user_id(UsrID),RPFieldsList);
   //--------------
    ?user_structure(B);
   	?userInfo_Arguments_List(B,BFields,UsrID);
   	.member(name(BName),BFields);
	.findall(BName,B&RPart,ResultList).

   


@pgetnewNTTID[atomic]
+?getNewNTTID(ConfID,WMarket,NewID)//:openNTT(Tables)
<- 
   A = trading_table_new_id(configuration_id(ConfID),wmarket(WMarket),id(NewID));
   ?A. //query in db


@psubprotocolID[atomic]
+?subprotocolID(ID,Prot,Sender):subprotocolConvIDLiteral(ID,Prot,ConvID)
<-  .date(Y,Mo,D);
    .time(H,Mi,Seg);
    .concat("",Prot,".",Sender,".",Y,".",Mo,".",D,".",H,".",Mi,".",Seg,ConvID).
   

@paccreditationConvID[atomic]
+?accreditationConvID(ID,Sender):accreditConvIDLiteral(ID,ConvID)
<- 
   .date(Y,Mo,D);
   .time(H,Mi,Seg);
   .concat("acc.",Sender,".",Y,".",Mo,".",D,".",H,".",Mi,".",Seg,ConvID).
   
@popenNTTConvID[atomic]
+?openNTTConvID(ID,Sender):openNTTConvIDLiteral(ID,ConvID)
<- .date(Y,Mo,D);
   .time(H,Mi,Seg);
   .concat("onnt.",Sender,".",Y,".",Mo,".",D,".",H,".",Mi,".",Seg,ConvID).

@pnewNTTConvID[atomic]
+?newNTTConvID(WMarket,ID,Sender):newNTTConvIDLiteral(ID,ConvID)
<- .date(Y,Mo,D);
   .time(H,Mi,Seg);
   .concat("newnnt.",Sender,".",WMarket,".",Y,".",Mo,".",D,".",H,".",Mi,".",Seg,ConvID).
      
@pjoinNTTConvID[atomic]
+?joinNTTConvID(ID,Rol,Sender):joinNTTConvIDLiteral(ID,Rol,ConvID)
<- .date(Y,Mo,D);
   .time(H,Mi,Seg);
   .concat("jonnt.",Sender,".",Y,".",Mo,".",D,".",H,".",Mi,".",Seg,ConvID).

@pinvitationConvID[atomic]
+?invitationConvID(ID,Sender):invitationConvIDLiteral(ID,Sender,ConvID)
<- .date(Y,Mo,D);
   .time(H,Mi,Seg);
   .concat("inv.",Sender,".",Y,".",Mo,".",D,".",H,".",Mi,".",Seg,ConvID).

+!tablesActive([],TempList,Result)
<-  Result = TempList.
//ontology
+!tablesActive([FirstTable|Rest],TempList,Result)
<- ?table_Arguments_List(FirstTable,FieldsList,TableID);
   .member(closing_date(ClosingDate),FieldsList);
   .member(opening_date(OpeningDate),FieldsList);
   .date(Y,M,D);
   CurrDate = [D,M,Y];
   !compareDates(CurrDate,OpeningDate,Result1);
   if (ClosingDate\==[])
   	{!compareDates(ClosingDate,CurrDate,Result2);
   	if (  ((Result1==1)|(Result1==0)) & ((Result2==1)|(Result2==0))  ) //it is in the range
     {
     	.concat(TempList,[FirstTable],NewList);
      	!tablesActive(Rest,NewList,Result);
     }
     else
     {
     	!tablesActive(Rest,TempList,Result);
     }
    }
    else //ClosingDate = []
    {
    	.concat(TempList,[FirstTable],NewList);
      	!tablesActive(Rest,NewList,Result);
    }.
//ontology
+!openNTT(OpenNTTList,MWaterMarket,THall,ConfID)
<- 		
		?table_structure(Table);
		?table_Arguments_List(Table,FieldsList,TableID);
		.member(wmarket(MWaterMarket),FieldsList);
		//.member(closing_date([]),FieldsList);
		.member(th_id(THall),FieldsList);
		.member(configuration_id(ConfID),FieldsList);
		//.print("Dentro de openNTT query: ",Table);
		.findall(Table,Table,NTTList); //query in db
		//.print("Dentro de openNTT lista: ",NTTList);
		!tablesActive(NTTList,[],OpenNTTList). 
//ontology
+?pendinginvitations(TableID, WMarketID,UserID,PIList):currentConfigurationID(ConfID)
<- 	
	?recruited_participant_structure(RPart);
   	?recruited_participant_Arguments_List(RPart,RPFieldsList,UserID);
   	.member(wmarket(WMarketID),RPFieldsList);
   	.member(trading_table_id(TableID),RPFieldsList);
   	.member(configuration_id(ConfID),RPFieldsList);
   	.member(accepted(false),RPFieldsList);
   	.findall(RPart,RPart,PIList).


//ontology
+?tablesinvolved(WMarketID,UserID,TTInvitationList):currentConfigurationID(ConfID)
<- 	
	?recruited_participant_structure(RPart);
   	?recruited_participant_Arguments_List(RPart,RPFieldsList,UserID);
   	.member(wmarket(WMarketID),RPFieldsList);
   	.member(trading_table_id(TableID),RPFieldsList);
   	.member(configuration_id(ConfID),RPFieldsList);
   	.member(accepted(true),RPFieldsList);
	?table_structure(Table);
	?table_Arguments_List(Table,TFieldsList,TableID);
	.member(wmarket(WMarketID),TFieldsList);
	.member(configuration_id(ConfID),TFieldsList);
   	.findall(Table,RPart&Table,TTInvList);
	!tablesActive(TTInvList,[],TTInvitationList).
   	//.print("TTInvList: ",TTInvitationList).
//ontology   	
+?waterrightsasseller(UserID,WMarket,TableID,WRList):currentConfigurationID(ConfID)
<-  
	?waterright_tt_structure(WMTT);
	?waterright_tt_Arguments_List(WMTT,FieldsList,WRID,TableID);
	.member(wmarket(WMarket),FieldsList);
	.member(configuration_id(ConfID),FieldsList);
	?water_right_structure(WaterRight);
	?waterRight_Arguments_List(WaterRight,WRFieldsList,WRID);
	.member(owner(UserID),WRFieldsList);
	.findall(WaterRight,WaterRight&WMTT,WRList).

//ontology   	
+?tableownerwaterrights(WMarketID,TableID,WRList):currentConfigurationID(ConfID)
<-  
	?table_structure(Table);
	?table_Arguments_List(Table,TFieldsList,TableID);
	.member(wmarket(WMarket),TFieldsList);
	.member(configuration_id(ConfID),TFieldsList);
	?Table;
	?waterright_tt_structure(WMTT);
	?waterright_tt_Arguments_List(WMTT,FieldsList,WRID,TableID);
	.member(wmarket(WMarket),FieldsList);
	.member(configuration_id(ConfID),FieldsList);
	.member(opening_user(OpUsr),TFieldsList);
	?water_right_structure(WaterRight);
	?waterRight_Arguments_List(WaterRight,WRFieldsList,WRID);
	
	.member(owner(OpUsr),WRFieldsList);
	.findall(WaterRight,WaterRight&WMTT,WRList).
	//.print("///// tableownerwaterrights: ",WRList).

//ontology
+?get_trading_table(Table):currentConfigurationID(ConfID)
<- ?table_Arguments_List(Table,TFieldsList,TableID);
   .member(configuration_id(ConfID),TFieldsList);
   ?Table.
   	
/* -  GENERAL PLANS  <- */

/* ----------------------------------------------------------------*/

/* ->  ACREDITATION  - */

+?join(ConvID,frp,Action):
accreditConvIDLiteral(ConvID,_)&
accreditationTimeOut(TO)&
request(accreditation,Action,ParamList) &
.member(user_name(UserName),ParamList)&
.member(wmarket(WMarketID),ParamList)
//userInfo_Arguments_List(User,FieldsList,UserID)
<- 
   .ia_fipa_request_Participant("joinconversation",TO,ConvID).

+request(Sender,Content,Data,ConvID):accreditConvIDLiteral(ConvID,_)&
possibleactions(L)&not .member(Content,L)
<- .print("- I've received a request for doing: ",Content," but i don't understand. It is not inside the actions I can do.");
   -+taskStatus(Content,notunderstood,Sender,ConvID);
   .ia_fipa_request_Participant("notunderstood",ConvID).

+request(Sender,Content,Data,ConvID):Content=accreditation&accreditConvIDLiteral(ConvID,_)&
request(Content,Data,ParamList)&.member(user_name(UserName),ParamList)&.member(wmarket(WMarket),ParamList)//&
//userInfo_Arguments_List(User,FieldsList,UserID)
<- ?user_structure(User);
   ?userInfo_Arguments_List(User,UsrFieldsList,UserID);
   .concat("",UserName,StrUserName);
   .member(name(StrUserName),UsrFieldsList);
   ?User; //user must exist
   if (accrediteduser(UserID,WMarket))
	{
		.print("- I've received a request for doing: ",Content," but user ",UserName," is already accredited in this market.");
	   -+taskStatus(Content,refuse,Sender,ConvID);
	   .ia_fipa_request_Participant("agree",ConvID);
	   //.ia_fipa_request_Participant("refuse",ConvID);
	}else{
	//.print("-I've received a request for doing: ",Content," and i'm agree.");
 	  -taskStatus(Content,_,Sender,ConvID);
 	  +taskStatus(Content,agree,Sender,ConvID);
 	  .ia_fipa_request_Participant("agree",ConvID);
   }.
   
+request(Sender,Content,Data,ConvID):Content=accreditation&accreditConvIDLiteral(ConvID,_)
<- .print("- I've failed doing ",Content);
   -+taskStatus(Content,failure,Sender,ConvID);
   .ia_fipa_request_Participant("failure",Content,ConvID).
   
+timetodotask(Content,Data,ConvID):Content=accreditation&accreditConvIDLiteral(ConvID,_)&
taskStatus(Content,agree,Sender,ConvID)
<-// .print("- I'm going to make the task: ",ConvID);
   !doTask(Content,Sender,Data,ConvID);
   ?taskResult(Content,ConvID, Sender,R);
   -taskStatus(Content,agree,Sender,ConvID);
   
   .ia_fipa_request_Participant("inform",Content,R,ConvID).

+timetodotask(Content,Data,ConvID):Content=accreditation&accreditConvIDLiteral(ConvID,_)&
taskStatus(Content,refuse,Sender,ConvID)
<-  ?request(Content,Data,ParamList);
	.member(user_name(UserName),ParamList);
	.member(wmarket(WMarketID),ParamList);
	?user_structure(User);
   	?userInfo_Arguments_List(User,UsrFieldsList,UserID);
    .concat("",UserName,StrUserName);
    .member(name(StrUserName),UsrFieldsList);
    ?water_market_structure(WM);
    ?water_market_Arguments_List(WM,WMFieldsList,WMarketID);
    ?WM;
    if (User){
    	.ia_fipa_request_Participant("inform",Content,accredited(User,WM),ConvID);
    }else{
    	.ia_fipa_request_Participant("failure",Content,ConvID);
    }
    -taskStatus(Content,refuse,Sender,ConvID).
   
+timetodotask(Content,Data,ConvID):Content=accreditation&accreditConvIDLiteral(ConvID,_)
<- .print("- I've failed doing the task: ",Content);
	?taskStatus(Content,Status,Sender,ConvID);
	-taskStatus(Content,Status,Sender,ConvID);
   .ia_fipa_request_Participant("failure",Content,ConvID).

+!doTask(accreditation,Sender,Data,ConvID):Content=accreditation&accreditConvIDLiteral(ConvID,_)&
request(Content,Data,ParamList)&
.member(user_name(UserName),ParamList)&.member(wmarket(WMarketID),ParamList)
<- 
   ?user_structure(User);
   ?userInfo_Arguments_List(User,UsrFieldsList,UserID);
   .concat("",UserName,StrUserName);
   .member(name(StrUserName),UsrFieldsList);
   ?User; //To recover the uder id
   ?accredited_user_structure(AccUser);
   ?accredited_user_Arguments_List(AccUser,AccUFieldList,UserID,WMarketID);

   .member(trust_value(0),AccUFieldList);  
   .member(sanction_value("not-sanctioned"),AccUFieldList);
   .member(id(UserID),AccUFieldList);

   +AccUser;  //adding it in the db
   
   ?water_market_structure(WM);
   ?water_market_Arguments_List(WM,WMFieldsList,WMarketID);
   ?WM;
   .print("- Accredited: ",AccUser," WaterMarket ",WM);
   +taskResult(Content,ConvID,Sender,accredited(User,WM));

   //-- FOR PERFORMANCE TESTING PURPOSES:
   ?recruited_participant_structure(RPart);
   ?recruited_participant_Arguments_List(RPart,RPFieldsList,RPID);
   ?table_structure(Table);
   ?table_Arguments_List(Table,TableFieldsList,TTID);
   ?currentConfiguration(Conf);
   ?configuration_Arguments_List(Conf,ConfFieldsList,ConfID);
   .member(trading_table_id(TTID),RPFieldsList);
   .member(wmarket(WMarketID),RPFieldsList);
   .member(configuration_id(ConfID),RPFieldsList);
   .member(user_id(UserID),RPFieldsList);
   .member(id(TTID),TableFieldsList);
   .member(wmarket(WMarketID),TableFieldsList);
   .member(configuration_id(ConfID),TableFieldsList);
   .findall(Table,RPart&Table,ParticipantTablesList);
   .foreach (.member(T,ParticipantTablesList)){
  			?table_Arguments_List(T,TFieldsList,TID);
			?recruited_participant_structure(RPart2);
   			?recruited_participant_Arguments_List(RPart2,RPFieldsList2,RPID2);
   			.member(trading_table_id(TID),RPFieldsList2);
   			.member(wmarket(WMarketID),RPFieldsList2);
   			.member(configuration_id(ConfID),RPFieldsList2);
   			.member(user_id(UserID2),RPFieldsList2);
   			?accredited_user_structure(AccUser2);
   			?accredited_user_Arguments_List(AccUser2,AccUFieldList2,UserID2,WMarketID);
   			.findall(RPart2,RPart2&AccUser2&T,AccreditedPart);
			//.print("xxxxTable: ",T," RParticipant: ",RPart2," AccUser2: ",AccUser2);   			
   			.length(AccreditedPart,CountRPart);

		   	?table_Arguments_List(T,TFieldsList,TID);
		   	.member(opening_user(OpUserID),TFieldsList);
		   	.member(num_participants(NumPart),TFieldsList);
		    ?user_structure(OpeningUser);
		    ?userInfo_Arguments_List(OpeningUser,OpeningUserFieldsList,OpUserID);
		    ?OpeningUser;
		    .member(name(OpeningUsername),OpeningUserFieldsList);
		   	if (NumPart==CountRPart)
		   	{
		   		//.print("Enviando a ",OpeningUsername);
		   		.send(OpeningUsername,tell,allmemberjoined(T,UserID,[],true));
		   	};
   }.

   

-!doTask(Content,Sender,Data,ConvID):Content=accreditation & accreditConvIDLiteral(ConvID,_)
<- .ia_fipa_request_Participant("failure",Content,ConvID).

+conversationended(ConvID, Result):Content=accreditation&accreditConvIDLiteral(ConvID,_)&
taskResult(Content,ConvID, Sender,R)
<- -taskResult(Content,ConvID, Sender,R).
    // .print("- Conversation ",ConvID," with ",Sender," finished with result: ", Result,"!").

/* -  ACREDITATION  <- */

/* ----------------------------------------------------------------*/

/* ->  QUERY OPEN NEGOCIATION TRADING TABLES  - */


+?join(ConvID,fqrp,Query):openNTTConvIDLiteral(ConvID,_)&
openNTTQueryTimeOut(TO)&
query(openNTT,Query,ParamList)
<- 
    .member(sender(User),ParamList);
	?userInfo_Arguments_List(User,FieldsList,UserID);
	.member(wmarket(WMarketID),ParamList);
	?accrediteduser(UserID,WMarketID); 
	.ia_fipa_query_Participant("joinconversation",TO,ConvID).

+query(Sender,Query,Protocol,ConvID):openNTTConvIDLiteral(ConvID,_)&
query(openNTT,Query,ParamList)&
.member(wmarket(WMarketID),ParamList)&
.member(sender(User),ParamList)&
userInfo_Arguments_List(User,FieldsList,UserID)&
.ground(UserID)&
not accrediteduser(UserID,WMarketID)
<- .print("- I've received the query: ",Query," and I refuse. User ",Sender," is not accredited.");
   .ia_fipa_query_Participant("refuse",ConvID).

+query(Sender,Query,Protocol,ConvID):openNTTConvIDLiteral(ConvID,_)&
query(openNTT,Query,ParamList)&
.member(wmarket(WMarketID),ParamList)& 
.member(sender(User),ParamList)&
userInfo_Arguments_List(User,FieldsList,UserID)&
.ground(UserID)&
accrediteduser(UserID,WMarketID)
<- 
   !verifyQuery(Query,Protocol,Result);
	
   if (Result == none)
   	{.print("- Query: ",Query," not understood. Result: ",Result);
   	.ia_fipa_query_Participant("notunderstood",Result,ConvID);
   	}
	else{
	   	if (Result==false)
	     {.print("- No matching results for query: ", Query);
	      .ia_fipa_query_Participant("failure",ConvID); }
   		else{
  		 	.member(openNTTList(Result),ParamList);
  		 	?query(openNTT,Query,ParamList);
  		 	.print("- Results for Query: ",Query);
   		 	.ia_fipa_query_Participant("agree",Query ,ConvID);
			}
   		}.
   		
+query(Sender,Query,Protocol,ConvID):
openNTTConvIDLiteral(ConvID,_)
<-  .print("- No matching results for query: ", Query);
	.ia_fipa_query_Participant("failure",ConvID).

+!verifyQuery(Query,Protocol,Result):openNTTConvIDLiteral(ConvID,_)
<-      //Query = openNTT(List);
		//?openNTTquery(Query,OpenNTTList,WMarket);
		?query(openNTT,Query,ParamList);
		.member(wmarket(WMarket),ParamList);
		.member(openNTTList(OpenNTTList),ParamList);
		.member(th_id(THall),ParamList);
		?currentConfigurationID(ConfID);

		if (Protocol==fqip)
    	{Result = true;}
    	if (Protocol==fqrp)
    	{
    		!openNTT(OpenNTTList,WMarket,THall,ConfID);
    		Result = OpenNTTList;
    	}.
    	//.print("MATCH QUERY.");
 
-!verifyQuery(Query,fqip,Result):openNTTConvIDLiteral(ConvID,_)
<-  //.print("NO MATCH. IF QUERY FALSE."); 
    Result = false.

-!verifyQuery(Query,fqrp,Result):openNTTConvIDLiteral(ConvID,_)
<-  //.print("NO MATCH. REF QUERY FAILED.");
    Result=none.
    
-!verifyQuery(Query,Protocol,Result):openNTTConvIDLiteral(ConvID,_)
<-  //.print("NO MATCH. KIND OF QUERY UNDEFINED.");
    Result=none.

//+conversationended(ConvID,Result):openNTTConvIDLiteral(ConvID,_)
//<-   .print("------- Conversation ",ConvID," ENDED! Result: ",Result).
  
  
/* QUERY OPEN NEGOCIATION TRADING TABLES  <-  */
 
/* ----------------------------------------------------------------*/
 
/* ->  REQUEST NEW NEGOCIATION TRADING TABLE  - */

//This plan trigers when the conversation ID already exists 

+?join(ConvID,frp,Action):newNTTIDConv(_,ConvID)&newNTTConvIDLiteral(ConvID,_)&
newNTTTimeOut(TO)&
request(newNTT,Action,ParamList)&
.member(user(User),ParamList)&
userInfo_Arguments_List(User,FieldsList,UserID)&
.member(table(Table),ParamList)&
table_Arguments_List(Table,TableFieldsList,TableID)&
.member(wmarket(WMarketID),TableFieldsList)&
accrediteduser(UserID,WMarketID)
<- 
   .fail.

@pjoin[atomic]
+?join(ConvID,frp,Action):newNTTConvIDLiteral(ConvID,_)&
newNTTTimeOut(TO)&
request(newNTT,Action,ParamList)&
.member(user(User),ParamList)&
userInfo_Arguments_List(User,FieldsList,UserID)&
.member(table(Table),ParamList)&
table_Arguments_List(Table,TableFieldsList,TableID)&
.member(wmarket(WMarketID),TableFieldsList)&
accrediteduser(UserID,WMarketID)
<- .member(name(Name),FieldsList) ;
   +newNTTIDConv(Name,ConvID);
   .ia_fipa_request_Participant("joinconversation",TO,ConvID).

+request(Sender,Content,Request,ConvID):newNTTConvIDLiteral(ConvID,_)&
request(Content, Request,ParamList)&
possibleactions(L)&
not .member(Content,L)&
newNTTIDConv(Sender,ConvID)
<- .print("- I've received a request for doing: ",Content," but i don't understand. It is not inside the actions I can do.");
   -+taskStatus(Content,notunderstood,Sender,ConvID);
   .ia_fipa_request_Participant("notunderstood",ConvID).

+request(Sender,Content,Request,ConvID):newNTTConvIDLiteral(ConvID,_)&
request(Content,Request,ParamList)&
.concat("",Sender,StrSender)&
.member(user(User),ParamList)&
userInfo_Arguments_List(User,FieldsList,UserID)&
.member(table(Table),ParamList)&
table_Arguments_List(Table,TableFieldsList,TableID)&
.member(wmarket(WMarketID),TableFieldsList)&
accrediteduser(UserID,WMarketID)&
possibleactions(L)&.member(Content,L)&
newNTTIDConv(StrSenderConv,ConvID)&
StrSenderConv==StrSender
<- //.print("- I've received a request for doing: ",Content," and i'm agree. ConvID: ",ConvID);
   -+taskStatus(Content,agree,Sender,ConvID);
   .ia_fipa_request_Participant("agree",ConvID).

//Additional verification must be done in this step. Ex: Verify if table already exists
+request(Sender,Content,Request,ConvID):newNTTConvIDLiteral(ConvID,_)&
request(Content,Request,ParamList)&
.concat("",Sender,StrSender)&
.member(user(User),ParamList)&
userInfo_Arguments_List(User,FieldsList,UserID)&
.member(table(Table),ParamList)&
table_Arguments_List(Table,TableFieldsList,TableID)&
.member(wmarket(WMarketID),TableFieldsList)&
not accrediteduser(UserID,WMarketID)&
newNTTIDConv(StrSenderConv,ConvID)&
StrSenderConv==StrSender
<- .print("- I've received a request for doing: ",Content," but I refuse. User ",Sender," is not accredited.");
   -+taskStatus(Content,refuse,Sender,ConvID);
   .ia_fipa_request_Participant("refuse",ConvID).

+request(Sender,Content,Request,ConvID):newNTTConvIDLiteral(ConvID,_)&
request(Content, Request,ParamList)&
.concat("",Sender,StrSender)&
newNTTIDConv(StrSenderConv,ConvID)&
StrSenderConv==StrSender
<- .print("- I've failed doing ",Content);
   -+taskStatus(Content,failure,Sender,ConvID);
   .ia_fipa_request_Participant("failure",Content,ConvID).
   
+timetodotask(Content,Request,ConvID):newNTTConvIDLiteral(ConvID,_)&
taskStatus(Content,agree,Sender,ConvID)&
.concat("",Sender,StrSender)&
request(Content, Request,ParamList)&
newNTTIDConv(StrSenderConv,ConvID)&
StrSenderConv==StrSender
<- //.print("- I'm going to make the task: ",ConvID);
   !doTask(Content,Sender,Request,ConvID);
   ?taskResult(Content,ConvID, Sender,R);
   .ia_fipa_request_Participant("inform",Content,R,ConvID).
   
+timetodotask(Content,Request,ConvID):newNTTConvIDLiteral(ConvID,_)&
request(Content, Request,ParamList)&
.concat("",Sender,StrSender)&
newNTTIDConv(StrSenderConv,ConvID)&
StrSenderConv==StrSender
<- .print("- I've failed doing the task: ",Content);
   .ia_fipa_request_Participant("failure",Content,ConvID).

+!doTask(Content,Sender,Request,ConvID):newNTTConvIDLiteral(ConvID,_)&
request(Content,Request,ParamList)&
.concat("",Sender,StrSender)&
.member(table(Table),ParamList)&
table_Arguments_List(Table,TableFieldsList,TableID)&
newNTTIDConv(StrSenderConv,ConvID)&
StrSenderConv==StrSender
<- 
   
   .member(user(OpUser),ParamList);
   ?userInfo_Arguments_List(OpUser,UserFieldsList,OpUserID);
   
   .member(opening_user(OpUserID),TableFieldsList);
   .member(access_type("Public"),TableFieldsList);  //this must be evaluated in order to have a more sofisticated way to set access type
   .date(Y,M,D);
   .member(opening_date([D,M,Y]),TableFieldsList);
   .member(protocol_parameters(ProtParamNew),TableFieldsList); //I don know where to take this
   //.print("Protocol parameters: ",ProtParamNew);
   ?currentConfigurationID(ConfID);
   .member(configuration_id(ConfID),TableFieldsList);
   //.member(protocol_type(Protocol),ParamList); 
   .member(wmarket(MWaterMarket),TableFieldsList); 
	?table_Arguments_List(Table,TableFieldsList,TableID);
	.member(water_right_ids(WRIDs),ParamList);
	!insertNewTTable(Table,MWaterMarket,ConfID,TableID,OpUserID,WRIDs);
   //.print("- Table list updated to: ",NewTablesList);
   +taskResult(Content,ConvID,Sender,Table).

-!doTask(Content,Sender,Request,ConvID):newNTTConvIDLiteral(ConvID,_)&
newNTTIDConv(Sender,ConvID)
<- .print("I've failed creating new table or the opening user as participant.");
   .ia_fipa_request_Participant("failure",Content,ConvID).

@pinsertNewTTable[atomic]
+!insertNewTTable(Table,MWaterMarket,ConfID,TableID,OpeningUserID,WRIDs)
<- ?getNewNTTID(ConfID,MWaterMarket,NewID);
   ?table_Arguments_List(Table,FieldsList,NewID);
   //TableID = NewID;
   +Table; //Adding a trading table in the database
   //After adding the variable TableID has value
   
	?recruited_participant_structure(RPart);
	?recruited_participant_Arguments_List(RPart,RPFieldsList,OpeningUserID);
	 .member(trading_table_id(NewID),RPFieldsList);
	 .member(wmarket(MWaterMarket),RPFieldsList);
	 .member(configuration_id(ConfID),RPFieldsList);
	 .date(Y,M,D);
	 Date = [D,M,Y];
	 .member(invitation_date(Date),RPFieldsList);
	 .member(acceptance_date(Date),RPFieldsList);
	 .member(accepted(true),RPFieldsList);
	 .member(number_of_participations(0),RPFieldsList);
	 +RPart; //Adding the opening user as a member of the trading table
	 for (.member(WR,WRIDs))
	 {
	 	?waterright_tt_structure(WrightTT);
	 	?waterright_tt_Arguments_List(WrightTT,WRTTFieldsList,WR,NewID);
	 	.member(wmarket(MWaterMarket),WRFieldsList);
	 	.member(configuration_id(ConfID),WRFieldsList);
	 	+WrightTT; //Adding the water right for being negociated in the new trading table
	 }. 



+conversationended(ConvID, Result):newNTTConvIDLiteral(ConvID,_)&
taskStatus(Content,_,Sender,ConvID)&
.concat("",Sender,StrSender)&
newNTTIDConv(StrSenderConv,ConvID)&
StrSenderConv==StrSender
<- -newNTTIDConv(Sender,ConvID).
   //.print("- Conversation ",ConvID," with ",Sender," finished with result: ", Result,"!").
   
/* -  REQUEST NEW NEGOCIATION TRADING TABLE  <- */

/* ----------------------------------------------------------------*/


/* ->  INVITATION  - */

+?join(ConvID,frp,Action):invitationConvIDLiteral(ConvID,_,_)&
invitationTimeOut(TO)&
request(invitation,Action,ParamList)&
.member(sender(RequesterUser),ParamList)&
userInfo_Arguments_List(RequesterUser,FieldsList,UserID)&
.member(table(Table),ParamList)&
table_Arguments_List(Table,TableFieldsList,TableID)&
.member(wmarket(WMarketID),TableFieldsList)&
accrediteduser(UserID,WMarketID)
<- 
   .ia_fipa_request_Participant("joinconversation",TO,ConvID).

+request(Sender,Content,Request,ConvID):invitationConvIDLiteral(ConvID,_,_)&
possibleactions(L)&not .member(Content,L)
<- .print("- I've received a request for doing: ",Content," but i don't understand. It is not inside the actions I can do.");
   -+taskStatus(Content,notunderstood,Sender,ConvID);
   .ia_fipa_request_Participant("notunderstood",ConvID).

+request(Sender,Content,Request,ConvID):Content=invitation&invitationConvIDLiteral(ConvID,_,_)&
request(Content,Request,ParamList)&
.member(sender(RequesterUser),ParamList)&
.member(table(Table),ParamList)&
table_Arguments_List(Table,TableFieldsList,TableID)&
.member(wmarket(WMarket),TableFieldsList)&
userInfo_Arguments_List(RequesterUser,UserFieldsList,UserID)&
not accrediteduser(UserID,WMarket)
<- .print("- I've received a request for doing: ",Content," but I refuse. User ",Sender," is not accredited in the table market.");
   -+taskStatus(Content,refuse,Sender,ConvID);
   .ia_fipa_request_Participant("refuse",ConvID).

+request(Sender,Content,Request,ConvID):Content=invitation&invitationConvIDLiteral(ConvID,_,_)&
request(Content,Request,ParamList)&
.member(sender(RequesterUser),ParamList)&
.member(table(Table),ParamList)&
table_Arguments_List(Table,TableFieldsList,TableID)&
.member(wmarket(WMarket),TableFieldsList)&
userInfo_Arguments_List(RequesterUser,UserFieldsList,UserID)&
accrediteduser(UserID,WMarket)
<- //.print("-I've received a request for doing: ",Content," and i'm agree.");
   -+taskStatus(Content,agree,Sender,ConvID);
   .ia_fipa_request_Participant("agree",ConvID).
   
+request(Sender,Content,Data,ConvID):Content=invitation&invitationConvIDLiteral(ConvID,_,_)
<- .print("- I've failed doing ",Content);
   -+taskStatus(Content,failure,Sender,ConvID);
   .ia_fipa_request_Participant("failure",Content,ConvID).
   
+timetodotask(Content,Request,ConvID):Content=invitation&invitationConvIDLiteral(ConvID,_,_)&
taskStatus(Content,agree,Sender,ConvID)
<- //.print("- I'm going to make the task: ",ConvID);
   !doTask(Content,Sender,Request,ConvID);
   ?taskResult(Content,ConvID, Sender,R);
   -taskStatus(Content,agree,Sender,ConvID);
   
   .ia_fipa_request_Participant("inform",Content,R,ConvID).
   
+timetodotask(Content,Request,ConvID):Content=invitation&invitationConvIDLiteral(ConvID,_,_)
<- .print("- I've failed doing the task: ",Content);
	?taskStatus(Content,Status,Sender,ConvID);
	-taskStatus(Content,Status,Sender,ConvID);
   .ia_fipa_request_Participant("failure",Content,ConvID).

+!doTask(Content,Sender,Request,ConvID):Content=invitation&invitationConvIDLiteral(ConvID,_,_)&
request(Content,Request,ParamList)&
.member(sender(RequesterUser),ParamList)&
.member(table(Table),ParamList)&
.member(receivers(Receivers),ParamList)
<-// .print("Dentro del doTask del invitation");
	?userInfo_Arguments_List(RequesterUser,RUFieldsList,RUID);
	.member(name(RUName),RUFieldsList);

   ?table_Arguments_List(Table,TableFieldsList,TableID);
   .member(wmarket(WMarket),TableFieldsList);
   
   if (Receivers==[])
   {
	//Taking the name
    ?user_structure(CurrUser);
	?userInfo_Arguments_List(CurrUser,CurrUsrList,UsrID); 
	.member(name(Name),CurrUsrList);
	//Building recruited participant query
	?recruited_participant_structure(RPart);
	?recruited_participant_Arguments_List(RPart,RPFieldsList,UsrID);
	.member(wmarket(WMarket),RPFieldsList);
	.member(trading_table_id(TableID),RPFieldsList);
	?currentConfigurationID(ConfID) ;
    .member(configuration_id(ConfID),RPFieldsList);
    
    //Taking accredited in this market only
	?accredited_user_structure(AccUser);
	?accredited_user_Arguments_List(AccUser,AccUserList,UsrID,WMarket);
	?Table;
	.print("STARTING INVITATION TO PARTICIPANTS");
	for ( AccUser ) {
         ?CurrUser; //To recover the name
       if (Name \== RUName)
        {.send(Name,tell,invitation(Table, RequesterUser));//Name is maped with the user name in CurrUser
         .print("Sending invitation from ",RUName," to ",Name);
         .date(Y,M,D);
         .member(invitation_date([D,M,Y]),RPFieldsList);
         .member(accepted(false),RPFieldsList);
         .member(number_of_participations(0),RPFieldsList);
         +RPart;}
     }
   }else{ // list of receivers is not empty
    .print("THERE ARE SPECIFIC RECEIVERS: ",Receivers);
    for( .member(Name,Receivers) ){
    	.send(Name,tell,invitation(Table, RequesterUser));
    	?CurrUser; //to recover the user ID
    	.print("Sending invitation to ",Name);
        .date(Y,M,D);
        .member(invitation_date([D,M,Y]),RPFieldsList);
        .member(accepted(false),RPFieldsList);
        .member(number_of_participations(0),RPFieldsList);
        +RPart;
    }
   }
   +taskResult(Content,ConvID,Sender,done).

-!doTask(Content,Sender,Data,ConvID):Content=invitation & invitationConvIDLiteral(ConvID,_,_)
<- .ia_fipa_request_Participant("failure",Content,ConvID);
   +taskResult(Content,ConvID,Sender,fail).

+conversationended(ConvID, Result):Content=invitation&invitationConvIDLiteral(ConvID,_,_)&
taskResult(Content,ConvID, Sender,R)
<- -taskResult(Content,ConvID, Sender,R).
     //.print("- Conversation ",ConvID," with ",Sender," finished with result: ", Result,"!").

/* -  INVITATION  <- */

/* ----------------------------------------------------------------*/
 
/* ->  REQUEST JOIN NEGOCIATION TRADING TABLE  - */

+?join(ConvID,frp,Request):joinNTTConvIDLiteral(ConvID,_,_)&
joinNTTTimeOut(TO)&
request(joinNTT,Request,ParamList)&
.member(user(User),ParamList)&
userInfo_Arguments_List(User,FieldsList,UserID)&
.member(table(Table),ParamList)&
table_Arguments_List(Table,TableFieldsList,TableID)&
.member(wmarket(WMarketID),TableFieldsList)&
accrediteduser(UserID,WMarketID)
//&ConvID=joinNTTconv(Rol,ID)
<-   .ia_fipa_request_Participant("joinconversation",TO,ConvID).




+request(Sender,Content,Data,ConvID):Content==joinNTT&joinNTTConvIDLiteral(ConvID,_,_)&
request(Content,Data,ReqParamList)
<-
   !processrequest(Sender,Content,Data,ConvID).



+!processrequest(Sender,joinNTT,Data,ConvID):joinNTTConvIDLiteral(ConvID,_,_)&
possibleactions(L)&not .member(Content,L)
<- .print("- I've received a request for doing: ",Content," but i don't understand. It is not inside the actions I can do.");
   -+taskStatus(Content,notunderstood,Sender,ConvID);
   .ia_fipa_request_Participant("notunderstood",ConvID).

//Some other verification can be added in order to accept a participant without invitation 
+!processrequest(Sender,Content,Data,ConvID):joinNTTConvIDLiteral(ConvID,_,_)&
Content == joinNTT &
request(Content,Data,ReqParamList)&
.member(user(UserInfo),ReqParamList)&
.member(table(Table),ReqParamList)&
table_Arguments_List(Table,TableFieldsList,TTID)&
.member(wmarket(WMarket),TableFieldsList)&
userInfo_Arguments_List(UserInfo,FieldsList,UserID)&
accrediteduser(UserID,WMarket)
<- 
  
   ?recruited_participant_structure(RPart);
   ?recruited_participant_Arguments_List(RPart,RPFieldsList,UserID);
   .member(user_id(UserID),RPFieldsList);
   .member(trading_table_id(TTID),RPFieldsList);
   .member(wmarket(WMarket),RPFieldsList);
   ?currentConfigurationID(ConfID);  //Evaluate better if the ConfID must be taking from the current or from the table
   .member(configuration_id(ConfID),RPFieldsList);
   //Ojo: I don't know where to put the rol of the participant!!
   
   if (RPart) //If it exists
   {
	   	.member(accepted(Acc),RPFieldsList);
	   	if (Acc==true) //If it exists and it has been accepted 
	   	{
		   	.print("- I've received a request for doing: ",Content," but I refuse. User ",Sender," is already member of table ",TableID);
		   	-+taskStatus(Content,refuse,Sender,ConvID);
		   	+taskResult(Content,ConvID, Sender,RPart);
	   		.ia_fipa_request_Participant("agree",ConvID)
   		}else{ //it has not been accepted
	   		   	.print("- I've received a request for doing: ",Content," and i'm agree.");
   				-taskStatus(Content,_,Sender,ConvID);
   				+taskStatus(Content,agree,Sender,ConvID);
   				.ia_fipa_request_Participant("agree",ConvID);
   		}
   } //The participant does not exist
   else
   {
   	.print("- I've received a request for doing: ",Content," and i'm agree.");
   	-taskStatus(Content,_,Sender,ConvID);
   	+taskStatus(Content,agree,Sender,ConvID);
   	.ia_fipa_request_Participant("agree",ConvID);
   }.


//Additional verification must be done in this step. Ex: Verify if table already exists
+!processrequest(Sender,joinNTT,Data,ConvID):joinNTTConvIDLiteral(ConvID,_,_)&
request(joinNTT,Data,ReqParamList)&
.member(user(UserInfo),ReqParamList)&
userInfo_Arguments_List(UserInfo,FieldsList,UserID)&accrediteduser(UserID,WMarketID)
<- .print("- I've received a request for doing: ",Content," but I refuse. User ",Sender," is not accredited.");
   -+taskStatus(Content,refuse,Sender,ConvID);
   .ia_fipa_request_Participant("refuse",ConvID).

-!processrequest(Sender,Content,Data,ConvID):joinNTTConvIDLiteral(ConvID,_,_)&
request(Content,Data,ReqParamList)&Content==joinNTT
<- .print("- I've failed doing ",Content);
   -+taskStatus(Content,failure,Sender,ConvID);
   .ia_fipa_request_Participant("failure",Content,ConvID).


+timetodotask(Content,Data,ConvID):joinNTTConvIDLiteral(ConvID,_,_)&
taskStatus(Content,agree,Sender,ConvID)&
request(Content,Data,ReqParamList)&Content==joinNTT
<- .print("- I'm going to make the task: ",ConvID);
   !doTask(Content,Sender,Data,ConvID);

   if (taskResult(Content,ConvID, Sender,R))
     {   .member(table(Table),ReqParamList);
         .member(rol(Rol),ReqParamList);
     	.ia_fipa_request_Participant("inform",Content,participant(R,Table,Rol),ConvID);}
   else {
   		.print("I've failed doing the task ",Content," for agent ",Sender);
   		.ia_fipa_request_Participant("failure",Content,ConvID);
   }.
   
+timetodotask(Content,Data,ConvID):joinNTTConvIDLiteral(ConvID,_,_)&
taskStatus(Content,refuse,Sender,ConvID)&
request(Content,Data,ReqParamList)&Content==joinNTT
<- .print("- I'm going to make the task even i refuse: ",ConvID);
	if (taskResult(Content,ConvID, Sender,R))
     {
     	 .member(table(Table),ReqParamList);
     	 .member(rol(Rol),ReqParamList);
     	.ia_fipa_request_Participant("inform",Content,participant(R,Table,Rol),ConvID);
     }
   else {
   	.print("I've failed doing the task ",Content," for agent ",Sender);
   	.ia_fipa_request_Participant("failure",Content,ConvID);
   }.
   
+timetodotask(Content,Data,ConvID):joinNTTConvIDLiteral(ConvID,_,_)&
request(Content,Data,ReqParamList)&Content==joinNTT
<- .print("- I've failed doing the task: ",Content);
   .ia_fipa_request_Participant("failure",Content,ConvID).
   
+!doTask(Content,Sender,Data,ConvID):Content==joinNTT&joinNTTConvIDLiteral(ConvID,Rol,ID)&
request(Content,Data,ReqParamList)&
.member(user(UserInfo),ReqParamList)&
.member(table(Table),ReqParamList)&
table_Arguments_List(Table,TableFieldsList,TTID)&
.member(wmarket(WMarket),TableFieldsList)&
userInfo_Arguments_List(UserInfo,UserFieldsList,UserID)
<- 
 	?recruited_participant_structure(RPart);
 	?recruited_participant_structure(NewRPart);
	?recruited_participant_Arguments_List(RPart,RPFieldsList,UserID);
	?recruited_participant_Arguments_List(NewRPart,NewRPFieldsList,UserID);
	//.member(user_id(UserID),RPFieldsList); //de más
	.member(trading_table_id(TTID),RPFieldsList);
	.member(trading_table_id(TTID),NewRPFieldsList);
	.member(wmarket(WMarket),RPFieldsList);
	.member(wmarket(WMarket),NewRPFieldsList);
	?currentConfigurationID(ConfID);  //Evaluate better if the ConfID must be taking from the current or from the table
	.member(configuration_id(ConfID),RPFieldsList);
	.member(configuration_id(ConfID),NewRPFieldsList);
	.date(Y,M,D);
	if (not RPart) //next fields must be filled if the participant hasn't received an invitation
	{
		.member(invitation_date([D,M,Y]),NewRPFieldsList);
	}
	.member(number_of_participations(0),NewRPFieldsList);
    .member(accepted(true),NewRPFieldsList);
	.member(acceptance_date([D,M,Y]),NewRPFieldsList);
    +NewRPart; //updating 'accepted' and 'acceptance_date'
    //.print("ACTUALIZADO ",NewRPart);
    Result = NewRPart;
    +taskResult(Content,ConvID,Sender,Result);
    //Here the most appropiated is to ask the table owner if he accepts sender to join the table?
        
    //THE NEXT IS A PATCH TO START A SUBPROTOCOL BECAUSE THIS MUST BE DONE WHEN CONFIRMATIONS OF JOINING ARE RECEIVED
    //Sending the opening user a confirmation that a user has joined
     .member(opening_user(OpUsrID),TableFieldsList);
     ?user_structure(OpUsr);
     ?userInfo_Arguments_List(OpUsr,OpUsrFieldsList,OpUsrID);
     ?OpUsr;
     .member(name(OpUsrName),OpUsrFieldsList);
     ?Table;
     //.send(OpUsrName,tell,memberjoined(NewRPart,Table));
     .member(num_participants(NumPart),TableFieldsList);
     
     ?recruited_participant_structure(VerifRPart);
	 ?recruited_participant_Arguments_List(VerifRPart,VerifRPFieldsList,VerifUserID);
	 .member(trading_table_id(TTID),VerifRPFieldsList);
	 .member(wmarket(WMarket),VerifRPFieldsList);
	 .member(configuration_id(ConfID),VerifRPFieldsList);
     .member(accepted(true),VerifRPFieldsList);
     .findall(VerifRPart,VerifRPart,VerifRPartList);
     if (.length(VerifRPartList,NumPart)){StartAuction = true;}
     else{StartAuction=false;};
     ?tableownerwaterrights(WMarket,TTID,WRList); //if this doesn't fail
     if (WRList=[WR|WRTail]) //if there is a first water right
        {  
     		.send(OpUsrName,tell,memberjoined(Table,UserID,WR,StartAuction));
     	}else{   .send(OpUsrName,tell,memberjoined(Table,UserID,[],StartAuction));}.
     
  //   .print("********* Message sent to ",OpUsrName," about joining of ",UserID) .



-!doTask(Content,Sender,Data,ConvID):Content==joinNTT&joinNTTConvIDLiteral(ConvID,Rol,ID)&
request(Content,Data,ReqParamList)
<- .ia_fipa_request_Participant("failure",Content,ConvID).

+conversationended(ConvID, Result):Content==joinNTT&joinNTTConvIDLiteral(ConvID,Rol,ID)&
taskStatus(Content,_,Sender,ConvID)&request(Content,Data,ReqParamList)
<-  
	-taskStatus(Content,_,Sender,ConvID);
  	.print("- Conversation ",ConvID," with ",Sender," finished with result: ", Result,"!").
   
/* -  REQUEST JOIN NEGOCIATION TRADING TABLE  <- */






/* ----------------------------------------------------------------*/
 //some verification must be done. For example, verify if the requester is the owner of the tables
/* ->  SUPPRTOCOLS REQUESTS  - */
+?addTransferAgreement(TAgr,BuyerName)[source(Sender)]
<-  
	?transfer_agreement_Arguments_List(TAgr,FieldsList,_TAID);

    //to insert it and recover the generated id it is necessary to create new variable inside the current plan
    ?transfer_agreement_structure(TAgrNew);
	?transfer_agreement_Arguments_List(TAgrNew,FieldsListNew,TAIDNew);
	
	.member(agreed_price(AgreedPrice),FieldsList);
	.member(agreed_price(AgreedPrice),FieldsListNew);
	.member(aggregation_agreement(AggAgrID),FieldsList);
	.member(aggregation_agreement(AggAgrID),FieldsListNew);
	?user_structure(BuyerInfo);
	?userInfo_Arguments_List(BuyerInfo,BuyerFieldsList,BuyerID);
	.concat("",BuyerName,StrBuyerName);

	.member(name(StrBuyerName),BuyerFieldsList);
	?BuyerInfo;

	.member(id(BuyerID),BuyerFieldsList);
	.member(buyer_id(BuyerID),FieldsListNew);
	.member(water_right_id(WRID),FieldsList);
	.member(water_right_id(WRID),FieldsListNew);
	.member(trading_table_id(TTID),FieldsList);
	.member(trading_table_id(TTID),FieldsListNew);
	.member(wmarket(Market),FieldsList);
	.member(wmarket(Market),FieldsListNew);
	.member(configuration_id(ConfID),FieldsList);
	.member(configuration_id(ConfID),FieldsListNew);
	.member(signature_date(SigDate),FieldsList);
	.member(signature_date(SigDate),FieldsListNew);
	State = "Public";
	.member(state(State),FieldsListNew);//i'm not sure if this must be fullfilled here
	
    +TAgrNew;
	
    //.print("Transfer agreement registered. Id generated: ",TAIDNew);
    
    //updating trading table closing date
    ?table_structure(Table);
    ?table_Arguments_List(Table, TableFieldsList,TTID);
    .member(configuration_id(ConfID),TableFieldsList);
    .member(wmarket(Market),TableFieldsList);
    ?Table; //Recovering data
    .member(opening_date(OpDate),TableFieldsList);
    .member(conditions(Cond),TableFieldsList);
    .member(access_type(AccType),TableFieldsList);
	.member(deal(Deal),TableFieldsList);
	.member(protocol_parameters(ProtParam),TableFieldsList);
	.member(num_iter_until_agreem(NItUntilAgr),TableFieldsList);
	.member(time_until_agreem(TimeUntilAgr),TableFieldsList);
	.member(num_participants(NumPart),TableFieldsList);
	.member(opening_user(OpUsr),TableFieldsList);
	.member(protocol_type(ProtType),TableFieldsList);
	.member(role_when_opening_table(RolWOpenning),TableFieldsList);
	.member(number_of_opener_participations(NOpenerPart),TableFieldsList);
	.member(th_id(THall),TableFieldsList);
    ?table_structure(NewTable);
    ?table_Arguments_List(NewTable, NewTableFieldsList,TTID);
    .member(configuration_id(ConfID),NewTableFieldsList);
    .member(wmarket(Market),NewTableFieldsList);
    .member(opening_date(OpDate),NewTableFieldsList);
    .date(Y,M,D);
     ClDate = [D,M,Y];
    .member(closing_date(ClDate),NewTableFieldsList);
    .member(conditions(Cond),NewTableFieldsList);
    .member(access_type(AccType),NewTableFieldsList);
	.member(deal(Deal),NewTableFieldsList);
	.member(protocol_parameters(ProtParam),NewTableFieldsList);
	.member(num_iter_until_agreem(NItUntilAgr),NewTableFieldsList);
	.member(time_until_agreem(TimeUntilAgr),NewTableFieldsList);
	.member(num_participants(NumPart),NewTableFieldsList);
	.member(opening_user(OpUsr),NewTableFieldsList);
	.member(protocol_type(ProtType),NewTableFieldsList);
	.member(role_when_opening_table(RolWOpenning),NewTableFieldsList);
	.member(number_of_opener_participations(NOpenerPart),NewTableFieldsList);
	.member(th_id(THall),NewTableFieldsList);
    +NewTable; //The only one change is teh closing date
    .print("Closing date of trading table ",TTID," in water market ",Market," updated to current date."). 
    




+?addParticipation(Table,Participant,ParticipationsNum)
<-  //.print("Adding participations ",ParticipationsNum," of ",Participant," in table: ",Table);
	?table_Arguments_List(Table,TableFields,TableID);
   .member(configuration_id(ConfID),TableFields);
   .member(wmarket(WMarket),TableFields);
   	?user_structure(ParticInfo);
	?userInfo_Arguments_List(ParticInfo,ParticFieldsList,ParticID);
	.concat("",Participant,StrPartName);
	.member(name(StrPartName),ParticFieldsList);
	?ParticInfo;
    ?userInfo_Arguments_List(ParticInfo,ParticFieldsList,ParticID);
	?recruited_participant_structure(RPart);
	?recruited_participant_Arguments_List(RPart,RPFieldsList,RPartID);
	.member(user_id(ParticID),RPFieldsList);
	.member(trading_table_id(TableID),RPFieldsList);
	.member(configuration_id(ConfID),RPFieldsList);
	.member(wmarket(WMarket),RPFieldsList);
	?RPart; //to update it, it must exist

	?recruited_participant_structure(NewRPart);
	?recruited_participant_Arguments_List(NewRPart,NewRPFieldsList,NewRPartID);
	.member(user_id(ParticID),NewRPFieldsList);
	.member(trading_table_id(TableID),NewRPFieldsList);
	.member(configuration_id(ConfID),NewRPFieldsList);
	.member(wmarket(WMarket),NewRPFieldsList);
	.member(invitation_condition(InvCond),RPFieldsList);
	.member(invitation_condition(InvCond),NewRPFieldsList);
	.member(invitation_date(InvDate),RPFieldsList);
	.member(invitation_date(InvDate),NewRPFieldsList);
	.member(accepted(Acc),RPFieldsList);
	.member(accepted(Acc),NewRPFieldsList);
	.member(acceptance_date(AccDate),RPFieldsList);
	.member(acceptance_date(AccDate),NewRPFieldsList);
	.member(number_of_participations(ParticipationsNum),NewRPFieldsList);//this is the new information
	+NewRPart;
	.print("Updating number of participations of ", Participant,". Setting it to ",ParticipationsNum).

	
	
	

/* -  SUPPRTOCOLS REQUESTS  <- */