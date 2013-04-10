// Agent bob in project Communication.mas2j

!start.

+!start : true 
   <- .println("Sending tell vl(10)");
      .send(maria, tell, vl(10));
      
      .println("Sending achieve goto(10,2)");
      .send(maria, achieve, goto(10,2));
      
      .println("Sending synchronous ask ");
      .send(maria, askOne, vl(X), vl(X));
      .println("Answer from ask is: ", X, " (should be 10)");

      .println("Sending assynchronous ask ");
      .send(maria, askOne, vl(_)); // assync ask has no fourth argument
      // the answer is received as an event +vl(X)

      .println("Sending ask for something Maria does not know, but can handle by +? ");
      .send(maria, askOne, t2(_), Ans2);
      .println("Answer from ask is: ", Ans2, " (should be t2(20))");
      
      .println("Sending ask for something Maria does not know ");
      .send(maria, askOne, t1(_), Ans1);
      .println("Answer from ask is: ", Ans1, " (should be false)");
      
      .println("Sending askAll values");
      .send(maria, askAll, vl(Y), List1);
      .println("Answer from askAll is: ", List1, " (should be [vl(10),vl(1),vl(2)])");

      .println("Sending askAll t1(X)");
      .send(maria, askAll, t1(Y), List2);
      .println("Answer from askAll is: ", List2, " (should be [])");

      .println("Sending ask full name");
      .send(maria, askOne, fullname, FN);
      .println("Full name is ",FN);
      
      // Ask maria plans to goto
      .send(maria, askHow, {+!goto(_,_)[source(_)]});
      .wait(500); // wait answer
      .print("Received plans:");
      .list_plans( {+!goto(_,_)[source(_)]} );
      .print;
      // another implementation (that do not include the received plans automaticaly in the PL)
      .send(maria, askHow, {+!goto(_,_)[source(_)]}, ListOfPlans);
      .print("Received plans by askHow: ", ListOfPlans);
      
      // Send to maria a plan to achieve the goal hello
      .plan_label(Plan,hp); // get a plans based on a plan's label
      .println("Sending tell how: ",Plan);
      .send(maria,tellHow,Plan);
	  
      .println("Asking Maria to achieve 'hello'");
      .send(maria,achieve, hello(bob));
      .wait(2000);
      
      .println("Asking Maria to unachieve 'hello'");
      .send(maria,unachieve, hello(bob));
	  
      // send untell how to maria
      .println("Sending untell how to Maria");
      .send(maria,untellHow,hp).
	  

+vl(X)[source(A)]
   <- .print("Received value ",X," from ",A).
   
@hp // this plan will be sent to Maria 
+!hello(Who) 
   <- .println("Hello ",Who); 
      .wait(100);
	  !hello(Who).   
