package mWaterWeb.Start;

import jason.asSyntax.Literal;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;


import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;

/**
 * Application for executing 'mwater'prototype agents in a pc
 * 
 * @author Bexy Alfonso
 */
public class MainAgents_performance2 {
	/**
	 * @param args 0: max number of iterations
	 * 			   1: bid increment
	 * 			   2: initial bid
	 * 			   3 -> Partic_x_table
	 * 			   4 -> Protocol_type
	 * 			   5 -> Initial_agent_id:tot_ag
	 */
	public static void main(String[] args) throws Exception {
		Layout l;
		FileAppender fa;
		//Logger log = Logger.getLogger(MainCreateParameterizedAgents.class);
		
		if (args.length == 6)
		{
			l = new PatternLayout("%m%n");
			fa = new FileAppender(l,"logs/mwaterperformance.log", true);
			Logger mylogger = Logger.getLogger(MainAgents_performance2.class.getName());
			mylogger.addAppender(fa);

			//String namePreStr = args[0];

			//int agNumber = Integer.parseInt(args[2]);
			//The fourth parameter is a string with the format "16:10,26:10" specifing the range of agents identifiers

			String max_iterations = args[0];
			String bid_increment = args[1];
			String initial_bid = args[2];
			int partic_x_table = Integer.parseInt(args[3]);
			int protocol_type = Integer.parseInt(args[4]);
			//String acc_rate = args[7];
			
			
			//int agID = Integer.parseInt(iniAgId);
			
			DOMConfigurator.configure("configuration/loggin.xml");
			AgentsConnection.connect();

			ConvMagentixAgArch agentArchitecture ;
			ConvJasonAgent automatic_agent_performance2 ;

			List<Literal> percept = new ArrayList<Literal>();
			percept.add(Literal.parseLiteral("max_iteration_number("+max_iterations+")[source(self)]") ) ;
			percept.add(Literal.parseLiteral("bid_increment("+bid_increment+")[source(self)]") ) ;
			percept.add(Literal.parseLiteral("initial_bid("+initial_bid+")[source(self)]") ) ;
			percept.add(Literal.parseLiteral("participants_per_table("+partic_x_table+")[source(self)]") ) ;
			percept.add(Literal.parseLiteral("protocol_negotiations_type("+protocol_type+")[source(self)]") ) ;
			//percept.add(Literal.parseLiteral("acceptance_rate("+acc_rate+")[source(self)]") ) ;
			
			
			String[] executions_array = args[5].split(",");
			String[] iniAgId_count;
			String execution;
			int agNumber,iniAgId;
			List<String> agents = new ArrayList<String>(); //= args[0].split(","); //Here we obtain the agents names
			int tmpint;
			String agName;
			for (int j=0; j < executions_array.length ; j++){
				execution = executions_array[j];
				iniAgId_count = execution.split(":");
				iniAgId = Integer.parseInt(iniAgId_count[0]);
				agNumber = Integer.parseInt(iniAgId_count[1]);
				agents.clear();
				for (int i=0;i<agNumber;i++)
				{
					tmpint=i+iniAgId;
					agents.add("aut_agent"+tmpint);
				}
				System.out.println("CREATING "+agNumber+" AGENTS");
				for (int i=0; i < agNumber ; i++){
					
					agName = agents.get(i);
					agentArchitecture = new ConvMagentixAgArch();
					
					automatic_agent_performance2 = new ConvJasonAgent(new AgentID(agName), "./src/test/java/mWaterWeb/mwaterJasonAgents/automatic_agent_performance2.asl", agentArchitecture,null,null);
					automatic_agent_performance2.getAgArch().setPerception(percept);
					automatic_agent_performance2.setconvLogger(mylogger);
					
					automatic_agent_performance2.start();
					//Thread.sleep(1000, 0);
				}
			}

			
			
			

			
			

		}
	}

}
