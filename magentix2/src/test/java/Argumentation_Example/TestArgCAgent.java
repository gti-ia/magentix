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

/**
 * This class launches a test with argumentative agents, including the
 * Commitment Store and a tester agent that acts as initiator
 * 
 * @author Jaume Jordan
 * 
 */
public class TestArgCAgent {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(TestArgCAgent.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {
			int nTestCases = 1;

			int nOperators = 3;
			int nExperts = 0;
			int nManagers = 0;

			String testerAgentID = "testerAgent";
			//fileName where it will be write if the tets has been finished
			String finishFileName = "testArgLearnAgfinished";

			//initial domain-cases file names
			ArrayList<String> iniDomainFiles = new ArrayList<String>();
			//initial argument-cases file names
			ArrayList<String> iniArgFileNames = new ArrayList<String>();

			//test domain-cases
			Vector<DomainCase> testDomainCases = CreatePartitions.getTestDomainCases();

			//repeated test for different number of operators
			for (nOperators = 3; nOperators <= 9; nOperators += 2) {
				
				//init the argument-cases file names
				iniArgFileNames = new ArrayList<String>();
				for (int i = 0; i < nOperators; i++) {
					iniArgFileNames.add("partArgInc/partArg" + "Operator" + i + ".dat");
				}
				//empty argument-cases partitions
				AgentsCreation.createEmptyArgCasesPartitions(iniArgFileNames);
				
				
				//create the social entities, friends list, and dependency relation between all agents
				ArrayList<SocialEntity> socialEntities = AgentsCreation.createSocialEntities("ArgLearnCAg", nOperators,
						nExperts, nManagers);
				ArrayList<ArrayList<SocialEntity>> friendsLists = AgentsCreation.createFriendsLists(socialEntities);
				ArrayList<ArrayList<DependencyRelation>> depenRelsLists = AgentsCreation.createDependencyRelations(
						nOperators, nExperts, nManagers);

				//preferred values of the group
				ArrayList<String> values = new ArrayList<String>();
				values.add("ahorro");
				values.add("rapidez");
				values.add("calidad");
				Group group = new Group(1, "group1", new ValPref(values), socialEntities);

				//repeated tests for different number of domain-cases for each argumentative agent
				for (int cases = 5; cases <= 45; cases += 5) {

					iniDomainFiles = new ArrayList<String>();
					for (int i = 0; i < nOperators; i++) {
						iniDomainFiles.add("partitionsInc/partContinuous" + cases + "cas" + i + "op.dat");
					}

					//repetitions with different test domain-cases for the same partitions
					for (int repetition = 0; repetition < testDomainCases.size(); repetition++) {

						Vector<DomainCase> aTestDomCase = new Vector<DomainCase>();
						aTestDomCase.add(testDomainCases.get(repetition));

						//Create and start the Commitment Store
						CommitmentStore commitmentStore = new CommitmentStore(new AgentID(
								"qpid://commitmentStore@localhost:8080"));
						commitmentStore.start();

						//Create the argumentative agents
						ArrayList<ArgCAgent> agents = AgentsCreation.createArgLearnAgentsInc(socialEntities,
								friendsLists, depenRelsLists, group, iniDomainFiles, iniDomainFiles, 0, 0.5f,
								iniArgFileNames, iniArgFileNames, testerAgentID, 1f, 1f, 1f, 1f, 1f, 1f);

						//Create the tester agent that sends the test domain-case to solve to the group of agents
						//and acts as initiator of the dialogue
						TesterArgCAgent testerAgent = new TesterArgCAgent(new AgentID("qpid://"
								+ testerAgentID + "@localhost:8080"), nTestCases, socialEntities,
								commitmentStore.getName(),
								"results/performance/test1and2Inc/argLearnContinuousLL5DC+0AC+Per-" + nOperators
										+ "ag.txt", finishFileName, cases, repetition, aTestDomCase,
								new ArrayList<String>(), agents);
						testerAgent.start();

						//check every second if the test has finished
						while (true) {
							Thread.sleep(1000);
							try {
								FileReader fstream = new FileReader(finishFileName);
								BufferedReader file = new BufferedReader(fstream);
								String line = file.readLine();
								file.close();
								if (line != null && !line.equals(""))
									break;
							} catch (Exception e) {// Catch exception if any
								System.err.println("Error reading file: " + e.getMessage());
								e.printStackTrace();
							}
						}

					}

				}

			}// while

		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}

	}

}
