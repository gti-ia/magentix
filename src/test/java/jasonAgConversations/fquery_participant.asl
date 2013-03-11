// Agent fquery_participant in project magentix2

/* Initial beliefs and rules */
wantToAnswer(fquery_initiator,fqrp).
wantToAnswer(fquery_initiator,fqip).
age(30)[private].
name("Rodrigo")[public].
occupation(worker)[public].
sex(male)[public].
timeOut(5000).

/* Plans */

+!join(ConvID,Protocol)[source(S)]:timeOut(TO)&wantToAnswer(S,Protocol)
<- .ia_fipa_query_Participant("joinconversation",TO,ConvID).

+query(Sender,Query,Protocol,ConvID):not wantToAnswer(Sender,Protocol)
<- .print("------- I've received the query: ",Query," AND I REFUSE.");
   .ia_fipa_query_Participant("refuse",ConvID).
//query(fquery_initiator,sex(female),fqip,fqp4)[source(self)]
+query(Sender,Query,Protocol,ConvID):wantToAnswer(Sender,Protocol)
<- 
   !verifyQuery(Query,Protocol,Result,Visibility);
   if (Visibility==public)
   	{.print("------- I've received the query: ",Query," AND I'M AGREE. Result: ",Result);
   	.ia_fipa_query_Participant("agree",Result,ConvID);}
	else{
	   if (Visibility==private)
	     {.print("------- I've received a query: ",Protocol," AND I REFUSE. THIS DATA IS PRIVATE.");
	      .ia_fipa_query_Participant("refuse",ConvID); }
   	else{
  	 	if (Visibility==none)
   		 	{.print("------- I've received the query: ",Query," AND I DIDN'T UNDERSTAND.");
   		 	 .ia_fipa_query_Participant("notunderstood",ConvID);}
   		 	else{
   		 	.print("------- I've received the query: ",Query," AND I'VE FAILED. THERE WERE ERRORS PROCESSING THE QUERY.");
   		 	.ia_fipa_query_Participant("failure",ConvID);}
   		}
   	}.

+!verifyQuery(Query,Protocol,Result,Visibility)
<-  
    if (.literal(Query))
    {   ?Query;
    	?Query[Visibility];
    	if (Protocol==fqip)
    	{Result = true;}
    	if (Protocol==fqrp)
    	{Result = Query;}
    	.print("MATCH QUERY.");
    }else{
    	Visibility=none;
    	Result=none;
    }.
 
-!verifyQuery(Query,fqip,Result,Visibility)
<-  .print("NO MATCH. IF QUERY FALSE."); 
    Visibility=public;
    Result=false.

-!verifyQuery(Query,fqrp,Result,Visibility)
<-  .print("NO MATCH. REF QUERY FAILED.");
    Result=none;
    Visibility=none.
    
-!verifyQuery(Query,Protocol,Result,Visibility)
<-  .print("NO MATCH. KIND OF QUERY UNDEFINED.");
    Result=none.

+conversationended(ConvID,Result)
<-   .print("------- Conversation ",ConvID," ENDED! Result: ",Result," /////").