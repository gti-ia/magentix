// Agent qqasl in project MWaterWeb
{ include("../mwaterJasonAgents/wu.asl") }

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true 
<-  .print("hello world.");
.wait(2000);
 /*?water_right_structure(WaterRight);
 ?waterRight_Arguments_List(WaterRight,FieldsList,ID);
 .print(WaterRight);
 .member(owner(3),FieldsList);
 .print(WaterRight);*/
	.send(staff,askOne,waterrightsasseller(3,10,10,WRList),Reply);
	if (Reply==false)
	{.print("Respuesta fallida");}
	else
	{.print("Reply: ",Reply);}.