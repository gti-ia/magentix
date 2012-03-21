// Agent water_user in project mWater-Magentix-Jason
//THIS IS THE CICLIC BEHAVIOR OF A WATER USER
{ include("wu.asl") }



/* Initial beliefs and rules */
//personalinformation([name(Me),area(38),district("Valencia"),waterVol(50)]):-.my_name(Me).

//requirement([basin("Mancha Oriental"),waterUser(water_user),waterVol(10),district("Valencia"),timePeriodIni([1,6,2011]),timePeriodEnd([30,6,2011]),waterKind(purified)]).


/* Initial goals */
!start.


/* Plans */
//Plan for calling accreditation and for getting the open trading tables
+!start:currentWaterMarket_id(MWaterMarket)&.my_name(Me)
<- .print("Requesting staff for accreditation ...");
   !accredit(Me,MWaterMarket).  

//This belief indicates that the accreditation has finished successfully
+accredited(User):currentWaterMarket_id(MWaterMarket)&currentTradinghall_id(TradingHall)
<- .print(" - I'm accredited -");
   .my_name(Me);
    if (Me\== final_agent2)
    {	.random(R); 
    	NewR = R * 10000;
    	if (NewR < 6000){NewR2 = NewR + 6000 ;} else {NewR2 = NewR  ;}
    	.print("Waiting ",NewR2," miliseconds."); 
    	.wait(NewR2);}

   .print("Querying staff for open negociation trading tables ...");
   !getOpenNTTList(OpenNTTList,MWaterMarket,TradingHall).

//This belief indicates that the query for open tables has finished successfully
//If no table fits the requirement it is requested to create a new one
+openNTTqueryended(WMarket,THall):currentWaterMarket_id(WMarket)&
currentTradinghall_id(THall)
<- ?openNTT(OpenNTTList,WMarket,THall);
   .print("Result of open negociation tables received: ",OpenNTTList," for market ",WMarket);
   if (requirement(Req)) //if there are requirements
     {!getTableForRequirement(Req,OpenNTTList,Table);
       if (Table==none)  //There are no existing tables for requirements
       {
          .print("Requirements don't fit. Requesting staff to create a new Trading Table ...");
          !createNewNTT(Req,"Seller",4,5,MWaterMarket,THall); //evaluate how to determine the number of participants instead of 4
        }
       else
       {
          ?table_Arguments_List(Table,TableFieldsList,TableID);
          .member(opening_user(OpUsr),TableFieldsList);
          .member(wmarket(WMarketID),TableFieldsList);
          ?accredited(MyUserInformation);
          ?userInfo_Arguments_List(MyUserInformation,UsrFieldsList,UsrID);
          if (UsrID\==OpUsr)
          	{.print("Requirements fit. Requesting staff to join to Trading Table: ", Table);
          	!joinIfNoPendingInvitations(Table,TableID,WMarketID,buyer);
          	}
          else{
          	.print("I'm already member of the table.");
          }	
          //!createMoreTables;
       }
     }.

+tablecreated(Table)
<- !inviteParticipants(Table).

+!joinIfNoPendingInvitations(Table,TableID,WMarketID,Rol)
<-	!pendingInvitations(TableID,WMarketID,PI);
   	if ( (PI=0) & (not joined(RecruitedParticipant) )  ){ !joinNTT(Table,Rol);}
   	else {.print("I'm not going to join because i'm already joined or there are pending invitation to join table.");}.
-!joinIfNoPendingInvitations(Table,TableID,WMarketID,Rol)
<- .print("I've failed trying to join table ",TableID," in market ",WMarketID,".").


//Message received when an invitation in sent by anothe user
+invitation(Table, SenderUser)[source(S)]
<- 
   ?userInfo_Arguments_List(SenderUser,UserFieldsList,ID);
   .member(name(Sender),UserFieldsList);
   .print("Invitation received from ",Sender);
   ?table_Arguments_List(Table,TableFeldsList,TableID);
   .member(configuration_id(ConfID),TableFeldsList);
   .member(wmarket(WMarket),TableFeldsList);
   //Putting together the table parameters and the sender user parameters
   .concat(UserFieldsList,TableFeldsList,NewFieldsList); 
   ?requirement(Req);
   !compareListsReqVsTableFields(Req,NewFieldsList,Result);
   //Preparing the recrited participant (this can be made just by passing parameters or implementing the recruiting protocol)
   ?accredited(MyUserInformation);
   ?userInfo_Arguments_List(MyUserInformation,MyUserFieldsList,MyID);
   ?recruited_participant_structure(RecruitedParticipant);
   ?recruited_participant_Arguments_List(RecruitedParticipant,RPFieldsList,MyID);
   .member(configuration_id(ConfID),RPFieldsList);
   .member(wmarket(WMarket),RPFieldsList);
   .member(trading_table_id(TableID),RPFieldsList);
	
   if( not joined(RecruitedParticipant) )
   	{
   		.print("I'm not in table ",TableID,". Trying to join after being invited.");
   		!joinNTT(Table,buyer);  //If requirements fit it accepts
   	}else
   	{
   		.print("I'm already member of table ",TableID,". I can not attend the invitation.");
   	}.
//There can be a conflict between this two plans above and below ***
@pjoined[atomic]
+joined(RecruitedParticipant)
<- 	
	?recruited_participant_structure(RecruitedParticipant);
	?recruited_participant_Arguments_List(RecruitedParticipant,RPFieldsList,ID);
	.member(configuration_id(ConfID),RPFieldsList);
	.member(wmarket(WMarket),RPFieldsList);
	.member(trading_table_id(TTID),RPFieldsList);
	
	?table_structure(Table);
	?table_Arguments_List(Table,GenericArgumentsList,TTID);
	.member(configuration_id(ConfID),GenericArgumentsList);
	.member(wmarket(WMarket),GenericArgumentsList);
	.abolish(invitation(Table, SenderUser));
	.print("I'm in table ",TTID,". Invitations have been removed."). //Removes all invitationsto this table
	
//This set of plans searches in the list of the second parameter the table wich 
//fits the requirements
+!getTableForRequirement(Req,[],Table)
<- Table=none.
+!getTableForRequirement(Req,[FirstTable|Rest],Table)
<- ?table_Arguments_List(FirstTable,ArgumentsList,TableID);
   
   !compareListsReqVsTableFields(Req,ArgumentsList,Result);

   if (Result==true)
   {Table=FirstTable}
   else
   {!getTableForRequirement(Req,Rest,Table)}.
-!getTableForRequirement(Req,[FirstTable|Rest],Table)
<- !getTableForRequirement(Req,Rest,Table).

+!compareListsReqVsTableFields(Req,ArgumentsList,Result)
<- ?table_structure(Table);
   ?table_Arguments_List(Table,GenericArguments,TableID);
   !compareVsStructure(Req,ArgumentsList,GenericArguments,Result).
-!compareListsReqVsTableFields(Req,ArgumentsList,Result)
<-  Result = false.

+!compareVsStructure(Req,ArgumentsList,[],Result)
<- Result = true.
+!compareVsStructure(Req,ArgumentsList,[HeadGenArg|TailGenArg],Result)
<- HeadGenArg =.. [Functor,Value,Rest];
   !findElem(Functor,ArgumentsList,ResultArgList,ValueArg,0);
   if (Result2 == none){.fail};
   !findElem(Functor,Req,ResultReq,ValueReq,0);
   if (ResultReq \== none)
   {
   !compareFunctorValues(Functor,ValueReq,ValueArg,ResultFunctor);
   if (ResultFunctor == false){.fail}
   }
   !compareVsStructure(Req,ArgumentsList,TailGenArg,Result).
-!compareVsStructure(Req,ArgumentsList,[HeadGenArg|TailGenArg],Result)
<- Result = false.

/*
+!findElem(Functor,L,Result,ValueResult,Index)
<- .nth(Index,L,TempResult);
   TempResult =.. [TmpFunctor,Value,Rest];
   if (Functor==TmpFunctor)
   	{
	 Result=TempResult; ?(Value=[ValueResult]) ;
	}
	else {
	 NewIndex = Index + 1;
	 !findElem(Functor,L,Result,ValueResult,NewIndex);
	}.
-!findElem(Functor,L,Result,ValueResult,Index)
<- Result = none.*/

//This is the defauls comparisson criteria. I hope that, if there is another specification for the same plan
//in a asl code wich includes this, it will be executed first
//All this terms must be part of the ontology defined
/*+!compareFunctorValues(Functor,ValueReq,ValueArg,Result)
<- .my_name(Me);
   //.print("++++++++++++++++++  !compareFunctorValues(Functor,ValueReq,ValueArg,Result)  water_user");
   if (Functor == allowedUsers){ValueReq = L1;  
   								ValueArg = L2;
   								!isContained(L1,L2,Result);
   								?((ValueReq==[])|(Result==true)) ; Result = true};
   if (Functor == protocolType){?ValueReq==ValueArg; Result = true};
   if (Functor == basin){?ValueReq==ValueArg; Result = true};
   if (Functor == waterUser){?ValueReq==ValueArg; Result = true};
   if (Functor == waterVol){?ValueReq<=ValueArg; Result = true};
   if (Functor == district){?ValueReq==ValueArg; Result = true};
   if (Functor == timePeriodIni){ !compareDates(ValueReq,ValueArg,DateIniResult);
   								  ?(DateIniResult>=0);
                                  Result = true;};
   if (Functor == timePeriodEnd){ !compareDates(ValueReq,ValueArg,DateEndResult);
   								  ?(DateEndResult<=0);
                                  Result = true;};
   if (Functor == waterKind){?ValueReq==ValueArg; Result = true};
   if (Result \== true){Result = false}.*/
+!compareFunctorValues(Functor,ValueReq,ValueArg,Result)
<- 
   if (Functor == th_id){?ValueReq==ValueArg; Result = true};  
   if (Functor == wmarket){?ValueReq==ValueArg; Result = true};  
   if (Functor == rol_when_opening_table){?ValueReq==ValueArg; Result = true};  
   if (Functor == protocol_type){?ValueReq==ValueArg; Result = true};   								 
   if (Functor == seller_price){?ValueReq>=ValueArg; Result = true};
   if (Functor == seller_percentage){?ValueReq>=ValueArg; Result = true};
   if (Functor == seller_wt){?ValueReq>=ValueArg; Result = true};
   if (Result \== true){Result = false}.
-!compareFunctorValues(Functor,ValueReq,ValueArg,Result)
<- Result = false.

+!createMoreTables:requirement(Req)&currentWaterMarket_id(MWaterMarket)
<- //Req = fields(basin(Basin),waterUser(Wu),waterVol(Wvol),district(District),timePeriod(PeriodDateIni,PeriodDateEnd),waterKind(Wkind));
   .member(waterVol(Wvol),Req);
   NewWvol= Wvol + 10;
   !removeFromList(waterVol(Wvol),Req,ResultList1);
   .concat(ResultList1,[waterVol(NewWvol)],ResultList11);
   !createNewNTT(ResultList11,seller,4,cnp,MWaterMarket,THall);

   NewWvol2= NewWvol + 10;
   .concat(ResultList1,[waterVol(NewWvol2)],ResultList21);
   !createNewNTT(ResultList21,seller,4,cnp,MWaterMarket,THall);

    NewWvol3= NewWvol2 + 10;
    .concat(ResultList1,[waterVol(NewWvol3)],ResultList31);
   !createNewNTT(ResultList31,seller,4,cnp,MWaterMarket,THall).
    
						