// Agent jauc_initiator in project nConvTestProjectJar

/* Initial beliefs and rules */
conversation(ja1).
maxiter(30).
increment(2).
initialbid(1).
timeout(3000).
jointimeout(4000).
jarequest("shoes").
participants(P,ConvID):-P=[jauc_agent1,jauc_agent2,jauc_agent3,jauc_agent4,jauc_agent5].

/* Initial goals */

!start.

/* Plans */

+joined(Sender,ConvID)
<- .print(Sender," joined!").


@pstart[atomic] 
+!start : .my_name(Me)&conversation(ConvID)
<- 	.print("- Sending invitation to participants...");
  	 +conversationID(Me,ja,ConvID);
  	 ?participants(P,ConvID);
  	 for (.member(Part,P))
  	 {
  	 	.send(Part,achieve,join(Me,ConvID,ja));
  	 }
   	
   	.wait(3000);
   	
   	?maxiter(MaxIter);
	?increment(Increment);
	?initialbid(InitialBid);
	?timeout(TO);
	?jointimeout(JTO);
	//.print(" participants ",P);
   	!startauction(JTO,TO,InitialBid,Increment,MaxIter,"Starting Japanese auction",P,ConvID).
   	
-!start
<- .print("Failure when starting conversation!!!").

+!startauction(JTO,TO,InitialBid,Increment,MaxIter,InitialMsg,P,ConvID):jarequest(Req)
<- .ia_JAuc_Initiator("start",JTO,TO,InitialBid,Increment,MaxIter,InitialMsg,P,jarequest(Req),ConvID).

+conversationended(Participations,Winner,Bid,ConvID):.my_name(Me)
<- .print(" - Conversation ",ConvID," has ended! ");
   .print(" - RESULTS - ");
   if (.ground(Winner))
   {
   for (.member(P,Participations))
   {	
   		if (P = [Participant,ParticipationsNo])
   		{
   			.print(Participant," has ",ParticipationsNo," participantions.");
   		}else
   		{
   			.print("Fail reading list.");
   		}
   }
    .print(" Winner: ",Winner, " has bid: ",Bid);
   }else { .print("No winner!!!");}
  
   -conversationID(Me,ja,ConvID);.

