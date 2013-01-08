// Agent frecruiting_agent in project magentix2

/* Initial beliefs and rules */
participant(frecruiting_participant).

/* Initial goals */

!start.

/* Plans */
@pstart[atomic]
+!start : participant(P)&.my_name(Me)
<-  .send(P,tell,logged(Me));
    .print("LOGGED SENT");
    .random(R);
	R2 = R * 20 ;
	!setavailability(R2).

+!setavailability(P):.my_name(Me)&P>10
<-	.print("AGENT ",Me," SETTING AVAILABILITY TO TRUE.");
	+available;
	+notready.

+!setavailability(P):.my_name(Me)
<-	.print("AGENT ",Me," SETTING AVAILABILITY TO FALSE.");
	+notavailable;
	+ready.

 