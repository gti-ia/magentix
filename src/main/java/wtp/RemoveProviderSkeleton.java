
    /**
 * RemoveProviderSkeleton.java
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
import java.util.*;

import com.hp.hpl.jena.update.*;
import java.io.*;

import persistence.DataBaseInterface;



/**
 * RemoveProviderSkeleton java skeleton for the axisService
 */
public class RemoveProviderSkeleton {

	public static final Boolean DEBUG = true;

	// database connection parameters, with defaults
	private static String s_dbURL;
	private static String s_dbUser;
	private static String s_dbPw;
	private static String s_dbType;
	private static String s_dbDriver;

	/**
	 * RemoveProvider
	 * 
	 * @param removeProvider
	 *  service implementation ID: serviceprofileid@agentid 
	 *  provider ID
	 *  agent ID
	 * @return response 1:OK otherwise 0
	 * @throws
	 */
	public wtp.RemoveProviderResponse RemoveProvider(wtp.RemoveProvider removeProvider) {

		wtp.RemoveProviderResponse response = new wtp.RemoveProviderResponse();

		if (DEBUG) {
			System.out.println("RemoveProvider Service :");
			System.out.println("***ProviderID..."+ removeProvider.getProviderID());
			System.out.println("***AgentID..."+ removeProvider.getAgentID());
			System.out.println("***ServiceImplementationID... "	+ removeProvider.getServiceImplementationID());
		}

		// Get the service process 
		persistence.DataBaseInterface thomasBD = new DataBaseInterface();
		String serviceprocess = thomasBD.GetServiceProcessFromProcessID(removeProvider.getServiceImplementationID());

		if (thomasBD.DeleteProcess(removeProvider.getServiceImplementationID())) {

			////////////
			////JENA////
			////////////

			IDBConnection conn = null;
			Properties properties = new Properties();

			try {
				properties.loadFromXML(RemoveProviderSkeleton.class
						.getResourceAsStream("/"
								+ "THOMASDemoConfiguration.xml"));
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
				System.err
						.println("Failed to load the driver for the database: "
								+ e.getMessage());
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

			/*
			 * String url = removeProvider.getProviderID(); StringTokenizer Tok =
			 * new StringTokenizer(url); // Get the owl document where the
			 * provider is described String urldoc = Tok.nextToken("#"); // Get
			 * the provider ID String providerName = Tok.nextToken();
			 */

			StringTokenizer Tok = new StringTokenizer(serviceprocess);
			// Get the owl process document
			String urlProcessDoc = Tok.nextToken("#");
			// Get the name of the process
			String processName = Tok.nextToken();

			System.out.println("urlProcessDoc " + urlProcessDoc);
			System.out.println("processname " + processName);

			// Get the url profile # service name
			m.write(System.out, "N3");
			String urlProfileService = GetServiceProfile(urlProcessDoc,
					processName, m);

			if (DEBUG) {
				System.out.println("URL process: " + urlProcessDoc);
				System.out.println("Process name: " + processName);
				System.out.println("URL profile#service: " + urlProfileService);
				// System.out.println("Provider ID: "+ providerName);
			}

			// Query to get the service profile name
			String queryStringServiceProfileName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
					+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
					+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
					+ "select ?x "
					+ "where {"
					+ urlProfileService
					+ " service:presents ?x" + "}";

			Query queryServiceProfileName = QueryFactory.create(queryStringServiceProfileName);

			// Execute the query
			QueryExecution qeServiceProfileName = QueryExecutionFactory.create(	queryServiceProfileName, m);
			ResultSet resultsServiceProfileName = qeServiceProfileName.execSelect();

			// To get the url profile # profile name
			String result = resultsServiceProfileName.next().toString();
			Tok = new StringTokenizer(result);
			String ServiceProfileResult = Tok.nextToken("=");
			String Profile = Tok.nextToken();
			Profile = Profile.replace(")", "");

			// RemoveProvider(urlProcessDoc, providerName, processName, m);
			RemoveProcess(urlProcessDoc, processName, urlProfileService,
					Profile, m);

			m.commit();
			response.set_return(1);

			if (DEBUG) {
				m.write(System.out, "N3");
			}

			m.close();

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
			System.out.println("[Error] Service process id does not exist");
			response.set_return(0);

		}
		
		return (response);
	}// end RemoveProvider
     
  
         /**
			 * RemoveProcess
			 * 
			 * @param urlProcessDoc
			 * @param urlProfileService
			 * @param Profile
			 * @param m
			 * @return
			 */
         public int RemoveProcess(String urlProcessDoc, String processname, String urlProfileService, String Profile, OntModel m){
        	 
        	   if (DEBUG) {
       			System.out.println("Removing Process ... ");
	 	       }
        	 
        	   
        	    // Query to know if there are more process with the same profile
    			String queryStringServiceProcess =
    				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
    						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
    						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
    						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
    						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
    						+ "select ?x " 
    						+ "where {" 
    						+ "?x service:presents "+Profile 
    						+ "}";

    			Query queryServiceProcess = QueryFactory.create(queryStringServiceProcess);

         		// Execute the query
    			QueryExecution qeServiceProcess = QueryExecutionFactory.create(queryServiceProcess, m);
    			ResultSet resultsServiceProcess = qeServiceProcess.execSelect();
    			
    			int numprocess=0;
    			Iterator j = resultsServiceProcess;
    			while ( resultsServiceProcess.hasNext()) {
    				resultsServiceProcess.next();
    				numprocess++;
    			}
    			
    			// If there are not more than one process, the profile should be deleted
    			if (numprocess==1) {
    				if (DEBUG) {
	          			System.out.println("The profile should be deleted... ");
		 	        }
    				DeleteProfile(urlProfileService,Profile, m);
    			}
    			
        		String processGround = GetServiceGrounding(urlProcessDoc, processname,urlProfileService, m);
        		String processGroundWSDL = GetServiceWSDLGrounding(urlProcessDoc, processGround, m);
        		String WsdlURL = GetServiceWSDLGroundingDoc(urlProcessDoc, processGroundWSDL, m);
        		
        		// Delete Process tuples	
        		DeleteWSDLMessagePart(urlProcessDoc, WsdlURL, m);
        		DeleteWSDLOperation(urlProcessDoc, WsdlURL, m);
        		DeleteWSDLPortType(urlProcessDoc, WsdlURL, m);
        		DeleteProcessInputs(urlProcessDoc, processname, WsdlURL, m);
        		DeleteProcessOutputs(urlProcessDoc, processname, WsdlURL, m);
        		DeleteProcessGrounding(urlProcessDoc, processname, processGround, processGroundWSDL, m);
        		DeleteProcess(urlProcessDoc, processname, m);
              		
        		return(1);
        	 
         }// end RemoveProcess
         
         
         
         
         /**
          * RemoveProvider
          * @param urlProcessDoc
          * @param providerName
          * @param processName
          * @param m
          * @return
          */
         public void RemoveProvider(String urlProcessDoc, String providerName, String processName, OntModel m){
        	 
        	   if (DEBUG) {
       			System.out.println("Remove Provider ... ");
	 	       }
        	 
        	   
        	    // Query to know if there are more process with the same profile
    			String deleteProvider =
    				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
    						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
    						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
    						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
    				    	+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
    				    	+ "delete {?x ?y ?z}" 
    						+ "where" 
    					    + "{ <"+urlProcessDoc+"#"+providerName+">"+"?y ?z" +
    		 	            " filter ( ?y = process:hasServer " +
    		 	            "		 || ?y = rdfs:label "+
    		 	            "        || ?z = process:hasParticipant ) "+
    		 	            "?x ?y ?z}";
    			
    			// Query to know if there are more process with the same profile
    			String deleteProvider2 =
    				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
    						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
    						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
    						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
    				    	+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
    				    	+ "delete {?x ?y ?z}" 
    						+ "where" 
    					    + "{ <"+urlProcessDoc+"#"+processName+">"+"?y ?z" +
    		 	            " filter ( ?y = process:hasServer " +
    		 	            ") "+
    		 	            "?x ?y ?z}";
    						
    		    // Execute the query and obtain results
    		    QuerySolution querysol=new QuerySolutionMap();
    		  	UpdateAction.parseExecute(deleteProvider, m, querysol);
    		  	UpdateAction.parseExecute(deleteProvider2, m, querysol);
         }
         
         
         /**
          * DeleteProfile
          * @param urlProfileService
          * @param Profile
          * @param m
          * @return
          */
         void DeleteProfile(String urlProfileService,String Profile, OntModel m){
        	 
        	 if (DEBUG) {
       			System.out.println("Delete Profile... ");
	 	      }
        	 
        	//Delete profile tuples where the property is profile
 			String update= 
 	        	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
 	            "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
 	            "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
 	            "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + 
 	            "delete {?x ?y ?z}" +
 	            "where" +
 	            "{" +Profile+" ?y ?z" +
 	            " filter ( ?y = profile:hasInput " +
 	                   "|| ?y = profile:hasOutput " +
 	                   "|| ?y = profile:serviceName " +
 	                   "|| ?y = service:isPresentedBy " +
 	                   "|| ?y = service:presentedBy " +
   	                   "|| ?y = profile:textDescription " +
 	                   "|| ?y = service:presents " +
 	                   "|| ?z = profile:Profile " +
 	                   ")" +
 	            "?x ?y ?z}";
 			
 		         		
 			//Delete tuples where the property is service (it is related with the profile)
 	        String update2= 
 	        	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
 	            "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
 	            "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
 	            "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + 
 	            "delete {?x ?y ?z}" +
 	            "where" +
 	            "{" +urlProfileService+" ?y ?z" +
 	            " filter ( ?z = service:Service " +
 	                   "|| ?y = service:presents " +
 	                   ")" +
 	            "?x ?y ?z}";
 	        
 	       
        		// Execute the query and obtain results
        		QuerySolution querysol=new QuerySolutionMap();
  	            UpdateAction.parseExecute(update, m, querysol);
  	            UpdateAction.parseExecute(update2, m, querysol);
  	      
         }// end DeleteProfile
         
         
         /**
          * DeleteProcessInputs
          * @param urlProcessDoc
          * @param processname
          * @param WsdlURL
          * @param m
          * @return
          */
         public void DeleteProcessInputs(String urlProcessDoc, String processname, String WsdlURL, OntModel m) {
         
         	// Query to get the service inputs tuples related with the service process
 			String queryStringServiceInputs =
 				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
 						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
 						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
 						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
 						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
 						+ "prefix mind: <"+urlProcessDoc+"#>" 
 						+ "select ?x " 
 						+ "where {" 
 						+ "mind:"+processname+" process:hasInput ?x " 
 						+ "}";

 			Query queryServiceInputs = QueryFactory.create(queryStringServiceInputs);

      		// Execute the query and obtain the service process inputs
 			QueryExecution qeServiceInputs = QueryExecutionFactory.create(queryServiceInputs, m);
 			ResultSet resultsServiceInputs = qeServiceInputs.execSelect();
 		    // For each input, all the tuples related with the process are deleted
 			if (resultsServiceInputs != null) {
 				for (Iterator j = resultsServiceInputs; resultsServiceInputs.hasNext();) {
 					//To take only the name of the input
 					String result = resultsServiceInputs.next().toString();
 					StringTokenizer Tok = new StringTokenizer(result);
 					String processInputResult = Tok.nextToken("#");
 					String processInputName = Tok.nextToken();
 					processInputName = processInputName.replace(">", "");
 					processInputName = processInputName.replace(")", "");
 					if (DEBUG) {
 						System.out.println("Process Input: " + processInputName);
 					}
 				    
 					// Delete input tuples related with the service process
 					String updateInput= 
 			        	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
 			            "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
 			            "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
 			            "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
 			            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
 			            "prefix mind: <"+urlProcessDoc+"#>" +
 			            "delete {?x ?y ?z}" +
 			            "where" +
 			            "{mind:" +processInputName+" ?y ?z" +
 			            " filter ( ?y = rdfs:label " +
 			                   "|| ?y = process:parameterType " +
 			                   "|| ?z = process:Input " +
 			                   ")" +
 			            "?x ?y ?z}";
 		       		
 	        
 					//Delete the output tuple related with the service grounding
 					String updateGroundInput= 
 			        	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
 			            "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
 			            "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
 			            "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
 			            "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
 			            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
 			            "prefix mind: <"+urlProcessDoc+"#>" +
 			            "delete {?x ?y ?z}" +
 			            "where" +
 			            "{?x ?y mind:"+processInputName +
 			            " filter ( ?y = grounding:owlsParameter " +
 			                   ")" +
 			            "?x ?y ?z}";
 					
 		       		// Execute the query 
 					QuerySolution querysol=new QuerySolutionMap();
 		 	        UpdateAction.parseExecute(updateInput, m, querysol);
 		 	        UpdateAction.parseExecute(updateGroundInput, m, querysol);
 	   
 				}//end for 
 			}//end if	

         }// end Delete inputs
         
         
         /**
          * DeleteWSDLMessagePart
          * @param urlProcessDoc
          * @param WsdlURL
          * @param m
          * @return 
          */
         public void DeleteWSDLMessagePart(String urlProcessDoc, String WsdlURL, OntModel m){
         	
         	
 			// Query to get the service input and output tuples related with the property grounding:wsdlMessagePart
 			String queryStringWSDLMsgMap =
 				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
 						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
 						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
 						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
 						+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" 
 						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
 						+ "prefix mind: <"+urlProcessDoc+"#>" 
 						+ "select ?y " 
 						+ "where {" 
 						+ "?x grounding:wsdlMessagePart ?y " 
 						+ "}";

 			Query queryWSDLMsgMap = QueryFactory.create(queryStringWSDLMsgMap);
 			WsdlURL=WsdlURL.replace(" ", "");
 			
      		// Execute the query and obtain the WsdlMessageMap value  
 			QueryExecution qeWSDLMsgMap = QueryExecutionFactory.create(queryWSDLMsgMap, m);
 			ResultSet resultsqeWSDLMsgMap = qeWSDLMsgMap.execSelect();
 			
 			// For each WsdlMessageMap, all the tuples related with the service are deleted
 			if (resultsqeWSDLMsgMap != null) {

 				for (Iterator j = resultsqeWSDLMsgMap; resultsqeWSDLMsgMap.hasNext();) {
 				
 					// To take only the WsdlMessageMap 
 					String result = resultsqeWSDLMsgMap.next().toString();
 					// If the url related with the WsdlMessageMap property contains the url of the service,
 					// the tuple should be deleted
 					if(result.contains((CharSequence)WsdlURL)){
 						StringTokenizer Tok = new StringTokenizer(result);
 						String wsdlMsg = Tok.nextToken("=");
 						wsdlMsg = Tok.nextToken();
 						wsdlMsg = wsdlMsg.replace(")", "");
 						System.out.println("wsdlMSG : "+wsdlMsg);
 					
 						// Delete the WsdlMessageMap tuple related with the service grounding
 						String updateWSDLMessageMap= 
 			        	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
 			            "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
 			            "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
 			            "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
 			            "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
 			            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
 			            "prefix mind: <"+urlProcessDoc+"#>" +
 			            "delete {?x ?y ?z}" +
 			            "where" +
 			            "{?x ?y "+wsdlMsg +
 			            " filter ( ?y = grounding:wsdlMessagePart " +
 			                  ")" +
 			            "?x ?y ?z}";
 		       		
 						// Execute the query 
 						QuerySolution querysol=new QuerySolutionMap();
 						UpdateAction.parseExecute(updateWSDLMessageMap, m, querysol);
 					
 					}// end if 
 				}// end for
 			}// end if
         }// DeleteWSDLMessagePart
         
         
         /**
          * DeleteWSDLPortType
          * @param urlProcessDoc
          * @param WsdlURL
          * @param m
          * @return
          */
         public void DeleteWSDLPortType(String urlProcessDoc, String WsdlURL, OntModel m){
         	
         	
 			// Query to get the service tuples related with the property grounding:portType 
 			String queryStringWSDLPort =
 				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
 						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
 						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
 						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
 						+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" 
 						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
 						+ "prefix mind: <"+urlProcessDoc+"#>" 
 						+ "select ?y " 
 						+ "where {" 
 						+ "?x grounding:portType ?y " 
 						+ "}";

 			Query queryWSDLPort = QueryFactory.create(queryStringWSDLPort);
 	
      		// Execute the query 
 			QueryExecution qeWSDLPort = QueryExecutionFactory.create(queryWSDLPort, m);
 			ResultSet resultsqeWSDLPort = qeWSDLPort.execSelect();
 			WsdlURL=WsdlURL.replace(" ", "");
 			
 			// For each input, all the tuples with the property grounding:portType related with the service are deleted
 			if (resultsqeWSDLPort != null) {

 				for (Iterator j = resultsqeWSDLPort; resultsqeWSDLPort.hasNext();) {
 				
 					// To take only the url associated with the property grounding:portType
 					String result = resultsqeWSDLPort.next().toString();
 					// If the url contains the url of the service, this tuple should be delete
 					if(result.contains((CharSequence)WsdlURL)){
 						StringTokenizer Tok = new StringTokenizer(result);
 						String wsdlPort = Tok.nextToken("=");
 						wsdlPort = Tok.nextToken();
 						wsdlPort = wsdlPort.replace(")", "");
 					
 						System.out.println("wsdlPort : "+wsdlPort);
 						//Delete the tuple 
 						String updateWSDLPort= 
 			        	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
 			            "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
 			            "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
 			            "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
 			            "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
 			            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
 			            "prefix mind: <"+urlProcessDoc+"#>" +
 			            "delete {?x ?y ?z}" +
 			            "where" +
 			            "{?x ?y "+wsdlPort +
 			            " filter ( ?y = grounding:portType " +
 			                  ")" +
 			            "?x ?y ?z}";
 		       		
 						// Execute the query 
 						QuerySolution querysol=new QuerySolutionMap();
 						UpdateAction.parseExecute(updateWSDLPort, m, querysol);
 					
 					}// end if 
 				}// end for
 			}// end if
         }// DeleteWSDLPortType
         
         
         
         /**
          * DeleteWSDLOperation
          * @param urlProcessDoc
          * @param WsdlURL
          * @param m
          * @return
          */
         public void DeleteWSDLOperation(String urlProcessDoc, String WsdlURL, OntModel m){
         	
         	
 			// Query to get the service tuples related with the property grounding:operation
 			String queryStringWSDLOp =
 				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
 						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
 						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
 						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
 						+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" 
 						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
 						+ "prefix mind: <"+urlProcessDoc+"#>" 
 						+ "select ?y " 
 						+ "where {" 
 						+ "?x grounding:operation ?y " 
 						+ "}";

 			Query queryWSDLOp = QueryFactory.create(queryStringWSDLOp);
 	
      		// Execute the query 
 			QueryExecution qeWSDLOp = QueryExecutionFactory.create(queryWSDLOp, m);
 			ResultSet resultsqeWSDLOp = qeWSDLOp.execSelect();
 			WsdlURL=WsdlURL.replace(" ", "");
 			
 			// For each tuple, the tuples with the property grounding:operation related 
 			// with the service grounding are deleted
 			if (resultsqeWSDLOp != null) {

 				for (Iterator j = resultsqeWSDLOp; resultsqeWSDLOp.hasNext();) {
 				
 					//To take only the url of the property grounding:operation
 					String result = resultsqeWSDLOp.next().toString();
 					//Delete the tuple with the url that contains the service url 
 					if(result.contains((CharSequence)WsdlURL)){
 						StringTokenizer Tok = new StringTokenizer(result);
 						String wsdlOp = Tok.nextToken("=");
 						wsdlOp = Tok.nextToken();
 						wsdlOp = wsdlOp.replace(")", "");
 						System.out.println("wsdlOP : "+wsdlOp);
 					
 						//Delete the tuple with the url that contains the service url 
 						String updateWSDLOp= 
 			        	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
 			            "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
 			            "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
 			            "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
 			            "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
 			            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
 			            "prefix mind: <"+urlProcessDoc+"#>" +
 			            "delete {?x ?y ?z}" +
 			            "where" +
 			            "{?x ?y "+wsdlOp +
 			            " filter ( ?y = grounding:operation " +
 			                  ")" +
 			            "?x ?y ?z}";
 		       		
 						// Execute the query 
 						QuerySolution querysol=new QuerySolutionMap();
 						UpdateAction.parseExecute(updateWSDLOp, m, querysol);
 					
 					}// end if 
 				}//end for 
 			}// end if 
         }// DeleteWSDLOperation
         
         
         
         /**
          * DeleteProcessOutputs
          * @param urlProcessDoc
          * @param processname
          * @param WsdlURL
          * @param m
          * @return
          */
         public void DeleteProcessOutputs(String urlProcessDoc, String processname, String WsdlURL, OntModel m) {
             
        	    // Query to get the service outputs related with the process
 			String queryStringServiceOutputs =
 				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
 						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
 						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
 						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
 						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
 						+ "prefix mind: <"+urlProcessDoc+"#>" 
 						+ "select ?x " 
 						+ "where {" 
 						+ "mind:"+processname+" process:hasOutput ?x " 
 						+ "}";

 			Query queryServiceOutputs = QueryFactory.create(queryStringServiceOutputs);

     		// Execute the query 
 			QueryExecution qeServiceOutputs = QueryExecutionFactory.create(queryServiceOutputs, m);
 			ResultSet resultsServiceOutputs = qeServiceOutputs.execSelect();
 		    // For each service output, all the tuples related with the process are deleted 
 			if (resultsServiceOutputs != null) {
 				for (Iterator j = resultsServiceOutputs; resultsServiceOutputs.hasNext();) {
 					// To take the name of the output
 					String result = resultsServiceOutputs.next().toString();
 					StringTokenizer Tok = new StringTokenizer(result);
 					String processOutputResult = Tok.nextToken("#");
 					String processOutputName = Tok.nextToken();
 					processOutputName = processOutputName.replace(">", "");
 					processOutputName = processOutputName.replace(")", "");
 					
 					if (DEBUG) {
 						System.out.println("Process Output: " + processOutputName);
 					}
 				    
 					// Delete the output tuple related with the service process
 					String updateOutput= 
 			        	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
 			            "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
 			            "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
 			            "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
 			            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
 			            "prefix mind: <"+urlProcessDoc+"#>" +
 			            "delete {?x ?y ?z}" +
 			            "where" +
 			            "{mind:" +processOutputName+" ?y ?z" +
 			            " filter ( ?y = rdfs:label " +
 			                   "|| ?y = process:parameterType " +
 			                   "|| ?z = process:Output " +
 			                   ")" +
 			            "?x ?y ?z}";
 		       		
 		 	        
 					//Delete the output tuple related with the service grounding
 					String updateGroundOutput= 
 			        	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
 			            "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
 			            "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
 			            "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
 			            "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
 			            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
 			            "prefix mind: <"+urlProcessDoc+"#>" +
 			            "delete {?x ?y ?z}" +
 			            "where" +
 			            "{?x ?y mind:"+processOutputName +
 			            " filter ( ?y = grounding:owlsParameter " +
 			                   ")" +
 			            "?x ?y ?z}";
 		       		
 					
 		       		// Execute the query 
 					QuerySolution querysol=new QuerySolutionMap();
 		 	        UpdateAction.parseExecute(updateOutput, m, querysol);
 		 	        UpdateAction.parseExecute(updateGroundOutput, m, querysol);
 		 	        

 				}//end for 
 			}//end if	
        }//end DeleteProcessOutputs
        
         
         /**
          * GetServiceProfile
          * @param String urlProcessDoc
          * @param String processname
          * @param OntModel m
          * @return 
          */ 
         public String GetServiceProfile(String urlProcessDoc, String processname, OntModel m){
         	//Query to get the service profile
 			String queryStringProfile =
 				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
 						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
 						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
 						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
 						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
 						+ "prefix mind: <"+urlProcessDoc+"#>" 
 						+ "select ?x " 
 						+ "where {" 
 						+ "mind:"+processname+" service:describes ?x " 
 						+ "}";

 			Query queryProfile = QueryFactory.create(queryStringProfile);

     		// Execute the query and obtain results
 			QueryExecution qeProfile = QueryExecutionFactory.create(queryProfile, m);
 			ResultSet resultProfile = qeProfile.execSelect();
 		    // To get the name of the profile
 			String result = resultProfile.next().toString();
 			StringTokenizer Tok = new StringTokenizer(result);
 			String processProfileResult = Tok.nextToken("=");
 			String processProfile = Tok.nextToken();
 			processProfile = processProfile.replace(")", "");
 			if (DEBUG) {
 				System.out.println("Process Profile: " + processProfile);
 			}
         	
 			return(processProfile);
 			
         }//end GetServiceProfile 
         
         /**
          * GetServiceGrounding
          * @param String urlProcessDoc
          * @param String processname
          * @param String processProfile
          * @param OntModel m
          */ 
         public String GetServiceGrounding(String urlProcessDoc, String processname, String processProfile, OntModel m){
         	
         	// Query to get the service Grounding
 			String queryStringProcessGround =
 				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
 						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
 						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
 						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
 						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
 						+ "prefix mind: <"+urlProcessDoc+"#>" 
 						+ "select ?x " 
 						+ "where {" 
 						+ "?x service:supportedBy "+processProfile 
 						+ "}";

 			Query queryProcessGround = QueryFactory.create(queryStringProcessGround);

     		// Execute the query and obtain results
 			QueryExecution qeProcessGround = QueryExecutionFactory.create(queryProcessGround, m);
 			ResultSet resultProcessGround = qeProcessGround.execSelect();
 			// To take the grounding
 			String result = resultProcessGround.next().toString();
 			StringTokenizer Tok = new StringTokenizer(result);
 			String processGroundResult = Tok.nextToken("=");
 			String processGround = Tok.nextToken();
 			processGround = processGround.replace(")", "");
 			
 			if (DEBUG) {
 				System.out.println("Process Ground: " + processGround);
 			}
 			
 			return(processGround);
 			
         } //end GetServiceGrounding
         	
         	
         
         /**
          * GetServiceWSDLGrounding
          * @param String urlProcessDoc
          * @param String processGround
          * @param OntModel m
          */ 
         public String GetServiceWSDLGrounding(String urlProcessDoc, String processGround, OntModel m){
 			
         	// Query to get the service WSDLGrounding
 			String queryStringProcessGroundWSDL =
 				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
 						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
 						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
 						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
 						+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" 
 						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
 						+ "prefix mind: <"+urlProcessDoc+"#>" 
 						+ "select ?x " 
 						+ "where {" 
 						+ processGround +" grounding:hasAtomicProcessGrounding ?x" 
 						+ "}";

 			Query queryProcessGroundWSDL = QueryFactory.create(queryStringProcessGroundWSDL);

     		// Execute the query and obtain results
 			QueryExecution qeProcessGroundWSDL = QueryExecutionFactory.create(queryProcessGroundWSDL, m);
 			ResultSet resultProcessGroundWSDL = qeProcessGroundWSDL.execSelect();
 			
 			// To take the WSDL Grounding		
 			String result = resultProcessGroundWSDL.next().toString();
 			StringTokenizer Tok = new StringTokenizer(result);
 			String processGroundWSDLResult = Tok.nextToken("=");
 			String processGroundWSDL = Tok.nextToken();
 			processGroundWSDL = processGroundWSDL.replace(")", "");
 			
 			if (DEBUG) {
 				System.out.println("Process Ground WSDL: " + processGroundWSDL);
 			}
 			return(processGroundWSDL);
 			
         }//end GetServiceWSDLGrounding
         
         	
         
         /**
          * GetServiceWSDLGroundingDoc
          * @param String urlProcessDoc
          * @param String processGroundWSDL
          * @param OntModel m
          */ 
         String GetServiceWSDLGroundingDoc(String urlProcessDoc, String processGroundWSDL, OntModel m){
 			
         	// Query to get the service WSDLGrounding Document
 			String queryStringDocWSDL =
 				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
 						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
 						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
 						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
 						+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" 
 						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
 						+ "prefix mind: <"+urlProcessDoc+"#>" 
 						+ "select ?x " 
 						+ "where {" 
 						+ processGroundWSDL +" grounding:wsdlDocument ?x" 
 						+ "}";

 			Query queryDocWSDL = QueryFactory.create(queryStringDocWSDL);

     		// Execute the query and obtain results
 			QueryExecution qeDocWSDL = QueryExecutionFactory.create(queryDocWSDL, m);
 			ResultSet resultDocWSDL = qeDocWSDL.execSelect();
 			
 			
 			// To take the WSDL Grounding Document
 			String result = resultDocWSDL.next().toString();
 			StringTokenizer Tok = new StringTokenizer(result);
 			String DocWSDLResult = Tok.nextToken("=");
 			String DocWSDL = Tok.nextToken();
 			DocWSDL = DocWSDL.replace(")", "");
 			
 			if (DEBUG) {
 				System.out.println("DOC WSDL: " + DocWSDL);
 			}
 			// To take the URL of the WSDL Document
 			Tok = new StringTokenizer(DocWSDL);
 			String DocURL = Tok.nextToken("?");
 			DocURL = DocURL.replace("\"", " ");
 			if (DEBUG) {
 				System.out.println("DocURL: " + DocURL);
 			}
 			String URL = null;
 			URL = DocURL;
 			String WsdlURL = "";
 			Tok = new StringTokenizer(URL, ":8080/");
 			String DocPartURL = Tok.nextToken();
 			System.out.println("DocPartURL"+DocPartURL);	
 			
 			return (DocPartURL);
         }//end GetServiceWSDLGroundingDoc
         	
         	
         /**
          * DeleteProcessGrounding
          * @param String urlProcessDoc
          * @param String processname
          * @param String processGround
          * @param String processGroundWSDL
          * @param OntModel m
          */ 
 		void DeleteProcessGrounding(String urlProcessDoc, String processname, String processGround, String processGroundWSDL, OntModel m){
 				
 			//Deletes Query Strings
 			String updateGround= 
 	        	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
 	            "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
 	            "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
 	            "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
 	            "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
 	            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
 	            "prefix mind: <"+urlProcessDoc+"#>" +
 	            "delete {?x ?y ?z .}" +
 	            "where" +
 	            "{"+processGround+" ?y ?z ." +
 	            " filter ( ?y = grounding:owlsProcess" + 
 	                   "|| ?y = grounding:hasAtomicProcessGrounding " +
                        "|| ?y = service:supportedBy " +
                        "|| ?z = grounding:WsdlGrounding " +
 	                   ")" +
 	            "?x ?y ?z .}";
 		
 			
 			String updateGroundWSDL= 
 			        	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
 			            "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
 			            "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
 			            "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
 			            "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
 			            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
 			            "prefix mind: <"+urlProcessDoc+"#>" +
 			            "delete {?x ?y ?z .}" +
 			            "where" +
 			            "{"+processGroundWSDL+" ?y ?z ." +
 			            " filter ( ?y = grounding:owlsProcess" + 
 			                   "|| ?y = grounding:wsdlDocument " +
 		                       "|| ?y = grounding:wsdlInput " +
 		                       "|| ?y = grounding:wsdlInputMessage " +
 		                       "|| ?y = grounding:wsdlOperation " +
 		                       "|| ?y = grounding:wsdlOutput " + 
 		                       "|| ?y = grounding:wsdlOutputMessage "+
 		                       "|| ?z = grounding:WsdlAtomicProcessGrounding "+
 			                   ")" +
 			            "?x ?y ?z .} ";
                         
 			
 			String updateGroundSupportsProperty= 
 	        	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
 	            "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
 	            "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
 	            "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
 	            "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
 	            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
 	            "prefix mind: <"+urlProcessDoc+"#>" +
 	            "delete {?x ?y ?z}" +
 	            "where" +
 	            "{?x ?y " + processGround +
 	            " filter ( ?y = service:supports" +
 	                   ")" +
 	            "?x ?y ?z}";
 			
 		       	// Execute the deletes
 		       	QuerySolution querysol=new QuerySolutionMap();
 		 	    UpdateAction.parseExecute(updateGroundWSDL, m, querysol);
 		 	    UpdateAction.parseExecute(updateGround, m, querysol);
 		 	    UpdateAction.parseExecute(updateGroundSupportsProperty, m, querysol);
 			
         	 
         }//end DeleteProcessGrounding
         
 		
 		
 		/**
          * DeleteProcess
          * @param String urlProcessDoc
          * @param String processname
          * @param OntModel m
          */ 
         public void DeleteProcess(String urlProcessDoc, String processname, OntModel m) {
             
    					//Delete the process general description
    					String updateProcess= 
    			        	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
    			            "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
    			            "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
    			            "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
    			            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
    			            "prefix mind: <"+urlProcessDoc+"#>" +
    			            "delete {?x ?y ?z}" +
    			            "where" +
    			            "{mind:" +processname+" ?y ?z" +
    			            " filter ( ?y = process:hasOutput " +
    			                   "|| ?y = process:hasInput " +
    			                   "|| ?y = service:describes " +
    			                   "|| ?z = process:AtomicProcess " +
    			                   ")" +
    			            "?x ?y ?z}";
    					
    					//Delete the profile property where the process appears
    					String updateDescribedByProperty= 
    			        	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
    			            "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
    			            "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
    			            "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
    			            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
    			            "prefix mind: <"+urlProcessDoc+"#>" +
    			            "delete {?x ?y ?z}" +
    			            "where" +
    			            "{?x ?y mind:" +processname +
    			            " filter ( ?y = service:describedBy " +
    			                   ")" +
    			            "?x ?y ?z}";
    		       		
    		       		// Execute the query 
    		       		QuerySolution querysol=new QuerySolutionMap();
    		 	        UpdateAction.parseExecute(updateProcess, m, querysol);
    		 	        UpdateAction.parseExecute(updateDescribedByProperty, m, querysol);
    					
         }// DeleteProcess 
         
         
         
         public OntModelSpec getModelSpec(ModelMaker maker) {
        		// create a spec for the new ont model that will use no inference over
        		// models made by the given maker (which is where we get the persistent models
        		// from)
        		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
        		spec.setImportModelMaker(maker);

        		return spec;	
         }
    }
    