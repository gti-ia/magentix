package Argumentation_Example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.argAgents.CommitmentStore;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Group;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialContext.DependencyRelation;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialEntity;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ValPref;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class TestArgLearn2Test {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(TestArgLearn2Test.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {
						
			int nTickets=1;
			
			int nOperators=3;
			int nExperts=0;
			int nManagers=0;
			
			String testerAgentID="testerAgent";
			String finishFileName="testArgLearnAgfinished";
			
			ArrayList<String> iniDomainFiles=new ArrayList<String>();
			ArrayList<String> iniArgFileNames=new ArrayList<String>();
			
			
			Vector<DomainCase> tickets=CreatePartitions.getTestDomainCases();
						
			for(nOperators = 7; nOperators <= 7; nOperators+=2){	
			
				int nArgCases=10;
			
				iniArgFileNames=new ArrayList<String>();
				for(int i=0;i<nOperators;i++){
					iniArgFileNames.add("partArgInc/partArg"+"Operator"+i+".dat");
				}
				AgentsCreation.createEmptyArgCasesPartitions(iniArgFileNames);
				
			
				ArrayList<SocialEntity> socialEntities=AgentsCreation.createSocialEntities("ArgLearnCAg",nOperators, nExperts, nManagers);
				ArrayList<ArrayList<SocialEntity>> friendsLists=AgentsCreation.createFriendsLists(socialEntities);
				ArrayList<ArrayList<DependencyRelation>> depenRelsLists=AgentsCreation.createDependencyRelations(nOperators, nExperts, nManagers);
				
				ArrayList<String> values=new ArrayList<String>();
				values.add("ahorro");values.add("rapidez");values.add("calidad");
				Group group=new Group(1, "group1", new ValPref(values), socialEntities);
				
				for(int cases=40;cases<=45;cases+=5){
				
					iniDomainFiles=new ArrayList<String>();
					for(int i=0;i<nOperators;i++){
						iniDomainFiles.add("partitionsInc/partContinuous"+cases+"cas"+i+"op.dat");
					}
						
					for(int repetition=0;repetition<tickets.size();repetition++){

						Vector<DomainCase> aTicket=new Vector<DomainCase>();
						aTicket.add(tickets.get(repetition));
				
						CommitmentStore commitmentStore = new CommitmentStore(new AgentID("qpid://commitmentStore@localhost:8080"));
						commitmentStore.start();
						
						ArrayList<ArgCAgent> agents = AgentsCreation.createArgLearnAgentsInc(socialEntities, friendsLists, depenRelsLists, 
								group, iniDomainFiles, iniDomainFiles, 0, 0.5f, iniArgFileNames, iniArgFileNames, nArgCases, testerAgentID, 1f, 1f,1f,1f,1f,1f);
							
						TesterAgentArgLearn1and2 testerAgent= new TesterAgentArgLearn1and2(new AgentID("qpid://"+testerAgentID+"@localhost:8080"), nTickets, 
								socialEntities, commitmentStore.getName(), "results/performance/test1and2Inc/argLearnContinuousLL5DC+0AC+Per-"+nOperators+"ag.txt",
								finishFileName, cases, repetition, aTicket, new ArrayList<String>(), agents);
						testerAgent.start();
						
						while(true){
							Thread.sleep(1000);
							try{
								FileReader fstream = new FileReader(finishFileName);
								BufferedReader file = new BufferedReader(fstream);
								String line=file.readLine();
								file.close();
								if(line!=null && !line.equals(""))
									break;
							}catch (Exception e){//Catch exception if any
								System.err.println("Error reading file: " + e.getMessage());
								e.printStackTrace();
							}
						}
				
					}

				}

			}//while

		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}

	}

}
