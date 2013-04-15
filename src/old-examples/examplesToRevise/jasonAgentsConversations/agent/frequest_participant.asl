// Agent FRequest_Participant in project conversationsFactory

/* Initial beliefs and rules */
wantToObey(frequest_initiator).
interestingTasks([study,sport]).
notInterestingTasks([work]).
timeOut(5000).

/* Plans */
+!join(ConvID,Protocol)[source(S)]:timeOut(TO)&wantToObey(S)
<- .send(S,tell,joined(ConvID,Protocol));
   jasonAgentsConversations.conversationsFactory.participant.ia_fipa_request_Participant("joinconversation",TO,ConvID).

+request(Sender,Content,ConvID):wantToObey(Sender)&interestingTasks(T)&.member(Content,T)
<- .print("PARTICIPANT:- I'VE RECEIVED A REQUEST FOR DOING: ",Content," AND I'M AGREE.");
   -+taskStatus(Content,agree);
   jasonAgentsConversations.conversationsFactory.participant.ia_fipa_request_Participant("agree",ConvID).

+request(Sender,Content,ConvID):wantToObey(Sender)&notInterestingTasks(T)&.member(Content,T)
<- .print("PARTICIPANT:- I'VE RECEIVED A REQUEST FOR DOING: ",Content," AND I REFUSE.");
   -+taskStatus(Content,refuse);
   jasonAgentsConversations.conversationsFactory.participant.ia_fipa_request_Participant("refuse",ConvID).

+request(Sender,Content,ConvID):wantToObey(Sender)
<- .print("PARTICIPANT:- I'VE RECEIVED A REQUEST FOR DOING: ",Content," BUT I DIDN'T UNDERSTAND.");
   -+taskStatus(Content,notunderstood);
   jasonAgentsConversations.conversationsFactory.participant.ia_fipa_request_Participant("notunderstood",ConvID).

+request(Sender,Content,ConvID)
<- .print("PARTICIPANT:- I'VE RECEIVED A REQUEST FOR DOING: ",Content," AND I REFUSE.");
   -+taskStatus(Content,refuse);
   jasonAgentsConversations.conversationsFactory.participant.ia_fipa_request_Participant("refuse",ConvID).
   
+timetodotask(Content,ConvID):taskStatus(Content,agree)
<- .print("PARTICIPANT:- I'M GOING TO MAKE THE TASK: ",ConvID);
   !doTask(Content,ConvID);
   ?taskResult(Content,ConvID,P);
   jasonAgentsConversations.conversationsFactory.participant.ia_fipa_request_Participant("inform",Content,P,ConvID).
   
+timetodotask(Content,ConvID)
<- .print("PARTICIPANT:- I'VE FAIL DOING THE TASK: ",Content);
   jasonAgentsConversations.conversationsFactory.participant.ia_fipa_request_Participant("failure",Content,ConvID).
   
+!doTask(Content,ConvID)
<- .random(P);
	P2 = P * 20 ;
   +taskResult(Content,ConvID,P2).










   