// Agent fcn_initiator in project magentix2

/* Initial beliefs and rules */
participants([fcn_participant1,fcn_participant2,fcn_participant3,fcn_participant4,fcn_participant5]).
timeOut(5000).
deadlineForProposals(20000).
countEvaluated(C):-.count(proposal(P,S,ConvID,evaluated),C).
countProposal(C):-.count(proposal(P,S,ConvID)[source(self)],C).
countnotified(CN,Protocol):-.count(notified(P,ConvID,Protocol)[source(self)],CN).
stuff([clothe,shoes,furniture,www,toys,electronic]).

/* Initial goals */

!start.

/* Plans */

+!start : participants(P)&.my_name(Me)&stuff([FirstStuff|R])
<- +conversationID(Me,cnp,FirstStuff);
   -+stuff(R);
   !notifyParticipants(P,FirstStuff,cnp);
   !startConversation(FirstStuff,cnp).

+!notifyParticipants([],ConvID,Protocol)
<- .print("------- Participants notified.").


+!notifyParticipants([P|R],ConvID,Protocol): not notified(P,ConvID,Protocol)
<- .send(P,achieve,join(ConvID,Protocol));
   +notified(P,ConvID,Protocol);
   !notifyParticipants(R,ConvID,Protocol).

@p1[atomic]
+!startConversation(ConvID,Protocol):participants(P)&timeOut(TO)&deadlineForProposals(DL)&
	countnotified(CN,Protocol)&.length(P,CN)&stuff([FirstStuff|R])&.my_name(Me)
<-  .print("******** Starting conversation ",ConvID,"."); 
    .ia_FCN_Initiator("start", TO, DL, P, ConvID,ConvID);
    .print("******** Starting conversation ",FirstStuff,".");
     !notifyParticipants(P,FirstStuff,cnp);
    +conversationID(Me,cnp,FirstStuff);
    -+stuff(R);
    .ia_FCN_Initiator("start", TO, DL, P, FirstStuff,FirstStuff).

-!startConversation(ConvID)
<- .print("------- The conversation could't be started.").

@p2[atomic]
+proposalsevaluationtime(ConvID):proposal(P,S,ConvID)&.my_name(Me)&conversationID(Me,cnp,ConvID)&
   stuff([FirstStuff|StuffR])&participants(PAR)&timeOut(TO)&deadlineForProposals(DL)
<- .print("------- Proposals evaluation time. CID:",ConvID);
   -proposal(P,S,ConvID);
   ?countProposal(Z);
   .print("------- Evaluating proposal ",P," from agent ",S," and setting it as the best. CID: ",ConvID);
   +bestProposal(P,S,ConvID);
   +accepted([],ConvID);
   +rejected([],ConvID);
   !evaluateProposals(ConvID);
   ?bestProposal(BP,SBP,ConvID);
   -+accepted([BP,SBP],ConvID);
   ?rejected(R,ConvID);
   .print("******** Starting conversation ",FirstStuff,".");
    !notifyParticipants(PAR,FirstStuff,cnp);
   +conversationID(Me,cnp,ThirdStuff);
   -+stuff(StuffR);
   .ia_FCN_Initiator("start", TO, DL, PAR, FirstStuff,FirstStuff);
   .ia_FCN_Initiator("proposalsevaluated",[BP,SBP],R,ConvID).

+proposalsevaluationtime(ConvID):.my_name(Me)&conversationID(Me,cnp,ConvID)& not proposal(P,S,ConvID) 
<- .print("------- No proposals to evaluate.");
   .ia_FCN_Initiator("proposalsevaluated",[],[],ConvID).

//It was found a better propose than the one so far 
@p3[atomic]
+!evaluateProposals(ConvID):proposal(P,S,ConvID)&rejected(R,ConvID)&bestProposal(X,Sx,ConvID)&(P>X) 
<- .print("------- Evaluating proposal ",P," from agent ",S," and updating best because ", P, " > ", X,". CID: ",ConvID);
   -proposal(P,S,ConvID);
   .concat([X,Sx],R,R1); //the previous best is rejected
   -+rejected(R1,ConvID);
   -+bestProposal(P,S,ConvID);
   +proposal(P,S,ConvID,evaluated);
   !evaluateProposals(ConvID).
  
//The propose found is not better than the one so far
@p4[atomic]
+!evaluateProposals(ConvID):proposal(P,S,ConvID)&bestProposal(X,Sx,ConvID)&rejected(R,ConvID) 
<- .print("------- Evaluating proposal ",P," from agent ",S,". CID: ",ConvID);
   -proposal(P,S,ConvID);
   ?bestProposal(BP,BS,BConvID);
   .concat([P,S],R,R1);
   -+rejected(R1,ConvID);
   +proposal(P,S,ConvID,evaluated);
   !evaluateProposals(ConvID).

+!evaluateProposals(ConvID).

+resultsreceived(ConvID):.my_name(Me)&conversationID(Me,cnp,ConvID) 
 <- .print("------- Processing results... CID: ",ConvID);
    !processResults(ConvID); 
    .ia_FCN_Initiator("resultsprocessed",ConvID).
  
+!processResults(ConvID):taskDone(S,ConvID)
<- .print("------- Agent ",S," did the task successfully! CID: ",ConvID);
    -taskDone(S,ConvID);
    +taskDone(S,ConvID,analized);
    !processResults(ConvID).

+!processResults(ConvID):taskNotDone(S,ConvID)
<- .print("------- Agent ",S," didn't make the task successfully! CID: ",ConvID);
    -taskNotDone(S,ConvID);
    +taskNotDone(S,ConvID,analized);
    !processResults(ConvID).

+!processResults(ConvID).

+conversationended(ConvID):.my_name(Me)&conversationID(Me,cnp,ConvID) 
<- -conversationID(Me,cnp,ConvID);
  .print("------- Conversation ",ConvID," ENDED!  ++++++++++++++").
