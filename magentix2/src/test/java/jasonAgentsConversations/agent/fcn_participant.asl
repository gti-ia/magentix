// Agent fcn_participant in project conversationsFactory

/* Initial beliefs and rules */
wantTonegotiateWith(fcn_initiator).
interestingThings([clothe,shoes]).
notInterestingThings([furniture,electronic]).
timeOut(5000).
mood(ok).

/* Initial goals */


/* Plans */
+!join(ConvID,Protocol)[source(S)]: timeOut(TO)&wantTonegotiateWith(S)&.my_name(Me)
<-  jasonAgentsConversations.conversationsFactory.participant.ia_FCN_Participant("joinconversation",TO,ConvID).


+callforproposal(Sender,Proposal,ConvID):wantTonegotiateWith(Sender)&interestingThings(L)&.member(Proposal,L)
<- .random(P);
   P2 = P * 20 ;
   .print("PARTICIPANT:- I'VE RECEIVED A CALL FOR PROPOSAL: ",Proposal," AND SEND MINE: ",P2,".");
   jasonAgentsConversations.conversationsFactory.participant.ia_FCN_Participant("makeproposal",P2,ConvID).

+callforproposal(Sender,Proposal,ConvID):wantTonegotiateWith(Sender)&notInterestingThings(L)&.member(Proposal,L)
<- .print("PARTICIPANT:- I'VE RECEIVED A CALL FOR PROPOSAL: ",Proposal," AND I'VE REJECTED IT.");
   jasonAgentsConversations.conversationsFactory.participant.ia_FCN_Participant("refuse",ConvID).


+callforproposal(Sender,Proposal,ConvID):wantTonegotiateWith(Sender)
<- .print("PARTICIPANT:- I'VE RECEIVED A CALL FOR PROPOSAL: ",Proposal," AND I DID'N UNDERSTAND IT.");
   jasonAgentsConversations.conversationsFactory.participant.ia_FCN_Participant("notunderstood",ConvID).

+timetodotask(Sender,ConvID):mood(ok)
<- .print("PARTICIPANT:- I FEEL FINE, I'M GOING TO MAKE THE TASK.");
   jasonAgentsConversations.conversationsFactory.participant.ia_FCN_Participant("taskdone","OK, i did the task",ConvID).
   
+timetodotask(Sender,ConvID)
<- .print("PARTICIPANT:- I DON'T FEEL FINE, I DON'T DO THE TASK.");
   jasonAgentsConversations.conversationsFactory.participant.ia_FCN_Participant("tasknotdone","Task failed!",ConvID).
   