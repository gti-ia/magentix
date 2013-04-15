// Agent frequest_initiator in project magentix2


/* Initial beliefs and rules */
participants([frequest_participant]).
timeOut(5000).
conversationTime(20000).
stuff(study).
stuff(work).
stuff(www).
stuff(sport).
tasksDone([]).

/* Initial goals */
!start.

/* Plans */
//@pstart[atomic] 
+!start : participants(P)&stuff(FirstStuff)
<- .my_name(Me);
   +conversationID(Me,frp,FirstStuff);
   -stuff(FirstStuff);
   !notifyParticipants(P,FirstStuff,frp).

+!notifyParticipants([],ConvID,Protocol):notified(P,ConvID,Protocol)
<- .print("------- Participants notified.");
	+participantsNotified(P,ConvID,Protocol).

+!notifyParticipants([P|R],ConvID,Protocol): not notified(P,ConvID,Protocol)
<- .send(P,achieve,join(ConvID,Protocol));
   +notified(P,ConvID,Protocol);
   !notifyParticipants(R,ConvID,Protocol).

+participantsNotified(P,ConvID,Protocol):timeOut(TO)&conversationTime(CT)&stuff(NewConvID)& not NewConvID==ConvID
<- .print("------- Starting and making proposal to ",P);
   
   //New conversation 
   .print("********* Starting: ", ConvID);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_request_Initiator("start", P , TO, CT, "Fipa request conversation started",ConvID);
   .wait(500);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_request_Initiator("request",ConvID,P, ConvID);
   
   //.send(P,tell,join(NewConvID,frp));
   .print("********* Starting: ", NewConvID);
   +conversationID(Me,frp,NewConvID);
   -stuff(NewConvID);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_request_Initiator("start", P , 5000, 20000, "Fipa request conversation started",NewConvID);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_request_Initiator("request",NewConvID,P, NewConvID). //The second term is the proposal

+participantsNotified(P,ConvID,Protocol):timeOut(TO)&conversationTime(CT)
<- .print("------- Starting and making proposal to ",P);
   
   //New conversation 
   .print("********* Starting: ", ConvID);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_request_Initiator("start", P , TO, CT, "Fipa request conversation started",ConvID);
   .wait(500);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_request_Initiator("request",ConvID,P, ConvID).


+taskdonesuccessfully(P,ConvID):tasksDone(T)&timeOut(TO)&conversationTime(CT)&stuff(NewConvID)& not NewConvID==ConvID
<- .concat(T,[ConvID],T1);
   -+tasksDone(T1);
   .print("------- The task ",ConvID," was done successfully by agent ",P);
  
   //New conversation   
   .print("********* Starting: ",NewConvID); 
   +conversationID(Me,frp,NewConvID);
   -stuff(NewConvID);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_request_Initiator("start", P , TO, CT, "Fipa request conversation started",NewConvID);

   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_request_Initiator("request",NewConvID,P, NewConvID);

   
   //New conversation  
   if (stuff(NewConvID2))
   {.print("********* Starting: ",NewConvID2);
   +conversationID(Me,frp,NewConvID2);
   -stuff(NewConvID2);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_request_Initiator("start", P , TO, CT, "Fipa request conversation started",NewConvID2);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_request_Initiator("request",NewConvID2,P, NewConvID2);}

   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_request_Initiator("taskdone",ConvID). //The second term is the proposal.

+taskdonesuccessfully(P,ConvID):tasksDone(T)
<- .print("------- The task ",ConvID," was done successfully by agent ",P);
   .concat(T,[ConvID],T1);
   -+tasksDone(T1);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_request_Initiator("taskdone",ConvID).



+conversationended(ConvID):.my_name(Me)&conversationID(Me,frp,ConvID)
<- -conversationID(Me,frp,ConvID);
  .print("------- Conversation ",ConvID," ENDED!  ++++++++++++++").
