
/**
 * ModifyProcessSkeleton.java
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*; 
import java.io.*;

import persistence.DataBaseInterface;


    
    
    /**
     *  ModifyProcessSkeleton java skeleton for the axisService
     */
    public class ModifyProcessSkeleton{
        

    	public static final Boolean DEBUG = true;

    	// database connection parameters, with defaults
    	private static String s_dbURL;
    	private static String s_dbUser;
    	private static String s_dbPw;
    	private static String s_dbType;
    	private static String s_dbDriver;
    	
    	
    	
        /**
         * ModifyProcess
         * @param ModifyProcess contains three elements: service implementation ID (is a string: 
         * serviceprofile@servicenumdidagent), service model (is a string: urlprocess#processname),
         * service grounding (currently is not in use) and agent ID (is a string).
         * @return ModifyProcessResponse contains return which indicates if an error occurs (1:OK,
         * otherwise 0).
         */
        public wtp.ModifyProcessResponse ModifyProcess(
			wtp.ModifyProcess modifyProcess) {
		wtp.ModifyProcessResponse response = new wtp.ModifyProcessResponse();

		if (DEBUG) {
			System.out.println("ModifyProcess Service :");
			System.out.println("***ServiceImplementationID..."+ modifyProcess.getServiceImplementationID());
			System.out.println("***ServiceModel... "+ modifyProcess.getServiceModel());
			System.out.println("***ServiceGrounding... "+ modifyProcess.getServiceGrounding());
			System.out.println("***AgentID... " + modifyProcess.getAgentID());
		}

		persistence.DataBaseInterface thomasBD = new DataBaseInterface();
		
		if (thomasBD.CheckServiceProcessID(modifyProcess.getServiceImplementationID()) && thomasBD.CheckServiceProvider(modifyProcess.getAgentID(), modifyProcess.getServiceImplementationID())) {
			
			/////////////
			////JENA/////
			/////////////
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

			// Query to get the reference to the set of service process that the
			// service profile has (...owl#...)
			String queryStringService = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
					+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
					+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
					+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
					+ "select ?x "
					+ "where {"
					+ "      ?x service:describedBy <"
					+ modifyProcess.getServiceModel() + ">" + "      }";

			Query queryService = QueryFactory.create(queryStringService);

			if (DEBUG) {
				System.out.println(queryService.toString());
			}

			// Execute the query and obtain results
			QueryExecution qeService = QueryExecutionFactory.create(
					queryService, m);
			ResultSet resultService = qeService.execSelect();

			String result = resultService.next().toString();
			if (DEBUG) {
				System.out.println("Service: " + result);
			}
			StringTokenizer Tok = new StringTokenizer(result);
			String service = Tok.nextToken("<");
			service = Tok.nextToken(">");
			service = service.replace("<", "");

			if (DEBUG) {
				System.out.println("Service: " + service);
			}

			// Query to get the reference to the set of service process that the
			// service profile has (...owl#...)
			String queryStringProfile = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
					+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
					+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
					+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
					+ "select ?p "
					+ "where {"
					+ "<"
					+ service
					+ ">"
					+ "service:presents ?p" + "      }";

			Query queryProfile = QueryFactory.create(queryStringProfile);

			if (DEBUG) {
				System.out.println(queryProfile.toString());
			}

			// Execute the query and obtain results
			QueryExecution qeProfile = QueryExecutionFactory.create(queryProfile, m);
			ResultSet resultProfile = qeProfile.execSelect();

			result = resultProfile.next().toString();
			if (DEBUG) {
				System.out.println("Service profile: " + result);
			}
			Tok = new StringTokenizer(result);

			String profile = Tok.nextToken("<");
			profile = Tok.nextToken("#");
			profile = profile.replace("<", "");

			if (DEBUG) {
				System.out.println("Service profile: " + profile);
			}

			String process = modifyProcess.getServiceModel();
			Tok = new StringTokenizer(process);
			String urlprocess = Tok.nextToken("#");
			String processname = Tok.nextToken();

			if (DEBUG) {
				System.out.println("URL process: " + urlprocess);
				System.out.println("File to load ... " + urlprocess);
				System.out.println("processname: " + processname);
			}

			String processProfile = GetServiceProfile(urlprocess, processname,
					m);
			System.out.println("Profile " + processProfile);
			String processGround = GetServiceGrounding(urlprocess, processname,	processProfile, m);
			String processGroundWSDL = GetServiceWSDLGrounding(urlprocess,processGround, m);
			String WsdlURL = GetServiceWSDLGroundingDoc(urlprocess,	processGroundWSDL, m);

			m.write(System.out, "N3");
			DeleteWSDLMessagePart(urlprocess, WsdlURL, m);
			DeleteWSDLOperation(urlprocess, WsdlURL, m);
			DeleteWSDLPortType(urlprocess, WsdlURL, m);
			DeleteProcessInputs(urlprocess, processname, WsdlURL, m);
			DeleteProcessOutputs(urlprocess, processname, WsdlURL, m);
			DeleteProcessGrounding(urlprocess, processname, processGround,processGroundWSDL, m);
			DeleteProcess(urlprocess, processname, m);
			m.write(System.out, "N3");

			// Load the new service process
	
			m.read(urlprocess);
			m.commit();

			if (DEBUG) {
				System.out.println("DB after load new process ... ");
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
			
			response.set_return(1);
			
		} else {
			
			response.set_return(0);
		}

		return (response);

	}// end ModifyProcess

	/**
	 * DeleteProcessInputs
	 * 
	 * @param String
	 *            urlprocess
	 * @param String
	 *            processname
	 * @param String
	 *            WsdlURL
	 * @param OntModel
	 *            m
	 */
	public void DeleteProcessInputs(String urlprocess, String processname,
			String WsdlURL, OntModel m) {

		// Query to get the service inputs tuples related with the service
		// process
		String queryStringServiceInputs = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
				+ "prefix mind: <"
				+ urlprocess
				+ "#>"
				+ "select ?x "
				+ " where {"
				+ "mind:"
				+ processname
				+ " process:hasInput ?x "
				+ "}";

		Query queryServiceInputs = QueryFactory
				.create(queryStringServiceInputs);

		// Execute the query and obtain the service process inputs
		QueryExecution qeServiceInputs = QueryExecutionFactory.create(
				queryServiceInputs, m);
		ResultSet resultsServiceInputs = qeServiceInputs.execSelect();
		// For each input, all the tuples related with the process are deleted
		if (resultsServiceInputs != null) {
			for (Iterator j = resultsServiceInputs; resultsServiceInputs
					.hasNext();) {
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
				String updateInput = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
						+ "prefix mind: <"
						+ urlprocess
						+ "#>"
						+ "delete {?x ?y ?z}"
						+ " where"
						+ "{mind:"
						+ processInputName
						+ " ?y ?z"
						+ " filter ( ?y = rdfs:label "
						+ "|| ?y = process:parameterType "
						+ "|| ?z = process:Input " + ")" + "?x ?y ?z}";

				//Delete the output tuple related with the service grounding
				String updateGroundInput = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
						+ "prefix mind: <"
						+ urlprocess
						+ "#>"
						+ " delete {?x ?y ?z}"
						+ " where"
						+ "{?x ?y mind:"
						+ processInputName
						+ " filter ( ?y = grounding:owlsParameter "
						+ ")"
						+ "?x ?y ?z}";

				// Execute the query 
				QuerySolution querysol = new QuerySolutionMap();
				UpdateAction.parseExecute(updateInput, m, querysol);
				UpdateAction.parseExecute(updateGroundInput, m, querysol);

			}//end for 
		}//end if	

	}// end Delete inputs

	/**
	 * DeleteWSDLMessagePart
	 * @param String urlprocess
	 * @param String WsdlURL
	 * @param OntModel m
	 */
	public void DeleteWSDLMessagePart(String urlprocess, String WsdlURL,
			OntModel m) {

		// Query to get the service input and output tuples related with the property grounding:wsdlMessagePart
		String queryStringWSDLMsgMap = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
				+ "prefix mind: <"
				+ urlprocess
				+ "#>"
				+ " select ?y "
				+ " where {" + "?x grounding:wsdlMessagePart ?y " + "}";

		Query queryWSDLMsgMap = QueryFactory.create(queryStringWSDLMsgMap);
		WsdlURL = WsdlURL.replace(" ", "");

		// Execute the query and obtain the WsdlMessageMap value  
		QueryExecution qeWSDLMsgMap = QueryExecutionFactory.create(
				queryWSDLMsgMap, m);
		ResultSet resultsqeWSDLMsgMap = qeWSDLMsgMap.execSelect();

		// For each WsdlMessageMap, all the tuples related with the service are deleted
		if (resultsqeWSDLMsgMap != null) {

			for (Iterator j = resultsqeWSDLMsgMap; resultsqeWSDLMsgMap
					.hasNext();) {

				// To take only the WsdlMessageMap 
				String result = resultsqeWSDLMsgMap.next().toString();
				// If the url related with the WsdlMessageMap property contains the url of the service,
				// the tuple should be deleted
				if (result.contains((CharSequence) WsdlURL)) {
					StringTokenizer Tok = new StringTokenizer(result);
					String wsdlMsg = Tok.nextToken("=");
					wsdlMsg = Tok.nextToken();
					Tok = new StringTokenizer(wsdlMsg);
					wsdlMsg = Tok.nextToken();
					System.out.println("wsdlMSG : " + wsdlMsg);

					// Delete the WsdlMessageMap tuple related with the service grounding
					String updateWSDLMessageMap = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
							+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
							+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
							+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
							+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
							+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
							+ "prefix mind: <"
							+ urlprocess
							+ "#>"
							+ " delete {?x ?y ?z}"
							+ " where"
							+ "{?x ?y "
							+ wsdlMsg
							+ " filter ( ?y = grounding:wsdlMessagePart "
							+ ")"
							+ "?x ?y ?z}";

					// Execute the query 
					QuerySolution querysol = new QuerySolutionMap();
					UpdateAction
							.parseExecute(updateWSDLMessageMap, m, querysol);

				}// end if 
			}// end for
		}// end if
	}// DeleteWSDLMessagePart

	/**
	 * DeleteWSDLPortType
	 * @param String urlprocess
	 * @param String WsdlURL
	 * @param OntModel m
	 */
	public void DeleteWSDLPortType(String urlprocess, String WsdlURL, OntModel m) {

		// Query to get the service tuples related with the property grounding:portType 
		String queryStringWSDLPort = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
				+ "prefix mind: <"
				+ urlprocess
				+ "#>"
				+ " select ?y "
				+ " where {" + "?x grounding:portType ?y " + "}";

		Query queryWSDLPort = QueryFactory.create(queryStringWSDLPort);

		// Execute the query 
		QueryExecution qeWSDLPort = QueryExecutionFactory.create(queryWSDLPort,
				m);
		ResultSet resultsqeWSDLPort = qeWSDLPort.execSelect();
		WsdlURL = WsdlURL.replace(" ", "");

		// For each input, all the tuples with the property grounding:portType related with the service are deleted
		if (resultsqeWSDLPort != null) {

			for (Iterator j = resultsqeWSDLPort; resultsqeWSDLPort.hasNext();) {

				// To take only the url associated with the property grounding:portType
				String result = resultsqeWSDLPort.next().toString();
				// If the url contains the url of the service, this tuple should be delete
				if (result.contains((CharSequence) WsdlURL)) {
					StringTokenizer Tok = new StringTokenizer(result);
					String wsdlPort = Tok.nextToken("=");
					wsdlPort = Tok.nextToken();
					wsdlPort = wsdlPort.replace(")", "");

					System.out.println("wsdlPort : " + wsdlPort);
					//Delete the tuple 
					String updateWSDLPort = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
							+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
							+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
							+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
							+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
							+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
							+ "prefix mind: <"
							+ urlprocess
							+ "#>"
							+ " delete {?x ?y ?z}"
							+ " where"
							+ "{?x ?y "
							+ wsdlPort
							+ " filter ( ?y = grounding:portType "
							+ ")" + "?x ?y ?z}";

					// Execute the query 
					QuerySolution querysol = new QuerySolutionMap();
					UpdateAction.parseExecute(updateWSDLPort, m, querysol);

				}// end if 
			}// end for
		}// end if
	}// DeleteWSDLPortType

	/**
	 * DeleteWSDLOperation
	 * @param String urlprocess
	 * @param String WsdlURL
	 * @param OntModel m
	 */
	public void DeleteWSDLOperation(String urlprocess, String WsdlURL,
			OntModel m) {

		// Query to get the service tuples related with the property grounding:operation
		String queryStringWSDLOp = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
				+ "prefix mind: <"
				+ urlprocess
				+ "#>"
				+ " select ?y "
				+ " where {" + "?x grounding:operation ?y " + "}";

		Query queryWSDLOp = QueryFactory.create(queryStringWSDLOp);

		// Execute the query 
		QueryExecution qeWSDLOp = QueryExecutionFactory.create(queryWSDLOp, m);
		ResultSet resultsqeWSDLOp = qeWSDLOp.execSelect();
		WsdlURL = WsdlURL.replace(" ", "");

		// For each tuple, the tuples with the property grounding:operation related 
		// with the service grounding are deleted
		if (resultsqeWSDLOp != null) {

			for (Iterator j = resultsqeWSDLOp; resultsqeWSDLOp.hasNext();) {

				//To take only the url of the property grounding:operation
				String result = resultsqeWSDLOp.next().toString();
				//Delete the tuple with the url that contains the service url 
				if (result.contains((CharSequence) WsdlURL)) {
					StringTokenizer Tok = new StringTokenizer(result);
					String wsdlOp = Tok.nextToken("=");
					wsdlOp = Tok.nextToken();
					wsdlOp = wsdlOp.replace(")", "");
					System.out.println("wsdlOP : " + wsdlOp);

					//Delete the tuple with the url that contains the service url 
					String updateWSDLOp = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
							+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
							+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
							+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
							+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
							+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
							+ "prefix mind: <"
							+ urlprocess
							+ "#>"
							+ " delete {?x ?y ?z}"
							+ " where"
							+ "{?x ?y "
							+ wsdlOp
							+ " filter ( ?y = grounding:operation "
							+ ")" + "?x ?y ?z}";

					// Execute the query 
					QuerySolution querysol = new QuerySolutionMap();
					UpdateAction.parseExecute(updateWSDLOp, m, querysol);

				}// end if 
			}//end for 
		}// end if 
	}// DeleteWSDLOperation

	/**
	 * DeleteProcessOutputs
	 * @param String urlprocess
	 * @param String processname
	 * @param String WsdlURL
	 * @param OntModel m
	 */
	public void DeleteProcessOutputs(String urlprocess, String processname,
			String WsdlURL, OntModel m) {

		// Query to get the service outputs related with the process
		String queryStringServiceOutputs = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
				+ "prefix mind: <"
				+ urlprocess
				+ "#>"
				+ "select ?x "
				+ "where {"
				+ "mind:"
				+ processname
				+ " process:hasOutput ?x "
				+ "}";

		Query queryServiceOutputs = QueryFactory
				.create(queryStringServiceOutputs);

		// Execute the query 
		QueryExecution qeServiceOutputs = QueryExecutionFactory.create(
				queryServiceOutputs, m);
		ResultSet resultsServiceOutputs = qeServiceOutputs.execSelect();
		// For each service output, all the tuples related with the process are deleted 
		if (resultsServiceOutputs != null) {
			for (Iterator j = resultsServiceOutputs; resultsServiceOutputs
					.hasNext();) {
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
				String updateOutput = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
						+ "prefix mind: <"
						+ urlprocess
						+ "#>"
						+ " delete {?x ?y ?z}"
						+ " where"
						+ "{mind:"
						+ processOutputName
						+ " ?y ?z"
						+ " filter ( ?y = rdfs:label "
						+ "|| ?y = process:parameterType "
						+ "|| ?z = process:Output " + ")" + "?x ?y ?z}";

				//Delete the output tuple related with the service grounding
				String updateGroundOutput = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
						+ "prefix mind: <"
						+ urlprocess
						+ "#>"
						+ " delete {?x ?y ?z}"
						+ " where"
						+ "{?x ?y mind:"
						+ processOutputName
						+ " filter ( ?y = grounding:owlsParameter "
						+ ")"
						+ "?x ?y ?z}";

				// Execute the query 
				QuerySolution querysol = new QuerySolutionMap();
				UpdateAction.parseExecute(updateOutput, m, querysol);
				UpdateAction.parseExecute(updateGroundOutput, m, querysol);

			}//end for 
		}//end if	
	}//end DeleteProcessOutputs

	/**
	 * GetServiceProfile
	 * @param String urlprocess
	 * @param String processname
	 * @param OntModel m
	 */
	public String GetServiceProfile(String urlprocess, String processname,
			OntModel m) {
		//Query to get the service profile
		String queryStringProfile = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
				+ "prefix mind: <"
				+ urlprocess
				+ "#>"
				+ " select ?x "
				+ " where {"
				+ " mind:"
				+ processname
				+ " service:describes ?x " + "}";

		Query queryProfile = QueryFactory.create(queryStringProfile);

		// Execute the query and obtain results
		QueryExecution qeProfile = QueryExecutionFactory
				.create(queryProfile, m);
		ResultSet resultProfile = qeProfile.execSelect();
		// To get the name of the profile
		String result = resultProfile.next().toString();
		StringTokenizer Tok = new StringTokenizer(result);
		String processProfileResult = Tok.nextToken("=");
		System.out.println("processProfileResult " + processProfileResult);
		String processProfile = Tok.nextToken();
		System.out.println("processProfile " + processProfile);
		Tok = new StringTokenizer(processProfile);
		processProfile = Tok.nextToken();
		if (DEBUG) {
			System.out.println("Process Profile: " + processProfile);
		}

		return (processProfile);

	}//end GetServiceProfile

	/**
	 * GetServiceGrounding
	 * @param String urlprocess
	 * @param String processname
	 * @param String processProfile
	 * @param OntModel m
	 */
	public String GetServiceGrounding(String urlprocess, String processname,
			String processProfile, OntModel m) {
		System.out.println("en GetServiceGrounding");
		// Query to get the service Grounding
		String queryStringProcessGround = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
				+ "prefix mind: <"
				+ urlprocess
				+ "#>"
				+ " select ?x "
				+ " where {" + "?x service:supportedBy " + processProfile + "}";
		System.out.println("consulta: " + queryStringProcessGround);

		Query queryProcessGround = QueryFactory
				.create(queryStringProcessGround);

		// Execute the query and obtain results
		QueryExecution qeProcessGround = QueryExecutionFactory.create(
				queryProcessGround, m);
		ResultSet resultProcessGround = qeProcessGround.execSelect();
		// To take the grounding
		String result = resultProcessGround.next().toString();
		StringTokenizer Tok = new StringTokenizer(result);
		String processGroundResult = Tok.nextToken("=");
		String processGround = Tok.nextToken();
		Tok = new StringTokenizer(processGround);
		processGround = Tok.nextToken();

		if (DEBUG) {
			System.out.println("Process Ground: " + processGround);
		}
		System.out.println("Fin en GetServiceGrounding");
		return (processGround);

	} //end GetServiceGrounding

	/**
	 * GetServiceWSDLGrounding
	 * @param String urlprocess
	 * @param String processGround
	 * @param OntModel m
	 */
	public String GetServiceWSDLGrounding(String urlprocess,
			String processGround, OntModel m) {

		// Query to get the service WSDLGrounding
		String queryStringProcessGroundWSDL = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
				+ "prefix mind: <"
				+ urlprocess
				+ "#>"
				+ " select ?x "
				+ " where {"
				+ processGround
				+ " grounding:hasAtomicProcessGrounding ?x" + "}";

		Query queryProcessGroundWSDL = QueryFactory
				.create(queryStringProcessGroundWSDL);

		// Execute the query and obtain results
		QueryExecution qeProcessGroundWSDL = QueryExecutionFactory.create(
				queryProcessGroundWSDL, m);
		ResultSet resultProcessGroundWSDL = qeProcessGroundWSDL.execSelect();

		// To take the WSDL Grounding		
		String result = resultProcessGroundWSDL.next().toString();
		StringTokenizer Tok = new StringTokenizer(result);
		String processGroundWSDLResult = Tok.nextToken("=");
		String processGroundWSDL = Tok.nextToken();
		Tok = new StringTokenizer(processGroundWSDL);
		processGroundWSDL = Tok.nextToken();

		if (DEBUG) {
			System.out.println("Process Ground WSDL: " + processGroundWSDL);
		}
		return (processGroundWSDL);

	}//end GetServiceWSDLGrounding

	/**
	 * GetServiceWSDLGroundingDoc
	 * @param String urlprocess
	 * @param String processGroundWSDL
	 * @param OntModel m
	 */
	String GetServiceWSDLGroundingDoc(String urlprocess,
			String processGroundWSDL, OntModel m) {

		// Query to get the service WSDLGrounding Document
		String queryStringDocWSDL = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
				+ "prefix mind: <"
				+ urlprocess
				+ "#>"
				+ " select ?x "
				+ " where {"
				+ processGroundWSDL
				+ " grounding:wsdlDocument ?x"
				+ "}";

		Query queryDocWSDL = QueryFactory.create(queryStringDocWSDL);

		// Execute the query and obtain results
		QueryExecution qeDocWSDL = QueryExecutionFactory
				.create(queryDocWSDL, m);
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
		System.out.println("DocPartURL" + DocPartURL);

		return (DocPartURL);
	}//end GetServiceWSDLGroundingDoc

	/**
	 * DeleteProcessGrounding
	 * @param String urlprocess
	 * @param String processname
	 * @param String processGround
	 * @param String processGroundWSDL
	 * @param OntModel m
	 */
	void DeleteProcessGrounding(String urlprocess, String processname,
			String processGround, String processGroundWSDL, OntModel m) {

		//Deletes Query Strings
		String updateGround = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "prefix mind: <" + urlprocess + "#>" + " delete {?x ?y ?z .}"
				+ " where" + "{" + processGround + " ?y ?z ."
				+ " filter ( ?y = grounding:owlsProcess"
				+ "|| ?y = grounding:hasAtomicProcessGrounding "
				+ "|| ?y = service:supportedBy "
				+ "|| ?z = grounding:WsdlGrounding " + ")" + "?x ?y ?z .}";

		String updateGroundWSDL = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "prefix mind: <"
				+ urlprocess
				+ "#>"
				+ " delete {?x ?y ?z .}"
				+ " where"
				+ "{"
				+ processGroundWSDL
				+ " ?y ?z ."
				+ " filter ( ?y = grounding:owlsProcess"
				+ "|| ?y = grounding:wsdlDocument "
				+ "|| ?y = grounding:wsdlInput "
				+ "|| ?y = grounding:wsdlInputMessage "
				+ "|| ?y = grounding:wsdlOperation "
				+ "|| ?y = grounding:wsdlOutput "
				+ "|| ?y = grounding:wsdlOutputMessage "
				+ "|| ?z = grounding:WsdlAtomicProcessGrounding "
				+ ")"
				+ "?x ?y ?z .} ";

		String updateGroundSupportsProperty = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "prefix mind: <"
				+ urlprocess
				+ "#>"
				+ " delete {?x ?y ?z}"
				+ " where"
				+ "{?x ?y "
				+ processGround
				+ " filter ( ?y = service:supports" + ")" + "?x ?y ?z}";

		// Execute the deletes
		QuerySolution querysol = new QuerySolutionMap();
		UpdateAction.parseExecute(updateGroundWSDL, m, querysol);
		UpdateAction.parseExecute(updateGround, m, querysol);
		//UpdateAction.parseExecute(updateGroundParameters, m, querysol);
		UpdateAction.parseExecute(updateGroundSupportsProperty, m, querysol);

	}//end DeleteProcessGrounding

	/**
	 * DeleteProcess
	 * @param String urlprocess
	 * @param String processname
	 * @param OntModel m
	 */
	public void DeleteProcess(String urlprocess, String processname, OntModel m) {

		//Delete the process general description
		String updateProcess = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "prefix mind: <"
				+ urlprocess
				+ "#>"
				+ " delete {?x ?y ?z}"
				+ " where"
				+ "{mind:"
				+ processname
				+ " ?y ?z"
				+ " filter ( ?y = process:hasOutput "
				+ "|| ?y = process:hasInput "
				+ "|| ?y = service:describes "
				+ "|| ?z = process:AtomicProcess " + ")" + "?x ?y ?z}";

		//Delete the profile property where the process appears
		String updateDescribedByProperty = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "prefix mind: <"
				+ urlprocess
				+ "#>"
				+ " delete {?x ?y ?z}"
				+ " where"
				+ "{?x ?y mind:"
				+ processname
				+ " filter ( ?y = service:describedBy " + ")" + "?x ?y ?z}";

		// Execute the query 
		QuerySolution querysol = new QuerySolutionMap();
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
