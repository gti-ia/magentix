// Agent jauc_participant_template in project nConvTestProjectJar

/* Initial beliefs and rules */
try(0).
/* Initial goals */



/* Plans */

+!join(Sender,ConvID,ja):.my_name(Me)
<- 	//.print("Recibido join Sender ",Sender," ConvID ",ConvID);
    .ia_JAuc_Participant("joinconversation",4000,ConvID);
	.send(Sender,tell,joined(Me,ConvID)).
   	//.print(" - Joined!!!").
   	
+callforbid(Sender,Request,Participants,Bid,ConvID):maxtopay(MTP)
<- 	?try(T);
	NewT = T +1;
	-+try(NewT);
	.print(" call for bid received Bid ",Bid," Request: ",Request," participants: ",Participants);
   	if (Bid > MTP)
   	{ 	
   		.ia_JAuc_Participant("withdrawal",ConvID);
   		.print("- Withdrawal!!");
   	}else{
   		.ia_JAuc_Participant("agree",ConvID);
   		.print("- agree!!");   		
  	}.

+callforbid(Sender,Bid,ConvID)
<-    	.ia_JAuc_Participant("agree",ConvID);
   		.print("- Withdrawal for failure!!").
   		
+winner(Sender,FinalBid,ConvID)
<- .print(" - I'M THE WINNER!!!").