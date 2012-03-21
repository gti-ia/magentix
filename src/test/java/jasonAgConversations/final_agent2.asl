// Agent final_agent2 in project mWater-Magentix-Jason
// * RESPONDS TO A SELLER BEHAVIOR *

{ include("wu.asl") }


/* Initial beliefs and rules */



//ontology:personalinformation(PI)
personalinformation([name(Name),user_type(6),seller_timeout(0.3),
	seller_price(22),seller_percentage(0.2),seller_wt(18),
	seller_th(0.5),buyer_bid(0.4),buyer_enter(0.2),
	buyer_cont_enact(0.6)]):-.my_name(Me)&.concat("",Me,Name).
//ontology:waterright(WR)
water_right([id(7),authorized_extraction_flow(200),authorization_date([11,4,2011]),
	authorized(1),type_of_water(industrial),initial_date_for_extraction([1,1,2010]),
	final_date_for_extraction([31,12,2014]),aggregation_right(0),season_unit(1),season(1),
	general_water_right(7)]).
//water_right_protocol(WRID,Protocol_type)
water_right_protocol(id(7),protocol_type(5)).
//ontology:currentWaterMarket_id(MWaterMarket)

requirement([protocol_type(Prot),seller_price(25),seller_percentage(0.2),seller_wt(20),th_id(THall),wmarket(WMarket),
	rol_when_opening_table(seller)]):-currentWaterMarket_id(WMarket)&currentTradinghall_id(THall)&currentProtocol_id(Prot).

//For being analized!!!



/* Initial goals */

/* !start.*/

/* Plans */

/****
+!start:personalinformation(PI)&currentWaterMarket_id(WMarketID)
<- !accredit(PI,WMarketID). //300 is a fixed ID for a water market

+accredited(User)
<- .print(" - I'm accredited - ").


//this is a message sent by the staff to indicate that a protocol can be started
+testtablecreated(Table)
<- ?water_right(WaterRightFieldsList);
   ?table_Arguments_List(Table,ArgumentsList,TableID);
   .member(protocol_type(Prot),ArgumentsList);
   !startSubprotocol(Prot,seller,WaterRightFieldsist,Table).
   */

//+memberjoined(NewRPart,Table)
+memberjoined(Table,UserID)
<-
   .count(memberjoined(_,_),C);
	?table_Arguments_List(Table,FieldsList,TableID);

   if (C==3) //this criteria is completely random
   {
   		?water_right(WaterRightFieldsList);
   		.member(protocol_type(Prot),FieldsList);
   		.print("***** Enough users to start subprotocol. Starting ... ");
   		
   		/*

   			.member(,ProtocolParameters);
   			.member(,ProtocolParameters);
   			.member(,ProtocolParameters);
   		
   		*/
   		.concat([rol(seller)],[water_right_fields(WaterRightFieldsList)],[table(Table)],[max_iterations(5)],[increment(12)],[initial_bid(1)],ProtocolParameters);
   		!startSubprotocol(Prot,ProtocolParameters);
   }.
   
   
   
   { include("water_user.asl") }
