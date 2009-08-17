/**   
 * RegisterProfileSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import com.hp.hpl.jena.db.*;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*; 
import com.hp.hpl.jena.query.*;

import java.rmi.RemoteException;
import java.util.*;
import java.io.*;

import persistence.DataBaseInterface;
/**
 * RegisterProfileSkeleton java skeleton for the axisService
 */
public class RegisterProfileSkeleton {

 
	public static final Boolean DEBUG = true;

	// database connection parameters, with defaults
	private static String s_dbURL;
	private static String s_dbUser;
	private static String s_dbPw;
	private static String s_dbType;
	private static String s_dbDriver;

	/**
	 * RegisterProfile.  
	 * @param RegisterProfile. This parameter contains three elements: service goal ( is a string ),
	 * service profile ( is a string: urlprofile#profilename ) and agent id ( is a string ).
	 * @return RegisterProfileResponse. This parameter contains two elements: service id (the service 
	 * profile id which is an integer) and return which indicates if an error occurs.
	 */
   
	public wtp.RegisterProfileResponse RegisterProfile(wtp.RegisterProfile registerProfile) {
 
		wtp.RegisterProfileResponse response = new wtp.RegisterProfileResponse();
		
		if (DEBUG) {
			System.out.println("RegisterProfile Service :");
			System.out.println("***ServiceGoal... "	+ registerProfile.getServiceGoal());
			System.out.println("***AgentID... " + registerProfile.getAgentID());
			System.out.println("***ServiceProfile... "+ registerProfile.getServiceProfile());
		}

		
			 
			/////////////
			////JENA/////
			/////////////
			IDBConnection conn = null;
			OntModel m = null;
			Properties properties = new Properties();

			try {
			 	properties.loadFromXML(RegisterProfileSkeleton.class.getResourceAsStream("/" + "THOMASDemoConfiguration.xml"));
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

			
			StringTokenizer Tok = new StringTokenizer(registerProfile.getServiceProfile());
			String urlprofile = Tok.nextToken("#");
			
			
			if (DEBUG) {
				System.out.println("File to load ... "+ urlprofile);
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
			// now we plug that base model into an ontology model that also uses 
			// the given model maker to create storage for imported models
			OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
			spec.setImportModelMaker(maker);
			m = ModelFactory.createOntologyModel(spec, base);

			
			// Query to get the set of allowed provider roles
			String queryStringServiceRoles = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
					+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
					+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
					+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" 
					+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
					+ "select ?x "
					+ "where {"
					+ "      ?x rdf:subject <"+ urlprofile + "#provider_list"+">" + "      }";

			Query queryServiceRoles = QueryFactory.create(queryStringServiceRoles);

			if (DEBUG) {
				System.out.println(queryServiceRoles.toString());
			}

			// Execute the query and obtain results
			QueryExecution qeService = QueryExecutionFactory.create( queryServiceRoles, m);
			ResultSet resultServiceRoles = qeService.execSelect();
			String roleList=null;
			
			if (resultServiceRoles != null) {
				int controws=0;
				
				
				for (Iterator j = resultServiceRoles; resultServiceRoles.hasNext();) {
					controws++;
					String result = resultServiceRoles.next().toString();
					if (DEBUG) {
						System.out.println("Role: " + result);
					}
        	 
				    Tok = new StringTokenizer(result);
					String url = Tok.nextToken("<");
					url= Tok.nextToken("#");
					String role = Tok.nextToken(">");
					role = role.replace("#", "");

					if (DEBUG) {
						System.out.println("Role: " + role);
					}
					
					
					if(controws==1){
						roleList = role; 
					}
					else{
						roleList = roleList+", "+role;
					}
			
				}
			}
			
			System.out.println("Role list: "+roleList);
			
			//LLAMADA AL OMS
			//wtp.InformAgentRoleStub.InformAgentRoleResponse res = new  wtp.InformAgentRoleStub.InformAgentRoleResponse();
			//InformAgentRoleStub stub = new InformAgentRoleStub();
			//wtp.InformAgentRoleStub.InformAgentRole agentrole = new wtp.InformAgentRoleStub.InformAgentRole();
			//agentrole.setAgentID(registerProfile.getAgentID());
			//res.setRoleUnitList = stub.InformAgentRole(agentrole).getRoleUnitList();
			//res.setStatus = "OK";
			//res.setErrorValue = "";
			
			
			//Register in de DB the serviceprofileid
			persistence.DataBaseInterface thomasBD = new DataBaseInterface();
			String serviceprofileid = thomasBD.AddNewProfile(registerProfile.getServiceProfile());
			
			if (serviceprofileid != null) {
				
				if (DEBUG) {
					System.out.println("The serviceprofileid is: " + serviceprofileid);
				}
				
			//load the service profile in the database
			m.read(registerProfile.getServiceProfile());
			m.commit();

			if (DEBUG) {
				m.write(System.out, "N3");
			}

			try {
				if (DEBUG) {
					System.out.println("Closing DB connection...");
				}
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

			//Close the model 
			m.close();

			response.setServiceID(serviceprofileid);
			response.set_return(1);
			
			
		} else {
			if (DEBUG) {
				System.out.println("[Error] the service profile exists");
			}
			response.setServiceID("[Error] the service profile exists");
			response.set_return(0);
		}
		
		
		
		
		
		
		
		
		
		
		return (response);
	
	}//end RegisterProfile

}//end class