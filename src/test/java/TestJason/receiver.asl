+x(N) : N < 3
   <- do(0); .print("End NO OK!").

+x(N) : N >= 3
   <- do(50);  .print("End OK").

 +vl(X)[source(Ag)] 
   :  true
   <- .print("Received tell ",vl(X)," from ", Ag);
      .
 