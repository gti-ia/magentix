// Agent frequest_initiator in project magentix2


/* Initial beliefs and rules */
participants([frequestw_participant]).
timeOut(5000).
tasksDone([]).
pendingreq(sell(shoes)).
pendingreq(sell(laptop)).
pendingreq(rent(car)).
pendingreq(rent(bicicle)).
req(sell(shoes),condition(price(shoes,X))):- price(shoes,X)&X<35.
req(sell(laptop),condition(price(laptop,X))):- price(laptop,X)&X<500.
req(rent(car),condition(weather(sunny))):- weather(sunny).
req(rent(bicicle),condition(weather(sunny))):- weather(sunny).

/* Initial goals */
!start.

/* Plans */
//@pstart[atomic] 
+!start : participants(P)&pendingreq(FirstReq)
<- .my_name(Me);
   +conversationID(Me,frwp,FirstReq,none);
   !notifyParticipants(P,FirstReq,frwp).

+!notifyParticipants([],ConvID,Protocol):notified(P,ConvID,Protocol)
<- .print("------- Participants notified.");
	+participantsNotified(P,ConvID,Protocol).

+!notifyParticipants([P|R],ConvID,Protocol): not notified(P,ConvID,Protocol)&.my_name(Me)
<- .send(P,achieve,join(ConvID,Protocol));
   +notified(P,ConvID,Protocol);
   -+conversationID(Me,frwp,ConvID,P);
   !notifyParticipants(R,ConvID,Protocol).

@pstart[atomic]
+participantsNotified(P,ConvID,Protocol):timeOut(TO)&pendingreq(NewReq)&not NewReq==ConvID&.my_name(Me)
<- .print("------- Starting and making proposal to ",P);
   
   //New conversation 
   .print("********* Starting: ", ConvID);
   //Timeout is the time for waiting for the condition and it is not estrictely necessary. If it is not provided
   //the time out is 30 seconds by default.
   .ia_fipa_requestw_Initiator("start", P , TO, "Fipa request when conversation started",ConvID);
   !verifycondition(ConvID);
   
   .print("********* Starting: ", NewReq);
   +conversationID(Me,frwp,NewReq,P);
   .ia_fipa_requestw_Initiator("start", P , TO, "Fipa request when conversation started",NewReq);
   !verifycondition(NewReq).
   

+!verifycondition(ConvID):conversationID(Me,frwp,ConvID,P)
<- .print("Verifying condition.",ConvID);
   ?req(ConvID,condition(Cond));
   .ia_fipa_requestw_Initiator("request",ConvID,P, ConvID).

-!verifycondition(ConvID)
<- .print("Condition not fullfilled yet.");.


+Cond[source(Agent)]:req(ConvID,condition(Cond))&conversationID(Me,frwp,ConvID,P)
<-  .print("Incoming information... ",Cond," ConvID ",ConvID); 
    .ia_fipa_requestw_Initiator("request",ConvID,P, ConvID).

+taskdonesuccessfully(P,ConvID):tasksDone(T)
<- .concat(T,[ConvID],T1);
   -+tasksDone(T1);
   .print("------- The task ",ConvID," was done successfully by agent ",P);
   .ia_fipa_requestw_Initiator("taskdone",ConvID). //The second term is the proposal.


+conversationended(ConvID,"INFORM"):.my_name(Me)&conversationID(Me,frwp,ConvID,P)
<- -conversationID(Me,frwp,ConvID);
   -pendingreq(FirstReq);
   +ConvID;
   .print("------- Conversation ",ConvID," ENDED SUCCESSFULLY! Result: inform ++++++++++++++").
   
+conversationended(ConvID,Result):.my_name(Me)&conversationID(Me,frwp,ConvID,P)
<- -conversationID(Me,frwp,ConvID);
   -pendingreq(FirstReq);
   .print("------- Conversation ",ConvID," ENDED! Result: ",Result," ++++++++++++++").
   
