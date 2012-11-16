package Argumentation_Example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Group;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialContext.DependencyRelation;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialEntity;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ValPref;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 * This class has different methods to create groups of agents and some of their
 * parameters
 * 
 * @author Jaume Jordan
 * 
 */
public class AgentsCreation {

	/**
	 * Gets an {@link ArrayList} of {@link ValPref} with the different
	 * combinations of the following values: savings, quality and speed.
	 * 
	 * @return an {@link ArrayList} of {@link ValPref}
	 */
	public static ArrayList<ValPref> getValPrefList() {
		ArrayList<ValPref> preferredValues = new ArrayList<ValPref>();

		ArrayList<String> preferredValues0 = new ArrayList<String>();
		preferredValues0.add(new String("savings"));
		preferredValues0.add(new String("quality"));
		preferredValues0.add(new String("speed"));

		ArrayList<String> preferredValues1 = new ArrayList<String>();
		preferredValues1.add(new String("quality"));
		preferredValues1.add(new String("speed"));
		preferredValues1.add(new String("savings"));

		ArrayList<String> preferredValues2 = new ArrayList<String>();
		preferredValues2.add(new String("speed"));
		preferredValues2.add(new String("savings"));
		preferredValues2.add(new String("quality"));

		ArrayList<String> preferredValues3 = new ArrayList<String>();
		preferredValues3.add(new String("savings"));
		preferredValues3.add(new String("speed"));
		preferredValues3.add(new String("quality"));

		ArrayList<String> preferredValues4 = new ArrayList<String>();
		preferredValues4.add(new String("quality"));
		preferredValues4.add(new String("savings"));
		preferredValues4.add(new String("speed"));

		ArrayList<String> preferredValues5 = new ArrayList<String>();
		preferredValues5.add(new String("speed"));
		preferredValues5.add(new String("quality"));
		preferredValues5.add(new String("savings"));

		preferredValues.add(new ValPref(preferredValues0));
		preferredValues.add(new ValPref(preferredValues1));
		preferredValues.add(new ValPref(preferredValues2));
		preferredValues.add(new ValPref(preferredValues3));
		preferredValues.add(new ValPref(preferredValues4));
		preferredValues.add(new ValPref(preferredValues5));

		return preferredValues;
	}

	/**
	 * Gets an {@link ArrayList} of {@link ValPref} with void preference
	 * 
	 * @return an {@link ArrayList} of {@link ValPref}
	 */
	public static ArrayList<ValPref> getValPrefVoidList() {
		ArrayList<ValPref> preferredValues = new ArrayList<ValPref>();

		ArrayList<String> preferredValues0 = new ArrayList<String>();
		preferredValues0.add(new String(""));
		preferredValues.add(new ValPref(preferredValues0));
		return preferredValues;
	}

	/**
	 * Creates an empty {@link ArgumentCase} partitions in the specified file names
	 * 
	 * @param destFileNames
	 *            {@link ArrayList} of file names
	 */
	public static void createEmptyArgCasesPartitions(ArrayList<String> destFileNames) {

		for (int i = 0; i < destFileNames.size(); i++) {
			try {
				FileWriter fstream = new FileWriter(destFileNames.get(i), false);
				BufferedWriter outFile = new BufferedWriter(fstream);
				// Close the output stream
				outFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates a {@link SocialEntity} with the given base name and the number of
	 * operators, experts and administrators specified
	 * 
	 * @param baseName
	 *            Name to put as base of the {@link SocialEntity} create.
	 * @param nOperators
	 * @param nExperts
	 * @param nAdministrators
	 * @return {@link ArrayList} of {@link SocialEntity}
	 */
	public static ArrayList<SocialEntity> createSocialEntities(String baseName, int nOperators, int nExperts,
			int nAdministrators) {
		ArrayList<ValPref> preferredValues = AgentsCreation.getValPrefList();
		ArrayList<SocialEntity> socialEntities = new ArrayList<SocialEntity>();
		for (int i = 0; i < nOperators; i++) {
			SocialEntity socialEntity = new SocialEntity(i, baseName + "Operator" + i, "operator", null,
					preferredValues.get(1));
			socialEntities.add(socialEntity);
		}
		for (int i = 0; i < nExperts; i++) {
			SocialEntity socialEntity = new SocialEntity(i, baseName + "Expert" + i, "expert", null,
					preferredValues.get(1));
			socialEntities.add(socialEntity);
		}
		for (int i = 0; i < nAdministrators; i++) {
			SocialEntity socialEntity = new SocialEntity(i, baseName + "Administrator" + i, "administrator", null,
					preferredValues.get(1));
			socialEntities.add(socialEntity);
		}

		return socialEntities;
	}
	
	/**
	 * Creates a {@link SocialEntity} with the given base name and the number of
	 * operators, experts and administrators specified, putting the given valpref
	 * 
	 * @param baseName
	 *            Name to put as base of the {@link SocialEntity} create.
	 * @param nOperators
	 * @param nExperts
	 * @param nAdministrators
	 * @param value Main preferred value
	 * @return {@link ArrayList} of {@link SocialEntity}
	 */
	public static ArrayList<SocialEntity> createSocialEntities(String baseName, int nOperators, int nExperts,
			int nAdministrators, ArrayList<String> values) {
		ValPref valPref=new ValPref(values);
		ArrayList<SocialEntity> socialEntities = AgentsCreation.createSocialEntities(baseName, nOperators, nExperts, nAdministrators);
		Iterator<SocialEntity> iterSocEnt=socialEntities.iterator();
		while(iterSocEnt.hasNext()){
			SocialEntity soc=iterSocEnt.next();
			soc.setValPref(valPref);
		}
	
		return socialEntities;
	}

	/**
	 * Creates a {@link SocialEntity} with different value preferences, the given
	 * base name and the number of operators, experts and administrators specified
	 * 
	 * @param baseName
	 *            Name to put as base of the {@link SocialEntity} create.
	 * @param nOperators
	 * @param nExperts
	 * @param nAdministrators
	 * @return {@link ArrayList} of {@link SocialEntity}
	 */
	public static ArrayList<SocialEntity> createSocialEntitiesDifRoles(String baseName, int nAdministrators, int nExperts,
			int nOperators) {
		ArrayList<ValPref> preferredValues = AgentsCreation.getValPrefList();
		ArrayList<SocialEntity> socialEntities = new ArrayList<SocialEntity>();

		int j = 0;
		SocialEntity socialEntity;

		for (int i = j; i < nAdministrators; i++) {
			int index = (int) (Math.random() * preferredValues.size());
			if (index < 5)
				socialEntity = new SocialEntity(i, baseName + "Administrator" + i, "administrator", null,
						preferredValues.get(index));
			else
				socialEntity = new SocialEntity(i, baseName + "Administrator" + i, "administrator", null, preferredValues.get(5));

			socialEntities.add(socialEntity);
			j++;
		}

		for (int i = j; i < nExperts; i++) {
			int index = (int) (Math.random() * preferredValues.size());
			if (index < 5)
				socialEntity = new SocialEntity(i, baseName + "Expert" + i, "expert", null, preferredValues.get(index));
			else
				socialEntity = new SocialEntity(i, baseName + "Expert" + i, "expert", null, preferredValues.get(5));

			socialEntities.add(socialEntity);
			j++;
		}

		for (int i = j; i < nOperators; i++) {
			int index = (int) (Math.random() * preferredValues.size());
			if (index < 5)
				socialEntity = new SocialEntity(i, baseName + "Operator" + i, "operator", null,
						preferredValues.get(index));
			else
				socialEntity = new SocialEntity(i, baseName + "Operator" + i, "operator", null, preferredValues.get(5));

			socialEntities.add(socialEntity);
			j++;
		}

		return socialEntities;
	}

	/**
	 * Creates a {@link SocialEntity} with the given list of preferences, base
	 * name and the number of operators, experts and administrators specified
	 * 
	 * @param baseName
	 *            Name to put as base of the {@link SocialEntity} create.
	 * @param nOperators
	 * @param nExperts
	 * @param nAdministrators
	 * @return {@link ArrayList} of {@link SocialEntity}
	 */
	public static ArrayList<SocialEntity> createSocialEntitiesDifRoles(int valList, String baseName, int nAdministrators,
			int nExperts, int nOperators) {
		ArrayList<ValPref> preferredValues = AgentsCreation.getValPrefList();
		ArrayList<SocialEntity> socialEntities = new ArrayList<SocialEntity>();

		int j = 0;
		SocialEntity socialEntity;

		for (int i = j; i < nAdministrators; i++) {
			socialEntity = new SocialEntity(i, baseName + "Administrator" + i, "administrator", null, preferredValues.get(valList));
			socialEntities.add(socialEntity);
			j++;
		}

		for (int i = j; i < nExperts; i++) {
			socialEntity = new SocialEntity(i, baseName + "Expert" + i, "expert", null, preferredValues.get(valList));
			socialEntities.add(socialEntity);
			j++;
		}

		for (int i = j; i < nOperators; i++) {
			socialEntity = new SocialEntity(i, baseName + "Operator" + i, "operator", null,
					preferredValues.get(valList));
			socialEntities.add(socialEntity);
			j++;
		}

		return socialEntities;
	}

	/**
	 * Creates a {@link SocialEntity} with void values, the given base name and
	 * the number of operators, experts and administrators specified
	 * 
	 * @param baseName
	 *            Name to put as base of the {@link SocialEntity} create.
	 * @param nOperators
	 * @param nExperts
	 * @param nAdministrators
	 * @return {@link ArrayList} of {@link SocialEntity}
	 */
	public static ArrayList<SocialEntity> createSocialEntitiesDifRolesVoidValues(String baseName, int nAdministrators,
			int nExperts, int nOperators) {
		ArrayList<ValPref> preferredValues = AgentsCreation.getValPrefList();
		ArrayList<ValPref> preferredValuesVoid = AgentsCreation.getValPrefVoidList();
		ArrayList<SocialEntity> socialEntities = new ArrayList<SocialEntity>();

		int j = 0;
		SocialEntity socialEntity;

		for (int i = j; i < nAdministrators; i++) {
			int index = (int) (Math.random() * preferredValues.size());

			if (i == 0 && index < 5)
				socialEntity = new SocialEntity(i, baseName + "Administrator" + i, "administrator", null,
						preferredValues.get(index));
			else if (i == 0)
				socialEntity = new SocialEntity(i, baseName + "Administrator" + i, "administrator", null, preferredValues.get(5));
			else
				socialEntity = new SocialEntity(i, baseName + "Administrator" + i, "administrator", null,
						preferredValuesVoid.get(0));

			socialEntities.add(socialEntity);
			j++;
		}

		for (int i = j; i < nExperts; i++) {
			int index = (int) (Math.random() * preferredValues.size());

			if (i == 0 && index < 5)
				socialEntity = new SocialEntity(i, baseName + "Expert" + i, "expert", null, preferredValues.get(index));
			else if (i == 0)
				socialEntity = new SocialEntity(i, baseName + "Expert" + i, "expert", null, preferredValues.get(5));
			else
				socialEntity = new SocialEntity(i, baseName + "Expert" + i, "expert", null, preferredValuesVoid.get(0));

			socialEntities.add(socialEntity);
			j++;
		}

		for (int i = j; i < nOperators; i++) {
			int index = (int) (Math.random() * preferredValues.size());

			if (i == 0 && index < 5)
				socialEntity = new SocialEntity(i, baseName + "Operator" + i, "operator", null,
						preferredValues.get(index));
			else if (i == 0)
				socialEntity = new SocialEntity(i, baseName + "Operator" + i, "operator", null, preferredValues.get(5));
			else
				socialEntity = new SocialEntity(i, baseName + "Operator" + i, "operator", null,
						preferredValuesVoid.get(0));

			socialEntities.add(socialEntity);
			j++;
		}

		return socialEntities;
	}

	/**
	 * Creates an {@link ArrayList} for each {@link SocialEntity} given in the
	 * parameters. Each {@link ArrayList} represents a list of friends for each agent
	 * 
	 * @param socialEntities
	 *            {@link ArrayList} of {@link SocialEntity} to create the lists.
	 * @return {@link ArrayList} of {@link ArrayList} of {@link SocialEntity}
	 */
	public static ArrayList<ArrayList<SocialEntity>> createFriendsLists(ArrayList<SocialEntity> socialEntities) {
		int totalAgents = socialEntities.size();
		ArrayList<ArrayList<SocialEntity>> friendsLists = new ArrayList<ArrayList<SocialEntity>>();
		for (int i = 0; i < totalAgents; i++) {
			ArrayList<SocialEntity> friends = new ArrayList<SocialEntity>();
			for (int j = 0; j < totalAgents; j++) {
				if (i != j)
					friends.add(socialEntities.get(j));
			}
			friendsLists.add(friends);
		}

		return friendsLists;
	}

	/**
	 * Creates an {@link ArrayList} with {@link ArrayList} of
	 * {@link DependencyRelation} for each operator, expert and administrator
	 * specified.
	 * 
	 * @param nOperators
	 * @param nExperts
	 * @param nAdministrators
	 * @return an {@link ArrayList} with {@link ArrayList} of
	 *         {@link DependencyRelation}
	 */
	public static ArrayList<ArrayList<DependencyRelation>> createDependencyRelations(int nOperators, int nExperts,
			int nAdministrators) {
		ArrayList<ArrayList<DependencyRelation>> dependencyLists = new ArrayList<ArrayList<DependencyRelation>>();

		for (int i = 0; i < nOperators; i++) {
			ArrayList<DependencyRelation> operatorDepenRels = new ArrayList<DependencyRelation>();
			for (int j = 0; j < nOperators - 1; j++) {
				operatorDepenRels.add(DependencyRelation.CHARITY);
			}
			for (int j = 0; j < nExperts; j++) {
				operatorDepenRels.add(DependencyRelation.CHARITY);
			}
			for (int j = 0; j < nAdministrators; j++) {
				operatorDepenRels.add(DependencyRelation.CHARITY);
			}

			dependencyLists.add(operatorDepenRels);
		}

		for (int i = 0; i < nExperts; i++) {
			ArrayList<DependencyRelation> expertDepenRels = new ArrayList<DependencyRelation>();
			for (int j = 0; j < nOperators; j++) {
				expertDepenRels.add(DependencyRelation.AUTHORISATION);
			}
			for (int j = 0; j < nExperts - 1; j++) {
				expertDepenRels.add(DependencyRelation.CHARITY);
			}
			for (int j = 0; j < nAdministrators; j++) {
				expertDepenRels.add(DependencyRelation.CHARITY);
			}

			dependencyLists.add(expertDepenRels);
		}

		for (int i = 0; i < nAdministrators; i++) {
			ArrayList<DependencyRelation> managerDepenRels = new ArrayList<DependencyRelation>();
			for (int j = 0; j < nOperators; j++) {
				managerDepenRels.add(DependencyRelation.POWER);
			}
			for (int j = 0; j < nExperts; j++) {
				managerDepenRels.add(DependencyRelation.POWER);
			}
			for (int j = 0; j < nAdministrators - 1; j++) {
				managerDepenRels.add(DependencyRelation.CHARITY);
			}

			dependencyLists.add(managerDepenRels);
		}

		return dependencyLists;
	}

	/**
	 * Creates an {@link ArrayList} with {@link ArrayList} of
	 * {@link DependencyRelation} for each administrator, expert and operator
	 * specified.
	 * 
	 * @param nOperators
	 * @param nExperts
	 * @param nAdministrators
	 * @return an {@link ArrayList} with {@link ArrayList} of
	 *         {@link DependencyRelation}
	 */
	public static ArrayList<ArrayList<DependencyRelation>> createDependencyRelationsDifRoles(int nAdministrators,
			int nExperts, int nOperators) {
		ArrayList<ArrayList<DependencyRelation>> dependencyLists = new ArrayList<ArrayList<DependencyRelation>>();

		for (int i = 0; i < nAdministrators; i++) {
			ArrayList<DependencyRelation> managerDepenRels = new ArrayList<DependencyRelation>();
			for (int j = 0; j < nOperators; j++) {
				managerDepenRels.add(DependencyRelation.POWER);
			}
			for (int j = 0; j < nExperts; j++) {
				managerDepenRels.add(DependencyRelation.POWER);
			}
			for (int j = 0; j < nAdministrators - 1; j++) {
				managerDepenRels.add(DependencyRelation.CHARITY);
			}

			dependencyLists.add(managerDepenRels);
		}

		for (int i = 0; i < nExperts; i++) {
			ArrayList<DependencyRelation> expertDepenRels = new ArrayList<DependencyRelation>();
			for (int j = 0; j < nOperators; j++) {
				expertDepenRels.add(DependencyRelation.AUTHORISATION);
			}
			for (int j = 0; j < nExperts - 1; j++) {
				expertDepenRels.add(DependencyRelation.CHARITY);
			}
			for (int j = 0; j < nAdministrators; j++) {
				expertDepenRels.add(DependencyRelation.CHARITY);
			}

			dependencyLists.add(expertDepenRels);
		}

		for (int i = 0; i < nOperators; i++) {
			ArrayList<DependencyRelation> operatorDepenRels = new ArrayList<DependencyRelation>();
			for (int j = 0; j < nOperators - 1; j++) {
				operatorDepenRels.add(DependencyRelation.CHARITY);
			}
			for (int j = 0; j < nExperts; j++) {
				operatorDepenRels.add(DependencyRelation.CHARITY);
			}
			for (int j = 0; j < nAdministrators; j++) {
				operatorDepenRels.add(DependencyRelation.CHARITY);
			}

			dependencyLists.add(operatorDepenRels);
		}

		return dependencyLists;
	}
	
	public static ArrayList<ArrayList<DependencyRelation>> createDependencyRelations(ArrayList<SocialEntity> socialEntities) {
		ArrayList<ArrayList<DependencyRelation>> dependencies=new ArrayList<ArrayList<DependencyRelation>>();
		
		Iterator<SocialEntity> iterSoc=socialEntities.iterator();
		int currentSoc=0;
		while(iterSoc.hasNext()){
			SocialEntity soc=iterSoc.next();
			ArrayList<DependencyRelation> deps=new ArrayList<DependencyRelation>();
			for(int i=0;i<socialEntities.size();i++){
				if(currentSoc==i)
					continue;
				if(soc.getRole().equalsIgnoreCase("operator") || soc.getRole().equalsIgnoreCase(socialEntities.get(i).getRole())){
					deps.add(DependencyRelation.CHARITY);
				}
				else if(socialEntities.get(i).getRole().equalsIgnoreCase("operator")){
					if(soc.getRole().equalsIgnoreCase("expert")){
						deps.add(DependencyRelation.AUTHORISATION);
					}
					else{
						deps.add(DependencyRelation.POWER);
					}
				}
				else if(socialEntities.get(i).getRole().equalsIgnoreCase("expert")){
					deps.add(DependencyRelation.POWER);
				}
				else if(socialEntities.get(i).getRole().equalsIgnoreCase("administrator")){
					deps.add(DependencyRelation.CHARITY);
				}
				else 
					deps.add(DependencyRelation.CHARITY); //should not happen...
				
			}
			dependencies.add(deps);
			currentSoc++;
			
		}
		
		
		return dependencies;
	}

	/**
	 * Creates an {@link ArrayList} of {@link ArgCAgent} with the specified
	 * {@link SocialEntity}
	 * 
	 * @param socialEntities
	 *            {@link ArrayList} of {@link SocialEntity} that defines the
	 *            agents to create
	 * @param friendsLists
	 *            {@link ArrayList} of {@link ArrayList} of {@link SocialEntity}
	 *            with all the agents of the group
	 * @param dependencyRels
	 *            dependency relations between all the agents
	 * @param group
	 *            of the agents
	 * @param iniDomainFileNames
	 *            file names of the initial domain-cases case-bases
	 * @param finDomainFileNames
	 *            file names to store the final domain-cases case-bases
	 * @param domCBRindex
	 *            index of the domain CBR to distribute efficiently the
	 *            domain-cases in the hash table. -1 if there is no index
	 * @param domCBRthreshold
	 *            minimum threshold of similarity to take into account a
	 *            domain-case in the domain CBR as a possible solution
	 * @param iniArgFileNames
	 *            file names of the initial argument-cases case-bases
	 * @param finArgFileNames
	 *            file names to store the final argument-cases case-bases
	 * @param testerAgentID
	 *            identifier of the tester agent
	 * @param wPD
	 *            Weight of the Persuasion Degree
	 * @param wSD
	 *            Weight of the Support Degree
	 * @param wRD
	 *            Weight of the Risk Degree
	 * @param wAD
	 *            Weight of the Attack Degree
	 * @param wED
	 *            Weight of the Efficiency Degree
	 * @param wEP
	 *            Weight of the Explanatory Power
	 * @return list of ArgCAgents created with the given parameters
	 */
	public static ArrayList<ArgCAgent> createArgCAgentsInc(ArrayList<SocialEntity> socialEntities,
			ArrayList<ArrayList<SocialEntity>> friendsLists, ArrayList<ArrayList<DependencyRelation>> dependencyRels,
			Group group, ArrayList<String> iniDomainFileNames, ArrayList<String> finDomainFileNames, int domCBRindex,
			float domCBRthreshold, ArrayList<String> iniArgFileNames, ArrayList<String> finArgFileNames,
			String testerAgentID, float wPD, float wSD, float wRD, float wAD, float wED, float wEP) {

		ArrayList<ArgCAgent> agents = new ArrayList<ArgCAgent>();

		int totalAgents = socialEntities.size();

		try {

			for (int i = 0; i < totalAgents; i++) {
				ArgCAgent argCAgent = new ArgCAgent(new AgentID("qpid://" + socialEntities.get(i).getName()
						+ "@localhost:8080"), socialEntities.get(i), friendsLists.get(i), dependencyRels.get(i), group,
						"commitmentStore", null, iniDomainFileNames.get(i), finDomainFileNames.get(i), domCBRindex,
						domCBRthreshold, iniArgFileNames.get(i), finArgFileNames.get(i), wPD, wSD, wRD, wAD,
						wED, wEP);
				argCAgent.start();
				agents.add(argCAgent);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return agents;

	}

}
