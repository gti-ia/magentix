package Argumentation_Example_Experts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import Argumentation_Example.AgentsCreationExample;
import Argumentation_Example.CreatePartitions;

import es.upv.dsic.gti_ia.argAgents.ArgCAgent;
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
public class TestArgCAgentExperts {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(TestArgCAgentExperts.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {
			
			int nOperators = 6;
			int nExperts = 1;
			int nManagers = 0;
			
			int totalOperators = nOperators+nExperts+nManagers;

			String testerAgentID = "testerAgent";
			//fileName to write if the test has finished
			String finishFileName = "testArgumentation/TestArgCAgentExpertsfinished";

			//initial domain-cases file names
			ArrayList<String> iniDomainFiles = new ArrayList<String>();
			//initial argument-cases file names
			ArrayList<String> iniArgFileNames = new ArrayList<String>();

			//test domain-cases
			Vector<DomainCase> testDomainCases = CreatePartitions.getTestDomainCases();

			//repeat test for different number of experts
			for (nExperts = 0; nExperts <= 7; nExperts++) {
				nOperators = 7 - nExperts;
				totalOperators = nOperators+nExperts;
				//initialize the argument-cases file names
				iniArgFileNames = new ArrayList<String>();
				for (int i = 0; i < totalOperators; i++) {
					iniArgFileNames.add("testArgumentation/partArgInc/partArg" + "Operator" + i + ".dat");
				}
				//empty argument-cases partitions
				AgentsCreationExample.createEmptyArgCasesPartitions(iniArgFileNames);
				
				//create the social entities, friends list, and dependency relation between all agents
				ArrayList<SocialEntity> socialEntities = AgentsCreationExample.createSocialEntities("ArgCAgent", nOperators,
						nExperts, nManagers);
				ArrayList<ArrayList<SocialEntity>> friendsLists = AgentsCreationExample.createFriendsLists(socialEntities);
				ArrayList<ArrayList<DependencyRelation>> depenRelsLists = AgentsCreationExample.createDependencyRelations(
						nOperators, nExperts, nManagers);

				//preferred values of the group
				ArrayList<String> values = new ArrayList<String>();
				values.add("savings");
				values.add("quality");
				values.add("speed");
				Group group = new Group(1, "group1", new ValPref(values), socialEntities);

				//repeat tests for different number of domain-cases for each argumentative agent
				for (int cases = 5; cases <= 45; cases += 5) {

					iniDomainFiles = new ArrayList<String>();
					ArrayList<String> finDomainFiles = new ArrayList<String>();
					for (int i = 0; i < nOperators; i++) {
						//iniDomainFiles.add("testArgumentation/partitionsInc/domCases" + cases + "cas" + i + "op.dat");
						iniDomainFiles.add("testArgumentation/partitionsInc/part" + cases + "cas" + i + "op.dat");
						finDomainFiles.add("/dev/null");
					}
					for (int i = 0; i < nExperts; i++) {
						iniDomainFiles.add("testArgumentation/partitionsInc/domCasesExp" + cases + "cas" + i + "op.dat");
						finDomainFiles.add("/dev/null");
					}

					//repetitions with different test domain-cases for the same partitions
					for (int repetition = 0; repetition < testDomainCases.size(); repetition++) {

						Vector<DomainCase> domCasesVector = new Vector<DomainCase>();
						domCasesVector.add(testDomainCases.get(repetition));

						//Create and start the Commitment Store
						CommitmentStore commitmentStore = new CommitmentStore(new AgentID(
								"qpid://commitmentStore@localhost:8080"));
						commitmentStore.start();

						//Create the argumentative agents
						ArrayList<ArgCAgent> agents = AgentsCreationExample.createArgCAgentsInc(socialEntities,
								friendsLists, depenRelsLists, group, iniDomainFiles,finDomainFiles, 0, 0.5f,
								iniArgFileNames, iniArgFileNames, testerAgentID, 1f, 1f, 1f, 1f, 1f, 1f);

						//Create the tester agent that sends the test domain-case to solve to the group of agents
						//and acts as initiator of the dialogue
						TesterArgCAgentExperts testerAgent = new TesterArgCAgentExperts(new AgentID("qpid://"
								+ testerAgentID + "@localhost:8080"), socialEntities, commitmentStore.getName(),
								"/home/jaujorpr/Dropbox/GTI-IA/articles/CompIntelligence2012/dades/ultims/test" + nOperators
										+ "op"+ nExperts +"exp.txt",
								finishFileName, cases, repetition, domCasesVector, agents);
						testerAgent.start();

						//check every second if the test has finished by reading the finish file
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
