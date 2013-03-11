// Agent fcn_participant in project magentix2

/* Initial beliefs and rules */
wantTonegotiateWith(fcn_initiator).
interestingThings([clothe,toys,shoes]).
notInterestingThings([furniture,electronic]).
//timeOut(5000).
mood(ok).

/* Initial goals */


/* Plans */
+!join(ConvID,Protocol)[source(S)]: wantTonegotiateWith(S)&.my_name(Me) //&timeOut(TO)
<-  .ia_FCN_Participant("joinconversation",ConvID).

@p1[atomic]
+callforproposal(Sender,Proposal,ConvID):wantTonegotiateWith(Sender)&interestingThings(L)&.member(Proposal,L)
<- .random(P);
   P2 = P * 20 ;
   .print("------- I've recived a call for proposal: ",Proposal," and send mine: ",P2,".");
   .ia_FCN_Participant("makeproposal",P2,ConvID).

@p2[atomic]
+callforproposal(Sender,Proposal,ConvID):wantTonegotiateWith(Sender)&notInterestingThings(L)&.member(Proposal,L)
<- .print("------- I've recived a call for proposal: ",Proposal," and i've rejected it. ConvID ",ConvID);
   .ia_FCN_Participant("refuse",ConvID).

@p3[atomic]
+callforproposal(Sender,Proposal,ConvID):wantTonegotiateWith(Sender)&interestingThings(L)
<- .print("------- I've recived a call for proposal: ",Proposal," and I didn't understand it. ");
   .ia_FCN_Participant("notunderstood",ConvID).

@p4[atomic]
+timetodotask(Sender,ConvID):mood(ok)
<- .print("------- I feel fine, I'm going to make the task.");
   .ia_FCN_Participant("taskdone","OK, i did the task",ConvID).

@p5[atomic]   
+timetodotask(Sender,ConvID)
<- .print("------- I don't feel fine, I don't do the task.");
   .ia_FCN_Participant("tasknotdone","Task failed!",ConvID).
   