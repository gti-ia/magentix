// Agent fcn_initiator in project conversationsFactory

/* Initial beliefs and rules */
participants([fcn_participant1,fcn_participant2,fcn_participant3,fcn_participant4,fcn_participant5]).
timeOut(5000).
conversationTime(20000).
countEvaluated(C):-.count(proposal(P,S,ConvID,evaluated),C).
countProposal(C):-.count(proposal(P,S,ConvID)[source(self)],C).
countnotified(CN,Protocol):-.count(notified(P,ConvID,Protocol)[source(self)],CN).
stuff([clothe,shoes,furniture,electronic]).

/* Initial goals */

!start.

/* Plans */

+!start : participants(P)&.my_name(Me)&stuff([FirstStuff|R])
<- +conversationID(Me,cnp,FirstStuff);
   !notifyParticipants(P,FirstStuff,cnp).

+!notifyParticipants([],ConvID,Protocol)
<- .print("INITIATOR:- PARTICIPANTS NOTIFIED.");
	!startConversation(ConvID,Protocol).


+!notifyParticipants([P|R],ConvID,Protocol): not notified(P,ConvID,Protocol)
<- .send(P,achieve,join(ConvID,Protocol));
   +notified(P,ConvID,Protocol);
   !notifyParticipants(R,ConvID,Protocol).

+!startConversation(ConvID,Protocol):participants(P)&timeOut(TO)&conversationTime(CT)&countnotified(CN,Protocol)&.length(P,CN)
<-  .print("INITIATOR:- STARTING CONVERSATION."); 
    jasonAgentsConversations.conversationsFactory.initiator.ia_FCN_Initiator("start", TO, CT, P, ConvID,ConvID).

-!startConversation(ConvID)
<- .print("INITIATOR:- THE CONVERSATION COULDN'T BE STARTED.").

+proposalsevaluationtime(ConvID):proposal(P,S,ConvID)&.my_name(Me)&conversationID(Me,cnp,ConvID)
<- .print("INITIATOR:- PROPOSALS EVALUATION TIME.");
   -proposal(P,S,ConvID);
   ?countProposal(Z);
   .print("INITIATOR:- EVALUATING PROPOSAL ",P," FROM AGENT ",S," AND SETTING IT AS THE BEST.");
   +bestProposal(P,S,ConvID);
   +accepted([],ConvID);
   +rejected([],ConvID);
   !evaluateProposals(ConvID);
   ?bestProposal(BP,SBP,ConvID);
   -+accepted([BP,SBP],ConvID);
   ?rejected(R,ConvID);
   jasonAgentsConversations.conversationsFactory.initiator.ia_FCN_Initiator("proposalsevaluated",[BP,SBP],R,ConvID).

+proposalsevaluationtime(ConvID):.my_name(Me)&conversationID(Me,cnp,ConvID)
<- .print("INITIATOR:- NO PROPOSALS TO EVALUATE.");
    jasonAgentsConversations.conversationsFactory.initiator.ia_FCN_Initiator("proposalsevaluated",[],[],ConvID).
 
+!evaluateProposals(ConvID):proposal(P,S,ConvID)&rejected(R,ConvID)&bestProposal(X,Sx,ConvID)&(P>X) 
<- .print("INITIATOR:- EVALUATING PROPOSAL ",P," FROM AGENT ",S," AND UPDATING BEST BECAUSE ", P, " > ", X,".");
   -proposal(P,S,ConvID);
   .concat([X,Sx],R,R1); //the previous best is rejected
   -+rejected(R1,ConvID);
   -+bestProposal(P,S,ConvID);
   +proposal(P,S,ConvID,evaluated);
   !evaluateProposals(ConvID).
  

+!evaluateProposals(ConvID):proposal(P,S,ConvID)&bestProposal(X,Sx,ConvID)&rejected(R,ConvID) 
<- .print("INITIATOR:- EVALUATING PROPOSAL ",P," FROM AGENT ",S,".");
   -proposal(P,S,ConvID);
   ?bestProposal(BP,BS,BConvID);
   .concat([P,S],R,R1);
   -+rejected(R1,ConvID);
   +proposal(P,S,ConvID,evaluated);
   !evaluateProposals(ConvID).

+!evaluateProposals(ConvID).

+resultsreceived(ConvID):.my_name(Me)&conversationID(Me,cnp,ConvID) 
 <- .print("INITIATOR:- PROCESSING RESULTS...");
    !processResults(ConvID); 
    jasonAgentsConversations.conversationsFactory.initiator.ia_FCN_Initiator("resultsprocessed",ConvID).
  
+!processResults(ConvID):taskDone(S,ConvID)
<- .print("INITIATOR:- AGENT ",S," DID THE TASK SUCCESSFULLY!");
    -taskDone(S,ConvID);
    +taskDone(S,ConvID,analized);
    !processResults(ConvID).

+!processResults(ConvID):taskNotDone(S,ConvID)
<- .print("INITIATOR:- AGENT ",S," DIDN'T MAKE THE TASK SUCCESSFULLY!");
    -taskNotDone(S,ConvID);
    +taskNotDone(S,ConvID,analized);
    !processResults(ConvID).

+!processResults(ConvID).

+conversationended(ConvID):.my_name(Me)&conversationID(Me,cnp,ConvID) 
<- -conversationID(Me,cnp,ConvID);
  .print("INITIATOR:- CONVERSATION ENDED!").
