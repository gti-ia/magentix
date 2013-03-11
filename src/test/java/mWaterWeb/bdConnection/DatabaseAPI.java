/**
 * API to use the mWater database.
 */
package mWaterWeb.bdConnection;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/*import mWaterSQL.SQL;
import mWaterExceptions.DataManagementException;*/

/**
 * @author agarridot
 *
 */
public class DatabaseAPI 
{
	// Constant definitions for the XML conversion
	private static final String OPEN_RESULTS = "Results";
	private static final String OPEN_ITEM = "Item";
	private static final String EMPTY_RESULTS = "<Results/>";
	
	
	// SQL connection to the database. Not as a singleton because we may have different connections 
	private Connection connection;

	/**
	 * Opens the connection to the database.
	 * 
	 * @param server database server in {@code String} format.
	 * @param user database user in {@code String} format.
	 * @param passwd password server in {@code String} format.
	 * 
	 * @throws DataManagementException
	 */
	public void OpenConnection(String server, String user, String passwd) throws DataManagementException 
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");

			this.connection = DriverManager.getConnection(server, user, passwd);
		}
		catch (Exception e) 
		{
			throw new DataManagementException();
		}		
	}

	/**
	 * Closes the connection to the database.
	 * 
	 * @throws DataManagementException 
	 */
	public void CloseConnection() throws DataManagementException 
	{		
		if (this.connection != null)
		{
			try
			{
				this.connection.close();  
			}
			catch (SQLException e) 
			{
				throw new DataManagementException();
			}
		}
	}

	/**
	 * Starts a transaction. The connection must be open.
	 * 
	 * @return {@code Statement}
	 * 
	 * @throws SQLException 
	 */
	private Statement StartTransaction() throws SQLException
	{
		Statement st = this.connection.createStatement();
		st.execute("START TRANSACTION");
		
		return st;
	}
	
	/**
	 * Commits a transaction. The connection must be open.
	 * 
	 * @param statement in {@code Statement} format with the transaction open.
	 *  
	 * @throws SQLException 
	 */
	private void CommitTransaction(Statement st) throws SQLException
	{		
		try 
		{
			st.execute("COMMIT");
		} 
		catch (SQLException e) 
		{
			try 
			{
				RollbackTransaction(st);
			} 
			catch (SQLException e1) 
			{
				throw e1;
			}
			
			throw e;
		}
	}
	
	/**
	 * Rollbacks a transaction. The connection must be open.
	 * 
	 * @param statement in {@code Statement} format with the transaction open.
	 *  
	 * @throws SQLException 
	 */
	private void RollbackTransaction(Statement st) throws SQLException
	{		
		try 
		{
			st.execute("ROLLBACK");
		} 
		catch (SQLException e) 
		{
			throw e;
		}
	}
	
	/**
	 * Sends a query to the database and returns its result. The connection must be open.
	 * 
	 * @param query SQL sentence in {@code String} format.
	 * 
	 * @return {@code ResultSet}
	 *  
	 * @throws SQLException 
	 */
	private ResultSet QueryDB(String query) throws SQLException
	{
		if (connection != null)
		{
			try 
			{
				return this.connection.createStatement().executeQuery(query);
			} 
			catch (SQLException e) 
			{
				throw e;
			}
		}
		
		return null;
	}
	
	/**
	 * Executes updating SQL sentences. The connection must be open.
	 *
	 * @param sentence in {@code String} SQL format.
	 *       
	 * @return {@code ResultSet} with the autoincrement generated keys.
	 *  
	 * @throws SQLException
	 */
	private ResultSet UpdateDB(String sentence) throws SQLException 
	{			
		try 
		{
			Statement st = this.connection.createStatement();
			return UpdateDB(st, sentence); 
		} 
		catch (SQLException e) 
		{
			throw e;
		}		
	}	

	/**
	 * Executes updating SQL sentences within a given statement (useful for transactional blocks). The connection must be open.
	 *
	 * @param statement in {@code Statement} format.
	 * @param sentence in {@code String} SQL format.
	 *       
	 * @return {@code ResultSet} with the autoincrement generated keys.
	 *  
	 * @throws SQLException
	 */
	private ResultSet UpdateDB(Statement st, String sentence) throws SQLException 
	{		
		ResultSet rs = null;
		try 
		{
			st.execute(sentence, Statement.RETURN_GENERATED_KEYS);
			rs = st.getGeneratedKeys(); 
		} 
		catch (SQLException e) 
		{
			throw e;
		}

		return rs; 
	}	

	
	/**
	 * Water users
	 */ 
		
	/**
	 * Gets the water user with a given id. The connection must be open.
	 * 
	 * @param id user identifier in {@code String} format.
	 * 
	 * @return {@code String} with the user in XML format.
	 * 
	 * @throws DataManagementException 
	 */
	public String getWaterUserbyId(String id) throws DataManagementException 
	{
		try 
		{
			return convertToXML(QueryDB(SQL.GET_WATERUSER_BY_ID(id)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}
	}
	
	//Bexy
	public String getWaterMarketByID(String id) throws DataManagementException  
	{
		try 
		{
			return convertToXML(QueryDB(SQL.GET_WATERMARKET_BY_ID(id)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}
	}
	
	/**
	 * Gets the accredited water user with a given id. The connection must be open.
	 * 
	 * @param id user identifier in {@code String} format.
	 * 
	 * @param wmarket water market identifier in {@code String} format.
	 * 
	 * @return {@code String} with the user in XML format.
	 * 
	 * @throws DataManagementException 
	 */
	//Bexy
	public String getAccreditedWaterUserbyId(String id, String wmarket) throws DataManagementException 
	{
		try 
		{
			return convertToXML(QueryDB(SQL.GET_ACCREDITED_USERS_BY_IDS(id, wmarket)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}
	}
	
	/**
	 * Gets the collection of water users. The connection must be open.
	 * 
	 * @return {@code String} with the water users in XML format.
	 * 
	 * @throws DataManagementException 
	 */
	public String getWaterUsers() throws DataManagementException 
	{
		try 
		{
			return convertToXML(QueryDB(SQL.GET_WATERUSERS()));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}
	}
	
	/**
	 * Gets the participants of a trading table. The connection must be open.
	 * 
	 * @param tradingTableId of the market in {@code String} format.
	 * @param configurationId in {@code String} format.
	 * @param marketId in {@code String} format.
	 * @param userId in {@code String} format.
	 * 
	 * @return {@code String} with the water users in XML format.
	 * 
	 * @throws DataManagementException 
	 */
	//Bexy
	public String getRecruitedParticipants(String tradingTableId, String configuration, String market, String userId) throws DataManagementException 
	{
		try 
		{
			return convertToXML(QueryDB(SQL.GET_RECRUITED_PARTICIPANT(tradingTableId, configuration, market, userId)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}
	}
	
	/**
	 * Inserts a trading table. The connection must be open.
	 * 
	 * @param trading_table_id of the trading table in {@code String} format.
	 * @param mwater_market_id in {@code String} format.
	 * @param configuration_id in {@code String} format.
	 * @param user_id in {@code String} format.
	 * @param invitation_condition in {@code String} format.
	 * @param invitation_date in {@code String} format.
	 * @param accepted if the invitation was accepted in {@code String} format.
	 * @param acceptance_date in {@code String} format.
	 * @param number_of_participations of the user in {@code String} format.
	 * 
	 * @return {@code String} with the autoincrement id of the recruited participant in XML format. 
	 * For compatibility uses, but no autoincrement is generated
	 * 
	 * @throws DataManagementException 
	 */
	//Bexy
	public String insertRecruitedParticipants(String trading_table, String mwater_market, String configuration_id, String user,
			String invitation_condition, String invitation_date, String accepted, String acceptance_date, 
			String number_of_participations) throws DataManagementException
	{
		try 
		{
			return convertToXML(UpdateDB(SQL.INSERT_RECRUITEDPARTICIPANTS(trading_table, mwater_market, 
					configuration_id, user, invitation_condition, invitation_date, accepted, 
					acceptance_date, number_of_participations)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}		
	}
	
	/**
	 * Water rights
	 */ 
	
	/**
	 * Gets the collection of water rights for a given owner. The connection must be open.
	 * 
	 * @param owner of the water right in {@code String} format.
	 * @param id 
	 * 
	 * @return {@code String} with the water rights in XML format.
	 * 
	 * @throws DataManagementException 
	 */
	public String getWaterRightsByOwner(String owner, String id) throws DataManagementException 
	{
		try 
		{
			return convertToXML(QueryDB(SQL.GET_WATERRIGHTS_BY_OWNER(owner, id)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}
	}
	
	//Bexy
	public String getWaterHasTradintable(String table, String wright,
			String wmarket, String confid) throws DataManagementException 
	{
		try 
		{
			return convertToXML(QueryDB(SQL.GET_WATERRIGHT_HAS_TRADINGTABLE(table, wright,wmarket, confid)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}

	}
	
	//Bexy
	public String getGeneralWaterRightsByOwner(String owner) throws DataManagementException  
	{
		try 
		{
			return convertToXML(QueryDB(SQL.GET_GENERALWATERRIGHTS_BY_OWNER(owner)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}
	}
	
	
	/**
	 * Configurations
	 */
	
	/**
	 * Gets the configuration according to @param id. If no id is provided returns the whole collection.
	 *  
	 * The connection must be open.
	 * 
	 * @return {@code String} with the configuration in XML format.
	 * 
	 * @throws DataManagementException 
	 */
	//Bexy
	public String getConfiguration(String id) throws DataManagementException 
	{
		try 
		{
			return convertToXML(QueryDB(SQL.GET_CONFIGURATION(id)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}
	}
	
	
	/**
	 * Inserts a configuration. The connection must be open.
	 * 
	 * @param description of the market in {@code String} format.
	 * @param simulation_date in {@code String} format.
	 * @param negotiation_protocol in {@code String} format.
	 * @param group_selected in {@code String} format.
	 * @param initial_date in {@code String} format.
	 * @param final_date in {@code String} format.
	 * @param seller_timeout in {@code String} format.
	 * @param seller_price in {@code String} format.
	 * @param seller_percentage in {@code String} format.
	 * @param seller_wt in {@code String} format.
	 * @param buyer_bid in {@code String} format.
	 * @param buyer_enter in {@code String} format.
	 * @param buyer_cont_enact in {@code String} format.
	 * @param ba_agr_val in {@code String} format.
	 * @param ba_entitlement in {@code String} format.
	 * @param mf_cont_enact in {@code String} format.
	 * @param mf_accred in {@code String} format.
	 * @param seller_th in {@code String} format.
	 * @param numSellerProb 
	 * @param noPart 
	 * @param humanInt 
	 * 
	 * @return {@code String} with the autoincrement id of the market in XML format.
	 * 
	 * @throws DataManagementException 
	 */
	public String insertConfiguration(String description, String simulation_date, String negotiation_protocol, 
			String group_selected, String initial_date, String final_date, String seller_timeout, String seller_price, 
			String seller_percentage, String seller_wt, String buyer_bid, String buyer_enter, String buyer_cont_enact, 
			String ba_agr_val, String ba_entitlement, String mf_cont_enact, String mf_accred, 
			String seller_th, String humanInt, String noPart, String numSellerProb) throws DataManagementException
	{
		try 
		{
			return convertToXML(UpdateDB(SQL.INSERT_CONFIGURATION(description, simulation_date, negotiation_protocol, group_selected, 
					initial_date, final_date, seller_timeout, seller_price, seller_percentage, seller_wt, buyer_bid, buyer_enter, 
					buyer_cont_enact, ba_agr_val, ba_entitlement, mf_cont_enact, mf_accred, seller_th, humanInt, noPart, numSellerProb)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}		
	}
	
	/**
	 * Water market
	 */
	
	
	/**
	 * Inserts a water market. The connection must be open.
	 * 
	 * @param description of the market in {@code String} format.
	 * @param version of the market in {@code String} format.
	 * 
	 * @return {@code String} with the autoincrement id of the market in XML format.
	 * 
	 * @throws DataManagementException 
	 */
	public String insertWaterMarket(String description, String version) throws DataManagementException
	{
		try 
		{
			return convertToXML(UpdateDB(SQL.INSERT_WATERMAKET(description, version)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}
	}
	
	//Bexy
	public String insertWaterHasTradintable(String table, String wright,
			String wmarket, String confid) throws DataManagementException 
	{
		try 
		{
			return convertToXML(UpdateDB(SQL.INSERT_WATERMAKET_HAS_TRADINGTABLE(table, wright,
					wmarket, confid)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}
	}

	
	/**
	 * Trading halls
	 */
	
	/**
	 * Inserts a trading hall. The connection must be open.
	 *  
	 * @return {@code String} with the autoincrement id of the trading hall in XML format.
	 * 
	 * @throws DataManagementException 
	 */
	public String insertTradingHall() throws DataManagementException
	{
		try 
		{
			return convertToXML(UpdateDB(SQL.INSERT_TRADINGHALL()));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}		
	}
	
	
	/**
	 * Trading tables
	 */
	
	/**
	 * Gets the collection of trading tables. The connection must be open.
	 * 
	 * @param configuration identifier of the trading table in {@code String} format.
	 * @param market identifier of the trading table in {@code String} format.

	 * @return {@code String} with the trading tables in XML format.
	 * 
	 * @throws DataManagementException 
	 */
	public String getTradingTables(String configuration, String market, String id) throws DataManagementException 
	{
		try 
		{
			return convertToXML(QueryDB(SQL.GET_TRADINGTABLES(configuration, market, id)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}
	}
	
	/**
	 * Gets an ID for a new trading table. The connection must be open.
	 * 
	 * @param configuration identifier of the trading table in {@code String} format.
	 * @param market identifier of the trading table in {@code String} format.

	 * @return {@code String} with the trading tables in XML format.
	 * 
	 * @throws DataManagementException 
	 */
	public String getTradingTableNewID(String configuration, String market) throws DataManagementException 
	{
		try 
		{
			return convertToXML(QueryDB(SQL.GET_TRADINGTABLE_NEW_ID(configuration, market)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}
	}
	
	/**
	 * @param id User class identifier
	 * @return {@code String} with the user class in XML format.
	 */
	public String getUserClass(String usrid) throws DataManagementException {
		try 
		{
			return convertToXML(QueryDB(SQL.GET_USER_CLASS(usrid)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}
	}
	
	/**
	 * @param usrid User class identifier
	 * @param confid Configuration identifier
	 * @return {@code String} with the user class in XML format.
	 */
	public String getUserHasClass(String usrid, String confid) throws DataManagementException {
		try 
		{
			return convertToXML(QueryDB(SQL.GET_USER_HAS_CLASS(usrid,confid)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}
	}

	
	/**
	 * Inserts a trading table. The connection must be open.
	 * 
	 * @param configuration_id of the trading table in {@code String} format.
	 * @param mwater_market in {@code String} format.
	 * @param trading_table_id in {@code String} format.
	 * @param opening_date of the trading table in {@code String} format.
	 * @param closing_date of the trading table in {@code String} format.
	 * @param conditions of the trading table in {@code String} format.
	 * @param access_type of the trading table in {@code String} format.
	 * @param deal of the trading table in {@code String} format.
	 * @param protocol_parameters of the trading table in {@code String} format.
	 * @param num_iter_until_agreem of the trading table in {@code String} format.
	 * @param time_until_agreem of the trading table in {@code String} format.
	 * @param opening_user of the trading table in {@code String} format.
	 * @param protocol_type of the trading table in {@code String} format.
	 * @param role_when_opening_table of the trading table in {@code String} format.
	 * @param number_of_opener_participations of the trading table in {@code String} format.
	 * @param trading_hall_id of the trading table in {@code String} format.
	 * 
	 * @return {@code String} with the autoincrement id of the trading table in XML format. For compatibility uses, but no autoincrement is generated
	 * 
	 * @throws DataManagementException 
	 */
	public String insertTradingTable(String configuration_id, String mwater_market, String trading_table_id, String opening_date,
			String closing_date, String conditions, String access_type, String deal, String protocol_parameters, String num_iter_until_agreem,
			String time_until_agreem, String num_participants, String opening_user, String protocol_type, String role_when_opening_table, 
			String number_of_opener_participations, String trading_hall_id) throws DataManagementException
	{
		try 
		{
			return convertToXML(UpdateDB(SQL.INSERT_TRADINGTABLE(configuration_id, mwater_market, trading_table_id, opening_date, closing_date, conditions, 
					access_type, deal, protocol_parameters, num_iter_until_agreem, time_until_agreem, num_participants, opening_user, 
					protocol_type, role_when_opening_table, number_of_opener_participations, trading_hall_id)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}		
	}
	
	/**
	 * Inserts an accredited user. The connection must be open.
	 * 
	 * @param user_id User Id in {@code String} format.
	 * @param wmarket Water Market Id in {@code String} format.
	 * @param trust_value Trust Value of the user in {@code String} format.
	 * @param sanction_value Sanction value of the user in {@code String} format.
	 * 
	 * @return {@code String} with the autoincrement id of the trading table in XML format. For compatibility uses, but no autoincrement is generated
	 * 
	 * @throws DataManagementException 
	 */
	public String insertAccreditedUser(String user_id, String wmarket, String trust_value, String sanction_value) throws DataManagementException
	{
		try 
		{
			return convertToXML(UpdateDB(SQL.INSERT_ACCREDITED_USER(user_id, wmarket, trust_value, sanction_value)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}		
	}
	
	
	/**
	 * Transfer agreements
	 */
	
	/**
	 * Gets the collection of transfer agreements. The connection must be open.
	 * 
	 * @param id of the transfer agreement in {@code String} format.
	 * 
	 * @return {@code String} with the transfer agreement in XML format.
	 * 	 
	 * @throws DataManagementException 
	 */
	//Bexy
	public String getTransferAgreement(String id) throws DataManagementException  
	{
		try
		{
			return convertToXML(QueryDB(SQL.GET_TRANSFERAGREEMENT(id)));
		}
		catch (SQLException e)
		{
			throw new DataManagementException();
		}
	}
	
	/**
	 * Inserts a transfer agreement. The connection must be open.
	 * 
	 * @param signature_date of the transfer agreement in {@code String} format.
	 * @param state of the transfer agreement  in {@code String} format.
	 * @param agreed_price of the transfer agreement in {@code String} format.
	 * @param aggregation_agreement of the transfer agreement  in {@code String} format.
	 * @param buyer_id of the transfer agreement in {@code String} format.
	 * @param waterright_id of the transfer agreement  in {@code String} format.
	 * @param trading_table_id of the transfer agreement in {@code String} format.
	 * @param mwater_market of the transfer agreement  in {@code String} format.
	 * @param configuration_id of the transfer agreement  in {@code String} format.
	 * 
	 * @return {@code String} with the autoincrement id of the transfer agreement in XML format.
	 * 	 
	 * @throws DataManagementException 
	 */
	public String insertTransferAgreement(String signature_date, String state, String agreed_price, String aggregation_agreement, 
			String buyer_id, String waterright_id, String trading_table_id, String mwater_market, String configuration_id) throws DataManagementException
	{
		Statement st = null;

		try 
		{
			st = StartTransaction();
			
			// inserting the agreement
			String idAgreement = convertToXML(UpdateDB(st, SQL.INSERT_AGREEMENT(signature_date, state)));
					
			// now inserting the transfer agreement
			UpdateDB(st, SQL.INSERT_TRANSFERAGREEMENT(Extract_GeneratedKey_From_XML_String(idAgreement), agreed_price, 
					aggregation_agreement, buyer_id, waterright_id, trading_table_id, mwater_market, configuration_id));
			CommitTransaction(st);
			
			return idAgreement;
		} 
		catch (SQLException e) 
		{
			try 
			{
				RollbackTransaction(st);
				throw new DataManagementException();
			} 
			catch (SQLException e1) 
			{
				throw new DataManagementException();
			}
		}
	}
	
	
	/**
	 * Agreed contracts
	 */
	
	/**
	 * Inserts an agreed contract. The connection must be open.
	 * 
	 * @param description of the agreed contract in {@code String} format.
	 * @param state of the agreed contract in {@code String} format.
	 * @param activation_date of the agreed contract in {@code String} format.
	 * @param expiration_date of the agreed contract in {@code String} format.
	 * @param isPrivate (privateness 0/1) of the agreed contract in {@code String} format.
	 * @param intended_water_use of the agreed contract in {@code String} format.
	 * @param successful of the agreed contract in {@code String} format.
	 * @param authorisation_date of the agreed contract in {@code String} format.
	 * @param reasons_for_negation of the agreed contract in {@code String} format.
	 * @param agreement of the agreed contract in {@code String} format.
	 * 
	 * @return {@code String} with the autoincrement id of the agreed contract in XML format.
	 * 	 
	 * @throws DataManagementException 
	 */
	public String insertAgreedContract(String description, String state, String activation_date, String expiration_date, String isPrivate,
			String intended_water_use, String successful, String authorisation_date, String reasons_for_negation, String agreement) throws DataManagementException
	{
		Statement st = null;
		
		try 
		{
			st = StartTransaction();
			
			// inserting the contract
			String idContract = convertToXML(UpdateDB(st, SQL.INSERT_CONTRACT(description, state, activation_date, expiration_date, isPrivate)));
					
			// now inserting the agreed contract
			UpdateDB(st, SQL.INSERT_AGREEDCONTRACT(Extract_GeneratedKey_From_XML_String(idContract), intended_water_use, successful, 
					authorisation_date, reasons_for_negation, agreement));

			CommitTransaction(st);
			
			return idContract;
		} 
		catch (SQLException e) 
		{
			try 
			{
				RollbackTransaction(st);
				throw new DataManagementException();
			} 
			catch (SQLException e1) 
			{
				throw new DataManagementException();
			}
		}		
	}
	

	/**
	 * Nomenclature
	 * */
	
	/**
	 * Gets the protocol type. The connection must be open.
	 * 
	 * @param id identifier of the protocol type in {@code String} format.
	 * @param name type name in {@code String} format.

	 * @return {@code String} with the protocol type in XML format.
	 * 
	 * @throws DataManagementException 
	 */
	//Bexy
	public String getProtocolType(String id, String name) throws DataManagementException 
	{
		try 
		{
			return convertToXML(QueryDB(SQL.GET_PROTOCOLTYPE(id, name)));
		} 
		catch (SQLException e) 
		{
			throw new DataManagementException();
		}
	}
	
	
	/**
	 * Converts to XML a given {@code ResultSet}.
	 * 
	 * @param {@code ResultSet}.
	 * 
	 * @return XML with the result of the query in format <Results> <Item>... </Item> </Results>. 
	 */
	private String convertToXML(ResultSet rs)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element results = doc.createElement(OPEN_RESULTS);
			doc.appendChild(results);

			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();

			while (rs.next()) 
			{
				Element row = doc.createElement(OPEN_ITEM);
				results.appendChild(row);
				for (int i = 1; i <= colCount; i++) 
				{
					String columnName = rsmd.getColumnName(i);
					Object value = rs.getObject(i);
					String stringValue = (rs.getObject(i) == null) ? "" : value.toString();  
					Element node = doc.createElement(columnName);
					node.appendChild(doc.createTextNode(stringValue));
					row.appendChild(node);
				}
			}
			rs.close();

			DOMSource domSource = new DOMSource(doc);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			StringWriter sw = new StringWriter();
			StreamResult sr = new StreamResult(sw);
			transformer.transform(domSource, sr);

			return sw.toString();
		}
		catch (Exception e)
		{		
			return EMPTY_RESULTS;  // empty results
		}
	}
	
	/**
	 * Extracts the GENERATED_KEY from a XML {@code String}.
	 * 
	 * @param {@code String} with the XML <GENERATED_KEY>.
	 * 
	 * @return {@code String} with the value of the <GENERATED_KEY>.
	 */
	public String Extract_GeneratedKey_From_XML_String(String xml)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));        
			Document doc = builder.parse(is);
						
			return ((Element) doc.getElementsByTagName("GENERATED_KEY").item(0)).getFirstChild().getNodeValue();			
		}
		catch (Exception e) 
		{
			return null;	
		}	
	}



	

}
