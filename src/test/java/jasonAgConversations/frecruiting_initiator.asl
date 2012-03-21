// Agent frecruiting_initiator in project magentix2

/* Initial beliefs and rules */
recruiter(frecruiting_participant).
timeOut(30000).
condition(available). //Condition to fulfill
condition(ready).
condition(open).
maxnumberofagents(3).
conversations([frc1,frc2,frc3,frc4]).

/* Initial goals */

!start.

/* Plans */

@pstart[atomic] 
+!start : recruiter(P)&.my_name(Me)&conversations([C1|R])
<- .print("------- Sending invitation to participant...");
   +conversationID(Me,frcp,C1);
   -+conversations(R);
   .send(P,achieve,join(C1,frcp)).

+joined(ConvID)[source(P)] : recruiter(P)&timeOut(TO)&.my_name(Me)&
condition(Cond1)&maxnumberofagents(M)&conversations([C1,C2|R]) //C is the proxy message
<- .print("******** Starting conversation ",ConvID,".");
   .ia_fipa_recruiting_Initiator("start",TO,Cond1,M,P, "askingForRecruiting",ConvID);
   -condition(Cond1);
   ?condition(Cond2);
   +conversationID(Me,frcp,C1);
   -+conversations([C2|R]);
   .print("******** Starting conversation ",C1,".");
   .ia_fipa_recruiting_Initiator("start",TO,Cond2,M,P, "askingForRecruiting",C1);
   -condition(Cond2);
   ?condition(Cond3);
   +conversationID(Me,frcp,C2);
   -+conversations(R);
   .print("******** Starting conversation ",C2,".");
   .ia_fipa_recruiting_Initiator("start",TO,Cond3,M,P, "askingForRecruiting",C2).
   

+receiveinform(P,ConvID):recruiter(P)
<- .print("------- The recruiting protocol was done successfully!.");
   .ia_fipa_recruiting_Initiator("receiveinform",ConvID).

+conversationended(P,ConvID):.my_name(Me)&recruiter(P)&conversationresult(Result,ConvID)
<- .print("------- Conversation ",ConvID," ended! Result: ",Result," ++++++++++++++").
   -conversationID(Me,_,ConvID).