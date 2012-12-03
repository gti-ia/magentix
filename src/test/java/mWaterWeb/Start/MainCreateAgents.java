package mWaterWeb.Start;

import java.util.Calendar;

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
public class MainCreateAgents {


	/**
	 * @param args 0:Agents name string; 
	 * 			   1:Initial identifier number for agents
	 * 			   2:Number of agents
	 * 			   3:A string with the format "16:10,26:10" specifying the range of agents identifiers
	 */
	public static void main(String[] args) throws Exception {
		Layout l;
		FileAppender fa;
		Logger log = Logger.getLogger(MainCreateAgents.class);
		Calendar cal = Calendar.getInstance();
		
		
		if (args.length == 4)
		{
			l = new PatternLayout("%m%n");
			fa = new FileAppender(l,"logs/mwaterperformance.log", true);

			Logger mylogger = Logger.getLogger(MainCreateAgents.class.getName());

			log.addAppender(fa);

			String namePreStr = args[0];
			String iniAgId = args[1];
			int agNumber = Integer.parseInt(args[2]);
			
			//The fourth parameter is a string with the format "16:10,26:10" specifing the range of agents identifiers
			String[] agentsids = args[3].split(","); //Here we obtain the ranges
			
			String agName;
			int agID = Integer.parseInt(iniAgId);
			
			System.out.println("CREATING "+agNumber+" AGENTS");
			DOMConfigurator.configure("configuration/loggin.xml");
			AgentsConnection.connect();

			ConvMagentixAgArch agentArchitecture ;
			ConvJasonAgent agent ;

			String[] tmprange;
			int iniid; int idsno; 
			for (int i=0; i < agentsids.length ; i++){
				tmprange = agentsids[i].split(":"); //this array must have two elements initial id and number of elements
				iniid = Integer.parseInt( tmprange[0]);
				idsno = Integer.parseInt( tmprange[1]);

				for (int j=iniid; j < iniid+idsno; j++){
					agName = namePreStr+j;
					agentArchitecture = new ConvMagentixAgArch();

					agent = new ConvJasonAgent(new AgentID(agName), "./src/test/java/mWaterWeb/mwaterJasonAgents/performance_aut_ag.asl", agentArchitecture,null,null);
					agent.setconvLogger(mylogger);

					agent.start();

					agID ++;
					Thread.sleep(700, 0);
				}
			}
		}
	}

}
