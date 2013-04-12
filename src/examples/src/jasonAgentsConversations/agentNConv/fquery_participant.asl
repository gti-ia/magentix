// Agent fquery_participant in project magentix2

/* Initial beliefs and rules */
wantToAnswer(fquery_initiator,fqp).
age(30)[private].
name("Rodrigo")[public].
occupation(worker)[public].
sex(male)[public].
timeOut(5000).

/* Plans */

+!join(ConvID,Protocol)[source(S)]:timeOut(TO)&wantToAnswer(S,Protocol)
<- jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_query_Participant("joinconversation",TO,ConvID).

+query(Sender,Query,Kind,ConvID):not wantToAnswer(Sender,Protocol)
<- .print("------- I've received the query: ",Query," AND I REFUSE.");
   jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_query_Participant("refuse",ConvID).

+query(Sender,Query,Kind,ConvID):wantToAnswer(Sender,Protocol)
<- 
   !verifyQuery(Query,Kind,Result,Visibility);

   if (Visibility==public)
   	{.print("------- I've received the query: ",Query," AND I'M AGREE. Result: ",Result);
   	jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_query_Participant("agree",Result,ConvID);}
	else{
	   if (Visibility==private)
	     {.print("------- I've received a query: ",Kind," AND I REFUSE. THIS DATA IS PRIVATE.");
	      jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_query_Participant("refuse",ConvID); }
   	else{
  	 	if (Visibility==none)
   		 	{.print("------- I've received the query: ",Query," AND I DIDN'T UNDERSTAND.");
   		 	 jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_query_Participant("notunderstood",ConvID);}
   		 	else{
   		 	.print("------- I've received the query: ",Query," AND I'VE FAILED. THERE WERE ERRORS PROCESSING THE QUERY.");
   		 	jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_query_Participant("failure",ConvID);}
   		}
   	}.

+!verifyQuery(Query,Kind,Result,Visibility)
<-  
    if (.literal(Query))
    {   ?Query;
    	?Query[Visibility];
    	if (Kind==ifquery)
    	{Result = true;}
    	if (Kind==refquery)
    	{Result = Query;}
    	.print("MATCH QUERY.");
    }else{
    	Visibility=none;
    	Result=none;
    }.
 
-!verifyQuery(Query,ifquery,Result,Visibility)
<-  .print("NO MATCH. IFQUERY FALSE."); 
    Visibility=public;
    Result=false.

-!verifyQuery(Query,refquery,Result,Visibility)
<-  .print("NO MATCH. REFQUERY FAILED.");
    Result=none;
    Visibility=none.
    
-!verifyQuery(Query,Kind,Result,Visibility)
<-  .print("NO MATCH. KIND UNDEFINED.");
    Result=none.
