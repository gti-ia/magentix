// Agent FRequest_Participant in project conversationsFactory

/* Initial beliefs and rules */
allowed([frecruiting_initiator]).
registeredagents(R):-.findall(X,logged(X),R).
someregistered:-.count(logged(X),C)&C>0.
countregistered(C):-.count(logged(X),C).

/* Initial goals */


/* Plans */
+join(ConvID,frcp)[source(S)]:allowed(A)&.member(S,A)
<- .send(S,tell,joined(ConvID));
   jasonAgentsConversations.conversationsFactory.participant.ia_fipa_recruiting_Participant("joinconversation",ConvID).
   
+receiveproxy(Sender,Content,TO,ConvID):allowed(A)&.member(Sender,A)
<- .print("PARTICIPANT: I'VE RECEIVED A PROXY MESSAGE FOR DOING: ",Content," FROM ",Sender," AND I'M AGREE.");
   +timeOut(TO);
   jasonAgentsConversations.conversationsFactory.participant.ia_fipa_recruiting_Participant("agree",Content,ConvID).

+receiveproxy(Sender,Content,TO,ConvID)
<- .print("PARTICIPANT: I'VE RECEIVED A PROXY MESSAGE FOR DOING: ",Content," FROM ",Sender," BUT I'M NOT AGREE.");
   jasonAgentsConversations.conversationsFactory.participant.ia_fipa_recruiting_Participant("refuse",Content,ConvID).

+!agentslocated(Condition,TO,ConvID)
<- .findall(X,proxyrequest(accepted,X),A);
   .count(proxyrequest(accepted,_),C);
   .print("PARTICIPANT: I'VE LOCATED: ",C," AND THEY ARE: ",A);
   jasonAgentsConversations.conversationsFactory.participant.ia_fipa_recruiting_Participant("locateagents",A,ConvID).

   
+timetolocateagents(Sender,Condition,ConvID):timeOut(TO)&countregistered(C)&C>0
<- ?registeredagents(R);
   !locateagents(Condition,TO,ConvID,R);
   !agentslocated(Condition,TO,ConvID).

-timetolocateagents(Sender,Condition,ConvID):countregistered(C)&C=0
<- .print("PARTICIPANT: NO AGENTS REGISTERED.");
   jasonAgentsConversations.conversationsFactory.participant.ia_fipa_recruiting_Participant("locateagents",[],ConvID).

+!locateagents(Condition,TO,ConvID,[]).
 
+!locateagents(Condition,TO,ConvID,[H|T])
<- .send(H,askOne,Condition,Result);
   !verifycondition(Result,H);
   !locateagents(Condition,TO,ConvID,T).
   
+!verifycondition(false,Agent)
<- +proxyrequest(notaccepted,Agent).

+!verifycondition(Cond,Agent)
<- +proxyrequest(accepted,Agent).

+infotosend(Sender,ConvID)
<- .print("PARTICIPANT: I CONFIRM THE SUCCESSFULLY AGENTS SEARCH TO INITIATOR.");
   jasonAgentsConversations.conversationsFactory.participant.ia_fipa_recruiting_Participant("inform","The agents search was done successfully!",ConvID).

+conversationended(Sender,Result,ConvID)
<- .print("PARTICIPANT: THE CONVERSATION ENDED! Result: ", Result).