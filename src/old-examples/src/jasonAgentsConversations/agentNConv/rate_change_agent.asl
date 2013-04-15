// Agent fsubscribe_agent in project magentix2

/* Initial beliefs and rules */
price(euro,dollar,1.42).
price(euro,pound,0.87).
price(euro,yen,109.06).
count(0).
/* Initial goals */

!start.

/* Plans */

+!start : count(C)&C<7
<- .wait(3000);
   C1 = C + 1 ;
   -+count(C1);
   !informchanges;
   !start.

-!start.


+!informchanges
<- ?price(euro,yen,Y);
   ?price(euro,dollar,D);
   ?price(euro,pound,P);

   .send(fsubscribe_participant,tell,price(euro,yen,Y));
   .send(fsubscribe_participant,tell,price(euro,dollar,D));
   .send(fsubscribe_participant,tell,price(euro,pound,P));

   NewD = D + 0.01;
   NewP = P + 0.01;
   NewY = Y + 1.0;
   -price(euro,dollar,D);
   +price(euro,dollar,NewD);
   -price(euro,pound,P);
   +price(euro,pound,NewP);
   -price(euro,yen,Y);
   +price(euro,yen,NewY);
   .print(".....Informing rate changes....").
