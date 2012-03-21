// Agent mail_agent in project magentix2

/* Initial beliefs and rules */
wantToAnswer(fsubscribe_initiator,fsp).
prox_estado(oficina,salida_oficina).
prox_estado(salida_oficina,en_camino).
prox_estado(en_camino,recepcionado).
prox_estado(recepcionado,entregado).

package(p1,en_camino).
package(p2,oficina).
package(p3,oficina).
package(p4,recepcionado).

/* Initial goals */
!start.

/* Plans */

+!start:package(P,State)&prox_estado(State,NextState)
<- .wait(5000);
   -package(P,State);
   +package(P,NextState);
   .findall(package(X,Z),package(X,Z),L);
   .print("..... State for package ",P," has changed ....");
   !!start.

+!start.

+!join(ConvID,Protocol)[source(S)]:wantToAnswer(S,Protocol)
<- .ia_fipa_subscribe_Participant("joinconversation",ConvID).


+subscriberequest(Sender,ObjectsList,ConvID):not wantToAnswer(Sender,Protocol)
<- .print("------- I've received the subscribe request: ",Object," AND I REFUSE.");
   .ia_fipa_subscribe_Participant("refuse",ConvID).

+subscriberequest(Sender,ObjectsList,ConvID):wantToAnswer(Sender,Protocol)
<- .print("------- I've received the subscribe request: ",ObjectsList," AND I'M AGREE."); 
   .ia_fipa_subscribe_Participant("agree",ConvID).


//Belief added by the platform
+subscribe(Sender,Object,ConvID):wantToAnswer(Sender,Protocol)
<- +subscript(Object,Sender,ConvID);
   .print("------- Subscribing: ",Sender, " for object: ",Object).


@pnotify 
+Object[Source]:subscript(Object1,fsubscribe_initiator,ConvID)&.concat("",Object1,InitialObj)&Object1 = Object
<- .print("------- Informing of change ",Object," for object ",InitialObj);
   .ia_fipa_subscribe_Participant("inform", InitialObj,Object,ConvID).

+conversationcanceledbyinitiator(ConvID)
<- !removesubscriptions(ConvID).

+!removesubscriptions(ConvID):subscript(Object,Sender,ConvID)
<- .abolish(subscript(_,_,ConvID));
   .print("------- Subscriptions right removed.");
   .ia_fipa_subscribe_Participant("informcancel",ConvID).

-!removesubscriptions(ConvID)
<- .print("------- Subscriptions removal failed. Failed cancel.");
    .ia_fipa_subscribe_Participant("failurecancel",ConvID).
   

