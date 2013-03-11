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
public class MainCreateParameterizedAgents {
	/**
	 * @param args 0: A list of agents ids separated by ","
	 * 			   1: max number of iterations
	 * 			   2: bid increment
	 * 			   3: initial bid
	 * 			   4 -> Partic_x_table
	 * 			   5 -> Protocol_type
	 */
	public static void main(String[] args) throws Exception {
		Layout l;
		FileAppender fa;
		//Logger log = Logger.getLogger(MainCreateParameterizedAgents.class);
		
		if (args.length == 6)
		{
			l = new PatternLayout("%m%n");
			fa = new FileAppender(l,"logs/mwaterperformance.log", true);
			Logger mylogger = Logger.getLogger(MainCreateParameterizedAgents.class.getName());
			mylogger.addAppender(fa);

			//String namePreStr = args[0];
			//String iniAgId = args[1];
			//int agNumber = Integer.parseInt(args[2]);
			//The fourth parameter is a string with the format "16:10,26:10" specifing the range of agents identifiers
			String[] agents = args[0].split(","); //Here we obtain the agents names
			int agNumber = agents.length;
			String max_iterations = args[1];
			String bid_increment = args[2];
			String initial_bid = args[3];
			int partic_x_table = Integer.parseInt(args[4]);
			int protocol_type = Integer.parseInt(args[5]);
			//String acc_rate = args[7];
			
			String agName;
			//int agID = Integer.parseInt(iniAgId);
			
			System.out.println("CREATING "+agNumber+" AGENTS");
			DOMConfigurator.configure("configuration/loggin.xml");
			AgentsConnection.connect();

			ConvMagentixAgArch agentArchitecture ;
			ConvJasonAgent agent ;

			List<Literal> percept = new ArrayList<Literal>();
			percept.add(Literal.parseLiteral("max_iteration_number("+max_iterations+")[source(self)]") ) ;
			percept.add(Literal.parseLiteral("bid_increment("+bid_increment+")[source(self)]") ) ;
			percept.add(Literal.parseLiteral("initial_bid("+initial_bid+")[source(self)]") ) ;
			percept.add(Literal.parseLiteral("participants_per_table("+partic_x_table+")[source(self)]") ) ;
			percept.add(Literal.parseLiteral("protocol_negotiations_type("+protocol_type+")[source(self)]") ) ;
			//percept.add(Literal.parseLiteral("acceptance_rate("+acc_rate+")[source(self)]") ) ;
			
			for (int i=0; i < agNumber ; i++){

				agName = agents[i];
				agentArchitecture = new ConvMagentixAgArch();
				agent = new ConvJasonAgent(new AgentID(agName), "./src/test/java/mWaterWeb/mwaterJasonAgents/automatic_agent.asl", agentArchitecture,null,null);
				agent.getAgArch().setPerception(percept);
				agent.setconvLogger(mylogger);
				//agentArchitecture.getTS().getLogger().info("Creando agente "+agName);
				agent.start();
				Thread.sleep(4000, 0);

			}
		}
	}

}
