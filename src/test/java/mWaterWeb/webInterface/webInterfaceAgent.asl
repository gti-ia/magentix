// Agent webInterfaceAgent in project MWaterWeb

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start:.my_name(Me)
<- .setlogfile("logs/staff.log"); //Form performance purposes only
   .print(Me, " joined to plataform. Waiting for web requests...");
   .ia_web_request("joinconversation").
   
   
+request("accreditation",UsrName,WMarket,ConvID)
<- .print("Request for accreditate ",UsrName," in ",WMarket);
   //if (.ia_manage_web_agent(kill,UsrName)){.print("Agent killed!!!!");}
	//else {.print("Agent not killed!!!!");}
   .print("Creating agent ",UsrName);
   if (.ia_manage_web_agent(create,UsrName))
   {.print("Agent ",UsrName," created.");
    .send(UsrName,tell,request("accreditation",UsrName,WMarket,ConvID));}
   else
   {.print("Agent ",UsrName," could not be created.");}.
     
+accreditationresult(Sender,Result):request("accreditation",Sender,WMarket,ConvID)
<- // Result = result(Invitations,TTInvolved,WMarketID)
	.abolish(accreditationresult(Sender,_));
   .print("Accreditation result received.");
   .ia_web_request("inform","accreditation",Result,ConvID).
   
+accreditationresult(Sender,Result)
<- .print("Accreditation received!!!!! ",Sender," Result ",Result).