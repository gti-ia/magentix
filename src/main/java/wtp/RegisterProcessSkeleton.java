 /**
 * RegisterProcessSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.io.*;
import java.net.*;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.db.*;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;

import java.util.*;

import persistence.DataBaseInterface;


/**
 * RegisterProcessSkeleton java skeleton for the axisService
 */
public class RegisterProcessSkeleton {


	public static final Boolean DEBUG = true;


	// database connection parameters, with defaults
	private static String s_dbURL;
	private static String s_dbUser;
	private static String s_dbPw;
	private static String s_dbType;
	private static String s_dbDriver;

	/**
	 * RegisterProcess
	 * @param RegisterProcess. This parameter contains three elements: service id (is a string), service
	 * model (is a string: urlprocess#urlprocessname) and agent id (is a string).
	 * @return RegisterProcessResponse. This parameter contains two elements: service implementation id )
	 * serviceprofileid@servicenumidagentid) and return which indicates if an error occurs (1:ok , 0: bad news).
	 */
	
	
	public wtp.RegisterProcessResponse RegisterProcess(wtp.RegisterProcess registerProcess) {

		
		wtp.RegisterProcessResponse response = new wtp.RegisterProcessResponse();

		if (DEBUG) {
			System.out.println("RegisterProcess Service :");
			System.out.println("***AgentID... " + registerProcess.getAgentID());
			System.out.println("***ServiceID... "+ registerProcess.getServiceID());
			System.out.println("***ServiceModel... "+ registerProcess.getServiceModel());
		}


			/////////////
			////JENA/////
			/////////////
			IDBConnection conn = null;
			OntModel m = null;

			Properties properties = new Properties();

			try {
				properties.loadFromXML(RegisterProcessSkeleton.class.getResourceAsStream("/"+"THOMASDemoConfiguration.xml"));
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
			ModelRDB model = (ModelRDB) maker.openModel("http://example.org/ontologias");

			// use the model maker to get the base model as a persistent model
			// strict=false, so we get an existing model by that name if it
			// exists
			// or create a new one
			String urlprocess = registerProcess.getServiceModel();
			StringTokenizer Tok = new StringTokenizer(urlprocess);
			urlprocess = Tok.nextToken("#");
			String processname = Tok.nextToken();

			if (DEBUG) {
				System.out.println("URL process: "+ urlprocess);
				System.out.println("process name: "+ processname);
				System.out.println("File to load... "+ urlprocess);
			}

			// now we plug that base model into an ontology model that also uses
			// the given model maker to create storage for imported models
			OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
			spec.setImportModelMaker(maker);
			m = ModelFactory.createOntologyModel(spec, model);

			
			persistence.DataBaseInterface thomasBD = new DataBaseInterface();
			String urlprofile=thomasBD.GetServiceProfileURL(registerProcess.getServiceID());
			
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

			// Register de serviceimplementationid in the DB
			String serviceprocessid = thomasBD.AddNewProcess(registerProcess.getServiceModel(), registerProcess.getServiceID(),registerProcess.getAgentID());
			
			/////////// 
			///JENA////
			///////////
			
			if (serviceprocessid != null) {
			
			m.read(registerProcess.getServiceModel());
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

			// Close the model
			m.close();
			response.setServiceModelID(serviceprocessid);
			response.set_return(1);

		} else {
			response.setServiceModelID("[Error]: the service profile does not exist or the process is already registered");
			response.set_return(0);
		}

		return (response);

	}
	


}
