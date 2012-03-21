// Agent frequest_initiator in project magentix2


/* Initial beliefs and rules */
data([name(Me),age(38),sex(m)]):-.my_name(Me).
participants([frequest_participant]).
timeOut(5000).
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

+participantsNotified(P,ConvID,Protocol):timeOut(TO)&stuff(NewConvID)& not NewConvID==ConvID&data(Data)
<- .print("------- Starting and making proposal to ",P);
   
   //New conversation 
   .print("********* Starting: ", ConvID);
   .ia_fipa_request_Initiator("start", P , TO, "Fipa request conversation started",ConvID);
   .wait(500);
   .ia_fipa_request_Initiator("request",ConvID,P,data(Data), ConvID);
   
   //.send(P,tell,join(NewConvID,frp));
   .print("********* Starting: ", NewConvID);
   +conversationID(Me,frp,NewConvID);
   -stuff(NewConvID);
   .ia_fipa_request_Initiator("start", P , 5000, "Fipa request conversation started",NewConvID);
   .ia_fipa_request_Initiator("request",NewConvID,P,data(Data), NewConvID). //The second term is the proposal

+participantsNotified(P,ConvID,Protocol):timeOut(TO)&data(Data)
<- .print("------- Starting and making proposal to ",P);
   
   //New conversation 
   .print("********* Starting: ", ConvID);
   .ia_fipa_request_Initiator("start", P , TO,  "Fipa request conversation started",ConvID);
   .wait(500);
   .ia_fipa_request_Initiator("request",ConvID,P,data(Data), ConvID).


+taskdonesuccessfully(P,Result,ConvID):tasksDone(T)&timeOut(TO)&stuff(NewConvID)& not NewConvID==ConvID&data(Data)
<- .concat(T,[ConvID],T1);
   -+tasksDone(T1);
   .print("------- The task ",ConvID," was done successfully by agent ",P,". Result: ",Result);
  
   //New conversation   
   .print("********* Starting: ",NewConvID); 
   +conversationID(Me,frp,NewConvID);
   -stuff(NewConvID);
   .ia_fipa_request_Initiator("start", P , TO,  "Fipa request conversation started",NewConvID);

   .ia_fipa_request_Initiator("request",NewConvID,P,data(Data),NewConvID);
   //New conversation  
   if (stuff(NewConvID2))
   {.print("********* Starting: ",NewConvID2);
   +conversationID(Me,frp,NewConvID2);
   -stuff(NewConvID2);
   .ia_fipa_request_Initiator("start", P , TO, "Fipa request conversation started",NewConvID2);
   .ia_fipa_request_Initiator("request",NewConvID2,P,data(Data), NewConvID2);}

   .ia_fipa_request_Initiator("taskdone",ConvID). 

+taskdonesuccessfully(P,Result,ConvID):tasksDone(T)
<- .print("------- The task ",ConvID," was done successfully by agent ",P,". Result: ",Result);
   .concat(T,[ConvID],T1);
   -+tasksDone(T1);
   .ia_fipa_request_Initiator("taskdone",ConvID).



+conversationended(ConvID, Result):.my_name(Me)&conversationID(Me,frp,ConvID)
<- -conversationID(Me,frp,ConvID);
  .print("------- Conversation ",ConvID," ENDED!  Result: ",Result," ++++++++++++++").
