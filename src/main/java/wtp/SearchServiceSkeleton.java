/**
* SearchServiceSkeleton.java
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
import java.io.*;
import java.rmi.RemoteException;

import persistence.DataBaseInterface;

import wtp.PlannerStub;


    /**
     *  SearchServiceSkeleton java skeleton for the axisService
     */
    public class SearchServiceSkeleton{
        

    	public static final Boolean DEBUG = true;

    	// database connection parameters, with defaults
    	private static String s_dbURL;
    	private static String s_dbUser;
    	private static String s_dbPw;
    	private static String s_dbType;
    	private static String s_dbDriver;
       
    /**
     * Auto generated method signature
     * @param SearchService contains an element: service purpose (is a string: the service description).
     * @return SearchServiceResponse contains an element: services list (is a list of 
     * <service profile id, ranking: service profile id, ranking: ...>  and return which indicates if an
     * error occurs. 
    */
     public wtp.SearchServiceResponse SearchService(wtp.SearchService searchService) {
    	 
    	if (DEBUG) {
 			System.out.println("Servicio SearchService:");
 			System.out.println("***ServicePurpose... "+ searchService.getServicePurpose());
 			System.out.println("***AgentID... "+ searchService.getAgentID());
 		}
    	
    	// answer
 		wtp.SearchServiceResponse response = new wtp.SearchServiceResponse();
 		
 		Properties properties = new Properties();
  
		  try {
			   properties.loadFromXML(SearchServiceSkeleton.class.getResourceAsStream("/"+"THOMASDemoConfiguration.xml"));
				for (Enumeration e = properties.keys(); e.hasMoreElements() ; ) {
				    // Obtenemos el objeto
				    Object obj = e.nextElement();
				    if (obj.toString().equalsIgnoreCase("DB_URL"))
				    {
				    	s_dbURL= properties.getProperty(obj.toString());	
				    }
				    else if (obj.toString().equalsIgnoreCase("DB_USER"))
				    {
				    	s_dbUser = properties.getProperty(obj.toString());
				    }
				    else    if (obj.toString().equalsIgnoreCase("DB_PASSWD"))
				    {
				    	s_dbPw = properties.getProperty(obj.toString());
				    }
				    else    if (obj.toString().equalsIgnoreCase("DB"))
				    {
				    	s_dbType = properties.getProperty(obj.toString());
				    }
				    else    if (obj.toString().equalsIgnoreCase("DB_DRIVER"))
				    {
				    	s_dbDriver = properties.getProperty(obj.toString());
				    }
				}

		    } catch (IOException e) {
		    	System.out.print(e);
		    }
  	
		IDBConnection conn = null;

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
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
	
		// Answer
	
		String serviceList = SearchByServiceName(m,searchService.getServicePurpose());
		//response.setServicesList(planningComposition(m));
		
		try {
			if (DEBUG) {
				System.out.println("Closing DB connection...");
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		m.close();
		
		if(serviceList!=null){
			response.set_return(1);
			response.setServicesList(serviceList);
		}
		else{
			response.set_return(0);
			response.setServicesList("There are not profiles with the goal: "+searchService.getServicePurpose());
		}
		return (response);

	}// end SearchService

    
	public OntModelSpec getModelSpec(ModelMaker maker) {
		// create a spec for the new ont model that will use no inference over
		// models made by the given maker (which is where we get the persistent models
		// from)
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		spec.setImportModelMaker(maker);

		return spec;
	}

	
	/**
     * SearchByServiceName
     * @param m
     * @param ServicePurpose the name of the service to be searched
     * @return response a list of <service ID, ranking> or <service Composition, ranking>
    */
	public String SearchByServiceName(OntModel m, String ServicePurpose){
		
		String ServiceID = null;
		String servicesList =null;
		String queryStringSearchName =
					"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
					+ "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
					+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
					+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
					+ "select ?x " + "where {" + "      ?x profile:serviceName \""
					+ ServicePurpose + "\"@en  ." + "      }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		if (DEBUG) {
			System.out.println(querySearchName.toString());
		}

		// Execute the query and obtain results
		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {
			int controws=0;
			
			for (Iterator j = resultsSearchName; resultsSearchName.hasNext();) {
				controws++;
				String result = resultsSearchName.next().toString();
				StringTokenizer Tok = new StringTokenizer(result);
				String profile = Tok.nextToken("<");
				ServiceID = Tok.nextToken(">");
				ServiceID = ServiceID.replace("<", "");
				if (DEBUG) {
					System.out.println("Service profile: " + ServiceID);
				}
				Tok = new StringTokenizer(result);
				profile = Tok.nextToken("<");
				profile = Tok.nextToken("#");
				profile = profile.replace("<", "");
				if (DEBUG) {
					System.out.println("Profile " + profile);
				}
				
				persistence.DataBaseInterface thomasBD = new DataBaseInterface();
         		ServiceID = thomasBD.GetServiceProfileID(profile);

				//Service Ranking is not implemented, ranking is always 5
				if(controws==1){
					servicesList = ServiceID + " 5 "; 
				}
				else{
					servicesList = servicesList + ":" + ServiceID + " 5";
				}
				
			}//end for 
		}//end if
			
		// close the query
		qeSearchName.close();
		
		
		return(servicesList);
		
			
	}//end SearchByServiceName
	
	
	/**
     * planningComposition
     * @param m
     * @return response a list of <service ID, ranking> or <service Composition, ranking>
    */
	public String planningComposition(OntModel m){
		
		wtp.PlannerStub.PlannerResponse res = new  wtp.PlannerStub.PlannerResponse();
		String queryStringSearchProcess =
			"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
					+ "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
					+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
					+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
					+ "select ?x " 
					+ "where {" 
					+ " ?y service:describedBy ?x"
					+ "}";

			Query querySearchProcess = QueryFactory.create(queryStringSearchProcess);

			if (DEBUG) {
				System.out.println(querySearchProcess.toString());
			}

			// Execute the query and obtain results
			QueryExecution qeSearchProcess = QueryExecutionFactory.create(querySearchProcess, m);
			ResultSet resultsSearchProcess = qeSearchProcess.execSelect();

			String ProcessID="";
			String processList= "";
			if (resultsSearchProcess != null) {
				for (Iterator j = resultsSearchProcess; resultsSearchProcess.hasNext();) {

					String result = resultsSearchProcess.next().toString();
					StringTokenizer Tok = new StringTokenizer(result);
					String profile = Tok.nextToken("<");
					ProcessID = Tok.nextToken("#");
					ProcessID = ProcessID.replace("<", "");
					if (DEBUG) {
						System.out.println("ProcessID: " + ProcessID);
					}
					//ProcessList
					processList = processList + " " + ProcessID ;
				}//end for 
			}//end if
			
			// close the query
			qeSearchProcess.close();
	
			try {
			
				PlannerStub stub = new PlannerStub();
				wtp.PlannerStub.Planner planner = new wtp.PlannerStub.Planner();
				
				//Manufacturing example
				/*planner.setGoal("http://www.comounaregadera.es/inicialmente/services/Packing/UseCase1_GoalOntology.owl");
				planner.setInit("http://www.comounaregadera.es/inicialmente/services/Packing/UseCase1_InitialOntology.owl");
				planner.setServices("http://www.comounaregadera.es/inicialmente/services/Packing/GetItems.owl http://www.comounaregadera.es/inicialmente/services/Packing/GetOrder.owl http://www.comounaregadera.es/inicialmente/services/Packing/LockPiston.owl http://www.comounaregadera.es/inicialmente/services/Packing/UnLockPiston.owl http://www.comounaregadera.es/inicialmente/services/Packing/QueryStorage.owl http://www.comounaregadera.es/inicialmente/services/Packing/QueryCarriersAndStorage.owl http://www.comounaregadera.es/inicialmente/services/Packing/SendOrder.owl http://www.comounaregadera.es/inicialmente/services/Packing/GetItemsOp.owl ");
				*/
				
				//other example
				planner.setGoal("http://localhost:8080/sfservices/THservices/owl/owls/TH_GoalOntology.owl");
				planner.setInit("http://localhost:8080/services/THservices/owl/owls/TH_InitialOntology.owl");
				planner.setServices(processList);
				
				
				System.out.println("antes del llamar al planner goal "+planner.getGoal());
				System.out.println("init "+planner.getInit());
				System.out.println("services "+planner.getServices());
				res.localPlan = stub.planner(planner).getPlan();
				res.local_return = 1;
            
			} catch (RemoteException e) {
				e.printStackTrace();
			}//end catch			
			
			return (res.localPlan);
	
	}//end Planning Composition	
 
}//end class

