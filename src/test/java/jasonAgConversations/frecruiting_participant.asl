// Agent frecruiting_participant in project magentix2

/* Initial beliefs and rules */
allowed([frecruiting_initiator]).
registeredagents(R):-.findall(X,logged(X),R).
someregistered:-.count(logged(X),C)&C>0.
countregistered(C):-.count(logged(X),C).

/* Initial goals */


/* Plans */
+!join(ConvID,frcp)[source(S)]:allowed(A)&.member(S,A)
<- .send(S,tell,joined(ConvID));
   .ia_fipa_recruiting_Participant("joinconversation",ConvID).
   
+receiveproxy(Sender,Content,TO,ConvID):allowed(A)&.member(Sender,A)
<- .print("------- I've received a proxy message for doing: ",Content," from ",Sender," and i'm agree.");
   +timeOut(TO);
   .ia_fipa_recruiting_Participant("agree",Content,ConvID).

+receiveproxy(Sender,Content,TO,ConvID)
<- .print("------- I've received a proxy message for doing: ",Content," from ",Sender," but i'm not agree.");
   .ia_fipa_recruiting_Participant("refuse",Content,ConvID).

+!agentslocated(Condition,TO,ConvID)
<- .findall(X,proxyrequest(Condition,accepted,X),A);
   .count(proxyrequest(Condition,accepted,_),C);
   .print("------- I've located: ",C," and they are: ",A," for condition: ",Condition);
   .ia_fipa_recruiting_Participant("locateagents",A,ConvID).

   
+timetolocateagents(Sender,Condition,ConvID):timeOut(TO)&countregistered(C)&C>0
<- ?registeredagents(R);
   !locateagents(Condition,TO,ConvID,R);
   !agentslocated(Condition,TO,ConvID).

-timetolocateagents(Sender,Condition,ConvID):countregistered(C)&C=0
<- .print("------- No agents registered for condition: ",Condition,".");
   .ia_fipa_recruiting_Participant("locateagents",[],ConvID).

+!locateagents(Condition,TO,ConvID,[]).
 
+!locateagents(Condition,TO,ConvID,[H|T])
<- .send(H,askOne,Condition,Result);
   !verifycondition(Condition,Result,H);
   !locateagents(Condition,TO,ConvID,T).
   
+!verifycondition(Condition,false,Agent)
<- +proxyrequest(Condition,notaccepted,Agent).

+!verifycondition(Condition,Cond,Agent)
<- +proxyrequest(Condition,accepted,Agent).

+infotosend(Sender,ConvID)
<- .print("------- I confirm the successfully agents search to initiator.");
   .ia_fipa_recruiting_Participant("inform","The agents search was done successfully!",ConvID).

+conversationended(Sender,Result,ConvID)
<- .print("------- The conversation ",ConvID," ended for me! Result: ", Result).