// Agent fcn_initiator in project magentix2

/* Initial beliefs and rules */
participants([ficn_participant1,ficn_participant2,ficn_participant3,ficn_participant4,ficn_participant5]).
timeOut(5000).
deadlineForProposals(13000).
countEvaluated(C):-.count(proposal(P,S,ConvID,evaluated),C).
countProposal(C):-.count(proposal(P,S,ConvID)[source(self)],C).
countnotified(CN,Protocol):-.count(notified(P,ConvID,Protocol)[source(self)],CN).
stuff([clothe,shoes,furniture,www,toys,electronic]).

/* Initial goals */

!start.

/* Plans */

+!start : participants(P)&.my_name(Me)&stuff([FirstStuff|R])
<- +conversationID(Me,cinp,FirstStuff);
   +iterations(FirstStuff,0);
   -+stuff(R);
   !notifyParticipants(P,FirstStuff,cinp);
   //.wait(500);
   !startConversation(FirstStuff,cinp).

+!notifyParticipants([],ConvID,Protocol)
<- .print("------- Participants notified.").


+!notifyParticipants([P|R],ConvID,Protocol): not notified(P,ConvID,Protocol)
<- .send(P,achieve,join(ConvID,Protocol));
   +notified(P,ConvID,Protocol);
   !notifyParticipants(R,ConvID,Protocol).

+!startConversation(ConvID,Protocol):participants(P)&timeOut(TO)&deadlineForProposals(DL)&
	countnotified(CN,Protocol)&.length(P,CN)&stuff([SecondStuff|R])&.my_name(Me)
<-  .print("******** Starting conversation ",ConvID,"."); 
    .ia_FICN_Initiator("start", TO, DL, P, ConvID,ConvID);
    .print("******** Starting conversation ",SecondStuff,".");
    +conversationID(Me,cinp,SecondStuff);
    +iterations(SecondStuff,0);
    -+stuff(R);
    !notifyParticipants(P,SecondStuff,cinp);
   // .wait(500);
    .ia_FICN_Initiator("start", TO, DL, P, SecondStuff,SecondStuff).

-!startConversation(ConvID)
<- .print("------- The conversation could't be started.").

@p1[atomic]
+proposalsevaluationtime(ConvID):proposal(P,S,ConvID)&.my_name(Me)&conversationID(Me,cinp,ConvID)
   //&stuff([ThirdStuff|StuffR])&timeOut(TO)&participants(PAR)&deadlineForProposals(DL)
<- .print("------- Proposals evaluation time. CID:",ConvID);
   .findall(P,proposal(P,S,ConvID),LP);
   .print("Proposals of participants ",LP);
   +proposal(P,S,ConvID,evaluated);
   -proposal(P,S,ConvID);
   ?countProposal(Z);
   .print("------- Evaluating proposal ",P," from agent ",S," and setting it as the best. CID: ",ConvID);
   -bestProposal(_,_,ConvID);
   +bestProposal(P,S,ConvID);
   -accepted(_,ConvID);
   +accepted([],ConvID);
   -rejected(_,ConvID);
   +rejected([],ConvID);
   !evaluateProposals(ConvID);
   ?bestProposal(BP,SBP,ConvID);
   -+accepted([BP,SBP],ConvID);
   ?rejected(R,ConvID);
   //.print("******** Starting conversation ",ThirdStuff,".");
   //+conversationID(Me,cinp,ThirdStuff);
  // +iterations(ThirdStuff,0);
   //-+stuff(StuffR);
  // .ia_FICN_Initiator("start", TO, DL, PAR, ThirdStuff,ThirdStuff);
   ?iterations(ConvID,I);
   NewI = I + 1 ;
   -iterations(ConvID,I);
   +iterations(ConvID,NewI);
   !removeproposals(ConvID);
   if (NewI <= 5)
      {.ia_FICN_Initiator("proposalsevaluated",[BP,SBP],R,ConvID);
      .print("PROPOSALS EVALUATED CON END EN EL 1RO");}
   else
      {.ia_FICN_Initiator("proposalsevaluated",[BP,SBP],R,end,ConvID);
      .print("PROPOSALS EVALUATED SIN END EN EL 1RO");}.

+proposalsevaluationtime(ConvID):.my_name(Me)&conversationID(Me,cinp,ConvID)& not proposal(P,S,ConvID) 
<- .print("------- No proposals to evaluate.");
   ?iterations(ConvID,I);
   NewI = I +1 ;
   -iterations(ConvID,I);
   +iterations(ConvID,NewI);
   if (NewI <= 5)
     {.ia_FICN_Initiator("proposalsevaluated",[],[],ConvID);
     .print("PROPOSALS EVALUATED CON END EN EL 2DO");}
   else
     {.ia_FICN_Initiator("proposalsevaluated",[],[],end,ConvID);
     .print("PROPOSALS EVALUATED SIN END EN EL 2DO");}.


//It was found a better propose than the one so far 
@p2[atomic]
+!evaluateProposals(ConvID):proposal(P,S,ConvID)&rejected(R,ConvID)&bestProposal(X,Sx,ConvID)&(P>X) 
<- .print("------- Evaluating proposal ",P," from agent ",S," and updating best because ", P, " > ", X,". CID: ",ConvID);
   -proposal(P,S,ConvID);
   .concat([X,Sx],R,R1); //the previous best is rejected
   -+rejected(R1,ConvID);
   -+bestProposal(P,S,ConvID);
   +proposal(P,S,ConvID,evaluated);
   !evaluateProposals(ConvID).
  
//The propose found is not better than the one so far
@p3[atomic]
+!evaluateProposals(ConvID):proposal(P,S,ConvID)&bestProposal(X,Sx,ConvID)&rejected(R,ConvID) 
<- .print("------- Evaluating proposal ",P," from agent ",S,". CID: ",ConvID);
   ?bestProposal(BP,BS,BConvID);
   .concat([P,S],R,R1);
   -+rejected(R1,ConvID);
   +proposal(P,S,ConvID,evaluated);
   -proposal(P,S,ConvID);
   !evaluateProposals(ConvID).

+!evaluateProposals(ConvID).

+!removeproposals(ConvID):proposal(P,S,ConvID,evaluated)
<- //.print("|||||||||||||||| - Removing evaluated ",P,",",S,",",ConvID);
   -proposal(P,S,ConvID,evaluated);
   !removeproposals(ConvID).
   
+!removeproposals(ConvID).

+resultsreceived(ConvID):.my_name(Me)&conversationID(Me,cinp,ConvID) 
 <- .print("------- Processing results... CID: ",ConvID);
    !processResults(ConvID); 
    .ia_FICN_Initiator("resultsprocessed",ConvID).

@p4[atomic]  
+!processResults(ConvID):taskDone(S,ConvID)
<- .print("------- Agent ",S," did the task successfully! CID: ",ConvID);
    -taskDone(S,ConvID);
    +taskDone(S,ConvID,analized);
    !processResults(ConvID).

@p5[atomic]
+!processResults(ConvID):taskNotDone(S,ConvID)
<- .print("------- Agent ",S," didn't make the task successfully! CID: ",ConvID);
    -taskNotDone(S,ConvID);
    +taskNotDone(S,ConvID,analized);
    !processResults(ConvID).

+!processResults(ConvID).

+conversationended(ConvID):.my_name(Me)&conversationID(Me,cinp,ConvID) 
<- -conversationID(Me,cinp,ConvID);
   -iterations(ConvID,I);
  .print("------- Conversation ",ConvID," ENDED!  ++++++++++++++").
