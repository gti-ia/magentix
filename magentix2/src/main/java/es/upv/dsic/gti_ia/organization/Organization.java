package es.upv.dsic.gti_ia.organization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Organization {

	private Document doc;

	public int roleIndex = 0;
	public int serviceIndex = 0;
	public int agentIndex = 0;
	public int goalIndex = 0;

	public List<String> roles = new ArrayList<String>();
	public List<String> services = new ArrayList<String>();
	public List<String> agents = new ArrayList<String>();
	public List<String> goals = new ArrayList<String>();
	// fer hashtables amb la resta de coses
	public Hashtable<String, Integer[]> provider = new Hashtable<String, Integer[]>();
	public Hashtable<String, Integer[]> provides = new Hashtable<String, Integer[]>();
	public Hashtable<String, Integer[]> plays = new Hashtable<String, Integer[]>();
	public Hashtable<String, Integer[]> pursues = new Hashtable<String, Integer[]>();

	public Hashtable<String, Integer> roleStructure = new Hashtable<String, Integer>();

	public Hashtable<String, Integer[]> costMatrix = new Hashtable<String, Integer[]>();
	public Hashtable<String, Integer> agentAssignment = new Hashtable<String, Integer>();
	public Hashtable<String, Integer> roleAssignment = new Hashtable<String, Integer>();

	public Hashtable<String, Integer> newRoleStructure = new Hashtable<String, Integer>();

	public Organization() {
	}

	public Organization(File file) {
		try {
			Document document = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			document = db.parse(file);
			this.createOrganizationFromXML(document);
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public Organization(String orgSpecification) {
		try{
			Document document = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			InputSource s = new InputSource(new StringReader(orgSpecification)); 
			document = db.parse(s);
			this.createOrganizationFromXML(document);
		}
		catch(Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}

	private void createOrganizationFromXML(Document document) {
		try {
			this.doc = document;
			this.doc.getDocumentElement().normalize();

			// roles
			NodeList rolesList = doc.getElementsByTagName("roles");
			Node roles = rolesList.item(0);
			Element rolesElement = (Element) roles;
			NodeList roleList = rolesElement.getElementsByTagName("role");
			for (int s = 0; s < roleList.getLength(); s++) {
				Node role = roleList.item(s);
				if (role.getNodeType() == Node.ELEMENT_NODE) {
					NodeList childList = role.getChildNodes();
					this.roles.add(childList.item(0).getNodeValue());
					this.roleIndex++;
				}
			}

			// services
			NodeList servicesList = doc.getElementsByTagName("services");
			Node services = servicesList.item(0);
			Element servicesElement = (Element) services;
			NodeList serviceList = servicesElement
					.getElementsByTagName("service");

			for (int s = 0; s < serviceList.getLength(); s++) {
				Node service = serviceList.item(s);
				if (service.getNodeType() == Node.ELEMENT_NODE) {
					NodeList childList = service.getChildNodes();
					this.services.add(childList.item(0).getNodeValue());
					this.serviceIndex++;
				}
			}

			// agents
			NodeList agentsList = doc.getElementsByTagName("agents");
			Node agents = agentsList.item(0);
			Element agentsElement = (Element) agents;
			NodeList agentList = agentsElement.getElementsByTagName("agent");

			for (int s = 0; s < agentList.getLength(); s++) {
				Node agent = agentList.item(s);
				if (agent.getNodeType() == Node.ELEMENT_NODE) {
					NodeList childList = agent.getChildNodes();
					this.agents.add(childList.item(0).getNodeValue());
					this.agentIndex++;
				}
			}

			// goals
			NodeList goalsList = doc.getElementsByTagName("goals");
			Node goals = goalsList.item(0);
			Element goalsElement = (Element) goals;
			NodeList goalList = goalsElement.getElementsByTagName("goal");

			for (int s = 0; s < goalList.getLength(); s++) {
				Node goal = goalList.item(s);
				if (goal.getNodeType() == Node.ELEMENT_NODE) {
					NodeList childList = goal.getChildNodes();
					this.goals.add(childList.item(0).getNodeValue());
					this.goalIndex++;
				}
			}

			// provider
			NodeList providersList = doc.getElementsByTagName("providers");
			Node providers = providersList.item(0);
			Element providersElement = (Element) providers;
			NodeList providerList = providersElement
					.getElementsByTagName("provider");

			for (int s = 0; s < providerList.getLength(); s++) {
				Node provider = providerList.item(s);
				if (provider.getNodeType() == Node.ELEMENT_NODE) {
					NodeList childList = provider.getChildNodes();
					String[] provideraux = childList.item(0).getNodeValue()
							.split(" ");
					this.provider.put(this.roles.get(s),
							new Integer[provideraux.length]);
					for (int j = 0; j < provideraux.length; j++) {
						this.provider.get(this.roles.get(s))[j] = Integer
								.valueOf(provideraux[j]);
					}
				}
			}

			// provides
			NodeList providingList = doc.getElementsByTagName("providing");
			Node providing = providingList.item(0);
			Element providingElement = (Element) providing;
			NodeList providesList = providingElement
					.getElementsByTagName("provides");

			for (int s = 0; s < providesList.getLength(); s++) {
				Node provides = providesList.item(s);
				if (provides.getNodeType() == Node.ELEMENT_NODE) {
					NodeList childList = provides.getChildNodes();
					String[] providesaux = childList.item(0).getNodeValue()
							.split(" ");
					this.provides.put(this.agents.get(s),
							new Integer[providesaux.length]);
					for (int j = 0; j < providesaux.length; j++) {
						this.provides.get(this.agents.get(s))[j] = Integer
								.valueOf(providesaux[j]);
					}
				}
			}

			// plays
			NodeList playingList = doc.getElementsByTagName("playing");
			Node playing = playingList.item(0);
			Element playingElement = (Element) playing;
			NodeList playsList = playingElement.getElementsByTagName("plays");

			for (int s = 0; s < playsList.getLength(); s++) {
				Node plays = playsList.item(s);
				if (plays.getNodeType() == Node.ELEMENT_NODE) {
					NodeList childList = plays.getChildNodes();
					String[] playsaux = childList.item(0).getNodeValue()
							.split(" ");
					this.plays.put(this.agents.get(s),
							new Integer[playsaux.length]);
					for (int j = 0; j < playsaux.length; j++) {
						this.plays.get(this.agents.get(s))[j] = Integer
								.valueOf(playsaux[j]);
					}
				}
			}

			// pursues
			NodeList pursuingList = doc.getElementsByTagName("pursuing");
			Node pursuing = pursuingList.item(0);
			Element pursuingElement = (Element) pursuing;
			NodeList pursuesList = pursuingElement
					.getElementsByTagName("pursues");

			for (int s = 0; s < pursuesList.getLength(); s++) {
				Node pursues = pursuesList.item(s);
				if (pursues.getNodeType() == Node.ELEMENT_NODE) {
					NodeList childList = pursues.getChildNodes();
					String[] pursuessaux = childList.item(0).getNodeValue()
							.split(" ");
					this.pursues.put(this.goals.get(s),
							new Integer[pursuessaux.length]);
					for (int j = 0; j < pursuessaux.length; j++) {
						this.pursues.get(this.goals.get(s))[j] = Integer
								.valueOf(pursuessaux[j]);
					}
				}
			}

			// current rol structure
			NodeList roleStructure = doc.getElementsByTagName("rolstructure");
			Node structure = roleStructure.item(0);
			Element structureElement = (Element) structure;
			String[] structureaux = structureElement.getFirstChild()
					.getNodeValue().split(" ");
			for (int j = 0; j < structureaux.length; j++) {
				this.roleStructure.put(this.roles.get(j),
						Integer.valueOf(structureaux[j]));
			}

			// cost matrix
			NodeList costMatrixList = doc.getElementsByTagName("costmatrix");
			Node costMatrix = costMatrixList.item(0);
			Element costMatrixElement = (Element) costMatrix;
			NodeList costList = costMatrixElement.getElementsByTagName("cost");

			for (int s = 0; s < costList.getLength(); s++) {
				Node cost = costList.item(s);
				if (cost.getNodeType() == Node.ELEMENT_NODE) {
					NodeList childList = cost.getChildNodes();
					String[] costaux = childList.item(0).getNodeValue()
							.split(" ");
					this.costMatrix.put(this.agents.get(s),
							new Integer[costaux.length]);
					for (int j = 0; j < costaux.length; j++) {
						this.costMatrix.get(this.agents.get(s))[j] = Integer
								.valueOf(costaux[j]);
					}
				}
			}

			// agent assignment
			NodeList agentassignmentStructure = doc
					.getElementsByTagName("agentassignment");
			Node agentassignment = agentassignmentStructure.item(0);
			Element agentassignmentElement = (Element) agentassignment;
			String[] agentassignmentaux = agentassignmentElement
					.getFirstChild().getNodeValue().split(" ");
			for (int j = 0; j < agentassignmentaux.length; j++) {
				this.agentAssignment.put(this.agents.get(j),
						Integer.valueOf(agentassignmentaux[j]));
			}

			// role assignment
			NodeList roleassignmentStructure = doc
					.getElementsByTagName("roleassignment");
			Node roleassignment = roleassignmentStructure.item(0);
			Element roleassignmentElement = (Element) roleassignment;
			String[] roleassignmentaux = roleassignmentElement.getFirstChild()
					.getNodeValue().split(" ");
			for (int j = 0; j < roleassignmentaux.length; j++) {
				this.roleAssignment.put(this.roles.get(j),
						Integer.valueOf(roleassignmentaux[j]));
			}

			// new role structure
			NodeList newroleassignmentStructure = doc
					.getElementsByTagName("newrolestructure");
			Node newroleassignment = newroleassignmentStructure.item(0);
			Element newroleassignmentElement = (Element) newroleassignment;
			String[] newroleassignmentaux = newroleassignmentElement
					.getFirstChild().getNodeValue().split(" ");
			for (int j = 0; j < newroleassignmentaux.length; j++) {
				this.newRoleStructure.put(this.roles.get(j),
						Integer.valueOf(newroleassignmentaux[j]));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getOrganizationXML(){
		String res="<?xml version=\"1.0\"?>\n";
		res += "<organization>\n";
		res += "<roles>\n";
		for (int s = 0; s < this.roles.size(); s++) {
			res += "<role>"+this.roles.get(s)+"</role>\n";
		}
		res += "</roles>\n";

		res += "<services>\n";
		for (int s = 0; s < this.services.size(); s++) {
			res += "<service>"+this.services.get(s)+"</service>\n";
		}
		res += "</services>\n";

		res += "<agents>\n";
		for (int s = 0; s < this.agents.size(); s++) {
			res += "<agent>"+this.agents.get(s)+"</agent>\n";
		}
		res += "</agents>\n";

		res += "<goals>\n";
		for (int s = 0; s < this.goals.size(); s++) {
			res += "<goal>"+this.goals.get(s)+"</goal>\n";
		}
		res += "</goals>\n";

		res += "<providers>\n";
		for (int i = 0; i < this.roles.size(); i++) {
			res += "<provider>";
			for (int j = 0; j < provider.get(roles.get(i)).length; j++) {
				res += provider.get(roles.get(i))[j] + " ";
			}
			res += "</provider>\n";
		}
		res += "</providers>\n";

		res += "<providing>\n";
		for (int i = 0; i < this.agents.size(); i++) {
			res += "<provides>";
			for (int j = 0; j < provides.get(agents.get(i)).length; j++) {
				res += provides.get(agents.get(i))[j] + " ";
			}
			res += "</provides>\n";
		}
		res += "</providing>\n";

		res += "<playing>\n";
		for (int i = 0; i < this.agents.size(); i++) {
			res += "<plays>";
			for (int j = 0; j < plays.get(agents.get(i)).length; j++) {
				res += plays.get(agents.get(i))[j] + " ";
			}
			res += "</plays>\n";
		}
		res += "</playing>\n";

		res += "<pursuing>\n";
		for (int i = 0; i < this.goals.size(); i++) {
			res += "<pursues>";
			for (int j = 0; j < pursues.get(goals.get(i)).length; j++) {
				res += pursues.get(goals.get(i))[j] + " ";
			}
			res += "</pursues>\n";
		}
		res += "</pursuing>\n";

		res += "<rolstructure>";
		for (int i = 0; i < this.roles.size(); i++) {
			res += roleStructure.get(roles.get(i)) + " ";
		}
		res += "</rolstructure>\n";

		res += "<costmatrix>\n";
		for (int i = 0; i < this.agents.size(); i++) {
			res += "<cost>";
			for (int j = 0; j < costMatrix.get(agents.get(i)).length; j++) {
				res += costMatrix.get(agents.get(i))[j] + " ";
			}
			res += "</cost>\n";
		}
		res += "</costmatrix>\n";
		
		res += "<agentassignment>";
		for (int i = 0; i < this.agents.size(); i++) {
			res += agentAssignment.get(agents.get(i)) + " ";
		}
		res += "</agentassignment>\n";

		res += "<roleassignment>";
		for (int i = 0; i < this.roles.size(); i++) {
			res += roleAssignment.get(roles.get(i)) + " ";
		}
		res += "</roleassignment>\n";

		res += "<newrolestructure>";
		for (int i = 0; i < this.roles.size(); i++) {
			res += newRoleStructure.get(roles.get(i)) + " ";
		}
		res += "</newrolestructure>\n";
		res += "</organization>";
		
		return res;
	}
	
	

	public void print_organization() {
		System.out.println("");
		System.out
				.println("ROLES:------------------------------------------------------------");
		for (int s = 0; s < this.roles.size(); s++) {
			System.out.println(this.roles.get(s));
		}

		System.out
				.println("SERVICES:------------------------------------------------------------");
		for (int s = 0; s < this.services.size(); s++) {
			System.out.println(this.services.get(s));
		}

		System.out
				.println("AGENTS:------------------------------------------------------------");
		for (int s = 0; s < this.agents.size(); s++) {
			System.out.println(this.agents.get(s));
		}

		System.out
				.println("GOALS:------------------------------------------------------------");
		for (int s = 0; s < this.goals.size(); s++) {
			System.out.println(this.goals.get(s));
		}

		System.out
				.println("PROVIDER:------------------------------------------------------------");
		for (int i = 0; i < this.roles.size(); i++) {
			System.out.print(roles.get(i) + "\t");
			for (int j = 0; j < provider.get(roles.get(i)).length; j++) {
				System.out.print(provider.get(roles.get(i))[j] + "\t");
			}
			System.out.println("");
		}

		System.out
				.println("PROVIDES:------------------------------------------------------------");
		for (int i = 0; i < this.agents.size(); i++) {
			System.out.print(agents.get(i) + "\t");
			for (int j = 0; j < provides.get(agents.get(i)).length; j++) {
				System.out.print(provides.get(agents.get(i))[j] + "\t");
			}
			System.out.println("");
		}

		System.out
				.println("PLAYS:------------------------------------------------------------");
		for (int i = 0; i < this.agents.size(); i++) {
			System.out.print(agents.get(i) + "\t");
			for (int j = 0; j < plays.get(agents.get(i)).length; j++) {
				System.out.print(plays.get(agents.get(i))[j] + "\t");
			}
			System.out.println("");
		}

		System.out
				.println("PURSUES:------------------------------------------------------------");
		for (int i = 0; i < this.goals.size(); i++) {
			System.out.print(goals.get(i) + "\t");
			for (int j = 0; j < pursues.get(goals.get(i)).length; j++) {
				System.out.print(pursues.get(goals.get(i))[j] + "\t");
			}
			System.out.println("");
		}

		System.out
				.println("ESTRUCTURA DE ROLS:------------------------------------------------------------");
		for (int i = 0; i < this.roles.size(); i++) {
			System.out.print(roleStructure.get(roles.get(i)) + "\t");
		}
		System.out.println("");

		System.out
				.println("MATRIU DE COSTOS------------------------------------------------------------");
		System.out.print("\t");
		for (int i = 0; i < this.roleIndex; i++) {
			System.out.print(this.roles.get(i) + "\t");
		}
		System.out.println("");
		for (int i = 0; i < this.agents.size(); i++) {
			System.out.print(agents.get(i) + "\t");
			for (int j = 0; j < costMatrix.get(agents.get(i)).length; j++) {
				System.out.print(costMatrix.get(agents.get(i))[j] + "\t");
			}
			System.out.println("");
		}

		System.out.println("ASSIGNACIÓ INICIAL:");
		System.out
				.println("ASSIGNACIÓ D'AGENTS------------------------------------------------------------");
		for (int i = 0; i < this.agents.size(); i++) {
			System.out.print(agentAssignment.get(agents.get(i)) + "\t");
		}
		System.out.println("");

		System.out
				.println("ASSIGNACIÓ DE ROLES------------------------------------------------------------");
		for (int i = 0; i < this.roles.size(); i++) {
			System.out.print(roleAssignment.get(roles.get(i)) + "\t");
		}
		System.out.println("");

		System.out.println("NOVA ESTRUCTURA DE ROLS:");
		System.out
				.println("ESTRUCTURA DE ROLS:------------------------------------------------------------");
		for (int i = 0; i < this.roles.size(); i++) {
			System.out.print(newRoleStructure.get(roles.get(i)) + "\t");
		}
		System.out.println("");
	}

	public void writeTxtFile(String filename) {
		try {
			File file = new File(filename);
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);

			out.write("\n");
			out.write("ROLES:------------------------------------------------------------\n");
			out.flush();
			for (int s = 0; s < this.roles.size(); s++) {
				out.write(this.roles.get(s) + "\n");
				out.flush();
			}
			out.write("\n");
			out.flush();

			out.write("SERVICES:------------------------------------------------------------\n");
			out.flush();
			for (int s = 0; s < this.services.size(); s++) {
				out.write(this.services.get(s) + "\n");
				out.flush();
			}
			out.write("\n");
			out.flush();

			out.write("AGENTS:------------------------------------------------------------\n");
			out.flush();
			for (int s = 0; s < this.agents.size(); s++) {
				out.write(this.agents.get(s) + "\n");
				out.flush();
			}
			out.write("\n");
			out.flush();
			
			out.write("GOALS:------------------------------------------------------------\n");
			out.flush();
			for (int s = 0; s < this.goals.size(); s++) {
				out.write(this.goals.get(s) + "\n");
				out.flush();
			}
			out.write("\n");
			out.flush();

			out.write("PROVIDER:------------------------------------------------------------\n");
			out.flush();
			for (int i = 0; i < this.roles.size(); i++) {
				for (int j = 0; j < provider.get(roles.get(i)).length; j++) {
					out.write(provider.get(roles.get(i))[j] + "\t");
					out.flush();
				}
				out.write("\n");
				out.flush();
			}
			out.write("\n");
			out.flush();

			out.write("PROVIDES:------------------------------------------------------------\n");
			out.flush();
			for (int i = 0; i < this.agents.size(); i++) {
				for (int j = 0; j < provides.get(agents.get(i)).length; j++) {
					out.write(provides.get(agents.get(i))[j] + "\t");
					out.flush();
				}
				out.write("\n");
				out.flush();
			}
			out.write("\n");
			out.flush();

			out.write("PLAYS:------------------------------------------------------------\n");
			out.flush();
			for (int i = 0; i < this.agents.size(); i++) {
				for (int j = 0; j < plays.get(agents.get(i)).length; j++) {
					out.write(plays.get(agents.get(i))[j] + "\t");
					out.flush();
				}
				out.write("\n");
				out.flush();
			}
			out.write("\n");
			out.flush();

			out.write("PURSUES:------------------------------------------------------------\n");
			out.flush();
			for (int i = 0; i < this.goals.size(); i++) {
				for (int j = 0; j < pursues.get(goals.get(i)).length; j++) {
					out.write(pursues.get(goals.get(i))[j] + "\t");
					out.flush();
				}
				out.write("\n");
				out.flush();
			}

			out.write("ESTRUCTURA DE ROLS:------------------------------------------------------------\n");
			out.flush();
			for (int i = 0; i < this.roles.size(); i++) {
				if(i == this.roles.size()-1)
					out.write(roleStructure.get(roles.get(i)) + "");
				else
					out.write(roleStructure.get(roles.get(i)) + " ");
				out.flush();
			}
			out.write("\n");
			out.flush();

			out.write("MATRIU DE COSTOS------------------------------------------------------------ESTA MATRIU LA MODIFIQUE A\n");
			out.flush();
			out.flush();
			for (int i = 0; i < this.roleIndex; i++) {
				out.write("\t"+ this.roles.get(i));
				out.flush();
			}
			out.write("\n");
			out.flush();
			for (int i = 0; i < this.agents.size(); i++) {
				out.write(agents.get(i));
				out.flush();
				for (int j = 0; j < costMatrix.get(agents.get(i)).length; j++) {
					out.write("\t"+costMatrix.get(agents.get(i))[j]);
					out.flush();
				}
				out.write("\n");
				out.flush();
			}
			out.write("\n");
			out.flush();

			out.write("ASSIGNACIÓ INICIAL:\n");
			out.write("ASSIGNACIÓ D'AGENTS------------------------------------------------------------\n");
			out.write("\t");
			out.flush();
			for (int i = 0; i < this.agents.size(); i++) {
				if(i == this.agents.size() - 1)
					out.write(agentAssignment.get(agents.get(i))+"");
				else
					out.write(agentAssignment.get(agents.get(i))+" \t");
				out.flush();
			}
			out.write("\n");
			out.flush();

			out.write("ASSIGNACIÓ DE ROLES------------------------------------------------------------\n");
			out.write("\t");
			out.flush();			
			for (int i = 0; i < this.roles.size(); i++) {
				if(i == this.roles.size() - 1)
					out.write(roleAssignment.get(roles.get(i))+"");
				else
					out.write(roleAssignment.get(roles.get(i))+" \t");
				out.flush();
			}
			out.write("\n");
			out.flush();

			out.write("NOVA ESTRUCTURA DE ROLS:\n");
			out.write("ESTRUCTURA DE ROLS:------------------------------------------------------------\n");
			out.flush();
			for (int i = 0; i < this.roles.size(); i++) {
				out.write(newRoleStructure.get(roles.get(i)) + " ");
				out.flush();
			}

			fstream.close();
			out.close();
		} catch (Exception e) {
		}
	}
	
	
	
	public int addCost(String agent, String role, int value){
		int i=0;
		for(i=0; i<this.roleIndex; i++)
			if(this.roles.get(i).equals(role))
				break;
		this.costMatrix.get(agent)[i] += value;
		return this.costMatrix.get(agent)[i];
	}
	
	public void setProvider(String role, String service, int value){
		int i=0;
		for(i=0; i<this.serviceIndex; i++)
			if(this.services.get(i).equals(service))
				break;
		this.provider.get(role)[i] = value;
	}
	
	public void setPlays(String agent, String role, int value){
		int i=0;
		for(i=0; i<this.roleIndex; i++)
			if(this.roles.get(i).equals(role))
				break;
		this.plays.get(agent)[i] = value;
	}
	
	public void setProvides(String agent, String service, int value){
		int i=0;
		for(i=0; i<this.serviceIndex; i++)
			if(this.services.get(i).equals(service))
				break;
		this.provides.get(agent)[i] = value;
	}
	
	public void setPursues(String goal, String service, int value){
		int i=0;
		for(i=0; i<this.serviceIndex; i++)
			if(this.services.get(i).equals(service))
				break;
		this.pursues.get(goal)[i] = value;
	}
	
	public int getProvider(String role, String service){
		int i=0;
		for(i=0; i<this.serviceIndex; i++)
			if(this.services.get(i).equals(service))
				break;
		return this.provider.get(role)[i];
	}
	
	public int getPlays(String agent, String role){
		int i=0;
		for(i=0; i<this.roleIndex; i++)
			if(this.roles.get(i).equals(role))
				break;
		return this.plays.get(agent)[i];
	}
	
	public int getProvides(String agent, String service){
		int i=0;
		for(i=0; i<this.serviceIndex; i++)
			if(this.services.get(i).equals(service))
				break;
		return this.provides.get(agent)[i];
	}
	
	public int getPursues(String goal, String service){
		int i=0;
		for(i=0; i<this.serviceIndex; i++)
			if(this.services.get(i).equals(service))
				break;
		return this.pursues.get(goal)[i];
	}
}
