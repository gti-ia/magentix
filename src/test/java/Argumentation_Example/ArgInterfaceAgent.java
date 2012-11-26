package Argumentation_Example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import Argumentation_Example.InterfaceStructs.InJSONObject;
import Argumentation_Example.InterfaceStructs.OutJSONObject;
import Argumentation_Example.InterfaceStructs.Technician;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

import es.upv.dsic.gti_ia.argAgents.CommitmentStore;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainContext;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Group;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Justification;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Premise;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Problem;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialContext.DependencyRelation;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialEntity;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Solution;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ValPref;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class ArgInterfaceAgent extends SingleAgent {

	public ArgInterfaceAgent(AgentID aid) throws Exception {
		super(aid);

	}

	private ArrayList<Technician> parseTechnicians(String techsStr) {
		ArrayList<Technician> technicians = new ArrayList<Technician>();

		StringTokenizer techsTok = new StringTokenizer(techsStr, "@@@");
		while (techsTok.hasMoreTokens()) {
			StringTokenizer aTechTok = new StringTokenizer(techsTok.nextToken(), "##");
			InterfaceStructs is = new InterfaceStructs();
			Technician t = is.new Technician(aTechTok.nextToken(), aTechTok.nextToken(), aTechTok.nextToken(),
					aTechTok.nextToken(), aTechTok.nextToken(), aTechTok.nextToken(), Integer.parseInt(aTechTok
							.nextToken()), Integer.parseInt(aTechTok.nextToken()), Integer.parseInt(aTechTok
							.nextToken()), Integer.parseInt(aTechTok.nextToken()), Integer.parseInt(aTechTok
							.nextToken()), Integer.parseInt(aTechTok.nextToken()));
			technicians.add(t);
			System.out.println(t.toString());
		}

		return technicians;
	}

	private OutJSONObject solveProblem(InJSONObject query) throws InterruptedException {
		// parse problem
		String problem = query.content.problem; // "0::22||26::yes||27::yes||28::yes||65::yes";
		DomainCase problemToSolve = new DomainCase(null, new ArrayList<Solution>(), new Justification());
		HashMap<Integer, Premise> premises = new HashMap<Integer, Premise>();
		StringTokenizer probTok = new StringTokenizer(problem, "||");
		while (probTok.hasMoreTokens()) {
			String quesTok = probTok.nextToken().trim();
			StringTokenizer quesPartsTok = new StringTokenizer(quesTok, "::");
			String id = quesPartsTok.nextToken();
			String answer = quesPartsTok.nextToken();
			premises.put(Integer.parseInt(id), new Premise(Integer.parseInt(id), "", answer));
		}
		problemToSolve.setProblem(new Problem(new DomainContext(premises)));

		InterfaceStructs is = new InterfaceStructs();

		ArrayList<Technician> technicians = parseTechnicians(query.content.technicians);

		OutJSONObject outJsonObject;
		if (technicians != null && !technicians.isEmpty()) {

			ArrayList<SocialEntity> socialEntities = new ArrayList<SocialEntity>();
			HashMap<String,Group> groups = new HashMap<String,Group>();
			ArrayList<String> iniArgFileNames = new ArrayList<String>();
			ArrayList<String> finArgFileNames = new ArrayList<String>();
			String commitmentStoreID = "commitmentStore";

			Iterator<Technician> iterTechs = technicians.iterator();
			int currentTechSet = 0;
			while (iterTechs.hasNext()) {
				Technician tech = iterTechs.next();
				for (int i = 0; i < Integer.parseInt(tech.getQuantity()); i++) {
					String argFile = "testArgumentation/partArgInc/partArg" + tech.getBaseID() + i + ".dat";
					iniArgFileNames.add(argFile);
					finArgFileNames.add(argFile);
				}
				int nOperators = 0, nExperts = 0, nAdministrators = 0;
				if (tech.getRole().equalsIgnoreCase("operator")) {
					nOperators = Integer.parseInt(tech.getQuantity());
				} else if (tech.getRole().equalsIgnoreCase("expert")) {
					nExperts = Integer.parseInt(tech.getQuantity());
				} else if (tech.getRole().equalsIgnoreCase("administrator")) {
					nAdministrators = Integer.parseInt(tech.getQuantity());
				}
				// TODO change to the real value, tindre en compte que l'agent
				// només tria les posicions que promouen un dels seus valors
				// tech.getDecisionStrategy();
				ArrayList<String> values = new ArrayList<String>();
				values.add("Speed");
				values.add("Quality");
				values.add("Savings");
				ArrayList<SocialEntity> socEnt = AgentsCreation.createSocialEntities(tech.getBaseID(), nOperators,
						nExperts, nAdministrators, values);
				
				
				
				Group g=groups.get(tech.getGroup());
				if(g!=null){
					ArrayList<SocialEntity> members = g.getMembers();
					members.addAll(socEnt);
					g.setMembers(members);
				}
				else{
					// TODO change to the real value of the group from the interface
					Group group = new Group(currentTechSet, tech.getGroup(), new ValPref(values), socEnt);
					groups.put(tech.getGroup(), group);
				}
				

				socialEntities.addAll(socEnt);

				currentTechSet++;
			}

			ArrayList<ArrayList<SocialEntity>> friendLists = AgentsCreation.createFriendsLists(socialEntities);
			ArrayList<ArrayList<DependencyRelation>> dependencyRels = AgentsCreation
					.createDependencyRelations(socialEntities);
			AgentsCreation.createEmptyArgCasesPartitions(iniArgFileNames);
			AgentsCreation.createEmptyArgCasesPartitions(finArgFileNames);
			ArrayList<ArgCAgent> agents = new ArrayList<ArgCAgent>();

			iterTechs = technicians.iterator();
			// System.out.println("technicians.size():"+technicians.size());
			currentTechSet = 0;
			int currentTech = 0;
			while (iterTechs.hasNext()) {
				Technician tech = iterTechs.next();

				for (int i = 0; i < Integer.parseInt(tech.getQuantity()); i++) {

					// System.out.println("currentTech: "+currentTech+" "+socialEntities.get(currentTech).getName());
					ArgCAgent argCAgent;
					try {
						argCAgent = new ArgCAgent(new AgentID("qpid://" + socialEntities.get(currentTech).getName()
								+ "@localhost:8080"), socialEntities.get(currentTech), friendLists.get(currentTech),
								dependencyRels.get(currentTech), groups.get(tech.getGroup()), commitmentStoreID, null,
								tech.getInitialDataPath(), tech.getInitialDataPath(), 0, 0.25f,
								iniArgFileNames.get(currentTech), finArgFileNames.get(currentTech),
								tech.getPersuasiveness() * .01f, tech.getSupport() * .01f, tech.getRisk() * .01f,
								tech.getAttack() * .01f, tech.getEfficiency() * .01f, tech.getExplanatoryPower() * .01f);

						argCAgent.start();
						agents.add(argCAgent);

					} catch (Exception e) {

						e.printStackTrace();
					}
					currentTech++;

				}

				currentTechSet++;
			}

			String testerAgentID = "testerAgent";
			// fileName to write if the test has finished
			String finishFileName = "testArgumentation/testArgCAgentInterfacefinished";

			// Create and start the Commitment Store
			CommitmentStore commitmentStore = null;
			try {
				commitmentStore = new CommitmentStore(new AgentID("qpid://" + commitmentStoreID + "@localhost:8080"));
				commitmentStore.start();
			} catch (Exception e) {

				e.printStackTrace();
			}

			// Create the argumentative agents
			// ArrayList<ArgCAgent> agents =
			// AgentsCreation.createArgCAgentsInc(socialEntities,
			// friendsLists, depenRelsLists, group, iniDomainFiles,
			// iniDomainFiles, 0, 0.5f,
			// iniArgFileNames, iniArgFileNames, testerAgentID, 1f, 1f, 1f, 1f,
			// 1f, 1f);

			Vector<DomainCase> domCasesVector = new Vector<DomainCase>();
			domCasesVector.add(problemToSolve);
			// Create the tester agent that sends the test domain-case to solve
			// to the group of agents
			// and acts as initiator of the dialogue
			TesterArgCAgentInterface testerAgent;
			try {
				testerAgent = new TesterArgCAgentInterface(new AgentID("qpid://" + testerAgentID + "@localhost:8080"),
						socialEntities, commitmentStore.getName(), "ArgInterfaceAgent",
						"testArgumentation/results/argTestResInterface" + agents.size() + "ag.txt", finishFileName, 5,
						0, domCasesVector, agents);
				testerAgent.start();
			} catch (Exception e) {

				e.printStackTrace();
			}

			ACLMessage solMsg = receiveACLMessage();
			// String solution="";
			outJsonObject = is.new OutJSONObject();
			outJsonObject.result.problem = query.content.problem;
			outJsonObject.result.technicians = query.content.technicians;

			if (solMsg.getHeaderValue("locution").equalsIgnoreCase("SOLUTION")) {
				Solution sol = (Solution) solMsg.getContentObject();
				outJsonObject.result.promotedValue = sol.getPromotesValue();
				outJsonObject.result.solution = sol.getConclusion().getDescription().replaceAll("_", " ");
				outJsonObject.result.traceID = solMsg.getConversationId();
				outJsonObject.result.dateMillis = String.valueOf(System.currentTimeMillis());
				Iterator<SocialEntity> iterSocE = socialEntities.iterator();
				outJsonObject.result.techniciansIDs = "";
				while (iterSocE.hasNext()) {
					outJsonObject.result.techniciansIDs += iterSocE.next().getName() + "@@@";
				}
				
				//*********************
				outJsonObject.result.traceID = getTraceContent(solMsg.getConversationId(), outJsonObject.result.techniciansIDs);
				
				//*************
				
				// solution="SOLUTION: "+sol.getConclusion().getID()+" "+sol.getConclusion().getDescription()+
				// "\n\tPromotesValue: "+sol.getPromotesValue();
				// System.out.println(getName()+": "+solution);
			} else {
				System.err.println("Incorrect Message");
				// solution="Incorrect Message";
			}

		} else { // incorrect technicians
			outJsonObject = is.new OutJSONObject();
			outJsonObject.result.problem = query.content.problem;
			outJsonObject.result.technicians = query.content.technicians;
			outJsonObject.result.error = "Incorrect technicians configuration";
		}

		return outJsonObject;
	}

	private String getTraceContent(String traceID, String techniciansIDsStr) {
		String trace = "";

		StringTokenizer techsTok = new StringTokenizer(techniciansIDsStr, "@@@");
		while (techsTok.hasMoreTokens()) {
			String aTech = techsTok.nextToken();

			try {

				FileReader fstream = new FileReader("testArgumentation/tracesArg" + traceID + "-" + aTech);
				BufferedReader reader = new BufferedReader(fstream);
				String line = reader.readLine();
				
				while (line != null) {
					trace += line + "###\n";
					
					line = reader.readLine();
				}
				// if(lineCount==0)
				// trace+=aTech; //if it has not participated, we only add its
				// name to the trace
				trace += "@@@\n";

				reader.close();
			} catch (Exception e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}

		}

		return trace;
	}
	
	private OutJSONObject getTrace(InJSONObject query) {
		InterfaceStructs is = new InterfaceStructs();
		OutJSONObject outJsonObject = is.new OutJSONObject();

		String traceID = query.content.traceID;
		String techniciansIDsStr = query.content.techniciansIDs;

		outJsonObject.result.type = "trace";
		String trace = "";

		StringTokenizer techsTok = new StringTokenizer(techniciansIDsStr, "@@@");
		while (techsTok.hasMoreTokens()) {
			String aTech = techsTok.nextToken();

			try {

				FileReader fstream = new FileReader("testArgumentation/tracesArg/" + traceID + "-" + aTech);
				BufferedReader reader = new BufferedReader(fstream);
				String line = reader.readLine();
				
				while (line != null) {
					trace += line + "###\n";
					line = reader.readLine();
				}
				// if(lineCount==0)
				// trace+=aTech; //if it has not participated, we only add its
				// name to the trace
				trace += "@@@\n";

				reader.close();
			} catch (Exception e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}

		}

		outJsonObject.result.trace = trace;

		return outJsonObject;
	}

	public void execute() {
		/**
		 * Wait for messages, forever
		 */
		while (true) {
			try {
				ACLMessage msg = receiveACLMessage(); // receive the message
														// with the query from
														// the web page
				// Transform the input data into a Java object
				XStream xstream = new XStream(new JettisonMappedXmlDriver());
				xstream.alias("jsonObject", InJSONObject.class);

				System.out.println(getName() + ": message Content:\n" + msg.getContent());
				// xstream.setClassLoader(InJSONObject.class.getClassLoader());
				xstream.processAnnotations(InJSONObject.class);
				InJSONObject query = (InJSONObject) xstream.fromXML(msg.getContent());

				InterfaceStructs is = new InterfaceStructs();
				OutJSONObject outJsonObject;

				if (query.content.type.equalsIgnoreCase("problem")) {
					outJsonObject = solveProblem(query);
				} else if (query.content.type.equalsIgnoreCase("trace")) {
					outJsonObject = getTrace(query);
				} else {
					outJsonObject = is.new OutJSONObject();
				}

				// We prepare the response message
				ACLMessage response = new ACLMessage(ACLMessage.INFORM);
				response.setSender(getAid());
				response.setReceiver(msg.getSender());

				// Transform the product Java object into a JSON object
				XStream xstream2 = new XStream(new JsonHierarchicalStreamDriver() {
					@Override
					public HierarchicalStreamWriter createWriter(Writer writer) {
						return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
					}
				});
				String result = xstream2.toXML(outJsonObject);
				response.setContent(result);

				logger.info(this.getName() + ": result to send= " + result);
				System.out.println(this.getName() + ": result to send= " + result);

				// send the response message
				this.send(response);

			} catch (InterruptedException e) {
				logger.error(e.getMessage());
				System.out.println(e.getMessage());
				return;
			}
		}
	}

}
