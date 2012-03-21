// Agent final_agent3 in project mWater-Magentix-Jason
// * RESPONDS TO A BUYER BEHAVIOR *

/* Initial beliefs and rules */


//ontology:personalinformation(PI)
personalinformation([name(Name),user_type(3),seller_timeout(0.1),
	seller_price(18),seller_percentage(0.2),seller_wt(26),
	seller_th(0.7),buyer_bid(0.4),buyer_enter(0.2),
	buyer_cont_enact(0.65)]):-.my_name(Me)&.concat("",Me,Name).
//ontology:requirement(R)
requirement([protocol_type(Prot),seller_price(25),seller_percentage(0.2),seller_wt(20),th_id(THall),wmarket(WMarket),
	rol_when_opening_table(seller)]):-currentWaterMarket_id(WMarket)&currentTradinghall_id(THall)&currentProtocol_id(Prot).

//ontology:currentWaterMarket_id(MWaterMarket)


/* Initial goals */

/* Plans */

//+?acceptPrice(WRight,Bid,Participants,Reply)

// -!evaluateProposals(cnp,Proposals,Accepted,Rejected)
// -!evaluatePriceOfWaterRight(Call,Price)

+!compareFunctorValues(Functor,ValueReq,ValueArg,Result)
<- 
   if (Functor == th_id){?ValueReq==ValueArg; Result = true};  
   if (Functor == wmarket){?ValueReq==ValueArg; Result = true};  
   if (Functor == rol_when_opening_table){?ValueReq==ValueArg; Result = true};  
   if (Functor == protocol_type){?ValueReq==ValueArg; Result = true};   								 
   if (Functor == seller_price){?ValueReq>=ValueArg; Result = true};
   if (Functor == seller_percentage){?ValueReq>=ValueArg; Result = true};
   if (Functor == seller_wt){?ValueReq>=ValueArg; Result = true};
   if (Result \== true){Result = false}.

{ include("water_user.asl") }