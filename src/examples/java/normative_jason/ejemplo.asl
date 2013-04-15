
/*
+?norm(N,ReplyWith)[source(Sender)]
<- if (N){Result = N}
   else {Result = false};
   .send(Sender,tell,Result,ReplyWith).

deregisterUnit(launidad,AgentName):- not(playsRole(_, launidad,RoleName) &
									 	hasPosition(RoleName, launidad,Position)&
									 	Position \== creator
									 	) & 
									 not(isUnit(SubUnitName)& 
									 hasParent(SubUnitName,launidad)).
									 
//isUnit(UnitName):- hasParent(foro, nada).
*/

