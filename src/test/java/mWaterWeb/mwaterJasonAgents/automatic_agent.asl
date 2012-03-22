// Agent automatic_agent in project MWaterWeb

/* Initial beliefs and rules */

/* Initial goals */



/* Plans */

+!start:myname(Me)
<- +contador(1);
   .print("Requesting staff for accreditation....");
   !accredit(Me,"").

+accredited(User,WMarket)
<- .print("I'm accredited!").

+?acceptPrice(WRight,TableID,WMarket,Protocol,Bid,Participants,Reply)
<-//.print("@@autom@@@ Recibido accept price!!! ",WRight); 
   .random(R);
	/*NewR = R * 100;
	if ((NewR > 50)|(NewR<20 ))
	{Reply = true}
	else{
	Reply = false;
	}*/
	
	?contador(C);
	if (C==4){Reply=false;}else{Reply=true;};
	NewC = C + 1;
	-+contador(NewC).
	

	
	{ include("../webInterface/webCommParticipant.asl") }