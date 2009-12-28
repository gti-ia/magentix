/**
 * GetProfileSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.util.*;
import java.io.*;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;

import persistence.DataBaseInterface;

/**
 * GetProcessSkeleton java skeleton for the axisService
 */
public class GetProcessSkeleton {
	

	public static final Boolean DEBUG = true;

	// database connection parameters, with defaults
	private static String s_dbURL;
	private static String s_dbUser;
	private static String s_dbPw;
	private static String s_dbType;
	private static String s_dbDriver;



	/**
	 * GetProfile
	 * @param GetProcess contains two elements: the service ID (is a string: service profile id) and the 
	 * agent id (is a string).
	 * @return GetProcessResponse contains two elements: provider list (is a string with the next template:
	 * [service implementation id urlprocess, service implementation id urlproces, ... ] and return which
	 * indicates if an error occurs
	 */
     public wtp.GetProcessResponse GetProcess(wtp.GetProcess getProcess){
    	 
                	 GetProcessResponse response = new GetProcessResponse();
                	 if (DEBUG) {
             			System.out.println("GetProcess Service:");
             			System.out.println("***ServiceID... " + getProcess.getServiceID());
             			System.out.println("***AgentID... " + getProcess.getAgentID());
             		}
         
                	 

                	 ////////////////
              		 //////JENA//////
                	 ////////////////
                	 
         			IDBConnection conn = null;
        			Properties properties = new Properties();

        			try {
        				properties.loadFromXML(ModifyProcessSkeleton.class.getResourceAsStream("/"+ "THOMASDemoConfiguration.xml"));
        				for (Enumeration e = properties.keys(); e.hasMoreElements();) {
        					// Obtenemos el objeto
        					Object obj = e.nextElement();
        					if (obj.toString().equalsIgnoreCase("DB_URL")) {
        						s_dbURL = properties.getProperty(obj.toString());
        					} else if (obj.toString().equalsIgnoreCase("DB_USER")) {
        						s_dbUser = properties.getProperty(obj.toString());
        					} else if (obj.toString().equalsIgnoreCase("DB_PASSWD")) {
        						s_dbPw = properties.getProperty(obj.toString());
        					} else if (obj.toString().equalsIgnoreCase("DB")) {
        						s_dbType = properties.getProperty(obj.toString());
        					} else if (obj.toString().equalsIgnoreCase("DB_DRIVER")) {
        						s_dbDriver = properties.getProperty(obj.toString());
        					}
        				}

        			} catch (IOException e) {
        				System.out.print(e);
        			}

        			// ensure the JDBC driver class is loaded
        			try {
        				Class.forName(s_dbDriver);
        			} catch (Exception e) {
        				System.err.println("Failed to load the driver for the database: "+ e.getMessage());
        				System.err.println("Have you got the CLASSPATH set correctly?");
        			}

        			// Create database connection
        			try {
        				conn = new DBConnection(s_dbURL, s_dbUser, s_dbPw, s_dbType);
        			} catch (Exception e) {
        				e.printStackTrace();
        				System.exit(1);
        			}

        			ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
        			Model base = maker.createModel("http://example.org/ontologias");
        			OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker),base);

        			persistence.DataBaseInterface thomasBD = new DataBaseInterface();
             		String urlprofile = thomasBD.GetServiceProfileURL(getProcess.getServiceID());
        			String roleList = getProfileRoles(urlprofile, m);
        			boolean hasRole = checkRole(getProcess.getAgentID(), getProcess.getAgentID(), roleList);
        			
        			if(hasRole){
        				String processlist = thomasBD.GetServiceProcessFromProfile(getProcess.getServiceID());
        				if(processlist!=null){
        					response.setProcessList(processlist);
        					response.set_return(1);
        				}
        				else{
        					response.setProcessList("[Error] The service id does not exist");
        					response.set_return(0);
        				}
        			}
        			else{
        				response.setProcessList("[Error]: the agent does not have the appropiated role");
    					response.set_return(0);
        			}
        			
        			return (response);
        }
     
 	public OntModelSpec getModelSpec(ModelMaker maker) {
		// create a spec for the new ont model that will use no inference over
		// models made by the given maker (which is where we get the persistent models
		// from)
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		spec.setImportModelMaker(maker);

		return spec;
	}
 	
 	
 	String getProfileRoles(String urlprofile, OntModel m){
 		
 		// Query to get the set of allowed provider roles
 		String queryStringServiceRoles = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
 				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
 				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
 				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
 				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" 
 				+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
 				+ "select ?x "
 				+ "where {"
 				+ "      ?x rdf:subject <"+ urlprofile + "#client_list"+">" + "      }";

 		Query queryServiceRoles = QueryFactory.create(queryStringServiceRoles);

 		if (DEBUG) {
 			System.out.println(queryServiceRoles.toString());
 		}

 		// Execute the query and obtain results
 		QueryExecution qeService = QueryExecutionFactory.create( queryServiceRoles, m);
 		ResultSet resultServiceRoles = qeService.execSelect();
 		String roleList=null;
 		String organizationList=null;
 		
 		if (resultServiceRoles != null) {
 			int controws=0;
 			
 			
 			for (Iterator j = resultServiceRoles; resultServiceRoles.hasNext();) {
 				controws++;
 				String result = resultServiceRoles.next().toString();
 				if (DEBUG) {
 					System.out.println("Role: " + result);
 				}
 		 
 				StringTokenizer Tok = new StringTokenizer(result);
 				String url = Tok.nextToken("<");
 				url= Tok.nextToken("#");
 				String role = Tok.nextToken(">");
 				role = role.replace("#", "");

 				if (DEBUG) {
 					System.out.println("Role: " + role);
 				}
 				
 				// Query to get the set of allowed provider roles
 				String queryStringOrganization = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
 						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
 						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
 						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
 						+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" 
 						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
 						+ "select ?x "
 						+ "where {"
 						+ "      <"+ urlprofile + "#"+role+">" + " rdf:object "+ "?x}";

 				Query queryOrganization = QueryFactory.create(queryStringOrganization);

 				if (DEBUG) {
 					System.out.println(queryOrganization.toString());
 				}

 				// Execute the query and obtain results
 				QueryExecution qeOrganization = QueryExecutionFactory.create( queryOrganization, m);
 				ResultSet resultOrganization = qeOrganization.execSelect();
 				String resultOrg = resultOrganization.next().toString();
 				
 				StringTokenizer TokOrg = new StringTokenizer(resultOrg);
 				String urlOrg = TokOrg.nextToken("<");
 				urlOrg= TokOrg.nextToken("#");
 				String org = TokOrg.nextToken(">");
 				org = org.replace("#", "");

 				if (DEBUG) {
 					System.out.println("Organization: " + org);
 				}
 				
 				if(controws==1){
 					roleList = "("+role+","+org+")"; 
 					
 				}
 				else{
 					roleList = roleList+" "+"("+role+","+org+")";
 				}
 		
 			}
 		}
 		
 		System.out.println("Role list: "+roleList);
 		return(roleList);
 	}
 		
 		boolean checkRole(String AgentID, String RequestedAgentID, String roleList){
 			
 			boolean hasRole = false;
 			try {
 				//stub para hacer la llamada
 				InformAgentRoleStub stub = new InformAgentRoleStub();
 				
 				//objeto input
 				wtp.InformAgentRoleStub.InformAgentRole agentrole = new wtp.InformAgentRoleStub.InformAgentRole();
 				agentrole.setAgentID(AgentID);
 				agentrole.setRequestedAgentID(RequestedAgentID);
 				
 				//objeto output
 				wtp.InformAgentRoleStub.InformAgentRoleResponse res = new wtp.InformAgentRoleStub.InformAgentRoleResponse();
 				
 				res.localRoleUnitList = stub.InformAgentRole(agentrole).getRoleUnitList();
 				res.localStatus = "OK";
 				res.localErrorValue = "";

 				System.out.println("OMS Role Unit List: " + res.localRoleUnitList);
 			
 				String omslist = res.localRoleUnitList.replace("[", "");
 				omslist = omslist.replace("]", "");
 				
 				StringTokenizer Token = new StringTokenizer(roleList);
 				String unitandrole;

 				
 				while (Token.hasMoreTokens()) {

 					unitandrole = Token.nextToken(")");
 					unitandrole = unitandrole.concat(")");
 					System.out.println("unitandrole: " + unitandrole);

 					
 					if (omslist.contains(unitandrole)) {
 						hasRole = true;
 					}

 				}
 				
 				
 			} catch (Exception e) {
 				e.printStackTrace();
 			}

 			return (hasRole);
 		}
     
    }
    