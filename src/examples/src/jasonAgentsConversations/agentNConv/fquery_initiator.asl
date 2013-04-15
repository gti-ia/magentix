// Agent fquery_initiator in project magentix2

/* Initial beliefs and rules */
participants([fquery_participant]).
timeOut(5000).
query(refquery,occupation(O),fqp1).
query(refquery,age(A),fqp2).
query(refquery,name(N),fqp3).
query(ifquery,sex(female),fqp4).
fqpcountConversations(C):-.count(conversationID(_,_,fqp,_),C).

/* Initial goals */
!start.

/* Plans */
+!start : participants(P)&query(Kind,FirstQuery,ConvID)&fqpcountConversations(C)
<- .my_name(Me);
   +conversationID(ConvID,Me,fqp,FirstQuery);
   !notifyParticipants(P,ConvID,fqp).

+!notifyParticipants([],ConvID,Protocol):notified(P,ConvID,Protocol)
<- .print("------- Participants notified.");
	+participantsNotified(P,ConvID,Protocol).

+!notifyParticipants([P|R],ConvID,Protocol): not notified(P,ConvID,Protocol)
<- .send(P,achieve,join(ConvID,Protocol));
   +notified(P,ConvID,Protocol);
   !notifyParticipants(R,ConvID,Protocol).

+participantsNotified(P,ConvID,Protocol):timeOut(TO)&conversationID(ConvID,Me,fqp,FirstQuery) & query(refquery,FirstQuery,ConvID)
<- .print("------- Starting and making ref-query ",FirstQuery," to ",P);
   //New conversation
   .print("********* Starting: ", ConvID);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_query_Initiator("start", P , TO, "Fipa query conversation started",refquery,ConvID);
   .wait(500);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_query_Initiator("ref-query",FirstQuery,P, ConvID);
   -query(refquery,_,ConvID);
   
   ?query(refquery,SecondQuery,fqp2);
   .print("********* Starting: ", fqp2);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_query_Initiator("start", P , TO, "Fipa query conversation started",refquery,fqp2);
   +conversationID(fqp2,Me,fqp,SecondQuery);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_query_Initiator("ref-query",SecondQuery,P, fqp2);
   -query(refquery,_,fqp2);
   
   ?query(refquery,ThirdQuery,fqp3);
   .print("********* Starting: ", fqp3);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_query_Initiator("start", P , TO, "Fipa query conversation started",refquery,fqp3);
   +conversationID(fqp3,Me,fqp,ThirdQuery);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_query_Initiator("ref-query",ThirdQuery,P, fqp3);
   -query(refquery,_,fqp3);
   
   ?query(ifquery,FourthQuery,fqp4);
   .print("********* Starting: ", fqp4," Query: ",FourthQuery);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_query_Initiator("start", P , TO, "Fipa query conversation started",ifquery,fqp4);
   +conversationID(fqp4,Me,fqp,FourthQuery);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_query_Initiator("if-query",FourthQuery,P, fqp4);
   -query(refquery,_,fqp4).

+participantsNotified(P,ConvID,Protocol):timeOut(TO) &conversationID(ConvID,Me,fqp,FirstQuery)& query(ifquery,FirstQuery,ConvID)
<- .print("------- Starting and making if-query ",FirstQuery," to ",P);
   //New conversation
   .print("********* Starting: ", ConvID);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_query_Initiator("start", P , TO, "Fipa query conversation started",ifquery,ConvID);
   .wait(500);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_query_Initiator("if-query",FirstQuery,P, ConvID).

+queryResult(P,Result,ConvID):.my_name(Me)&conversationID(ConvID,Me,fqp,Query)&query(ifquery,Query,ConvID)
<- if (Result==1){
   		R = "positive";
   		-query(ifquery,Query,ConvID);
   		+data(P,Query,positive);
   }else
	  { if (Result==0){
   		R = "negative";
   		-query(ifquery,Query,ConvID);
   		+data(P,Query,negative);
   		}else
   			{R = "unknown";}}
   .print("------- The query ",Query," was done successfully by agent ",P,". The result is ",R,".");

   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_query_Initiator("inform",ConvID). 

+queryResult(P,Result,ConvID):.my_name(Me)&conversationID(ConvID,Me,fqp,Query)&query(refquery,Query,ConvID)
<- .print("------- The query ",Query," was done successfully by agent ",P,". The result is ",Result,".");
	-query(refquery,Query,ConvID);
	+data(P,Result);
   jasonAgentsConversations.nconversationsFactory.initiator.ia_fipa_query_Initiator("inform",ConvID). 

+conversationended(ConvID,Result):.my_name(Me)&conversationID(ConvID,Me,fqp,Query)&participants([P|R])&timeOut(TO)
<- -conversationID(ConvID,Me,fqp,Query);
  .print("------- Conversation ",ConvID," for query: ",Query," ENDED! Result: ",Result," ++++++++++++++").

