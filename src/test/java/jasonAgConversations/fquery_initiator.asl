// Agent fquery_initiator in project magentix2

/* Initial beliefs and rules */
participants([fquery_participant]).
timeOut(5000).
query(fqrp,occupation(O),fqp1).
query(fqrp,age(A),fqp2).
query(fqrp,name(N),fqp3).
query(fqip,sex(female),fqp4).
fqpcountConversations(C):-.count(conversationID(_,_,fqrp,_),Cr)&.count(conversationID(_,_,fqip,_),Ci)&C=Cr+Ci.

/* Initial goals */
!start.

/* Plans */
+!start : participants(P)&query(Protocol,FirstQuery,ConvID)&fqpcountConversations(C)
<- .my_name(Me);
   +conversationID(ConvID,Me,Protocol,FirstQuery);
   !notifyParticipants(P,ConvID,Protocol).

+!notifyParticipants([],ConvID,Protocol):notified(P,ConvID,Protocol)
<- .print("------- Participants notified.");
	+participantsNotified(P,ConvID,Protocol).

+!notifyParticipants([P|R],ConvID,Protocol): not notified(P,ConvID,Protocol)
<- .send(P,achieve,join(ConvID,Protocol));
   +notified(P,ConvID,Protocol);
   !notifyParticipants(R,ConvID,Protocol).

+participantsNotified(P,ConvID,Protocol):timeOut(TO)&conversationID(ConvID,Me,Protocol,FirstQuery) & query(fqrp,FirstQuery,ConvID)
<- .print("------- Starting and making ref-query ",FirstQuery," to ",P);
   //New conversation
   .print("********* Starting: ", ConvID);
   .ia_fipa_query_Initiator("start", P , TO, "Fipa query conversation started",fqrp,ConvID);
   .wait(500);
   .ia_fipa_query_Initiator("ref-query",FirstQuery,P, ConvID);
   -query(fqrp,_,ConvID);
   
   ?query(fqrp,SecondQuery,fqp2);
   .print("********* Starting: ", fqp2);
   .ia_fipa_query_Initiator("start", P , TO, "Fipa query conversation started",fqrp,fqp2);
   +conversationID(fqp2,Me,fqrp,SecondQuery);
   .ia_fipa_query_Initiator("ref-query",SecondQuery,P, fqp2);
   -query(fqrp,_,fqp2);
   
   ?query(fqrp,ThirdQuery,fqp3);
   .print("********* Starting: ", fqp3);
   .ia_fipa_query_Initiator("start", P , TO, "Fipa query conversation started",fqrp,fqp3);
   +conversationID(fqp3,Me,fqrp,ThirdQuery);
   .ia_fipa_query_Initiator("ref-query",ThirdQuery,P, fqp3);
   -query(fqrp,_,fqp3);
   
   ?query(fqip,FourthQuery,fqp4);
   .print("********* Starting: ", fqp4," Query: ",FourthQuery);
   .ia_fipa_query_Initiator("start", P , TO, "Fipa query conversation started",fqip,fqp4);
   +conversationID(fqp4,Me,fqip,FourthQuery);
   .ia_fipa_query_Initiator("if-query",FourthQuery,P, fqp4);
   -query(fqip,_,fqp4).

+participantsNotified(P,ConvID,Protocol):timeOut(TO) &conversationID(ConvID,Me,Protocol,FirstQuery)& query(fqip,FirstQuery,ConvID)
<- .print("------- Starting and making if-query ",FirstQuery," to ",P);
   //New conversation
   .print("********* Starting: ", ConvID);
   .ia_fipa_query_Initiator("start", P , TO, "Fipa query conversation started",fqip,ConvID);
   .wait(500);
   .ia_fipa_query_Initiator("if-query",FirstQuery,P, ConvID).

+queryResult(P,Result,ConvID):.my_name(Me)&conversationID(ConvID,Me,fqip,Query)//&query(fqip,Query,ConvID)
<- 
   if (Result==1){
   		R = "positive";
   		-query(fqip,Query,ConvID);
   		+data(P,Query,positive);
   }else
	  { if (Result==0){
   		R = "negative";
   		-query(fqip,Query,ConvID);
   		+data(P,Query,negative);
   		}else
   			{R = "unknown";}}
   .print("------- The query ",Query," was done successfully by agent ",P,". The result is ",R,".");

   .ia_fipa_query_Initiator("inform",ConvID). 

+queryResult(P,Result,ConvID):.my_name(Me)&conversationID(ConvID,Me,fqrp,Query)&query(fqrp,Query,ConvID)
<- .print("------- The query ",Query," was done successfully by agent ",P,". The result is ",Result,".");
	-query(fqrp,Query,ConvID);
	+data(P,Result);
   .ia_fipa_query_Initiator("inform",ConvID). 

+conversationended(ConvID,Result):.my_name(Me)&conversationID(ConvID,Me,Protocol,Query)&participants([P|R])&timeOut(TO)
<- -conversationID(ConvID,Me,Protocol,Query);
  .print("------- Conversation ",ConvID," for query: ",Query," ENDED! Result: ",Result," ++++++++++++++").

