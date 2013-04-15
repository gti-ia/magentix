// Agent frecruiting_initiator in project magentix2

/* Initial beliefs and rules */
recruiter(frecruiting_participant).
timeOut(30000).
condition(available). //Condition to fulfill
maxnumberofagents(3).

/* Initial goals */

!start.

/* Plans */

@pstart[atomic] 
+!start : recruiter(P)&.my_name(Me)
<- +conversationID(Me,frcp,frc1);
   .print("INITIATOR:--- SENDING INVITATION TO PARTICIPANT...");
   .send(P,tell,join(frc1,frcp)).

+joined(ConvID)[source(P)] : recruiter(P)&timeOut(TO)&.my_name(Me)&
condition(C)&maxnumberofagents(M) //C is the proxy message
<- jasonAgentsConversations.conversationsFactory.initiator.ia_fipa_recruiting_Initiator("start",TO,C,M,P, "askingForRecruiting",ConvID).

+receiveinform(P,ConvID):recruiter(P)
<- .print("INITIATOR:--- THE RECRUITING PROTOCOL WAS DONE SUCCESSFULLY!.");
   jasonAgentsConversations.conversationsFactory.initiator.ia_fipa_recruiting_Initiator("receiveinform",ConvID).

+conversationended(P,ConvID):.my_name(Me)&recruiter(P)&conversationresult(Result,ConvID)
<- .print("INITIATOR:--- THE CONVERSATION FINISHED WITH RESULT: ",Result);
   -conversationID(Me,_,ConvID).