// Agent fsubscribe_initiator in project magentix2

/* Initial beliefs and rules */
participants([fsubscribe_participant]).
timeOut(5000).

subscribe(1,price(euro,dollar,_),fsp1).
subscribe(2,price(euro,pound,_),fsp1).
subscribe(3,price(euro,yen,_),fsp1).
subscribe(4,package(p3,_),fsp2).
subscribe(5,package(p1,_),fsp2).
countsubscribe(C,ConvID):-.count(subscribe(N,A,ConvID),C).
countsubscribeyes(C,ConvID):-.count(subscribe(N,A,ConvID),C).
cancell_all(false).

/* Initial goals */
!start.

/* Plans */
+!start : participants(P)&subscribe(Id,Object,ConvID)&.my_name(Me)
<- +conversationID(Me,_,fsp,ConvID);
   !notifyParticipants(P,ConvID,fsp).

+!notifyParticipants([],ConvID,Protocol):notified(P,ConvID,Protocol)
<- .print("------- Participants notified.");
	+participantsNotified(P,ConvID,Protocol).

+!notifyParticipants([P|R],ConvID,Protocol): not notified(P,ConvID,Protocol)
<- .send(P,achieve,join(ConvID,Protocol));
   +notified(P,ConvID,Protocol);
   !notifyParticipants(R,ConvID,Protocol).

+participantsNotified(P,ConvID,Protocol):.my_name(Me)&timeOut(TO)&
		conversationID(Me,_,fsp,ConvID)&countsubscribe(C,ConvID)
<- .print("------- Starting and requesting subscription ",ConvID," to ",P);
   //New conversation
   .print("********* Starting: ", ConvID);
   -+conversationID(Me,P,fsp,ConvID);
   .findall(X,subscribe(N,X,fsp1),ObjectsList);
   if (ObjectsList==[])
   {.print("------- The conversation ",ConvID," couldn't start. No pending subscriptions.");
    -conversationID(Me,P,fsp,ConvID);}
   else
   {
   Max = C * 2 ;
   +counter(ConvID,0,Max);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_subscribe_Initiator("start", P , TO, "Fipa subscribe conversation started",ConvID);
   .wait(500);
   !updatingsubscriptions(ConvID);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_subscribe_Initiator("subscribe",ObjectsList,P, ConvID)}.

+!updatingsubscriptions(ConvID):.my_name(Me)&subscribe(Id,Object,ConvID)&
		conversationID(Me,P,fsp,ConvID)& not object(Id,Object,P)
<- +object(Id,Object,P); 
   !updatingsubscriptions(ConvID).

+!updatingsubscriptions(ConvID).

+subscribeagree(Sender,ConvID):not Sender = mail_agent
<- .print("------- ",Sender, " has agree the suscription request.");
   ?subscribe(Id,Object,NewConvID)& not ConvID = NewConvID;
   !!startnewConversation(NewConvID).
   
+!startnewConversation(NewConvID):timeOut(TO)&.my_name(Me)
<-  
   .findall(X,subscribe(N,X,NewConvID),ObjectsList);
   if (ObjectsList==[])
   {.print("------- The conversation ",NewConvID," couldn't start. No pending subscriptions.");
    -conversationID(Me,mail_agent,fsp,NewConvID);}
   else
   {
      +conversationID(Me,mail_agent,fsp,NewConvID);
     .send(mail_agent,achieve,join(NewConvID,fsp));
     .print("********* Starting: ", NewConvID);
      jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_subscribe_Initiator("start", mail_agent , TO, "Fipa subscribe conversation started",NewConvID);
      wait(500);
      !updatingsubscriptions(NewConvID);
      jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_subscribe_Initiator("subscribe",ObjectsList,mail_agent, NewConvID)
    }.
     
     
-!startnewConversation
<- .print("-------  Conversation with mail_agent has failed.").

@p1inform[atomic]
+inform(Sender,Object,Result,ConvID):subscribe(Id,Object,ConvID)&object(Id,_,Sender)&counter(ConvID,C,Max)&C<Max
<- -object(Id,_,Sender);
   +object(Id,Result,Sender);
   NewC = C + 1 ;
   -+counter(ConvID,NewC,Max);
   .print("------- Result ",Result," received and updated for object ",Object," from ",Sender,". Counter: ",NewC).
    
 
+inform(mail_agent,Object,Result,ConvID):subscribe(Id,Object,ConvID)
<- -object(Id,_,mail_agent);
   +object(Id,Result,mail_agent);
   .findall(B,object(A,package(P,B),C),L);
   .count(object(_,package(_,none),_),None);
   if (None==0) //All objects have value
   {   
   .count(object(_,package(_,_),_),Total);
   .count(object(_,package(_,entregado),_),Delivered);
   if (Delivered==Total)
     {!cancelconversation(ConvID);}
   }

   .print("------- Result ",Result," received and updated for object ",Object," from mail_agent.").

-inform(sender,Object,Result,ConvID).

@pcancel[atomic]
+inform(Sender,Object,Result,ConvID):subscribe(Id,Object,ConvID)&object(Id,_,Sender)&
    not canceled(ConvID)
<- !cancelconversation(ConvID).

+!cancelconversation(ConvID)
<- .print("------- Conversation ",ConvID," canceled by initiator.");
   +canceled(ConvID);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_subscribe_Initiator("cancel", ConvID).
   
+conversationended(ConvID,Result):.my_name(Me)&conversationID(Me,P,fsp,ConvID)
<- -conversationID(Me,P,fsp,ConvID);
  .print("------- Conversation ",ConvID," ENDED! Result: ",Result," ++++++++++++++").