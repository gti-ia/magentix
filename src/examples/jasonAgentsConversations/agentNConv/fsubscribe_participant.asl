// Agent rate_change_agent in project magentix2

/* Initial beliefs and rules */
wantToAnswer(fsubscribe_initiator,fsp).
price(euro,yen,_).
price(euro,dollar,_).
price(euro,pound,_).

/* Plans */

+!join(ConvID,Protocol)[source(S)]:wantToAnswer(S,Protocol)
<- jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_subscribe_Participant("joinconversation",ConvID).

+subscriberequest(Sender,ObjectsList,ConvID):not wantToAnswer(Sender,Protocol)
<- .print("------- I've received the subscribe request: ",Object," AND I REFUSE.");
   jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_subscribe_Participant("refuse",ConvID).

+subscriberequest(Sender,ObjectsList,ConvID):wantToAnswer(Sender,Protocol)
<- .print("------- I've received the subscribe request: ",ObjectsList," AND I'M AGREE."); 
   jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_subscribe_Participant("agree",ConvID).

//Belief added by the platform
+subscribe(Sender,Object,ConvID):wantToAnswer(Sender,Protocol)
<- +subscript(Object,Sender,ConvID);
   .print("------- Subscribing: ",Object).

@pnotify 
+Object[Source]:subscript(Object1,fsubscribe_initiator,fsp1)&.concat("",Object1,InitialObj)&Object1 = Object
<-
   .print("------- Informing of change ",Object," for object ",InitialObj);
   jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_subscribe_Participant("inform", InitialObj,Object,fsp1).

+conversationcanceledbyinitiator(ConvID)
<- !removesubscriptions(ConvID).

+!removesubscriptions(ConvID):subscript(Object,Sender,ConvID)
<- .abolish(subscript(_,_,ConvID));
   .print("------- Subscriptions right removed.");
   jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_subscribe_Participant("informcancel",ConvID).

-!removesubscriptions(ConvID)
<- .print("------- Subscriptions removal failed. Failed cancel.");
    jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_subscribe_Participant("failurecancel",ConvID).
   
