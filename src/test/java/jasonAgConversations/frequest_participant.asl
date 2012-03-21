// Agent frequest_participant in project magentix2

/* Initial beliefs and rules */
wantToObey(frequest_initiator).
interestingTasks([study,sport]).
notInterestingTasks([work]).
timeOut(5000).

/* Plans */

+!join(ConvID,Protocol)[source(S)]:timeOut(TO)&wantToObey(S)
<- //.send(S,tell,joined(ConvID,Protocol));
   .ia_fipa_request_Participant("joinconversation",TO,ConvID).

+request(Sender,Content,Data,ConvID):wantToObey(Sender)&interestingTasks(T)&.member(Content,T)
<- .print("------- I've received a request for doing: ",Content," AND I'M AGREE.");
   -+taskStatus(Content,agree);
   .ia_fipa_request_Participant("agree",ConvID).

+request(Sender,Content,Data,ConvID):wantToObey(Sender)&notInterestingTasks(T)&.member(Content,T)
<- .print("------- I've received a request for doing: ",Content," AND I REFUSE.");
   -+taskStatus(Content,refuse);
   .ia_fipa_request_Participant("refuse",ConvID).

+request(Sender,Content,Data,ConvID):wantToObey(Sender)
<- .print("------- I've received a request for doing: ",Content," BUT I DIDN'T UNDERSTAND.");
   -+taskStatus(Content,notunderstood);
   .ia_fipa_request_Participant("notunderstood",ConvID).

+request(Sender,Content,Data,ConvID)
<- .print("------- I've received a request for doing: ",Content," AND I REFUSE.");
   -+taskStatus(Content,refuse);
   .ia_fipa_request_Participant("refuse",ConvID).
   
+timetodotask(Content,Data,ConvID):taskStatus(Content,agree)
<- data(L)=Data;
   .print("------- I'm going to make the task: ",ConvID,". I've received the information: ", L);
   !doTask(Content,Data,ConvID);
   ?taskResult(Content,ConvID,P);
   .ia_fipa_request_Participant("inform",Content,P,ConvID).
   
   
+timetodotask(Content,Data,ConvID)
<- .print("------- I've failed doing the task: ",Content);
   .ia_fipa_request_Participant("failure",Content,ConvID).
   
+!doTask(Content,Data,ConvID)
<- .random(P);
	P2 = P * 20 ;
   +taskResult(Content,ConvID,P2).
   
//+conversationended(ConvID, Result)
