// Agent frequest_participant in project magentix2

/* Initial beliefs and rules */
sellclient(frequestw_initiator).
rentclient(frequestw_initiator).
stock(shoes,1).
stock(shirt,1).
stock(laptop,1).
forrent(car(mercedes,1)).
forrent(car(toyota,1)).
forrent(bicicle(3)).
timeOut(5000).

/* Plans */

+!join(ConvID,Protocol)[source(S)]:timeOut(TO)
<- 
   .ia_fipa_requestw_Participant("joinconversation",TO,ConvID).

+request(Sender,Content,ConvID):not sellclient(Sender)&Content=sell(X)
<- .print("------- I've received a request for doing: ",Content," but ",Sender,"is not a client AND I REFUSE.");
   .ia_fipa_requestw_Participant("refuse",ConvID).
   
+request(Sender,Content,ConvID):not rentclient(Sender)&Content=rent(car)
<- .print("------- I've received a request for doing: ",Content," but ",Sender,"is not a client AND I REFUSE.");
   .ia_fipa_requestw_Participant("refuse",ConvID).
   
   
+request(Sender,Content,ConvID):sellclient(Sender)&Content=sell(X)&stock(X,Count)&Count<=0
<- .print("------- I've received a request for doing: ",Content," but there is not availability on stock AND I REFUSE.");
   .ia_fipa_requestw_Participant("refuse",ConvID).
   
+request(Sender,Content,ConvID):rentclient(Sender)&Content=rent(car)&forrent(car(Kind,Count))&Count<=0
<- .print("------- I've received a request for doing: ",Content," but there is not availability for rent AND I REFUSE.");
   .ia_fipa_requestw_Participant("refuse",ConvID).   
   
+request(Sender,Content,ConvID):sellclient(Sender)&Content=sell(X)&stock(X,Count)&Count>0
<- .print("------- I've received a request for doing: ",Content," AND I'M AGREE.");
  // NewCount = Count -1 ;
   //-+stock(X,NewCount);
   .ia_fipa_requestw_Participant("agree",ConvID).
   
+request(Sender,Content,ConvID):rentclient(Sender)&Content=rent(car)&forrent(car(Kind,Count))&Count>0
<- .print("------- I've received a request for doing: ",Content," AND I'M AGREE.");
   .ia_fipa_requestw_Participant("agree",ConvID).   


+request(Sender,Content,ConvID):rentclient(Sender)
<- .print("------- I've received a request for doing: ",Content," BUT I DIDN'T UNDERSTAND.");
   ia_fipa_requestw_Participant("notunderstood",ConvID).  

+request(Sender,Content,ConvID):sellclient(Sender)
<- .print("------- I've received a request for doing: ",Content," BUT I DIDN'T UNDERSTAND.");
   .ia_fipa_requestw_Participant("notunderstood",ConvID).

  
+timetodotask(Sender,Content,ConvID):Content=rent(car)&forrent(car(Kind,Count))&Count>0
<- .print("------- I'm going to make the task: ",Content);
   NewCount = Count - 1 ;
   -+forrent(car(Kind,NewCount));
   +rented(car(Kind),Sender);
   .ia_fipa_requestw_Participant("inform",Content,ConvID).

+timetodotask(Sender,Content,ConvID):Content=sell(X)&stock(X,Count)&Count>0
<- .print("------- I'm going to make the task: ",Content);
   NewCount = Count -1 ;
   -+stock(X,NewCount);
   .ia_fipa_requestw_Participant("inform",Content,ConvID).
   
+timetodotask(Sender,Content,ConvID)
<- .print("------- I've failed doing the task: ",Content);
   .ia_fipa_requestw_Participant("failure",Content,ConvID).
   
