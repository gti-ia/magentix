// Agent final_agent1 in project mWater-Magentix-Jason 
// * RESPONDS TO A BUYER BEHAVIOR *


/* Initial beliefs and rules */


//ontology:personalinformation(PI)
personalinformation([name(Name),user_type(3),seller_timeout(0.2),
	seller_price(23),seller_percentage(0.3),seller_wt(21),
	seller_th(0.7),buyer_bid(0.3),buyer_enter(0.2),
	buyer_cont_enact(0.6)]):-.my_name(Me)&.concat("",Me,Name).
//ontology:requirement(R)
requirement([protocol_type(Prot),seller_price(25),seller_percentage(0.2),seller_wt(20),th_id(THall),wmarket(WMarket),
	rol_when_opening_table(seller)]):-currentWaterMarket_id(WMarket)&currentTradinghall_id(THall)&currentProtocol_id(Prot).
//ontology:currentWaterMarket_id(MWaterMarket)


/* Initial goals */



/* Plans */



//This belief indicates that the trading table has been created successfully
	
	

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

/*

+!compareFunctorValues(Functor,ValueReq,ValueArg,Result)
<- 
    if (Functor == allowedUsers){ValueReq = L1;  
   								ValueArg = L2;
   								!isContained(L1,L2,Result);
   								?((ValueReq==[])|(Result==true)) ; Result = true};
   if (Functor == protocolType){?ValueReq==ValueArg; Result = true};   								 
   if (Functor == basin){?ValueReq==ValueArg; Result = true};
   if (Functor == waterUser){?ValueReq==ValueArg; Result = true};
   if (Functor == waterVol){?ValueReq<=ValueArg; Result = true};
   if (Functor == district){?ValueReq==ValueArg; Result = true};
   if (Functor == timePeriodIni){ !compareDates(ValueReq,ValueArg,DateIniResult);
   								  ?(DateIniResult>=0);
                                  Result = true;};
   if (Functor == timePeriodEnd){ !compareDates(ValueReq,ValueArg,DateEndResult);
   								  ?(DateEndResult<=0);
                                  Result = true;};
   if (Functor == waterKind){?ValueReq==ValueArg; Result = true};
   if (Result \== true){Result = false}.
 */

   
//+?acceptPrice(WRight,Bid,Participants,Reply)


//REQUIREMENT: TO ADD THIS AT THE END!!!!!!!
{ include("water_user.asl") }