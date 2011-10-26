package Argumentation_Example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Vector;


import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.argAgents.CommitmentStore;
import es.upv.dsic.gti_ia.argAgents.CommitmentStore2;
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
//			int totalAgents=nOperators+nExperts+nManagers;
			
			String testerAgentID="testerAgent";
			String finishFileName="testArgLearnAgfinished";
			
			ArrayList<String> iniDomainFiles=new ArrayList<String>();
			ArrayList<String> finDomainFiles=new ArrayList<String>();
			ArrayList<String> iniArgFileNames=new ArrayList<String>();
			ArrayList<String> finArgFileNames=new ArrayList<String>();

//			for(int i=0;i<nOperators;i++){
//				//TODO change it when saveCases function implemented
////				owlDomainFileNames.add("partitions/test2DomCBArg"+"Operator"+i+".dat");
//				owlArgFileNames.add("partArg/test2ArgCBArg"+"Operator"+i+".dat");
//			}
//			for(int i=0;i<nExperts;i++){
//				//TODO change it when saveCases function implemented
////				owlDomainFileNames.add("partitions/test2DomCBArg"+"Expert"+i+".dat");
//				owlArgFileNames.add("partArg/test2ArgCBArg"+"Expert"+i+".dat");
//			}
//			for(int i=0;i<nManagers;i++){
//				//TODO change it when saveCases function implemented
////				owlDomainFileNames.add("partitions/test2DomCBArg"+"Manager"+i+".dat");
//				owlArgFileNames.add("partArg/test2ArgCBArg"+"Manager"+i+".dat");
//			}
			
			
//			Iterator<ArrayList<DependencyRelation>> iterLists=depenRelsLists.iterator();
//			int count=0;
//			while(iterLists.hasNext()){
//				System.out.println(count);
//				count++;
//				ArrayList<DependencyRelation> list=iterLists.next();
//				Iterator<DependencyRelation> iterList=list.iterator();
//				while(iterList.hasNext()){
//					System.out.println(iterList.next().toString());
//				}
//				System.out.println();
//			}
			
			Vector<DomainCase> tickets=CreatePartitions.getTestDomainCases();
						
			for(nOperators = 5; nOperators <= 5; nOperators+=2){	
			
				int nArgCases=10;
			
			
//			for(int nDomCases=5;nDomCases<=25;nDomCases+=5){
//				for(int repetition=0;repetition<10;repetition++){	
				
//				ArrayList<String> owlArgFileNames=new ArrayList<String>();
//				for(int cases=5;cases<=45;cases+=5){
//					for(int i=0;i<nOperators;i++){
//						owlArgFileNames.add("partArg/partArg"+cases+"cas"+i+"op.dat");
//					}
//				}
//				AgentsCreation.createEmptyArgCasesPartitions(owlArgFileNames);
			
				ArrayList<SocialEntity> socialEntities=AgentsCreation.createSocialEntities("ArgLearnAg",nOperators, nExperts, nManagers);
				ArrayList<ArrayList<SocialEntity>> friendsLists=AgentsCreation.createFriendsLists(socialEntities);
				ArrayList<ArrayList<DependencyRelation>> depenRelsLists=AgentsCreation.createDependencyRelations(nOperators, nExperts, nManagers);
				
				ArrayList<String> values=new ArrayList<String>();
				values.add("ahorro");values.add("rapidez");values.add("calidad");
				Group group=new Group(1, "group1", new ValPref(values), socialEntities);
				
				int casesInc;
			
				for(int cases=25;cases<=45;cases+=5){
//					for(int repetition=0;repetition<tickets.size();repetition++){
					
					casesInc = cases + 5;
					
						iniDomainFiles=new ArrayList<String>();
						for(int i=0;i<nOperators;i++){
							iniDomainFiles.add("partitionsInc/part"+cases+"cas"+i+"op.dat");

						}
						
						finDomainFiles=new ArrayList<String>();
						for(int i=0;i<nOperators;i++){
							finDomainFiles.add("partitionsInc/part"+cases+"cas"+i+"op.dat");

						}
						
//						iniArgFileNames=new ArrayList<String>();
//						for(int i=0;i<nOperators;i++){
//							iniArgFileNames.add("partArgInc/partArg"+cases+"cas"+i+"op.dat");
//
//						}
						
						iniArgFileNames=new ArrayList<String>();
						for(int i=0;i<nOperators;i++){
							iniArgFileNames.add("partArgInc/partArg"+"Operator"+i+".dat");

						}
						
						finArgFileNames=new ArrayList<String>();
						for(int i=0;i<nOperators;i++){
				
							finArgFileNames.add("partArgInc/partArg"+"Operator"+i+".dat");

						}
						
						
						for(int repetition=0;repetition<tickets.size();repetition++){

						
						Vector<DomainCase> aTicket=new Vector<DomainCase>();
						aTicket.add(tickets.get(repetition));
				
						CommitmentStore commitmentStore = new CommitmentStore(new AgentID("qpid://commitmentStore@localhost:8080"));
						commitmentStore.start();
						
						ArrayList<ArgCAgent> agents = new ArrayList<ArgCAgent>();
						
						if (repetition<(tickets.size()-1)){
							agents = AgentsCreation.createArgLearnAgentsInc(socialEntities, friendsLists, depenRelsLists, 
									group, iniDomainFiles, iniDomainFiles, iniArgFileNames, iniArgFileNames, nArgCases, testerAgentID, 0.5f, 1f, 0f,0f,0f,0f,0f);
						}else{
							agents = AgentsCreation.createArgLearnAgentsInc(socialEntities, friendsLists, depenRelsLists, 
									group, iniDomainFiles, finDomainFiles, iniArgFileNames, finArgFileNames, nArgCases, testerAgentID, 0.5f, 1f, 0f,0f,0f,0f,0f);
						}
						
//						agents = AgentsCreation.createArgLearnAgentsInc(socialEntities, friendsLists, depenRelsLists, 
//								group, owlDomainFiles, iniArgFileNames, iniArgFileNames, nArgCases, testerAgentID, 0.5f, 1f, 1f,1f,1f,1f,1f);
//					
						
						
						
						TesterAgentArgLearn1and2 testerAgent= new TesterAgentArgLearn1and2(new AgentID("qpid://"+testerAgentID+"@localhost:8080"), nTickets, 
								socialEntities, commitmentStore.getName(), "results/performance/test1and2Inc/test2argLearnLL5DC+0AC+Per-"+nOperators+"agB.txt",
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
