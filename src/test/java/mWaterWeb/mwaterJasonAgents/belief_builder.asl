// Agent belief_builder in project mWater-Magentix-Jason2

/* Initial beliefs and rules */
//(28).
//currentTradinghall_id(28).
//currentProtocol_id(4).
staffname(staff_performance2).  //This must be obtained in other way
myname(Name):-.my_name(Me)&.concat("",Me,Name).
// ontonology
table_Arguments_List(Table,FieldsList,TableID):-
		Table =.. [Functor,FieldsList,Rest]&IDFiled=id(TableID)&
		.member(IDFiled,FieldsList).
table_structure(Table):-
		Table= trading_table(configuration_id(CID),wmarket(Wm),id(ID),opening_date(OpDate),
		closing_date(ClDate),conditions(Cond),access_type(AccType),
		deal(Deal),protocol_parameters(ProtParam),num_iter_until_agreem(NItUntilAgr),
		time_until_agreem(TimeUntilAgr),num_participants(NumPart),opening_user(OpUsr),
		protocol_type(ProtType),role_when_opening_table(RolWOpenning),
		number_of_opener_participations(NOpenerPart),th_id(THall)).

trading_table_wmID(Table,WMarketID):- table_structure(Table)&table_Arguments_List(Table,FieldsList,TableID)&
		.member(wmarket(WMarketID),FieldsList).
		
// ontonology
userInfo_Arguments_List(UserInfo,FieldsList,ID):-
		UserInfo =.. [Functor,FieldsList,Rest]&IDFiled=id(ID)&
		.member(IDFiled,FieldsList).
user_structure(User):-
				User= water_user(id(ID),name(Name),user_type(UsrType),seller_timeout(SellerTO),seller_price(SellerPrice),
				seller_percentage(SellerPerc),seller_wt(SellerWt),seller_th(SellerTh),buyer_bid(BuyerID),
				buyer_enter(BuyerEnter),buyer_cont_enact(BuyerContEnact)).

// ontonology
userClass_Arguments_List(UserClass,FieldsList,ID):-
		UserClass =.. [Functor,FieldsList,Rest]&IDFiled=id(ID)&
		.member(IDFiled,FieldsList).
user_class_structure(UserClass):-
				UserClass= user_class(id(ID),description(ClassDesc),selling_probability(SProb),
				buying_probability(BProb),invitation_acceptance_probability(InvAccProb)).

// ontonology
userHasClass_Arguments_List(UserHasClass,FieldsList,UserID):-
		UserHasClass =.. [Functor,FieldsList,Rest]&IDFiled=user_id(UserID)&
		.member(IDFiled,FieldsList).
user_has_class_structure(UserHasClass):-
				UserHasClass= user_has_class(user_id(ID),configuration_id(ConfID),user_class_id(UsrClassID)).

// ontonology
waterRight_Arguments_List(WaterRight,FieldsList,ID):- 
		WaterRight =.. [Functor,FieldsList,Rest]&IDFiled=id(ID)&
		.member(IDFiled,FieldsList).
water_right_structure(Wright):-
		Wright=  water_right(owner(Own),id(ID),authorized_extraction_flow(AuthFlow),authorization_date(AuthDate),
		authorized(Auth),type_of_water(WaterType),initial_date_for_extraction(IniDateExtract),
		final_date_for_extraction(EndDayExtract),aggregation_right(AggregRight),season_unit(SeasonUnit),season(Season),
		general_water_right(GralWR)).

// ontonology
gwaterRight_Arguments_List(WaterRight,FieldsList,ID):- 
		WaterRight =.. [Functor,FieldsList,Rest]&IDFiled=id(ID)&
		.member(IDFiled,FieldsList).
gwater_right_structure(GWright):-
		GWright=  general_water_right(id(ID),authorized(Auth),authorized_extraction_flow(AuthFlow),authorization_date(AuthDate),
		owner(Own),owner_initial_date(OwnIniDate),owner_final_date(OwnFinalDate)).
	
//ontology
accredited_user_Arguments_List(AccUser,FieldList,UserID,WMarketID):- 
		AccUser =..[Functor,FieldList,Rest]&
		.member(id(UserID),FieldList)&
		.member(wmarket(WMarketID),FieldList).
accredited_user_structure(AccUser):- 
		AccUser = accredited_user(wmarket(WMarketID),id(ID),trust_value(TrustVal),sanction_value(SanctionVal)).

//ontology
configuration_Arguments_List(Conf,FieldsList,ID):- 
		Conf =.. [Functor,FieldsList,Rest]&IDFiled=id(ID)&
		.member(IDFiled,FieldsList).
configuration_structure(Conf):-
		Conf = configuration(id(ID), description(Desc), simulation_date(SimDate), negotiation_protocol(NegProto), 
		group_selected(GrSelected),initial_date(InitDate), final_date(FinalDate), seller_timeout(SellerTO), 
		seller_price(SellerPrice), seller_percentage(SellerPerc), seller_wt(SellerWT), buyer_bid(BuyerBid), 
		buyer_enter(BuyerEnt), buyer_cont_enact(BuyerContEnact), ba_agr_val(BaAgrVal), 
		ba_entitlement(BaEnt), mf_cont_enact(MfConEnac), mf_accred(MfAccr), seller_th(SellerTh), human_interaction(HInt),
		num_participants(NPart),num_seller_probability(SNProb)).
		
//ontology

recruited_participant_Arguments_List(RPart,FieldsList,ID):- 
		RPart =.. [Functor,FieldsList,Rest]&IDFiled=user_id(ID)&
		.member(IDFiled,FieldsList).
recruited_participant_structure(RPart):-
		RPart = recruited_participant(trading_table_id(TTID),wmarket(WMarket),configuration_id(ConfID),
		user_id(UserID),invitation_condition(InvCond),invitation_date(InvDate),accepted(Acc),
		acceptance_date(AccDate),number_of_participations(PartNumber)).

//ontology
transfer_agreement_Arguments_List(TAgr,FieldsList,ID):- 
		TAgr =.. [Functor,FieldsList,Rest]&IDFiled=id(ID)&
		.member(IDFiled,FieldsList).
transfer_agreement_structure(TAgr):-
		TAgr = transfer_agreement(id(ID),agreed_price(AgreedPrice),aggregation_agreement(AggAgrID),buyer_id(BuyerID),
		water_right_id(WRID),trading_table_id(TTID),wmarket(Market),configuration_id(ConfID),
		signature_date(SigDate),state(State)).
transfer_agreement_generatedID(TAgr,ID):-
		TAgr = transfer_agreement(id(ID),agreed_price(AgreedPrice),aggregation_agreement(AggAgrID),buyer_id(BuyerID),
		water_right_id(WRID),trading_table_id(TTID),wmarket(Market),configuration_id(ConfID),
		signature_date(SigDate),state(State)).
		
//ontology
protocol_type_Arguments_List(PType,FieldsList,ID):- 
		PType =.. [Functor,FieldsList,Rest]&IDFiled=id(ID)&
		.member(IDFiled,FieldsList).
protocol_type_structure(PType):-
		PType = protocol_type(id(ID),type_name(Type)).

//ontology
water_market_Arguments_List(WMarket,FieldsList,ID):- 
		WMarket =.. [Functor,FieldsList,Rest]&IDFiled=id(ID)&
		.member(IDFiled,FieldsList).
water_market_structure(WMarket):-
		WMarket = water_market(id(ID),description(Type),version(Ver)).

//ontology
waterright_tt_Arguments_List(WMTT,FieldsList,WRID,TTID):- 
		WMTT =.. [Functor,FieldsList,Rest]&
		.member(trading_table_id(TTID),FieldsList)&
		.member(water_right_id(WRID),FieldsList).
waterright_tt_structure(WMTT):-
		WMTT = waterright_tt(trading_table_id(TTID),wmarket(WMarket),configuration_id(ConfID),
			water_right_id(WRID)).


//OJO: REVISAR TODAS LAS CREENCIAS Y ADAPTARLAS AL NUEVO FORMATO!!!!
/* Conversations rules */
//accreditationRequest(accreditate(UserID,WMarket,THall),UserID,WMarket,THall).
request_rule(accreditation,Request,ParamList):- 
		Request = accreditate(UserName,WMarket)&
		(.concat([user_name(UserName)],[wmarket(WMarket)],ParamList)|
		(.member(user_name(UserName),ParamList)&.member(wmarket(WMarket),ParamList))).
//openNTTquery(Query,OpenNTTList,WMarket):-Query = openNTT(OpenNTTList,WMarket).
query(openNTT,Query,ParamList):-
		Query = openNTT(User,OpenNTTList,WMarket,OpDate,THall)&
		(.concat([openNTTList(OpenNTTList)],[wmarket(WMarket)],[th_id(THall)],[opening_date(OpDate)],[sender(User)],ParamList))|
		(.member(openNTTList(OpenNTTList),ParamList)&.member(wmarket(WMarket),ParamList)&.member(th_id(THall),ParamList)&.member(opening_date(OpDate),ParamList)&.member(sender(User),ParamList)).
//newNTTrequest( Request,Table, WMarket,Config ):- Request = newNTT(Table,WMarket,Config)&table_Arguments_List(Table,ArgumentsList,TableID).
request_rule(newNTT, Request, ParamList ):- 
		Request = newNTT(Table,User,WaterRightIDsList)&
		//table_Arguments_List(Table,ArgumentsList,TableID)&
		(.concat([table(Table)],[user(User)],[water_right_ids(WaterRightIDsList)],ParamList))|
		(.member(table(Table),ParamList)&.member(user(User),ParamList)&.member(water_right_ids(WaterRightIDsList),ParamList)). 
//joinNTTrequest( joinNTT,Table ,TableID):- table_structure(Table)&table_Arguments_List(Table,ArgumentsList,TableID).
request_rule(joinNTT, Request, ParamList):- 
		Request = joinNTT(Table,User,Rol)&
		//table_Arguments_List(Table,ArgumentsList,TableID)&
		(.concat([table(Table)],[user(User)],[rol(Rol)],ParamList))|
		(.member(table(Table),ParamList)&.member(user(User),ParamList)&.member(rol(Rol),ParamList)).
request_rule(invitation, Request, ParamList):- 
		Request = invitation(Table,SenderUser,ReceiversList)&
		//table_Arguments_List(Table,TableFieldsList,TableID)&
		//userInfo_Arguments_List(RequesterUser,UserFieldsList,UserID)&
		(.concat([table(Table)],[sender(SenderUser)],[receivers(ReceiversList)],ParamList))|
		(.member(table(Table),ParamList)&.member(sender(SenderUser),ParamList)&.member(receivers(ReceiversList),ParamList)).
		
//protocolrequest(4,water_right(...),trading_table(...),"VBotti",5,12,1)
protocol_request("contract_net",ProtocolID, Request, ParamList):-
		Request = protocolrequest(ProtocolID,WaterRight,Table,Sender)&
		(.concat([water_right(WaterRight)],[table(Table)],[sender(Sender)],ParamList))|
		(.member(water_right(WaterRight),ParamList)&.member(table(Table),ParamList)&.member(sender(Sender),ParamList)).
protocol_request("japanese_auction",ProtocolID, Request, ParamList):-
		Request = protocolrequest(ProtocolID,WaterRight,Table,Sender,MaxIter,Inc,IniBid)&
		(.concat([water_right(WaterRight)],[table(Table)],[sender(Sender)],[max_iterations(MaxIter)],[increment(Inc)],[initial_bid(IniBid)],ParamList))|
		(.member(water_right(WaterRight),ParamList)&.member(table(Table),ParamList)&.member(sender(Sender),ParamList)&
		 .member(max_iterations(MaxIter),ParamList)&member(increment(Inc),ParamList)&.member(initial_bid(IniBid),ParamList)).

/* Initial goals */



/* Plans */
//Getting a table having a list of instanciated fields
@pTableToFields[atomic]
+!tableFiledsToTable(FieldsList,Table,TableID): table_structure(Table)&table_Arguments_List(Table,GenericArguments,TableID)
<-  !instantiateVsStructure(FieldsList,GenericArguments,Table).

@pWaterRightToFields[atomic]
+!waterRightFiledsToWRight(FieldsList,WaterRight,ID):water_right_structure(WaterRight)&waterRight_Arguments_List(WaterRight,GenericArguments,ID)
<- 
   .difference(FieldsList,GenericArguments,Dif);
   !instantiateVsStructure(FieldsList,GenericArguments,WaterRight).

//rename with the same pattern than the prevoius plans
+!userlInfoFiledsToStructure(FieldsList,UserInfo,UserID): user_structure(UserInfo)&userInfo_Arguments_List(UserInfo,UserFieldsList,UserID)
<- //.sort(FieldsList,FieldsListSorted);
   //.sort(UserFieldsList,UserFieldsListSorted);
   //?FieldsListSorted=UserFieldsListSorted; //this must be better done
   !instantiateVsStructure(FieldsList,UserFieldsList,UserInfo);
   ?userInfo_Arguments_List(UserInfo,UserFieldsList,UserID).
   

//The second parameter must be a list with the arguments of the thirth not instantiated
+!instantiateVsStructure(List,[],Result).
+!instantiateVsStructure(List,[HeadGenArg|TailGenArg],Result)
<- HeadGenArg  =.. [Functor,Value,Rest];
   !findElem(Functor,List,ResultList,ValueList,0);
   if (ResultList \== none){ ?HeadGenArg=ResultList ; };
   !instantiateVsStructure(List,TailGenArg,Result).

    
   

   
/* -> GENERAL PLANS -  */

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
<- Result = none.

//Retruns -1 if first date is less than second, 0 if it is equal or 1 if it is greater 
 +!compareDates([D1,M1,Y1],[D2,M2,Y2],Result)
<- if (Y1<Y2)
   {Result = -1}
   else
     {if(Y1>Y2)
	 {Result = 1}
	 else
	   {if (M1<M2)
	    {Result=-1}
		else
		{if (M1>M2)
		 {Result=1}
		 else
		 {if (D1<D2)
		  {Result = -1}
		  else
		  {if (D1>D2)
		   {Result = 1}
		   else
		   {Result=0}
		  }}}}}.

@premoveElem[atomic]
+!removeFromList(Elem,List,ResultList)
<-    !removeElemFromList(Elem,List,[],ResultList).

+!removeElemFromList(Elem,[],TempResult,ResultList)
<- ResultList = TempResult.

+!removeElemFromList(Elem,[AuxHead|AuxTail],TempResult,ResultList)
<- if (Elem \== AuxHead){.concat(TempResult,[AuxHead],NewTempResult);}
   else{NewTempResult = TempResult;}
   !removeElemFromList(Elem,AuxTail,NewTempResult,ResultList).
    
-!removeFromList(Elem,List,ResultList)
<- .print("Failing when removing ",Elem," from ",List,".").

+!isContained([],L2,Result)
<- Result = true.
+!isContained([A|B],L2,Result)
<- .member(A,L2);
   !isContained(B,L2,Result).
-!isContained(L1,L2,Result)
<- Result = false.

+!firstNElementsOfList(N,List,TmpList,ResultList): .length(TmpList,N)
<-  ResultList = TmpList.
+!firstNElementsOfList(N,[H|T],TmpList,ResultList)
<- .concat(TmpList,[H] ,NewList ); 
   !firstNElementsOfList(N,T,NewList,ResultList).

/* - GENERAL PLANS <-  */