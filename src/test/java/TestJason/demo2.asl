+x(N) : N < 3
   <- do(0); .print("End NO OK!").

+x(N) : N >= 3
   <- do(50);  .print("End OK").
   
+s(N) :true 
   <- .println("Sending tell s");
      .send(test, unknow, d(N));
      +sentTell.