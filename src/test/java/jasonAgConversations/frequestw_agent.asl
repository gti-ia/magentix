// Agent frequestw_agent in project magentix2

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true 
<- .send(frequestw_initiator,tell,price(laptop,450));
   wait(5000);
   .send(frequestw_initiator,tell,price(shoes,50));
   wait(5000);
   .send(frequestw_initiator,tell,price(shoes,30));
   wait(5000);
   .send(frequestw_initiator,tell,weather(sunny)).