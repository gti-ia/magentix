package Argumentation_Example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Vector;

import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Group;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialContext.DependencyRelation;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialEntity;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ValPref;
import es.upv.dsic.gti_ia.core.AgentID;

public class AgentsCreation {
	
	
	
	public static ArrayList<ValPref> getValPrefList(){
		ArrayList<ValPref> preferredValues=new ArrayList<ValPref>();
		
		ArrayList<String> preferredValues0=new ArrayList<String>();
		preferredValues0.add(new String("ahorro"));
		preferredValues0.add(new String("calidad"));
		preferredValues0.add(new String("rapidez"));
		
		ArrayList<String> preferredValues1=new ArrayList<String>();
		preferredValues1.add(new String("calidad"));
		preferredValues1.add(new String("rapidez"));
		preferredValues1.add(new String("ahorro"));
				
		ArrayList<String> preferredValues2=new ArrayList<String>();
		preferredValues2.add(new String("rapidez"));
		preferredValues2.add(new String("ahorro"));
		preferredValues2.add(new String("calidad"));
		
		ArrayList<String> preferredValues3=new ArrayList<String>();
		preferredValues3.add(new String("ahorro"));
		preferredValues3.add(new String("rapidez"));
		preferredValues3.add(new String("calidad"));
		
		ArrayList<String> preferredValues4=new ArrayList<String>();
		preferredValues4.add(new String("calidad"));
		preferredValues4.add(new String("ahorro"));
		preferredValues4.add(new String("rapidez"));
		
		ArrayList<String> preferredValues5=new ArrayList<String>();
		preferredValues5.add(new String("rapidez"));
		preferredValues5.add(new String("calidad"));
		preferredValues5.add(new String("ahorro"));
		
		preferredValues.add(new ValPref(preferredValues0));
		preferredValues.add(new ValPref(preferredValues1));
		preferredValues.add(new ValPref(preferredValues2));
		preferredValues.add(new ValPref(preferredValues3));
		preferredValues.add(new ValPref(preferredValues4));
		preferredValues.add(new ValPref(preferredValues5));

		
		return preferredValues;
	}
	
	public static ArrayList<ValPref> getValPrefVoidList(){
		ArrayList<ValPref> preferredValues=new ArrayList<ValPref>();
		
		ArrayList<String> preferredValues0=new ArrayList<String>();
		preferredValues0.add(new String(""));		
		preferredValues.add(new ValPref(preferredValues0));
		return preferredValues;
	}
	
//	
//	public static void createDomCasesPartitions(ArrayList<String> destFileNames,int nCases){
//		
//		DomainCBR domCBR=new DomainCBR("Helpdesk-Cases.owl"); //TODO we could use directly the parser
//		ArrayList<Case> allCases=domCBR.getAllCasesList();
//		OWLDomainParser domainParser=new OWLDomainParser();
//		
//		
//		//ArrayList<ArrayList<Case>> partitionsCases=new ArrayList<ArrayList<Case>>();
//		for(int partitions=0;partitions<destFileNames.size();partitions++){
//			Vector<Case> currentPartition=new Vector<Case>();
//			for(int i=0;i<nCases;i++){
//				int index=(int)(Math.random()*allCases.size());
//				Case aCase=allCases.get(index);
//				currentPartition.add(aCase);
//			}
//			//partitionsCases.add(currentPartition);
//			
//			//TODO save the list currentPartition in the given file, using the function of domain onto parser
//			
//			try {
////				domainParser.saveCasesInDomainOntology(currentPartition, "HelpdeskOnto.owl", destFileNames.get(partitions));
//				domainParser.saveCasesInDomainOntology(currentPartition, destFileNames.get(partitions), destFileNames.get(partitions));
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//		}
//		
//	}
	
	
	
//	public static void createArgCasesPartitions(String initialOnto,ArrayList<String> destFileNames){
//		OWLArgCBRParser owlArgCBRParser=new OWLArgCBRParser();
//		for(int i=0;i<destFileNames.size();i++){
//			try {
//				owlArgCBRParser.saveArgumentationOntology(new Vector<ArgumentCase>(), initialOnto, destFileNames.get(i));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	
	public static void createEmptyArgCasesPartitions(ArrayList<String> destFileNames){
	
		for(int i=0;i<destFileNames.size();i++){
			try {
				FileWriter fstream = new FileWriter(destFileNames.get(i),false);
				BufferedWriter outFile = new BufferedWriter(fstream);
				//Close the output stream
				outFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static ArrayList<SocialEntity> createSocialEntities(String baseName, int nOperators, int nExperts, int nManagers){
		ArrayList<ValPref> preferredValues=AgentsCreation.getValPrefList();
		ArrayList<SocialEntity> socialEntities=new ArrayList<SocialEntity>();
		for(int i=0;i<nOperators;i++){
			//preferredValues randomly distributed 
//			int index=(int)(Math.random()*preferredValues.size());
//			SocialEntity socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(index));
			SocialEntity socialEntity;
//			if (i<5)
//				socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(i));
//			else
//				socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(5));
			socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(1));

			socialEntities.add(socialEntity);
		}
		for(int i=0;i<nExperts;i++){
			//preferredValues randomly distributed 
//			int index=(int)(Math.random()*preferredValues.size());
//			SocialEntity socialEntity=new SocialEntity(i+nOperators, baseName+"Expert"+i, "expert", null, preferredValues.get(index));
			SocialEntity socialEntity;
//			if (i<5)
//				socialEntity=new SocialEntity(i, baseName+"Expert"+i, "expert", null, preferredValues.get(i));
//			else
//				socialEntity=new SocialEntity(i, baseName+"Expert"+i, "expert", null, preferredValues.get(5));
			socialEntity=new SocialEntity(i, baseName+"Expert"+i, "expert", null, preferredValues.get(1));

			socialEntities.add(socialEntity);
		}
		for(int i=0;i<nManagers;i++){
			//preferredValues randomly distributed 
//			int index=(int)(Math.random()*preferredValues.size());
//			SocialEntity socialEntity=new SocialEntity(i+nOperators+nExperts, baseName+"Manager"+i, "manager", null, preferredValues.get(index));
			SocialEntity socialEntity;
//			if (i<5)
//				socialEntity=new SocialEntity(i, baseName+"Manager"+i, "manager", null, preferredValues.get(i));
//			else
//				socialEntity=new SocialEntity(i, baseName+"Manager"+i, "manager", null, preferredValues.get(5));
			socialEntity=new SocialEntity(i, baseName+"Manager"+i, "manager", null, preferredValues.get(1));

			socialEntities.add(socialEntity);		
		}
		
		
		return socialEntities;
	}
	
	public static ArrayList<SocialEntity> createSocialEntitiesDifRoles(String baseName, int nManagers, int nExperts, int nOperators){
		ArrayList<ValPref> preferredValues=AgentsCreation.getValPrefList();
		ArrayList<SocialEntity> socialEntities=new ArrayList<SocialEntity>();
				
		int j = 0;
		SocialEntity socialEntity;
		
		for (int i = j; i< nManagers; i++){
//			if preferredValues randomly distributed 
			int index=(int)(Math.random()*preferredValues.size());
			if (index<5)
				socialEntity=new SocialEntity(i, baseName+"Manager"+i, "manager", null, preferredValues.get(index));
			else
				socialEntity=new SocialEntity(i, baseName+"Manager"+i, "manager", null, preferredValues.get(5));
			
//			socialEntity=new SocialEntity(i, baseName+"Manager"+i, "manager", null, preferredValues.get(1));
			socialEntities.add(socialEntity);
			j++;
		}
		
		for (int i = j; i < nExperts; i++){
//			if preferredValues randomly distributed 
			int index=(int)(Math.random()*preferredValues.size());
			if (index<5)
				socialEntity=new SocialEntity(i, baseName+"Expert"+i, "expert", null, preferredValues.get(index));
			else
				socialEntity=new SocialEntity(i, baseName+"Expert"+i, "expert", null, preferredValues.get(5));
			
//			socialEntity=new SocialEntity(i, baseName+"Expert"+i, "expert", null, preferredValues.get(1));
			socialEntities.add(socialEntity);
			j++;
		}
		
		for (int i = j; i < nOperators; i++){
//			if preferredValues randomly distributed 
			int index=(int)(Math.random()*preferredValues.size());
			if (index<5)
				socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(index));
			else
				socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(5));
			
//			socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(1));
			socialEntities.add(socialEntity);
			j++;
		}
		
		return socialEntities;
	}
	
	public static ArrayList<SocialEntity> createSocialEntitiesDifRoles(int valList,String baseName, int nManagers, int nExperts, int nOperators){
		ArrayList<ValPref> preferredValues=AgentsCreation.getValPrefList();
		ArrayList<SocialEntity> socialEntities=new ArrayList<SocialEntity>();
		
		int totalAgents = nManagers + nExperts + nOperators;
		
		int j = 0;
		SocialEntity socialEntity;
		
		for (int i = j; i< nManagers; i++){
//			if preferredValues randomly distributed 
//			int index=(int)(Math.random()*preferredValues.size());
//			if (i<5)
//				socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(i));
//			else
//				socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(5));
			
			socialEntity=new SocialEntity(i, baseName+"Manager"+i, "manager", null, preferredValues.get(valList));
			socialEntities.add(socialEntity);
			j++;
		}
		
		for (int i = j; i < nExperts; i++){
//			if preferredValues randomly distributed 
//			int index=(int)(Math.random()*preferredValues.size());
//			if (i<5)
//				socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(i));
//			else
//				socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(5));
			
			socialEntity=new SocialEntity(i, baseName+"Expert"+i, "expert", null, preferredValues.get(valList));
			socialEntities.add(socialEntity);
			j++;
		}
		
		for (int i = j; i < nOperators; i++){
//			if preferredValues randomly distributed 
//			int index=(int)(Math.random()*preferredValues.size());
//			if (i<5)
//				socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(i));
//			else
//				socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(5));
			
			socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(valList));
			socialEntities.add(socialEntity);
			j++;
		}
		
		return socialEntities;
	}
	
	public static ArrayList<SocialEntity> createSocialEntitiesDifRolesVoidValues(String baseName, int nManagers, int nExperts, int nOperators){
		ArrayList<ValPref> preferredValues=AgentsCreation.getValPrefList();
		ArrayList<ValPref> preferredValuesVoid=AgentsCreation.getValPrefVoidList();
		ArrayList<SocialEntity> socialEntities=new ArrayList<SocialEntity>();
				
		int j = 0;
		SocialEntity socialEntity;
		
		for (int i = j; i< nManagers; i++){
//			if preferredValues randomly distributed 
			int index=(int)(Math.random()*preferredValues.size());
			
			if (i == 0 && index <5)
				socialEntity=new SocialEntity(i, baseName+"Manager"+i, "manager", null, preferredValues.get(index));
			else if (i == 0)
				socialEntity=new SocialEntity(i, baseName+"Manager"+i, "manager", null, preferredValues.get(5));
			else
				socialEntity=new SocialEntity(i, baseName+"Manager"+i, "manager", null, preferredValuesVoid.get(0));
			
			socialEntities.add(socialEntity);
			j++;
		}
		
		for (int i = j; i < nExperts; i++){
//			if preferredValues randomly distributed 
			int index=(int)(Math.random()*preferredValues.size());
			
			if (i == 0 && index <5)
				socialEntity=new SocialEntity(i, baseName+"Expert"+i, "expert", null, preferredValues.get(index));
			else if (i == 0)
				socialEntity=new SocialEntity(i, baseName+"Expert"+i, "expert", null, preferredValues.get(5));
			else
				socialEntity=new SocialEntity(i, baseName+"Expert"+i, "expert", null, preferredValuesVoid.get(0));
			
			socialEntities.add(socialEntity);
			j++;
		}
		
		for (int i = j; i < nOperators; i++){
//			if preferredValues randomly distributed 
			int index=(int)(Math.random()*preferredValues.size());
			
			if (i == 0 && index <5)
				socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(index));
			else if (i == 0)
				socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValues.get(5));
			else
				socialEntity=new SocialEntity(i, baseName+"Operator"+i, "operator", null, preferredValuesVoid.get(0));
			
			socialEntities.add(socialEntity);
			j++;
		}
		
		return socialEntities;
	}
	
	
	
	public static ArrayList<ArrayList<SocialEntity>> createFriendsLists(ArrayList<SocialEntity> socialEntities){
		int totalAgents=socialEntities.size();
		ArrayList<ArrayList<SocialEntity>> friendsLists=new ArrayList<ArrayList<SocialEntity>>();
		for(int i=0;i<totalAgents;i++){
			ArrayList<SocialEntity> friends=new ArrayList<SocialEntity>();
			for(int j=0;j<totalAgents;j++){
				if(i!=j)
					friends.add(socialEntities.get(j));
			}
			friendsLists.add(friends);
		}
		
		return friendsLists;
	}
	
	public static ArrayList<ArrayList<DependencyRelation>> createDependencyRelations(int nOperators, int nExperts, int nManagers){
		ArrayList<ArrayList<DependencyRelation>> dependencyLists=new ArrayList<ArrayList<DependencyRelation>>();
		//int totalAgents=nOperators+nExperts+nManagers;
		
		for(int i=0;i<nOperators;i++){
			ArrayList<DependencyRelation> operatorDepenRels=new ArrayList<DependencyRelation>();
			for(int j=0;j<nOperators-1;j++){
				operatorDepenRels.add(DependencyRelation.CHARITY);
			}
			for(int j=0;j<nExperts;j++){
				operatorDepenRels.add(DependencyRelation.CHARITY);
			}
			for(int j=0;j<nManagers;j++){
				operatorDepenRels.add(DependencyRelation.CHARITY);
			}
			
			dependencyLists.add(operatorDepenRels);
		}
		
		
		for(int i=0;i<nExperts;i++){
			ArrayList<DependencyRelation> expertDepenRels=new ArrayList<DependencyRelation>();
			for(int j=0;j<nOperators;j++){
				expertDepenRels.add(DependencyRelation.AUTHORISATION);
			}
			for(int j=0;j<nExperts-1;j++){
				expertDepenRels.add(DependencyRelation.CHARITY);
			}
			for(int j=0;j<nManagers;j++){
				expertDepenRels.add(DependencyRelation.CHARITY);
			}
			
			dependencyLists.add(expertDepenRels);
		}
		
		
		for(int i=0;i<nManagers;i++){
			ArrayList<DependencyRelation> managerDepenRels=new ArrayList<DependencyRelation>();
			for(int j=0;j<nOperators;j++){
				managerDepenRels.add(DependencyRelation.POWER);
			}
			for(int j=0;j<nExperts;j++){
				managerDepenRels.add(DependencyRelation.POWER);
			}
			for(int j=0;j<nManagers-1;j++){
				managerDepenRels.add(DependencyRelation.CHARITY);
			}
			
			dependencyLists.add(managerDepenRels);
		}
		
		
		return dependencyLists;
	}
	
	public static ArrayList<ArrayList<DependencyRelation>> createDependencyRelationsDifRoles(int nManagers, int nExperts, int nOperators){
		ArrayList<ArrayList<DependencyRelation>> dependencyLists=new ArrayList<ArrayList<DependencyRelation>>();
		//int totalAgents=nOperators+nExperts+nManagers;
		
		for(int i=0;i<nManagers;i++){
			ArrayList<DependencyRelation> managerDepenRels=new ArrayList<DependencyRelation>();
			for(int j=0;j<nOperators;j++){
				managerDepenRels.add(DependencyRelation.POWER);
			}
			for(int j=0;j<nExperts;j++){
				managerDepenRels.add(DependencyRelation.POWER);
			}
			for(int j=0;j<nManagers-1;j++){
				managerDepenRels.add(DependencyRelation.CHARITY);
			}
			
			dependencyLists.add(managerDepenRels);
		}		
		
		for(int i=0;i<nExperts;i++){
			ArrayList<DependencyRelation> expertDepenRels=new ArrayList<DependencyRelation>();
			for(int j=0;j<nOperators;j++){
				expertDepenRels.add(DependencyRelation.AUTHORISATION);
			}
			for(int j=0;j<nExperts-1;j++){
				expertDepenRels.add(DependencyRelation.CHARITY);
			}
			for(int j=0;j<nManagers;j++){
				expertDepenRels.add(DependencyRelation.CHARITY);
			}
			
			dependencyLists.add(expertDepenRels);
		}
		
		for(int i=0;i<nOperators;i++){
			ArrayList<DependencyRelation> operatorDepenRels=new ArrayList<DependencyRelation>();
			for(int j=0;j<nOperators-1;j++){
				operatorDepenRels.add(DependencyRelation.CHARITY);
			}
			for(int j=0;j<nExperts;j++){
				operatorDepenRels.add(DependencyRelation.CHARITY);
			}
			for(int j=0;j<nManagers;j++){
				operatorDepenRels.add(DependencyRelation.CHARITY);
			}
			
			dependencyLists.add(operatorDepenRels);
		}
		
		return dependencyLists;
	}
	
	
//	public static ArrayList<RandAgent> createRandomAgents(ArrayList<SocialEntity> socialEntities, 
//			ArrayList<ArrayList<SocialEntity>> friendsLists, Group group, ArrayList<String> owlFileNames, int nCases, String testerAgentID){
//		
//		ArrayList<RandAgent> agents = new ArrayList<RandAgent>();
//		
//		float threshold=0.5f;
//		
//		
//		int totalAgents=socialEntities.size();
//		//createDomCasesPartitions(owlFileNames,nCases);
//		
//		try {
//			
//			RandAgent randAgent1 = new RandAgent(new AgentID("qpid://"+socialEntities.get(0).getName()+"@localhost:8080"), 
//						true, socialEntities.get(0), friendsLists.get(0), group, 
//						"commitmentStore", testerAgentID, owlFileNames.get(0), owlFileNames.get(0), threshold);
//			randAgent1.start();
//			agents.add(randAgent1);
//			
//			for(int i=1;i<totalAgents;i++){
//				RandAgent randAgent=new RandAgent(new AgentID("qpid://"+socialEntities.get(i).getName()+"@localhost:8080"), 
//						false, socialEntities.get(i), friendsLists.get(i),  group, 
//						"commitmentStore", null, owlFileNames.get(i), owlFileNames.get(i), threshold);
//				randAgent.start();
//				agents.add(randAgent);
//			}
//		
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
//		
//		
//		return agents;
//		
//		
//	}
//	
//	
//	public static ArrayList<VotAgent> createVotAgents(ArrayList<SocialEntity> socialEntities, 
//			ArrayList<ArrayList<SocialEntity>> friendsLists, Group group, ArrayList<String> owlFileNames, int nCases, String testerAgentID){
//		
//		ArrayList<VotAgent> agents = new ArrayList<VotAgent>();
//		
//		float threshold=0.5f;
//		
//		
//		int totalAgents=socialEntities.size();
//		//createDomCasesPartitions(owlFileNames,nCases);
//		
//		try {
//			
//			VotAgent votAgent1 = new VotAgent(new AgentID("qpid://"+socialEntities.get(0).getName()+"@localhost:8080"), 
//						true, socialEntities.get(0), friendsLists.get(0), group, 
//						"commitmentStore", testerAgentID, owlFileNames.get(0), owlFileNames.get(0), threshold);
//			votAgent1.start();
//			agents.add(votAgent1);
//			
//			for(int i=1;i<totalAgents;i++){
//				VotAgent votAgent=new VotAgent(new AgentID("qpid://"+socialEntities.get(i).getName()+"@localhost:8080"), 
//						false, socialEntities.get(i), friendsLists.get(i),  group, 
//						"commitmentStore", null, owlFileNames.get(i), owlFileNames.get(i), threshold);
//				votAgent.start();
//				agents.add(votAgent);
//			}
//		
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
//		
//		
//		return agents;
//		
//		
//	}
//	
//	public static ArrayList<ArgAgent> createArgAgents(ArrayList<SocialEntity> socialEntities, 
//			ArrayList<ArrayList<SocialEntity>> friendsLists, ArrayList<ArrayList<DependencyRelation>> dependencyRels, 
//			Group group, ArrayList<String> owlFileNames, int nCases, String testerAgentID){
//		
//		ArrayList<ArgAgent> agents = new ArrayList<ArgAgent>();
//		
//		float threshold=0.5f;
//		
//		int totalAgents=socialEntities.size();
//		//createDomCasesPartitions(owlFileNames,nCases);
//		
//		try {
//			
//			ArgAgent argAgent1 = new ArgAgent(new AgentID("qpid://"+socialEntities.get(0).getName()+"@localhost:8080"), 
//						true, socialEntities.get(0), friendsLists.get(0), dependencyRels.get(0), 
//						group, "commitmentStore", testerAgentID, owlFileNames.get(0), owlFileNames.get(0), threshold);
//			argAgent1.start();
//			agents.add(argAgent1);
//			
//			for(int i=1;i<totalAgents;i++){
//				ArgAgent argAgent=new ArgAgent(new AgentID("qpid://"+socialEntities.get(i).getName()+"@localhost:8080"), 
//						false, socialEntities.get(i), friendsLists.get(i), dependencyRels.get(i), 
//						group, "commitmentStore", null, owlFileNames.get(i), owlFileNames.get(i), threshold);
//				argAgent.start();
//				agents.add(argAgent);
//			}
//		
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
//		
//		
//		return agents;
//		
//		
//	}
	
	
	public static ArrayList<ArgCAgent> createArgLearnAgentsInc(ArrayList<SocialEntity> socialEntities, 
			ArrayList<ArrayList<SocialEntity>> friendsLists, ArrayList<ArrayList<DependencyRelation>> dependencyRels, Group group, 
			ArrayList<String> iniDomainFileNames, ArrayList<String> finDomainFileNames, int domCBRindex, float domCBRthreshold,
			ArrayList<String> iniArgFileNames,  ArrayList<String> finArgFileNames,int nArgCases, String testerAgentID, 
			float wPD, float wSD, float wRD, float wAD, float wED, float wEP){
		
		ArrayList<ArgCAgent> agents = new ArrayList<ArgCAgent>();
		
		int totalAgents=socialEntities.size();
		//createDomCasesPartitions(owlDomainFileNames,nDomCases);
		
		try {
			
			
			for(int i=0;i<totalAgents;i++){
				ArgCAgent argLeanAgent=new ArgCAgent(new AgentID("qpid://"+socialEntities.get(i).getName()+"@localhost:8080"), 
						false, socialEntities.get(i), friendsLists.get(i),  dependencyRels.get(i), 
						group, "commitmentStore", null, 
						iniDomainFileNames.get(i), finDomainFileNames.get(i), domCBRindex, domCBRthreshold,
						iniArgFileNames.get(i), finArgFileNames.get(i), wPD, wSD, wRD, wAD, wED, wEP);
				argLeanAgent.start();
				agents.add(argLeanAgent);
			}
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		return agents;
		
		
	}
//	
//	public static ArrayList<ArgLearnAgentNoLearn> createArgLearnNoLearnAgents(ArrayList<SocialEntity> socialEntities, 
//			ArrayList<ArrayList<SocialEntity>> friendsLists, ArrayList<ArrayList<DependencyRelation>> dependencyRels, Group group, 
//			ArrayList<String> iniDomainFileNames, ArrayList<String> finDomainFileNames, ArrayList<String> owlArgFileNames, int nDomCases, int nArgCases, String testerAgentID,
//			float threshold, float wPD, float wSD, float wRD, float wAD, float wED, float wEP){
//		
//		ArrayList<ArgLearnAgentNoLearn> agents = new ArrayList<ArgLearnAgentNoLearn>();
//		
//		int totalAgents=socialEntities.size();
//		//createDomCasesPartitions(owlDomainFileNames,nDomCases);
//		
//		try {
//			
//			ArgLearnAgentNoLearn argLearnAgentNoLearn1 = new ArgLearnAgentNoLearn(new AgentID("qpid://"+socialEntities.get(0).getName()+"@localhost:8080"), 
//						true, socialEntities.get(0), friendsLists.get(0), dependencyRels.get(0), 
//						group, "commitmentStore", testerAgentID, iniDomainFileNames.get(0), finDomainFileNames.get(0),
//						owlArgFileNames.get(0), owlArgFileNames.get(0), threshold, wPD, wSD, wRD, wAD, wED, wEP);
//			argLearnAgentNoLearn1.start();
//			agents.add(argLearnAgentNoLearn1);
//			
//			for(int i=1;i<totalAgents;i++){
//				ArgLearnAgentNoLearn argLeanAgentNoLearn=new ArgLearnAgentNoLearn(new AgentID("qpid://"+socialEntities.get(i).getName()+"@localhost:8080"), 
//						false, socialEntities.get(i), friendsLists.get(i),  dependencyRels.get(i), 
//						group, "commitmentStore", null, iniDomainFileNames.get(i), finDomainFileNames.get(i),
//						owlArgFileNames.get(i), owlArgFileNames.get(i), threshold, wPD, wSD, wRD, wAD, wED, wEP);
//				argLeanAgentNoLearn.start();
//				agents.add(argLeanAgentNoLearn);
//			}
//		
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
//		
//		
//		return agents;
//		
//		
//	}
	
	
	//TODO read helpdesk global and create list of operators and groups
	
	//TODO initialize agents with the name of the operators of the list, if the list is too short, invent more names
	
	//TODO partition helpdesk database in several databases, one per agent
	
	//TODO do a function to assign to each operator ONLY the tickets that he has solved
	
	//TODO program a bobo agent and a semi-bobo agent
	
	//TODO update the tester agent
	
	//TODO create a class for each test
}
