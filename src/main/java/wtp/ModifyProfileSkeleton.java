
/**
 * ModifyProfileSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.update.*;

import java.rmi.RemoteException;
import java.util.*; 
import java.io.*;
 
 
import  persistence.DataBaseInterface;



  /**
     *  ModifyProfileSkeleton java skeleton for the axisService
     */
    public class ModifyProfileSkeleton{
    	

    	public static final Boolean DEBUG = true;

    	// database connection parameters, with defaults
    	private static String s_dbURL;
    	private static String s_dbUser;
    	private static String s_dbPw;
    	private static String s_dbType;
    	private static String s_dbDriver;
         
        /**
         * ModifyProfile
         * @param ModifyProfile contains four elements: service id (is a string: service profile id), service
         * goal (currently is not in use), agent id (is a string) and service profile ( is a string 
         * urlprofile#profilename).
         * @return ModifyProfileResponse contains return which indicates if a problem occurs (1: ok, 0: there
         * are provider which implement the profile, -1: the service id is not valid). 
         */
         public wtp.ModifyProfileResponse ModifyProfile(wtp.ModifyProfile modifyProfile){
        	 
        	wtp.ModifyProfileResponse response = new wtp.ModifyProfileResponse();

		if (DEBUG) {
			System.out.println("ModifyProfile Service :");
			System.out.println("***ServiceID..." + modifyProfile.getServiceID());
			System.out.println("***AgentID..." + modifyProfile.getAgentID());
			System.out.println("***ServiceGoal... "	+ modifyProfile.getServiceGoal());
			System.out.println("***ServiceProfile... "+ modifyProfile.getServiceProfile());
		}
		
		
		StringTokenizer Tok = new StringTokenizer(modifyProfile.getServiceProfile());
		String urlprofile = Tok.nextToken("#");
		String profilename = Tok.nextToken();
		
		persistence.DataBaseInterface thomasBD = new DataBaseInterface();
		
		
		if (thomasBD.CheckServiceProfileID(modifyProfile.getServiceID())) {

			/////////////
			////JENA/////
			/////////////
			IDBConnection conn = null;
			Properties properties = new Properties();
			
			try {
				properties.loadFromXML(ModifyProfileSkeleton.class.getResourceAsStream("/"+ "THOMASDemoConfiguration.xml"));
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
   		
			//Query to get the reference to the set of service process that the service profile has (...owl#...)
			/*String queryStringProcess = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
				+ "select ?x " 
				+ "where {" 
				+ "      ?x service:presents <"+modifyProfile.getServiceProfile()+">"
				+ "      }";

			Query queryProcess = QueryFactory.create(queryStringProcess);
		
			if (DEBUG) {
				System.out.println(queryProcess.toString());
			}
		
			// Execute the query and obtain results
			QueryExecution qeProcess = QueryExecutionFactory.create(queryProcess, m);
			ResultSet resultsProcess = qeProcess.execSelect();
		    */
			//if(!resultsProcess.hasNext()){
			
			if(thomasBD.GetServiceProcessFromProfile(modifyProfile.getServiceID())==null){
				
		
				if (DEBUG) {
					System.out.println("URL profile: "+ urlprofile);
					System.out.println("profile name: "+ profilename);
				}
				
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
   		
				//Delete profile tuples where the property is profile
				String update= 
					"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
					"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
					"prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
					"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + 
					"prefix mind: <"+urlprofile+"#>" + 
      
					" delete {?x ?y ?z}" +
					" where" +
					"{" +
					" mind:"+profilename+" ?y ?z" +
					" filter ( ?y = profile:hasInput " +
					"|| ?y = profile:hasOutput " +
					"|| ?y = profile:serviceName " +
					"|| ?y = service:isPresentedBy " +
					"|| ?y = service:presents " +
					"|| ?z = profile:Profile " + 
					")" +
					"?x ?y ?z}";
        
				if (DEBUG) {
    				System.out.println(update);
    			}
				
				//Query to get the service name related with the profile
				String queryStringServiceName =
					"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
					+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
					+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
					+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
					+ "prefix mind: <"+urlprofile+"#>" 
					+ " select ?x " 
					+ " where {" 
					+ " mind:"+profilename+" service:presentedBy ?x " 
					+ "}";

				Query queryServiceName = QueryFactory.create(queryStringServiceName);
				// Execute the query and obtain results
				QueryExecution qeServiceName = QueryExecutionFactory.create(queryServiceName, m);
				ResultSet resultsServiceName = qeServiceName.execSelect();
				String ServiceName = resultsServiceName.next().toString();
	      		
	    
				//Query Result format
				//( ?x = <http://paracetamol.dsic.upv.es:8080/SF/OWLS/SearchCheapHotelProfile.owl#SearchCheapHotelService> )
				Tok = new StringTokenizer(ServiceName);
				String urlname = Tok.nextToken("#");
				String servicename = Tok.nextToken();
				Tok = new StringTokenizer(servicename);
				String name = Tok.nextToken(">");
   		
				if (DEBUG) {
					System.out.println("Service name: "+ name);
				}
				
				//Delete tuples where the property is service (it is related with the profile)
				String update2= 
					"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
					"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
					"prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
					"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + 
					"prefix mind: <"+urlprofile+"#>" + 
					" delete {?x ?y ?z}" +
					" where" +
					"{" +
					" mind:"+name+" ?y ?z" +
					" filter ( ?z = service:Service " +
					"|| ?y = service:presents " +
					")" +
					"?x ?y ?z}";
        
				// Execute the query and obtain results
				QuerySolution querysol=new QuerySolutionMap();
				QuerySolution querysol2=new QuerySolutionMap();
				UpdateAction.parseExecute(update, m, querysol2);
				UpdateAction.parseExecute(update2, m, querysol);

				if (DEBUG) {
					System.out.println("DB after delete profile ... ");
					m.write(System.out, "N3");
				}
	        
				// Load the new service profile
				m.read(modifyProfile.getServiceProfile());
				m.commit();
				response.set_return(1);
				if (DEBUG) {
					System.out.println("DB after load new profile ... ");
					m.write(System.out, "N3");
				}
	       
				m.close();
	    	
				
			
			}else{
				response.set_return(0);
				System.err.println("[ERROR]The profile can not be modify. There are providers which implement it");
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
	
		} else {
			response.set_return(-1);
			System.err.println("[ERROR]The profile id does not exit");
			
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
           
           
      
    }
