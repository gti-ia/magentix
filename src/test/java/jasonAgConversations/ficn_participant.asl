// Agent fcn_participant in project magentix2

/* Initial beliefs and rules */
wantTonegotiateWith(ficn_initiator).
interestingThings([clothe,toys,shoes]).
notInterestingThings([furniture,electronic]).
//timeOut(5000).
mood(ok).

/* Initial goals */


/* Plans */
+!join(ConvID,Protocol)[source(S)]: wantTonegotiateWith(S)&.my_name(Me) //&timeOut(TO)
<-  +joined(ConvID);
    .ia_FICN_Participant("joinconversation",ConvID).

+callforproposal(Sender,Proposal,ConvID):wantTonegotiateWith(Sender)&interestingThings(L)&.member(Proposal,L)&joined(ConvID)
<- .random(P);
   P2 = P * 20 ;
   .print("------- I've recived a call for proposal: ",Proposal," and send mine: ",P2,".");
   .ia_FICN_Participant("makeproposal",P2,ConvID).

+callforproposal(Sender,Proposal,ConvID):wantTonegotiateWith(Sender)&notInterestingThings(L)&.member(Proposal,L)&joined(ConvID)
<- .print("------- I've recived a call for proposal: ",Proposal," and i've rejected it.");
   .ia_FICN_Participant("refuse",ConvID).


+callforproposal(Sender,Proposal,ConvID):wantTonegotiateWith(Sender)&interestingThings(L)&joined(ConvID)
<- .print("------- I've recived a call for proposal: ",Proposal," and I didn't understand it. ");
   .ia_FICN_Participant("notunderstood",ConvID).

+timetodotask(Sender,ConvID):mood(ok)&joined(ConvID)
<- .print("------- I feel fine, I'm going to make the task.");
   .ia_FICN_Participant("taskdone","OK, i did the task",ConvID).
   
+timetodotask(Sender,ConvID):joined(ConvID)
<- .print("------- I don't feel fine, I don't do the task.");
   .ia_FICN_Participant("tasknotdone","Task failed!",ConvID).
   
+conversationended(ConvID):joined(ConvID)
<- -joined(ConvID);
   .print("------- Getting out of the conversation. **************************").
   