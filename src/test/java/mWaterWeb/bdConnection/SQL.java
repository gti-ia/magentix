/**
 * SQL sentences for mWater database.
 */
package mWaterWeb.bdConnection;

/**
 * @author agarridot
 *
 */
public class SQL 
{
	public static final String GET_WATERUSERS()
	{
		return "SELECT * FROM mWaterDB.user";
	}
	
	public static final String GET_WATERUSER_BY_ID(String id)
	{
		String result =  "SELECT * FROM mWaterDB.user";
		if (id.compareTo("")!=0)
			result = result + " WHERE id = " + id;
		return result;	
	}
	
	//Bexy
	public static String GET_WATERMARKET_BY_ID(String id) {
		String result =  "SELECT * FROM mWaterDB.mwatermarket";
		if (id.compareTo("")!=0)
			result = result + " WHERE id = " + id;
		return result;	
	}

	//Bexy
	public static final String GET_ACCREDITED_USERS_BY_IDS(String user_id, String wmarket)
	{
		String result =  "SELECT * FROM mWaterDB.accrediteduser";
		String and = "";
		if ((user_id.compareTo("")!=0)||(wmarket.compareTo("")!=0)){
			result = result + " WHERE";
			if (user_id.compareTo("")!=0){	result = result + and + " user = " + user_id;  and = " AND ";}
			if (wmarket.compareTo("")!=0)	result = result + and + " mwater_market = " + wmarket ;
			
		}
		return result;	
	}
	
	//Bexy
	public static final String GET_ACCREDITED_USERS()
	{
		return "SELECT * FROM mWaterDB.accrediteduser";
	}
	
	public static final String GET_WATERRIGHTS_BY_OWNER(String owner, String id)
	{
		//return "SELECT mWaterDB.waterright.* FROM mWaterDB.waterright, mWaterDB.generalwaterright " +
		//"WHERE (waterright.general_water_right = generalwaterright.id) AND (generalwaterright.owner = " + owner + ")";
		
		String result =  "SELECT mWaterDB.generalwaterright.owner,mWaterDB.waterright.* FROM mWaterDB.waterright, mWaterDB.generalwaterright "+
		"WHERE (waterright.general_water_right = generalwaterright.id)";

		if (owner.compareTo("")!=0)
			result = result + " AND (generalwaterright.owner = " + owner + ")";
		if (id.compareTo("")!=0)
			result = result + " AND (generalwaterright.id = " + id + ")";
		return result;	
	}
	
	//Bexy
	public static String GET_WATERRIGHT_HAS_TRADINGTABLE(String table,
			String wright, String wmarket, String confid) {
		String result =  "SELECT mWaterDB.tradingtable_has_waterright.* FROM mWaterDB.tradingtable_has_waterright";
		String and = "";
		if ((table.compareTo("")!=0)||(wright.compareTo("")!=0)||
				(wmarket.compareTo("")!=0)||(confid.compareTo("")!=0)){
			result = result + " WHERE";
			if (table.compareTo("")!=0){result = result + and + " trading_table = " + table;  and = " AND ";}
			if (wmarket.compareTo("")!=0) {result = result + and +  " mwater_market = " + wmarket ; and = " AND ";}
			if (confid.compareTo("")!=0) {result = result + and +  " configuration_id = " + confid ; and = " AND ";}
			if (wright.compareTo("")!=0) {result = result + and +  " water_right = " + wright ;}			
		}

		return result;	
	}

	
	public static final String GET_GENERALWATERRIGHTS_BY_OWNER(String owner)
	{
		//return "SELECT mWaterDB.waterright.* FROM mWaterDB.waterright, mWaterDB.generalwaterright " +
		//"WHERE (waterright.general_water_right = generalwaterright.id) AND (generalwaterright.owner = " + owner + ")";
		
		String result =  "SELECT mWaterDB.generalwaterright.* FROM mWaterDB.generalwaterright";
		if (owner.compareTo("")!=0)
			result = result + " WHERE (generalwaterright.owner = " + owner + ")";
		return result;	
	}
	
	public static final String GET_TRADINGTABLES(String configuration, String market, String id)
	{
		//return "SELECT * FROM mWaterDB.tradingtable WHERE (configuration_id = " + configuration + ") AND (mwater_market = " + market + ")";
		String result =  "SELECT * FROM mWaterDB.tradingtable";
		String and = "";
		if ((configuration.compareTo("")!=0)||(market.compareTo("")!=0)){
			result = result + " WHERE";
			if (configuration.compareTo("")!=0){result = result + and + " configuration_id = " + configuration;  and = " AND ";}
			if (market.compareTo("")!=0) {result = result + and +  " mwater_market = " + market ; and = " AND ";}
			if (id.compareTo("")!=0) {result = result + and +  " trading_table_id = " + id ;}			
		}

		return result;	
	}	

	//Bexy
	public static final String GET_CONFIGURATION(String id)
	{
		String result = "SELECT * FROM mWaterDB.configuration";
		if (id.compareTo("")!=0)
			result = result+" where id = "+id;
		return result;
		
	}

	//Bexy
	public static final String GET_TRADINGTABLE_NEW_ID(String configuration, String market)
	{
		String result =  "SELECT result.configuration_id, result.mwater_market, "+
		"CASE WHEN result.trading_table_id IS NOT NULL THEN result.trading_table_id+1 ELSE 1 END AS trading_table_id FROM"+
		" ( SELECT "+configuration+" AS configuration_id, "+market+" AS mwater_market, MAX(trading_table_id) AS trading_table_id "+
		"FROM mWaterDB.tradingtable WHERE configuration_id = "+configuration+" AND mwater_market = "+market+" ) result";

		return result;	
	}
	
	//Bexy
	public static final String GET_RECRUITED_PARTICIPANT(String tradingTableId, String configuration, String market, String userId)
	{
		String result =  "SELECT * from mWaterDB.recruitedparticipant ";
		String and = "";
		if ((userId.compareTo("")!=0)||(market.compareTo("")!=0)||(configuration.compareTo("")!=0)||(tradingTableId.compareTo("")!=0)){
			result = result + " WHERE";
			if (userId.compareTo("")!=0){ result = result + and + " user = " + userId;  and = " AND ";}
			if (market.compareTo("")!=0){result = result + and + " mwater_market = " + market ;  and = " AND ";}
			if (configuration.compareTo("")!=0){result = result + and +  " configuration_id = " + configuration;  and = " AND ";}
			if (tradingTableId.compareTo("")!=0){result = result + and + " trading_table = " + tradingTableId; }
		}
		return result;	
	}
	
	//Bexy
	public static final String GET_PROTOCOLTYPE(String id, String name)
	{
		String result =  "SELECT * from mWaterDB.protocoltype ";
		String and = "";
		if ((id.compareTo("")!=0)||(name.compareTo("")!=0)){
			result = result + " WHERE";
			if (id.compareTo("")!=0) {result = result + and + " id = " + id; and = " AND ";}
			if (name.compareTo("")!=0)result = result + and +  " name = " + name;
		}
		return result;	
	}
	
	//Bexy
	public static String GET_TRANSFERAGREEMENT(String id) {
		String result = "select tagr.*, agr.signature_date, agr.state from agreement as agr "+
		"inner join transferagreement as tagr on agr.id = tagr.id";
		if (id.compareTo("")!=0){result = result + " where tagr.id = " + id;}
		return result;
	}
	
	public static final String INSERT_CONFIGURATION(String description, String simulation_date, String negotiation_protocol, 
			String group_selected, String initial_date, String final_date, String seller_timeout, String seller_price, 
			String seller_percentage, String seller_wt, String buyer_bid, String buyer_enter, String buyer_cont_enact, 
			String ba_agr_val, String ba_entitlement, String mf_cont_enact, String mf_accred, String seller_th)
	{
		return "INSERT INTO mWaterDB.configuration (`id`, `description`, `simulation_date`, `negotiation_proto`, `group_selected`, " +
				"`initial_date`, `final_date`, `seller_timeout`, `seller_price`, `seller_percentage`, `seller_wt`, `buyer_bid`, `buyer_enter`, " +
				"`buyer_cont_enact`, `ba_agr_val`, `ba_entitlement`, `mf_cont_enact`, `mf_accred`, `seller_th`) VALUES " +
				"(null, " + description + ", " + simulation_date + ", " + negotiation_protocol + ", " + group_selected + ", " + 
				initial_date + ", " + final_date + ", " + seller_timeout + ", " + seller_price + ", " + seller_percentage + ", " + 
				seller_wt + ", " + buyer_bid + ", " + buyer_enter + ", " + buyer_cont_enact + ", " + ba_agr_val + ", " + ba_entitlement + 
				", " + mf_cont_enact + ", " + mf_accred + ", " + seller_th + ")";	
	}
	
	public static final String INSERT_WATERMAKET(String description, String version)
	{
		return "INSERT INTO mWaterDB.mwatermarket (`id`, `description`, `version`) VALUES (null, " + 
		description + ", " + version + ")";
	}
	
	public static final String INSERT_ACCREDITED_USER(String user_id, String wmarket, String trust_value, String sanction_value)
	{
		return "INSERT INTO mWaterDB.accrediteduser (`mwater_market`, `user`, `trust_value`,`sanction_value`) VALUES (" + 
		wmarket + ", " + user_id +", "+ trust_value+", "+sanction_value +")";
	}
	
	public static final String INSERT_TRADINGHALL()
	{
		return "INSERT INTO mWaterDB.tradinghall (`id`) VALUES (null)"; 		
	}
	
	public static final String INSERT_TRADINGTABLE(String configuration_id, String mwater_market, String trading_table_id, String opening_date,
			String closing_date, String conditions, String access_type, String deal, String protocol_parameters, String num_iter_until_agreem,
			String time_until_agreem, String num_participants, String opening_user, String protocol_type, String role_when_opening_table, 
			String number_of_opener_participations, String trading_hall_id)
	{
		String result = "INSERT INTO mWaterDB.tradingtable (`configuration_id`, `mwater_market`, `trading_table_id`, `opening_date`, " +
		"`closing_date`, `conditions`, `access_type`, `deal`, `protocol_parameters`, `num_iter_until_agreem`, " +
		"`time_until_agreem`, `num_participants`, `opening_user`, `protocol_type`, `role_when_opening_table`, " +
		"`number_of_opener_participations`, `trading_hall_id`) VALUES (" +
		configuration_id + ", " + mwater_market + ", " + trading_table_id + ", " + opening_date + ", " + 
		closing_date + ", " + conditions + ", " + access_type + ", " + deal + ", " + protocol_parameters + ", " + 
		num_iter_until_agreem + ", " +  time_until_agreem + ", " + num_participants + ", " + opening_user + ", " + 
		protocol_type + ", " + role_when_opening_table + ", " + number_of_opener_participations + ", " + 
		trading_hall_id + ")"; 	

		return result;
	}
	
	//Bexy
	public static String INSERT_WATERMAKET_HAS_TRADINGTABLE(String table,
			String wright, String wmarket, String confid) {
		String result = "INSERT INTO mWaterDB.tradingtable_has_waterright (`trading_table`,`mwater_market`," +
		"`configuration_id`, `water_right`) VALUES (" +
		table + ", " + wmarket + ", " + confid + ", " + wright + ") ";
		return result;
	}

	
	public static final String INSERT_AGREEMENT(String signature_date, String state)
	{
		return "INSERT INTO mWaterDB.agreement (`id`, `signature_date`, `state`) VALUES (null, " + signature_date + ", " +  state + ")";
	}
	
	public static final String INSERT_TRANSFERAGREEMENT(String idTransferAgreem, String agreed_price, String aggregation_agreement, String buyer_id , 
			String waterright_id, String trading_table_id, String mwater_market, String configuration_id)
	{
		return "INSERT INTO mWaterDB.transferagreement (`id`, `agreed_price`, `aggregation_agreement`, `buyer_id`, `waterright_id`, " +
		"`trading_table_id`, `mwater_market`, `configuration_id`) VALUES (" + idTransferAgreem + ", " + agreed_price + ", " + 
		aggregation_agreement + ", " + buyer_id + ", " + waterright_id + ", " + trading_table_id + ", " + 
		mwater_market + ", " + configuration_id + ")";		
	}
	
	public static final String INSERT_CONTRACT(String description, String state, String activation_date, String expiration_date, String isPrivate)
	{
		return "INSERT INTO mWaterDB.contract (`id`, `description`, `state`, `activation_date`, `expiration_date`, `private`) VALUES (null, " +
		description + ", " + state + ", " + activation_date  + ", " + expiration_date  + ", " + isPrivate +")";		
	}
	
	public static final String INSERT_AGREEDCONTRACT(String idAgreedContract, String intended_water_use, String successful, 
			String authorisation_date, String reasons_for_negation, String agreement)
	{
		return "INSERT INTO mWaterDB.agreedcontract (`id`, `intended_water_use`, `successful`, `authorisation_date`, `reasons_for_negation`, `agreement`) " +
				"VALUES (" + idAgreedContract + ", " + intended_water_use + ", " + successful + ", " + authorisation_date + ", " + 
				reasons_for_negation + ", " + agreement + ")";
	}
	
	//Bexy
	public static final String INSERT_RECRUITEDPARTICIPANTS(String trading_table, String mwater_market, String configuration_id, 
			String user, String invitation_condition, String invitation_date, String accepted, String acceptance_date, 
			String number_of_participations)
	{
		return "INSERT INTO mWaterDB.recruitedparticipant (`trading_table`, `mwater_market`, `configuration_id`, `user`,"+
		" `invitation_condition`, `invitation_date` , `accepted`,`acceptance_date`,`number_of_participations`) " +
				"VALUES (" + trading_table + ", " + mwater_market + ", " + configuration_id + ", " + user + ", " + 
				invitation_condition + ", " + invitation_date +", "+accepted+", "+acceptance_date+ ", "+number_of_participations+")";
	}



}
