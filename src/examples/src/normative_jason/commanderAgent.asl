/* Initial belief and goals*/
role(CommanderAgent).
!start.

/* Plans */
+!start : true <- 			 
							   .wait(3000);
							   .print("Voy a informar que hay mal tiempo  y el helicóptero está en mantenimiento.");
							   //.send(agentA,tell,weatherBad);
							   .send(agentA,tell,maintenance(apache2));
							   
							   .wait(3000);
							   .print("Voy a informar que ya no creo que haya mal tiempo.");
							   .send(agentA,untell,weatherBad);
							   .wait(5000);
							   .print("Voy a informar que hay buen tiempo y que el helicóptero no está en estado mantenimiento.");
							   .send(agentA,tell,weatherGood);
							   .send(agentA,untell,maintenance(apache2)).
