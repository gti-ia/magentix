// Agent FRequest_Initiator in project conversationsFactory

/* Initial beliefs and rules */
participants([frequest_participant]).
timeOut(5000).
conversationTime(20000).
stuff([www,study,work,sport]).
tasksDone([]).

/* Initial goals */
!start.

/* Plans */
//@pstart[atomic] 
+!start : participants(P)&stuff([FirstStuff|Rest])
<- .my_name(Me);
   +conversationID(Me,frp,FirstStuff);
   !notifyParticipants(P,FirstStuff,frp).

+!notifyParticipants([],ConvID,Protocol)
<- .print("INITIATOR:--- PARTICIPANTS NOTIFIED.");
	+participantsNotified(ConvID,Protocol).

+!notifyParticipants([P|R],ConvID,Protocol): not notified(P,ConvID,Protocol)
<- .send(P,achieve,join(ConvID,Protocol));
   +notified(P,ConvID,Protocol);
   !notifyParticipants(R,ConvID,Protocol).

+joined(ConvID,frp)[source(P)]:timeOut(TO)&conversationTime(CT)
<- .print("INITIATOR:- Starting and making proposal to ",P);
   jasonAgentsConversations.conversationsFactory.initiator.ia_fipa_request_Initiator("start", P , TO, CT, "Fipa request conversation started",ConvID);
   jasonAgentsConversations.conversationsFactory.initiator.ia_fipa_request_Initiator("request",ConvID,P, ConvID). //The second term is the proposal

+taskdonesuccessfully(P,ConvID):tasksDone(T)
<- .print("INITIATOR:- The task was done successfully by agent ",P);
   .concat(T,[ConvID],T1);
   -+tasksDone(T1);
   jasonAgentsConversations.conversationsFactory.initiator.ia_fipa_request_Initiator("taskdone",ConvID).
   
+conversationended(ConvID):.my_name(Me)&conversationID(Me,frp,ConvID) 
<- -conversationID(Me,frp,ConvID);
  .print("INITIATOR:- CONVERSATION ",ConvID," ENDED!  ").
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  