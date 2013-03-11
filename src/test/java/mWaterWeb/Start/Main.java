/**
 * 
 */
package mWaterWeb.Start;

import jason.asSyntax.Literal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import mWaterWeb.bdConnection.mWaterBB;

import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * @author bexy
 *
 */
public class Main {

	/**
	 * @param args 0: Configuration_id
	 * 			   1: Water Market id
	 * 			   2: Start date (dd/MM/yyyy)
	 * 			   3: End date (dd/MM/yyyy)
	 * 			   4: Number of participants
	 * 			   5 Timeout for participants to join
	 * 			   6: Timeout for seller waits in negotiations
	 * 			   7: Timeout for buyers wait in negotiations
	 * 
	 * 	 		   8: max number of iterations
	 * 			   9: bid increment
	 * 			   10: initial bid
	 * 			   11 -> Partic_x_table
	 * 			   12 -> Protocol_type
	 * 
	 *			   8: mysql host
	 * 			   9: mysql user
	 * 			   10: mysql password
	 * 			   11: bd name
	 */
	public static void main(String[] args) throws Exception {

		if (args.length >= 8){
			System.out.println("COMIENZO");
			DOMConfigurator.configure("configuration/loggin.xml");
			AgentsConnection.connect();
			SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy"); 
			String config_id = args[0];
			String wmarket_id = args[1];
			String inidate = args[2];
			String enddate = args[3];
			String[] arr_inidate = inidate.split("/");
			String[] arr_cenddate = enddate.split("/");

			int partic_number = Integer.parseInt(args[4]);
			String join_Timeout = args[5];
			String initiator_Timeout = args[6];
			String partic_Timeout = args[7];
			String max_it_num = args[8];
			String bid_increm = args[9];
			String ini_bid = args[10];
			String part_x_table = args[11];
			String prot_type = args[12];

			String mysqlhost = "localhost";
			String mysqluser = "bexy";
			String mysqlpw = "bexy";
			String bd_name = "mWaterDB";
			if (args.length == 17){
				mysqlhost = args[13];
				mysqluser = args[14];
				mysqlpw = args[15];
				bd_name = args[16];
			}
			String[] connargs;
			connargs = new String[5];
			connargs[0]="com.mysql.jdbc.Driver";
			connargs[1]="jdbc:mysql://"+mysqlhost+"/"+bd_name;
			connargs[2]=mysqluser;
			connargs[3]=mysqlpw;
			connargs[4]="[]"; //tables mapped to DB

			ConvMagentixAgArch arch = new ConvMagentixAgArch();	
			mWaterBB bb = new mWaterBB();
			ConvJasonAgent staff = new ConvJasonAgent(new AgentID("staff"), "./src/test/java/mWaterWeb/mwaterJasonAgents/staff.asl", arch,bb, connargs);
			List<Literal> percept = new ArrayList<Literal>();
			percept.add(Literal.parseLiteral("currentConfiguration_id("+config_id+")[source(self)]") ) ;
			percept.add(Literal.parseLiteral("currentWaterMarket_id("+wmarket_id+")[source(self)]") ) ;
			percept.add(Literal.parseLiteral("currentTradinghall_id("+wmarket_id+")[source(self)]") ) ;
			percept.add(Literal.parseLiteral("participants_total_number("+partic_number+")[source(self)]") ) ;
			percept.add(Literal.parseLiteral("subprotocoljoinTimeOut(initiator,"+join_Timeout+")[source(self)]") ) ;
			percept.add(Literal.parseLiteral("subprotocolTimeOut(initiator,"+initiator_Timeout+")[source(self)]") ) ;
			percept.add(Literal.parseLiteral("subprotocolTimeOut(participant,"+partic_Timeout+")[source(self)]") ) ;
			percept.add(Literal.parseLiteral("configuration_date("+arr_inidate[2]+","+arr_inidate[1]+","+arr_inidate[0]+")[source(self)]"));
			percept.add(Literal.parseLiteral("configuration_end_date("+arr_cenddate[2]+","+arr_cenddate[1]+","+arr_cenddate[0]+")[source(self)]"));
			//percept.add(Literal.parseLiteral("configuration_tables("+trading_tables_no+")[source(self)]"));
			//[max_iterations(RMaxIt)],[increment(RBidInc)],[initial_bid(RIniBid)],[protocol_type(RPtype)],[num_participants(RNPart)]
			percept.add(Literal.parseLiteral("japaneseauc_protocolparameters([max_iterations("+max_it_num+"),"+
					"increment("+bid_increm+"),initial_bid("+ini_bid+"),protocol_type("+prot_type+"),num_participants("+part_x_table+")])[source(self)]"));
			percept.add(Literal.parseLiteral("configuration_finished_tables([])[source(self)]"));
			staff.getAgArch().setPerception(percept);
			
			percept.clear();
			percept.add(Literal.parseLiteral("firstRound(true)[source(self)]"));
			
			ConvMagentixAgArch webInterfaceAgentarch = new ConvMagentixAgArch();
			ConvJasonAgent webInterfaceAgent = new ConvJasonAgent(new AgentID("webInterfaceAgent"), "./src/test/java/mWaterWeb/webInterface/webInterfaceAgent.asl", webInterfaceAgentarch,null,null);

			//ConvMagentixAgArch archABella = new ConvMagentixAgArch();
			//ConvJasonAgent ABella = new ConvJasonAgent(new AgentID("ABella"), "./src/test/java/mWaterWeb/mwaterJasonAgents/automatic_agent.asl", archABella,null,null);
			//ABella.getAgArch().setPerception(percept);
			//ConvMagentixAgArch archEMDura = new ConvMagentixAgArch();
			//ConvJasonAgent EMDura = new ConvJasonAgent(new AgentID("EMDura"), "./src/test/java/mWaterWeb/mwaterJasonAgents/automatic_agent.asl", archEMDura,null,null);
			//EMDura.getAgArch().setPerception(percept);
			
			//ConvMagentixAgArch archAGarrido = new ConvMagentixAgArch();
			//ConvJasonAgent AGarrido = new ConvJasonAgent(new AgentID("AGarrido"), "./src/test/java/mWaterWeb/mwaterJasonAgents/automatic_agent.asl", archAGarrido,null,null);
			//AGarrido.getAgArch().setPerception(percept);
			staff.start();
			//Thread.sleep(3000, 0);
			webInterfaceAgent.start();

			//ABella.start();
			//EMDura.start();
			//AGarrido.start();

		}
	}

	public void create_configuration(String pHost, String pDataBase, String pUser, String pPassword, String sim_description) throws SQLException{
		Connection con_mysql = null;
		// Inserting a new configuration
		try {
			String databaseURL = "jdbc:mysql://" + pHost + "/" + pDataBase;
			Class.forName("com.mysql.jdbc.Driver");
			con_mysql = java.sql.DriverManager.getConnection(databaseURL, pUser, pPassword);

			// establecemos que no sea autocommit,
			// asi controlamos la transaccion de manera manual
			con_mysql.setAutoCommit(false);
			CallableStatement sp1 = con_mysql.prepareCall("{ call create_configuration(?,?,?) }");
			sp1.setString(1, sim_description);
			sp1.registerOutParameter(2, java.sql.Types.INTEGER);
			sp1.registerOutParameter(3, java.sql.Types.INTEGER);
			sp1.execute();
			
			CallableStatement sp2 = con_mysql.prepareCall("{ prepare_mwater_db_given_conf(?, ?, ?, ?, ?,?, ?, ?) }");
			
			// confirmar si se ejecuto sin errores
			con_mysql.commit();
			
		} catch (Exception e) {
			con_mysql.rollback();
			e.printStackTrace();			
		}
		finally {
			con_mysql.close();
		}
		/*
		 * #Preparar datos en bd
echo "Preparing data..." >> $Diraplications"/logs/logscripts"
txt="use mWaterDB; CALL remove_configuration("$idconfig","$idwmarket"); CALL prepare_mwater_db('"$sim_descrip"',  "$ag_no", "$tbl_no", "$max_ag_x_tbl", "$ag_x_tbl_and_pc", "$pcs_no", "$ini_usr_id"); "; echo $txt > tempsqlscript.sql; 
mysql -ubexy -pbexy < tempsqlscript.sql; #ojo: esto se debe pasar como parámetro
rm tempsqlscript.sql;
echo "Data has been prepared." >> $Diraplications"/logs/logscripts"*/
	}
}