// Agent maria in project Communication.mas2j

vl(1).
vl(2).

//!hello(maria).

/* The plan below is triggered when a tell message is 
   received. It is like the belief addition, but with 
   a source that is not self.
*/
+vl(X)[source(Ag)] 
   :  Ag \== self
   <- .print("Received tell ",vl(X)," from ", Ag);
      +sendingTell(X).
 
+!kqml_received(Sender, askOne, fullname, ReplyWith) : true
   <- .send(Sender,tell,"Maria dos Santos", ReplyWith). // send the answer

