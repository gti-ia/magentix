// Agent webInterfaceAgent in project MWaterWeb
{ include("../mwaterJasonAgents/belief_builder.asl") }
 
 
/* Initial beliefs and rules */
//  There are going to be beliefs "pendingWebUser(Name,WMarket, Rol, WRID,ConvID)" for considering then in each round


/*  Initial goals */

!start.

/* Plans */

+!start:.my_name(Me)
<- .setlogfile("logs/staff.log"); //Form performance purposes only
   .print(Me, " joined to plataform. Waiting for web requests...");
   .ia_web_request("joinconversation").

@pfinishedround[atomic]
+finishedround(ConvID):myname(Me)
<- 
   .print("FINISHED ROUND!!!");
   if (not user_accredited(CurrUsrName)){NewUser="true";} else{NewUser="false"};
   ?staffname(Sn);
   .send(Sn,askOne,lastround(true),Reply);
   if (Reply=lastround(true)){LasR="true";}else{LasR="false";};
   if (NewUser="false")
   {
     .send(Sn,achieve,finishedRound(Me));
   }

   .ia_web_request("inform","finishedround",round(NewUser,LasR),ConvID).
   
@proundstarted[atomic]
+roundstarted(UserName,ConvID)
<- 
   .print("ASKING ROUND  STARTED!!! UserName: ",UserName);
   if (round_has_started(UserName,Date))
   {
   	RS="true"; DateR=Date;
	.abolish(round_has_started(UserName,_));
	//.print("**********ELIMINADO round_has_started(UserName,_) ",UserName);
        
   } else {RS="false"; DateR=[]};
   ?staffname(Sn); 
   .send(Sn,askOne,lastround(true),Reply);
   if (Reply=lastround(true)){LasR="true";}else{LasR="false";};
   if (not pendingWebUser(UserName,_))
   { 		
		PendingUsr = pendingWebUser(UserName,ConvID);
   		.send(Sn,tell,PendingUsr);
   		+PendingUsr;
    }

   .ia_web_request("inform","roundstarted",round(RS,LasR,DateR),ConvID).

// First request for getting into the system as seller
+request("WRList",UsrName,WMarket,Rol,WRID,ConvID)
<- ?staffname(Sn);
   .send(Sn,askOne,waterRightsListGivenUser(UsrName,WRList),Result);
   if (Result=waterRightsListGivenUser(UsrName,WRList))
   {    	!firstNElementsOfList(8,WRList,[],ResultWRList);
   		.print("Sending water rights list to user ",UsrName);
   		.ia_web_request("inform","WRList",wrlist(ResultWRList),ConvID);
   } else { .ia_web_request("inform","WRList",[],ConvID);}.
   

//First request for getting into the system as buyer or seller
+request("accreditation",UsrName,WMarket,Rol,WRID,[D,M,Y],ConvID)
<- 
   Reqq=request("accreditation",UsrName,Rol,WRID,WMarket,[D,M,Y],ConvID);
	if (not user_accredited(UsrName)){ 
			if (.ia_manage_web_agent(create,UsrName))
   			{	
   				+user_accredited(UsrName);
   				.wait(500);
   		     		.send(UsrName,tell,Reqq);
	   		 	.send(UsrName,achieve,Reqq);
   		 		}
  		 	else
   			{	.print("Agent ",UsrName," could not be created.");}
   	}else{ //user is already accredited
   			.send(UsrName,tell,Reqq); 
			.wait(2000); //this is for allowing to have invitations in the next round
   			.send(UsrName,achieve,manageAccrediteUserWithRol(UsrName,Rol,WRID,[D,M,Y]));
   	}.



+!startRound([D,M,Y])
<- 
	if (pendingWebUser(GUsrName,GConvID))
	{	.findall(UserName,pendingWebUser(UserName,_),WebUsersList);
		for (.member(UName,WebUsersList))
		{
			+round_has_started(UName,[D,M,Y]); //.print("***************ANNADIDO round_has_started(UserName,_) ",UName);
		}
	}.


+accreditationresult(Result,Sender)[source(S)]:
request("accreditation",Sender,WMarket,Rol,WRID,Date,ConvID)
<- 
   .print("¡¡¡¡ RESULTS OF TRADING HALL RECEIVED !!!!!");
   .abolish(accreditationresult(_,Sender));
   .abolish(request("accreditation",Sender,WMarket,Rol,_,_,_));

   .ia_web_request("inform","accreditation",Result,ConvID).

+accreditationresult(Result,Sender)[source(S)]
<- .print("Accreditation received!!!!! ",Sender," Result ",Result).

