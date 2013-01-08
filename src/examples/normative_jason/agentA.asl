role(rescueEntity).
useHelicopters(cobra1).
useHelicopters(apache2).

deregisterUnit(launidad,AgentName):- not(playsRole(_, launidad,RoleName) &
									 	hasPosition(RoleName, launidad,Position)&
									 	Position \== creator
									 	) & 
									 not(isUnit(SubUnitName)& 
									 hasParent(SubUnitName,launidad)).


/*
+?norm(N,ReplyWith)[source(Sender)]
<- if (N){Result = N}
   else {Result = false};
   .send(Sender,tell,Result,ReplyWith).
   
   
//***************

+?norm(N,ReplyWith)[source(Sender)]:N
<-   ?N;
	.send(Sender,tell,N,ReplyWith).
	
+?norm(N,ReplyWith)[source(Sender)]:not N
<-  .send(Sender,tell,false,ReplyWith).
	*/