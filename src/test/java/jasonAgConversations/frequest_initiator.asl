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
count(0).

/* Initial goals */
!start.

+!notifyParticipants([],ConvID,Protocol):notified(P,ConvID,Protocol)
<- .print("------- Participants notified.");
	+participantsNotified(P,ConvID,Protocol).

+!notifyParticipants([P|R],ConvID,Protocol): not notified(P,ConvID,Protocol)
<- .send(P,achieve,join(ConvID,Protocol));
   +notified(P,ConvID,Protocol);
   !notifyParticipants(R,ConvID,Protocol).

//+participantsNotified(P,ConvID,Protocol):timeOut(TO)&stuff(NewConvID)& not NewConvID==ConvID&data(Data)
+!start : participants([P|PTail])&stuff(FirstStuff)&count(Count)&.my_name(Me)&timeOut(TO)&data(Data)
<- NewCount = Count + 1 ;
   -+count(NewCount);
   .print("------- Starting and making proposal to ",P);
   
   //New conversation 
   .print("********* Starting: ", FirstStuff);
   +conversationID(Me,frp,FirstStuff);
   -stuff(FirstStuff);
   !notifyParticipants([P|PTail],FirstStuff,frp);
   .ia_fipa_request_Initiator("start", P , TO, "Fipa request conversation started",FirstStuff);
   //.wait(500);
   .ia_fipa_request_Initiator("request",FirstStuff,P,data(Data), FirstStuff);
   
   //.send(P,tell,join(NewConvID,frp));
   ?stuff(NewConvID);
   .print("********* Starting: ", NewConvID);
   +conversationID(Me,frp,NewConvID);
   !notifyParticipants([P|PTail],NewConvID,frp);
   -stuff(NewConvID);
   .ia_fipa_request_Initiator("start", P , 5000, "Fipa request conversation started",NewConvID);
   //.wait(500);
   .ia_fipa_request_Initiator("request",NewConvID,P,data(Data), NewConvID). //The second term is the proposal

+taskdonesuccessfully(P,Result,ConvID):tasksDone(T)&timeOut(TO)&stuff(NewConvID)& not NewConvID==ConvID&data(Data)
<- .concat(T,[ConvID],T1);
   -+tasksDone(T1);
   .print("------- The task ",ConvID," was done successfully by agent ",P,". Result: ",Result);
    ?count(Count);
   .ia_fipa_request_Initiator("taskdone",ConvID);
    if (Count<2){!start;}. 

+taskdonesuccessfully(P,Result,ConvID):tasksDone(T)
<- .print("------- The task ",ConvID," was done successfully by agent ",P,". Result: ",Result);
   .concat(T,[ConvID],T1);
   -+tasksDone(T1);
   .ia_fipa_request_Initiator("taskdone",ConvID).



+conversationended(ConvID, Result):.my_name(Me)&conversationID(Me,frp,ConvID)
<- -conversationID(Me,frp,ConvID);
  .print("------- Conversation ",ConvID," ENDED!  Result: ",Result," ++++++++++++++").
