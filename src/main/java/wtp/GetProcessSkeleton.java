/**
 * GetProcessSkeleton.java
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

import java.util.*;

/**
 * GetProcessSkeleton java skeleton for the axisService
 */
public class GetProcessSkeleton {

	public static final String DB_URL = "jdbc:mysql://localhost/thomas";
	public static final String DB_USER = "thomas";
	public static final String DB_PASSWD = "thomas";
	public static final String DB = "MySQL";
	public static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	public static final boolean DEBUG = true; 

	// database connection parameters, with defaults
	private static String s_dbURL = DB_URL;
	private static String s_dbUser = DB_USER;
	private static String s_dbPw = DB_PASSWD;
	private static String s_dbType = DB;
	private static String s_dbDriver = DB_DRIVER;

	/**
	 * GetProcess Gets the set of process associated to a profile and for each
	 * process gets the provider name. The method returns a list with the service ID and the 
	 * providers.
	 * 
	 * @param getProcess contains the service ID
	 * @return response contains a list with all the process that implement the profile
	 * @throws
	 */
	public wtp.GetProcessResponse GetProcess(wtp.GetProcess getProcess) {

		String processList = " ";
		String providerList = " ";
		GetProcessResponse response = new GetProcessResponse();
		String ServiceID = null;
		String ProcessID = null;
		IDBConnection conn = null;
		
		if (DEBUG) {
			System.out.println("GetProcess Service:");
			System.out.println("***ServiceID... " + getProcess.getServiceID());
		}
		
		// ensure the JDBC driver class is loaded
		try {
			Class.forName(s_dbDriver);
		} catch (Exception e) {
			System.err.println("Failed to load the driver for the database: " + e.getMessage());
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
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		
		if (DEBUG) {
			m.write(System.out, "N3");
		}
		
		//Query to get the reference to the set of service process that the service profile has (...owl#...)
		String queryStringProcess = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix prof: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
				+ "select ?p ?x" 
				+ "where {" 
				+ "      ?x service:presents <"+ getProcess.getServiceID() + "> ."
				+ "      ?x service:describedBy ?p ." 
				+ "      }";

		Query queryProcess = QueryFactory.create(queryStringProcess);
		
		if (DEBUG) {
			System.out.println(queryProcess.toString());
		}
		
		// Execute the query and obtain results
		QueryExecution qeProcess = QueryExecutionFactory.create(queryProcess, m);
		ResultSet resultsProcess = qeProcess.execSelect();
	
		//scan the service process that the service profile has
		for (Iterator j = resultsProcess; resultsProcess.hasNext();) {

			String result = resultsProcess.next().toString();
			StringTokenizer Tok = new StringTokenizer(result);
			String process = Tok.nextToken("<");
			ProcessID = Tok.nextToken(">");
			ProcessID = ProcessID.replace("<", "");
			
			if (DEBUG) {
				System.out.println("ProcessID: " + ProcessID);
			}
			
			Tok = new StringTokenizer(result);
			process = Tok.nextToken("<");

			process = Tok.nextToken("#");
			process = process.replace("<", "");
			if (DEBUG) {
				System.out.println("Service Process: " + process);
			}
			
			//Get providers that appear in the DB
			String queryStringProviders = 
				  "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
				+ "select ?x " 
				+ "where {" 
				+ "<"+ProcessID+">" + " process:hasServer ?x ."
				+ "      }";
			
			Query queryProviders = QueryFactory.create(queryStringProviders);
			
		
			// Execute the query and obtain results
			QueryExecution qeProviders = QueryExecutionFactory.create(queryProviders, m);
			ResultSet resultsProviders = qeProviders.execSelect();
			
			//Get the providers of the process 
			for (Iterator k = resultsProviders; resultsProviders.hasNext();) {

				String resultProv = resultsProviders.next().toString();
				StringTokenizer TokProv = new StringTokenizer(resultProv);
				String processProv = TokProv.nextToken("<");
				String ProviderID = TokProv.nextToken(">");
				ProviderID = ProviderID.replace("<", "");
				
				if (DEBUG) {
					System.out.println("Provider: " + ProviderID);
				}
				
				TokProv = new StringTokenizer(resultProv);
				processProv = TokProv.nextToken("<");

				processProv = TokProv.nextToken("#");
				processProv = processProv.replace("<", "");
				
				//If the process is the same that we are interested in, we add the provider
				//to the providerList
				if (process.equals(processProv)){
					String providerID = TokProv.nextToken(">").toString();
					providerID = providerID.replace("#", "");
					if (DEBUG) {
						System.out.println("ProviderID: " + providerID);
					}
					providerList = providerList+" "+providerID;
					
				}
				
				if (DEBUG) {
					System.out.println("Provider List: "+providerList);
				}
				
			}//end of get service providers
			
			qeProviders.close();
			processList= processList+" "+ProcessID+":"+providerList;
			
		}//end of get process of the service profile
		
		qeProcess.close();
		
		try {
			if (DEBUG) {
				System.out.println("Closing DB conection...");
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		response.set_return(1);
		response.setProcessList(processList);
		
		m.close();
		
		return (response);

	}//end GetProcess

	
	
	public OntModelSpec getModelSpec(ModelMaker maker) {
		// create a spec for the new ont model that will use no inference over
		// models made by the given maker (which is where we get the persistent models
		// from)
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		spec.setImportModelMaker(maker);

		return spec;
	}

}
